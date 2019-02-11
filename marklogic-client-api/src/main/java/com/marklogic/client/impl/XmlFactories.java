/*
 * Copyright 2012-2016 MarkLogic Corporation
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

import com.marklogic.client.MarkLogicInternalException;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSParser;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLOutputFactory;

public final class XmlFactories {

  private static final CachedInstancePerThreadSupplier<XMLOutputFactory> cachedOutputFactory =
    new CachedInstancePerThreadSupplier<>(XmlFactories::makeNewOutputFactory);

  private static final DocumentBuilderFactory documentBuilderFactory = makeDocumentBuilderFactory();
  private static final CachedInstancePerThreadSupplier<DocumentBuilder> cachedDocumentBuilder =
    new CachedInstancePerThreadSupplier<>(XmlFactories::makeDocumentBuilder);

  private static final CachedInstancePerThreadFunction<DocumentBuilder, LSParser> cachedLsParser =
    new CachedInstancePerThreadFunction<>(XmlFactories::makeLSParser);

  private XmlFactories() {
  } // preventing instances of utility class

  /**
   * Returns a new {@link XMLOutputFactory}. This factory will have its
   * {@link XMLOutputFactory#IS_REPAIRING_NAMESPACES} property set to {@code true}.
   * <p>
   * CAUTION: Creating XML factories is potentially a pretty expensive operation. If possible, consider using a shared
   * instance ({@link #getOutputFactory()}) to amortize this initialization cost via reuse.
   *
   * @return a namespace-repairing {@link XMLOutputFactory}
   * @throws FactoryConfigurationError see {@link XMLOutputFactory#newInstance()}
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
   * @return a namespace-repairing {@link XMLOutputFactory}
   * @throws FactoryConfigurationError see {@link XMLOutputFactory#newInstance()}
   * @see #makeNewOutputFactory()  if you really (really?) need an non-shared instance
   */
  public static XMLOutputFactory getOutputFactory() {
    return cachedOutputFactory.get();
  }

  private static DocumentBuilderFactory makeDocumentBuilderFactory() {
    final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setNamespaceAware(true);
    factory.setValidating(false);

    return factory;
  }

  private static DocumentBuilder makeDocumentBuilder() {
    try {
      return documentBuilderFactory.newDocumentBuilder();
    } catch (final ParserConfigurationException e) {
      throw new MarkLogicInternalException("Failed to create a document builder.", e);
    }
  }

  /**
   * Returns a shared {@link DocumentBuilder}. This DocumentBuilder is created <b>namespace-aware</b> and <b>not validating</b>.
   * <p>
   * Creating a DocumentBuilder is potentially a pretty expensive operation. Using a shared instance helps to amortize
   * this initialization cost via reuse.
   *
   * @return a namespace-aware, non-validating {@link DocumentBuilder}
   */
  public static DocumentBuilder getNsAwareNotValidatingDocBuilder() {
    return cachedDocumentBuilder.get();
  }

  /**
   * Returns a shared {@link LSParser}. This LSParser will have {@link DOMImplementationLS#MODE_SYNCHRONOUS} and no {@code schemaType} set.
   * <p>
   * Every first usage per {@code documentBuilder} instance will create a new {@link LSParser}.
   * Further usages might return a cached LSParser instance.
   *
   * @param documentBuilder DocumentBuilder from which the LSParser is created.
   * @return a synchronous LSParser
   */
  public static LSParser getSynchronousLSParser(final DocumentBuilder documentBuilder) {
    return cachedLsParser.apply(documentBuilder);
  }

  private static LSParser makeLSParser(final DocumentBuilder documentBuilder) {
    final DOMImplementationLS domImpl = (DOMImplementationLS) documentBuilder.getDOMImplementation();

    return domImpl.createLSParser(DOMImplementationLS.MODE_SYNCHRONOUS, null);
  }
}
