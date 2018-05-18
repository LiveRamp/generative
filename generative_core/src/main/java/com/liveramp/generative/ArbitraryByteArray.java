package com.liveramp.generative;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ArbitraryByteArray implements Arbitrary<byte[]> {

  private Arbitrary<Integer> length;

  public ArbitraryByteArray(int length) {
    this(new Fixed<>(length));
  }

  public ArbitraryByteArray(Arbitrary<Integer> length) {
    this.length = length;
  }

  public byte[] get(Random r) {
    byte[] result = new byte[length.get(r)];
    r.nextBytes(result);
    return result;
  }

  @Override
  public List<byte[]> shrink(byte[] val) {
    List<byte[]> result = new ArrayList<>();
    for (Integer shrunkLength : length.shrink(val.length)) {
      Collections.addAll(result,
          makeArrayOf(0, shrunkLength),
          makeArrayOf(1, shrunkLength),
          makeArrayOf(Byte.MIN_VALUE, shrunkLength),
          makeArrayOf(Byte.MAX_VALUE, shrunkLength)
      );
    }

    return result;
  }

  private byte[] makeArrayOf(int i, int length) {
    byte[] zeroArray = new byte[length];
    Arrays.fill(zeroArray, (byte)i);
    return zeroArray;
  }

  @Override
  public String prettyPrint(byte[] val) {
    return Arrays.toString(val);
  }
}
