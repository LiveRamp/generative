package com.liveramp.generative;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;
import org.jetbrains.annotations.NotNull;

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
    return Lists.newArrayList(
        makeArrayOf(0),
        makeArrayOf(1),
        makeArrayOf(Byte.MIN_VALUE),
        makeArrayOf(Byte.MAX_VALUE)
    );
  }

  private byte[] makeArrayOf(int i) {
    byte[] zeroArray = new byte[length];
    Arrays.fill(zeroArray, (byte)i);
    return zeroArray;
  }
}
