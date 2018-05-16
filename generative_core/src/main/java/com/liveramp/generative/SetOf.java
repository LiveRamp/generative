package com.liveramp.generative;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Sets;

public class SetOf<T> implements Arbitrary<Set<T>> {

  private Arbitrary<T> internal;
  private int length;

  public SetOf(Arbitrary<T> internal, int length) {
    this.internal = internal;
    this.length = length;
  }

  @Override
  public Set<T> get(Random r) {
    return internal.stream(r).limit(length).collect(Collectors.toSet());
  }

  @Override
  public List<Set<T>> shrink(Set<T> val) {
    return AbitraryUtil.shrinkCollection(val, internal, l -> Sets.newHashSet(l), length);
  }

}
