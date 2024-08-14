/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.semantics;

import java.util.Locale;

/**
 * <p>Represents a binding name, value, and type or language tag.</p>
 *
 * <p>For details about RDF, SPARQL, and semantics in MarkLogic see the <a
 * href="https://docs.marklogic.com/guide/semantics" target="_top">Semantics
 * Developer's Guide</a>.
 */
public interface SPARQLBinding {
  String getName();
  String getValue();
  RDFTypes getDatatype();
  Locale getLanguageTag();
}

