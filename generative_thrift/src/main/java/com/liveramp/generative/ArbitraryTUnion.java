package com.liveramp.generative;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import org.apache.thrift.TFieldIdEnum;
import org.apache.thrift.TUnion;

class ArbitraryTUnion<T extends TUnion> extends ArbitraryThrift<T> {
  private final List<TFieldIdEnum> possibleFields;

  ArbitraryTUnion(
      Class<T> clazz,
      Collection<TFieldIdEnum> possibleFields,
      Map<TFieldIdEnum, Arbitrary<?>> fieldArbitraries
  ) {
    super(clazz, fieldArbitraries, Collections.emptyMap());
    this.possibleFields = new ArrayList<>(possibleFields);
    if (this.possibleFields.isEmpty()) {
      throw new IllegalStateException("Empty TUnions are not valid");
    }
  }

  @Override
  public T get(Random r) {
    try {
      T t = clazz.newInstance();
      int idx = r.nextInt(possibleFields.size());
      TFieldIdEnum setField = possibleFields.get(idx);
      Arbitrary<?> arbitrary = getArbitrary(setField, metadata.get(setField).valueMetaData);
      t.setFieldValue(setField, arbitrary.get(r));
      return t;
    } catch (IllegalAccessException | InstantiationException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public List<T> shrink(T original) {
    TFieldIdEnum setField = original.getSetField();
    if (setField == null) {
      throw new IllegalStateException("TUnions must have a set field but " + original + " was empty");
    }
    Arbitrary arbitrary = getArbitrary(setField, metadata.get(setField).valueMetaData);
    return (List<T>)arbitrary.shrink(original.getFieldValue())
        .stream()
        .map(val -> {
          T copy = (T)original.deepCopy();
          copy.setFieldValue(setField, val);
          return copy;
        }).collect(Collectors.toList());
  }

  static boolean isTUnion(Class clazz) {
    try {
      return clazz.newInstance() instanceof TUnion;
    } catch (IllegalAccessException | InstantiationException e) {
      throw new IllegalArgumentException(e);
    }
  }
}
