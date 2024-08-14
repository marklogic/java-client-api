/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.impl;

import java.util.Locale;

import com.marklogic.client.semantics.RDFTypes;
import com.marklogic.client.semantics.SPARQLBinding;

/**
 * Represents binding names and values to go with a SPARQL Query.
 * For details about semantics in MarkLogic see
 * <a href="https://docs.marklogic.com/guide/semantics">
 *   Semantics Developer's Guide</a>
 */
public class SPARQLBindingImpl implements SPARQLBinding {
  private String name;
  private String value;
  private RDFTypes datatype;
  private Locale languageTag;

  public SPARQLBindingImpl(String name, String value, RDFTypes type) {
    this.name = name;
    this.value = value;
    this.datatype = type;
  }
  public SPARQLBindingImpl(String name, String value, Locale languageTag) {
    this.name = name;
    this.value = value;
    this.languageTag = languageTag;
  }
  @Override
  public String getName()        { return name;        }
  @Override
  public String getValue()       { return value;       }
  @Override
  public RDFTypes getDatatype()  { return datatype;    }
  @Override
  public Locale getLanguageTag() { return languageTag; }
}
