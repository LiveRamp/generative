package com.liveramp.generative;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

public class ListOf<T> implements Arbitrary<List<T>> {

  private Arbitrary<T> internal;
  private int length;

  public ListOf(Arbitrary<T> internal, int length) {
    this.internal = internal;
    this.length = length;
  }

  @Override
  public List<T> get(Random r) {
    return internal.stream(r).limit(length).collect(Collectors.toList());
  }

  @Override
  public List<List<T>> shrink(List<T> val) {
    List<T> shrinkElements = val.stream().map(v -> {
      List<T> shrink = internal.shrink(v);
      return shrink.isEmpty() ? v : shrink.get(0);
    }).collect(Collectors.toList());

    List<List<T>> result = Lists.newArrayList();
    result.add(shrinkElements);
    return result;
  }
}
