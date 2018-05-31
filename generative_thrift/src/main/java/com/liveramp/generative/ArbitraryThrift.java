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
import java.util.stream.Collectors;

import org.apache.thrift.TBase;
import org.apache.thrift.TFieldIdEnum;
import org.apache.thrift.TFieldRequirementType;
import org.apache.thrift.TUnion;
import org.apache.thrift.meta_data.FieldMetaData;
import org.apache.thrift.meta_data.FieldValueMetaData;

import com.liveramp.generative.default_values.DefaultValueCreatorFactory;

public class ArbitraryThrift<T extends TBase> implements Arbitrary<T> {

  private final Class<T> clazz;
  private final Map<TFieldIdEnum, Arbitrary<?>> fieldArbitraries;
  private final DefaultValueCreatorFactory defaultValueCreatorFactory;

  public ArbitraryThrift(Class<T> clazz) {
    this(clazz, new HashMap<>());
  }

  public ArbitraryThrift(Class<T> clazz,
                         Map<TFieldIdEnum, Arbitrary<?>> fieldArbitraries) {
    this.clazz = clazz;
    this.fieldArbitraries = new TreeMap<>(new FieldIdComparator());
    this.fieldArbitraries.putAll(fieldArbitraries);
    this.defaultValueCreatorFactory = new DefaultValueCreatorFactory();
  }

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
          // For required fields we always set them, but for optional fields, it's a coin-flip
          if (r.nextBoolean() || entry.getValue().requirementType ==  TFieldRequirementType.REQUIRED) {
            t.setFieldValue(entry.getKey(), maybeArbitrary.get().get(r));
          }
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
    FieldValueMetaData vm = entry.getValue().valueMetaData;
    TFieldIdEnum key = entry.getKey();
    if (fieldArbitraries.containsKey(key)) {
      return Optional.of(fieldArbitraries.get(key));
    } else {
      return Optional.of(defaultValueCreatorFactory.apply(vm).apply(vm));
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

}
