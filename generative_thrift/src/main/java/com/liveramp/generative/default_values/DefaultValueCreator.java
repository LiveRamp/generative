package com.liveramp.generative.default_values;

import java.util.function.Function;

import org.apache.thrift.meta_data.FieldValueMetaData;

import com.liveramp.generative.Arbitrary;

public interface DefaultValueCreator<T> extends Function<FieldValueMetaData, Arbitrary<T>> {

}
