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
    Set<T> result = new HashSet<>();
    Integer targetLength = length.get(r);
    //after choosing a length, we keep adding elements until we hit that length
    //or we've been trying for too long
    for (int i = 0; i < (targetLength * 10) && result.size() < targetLength; i++) {
      result.add(internal.get(r));
    }

    return result;
  }

  @Override
  public List<Set<T>> shrink(Set<T> val) {
    return AbitraryUtil.shrinkCollection(val, internal, l -> new HashSet<>(l), length, val.size());
  }

}
