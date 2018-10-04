package com.liveramp.generative.default_values;

import java.util.Set;

import org.apache.thrift.meta_data.FieldValueMetaData;
import org.apache.thrift.meta_data.SetMetaData;

import com.liveramp.generative.Arbitrary;
import com.liveramp.generative.ArbitraryBoundedInt;
import com.liveramp.generative.SetOf;

public class SetValueCreator implements DefaultValueCreator<Set> {

  private DefaultValueCreatorFactory defaultValueCreatorFactory;

  public SetValueCreator(DefaultValueCreatorFactory defaultValueCreatorFactory) {
    this.defaultValueCreatorFactory = defaultValueCreatorFactory;
  }

  @SuppressWarnings("unchecked")
  @Override
  public Arbitrary<Set> apply(FieldValueMetaData fv) {
    SetMetaData sv = (SetMetaData)fv;
    DefaultValueCreator<?> elementValueCreator = defaultValueCreatorFactory.apply(sv.elemMetaData);
    Arbitrary<?> arbitraryElement = elementValueCreator.apply(sv.elemMetaData);
    return new SetOf(arbitraryElement, new ArbitraryBoundedInt(0, 10));
  }
}
