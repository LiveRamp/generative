package com.liveramp.generative;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import org.apache.thrift.TBase;
import org.apache.thrift.TFieldIdEnum;
import org.apache.thrift.TFieldRequirementType;
import org.apache.thrift.TUnion;
import org.apache.thrift.meta_data.FieldMetaData;
import org.apache.thrift.meta_data.FieldValueMetaData;
import org.apache.thrift.meta_data.ListMetaData;
import org.apache.thrift.meta_data.SetMetaData;
import org.apache.thrift.meta_data.StructMetaData;
import org.apache.thrift.protocol.TType;

public class ArbitraryThrift<T extends TBase> implements Arbitrary<T> {


  private final Class<T> clazz;
  private Map<Byte, DefaultValueCreator> defaultTypeArbitraries;
  private final Map<TFieldIdEnum, Arbitrary<?>> fieldArbitraries;

  public ArbitraryThrift(Class<T> clazz) {
    this(clazz, new HashMap<>());
  }

  public ArbitraryThrift(Class<T> clazz,
                         Map<TFieldIdEnum, Arbitrary<?>> fieldArbitraries) {
    this.clazz = clazz;
    this.defaultTypeArbitraries = new HashMap<>();

    this.defaultTypeArbitraries.put(TType.BOOL, boolPrim);
    this.defaultTypeArbitraries.put(TType.BYTE, bytePrim);
    this.defaultTypeArbitraries.put(TType.I16, shortPrim);
    this.defaultTypeArbitraries.put(TType.I32, intPrim);
    this.defaultTypeArbitraries.put(TType.DOUBLE, doublePrim);
    this.defaultTypeArbitraries.put(TType.STRING, stringPrim);
    this.defaultTypeArbitraries.put(TType.I64, longPrim);
    this.defaultTypeArbitraries.put(TType.SET, setPrim);
    this.defaultTypeArbitraries.put(TType.STRUCT, structPrim);
    this.fieldArbitraries = new TreeMap<>(new FieldIdComparator());
    this.fieldArbitraries.putAll(fieldArbitraries);
  }

  public static final DefaultValueCreator<Boolean> boolPrim = (m, fv) -> new ArbitraryBoolean();
  public static final DefaultValueCreator<Byte> bytePrim =
      (m, fv) -> new ArbitraryByteArray(1).map(b -> b[0]);
  public static final DefaultValueCreator<Short> shortPrim =
      (m, fv) -> new ArbitraryBoundedInt(Short.MIN_VALUE, Short.MAX_VALUE).map(i -> i.shortValue());
  public static final DefaultValueCreator<Integer> intPrim =
      (m, fv) -> new ArbitraryBoundedInt(Integer.MIN_VALUE, Integer.MAX_VALUE);
  public static final DefaultValueCreator<Long> longPrim =
      (m, fv) -> (r) -> r.nextLong();
  public static final DefaultValueCreator<Double> doublePrim =
      (m, fv) -> (r) -> r.nextDouble();
  public static final DefaultValueCreator stringPrim =
      (m, f) -> {
        FieldValueMetaData fv = (FieldValueMetaData)f;
        ArbitraryBoundedInt size = new ArbitraryBoundedInt(1, 256);
        if (fv.isBinary() || fv.getTypedefName() != null) {
          return size.flatMap(
              l -> new ArbitraryByteArray(l),
              s -> s.length).map(
              b -> ByteBuffer.wrap(b),
              bb -> bb.array()
          );
        } else {
          return size.flatMap(
              l -> new ArbitraryString(l),
              s -> s.length());
        }
      };

  public static final DefaultValueCreator structPrim =
      (m, fv) -> new ArbitraryThrift(((StructMetaData)fv).structClass, new HashMap<>());
  public static final DefaultValueCreator<Set> setPrim =
      (m, fv) -> {
        SetMetaData setMetaData = (SetMetaData)fv;
        DefaultValueCreator<?> constructor = m.get(setMetaData.elemMetaData.type);
        Arbitrary<?> elems = constructor.apply(m, setMetaData.elemMetaData);
        return new SetOf(elems, new ArbitraryBoundedInt(0, 128));
      };

  public static final DefaultValueCreator<List> listPrim =
      (m, fv) -> {
        ListMetaData listMetaData = (ListMetaData)fv;
        DefaultValueCreator<?> constructor = m.get(listMetaData.elemMetaData.type);
        Arbitrary<?> elems = constructor.apply(m, listMetaData.elemMetaData);
        return new ListOf(elems, new ArbitraryBoundedInt(0, 128));
      };

