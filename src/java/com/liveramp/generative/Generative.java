package com.liveramp.generative;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Generative {

  private int index;
  private long seed = 0;
  private Random random;
  private List<Integer> shrinkIndices;
  private boolean lastShrinkExhausted = false;
  private Map<String, Object> generated = Maps.newHashMap();
  private String nextName;

  public Generative(long seed) {
    this(seed, Lists.newArrayList());
  }

  public Generative(long seed, List<Integer> shrinkIndices) {
    this.seed = seed;
    this.random = new Random(seed);
    this.shrinkIndices = shrinkIndices;
    this.index = 0;
  }

  public Generative namedVar(String name) {
    this.nextName = name;
    return this;
  }

  private String getName() {
    if (nextName != null) {
      String n = nextName;
      nextName = null;
      return n;
    } else {
      return "var" + index;
    }
  }


  public <T> T gen(Arbitrary<T> arbitrary) {
    T val = arbitrary.get(random);
    int currentIndex = this.index;
    index++;

    if (currentIndex < shrinkIndices.size() && shrinkIndices.get(currentIndex) >= 0) {
      Integer shrinkIndex = shrinkIndices.get(currentIndex);
      List<T> shrinks = arbitrary.shrink(val);
      if (shrinkIndex < shrinks.size()) {
        T t = shrinks.get(shrinkIndex);
        generated.put(getName(), t);
        return t;
      } else {
        lastShrinkExhausted = true;
        generated.put(getName(), val);
        return val;
      }
    }
    generated.put(getName(), val);
    return val;
  }

  public Integer anyInteger() {
    return gen(new BoundedInt(Integer.MIN_VALUE, Integer.MAX_VALUE));
  }


  public Integer anyPositiveInteger() {
    return boundedPositiveInteger(0, Integer.MAX_VALUE);
  }

  public Integer boundedPositiveInteger(int startInclusive, int endExclusive) {
    return gen(new BoundedInt(startInclusive, endExclusive - 1));
  }

  public Integer positiveIntegerGreaterThan(int startExclusive) {
    return boundedPositiveInteger(startExclusive + 1, Integer.MAX_VALUE);
  }

  public Integer positiveIntegerLessThan(int endExclusive) {
    return boundedPositiveInteger(0, endExclusive);
  }

  public byte[] anyByteArrayOfLength(int length) {
    Arbitrary<Byte> arbitraryByte = new BoundedInt(Byte.MIN_VALUE, Byte.MAX_VALUE).map(b -> b.byteValue(), i -> i.intValue());
    ListOf<Byte> arbitraryList = new ListOf<>(arbitraryByte, length);
    List<Byte> gen = gen(arbitraryList);
    byte[] b = new byte[gen.size()];
    for (int i = 0; i < gen.size(); i++) {
      b[i] = gen.get(i);
    }
    return b;
  }

  public byte[] anyByteArrayUpToLength(int length) {
    return anyByteArrayOfLength(new BoundedInt(0, length).get(random));
  }

  public byte[] anyByteArray() {
    return anyByteArrayUpToLength(255);
  }

  public <T, L extends List<T>> L shuffle(L l) {
    Collections.shuffle(l, random);
    return l;
  }

  public boolean anyBoolean() {
    return random.nextBoolean();
  }

  public boolean atRandom() {
    return anyBoolean();
  }

  public String anyStringOfLength(int length) {
    return RandomStringUtils.random(length, 0, 0, true, true, null, random);
  }

  public String anyStringOfLengthUpTo(int length) {
    return anyStringOfLength(positiveIntegerLessThan(length + 1));
  }

  public String anyString() {
    return anyStringOfLengthUpTo(256);
  }


  public static void runTests(int numTests, TestBlock block, String... additionalSeeds) {
    int i;
    for (i = 0; i < numTests; i++) {
      runTestWithSeed(new Random().nextInt(), i, block);
    }
    for (String specificSeed : additionalSeeds) {
      runTestWithSeed(specificSeed, i, block);
    }
  }

  private static void runTestWithSeed(long seed, int testNumber, TestBlock block) {
    Generative gen = null;
    try {
      gen = new Generative(seed);
      block.run(testNumber, gen);
    } catch (Throwable e) {
      Pair<String, Throwable> shrinkSeed = shrink(seed, testNumber, gen.index, block);
      if (shrinkSeed.getRight() != null) {
        throw new RuntimeException("Shrunken test case failed with seed: " + shrinkSeed.getLeft(),
            shrinkSeed.getRight());
      } else {
        throw new RuntimeException("Test case failed with seed: " + seed, e);
      }
    }
  }

  public static void runTestWithSeed(String seed, int testNumber, TestBlock block) {
    try {
      Pair<Long, List<Integer>> seedVars = parseSeed(seed);
      block.run(testNumber, new Generative(seedVars.getLeft(), seedVars.getRight()));
    } catch (Exception e) {
      throw new RuntimeException("Error during test while using seed: " + seed, e);
    }
  }

  private static String toString(List<Integer> shrinkIndices) {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    String join = StringUtils.join(shrinkIndices, ",");
    try {
      PrintWriter writer = new PrintWriter(new GZIPOutputStream(out));
      writer.print(join);
      writer.close();
      out.close();
      return Hex.encodeHexString(out.toByteArray());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static List<Integer> fromString(String encodedIndices) {
    try {
      ByteArrayInputStream in = new ByteArrayInputStream(Hex.decodeHex(encodedIndices.toCharArray()));
      String s = IOUtils.toString(new GZIPInputStream(in));
      List<Integer> indices = Lists.newArrayList(StringUtils.split(s, ",")).stream()
          .map(str -> Integer.parseInt(str)).collect(Collectors.toList());
      return indices;
    } catch (IOException | DecoderException e) {
      throw new RuntimeException(e);
    }
  }

  private static Pair<Long, List<Integer>> parseSeed(String seed) {
    String[] split = StringUtils.split(seed, ";");
    long seedLong = Long.parseLong(split[0]);
    if (split.length > 1) {
      List<Integer> indices = fromString(split[1]);
      return Pair.of(seedLong, indices);
    } else {
      return Pair.of(seedLong, Lists.newArrayList());
    }
  }

  private static Logger LOG = LoggerFactory.getLogger(Generative.class);

  private static Pair<String, Throwable> shrink(long seed, int testNumber, int variableCount, TestBlock block) {
    int shrinkTestNumber = testNumber;
    List<Integer> shrinks = Lists.newArrayList();
    Throwable lastException = null;
    Generative gen = null;
    while (shrinks.size() < variableCount) {
      shrinks.add(0);
      boolean testPassed = true;
      boolean shrinkExhausted = false;
      while (testPassed && !shrinkExhausted) {
        try {
          shrinkTestNumber++;
          gen = new Generative(seed, shrinks);
          block.run(testNumber, gen);
          shrinkExhausted = gen.lastShrinkExhausted;
          shrinks.set(shrinks.size() - 1, shrinks.get(shrinks.size() - 1) + 1);
        } catch (Throwable e) {
          lastException = e;
          testPassed = false;
        }
      }
    }
    LOG.info("Returning shrunken test case - performed " + (shrinkTestNumber - testNumber) + " shrinks");
    StringBuilder vars = new StringBuilder("Generated variables were: \n");
    for (Map.Entry<String, Object> entry : gen.generated.entrySet()) {
      vars.append(entry.getKey() + " : " + entry.getValue() + "\n");
    }
    LOG.info(vars.toString());
    return Pair.of(seed + ":" + toString(shrinks), lastException);
  }
}
