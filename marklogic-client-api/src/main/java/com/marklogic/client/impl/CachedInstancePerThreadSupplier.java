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
import java.util.function.Supplier;

/**
 * A supplier that caches results per thread.
 * <p>
 * The supplier is thread safe.
 * <p>
 * Upon first invocation from a certain thread it is guaranteed to invoke the {@code supplier}'s {@code get()}
 * method to obtain a thread-specific result.
 * <p>
 * Cached values are wrapped in a {@link SoftReference} to allow them to be garbage collected upon low
 * memory. This may lead to multiple calls to the {@code delegate}'s {@code get()} method over the lifetime of a
 * certain thread if a previous result was cleared due to low memory.
 *
 * @param <T> the supplier's value type
 */
class CachedInstancePerThreadSupplier<T> implements Supplier<T> {

  private final ThreadLocal<SoftReference<T>> cachedInstances = new ThreadLocal<>();

  /**
   * The underlying supplier, invoked to originally retrieve the per-thread result
   */
  private final Supplier<T> delegate;

  CachedInstancePerThreadSupplier(Supplier<T> delegate) {
    this.delegate = delegate;

    if (null == delegate) {
      throw new IllegalArgumentException("Delegate must not be null");
    }
  }

  /**
   * Returns the thread-specific instance, possibly creating a new one if there is none exists.
   *
   * @return a thread specific instance of {@code <T>}. Never {@literal null}.
   */
  @Override
  public T get() {

    SoftReference<T> cachedInstanceReference = cachedInstances.get();

    // careful, either the reference itself may be null (upon first access from a thread), or the referred-to
    // instance may be null (after a GC run that cleared it out)
    T cachedInstance = (null != cachedInstanceReference) ? cachedInstanceReference.get() : null;

    if (null == cachedInstance) {
      // no instance for the current thread, create a new one ...
      cachedInstance = delegate.get();
      if (null == cachedInstance) {
        throw new IllegalStateException("Must not return null from " + delegate.getClass().getName()
          + "::get() (" + delegate + ")");
      }

      // ... and retain it for later re-use
      cachedInstances.set(new SoftReference<>(cachedInstance));
    }

    return cachedInstance;
  }

}