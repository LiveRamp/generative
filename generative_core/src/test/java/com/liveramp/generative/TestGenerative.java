package com.liveramp.generative;

import java.util.List;
import java.util.Set;
import java.util.function.Function;

import org.junit.Test;


import static com.liveramp.generative.Generative.*;
import static org.junit.Assert.assertTrue;

public class TestGenerative extends GenerativeTestCase {

  @Test
  public void testShouldPass() {
    runTests(20, (testNumber, g) -> {
      Integer theInt = g.anyPositiveIntegerLessThan(50).get();
      Integer shouldBeEven = theInt * 2;
      assertTrue(shouldBeEven % 2 == 0);
    });
  }

  @Test
  public void testAlwaysFails() {
    try {
      runTests(10, (testNumber, g) -> {
        Integer theInt = g.namedVar("theNumber").anyPositiveIntegerLessThan(50).get();
        Integer shouldBeEven = theInt * 2;
        assertTrue(shouldBeEven % 2 == 1);
      });
      throw new RuntimeException("Should have failed");
    } catch (Exception e) {
      //expected
    }
  }

  @Test
  public void testFailsForSomeInputs() {
    try {
      runTests(20, (testNumber, g) -> {
        byte[] data = g.namedVar("The Data").anyByteArrayUpToLength(16).get();
        assertTrue(data.length < 8);
      });
      throw new RuntimeException("Should have failed");
    } catch (Exception e) {
      //expected
    }
  }

  @Test
  public void testBoundedIntegerIsBounded() {
    runTests(1000, (testNumber, g) -> {
      Integer bound1 = g.namedVar("bound1").anyInteger().get();
      Integer bound2 = g.namedVar("bound2").anyInteger().get();
      int lowerBound = Math.min(bound1, bound2);
      int upperBound = Math.max(bound1, bound2);
      Integer theNumber = g.namedVar("theNumber").anyBoundedInteger(lowerBound, upperBound).get();
      assertTrue("Number should be between bounds", theNumber >= lowerBound && theNumber <= upperBound);
    });
  }

  @Test
  public void testShrunkenIntegerIsBounded() {
    runTests(1000, (testNumber, g) -> {
      Integer bound1 = g.namedVar("bound1").anyInteger().get();
      Integer bound2 = g.namedVar("bound2").anyInteger().get();
      int lowerBound = Math.min(bound1, bound2);
      int upperBound = Math.max(bound1, bound2);
      ArbitraryBoundedInt arb = new ArbitraryBoundedInt(lowerBound, upperBound);
      Integer theNumber = arb.get(g.getInternalRandom());
      List<Integer> shrinks = arb.shrink(theNumber);
      for (Integer shrink : shrinks) {
        assertTrue("Shrink should be between bounds: " + shrink, shrink >= lowerBound && shrink <= upperBound);
      }
    });
  }

  @Test
  public void testSetOfWithGenerator() {
    runTests(10, (testNumber, g) -> {
      Set<Integer> things = g.setOf(g.anyInteger()).get();
      String gen = g.anyBoolean().map(b -> b ? "RED" : "BLUE").get();
    });
  }
}