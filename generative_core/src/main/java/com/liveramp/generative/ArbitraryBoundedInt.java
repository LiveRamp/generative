package com.liveramp.generative;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ArbitraryBoundedInt implements Arbitrary<Integer> {

  private int lowerBoundInclusive;
  private int upperBoundInclusive;


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
      int diff = upperBoundInclusive - lowerBoundInclusive;
      if (diff > 0 && diff != Integer.MAX_VALUE) {
        return r.nextInt(diff + 1) + lowerBoundInclusive;
      } else {
        //Only happens when bounds are very far apart, meaning randomly guessing to get between bounds has 50% chance of
        // success
        while (true) {
          int value = r.nextInt();
          if (value >= lowerBoundInclusive && value <= upperBoundInclusive) {
            return value;
          }
        }
      }
    }
  }

  @Override
  public List<Integer> shrink(Integer val) {
    List<Integer> result = new ArrayList<>();
    Collections.addAll(result,
        Math.min(Math.max(lowerBoundInclusive, 0), upperBoundInclusive),
        Math.min(Math.max(lowerBoundInclusive, 1), upperBoundInclusive),
        Math.min(Math.max(lowerBoundInclusive, -1), upperBoundInclusive),
        lowerBoundInclusive,
        upperBoundInclusive);
    return result;
  }
}
