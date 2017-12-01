package com.liveramp.generative;

import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

public class ArbitraryString implements Arbitrary<String> {

  private int length;

  public ArbitraryString(int length) {
    this.length = length;
  }

  @Override
  public String get(Random r) {
    return RandomStringUtils.random(length, 0, 0, true, true, null, r);
  }

  @Override
  public List<String> shrink(String val) {
    return Lists.newArrayList(
        StringUtils.repeat("0", length),
        StringUtils.repeat("a", length),
        StringUtils.repeat("A", length));
  }
}
