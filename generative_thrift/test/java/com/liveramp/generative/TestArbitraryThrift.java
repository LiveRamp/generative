package com.liveramp.generative;

import java.util.List;
import java.util.Random;

import com.google.common.collect.Maps;
import org.apache.log4j.Level;
import org.apache.thrift.TFieldIdEnum;
import org.junit.Test;

import com.liveramp.audience.generated.AudienceMember;
import com.liveramp.commons.collections.map.MapBuilder;
import com.liveramp.types.anonymous_records.AnonymousRecord;
import com.rapleaf.java_support.CommonJUnit4TestCase;
import com.rapleaf.types.new_person_data.LRCField;
import com.rapleaf.types.new_person_data.LongSet;


public class TestArbitraryThrift extends CommonJUnit4TestCase {


  public TestArbitraryThrift() {
    super(Level.INFO);
  }

  @Test
  public void testSimpleSpecifiedFields() {

    ArbitraryThrift<LongSet> arb = new ArbitraryThrift<>(LongSet.class,
        MapBuilder.<TFieldIdEnum, Arbitrary<?>>of(LongSet._Fields.LONGS,
            new SetOf<>(new ArbitraryBoundedInt(0, 10).map(i -> i.longValue()), 10))
            .get());

    arb.get(new Random());
  }

  @Test
  public void testDefaultFields() {
    ArbitraryThrift<LRCField> arbField = new ArbitraryThrift<>(LRCField.class);
    arbField.get(new Random());
    ArbitraryThrift<AnonymousRecord> arbField2 = new ArbitraryThrift<>(AnonymousRecord.class);
    arbField2.get(new Random());
  }

  @Test
  public void testShrink() {
    ArbitraryThrift<AnonymousRecord> arb = new ArbitraryThrift<>(AnonymousRecord.class);
    AnonymousRecord x = arb.get(new Random());
    arb.shrink(x);
  }

}