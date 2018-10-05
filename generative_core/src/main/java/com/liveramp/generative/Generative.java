package com.liveramp.generative;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Random;
import java.util.Set;
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
  private Optional<Map<String, ShrinkState>> shrinkStates;
  private final Map<String, Pair<Object, Arbitrary>> generated;

  //For printing nice names for generated variables
  private String varName;
  private final AtomicInteger index;

  //Used for generation of "helper" random values that don't need names
  private Generative gen;

  public Generative(long seed) {
    this(seed, Optional.empty());
  }

  public Generative(long seed, Map<String, ShrinkState> shrinkStates) {
    this(seed, Optional.ofNullable(shrinkStates));
  }


  public Generative(long seed, Optional<Map<String, ShrinkState>> shrinkStates) {
    this.seed = seed;
    this.random = new Random(seed);
    this.gen = this;
    this.generated = new HashMap<>();
    this.shrinkStates = shrinkStates.map(HashMap::new);
    this.index = new AtomicInteger(0);
  }

  //There's almost certainly a less dumb way to do this
  public Generative(Generative g, String name) {
    this.seed = g.seed;
    this.random = g.random;
    this.generated = g.generated;
    this.varName = name;
    this.gen = g;
    this.shrinkStates = g.shrinkStates;
    this.index = g.index;
  }

  public Random getInternalRandom() {
    return random;
  }

  public Generative namedVar(String name) {
    return new Generative(this, name);
  }

  public <T> T generate(Arbitrary<T> arbitrary) {

    T generatedVal = arbitrary.get(random);

    //We only engage shrinking behavior over named variables
    if (varName != null) {
      String realVarName = generated.containsKey(varName) ? varName + index.getAndIncrement() : varName;
      generatedVal = attemptShrink(generatedVal, realVarName);
      saveNamedVar(realVarName, arbitrary, generatedVal);
    }

    return generatedVal;
  }

  private <T> T attemptShrink(T generatedVal, String realVarName) {
    if (shrinkStates.isPresent()) {
      ShrinkState state = shrinkStates.get().get(realVarName);
      if (state != null && !state.shrinks.isEmpty()) {
        generatedVal = (T)state.shrinks.get(state.index);
      }
    }
    return generatedVal;
  }

  private <T> void saveNamedVar(String realVarName, Arbitrary<T> arb, T t) {
    if (varName != null) {
      generated.put(realVarName, Pair.of(t, arb));
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

  public Generator<byte[]> anyByteArrayOfLength(Arbitrary<Integer> length) {
    return new ArbitraryByteArray(length).gen(this);
  }


  public Generator<byte[]> anyByteArrayOfLength(int length) {
    return new ArbitraryByteArray(new Fixed<>(length)).gen(this);
  }

  public Generator<byte[]> anyByteArrayUpToLength(int length) {
    return anyByteArrayOfLength(gen.anyPositiveIntegerLessThan(length + 1));
  }

  public Generator<byte[]> anyByteArray() {
    return anyByteArrayOfLength(gen.anyPositiveIntegerLessThan(1025));
  }

  public <T, L extends List<T>> L shuffle(L l) {
    Collections.shuffle(l, random);
    List<T> copy = new ArrayList<>(l);
    //saveNamedVar(copy);
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
    return setOfSize(items, gen.anyPositiveIntegerLessThan(length + 1));
  }

  public <T> Generator<Set<T>> setOfSize(Arbitrary<T> items, Arbitrary<Integer> length) {
    return new SetOf<>(items, length).gen(this);
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
        logGeneratedNamedVars(gen);
        throw new RuntimeException(e);
      } else {
        Pair<String, Throwable> shrinkSeed = shrink(seed, testNumber, gen, block);
        if (shrinkSeed.getRight() != null) {
          throw new RuntimeException("Shrunken test case failed with seed: " + shrinkSeed.getLeft(),
              shrinkSeed.getRight());
        } else {
          throw new RuntimeException("Test case failed with seed: " + seed, e);
        }
      }
    }
  }

  private static void logGeneratedNamedVars(Generative gen) {
    if (gen != null) {
      StringBuilder vars = new StringBuilder("Generated variables were: \n");
      for (Map.Entry<String, Pair<Object, Arbitrary>> entry : gen.generated.entrySet()) {
        vars.append(entry.getKey() + " : " + entry.getValue().getRight().prettyPrint(entry.getValue().getLeft()) + "\n");
      }
      LOG.info(vars.toString());
    }
  }

  private static void runTestWithSeed(String seed, int testNumber, TestBlock block) {
    Generative gen = null;
    try {
      Pair<Long, Integer> seedVars = parseSeed(seed);
      gen = new Generative(seedVars.getLeft());

      if (seedVars.getRight() > -1) {
        //if we need to shrink, this is a dry run to get initial variables
        try {
          block.run(testNumber, gen);
        } catch (Throwable e) {
          //don't care about exceptions here - in fact we sort of expect them given how shrunken tests cases are derived
        }
        Map<String, ShrinkState> shrinkStates = getShrinkStatesFromGeneratedValues(gen.generated);
        for (int i = 0; i < seedVars.getRight(); i++) {
          shrinkStates = iterateShrinkStates(shrinkStates);
        }
        gen = new Generative(seedVars.getLeft(), shrinkStates);
        block.run(testNumber, gen);
      } else {
        block.run(testNumber, gen);
      }
    } catch (Throwable e) {
      logGeneratedNamedVars(gen);
      throw new RuntimeException("Error during test while using seed: " + seed, e);
    }
  }

  private static Pair<Long, Integer> parseSeed(String seed) {
    String[] split = StringUtils.split(seed, ";");
    long seedLong = Long.parseLong(split[0]);
    if (split.length > 1) {
      return Pair.of(seedLong, Integer.parseInt(split[1]));
    } else {
      return Pair.of(seedLong, -1);
    }
  }

  private static Logger LOG = LoggerFactory.getLogger(Generative.class);

  private static Pair<String, Throwable> shrink(long seed, int testNumber, Generative originalGen, TestBlock block) {
    Throwable lastException = null;
    Map<String, Pair<Object, Arbitrary>> generated = originalGen.generated;
    Map<String, ShrinkState> shrinkStates = getShrinkStatesFromGeneratedValues(generated);
    Generative gen = null;

    int shrinksPerformed = 0;

    try {
      while (shrinkStates != null) {
        shrinksPerformed++;
        gen = new Generative(seed, shrinkStates);
        block.run(testNumber + shrinksPerformed, gen);
        shrinkStates = iterateShrinkStates(shrinkStates);
      }
    } catch (Throwable e) {
      lastException = e;
    }
    LOG.info("Returning shrunken test case - performed " + shrinksPerformed + " shrinks");
    logGeneratedNamedVars(gen);
    return Pair.of(seed + ";" + shrinksPerformed, lastException);
  }

  private static Map<String, ShrinkState> getShrinkStatesFromGeneratedValues(Map<String, Pair<Object, Arbitrary>> generated) {
    return generated.entrySet().stream().collect(
        Collectors.toMap(
            e -> e.getKey(),
            e -> new ShrinkState<>(e.getValue().getRight().shrink(e.getValue().getLeft()), 0)));
  }

  private static Map<String, ShrinkState> iterateShrinkStates(Map<String, ShrinkState> shrinkStates) {
    OptionalInt minIndex = shrinkStates.values().stream().mapToInt(s -> s.index).min();
    Map<String, ShrinkState> result = new HashMap<>(shrinkStates);
    if (minIndex.isPresent()) {
      for (Map.Entry<String, ShrinkState> entry : shrinkStates.entrySet()) {
        if (entry.getValue().index == minIndex.getAsInt() && entry.getValue().index < entry.getValue().shrinks.size()) {
          result.put(entry.getKey(), new ShrinkState<>(entry.getValue().shrinks, entry.getValue().index + 1));
          return result;
        }
      }
    }
    return null;
  }

  private static class ShrinkState<T> {
    private List<T> shrinks;
    private int index;

    private ShrinkState(List<T> shrinks, int index) {
      this.shrinks = shrinks;
      this.index = index;
    }
  }
}
