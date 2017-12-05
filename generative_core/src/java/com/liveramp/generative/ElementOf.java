package com.liveramp.generative;

import java.util.Collection;
import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;
import org.jetbrains.annotations.NotNull;

public class ElementOf<T> implements Arbitrary<T> {

  private List<T> collection;

  public ElementOf(Collection<T> collection) {
    this.collection = collection instanceof List ? (List)collection : Lists.newArrayList(collection);
  }

  @NotNull
  @Override
  public T get(Random r) {
    int index = r.nextInt(collection.size());
    return collection.get(index);
  }
}
