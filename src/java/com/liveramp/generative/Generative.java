package com.liveramp.generative;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.google.common.collect.Lists;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Generative {

  private long seed = 0;
  private Random random;

  //For dealing with shrinking
  private AtomicInteger index;
  private List<Integer> shrinkIndices;
  private AtomicBoolean lastShrinkExhausted = new AtomicBoolean(false);

  //For printing nice names for generated variables
  private List<Pair<String, Object>> generated = Lists.newArrayList();
  private String varName;

  //Used for generation of "helper" random values that don't need names
  private Generative gen;

  public Generative(long seed) {
    this(seed, Lists.newArrayList());
  }

  public Generative(long seed, List<Integer> shrinkIndices) {
    this.seed = seed;
    this.random = new Random(seed);
    this.shrinkIndices = shrinkIndices;
    this.index = new AtomicInteger(0);
    this.gen = this;
  }

  //There's almost certainly a less dumb way to do this
  public Generative(Generative g, String name) {
    this.seed = g.seed;
    this.random = g.random;
    this.shrinkIndices = g.shrinkIndices;
    this.index = g.index;
    this.generated = g.generated;
    this.varName = name;
    this.lastShrinkExhausted = g.lastShrinkExhausted;
    this.gen = g;
  }

  public Generative namedVar(String name) {
    return new Generative(this, name);
  }

  public <T> T generate(Arbitrary<T> arbitrary) {
    T val = arbitrary.get(random);

    Optional<T> t = attemptShrink(arbitrary, val, index.get());

    T returnVal = t.orElse(val);

    saveNamedVar(returnVal);
    index.incrementAndGet();
    return returnVal;
  }

  @NotNull
  private <T> Optional<T> attemptShrink(Arbitrary<T> arbitrary, T val, int currentIndex) {
    if (currentIndex < shrinkIndices.size() && shrinkIndices.get(currentIndex) >= 0) {
      Integer shrinkIndex = shrinkIndices.get(currentIndex);
      List<T> shrinks = arbitrary.shrink(val);
      if (shrinkIndex < shrinks.size()) {
        T t = shrinks.get(shrinkIndex);
        return Optional.of(t);
      } else {
        lastShrinkExhausted.set(true);
        return Optional.of(val);
      }
    }
    return Optional.empty();
  }

  private <T> void saveNamedVar(T t) {
    if (varName != null) {
      generated.add(Pair.of(varName, t));
    }
  }

  public Integer anyInteger() {
    return generate(new ArbitraryBoundedInt(Integer.MIN_VALUE, Integer.MAX_VALUE));
  }

  public Integer anyPositiveInteger() {
    return boundedPositiveInteger(0, Integer.MAX_VALUE);
  }

  public Integer boundedPositiveInteger(int startInclusive, int endExclusive) {
    return generate(new ArbitraryBoundedInt(startInclusive, endExclusive - 1));
  }

  public Integer anyIntegerGreaterThan(int startExclusive) {
    return boundedPositiveInteger(startExclusive + 1, Integer.MAX_VALUE);
  }

  public Integer anyPositiveIntegerLessThan(int endExclusive) {
    return boundedPositiveInteger(0, endExclusive);
  }

  public Integer anyIntegerLessThan(int endExclusive) {
    return boundedPositiveInteger(Integer.MIN_VALUE, endExclusive);
  }

  public byte[] anyByteArrayOfLength(int length) {
    return generate(new ArbitraryByteArray(length));
  }

  public byte[] anyByteArrayUpToLength(int length) {
    return anyByteArrayOfLength(gen.anyPositiveIntegerLessThan(length + 1));
  }

  public byte[] anyByteArray() {
    return anyByteArrayUpToLength(1024);
  }

  public <T, L extends List<T>> L shuffle(L l) {
    Collections.shuffle(l, random);
    List<T> copy = Lists.newArrayList(l);
    saveNamedVar(copy);
    return l;
  }

  public boolean anyBoolean() {
    return generate(new ArbitraryBoolean());
  }

  public boolean atRandom() {
    return anyBoolean();
  }

  public String anyStringOfLength(int length) {
    return generate(new ArbitraryString(length));
  }

  public String anyStringOfLengthUpTo(int length) {
    return anyStringOfLength(gen.anyPositiveIntegerLessThan(length + 1));
  }

  public String anyString() {
    return anyStringOfLengthUpTo(256);
  }

  public <T> List<T> listOfLength(Arbitrary<T> items, int length) {
    return generate(new ListOf<>(items, length));
  }

  public <T> List<T> listOfLengthUpTo(Arbitrary<T> items, int length) {
    Integer trueLength = gen.anyPositiveIntegerLessThan(length + 1);
    return listOfLength(items, trueLength);
  }

  public <T> List<T> listOf(Arbitrary<T> items) {
    return listOfLengthUpTo(items, 1000);
  }

  public <T> Set<T> setOfSize(Arbitrary<T> items, int size) {
    return generate(new SetOf<>(items, size));
  }

  public <T> Set<T> setOfSizeUpTo(Arbitrary<T> items, int length) {
    Integer trueLength = gen.anyPositiveIntegerLessThan(length + 1);
    return setOfSize(items, trueLength);
  }

  public <T> Set<T> setOf(Arbitrary<T> items) {
    return setOfSizeUpTo(items, 1000);
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
      Pair<String, Throwable> shrinkSeed = shrink(seed, testNumber, gen.index.get(), block);
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
          shrinkExhausted = gen.lastShrinkExhausted.get();
          shrinks.set(shrinks.size() - 1, shrinks.get(shrinks.size() - 1) + 1);
        } catch (Throwable e) {
          lastException = e;
          testPassed = false;
        }
      }
    }
    LOG.info("Returning shrunken test case - performed " + (shrinkTestNumber - testNumber) + " shrinks");
    StringBuilder vars = new StringBuilder("Generated variables were: \n");
    for (Pair<String, Object> entry : gen.generated) {
      vars.append(entry.getKey() + " : " + entry.getValue() + "\n");
    }
    LOG.info(vars.toString());
    return Pair.of(seed + ":" + toString(shrinks), lastException);
  }
}
