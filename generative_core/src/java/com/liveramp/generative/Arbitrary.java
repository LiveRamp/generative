package com.liveramp.generative;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.Lists;
import org.jetbrains.annotations.NotNull;

public interface Arbitrary<T> {

  default Generator<T> gen(Generative gen) {
    return new Generator<>(this, gen);
  }

  @NotNull
  T get(Random r);

  default List<T> shrink(T val) {
    return Collections.emptyList();
  }

  default Stream<T> stream(Random r) {
    return Stream.generate(() -> get(r));
  }

  default <R> Arbitrary<R> map(Function<T, R> fn) {
    return map(fn, null);
  }

  default <R> Arbitrary<R> map(Function<T, R> fn, Function<R, T> reverse) {
    Arbitrary<T> arb = this;
    return new Arbitrary<R>() {
      @Override
      public R get(Random r) {
        return fn.apply(arb.get(r));
      }

      @Override
      public List<R> shrink(R val) {
        if (reverse != null) {
          return arb.shrink(reverse.apply(val)).stream()
              .map(fn).collect(Collectors.toList());
        } else {
          return Lists.newArrayList();
        }
      }
    };
  }

  default <R> Arbitrary<R> flatMap(Function<T, Arbitrary<R>> fn, Function<R, T> reverse) {
    Arbitrary<T> arb = this;
    return new Arbitrary<R>() {
      @Override
      public R get(Random r) {
        return fn.apply(arb.get(r)).get(r);
      }

      @Override
      public List<R> shrink(R val) {
        if (reverse != null) {
          T apply = reverse.apply(val);
          List<T> shrink = arb.shrink(apply);
          return shrink.stream().flatMap(t -> fn.apply(t).shrink(val).stream()).collect(Collectors.toList());
        } else {
          return Lists.newArrayList();
        }
      }
    };
  }

  default <R> Arbitrary<R> flatMap(Function<T, Arbitrary<R>> fn) {
    return flatMap(fn, null);
  }

}
