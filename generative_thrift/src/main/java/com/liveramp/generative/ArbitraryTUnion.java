package com.liveramp.generative;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.thrift.TFieldIdEnum;
import org.apache.thrift.TUnion;

class ArbitraryTUnion<T extends TUnion> extends ArbitraryThrift<T> {
  private final List<TFieldIdEnum> possibleFields;
  private final int bound;

  ArbitraryTUnion(
      Class<T> clazz,
      Collection<TFieldIdEnum> possibleFields,
      Map<TFieldIdEnum, Arbitrary<?>> fieldArbitraries,
      boolean allowEmpty
  ) {
    super(clazz, fieldArbitraries, Collections.emptyMap());
    this.possibleFields = new ArrayList<>(possibleFields);
    this.bound = possibleFields.size() + (allowEmpty ? 1 : 0);
    if (possibleFields.isEmpty() && !allowEmpty) {
      throw new IllegalStateException("Cannot return empty ");
    }
  }

  @Override
  public T get(Random r) {
    try {
      T t = clazz.newInstance();
      int idx = r.nextInt(bound);
      if (idx == possibleFields.size()) {
        return t;
      }
      TFieldIdEnum setField = possibleFields.get(idx);
      Arbitrary<?> arbitrary = getArbitrary(setField, metadata.get(setField).valueMetaData);
      t.setFieldValue(setField, arbitrary.get(r));
      return t;
    } catch (IllegalAccessException | InstantiationException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public List<T> shrink(T val) {
    TFieldIdEnum setField = val.getSetField();
    if (setField != null) {
      Arbitrary arbitrary = getArbitrary(setField, metadata.get(setField).valueMetaData);
      T shrink = (T)val.deepCopy();
      shrink.setFieldValue(setField, arbitrary.shrink(val.getFieldValue()));
      return Collections.singletonList(shrink);
    } else {
      return Collections.emptyList();
    }
  }

  static boolean isTUnion(Class clazz) {
    try {
      return clazz.newInstance() instanceof TUnion;
    } catch (IllegalAccessException | InstantiationException e) {
      throw new IllegalArgumentException(e);
    }
  }
}
