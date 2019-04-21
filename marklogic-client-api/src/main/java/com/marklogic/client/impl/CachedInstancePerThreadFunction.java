/*
 * Copyright 2012-2018 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.marklogic.client.impl;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * A {@link Function} that caches results per thread.
 * <p>
 * The Function is thread safe.
 * <p>
 * Upon first invocation from a certain thread it is guaranteed to invoke the {@link Function}'s {@link Function#apply(Object) apply}
 * method to obtain a thread-specific result.
 * <p>
 * Uses a Map internally to cache values created by the Function. The hashCode of the Function's arguments is used as a key for the map.
 * <p>
 * Cached values are wrapped in a {@link SoftReference} to allow them to be garbage collected upon low
 * memory. This may lead to multiple calls to the {@code delegate}'s {@link Function#apply(Object)} method over the lifetime of a
 * certain thread if a previous result was cleared due to low memory.
 *
 * @param <E> the delegate function's parameter type.
 * @param <T> the delegate function's value type.
 */
class CachedInstancePerThreadFunction<E, T> implements Function<E, T> {

  private final ThreadLocal<SoftReference<Map<Integer, T>>> cachedInstances = new ThreadLocal<>();

  /** The underlying function, invoked to originally retrieve the per-thread result. */
  private final Function<E, T> delegate;

  CachedInstancePerThreadFunction(final Function<E, T> delegate) {
    this.delegate = delegate;

    if (null == delegate) {
      throw new IllegalArgumentException("Delegate must not be null.");
    }
  }

  /**
   * Returns the thread-specific instance, possibly creating a new one if there is none exists.
   *
   * @param key value which is passed as an argument to the delegating function. Required to be not {@literal null}.
   * @return a thread specific instance of {@code <T>}. Never {@literal null}.
   */
  @Override
  public T apply(final E key) {
    Objects.requireNonNull(key);

    T result;
    final SoftReference<Map<Integer, T>> cachedInstanceReference = cachedInstances.get();

    // careful, either the reference itself may be null (upon first access from a thread), or the referred-to
    // instance may be null (after a GC run that cleared it out)
    Map<Integer, T> cacheMap = (null != cachedInstanceReference) ? cachedInstanceReference.get() : null;

    if (null == cacheMap) {
      // no map cache instance for the current thread, create a new one ...
      cacheMap = new HashMap<>();
      result = get(key);
      cacheMap.put(key.hashCode(), result);

      // ... and retain it for later re-use
      cachedInstances.set(new SoftReference<>(cacheMap));
    } else {
      // cache map already existing
      result = cacheMap.get(key.hashCode());

      if (null == result) {
        // no cached instance found in the threads cache map
        // use delegate to create a new one and cache it
        result = get(key);
        cacheMap.put(key.hashCode(), result);
      }
    }

    return result;
  }

  /**
   * Creates a instance using the provided {@link #delegate}.
   *
   * @param key key for the delegating function. Expected to be not {@literal null}.
   * @return Result of the delegating function. Never {@literal null}.
   */
  private T get(final E key) {
    final T result = delegate.apply(key);
    if (null == result) {
      throw new IllegalStateException(String.format("Must not return null from %s::apply() (%s)", delegate.getClass().getName(), delegate));
    }

    return result;
  }
}