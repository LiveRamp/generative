package com.liveramp.generative.default_values;

import java.util.List;

import org.apache.thrift.meta_data.FieldValueMetaData;
import org.apache.thrift.meta_data.ListMetaData;

import com.liveramp.generative.Arbitrary;
import com.liveramp.generative.ArbitraryBoundedInt;
import com.liveramp.generative.ListOf;

public class ListValueCreator implements DefaultValueCreator<List> {

  private DefaultValueCreatorFactory defaultValueCreatorFactory;

  public ListValueCreator(DefaultValueCreatorFactory defaultValueCreatorFactory) {
    this.defaultValueCreatorFactory = defaultValueCreatorFactory;
  }

  @SuppressWarnings("unchecked")
  @Override
  public Arbitrary<List> apply(FieldValueMetaData fv) {
    ListMetaData lv = (ListMetaData)fv;
    DefaultValueCreator<?> elementValueCreator = defaultValueCreatorFactory.apply(lv.elemMetaData);
    Arbitrary<?> arbitraryElement = elementValueCreator.apply(lv.elemMetaData);
    return new ListOf(arbitraryElement, new ArbitraryBoundedInt(0, 10));
  }

}
