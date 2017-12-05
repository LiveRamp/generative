package com.liveramp.generative;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
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
import org.jetbrains.annotations.NotNull;

import com.liveramp.commons.collections.map.MapBuilder;

public class ArbitraryThrift<T extends TBase> implements Arbitrary<T> {


  private final Class<T> clazz;
  private Map<Byte, DefaultValueCreator> defaultTypeArbitraries;
  private final Map<TFieldIdEnum, Arbitrary<?>> fieldArbitraries;

  public ArbitraryThrift(Class<T> clazz) {
    this(clazz, Maps.newHashMap());
  }

  public ArbitraryThrift(Class<T> clazz,
                         Map<TFieldIdEnum, Arbitrary<?>> fieldArbitraries) {
    this.clazz = clazz;
    this.defaultTypeArbitraries = MapBuilder.<Byte, DefaultValueCreator>of(
        TType.BOOL, boolPrim)
        .put(TType.BYTE, bytePrim)
        .put(TType.I16, shortPrim)
        .put(TType.I32, intPrim)
        .put(TType.DOUBLE, doublePrim)
        .put(TType.STRING, stringPrim)
        .put(TType.I64, longPrim)
        .put(TType.SET, setPrim)
        .put(TType.STRUCT, structPrim)
        .get();
    this.fieldArbitraries = Maps.newTreeMap(new FieldIdComparator());
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
      (m, fv) -> new ArbitraryThrift(((StructMetaData)fv).structClass, Maps.newHashMap());
  public static final DefaultValueCreator<Set> setPrim =
      (m, fv) -> {
        SetMetaData setMetaData = (SetMetaData)fv;
        DefaultValueCreator<?> constructor = m.get(setMetaData.elemMetaData.type);
        Arbitrary<?> elems = constructor.apply(m, setMetaData.elemMetaData);
        return new ArbitraryBoundedInt(0, 128).<Set>flatMap(
            l -> new SetOf(elems, l),
            s -> s.size()
        );
      };

  public static final DefaultValueCreator<List> listPrim =
      (m, fv) -> {
        ListMetaData listMetaData = (ListMetaData)fv;
        DefaultValueCreator<?> constructor = m.get(listMetaData.elemMetaData.type);
        Arbitrary<?> elems = constructor.apply(m, listMetaData.elemMetaData);
        return new ArbitraryBoundedInt(0, 128).<List>flatMap(
            l -> new ListOf(elems, l),
            s -> s.size()
        );
      };

  @NotNull
  @Override
  public T get(Random r) {
    try {
      T t = clazz.newInstance();

      Map<? extends TFieldIdEnum, FieldMetaData> metadata = getFieldMetaDataMap();

      Set<? extends Map.Entry<? extends TFieldIdEnum, FieldMetaData>> entries = metadata.entrySet();

      if (t instanceof TUnion) {
        ArrayList<? extends Map.Entry<? extends TFieldIdEnum, FieldMetaData>> entryList = Lists.newArrayList(entries);
        Collections.shuffle(entryList, r);
        entries = entryList.stream().limit(1).collect(Collectors.toSet());
      }

      for (Map.Entry<? extends TFieldIdEnum, FieldMetaData> entry : entries) {
        if (fieldArbitraries.containsKey(entry.getKey())) {
          t.setFieldValue(entry.getKey(), fieldArbitraries.get(entry.getKey()).get(r));
        } else if (defaultTypeArbitraries.containsKey(entry.getValue().valueMetaData.type)) {
          DefaultValueCreator<?> valueCreator = defaultTypeArbitraries.get(entry.getValue().valueMetaData.type);
          Arbitrary<?> arbitrary = valueCreator.apply(defaultTypeArbitraries, entry.getValue().valueMetaData);
          t.setFieldValue(entry.getKey(), arbitrary.get(r));
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

      return Lists.newArrayList(shrinkAllFieldsAndNoOptionals, shrinkAllFields, noOptionals);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private T shrinkAllFields(T val) throws NoSuchFieldException, IllegalAccessException {
    T shrinkCopy = (T)val.deepCopy();
    Map<? extends TFieldIdEnum, FieldMetaData> metadata = getFieldMetaDataMap();
    for (Map.Entry<? extends TFieldIdEnum, FieldMetaData> entry : metadata.entrySet()) {
      if (defaultTypeArbitraries.containsKey(entry.getValue().valueMetaData.type) && shrinkCopy.isSet(entry.getKey())) {
        DefaultValueCreator<?> valueCreator = defaultTypeArbitraries.get(entry.getValue().valueMetaData.type);
        Arbitrary arb = valueCreator.apply(defaultTypeArbitraries, entry.getValue().valueMetaData);
        Object currentValue = shrinkCopy.getFieldValue(entry.getKey());
        if (currentValue instanceof byte[]) {
          currentValue = ByteBuffer.wrap((byte[])currentValue);
        }
        List shrink = arb.shrink(currentValue);
        if (!shrink.isEmpty()) {
          shrinkCopy.setFieldValue(entry.getKey(), shrink.get(0));
        }
      }
    }
    return shrinkCopy;
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
