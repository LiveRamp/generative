package com.liveramp.generative;

import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    return AbitraryUtil.shrinkCollection(val, this.internal, Function.identity(), length);
  }

}
