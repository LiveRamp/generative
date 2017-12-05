package com.liveramp.generative;

import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;

public class ArbitraryBoolean implements Arbitrary<Boolean> {
  @Override
  public Boolean get(Random r) {
    return r.nextBoolean();
  }
}
