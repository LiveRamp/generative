package com.liveramp.generative;

import java.util.Random;

public class Fixed<T> implements Arbitrary<T> {

  private final T val;

  public Fixed(T val) {
    this.val = val;
  }

  @Override
  public T get(Random r) {
    return val;
  }
}
