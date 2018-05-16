package com.liveramp.generative;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public class SetOf<T> implements Arbitrary<Set<T>> {

  private Arbitrary<T> internal;
  private Arbitrary<Integer> length;

  public SetOf(Arbitrary<T> internal, int length) {
   this(internal, new Fixed<>(length));
  }

  public SetOf(Arbitrary<T> internal, Arbitrary<Integer> length) {
    this.internal = internal;
    this.length = length;
  }

  @Override
  public Set<T> get(Random r) {
    return internal.stream(r).limit(length.get(r)).collect(Collectors.toSet());
  }

  @Override
  public List<Set<T>> shrink(Set<T> val) {
    return AbitraryUtil.shrinkCollection(val, internal, l -> new HashSet<>(l), length, val.size());
  }

}
