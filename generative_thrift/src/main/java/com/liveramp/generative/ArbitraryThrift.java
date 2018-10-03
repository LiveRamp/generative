package com.liveramp.generative;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.thrift.TBase;
import org.apache.thrift.TFieldIdEnum;
import org.apache.thrift.TFieldRequirementType;
import org.apache.thrift.TUnion;
import org.apache.thrift.meta_data.FieldMetaData;
import org.apache.thrift.meta_data.FieldValueMetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liveramp.generative.default_values.DefaultValueCreatorFactory;

public class ArbitraryThrift<T extends TBase> implements Arbitrary<T> {
  private static final Logger LOG = LoggerFactory.getLogger(ArbitraryThrift.class);
  private ArbitraryTUnion arbTUnion;

  public static <T extends TBase> ArbitraryThrift<T> of(Class<T> clazz) {
    return builder(clazz).build();
  }

  // You probably want buildUnion or buildStruct
  public static <T extends TBase> Builder<T> builder(Class<T> clazz) {
    if (ArbitraryTUnion.isTUnion(clazz)) {
      return new TUnionBuilder(clazz);
    } else {
      return new TStructBuilder<>(clazz);
    }
  }

  public static <U extends TUnion> TUnionBuilder<U> buildUnion(Class<U> clazz) {
    return new TUnionBuilder<>(clazz);
  }

  public static <T extends TBase> TStructBuilder<T> buildStruct(Class<T> clazz) {
    return new TStructBuilder<>(clazz);
  }

  public static abstract class Builder<T extends TBase> {
    final Class<T> clazz;
    final Map<? extends TFieldIdEnum, FieldMetaData> fieldMetaDataMap;
    Map<TFieldIdEnum, Arbitrary<?>> fieldArbitraries;

    Builder(Class<T> clazz) {
      this.clazz = clazz;
      fieldMetaDataMap = getFieldMetaDataMap(clazz);
      this.fieldArbitraries = new TreeMap<>(new FieldIdComparator());
    }

    public Builder<T> withArbitrary(TFieldIdEnum field, Arbitrary<?> arbitrary) {
      assertFieldExists(field);
      fieldArbitraries.put(field, arbitrary);
      return this;
    }

    public Builder<T> withArbitraries(Map<TFieldIdEnum, Arbitrary<?>> fieldToArbitrary) {
      fieldToArbitrary.keySet().forEach(this::assertFieldExists);
      fieldArbitraries.putAll(fieldToArbitrary);
      return this;
    }

    abstract public ArbitraryThrift<T> build();

    void assertFieldExists(TFieldIdEnum field) {
      if (!fieldMetaDataMap.containsKey(field)) {
        throw new IllegalArgumentException(String.format("Couldn't find field %s in fields: %s", field, fieldMetaDataMap.keySet()));
      }
    }
  }

  public static class TStructBuilder<T extends TBase> extends Builder<T> {
    Map<TFieldIdEnum, RequirementType> fieldToRequirementType;

    TStructBuilder(Class<T> clazz) {
      super(clazz);
      this.fieldToRequirementType = new TreeMap<>(new FieldIdComparator());
    }

    public TStructBuilder<T> require(Collection<TFieldIdEnum> fields) {
      return updateRequirementType(fields, RequirementType.REQUIRED);
    }

    public TStructBuilder<T> require(TFieldIdEnum... fields) {
      return require(Arrays.asList(fields));
    }

    public TStructBuilder<T> unset(Collection<TFieldIdEnum> fields) {
      return updateRequirementType(fields, RequirementType.UNSET);
    }

    public TStructBuilder<T> unset(TFieldIdEnum... fields) {
      return unset(Arrays.asList(fields));
    }

    @Override
    public ArbitraryThrift<T> build() {
      return new ArbitraryThrift<>(clazz, fieldArbitraries, fieldToRequirementType);
    }

    private TStructBuilder<T> updateRequirementType(Collection<TFieldIdEnum> fields, RequirementType type) {
      fields.forEach(this::assertFieldExists);
      fields.forEach(f -> fieldToRequirementType.put(f, type));
      return this;
    }
  }

  public static class TUnionBuilder<T extends TUnion> extends Builder<T> {
    private Set<TFieldIdEnum> possibleFields;
    private boolean allowEmpty;

    TUnionBuilder(Class<T> clazz) {
      super(clazz);
      possibleFields = new TreeSet<>(new FieldIdComparator());
      possibleFields.addAll(fieldMetaDataMap.keySet());
      this.allowEmpty = false;
    }

    public TUnionBuilder<T> allowEmpty() {
      this.allowEmpty = true;
      return this;
    }

    public Builder<T> selectFrom(Collection<TFieldIdEnum> fields) {
      fields.forEach(this::assertFieldExists);
      possibleFields.addAll(fields);
      return this;
    }

    public Builder<T> selectFrom(TFieldIdEnum... fields) {
      return selectFrom(Arrays.asList(fields));
    }

    public Builder<T> ignore(Collection<TFieldIdEnum> fields) {
      fields.forEach(this::assertFieldExists);
      possibleFields.addAll(fieldMetaDataMap.keySet());
      possibleFields.removeAll(fields);
      return this;
    }

    public Builder<T> ignore(TFieldIdEnum... fieldIdEnums) {
      return ignore(Arrays.asList(fieldIdEnums));
    }

    @Override
    public ArbitraryThrift<T> build() {
      return new ArbitraryTUnion<>(clazz, possibleFields, fieldArbitraries, allowEmpty);
    }
  }

  private enum RequirementType {
    REQUIRED,
    OPTIONAL,
    UNSET
  }

