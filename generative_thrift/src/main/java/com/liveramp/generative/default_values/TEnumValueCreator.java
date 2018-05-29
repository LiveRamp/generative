package com.liveramp.generative.default_values;

import java.util.ArrayList;
import java.util.EnumSet;

import org.apache.thrift.meta_data.EnumMetaData;
import org.apache.thrift.meta_data.FieldValueMetaData;

import com.liveramp.generative.Arbitrary;
import com.liveramp.generative.ArbitraryBoundedInt;
import com.liveramp.generative.ElementOf;

@SuppressWarnings("unchecked")
public class TEnumValueCreator implements DefaultValueCreator<Enum> {

  @Override
  public Arbitrary<Enum> apply(FieldValueMetaData fieldValueMetaData) {
    EnumMetaData ev = (EnumMetaData)fieldValueMetaData;
    EnumSet<? extends Enum> elements = EnumSet.allOf(castTEnumToEnum(ev.enumClass));
    ArrayList<Enum> enums = new ArrayList<>(elements);
    return new ArbitraryBoundedInt(0, 128)
        .flatMap(i -> new ElementOf<>(enums));
  }

  private static Class<Enum> castTEnumToEnum(Class specific) {
    return (Class<Enum>)specific;
  }

}
