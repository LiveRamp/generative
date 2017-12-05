package com.liveramp.generative;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import org.jetbrains.annotations.NotNull;

public class AbitraryUtil {
  @NotNull
  public static <T, C extends Collection<T>> List<C> shrinkCollection(C val, Arbitrary<T> internal, Function<List<T>, C> fn) {
    List<List<T>> shrinkElements = val.stream().map(v -> {
      List<T> shrink = internal.shrink(v);
      shrink.add(v);
      return shrink;
    }).collect(Collectors.toList());

    List<List<T>> result = Lists.newArrayList();
    //Limit the explosion of possible values here
    result.add(getIth(shrinkElements, 0));
    result.add(getIth(shrinkElements, 1));
    result.add(getIth(shrinkElements, 2));
    return result.stream().map(fn).collect(Collectors.toList());
  }

  private static <T> List<T> getIth(List<List<T>> shrinkElements, int i) {
    return shrinkElements.stream().map(l -> l.get(Math.min(i, l.size() - 1))).collect(Collectors.toList());
  }
}
