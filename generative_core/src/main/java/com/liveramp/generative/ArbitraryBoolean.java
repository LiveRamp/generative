package com.liveramp.generative;

import java.util.Random;

public class ArbitraryBoolean implements Arbitrary<Boolean> {
  @Override
  public Boolean get(Random r) {
    return r.nextBoolean();
  }
}