  final Class<T> clazz;
  final Map<? extends TFieldIdEnum, FieldMetaData> metadata;
  private final Map<TFieldIdEnum, Arbitrary<?>> fieldArbitraries;
  private final DefaultValueCreatorFactory defaultValueCreatorFactory;
  private Map<TFieldIdEnum, RequirementType> fieldToRequirementType;

  @Deprecated // Use the builder
  public ArbitraryThrift(Class<T> clazz) {
    this(clazz, new HashMap<>());
  }

  @Deprecated // Use the builder
  public ArbitraryThrift(Class<T> clazz,
                         Map<TFieldIdEnum, Arbitrary<?>> fieldArbitraries
  ) {
    this(clazz, fieldArbitraries, Collections.emptyMap());
    if (ArbitraryTUnion.isTUnion(clazz)) {
      arbTUnion = new ArbitraryTUnion(clazz, getFieldMetaDataMap(clazz).keySet(), fieldArbitraries, false);
      LOG.warn(String.format("%s is a TUnion and should use %s", clazz.getSimpleName(), ArbitraryTUnion.class.getSimpleName()));
    }
  }

  ArbitraryThrift(
      Class<T> clazz,
      Map<TFieldIdEnum, Arbitrary<?>> fieldArbitraries,
      Map<TFieldIdEnum, RequirementType> fieldToRequirementType
  ) {
    this.clazz = clazz;
    this.metadata = getFieldMetaDataMap(clazz);
    this.fieldArbitraries = new TreeMap<>(new FieldIdComparator());
    this.fieldArbitraries.putAll(fieldArbitraries);
    this.defaultValueCreatorFactory = new DefaultValueCreatorFactory();
    this.fieldToRequirementType = new TreeMap<>(new FieldIdComparator());
    this.fieldToRequirementType.putAll(fieldToRequirementType);
    for (Map.Entry<? extends TFieldIdEnum, FieldMetaData> entry : metadata.entrySet()) {
      RequirementType requiredness = getRequirementType(entry.getValue().requirementType);
      switch (requiredness) {
        case REQUIRED:
          this.fieldToRequirementType.put(entry.getKey(), requiredness);
          break;
        case OPTIONAL:
        case UNSET:
          this.fieldToRequirementType.putIfAbsent(entry.getKey(), requiredness);
          break;
        default:
          throw new IllegalStateException();
      }
    }
  }

  @Override
  public T get(Random r) {
    try {
      // Delete this code once all clients use the builder
      if (arbTUnion != null) {
        return (T)arbTUnion.get(r);
      }
      T t = clazz.newInstance();

      Set<? extends Map.Entry<? extends TFieldIdEnum, FieldMetaData>> entries = metadata.entrySet();

      for (Map.Entry<? extends TFieldIdEnum, FieldMetaData> entry : entries) {
        if (shouldSetField(r, fieldToRequirementType.get(entry.getKey()))) {
          Arbitrary arbitrary = getArbitrary(entry.getKey(), entry.getValue().valueMetaData);
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

  Arbitrary getArbitrary(TFieldIdEnum key, FieldValueMetaData vm) {
    if (fieldArbitraries.containsKey(key)) {
      return fieldArbitraries.get(key);
    } else {
      return defaultValueCreatorFactory.apply(vm).apply(vm);
    }
  }

  private boolean shouldSetField(Random r, RequirementType type) {
    switch (type) {
      case REQUIRED:
        return true;
      case OPTIONAL:
        return r.nextBoolean();
      case UNSET:
        return false;
      default:
        throw new IllegalStateException();
    }
  }

  private static <T extends TBase> Map<? extends TFieldIdEnum, FieldMetaData> getFieldMetaDataMap(Class<T> clazz) {
    try {
      return (Map<? extends TFieldIdEnum, FieldMetaData>)clazz.getField("metaDataMap").get(null);
    } catch (IllegalAccessException | NoSuchFieldException e) {
      throw new IllegalArgumentException("Error getting field metadata from " + clazz.getName(), e);
    }
  }

  private RequirementType getRequirementType(byte b) {
    switch (b) {
      case TFieldRequirementType.REQUIRED:
        return RequirementType.REQUIRED;
      default:
        return RequirementType.OPTIONAL;
    }
  }

  private T shrinkAllFields(T val) {
    T shrinkCopy = (T)val.deepCopy();
    Map<? extends TFieldIdEnum, FieldMetaData> metadata = getFieldMetaDataMap(clazz);
    for (Map.Entry<? extends TFieldIdEnum, FieldMetaData> entry : metadata.entrySet()) {
      if (shrinkCopy.isSet(entry.getKey())) {
        Arbitrary arbitrary = getArbitrary(entry.getKey(), entry.getValue().valueMetaData);
        Object currentValue = shrinkCopy.getFieldValue(entry.getKey());
        if (currentValue instanceof byte[]) {
          currentValue = ByteBuffer.wrap((byte[])currentValue);
        }
        List shrink = arbitrary.shrink(currentValue);
        if (!shrink.isEmpty()) {
          shrinkCopy.setFieldValue(entry.getKey(), shrink.get(0));
        }
      }
    }
    return shrinkCopy;
  }

  private T unsetAllOptionalFields(T val) {
    T shrinkCopy = (T)val.deepCopy();
    Map<? extends TFieldIdEnum, FieldMetaData> metadata = getFieldMetaDataMap(clazz);
    for (TFieldIdEnum entry : metadata.keySet()) {
      if (fieldToRequirementType.get(entry) == RequirementType.OPTIONAL) {
        shrinkCopy.setFieldValue(entry, null);
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
