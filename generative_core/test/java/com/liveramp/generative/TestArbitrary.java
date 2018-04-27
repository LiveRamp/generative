package com.liveramp.generative;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javafx.util.Pair;
import org.apache.log4j.Level;
import org.junit.Assert;
import org.junit.Test;

import com.rapleaf.java_support.CommonJUnit4TestCase;

public class TestArbitrary extends CommonJUnit4TestCase {

  public TestArbitrary() {
    super(Level.INFO);
  }


  @Test
  public void testFlatMapAndMap() {
    Generative2.runTests(100, (testNum, g) -> {
      ArbitraryBoundedInt arbitrarySize = new ArbitraryBoundedInt(0, 10);

      Arbitrary<byte[]> arbitraryByteArrayOfSize = arbitrarySize.flatMap(
          size -> new ArbitraryByteArray(size),
          b -> b.length
      );

      Arbitrary<String> asString = arbitraryByteArrayOfSize.map(
          b -> new String(b),
          s -> s.getBytes()
      );

      List<String> strings = g.namedVar("The Strings").listOfLength(asString, 10000).get();

      Set<Integer> collect = strings.stream().map(s -> s.length()).collect(Collectors.toSet());
      Assert.assertEquals("flatMap construction should generate strings of all 11 possible lengths",
          11, collect.size());

    });
  }
}
