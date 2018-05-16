package com.liveramp.generative;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class ElementOf<T> implements Arbitrary<T> {

  private List<T> collection;

  public ElementOf(Collection<T> collection) {
    this.collection = collection instanceof List ? (List)collection : new ArrayList<>(collection);
  }

  @Override
  public T get(Random r) {
    int index = r.nextInt(collection.size());
    return collection.get(index);
  }
}
