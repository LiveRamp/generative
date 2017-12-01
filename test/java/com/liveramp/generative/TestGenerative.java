package com.liveramp.generative;

import org.apache.log4j.Level;
import org.junit.Test;

import com.rapleaf.java_support.CommonJUnit4TestCase;

import static com.liveramp.generative.Generative.*;
import static org.junit.Assert.*;

public class TestGenerative extends CommonJUnit4TestCase {


  public TestGenerative() {
    super(Level.INFO);
  }

  @Test
  public void testShouldPass() {
    runTests(20, (testNumber, g) -> {
      Integer theInt = g.anyPositiveIntegerLessThan(50);
      Integer shouldBeEven = theInt * 2;
      assertTrue(shouldBeEven % 2 == 0);
    });
  }

  @Test
  public void testAlwaysFails() {
      runTests(100, (testNumber, g) -> {
        Integer theInt = g.namedVar("The Number").anyPositiveIntegerLessThan(50);
        Integer whatWeMultipliedBy = g.namedVar("What we multiplied by").boundedPositiveInteger(2, 4);
        Integer shouldBeEven = theInt * whatWeMultipliedBy;
        assertTrue(shouldBeEven % 2 == 0);
      });

  }

  @Test
  public void testFailsForSomeInputs() {
      runTests(20, (testNumber, g) -> {
        byte[] data = g.namedVar("The Data").anyByteArrayUpToLength(16);
        assertTrue(data.length < 8);
      });
      throw new RuntimeException("Should have failed");
  }
}