  @Override
  public T get(Random r) {
    try {
      T t = clazz.newInstance();

      Map<? extends TFieldIdEnum, FieldMetaData> metadata = getFieldMetaDataMap();

      Set<? extends Map.Entry<? extends TFieldIdEnum, FieldMetaData>> entries = metadata.entrySet();

      if (t instanceof TUnion) {
        ArrayList<? extends Map.Entry<? extends TFieldIdEnum, FieldMetaData>> entryList = new ArrayList<>(entries);
        Collections.shuffle(entryList, r);
        entries = entryList.stream().limit(1).collect(Collectors.toSet());
      }

      for (Map.Entry<? extends TFieldIdEnum, FieldMetaData> entry : entries) {
        Optional<Arbitrary> maybeArbitrary = getArbitrary(entry);
        if (maybeArbitrary.isPresent()) {
          t.setFieldValue(entry.getKey(), maybeArbitrary.get().get(r));
        }
      }
      if (!(t instanceof TUnion)) {
        clazz.getMethod("validate").invoke(t);
      }
      return t;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private Map<? extends TFieldIdEnum, FieldMetaData> getFieldMetaDataMap() throws IllegalAccessException, NoSuchFieldException {
    return (Map<? extends TFieldIdEnum, FieldMetaData>)clazz.getField("metaDataMap").get(null);
  }

  @Override
  public List<T> shrink(T val) {
    try {
      T noOptionals = unsetAllOptionalFields(val);
      T shrinkAllFields = shrinkAllFields(val);
      T shrinkAllFieldsAndNoOptionals = unsetAllOptionalFields(shrinkAllFields);

      List<T> result = new ArrayList<>();
      Collections.addAll(result, shrinkAllFieldsAndNoOptionals, shrinkAllFields, noOptionals);
      return result;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private T shrinkAllFields(T val) throws NoSuchFieldException, IllegalAccessException {
    T shrinkCopy = (T)val.deepCopy();
    Map<? extends TFieldIdEnum, FieldMetaData> metadata = getFieldMetaDataMap();
    for (Map.Entry<? extends TFieldIdEnum, FieldMetaData> entry : metadata.entrySet()) {
      Optional<Arbitrary> maybeArbitrary = getArbitrary(entry);
      if (maybeArbitrary.isPresent() && shrinkCopy.isSet(entry.getKey())) {
        Object currentValue = shrinkCopy.getFieldValue(entry.getKey());
        if (currentValue instanceof byte[]) {
          currentValue = ByteBuffer.wrap((byte[])currentValue);
        }
        List shrink = maybeArbitrary.get().shrink(currentValue);
        if (!shrink.isEmpty()) {
          shrinkCopy.setFieldValue(entry.getKey(), shrink.get(0));
        }
      }
    }
    return shrinkCopy;
  }


  private Optional<Arbitrary> getArbitrary(Map.Entry<? extends TFieldIdEnum, FieldMetaData> entry) {
    byte type = entry.getValue().valueMetaData.type;
    TFieldIdEnum key = entry.getKey();
    if (fieldArbitraries.containsKey(key)) {
      return Optional.of(fieldArbitraries.get(key));
    } else if (defaultTypeArbitraries.containsKey(type)) {
      DefaultValueCreator<?> valueCreator = defaultTypeArbitraries.get(type);
      return Optional.of(valueCreator.apply(defaultTypeArbitraries, entry.getValue().valueMetaData));
    } else {
      return Optional.empty();
    }
  }

  private T unsetAllOptionalFields(T val) throws IllegalAccessException, NoSuchFieldException {
    T shrinkCopy = (T)val.deepCopy();
    Map<? extends TFieldIdEnum, FieldMetaData> metadata = getFieldMetaDataMap();
    for (Map.Entry<? extends TFieldIdEnum, FieldMetaData> entry : metadata.entrySet()) {
      if (entry.getValue().requirementType == TFieldRequirementType.OPTIONAL) {
        shrinkCopy.setFieldValue(entry.getKey(), null);
      }
    }
    return shrinkCopy;
  }

  private static class FieldIdComparator implements Comparator<TFieldIdEnum> {

    @Override
    public int compare(TFieldIdEnum o1, TFieldIdEnum o2) {
      return o1.getThriftFieldId() - o2.getThriftFieldId();
    }
  }

  private interface DefaultValueCreator<T> extends BiFunction<Map<Byte, DefaultValueCreator>, FieldValueMetaData, Arbitrary<T>> {

  }
}
