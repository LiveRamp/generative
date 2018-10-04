package com.liveramp.generative;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.apache.thrift.TFieldIdEnum;
import org.apache.thrift.TUnion;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TestArbitraryTUnion {
  Random r = new Random();

  @Test
  public void testGetEmptyUnion() {
    ArbitraryThrift<PrimitiveUnion> arbitrary = ArbitraryThrift.buildUnion(PrimitiveUnion.class)
        .allowEmpty()
        .selectFrom()
        .build();
    assertNull(arbitrary.get(r).getSetField());
  }

  @Test
  public void testSelectedFieldsChosen() {
    for (int i = 0; i < 50; i++) {
      selectChosenFields(PrimitiveUnion.class, PrimitiveUnion.metaDataMap.keySet());
      selectChosenFields(ComplexUnion.class, ComplexUnion.metaDataMap.keySet());
    }
  }

  private <U extends TUnion> void selectChosenFields(Class<U> clazz, Collection<? extends TFieldIdEnum> possibleFields) {
    ArrayList<TFieldIdEnum> fieldList = new ArrayList<>(possibleFields);
    Set<TFieldIdEnum> expected = new HashSet<>();
    for (int i = 0; i < 1 + r.nextInt(fieldList.size() - 1); i++) {
      expected.add(fieldList.get(r.nextInt(fieldList.size())));
    }
    ArbitraryThrift<U> arbitrary = ArbitraryThrift.buildUnion(clazz)
        .selectFrom(expected)
        .build();
    Set<TFieldIdEnum> actual = new HashSet<>();
    for (int i = 0; i < expected.size() * 20; i++) {
      actual.add(arbitrary.get(r).getSetField());
    }
    assertEquals(expected, actual);
  }

  @Test
  public void testShrinkUnset() {
    PrimitiveUnion u = new PrimitiveUnion();
    ArbitraryThrift<PrimitiveUnion> arbitrary = ArbitraryThrift.buildUnion(PrimitiveUnion.class).build();
    arbitrary.shrink(u);
    assertNull(u.getSetField());
  }

  @Test
  public void testShrunkUnionHasSameField() {
    for (int i = 0; i < 50; i++) {
      shrunkUnionHasSameField(PrimitiveUnion.class);
      shrunkUnionHasSameField(ComplexUnion.class);
    }
  }

  private <U extends TUnion> void shrunkUnionHasSameField(Class<U> clazz) {
    ArbitraryThrift<U> arbitrary = ArbitraryThrift.buildUnion(clazz).build();
    U u = arbitrary.get(r);
    arbitrary.shrink(u).forEach(shrunk -> assertEquals(u.getSetField(), shrunk.getSetField()));
  }
}
