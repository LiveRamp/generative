package com.liveramp.generative;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.thrift.TFieldIdEnum;
import org.junit.Assert;
import org.junit.Test;

public class TestArbitraryThrift {

  @Test
  public void testSimpleSpecifiedFields() {
    Map<TFieldIdEnum, Arbitrary<?>> fieldArbitraries = new HashMap<>();
    fieldArbitraries.put(LongSet._Fields.LONGS,
        new SetOf<>(new ArbitraryBoundedInt(0, 9).map(i -> i.longValue()), 1000));

    ArbitraryThrift<LongSet> arb = new ArbitraryThrift<>(LongSet.class, fieldArbitraries);

    Random r = new Random();
    LongSet result = arb.get(r);

    //Adding 1000 random values should give us all 10 unique possible values
    Assert.assertEquals(10, result.get_longs_size());
  }

  @Test
  public void testShrink() {
    ArbitraryThrift<LongSet> arb = new ArbitraryThrift<>(LongSet.class);
    LongSet x = arb.get(new Random());
    List<LongSet> shrunk = arb.shrink(x);
    //current code always produces 3 possibilities
    Assert.assertEquals(3, shrunk.size());
    //first possibilities should be drastic things like removing all elements
    Assert.assertEquals(0, shrunk.get(0).get_longs_size());
  }

}