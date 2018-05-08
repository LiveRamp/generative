package com.liveramp.generative;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import com.google.common.collect.Lists;
import org.jetbrains.annotations.NotNull;

public class RandomDelayThreadFactory implements ThreadFactory {

  private final List<Thread> chaosMonkeyThreads;
  private final List<Thread> producedThreads;
  private final Generator<Integer> delayInterval;
  private final TimeUnit delayIntervalUnit;
  private final Generator<Integer> delayLength;
  private final TimeUnit delayLengthUnit;
  private ThreadFactory internal;

  public RandomDelayThreadFactory(
      int delayThreads,
      Generator<Integer> delayInterval,
      TimeUnit delayIntervalUnit,
      Generator<Integer> delayLength,
      TimeUnit delayLengthUnit,
      ThreadFactory internal) {
    this.producedThreads = Collections.synchronizedList(Lists.newArrayList());
    this.delayInterval = delayInterval;
    this.delayIntervalUnit = delayIntervalUnit;
    this.delayLength = delayLength;
    this.delayLengthUnit = delayLengthUnit;
    this.internal = internal;
    this.chaosMonkeyThreads = Lists.newArrayList();
    int count = 0;
    for (int i = 0; i < delayThreads; i++) {
      Thread chaosMonkeyThread = new Thread(new ChaosMonkey());
      chaosMonkeyThread.setName("chaos-monkey-thread-" + count);
      chaosMonkeyThread.setDaemon(true);
      chaosMonkeyThread.start();
      count++;
    }

  }

  @Override
  public Thread newThread(@NotNull Runnable r) {
    Thread thread = internal.newThread(r);
    producedThreads.add(thread);
    return thread;
  }

  private class ChaosMonkey implements Runnable {

    @Override
    public void run() {
      while (true) {
        Long timeout;
        synchronized (delayInterval) {
          timeout = delayInterval.get().longValue();
        }
        try {
          delayIntervalUnit.sleep(timeout);
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }

        Optional<Thread> t = Optional.empty();
        synchronized (producedThreads) {
          if (!producedThreads.isEmpty()) {
            int index = new Random().nextInt(producedThreads.size());
            t = Optional.of(producedThreads.remove(index));
          }
        }

        if (t.isPresent()) {
          synchronized (delayLength) {
            timeout = delayLength.get().longValue();
          }
          t.get().suspend();
          try {
            delayLengthUnit.sleep(timeout);
          } catch (InterruptedException e) {
            throw new RuntimeException(e);
          } finally {
            t.get().resume();
            producedThreads.add(t.get());
          }
        }
      }
    }
  }
}
