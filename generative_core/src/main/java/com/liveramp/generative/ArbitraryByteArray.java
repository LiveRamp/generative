package com.liveramp.generative;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ArbitraryByteArray implements Arbitrary<byte[]> {

  private int length;

  public ArbitraryByteArray(int length) {
    this.length = length;
  }

  public byte[] get(Random r) {
    byte[] result = new byte[length];
    r.nextBytes(result);
    return result;
  }

  @Override
  public List<byte[]> shrink(byte[] val) {
    List<byte[]> result = new ArrayList<>();
    Collections.addAll(result,
        makeArrayOf(0),
        makeArrayOf(1),
        makeArrayOf(Byte.MIN_VALUE),
        makeArrayOf(Byte.MAX_VALUE)
    );
    return result;
  }

  private byte[] makeArrayOf(int i) {
    byte[] zeroArray = new byte[length];
    Arrays.fill(zeroArray, (byte)i);
    return zeroArray;
  }
}
