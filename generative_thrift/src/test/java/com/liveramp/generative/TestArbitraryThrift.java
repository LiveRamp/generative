package com.liveramp.generative;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.thrift.TBase;
import org.apache.thrift.TFieldIdEnum;
import org.apache.thrift.TUnion;
import org.apache.thrift.meta_data.FieldMetaData;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class TestArbitraryThrift {
  Random r = new Random();

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

  @Test
  public void testUnionBackwardCompatibility() {
    for (int i = 0; i < 500; i++) {
      unionBackwardCompatibility(PrimitiveUnion.class);
      unionBackwardCompatibility(ComplexUnion.class);
    }
  }

  private <U extends TUnion> void unionBackwardCompatibility(Class<U> clazz) {
    U u = new ArbitraryThrift<>(clazz).get(r);
    assertNotNull(u.getSetField());
  }

  @Test
  public void testFieldsAreSetAppropriately() {
    for (int i = 0; i < 500; i++) {
      setAndUnsetFields(PrimitiveStruct.class);
      setAndUnsetFields(NestedStruct.class);
      setAndUnsetFields(ComplexStruct.class);
    }
  }

  private <T extends TBase> void setAndUnsetFields(Class<T> clazz) {
    Map<? extends TFieldIdEnum, FieldMetaData> fieldMetaDataMap = ArbitraryThrift.getFieldMetaDataMap(clazz);
    Set<TFieldIdEnum> requiredFields = new HashSet<>();
    List<TFieldIdEnum> optionalFields = new ArrayList<>();
    for (Map.Entry<? extends TFieldIdEnum, FieldMetaData> e : fieldMetaDataMap.entrySet()) {
      switch (ArbitraryThrift.getRequirementType(e.getValue().requirementType)) {
        case REQUIRED:
          requiredFields.add(e.getKey());
          break;
        case OPTIONAL:
          optionalFields.add(e.getKey());
          break;
        default:
          throw new IllegalStateException();
      }
    }
    Collections.shuffle(optionalFields);
    int idx = r.nextInt(optionalFields.size());
    List<TFieldIdEnum> setOptionalFields = optionalFields.subList(0, idx);
    Set<TFieldIdEnum> unsetFields = new HashSet<>(optionalFields.subList(idx, optionalFields.size()));
    ArbitraryThrift<T> arb = ArbitraryThrift.buildStruct(clazz)
        .require(setOptionalFields)
        .unset(unsetFields)
        .build();
    T t = arb.get(r);
    requiredFields.forEach(f -> assertTrue(t.isSet(f)));
    setOptionalFields.forEach(f -> assertTrue(t.isSet(f)));
    unsetFields.forEach(f -> assertFalse(t.isSet(f)));
  }
}
