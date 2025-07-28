/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;

import com.marklogic.client.semantics.RDFTypes;
import com.marklogic.client.semantics.SPARQLBinding;
import com.marklogic.client.semantics.SPARQLBindings;

/**
 * Represents binding names and values to go with a SPARQL Query.
 * For details about semantics in MarkLogic see
 * <a href="https://docs.marklogic.com/guide/semantics">
 *   Semantics Developer's Guide</a>
 */
public class SPARQLBindingsImpl extends TreeMap<String, List<SPARQLBinding>> implements SPARQLBindings {
  @Override
  public SPARQLBindings bind(String name, String value) {
    return bind(name, value, (RDFTypes) null);
  }

  @Override
  public SPARQLBindings bind(String name, String value, RDFTypes type) {
    if ( name == null  ) throw new IllegalArgumentException("name cannot be null");
    if ( value == null ) throw new IllegalArgumentException("value cannot be null");
    add(new SPARQLBindingImpl(name, value, type));
    return this;
  }

  @Override
  public SPARQLBindings bind(String name, String value, Locale languageTag) {
    if ( name == null  ) throw new IllegalArgumentException("name cannot be null");
    if ( value == null ) throw new IllegalArgumentException("value cannot be null");
    add(new SPARQLBindingImpl(name, value, languageTag));
    return this;
  }

  private void add(SPARQLBinding binding) {
    String name = binding.getName();
    List<SPARQLBinding> bindings = this.get(name);
    if ( bindings == null ) bindings = new ArrayList<>();
    bindings.add(binding);
    this.put(name, bindings);
  }
};

