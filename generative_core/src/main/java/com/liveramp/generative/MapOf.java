package com.liveramp.generative;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MapOf<K, V> implements Arbitrary<Map<K, V>> {

  private final Arbitrary<K> keyGen;
  private final Arbitrary<V> valueGen;
  private final Arbitrary<Integer> numEntriesGen;

  public MapOf(Arbitrary<K> keyGen, Arbitrary<V> valueGen, int numEntriesGen) {
    this(keyGen, valueGen, new Fixed<>(numEntriesGen));
  }

  public MapOf(Arbitrary<K> keyGen, Arbitrary<V> valueGen, Arbitrary<Integer> numEntriesGen) {
    this.keyGen = keyGen;
    this.valueGen = valueGen;
    this.numEntriesGen = numEntriesGen;
  }


  @Override
  public Map<K, V> get(Random r) {
    final Integer numEntries = numEntriesGen.get(r);

    final Map<K, V> res = new HashMap<>(numEntries);

    for (int i=0; i < 10 * numEntries && res.size() < numEntries; i++) {
      res.put(keyGen.get(r), valueGen.get(r));
    }

    return res;
  }
}
