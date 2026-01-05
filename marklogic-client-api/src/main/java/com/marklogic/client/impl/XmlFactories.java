/*
 * Copyright (c) 2010-2026 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import java.lang.ref.SoftReference;
import java.util.function.Supplier;

public final class XmlFactories {

	private static final Logger logger = LoggerFactory.getLogger(XmlFactories.class);

  private static final CachedInstancePerThreadSupplier<XMLOutputFactory> cachedOutputFactory =
    new CachedInstancePerThreadSupplier<>(XmlFactories::makeNewOutputFactory);

  private static final CachedInstancePerThreadSupplier<DocumentBuilderFactory> cachedDocumentBuilderFactory =
    new CachedInstancePerThreadSupplier<>(XmlFactories::makeNewDocumentBuilderFactory);

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

	public static XMLInputFactory makeNewInputFactory() {
		XMLInputFactory factory = XMLInputFactory.newFactory();
		// Prevents Polaris warning related to https://cwe.mitre.org/data/definitions/611.html .
		factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.FALSE);
		factory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
		return factory;
	}

	public static TransformerFactory makeNewTransformerFactory() {
		TransformerFactory factory = TransformerFactory.newInstance();
		// Avoids Polaris warning related to https://cwe.mitre.org/data/definitions/611.html .
		// From https://stackoverflow.com/questions/32178558/how-to-prevent-xml-external-entity-injection-on-transformerfactory .
		try {
			factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
		} catch (TransformerConfigurationException e) {
			logTransformerWarning(XMLConstants.FEATURE_SECURE_PROCESSING, e.getMessage());
		}
		try {
			factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
		} catch (IllegalArgumentException e) {
			logTransformerWarning(XMLConstants.ACCESS_EXTERNAL_DTD, e.getMessage());
		}
		try {
			factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
		} catch (IllegalArgumentException e) {
			logTransformerWarning(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, e.getMessage());
		}
		return factory;
	}

	private static void logTransformerWarning(String xmlConstant, String errorMessage) {
		logger.warn("Unable to set {} on TransformerFactory; cause: {}", xmlConstant, errorMessage);
	}

	private static DocumentBuilderFactory makeNewDocumentBuilderFactory() {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		// Default to best practices for conservative security including recommendations per
		// https://github.com/OWASP/CheatSheetSeries/blob/master/cheatsheets/XML_External_Entity_Prevention_Cheat_Sheet.md
		try {
			factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
		} catch (ParserConfigurationException e) {
			logger.warn("Unable to set FEATURE_SECURE_PROCESSING on DocumentBuilderFactory; cause: {}", e.getMessage());
		}
		try {
			factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
		} catch (ParserConfigurationException e) {
			logger.warn("Unable to set disallow-doctype-decl on DocumentBuilderFactory; cause: {}", e.getMessage());
		}
		try {
			factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
		} catch (ParserConfigurationException e) {
			logger.warn("Unable to set external-general-entities on DocumentBuilderFactory; cause: {}", e.getMessage());
		}
		try {
			factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
		} catch (ParserConfigurationException e) {
			logger.warn("Unable to set external-parameter-entities on DocumentBuilderFactory; cause: {}", e.getMessage());
		}
		try {
			factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		} catch (ParserConfigurationException e) {
			logger.warn("Unable to set load-external-dtd on DocumentBuilderFactory; cause: {}", e.getMessage());
		}
		factory.setXIncludeAware(false);
		factory.setExpandEntityReferences(false);
		factory.setNamespaceAware(true);
		factory.setValidating(false);

		return factory;
	}

	/**
	 * Returns a shared {@link DocumentBuilderFactory} configured with secure defaults.
	 * <p>
	 * Creating XML factories is potentially a pretty expensive operation. Using a shared instance helps to amortize
	 * this initialization cost via reuse.
	 *
	 * @return  a securely configured {@link DocumentBuilderFactory}
	 *
	 * @since 8.1.0
	 *
	 * @see #makeNewDocumentBuilderFactory()  if you really (really?) need a non-shared instance
	 */
	public static DocumentBuilderFactory getDocumentBuilderFactory() {
		return cachedDocumentBuilderFactory.get();
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
   * @see #makeNewOutputFactory()  if you really (really?) need a non-shared instance
   */
  public static XMLOutputFactory getOutputFactory() {
    return cachedOutputFactory.get();
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
        cachedInstances.set(new SoftReference<>(cachedInstance));
      }

      return cachedInstance;
    }

  }
}
