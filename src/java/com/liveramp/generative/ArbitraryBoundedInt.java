package com.liveramp.generative;

import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;

public class ArbitraryBoundedInt implements Arbitrary<Integer> {

  int lowerBoundInclusive;
  int upperBoundInclusive;


  public ArbitraryBoundedInt(int lowerBoundInclusive, int upperBoundInclusive) {
    assert upperBoundInclusive >= lowerBoundInclusive;
    this.lowerBoundInclusive = lowerBoundInclusive;
    this.upperBoundInclusive = upperBoundInclusive;
  }

  @Override
  public Integer get(Random r) {
    if (upperBoundInclusive == lowerBoundInclusive) {
      return upperBoundInclusive;
    } else {
      return r.nextInt((upperBoundInclusive+1) - lowerBoundInclusive) + lowerBoundInclusive;
    }
  }

  @Override
  public List<Integer> shrink(Integer val) {
    return Lists.newArrayList(
        Math.max(lowerBoundInclusive, 0),
        lowerBoundInclusive,
        upperBoundInclusive);
  }
}
