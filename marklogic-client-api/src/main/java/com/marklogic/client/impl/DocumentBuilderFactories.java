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

import com.marklogic.client.MarkLogicInternalException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public final class DocumentBuilderFactories {

  private static final DocumentBuilderFactory documentBuilderFactory = makeDocumentBuilderFactory();
  private static final CachedInstancePerThreadSupplier<DocumentBuilder> cachedDocumentBuilder =
    new CachedInstancePerThreadSupplier<>(DocumentBuilderFactories::makeDocumentBuilder);

  private DocumentBuilderFactories() {
  } // preventing instances of utility class

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
  public static DocumentBuilder getNamespaceAwareNotValidating() {
    return cachedDocumentBuilder.get();
  }
}
