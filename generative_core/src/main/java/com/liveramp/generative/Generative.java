package com.liveramp.generative;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
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

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
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
  private final List<Pair<String, Object>> generated;
  private String varName;

  //Used for generation of "helper" random values that don't need names
  private Generative gen;

  public Generative(long seed) {
    this(seed, new ArrayList<>());
  }

  public Generative(long seed, List<Integer> shrinkIndices) {
    this.seed = seed;
    this.random = new Random(seed);
    this.shrinkIndices = shrinkIndices;
    this.index = new AtomicInteger(0);
    this.gen = this;
    this.generated = new ArrayList<>();
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

  public Random getInternalRandom() {
    return random;
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

  public Generator<Integer> anyInteger() {
    return new ArbitraryBoundedInt(Integer.MIN_VALUE, Integer.MAX_VALUE).gen(this);
  }

  public Generator<Integer> anyPositiveInteger() {
    return anyBoundedInteger(0, Integer.MAX_VALUE);
  }

  public Generator<Integer> anyBoundedInteger(int startInclusive, int endExclusive) {
    return new ArbitraryBoundedInt(startInclusive, endExclusive - 1).gen(this);
  }

  public Generator<Integer> anyIntegerGreaterThan(int startExclusive) {
    return anyBoundedInteger(startExclusive + 1, Integer.MAX_VALUE);
  }

  public Generator<Integer> anyPositiveIntegerLessThan(int endExclusive) {
    return anyBoundedInteger(0, endExclusive);
  }

  public Generator<Integer> anyIntegerLessThan(int endExclusive) {
    return anyBoundedInteger(Integer.MIN_VALUE, endExclusive);
  }

  public Generator<byte[]> anyByteArrayOfLength(int length) {
    return new ArbitraryByteArray(length).gen(this);
  }

  public Generator<byte[]> anyByteArrayUpToLength(int length) {
    return anyByteArrayOfLength(gen.anyPositiveIntegerLessThan(length + 1).get());
  }

  public Generator<byte[]> anyByteArray() {
    return anyByteArrayUpToLength(1024);
  }

  public <T, L extends List<T>> L shuffle(L l) {
    Collections.shuffle(l, random);
    List<T> copy = new ArrayList<>(l);
    saveNamedVar(copy);
    return l;
  }

  public Generator<Boolean> anyBoolean() {
    return new ArbitraryBoolean().gen(this);
  }

  public Generator<Boolean> anyWeightedBoolean(double probabilityTrue) {
    return anyDoubleFrom0To1().map(d -> d < probabilityTrue);
  }

  public Generator<Double> anyDoubleFrom0To1() {
    return ((Arbitrary<Double>)r -> r.nextDouble()).gen(this);
  }


  public Generator<Boolean> atRandom() {
    return anyBoolean();
  }

  public Generator<String> anyStringOfLength(int length) {
    return new ArbitraryString(length).gen(this);
  }

  public Generator<String> anyStringOfLengthUpTo(int length) {
    return anyStringOfLength(gen.anyPositiveIntegerLessThan(length + 1).get());
  }

  public Generator<String> anyString() {
    return anyStringOfLengthUpTo(256);
  }

  public <T> Generator<List<T>> listOfLength(Arbitrary<T> items, int length) {
    return new ListOf<>(items, new Fixed<>(length)).gen(this);
  }

  public <T> Generator<List<T>> listOfLength(Arbitrary<T> items, Arbitrary<Integer> length) {
    return new ListOf<>(items, length).gen(this);
  }

  public <T> Generator<List<T>> listOfLengthUpTo(Arbitrary<T> items, int length) {
    Integer trueLength = gen.anyPositiveIntegerLessThan(length + 1).get();
    return listOfLength(items, trueLength);
  }

  public <T> Generator<List<T>> listOf(Arbitrary<T> items) {
    return listOfLengthUpTo(items, 1000);
  }

  public <T> Generator<Set<T>> setOfSize(Arbitrary<T> items, int size) {
    return new SetOf<>(items, new Fixed<>(size)).gen(this);
  }

  public <T> Generator<Set<T>> setOfSizeUpTo(Arbitrary<T> items, int length) {
    Integer trueLength = gen.anyPositiveIntegerLessThan(length + 1).get();
    return setOfSize(items, trueLength);
  }

  public <T> Generator<Set<T>> setOf(Arbitrary<T> items) {
    return setOfSizeUpTo(items, 1000);
  }

  public <T> Generator<T> anyOf(T... options) {
    List<T> copy = new ArrayList<>();
    Collections.addAll(copy, options);
    return new ElementOf<>(copy).gen(this);
  }

  public <T> Generator<T> anyOf(Collection<T> options) {
    return new ElementOf<>(options).gen(this);
  }

  public static void runTests(int numTests, TestBlock block, String... additionalSeeds) {
    int i;
    for (i = 0; i < numTests; i++) {
      runTestWithSeed(new Random().nextInt(), i, block, true);
    }
    for (String specificSeed : additionalSeeds) {
      runTestWithSeed(specificSeed, i, block);
    }
  }

  public static void runTestsWithoutShrinking(int numTests, TestBlock block, String... additionalSeeds) {
    int i;
    for (i = 0; i < numTests; i++) {
      runTestWithSeed(new Random().nextInt(), i, block, false);
    }
    for (String specificSeed : additionalSeeds) {
      runTestWithSeed(specificSeed, i, block);
    }
  }

  private static void runTestWithSeed(long seed, int testNumber, TestBlock block, boolean shouldShrink) {
    Generative gen = null;
    try {
      gen = new Generative(seed);
      block.run(testNumber, gen);
    } catch (Throwable e) {
      if (!shouldShrink) {
        throw new RuntimeException(e);
      } else {
        Pair<String, Throwable> shrinkSeed = shrink(seed, testNumber, gen.index.get(), block);
        if (shrinkSeed.getRight() != null) {
          throw new RuntimeException("Shrunken test case failed with seed: " + shrinkSeed.getLeft(),
              shrinkSeed.getRight());
        } else {
          throw new RuntimeException("Test case failed with seed: " + seed, e);
        }
      }
    }
  }

  private static void runTestWithSeed(String seed, int testNumber, TestBlock block) {
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
      List<String> splits = new ArrayList<>();
      Collections.addAll(splits, StringUtils.split(s, ","));
      List<Integer> indices = splits.stream()
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
      return Pair.of(seedLong, new ArrayList<>());
    }
  }

  private static Logger LOG = LoggerFactory.getLogger(Generative.class);

  private static Pair<String, Throwable> shrink(long seed, int testNumber, int variableCount, TestBlock block) {
    int shrinkTestNumber = testNumber;
    List<Integer> shrinks = new ArrayList<>();
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
