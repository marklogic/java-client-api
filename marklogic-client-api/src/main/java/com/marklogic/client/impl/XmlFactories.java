/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.impl;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLOutputFactory;
import java.lang.ref.SoftReference;

public final class XmlFactories {

  private static final CachedInstancePerThreadSupplier<XMLOutputFactory> cachedOutputFactory =
    new CachedInstancePerThreadSupplier<XMLOutputFactory>(new Supplier<XMLOutputFactory>() {
      @Override
      public XMLOutputFactory get() {
        return makeNewOutputFactory();
      }
    });

  private XmlFactories() {} // preventing instances of utility class

  /**
   * Returns a new {@link XMLOutputFactory}. This factory will have its
   * {@link XMLOutputFactory#IS_REPAIRING_NAMESPACES} property set to {@code true}.
   * <p>
   * CAUTION: Creating XML factories is potentially a pretty expensive operation. If possible, consider using a shared
   * instance ({@link #getOutputFactory()}) to amortize this initialization cost via reuse.
   *
   * @return  a namespace-repairing {@link XMLOutputFactory}
   *
   * @throws FactoryConfigurationError  see {@link XMLOutputFactory#newInstance()}
   *
   * @see #getOutputFactory()
   */
  public static XMLOutputFactory makeNewOutputFactory() {
    XMLOutputFactory factory = XMLOutputFactory.newInstance();
    factory.setProperty(XMLOutputFactory.IS_REPAIRING_NAMESPACES, true);
    return factory;
  }

  /**
   * Returns a shared {@link XMLOutputFactory}. This factory will have its
   * {@link XMLOutputFactory#IS_REPAIRING_NAMESPACES} property set to {@code true}.
   * <p>
   * Creating XML factories is potentially a pretty expensive operation. Using a shared instance helps to amortize
   * this initialization cost via reuse.
   *
   * @return  a namespace-repairing {@link XMLOutputFactory}
   *
   * @throws FactoryConfigurationError  see {@link XMLOutputFactory#newInstance()}
   *
   * @see #makeNewOutputFactory()  if you really (really?) need an non-shared instance
   */
  public static XMLOutputFactory getOutputFactory() {
    return cachedOutputFactory.get();
  }

  /**
   * Represents a supplier of results.
   *
   * <p>There is no requirement that a new or distinct result be returned each
   * time the supplier is invoked.
   *
   * @param <T> the type of results supplied by this supplier
   */
   // TODO replace with java.util.function.Supplier<T> after Java 8 migration
  interface Supplier<T> {

    /**
     * Gets a result.
     *
     * @return a result
     */
    T get();
  }

  /**
   * A supplier that caches results per thread.
   * <p>
   * The supplier is thread safe.
   * <p>
   * Upon first invocation from a certain thread it is guaranteed to invoke the {@code supplier}'s {@code get()}
   * method to obtain a thread-specific result.
   * <p>
   * Cached values are wrapped in a {@link java.lang.ref.SoftReference} to allow them to be garbage collected upon low
   * memory. This may lead to multiple calls to the {@code delegate}'s {@code get()} method over the lifetime of a
   * certain thread if a previous result was cleared due to low memory.
   *
   * @param <T>  the supplier's value type
   */
  private static class CachedInstancePerThreadSupplier<T> implements Supplier<T> {

    private final ThreadLocal<SoftReference<T>> cachedInstances = new ThreadLocal<SoftReference<T>>();

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
     * @return  a thread specific instance of {@code <T>}. Never {@literal null}.
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
        cachedInstances.set(new SoftReference<T>(cachedInstance));
      }

      return cachedInstance;
    }

  }
}
