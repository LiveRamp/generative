package com.liveramp.generative.default_values;

import java.util.Map;

import org.apache.thrift.meta_data.FieldValueMetaData;
import org.apache.thrift.meta_data.MapMetaData;

import com.liveramp.generative.Arbitrary;
import com.liveramp.generative.MapOf;

@SuppressWarnings("unchecked")
class MapValueCreator<K, V> implements DefaultValueCreator<Map<K, V>> {

  private DefaultValueCreatorFactory defaultValueCreatorFactory;

  public MapValueCreator(DefaultValueCreatorFactory defaultValueCreatorFactory) {
    this.defaultValueCreatorFactory = defaultValueCreatorFactory;
  }

  @Override
  public Arbitrary<Map<K, V>> apply(FieldValueMetaData fv) {
    MapMetaData mv = (MapMetaData)fv;
    DefaultValueCreator<K> keyCreator = (DefaultValueCreator<K>)defaultValueCreatorFactory.apply(mv.keyMetaData);
    DefaultValueCreator<V> valueCreator = (DefaultValueCreator<V>)defaultValueCreatorFactory.apply(mv.valueMetaData);
    return new MapOf<>(keyCreator.apply(mv.keyMetaData), valueCreator.apply(mv.valueMetaData), 128);
  }
}
