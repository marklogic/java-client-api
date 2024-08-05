/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.impl;

import java.util.Arrays;
import java.util.Locale;

import com.marklogic.client.document.ServerTransform;
import com.marklogic.client.io.marker.TextWriteHandle;
import com.marklogic.client.query.QueryDefinition;
import com.marklogic.client.query.RawQueryByExampleDefinition;
import com.marklogic.client.semantics.Capability;
import com.marklogic.client.semantics.GraphPermissions;
import com.marklogic.client.semantics.RDFTypes;
import com.marklogic.client.semantics.SPARQLBindings;
import com.marklogic.client.semantics.SPARQLQueryDefinition;
import com.marklogic.client.semantics.SPARQLRuleset;

public class SPARQLQueryDefinitionImpl implements SPARQLQueryDefinition {

  private String sparql;
  private SPARQLBindings bindings = new SPARQLBindingsImpl();
  private GraphPermissions permissions;
  private String baseUri;
  private String[] defaultGraphUris;
  private String[] namedGraphUris;
  private String[] usingGraphUris;
  private String[] usingNamedUris;
  private SPARQLRuleset[] ruleSets;
  private Boolean includeDefaultRulesets;
  private QueryDefinition constrainingQuery;
  private String optionsName;
  private String[] collections;
  private String directory;
  private ServerTransform transform;
  private int optimizeLevel = -1;

  public SPARQLQueryDefinitionImpl() {}

  public SPARQLQueryDefinitionImpl(String sparql) {
    setSparql(sparql);
  }

  public SPARQLQueryDefinitionImpl(TextWriteHandle sparql) {
    setSparql(sparql);
  }

  @Override
  public String getOptionsName() {
    return optionsName;
  }

  @Override
  public void setOptionsName(String name) {
    this.optionsName = name;
  }

  @Override
  public String[] getCollections() {
    return collections;
  }

  @Override
  public void setCollections(String... collections) {
    this.collections = collections;
  }

  @Override
  public String getDirectory() {
    return directory;
  }

  @Override
  public void setDirectory(String directory) {
    this.directory = directory;
  }

  @Override
  public ServerTransform getResponseTransform() {
    return transform;
  }

  @Override
  public void setResponseTransform(ServerTransform transform) {
    this.transform = transform;
  }

  @Override
  public void setSparql(String sparql) {
    this.sparql = sparql;
  }

  @Override
  public void setSparql(TextWriteHandle sparql) {
    this.sparql = HandleAccessor.contentAsString(sparql);
  }

  @Override
  public String getSparql() {
    return sparql;
  }

  @Override
  public SPARQLQueryDefinition withSparql(String sparql) {
    setSparql(sparql);
    return this;
  }

  @Override
  public SPARQLQueryDefinition withSparql(TextWriteHandle sparql) {
    setSparql(sparql);
    return this;
  }

  @Override
  public void setBindings(SPARQLBindings bindings) {
    if ( bindings == null ) throw new IllegalArgumentException("bindings cannot be null");
    this.bindings = bindings;
  }

  @Override
  public SPARQLBindings getBindings() {
    return bindings;
  }

  @Override
  public SPARQLQueryDefinition withBinding(String name, String value) {
    bindings.bind(name, value);
    return this;
  }

  @Override
  public SPARQLQueryDefinition withBinding(String name, String value, RDFTypes type) {
    bindings.bind(name, value, type);
    return this;
  }

  @Override
  public SPARQLQueryDefinition withBinding(String name, String value, Locale languageTag) {
    bindings.bind(name, value, languageTag);
    return this;
  }

  @Override
  public SPARQLQueryDefinition clearBindings() {
    bindings = new SPARQLBindingsImpl();
    return this;
  }

  @Override
  public void setUpdatePermissions(GraphPermissions permissions) {
    this.permissions = permissions;
  }

  @Override
  public GraphPermissions getUpdatePermissions() {
    return permissions;
  }

  @Override
  public SPARQLQueryDefinition withUpdatePermission(String role,
                                                    Capability capability) {
    if ( permissions == null ) {
      permissions = new GraphPermissionsImpl().permission(role, capability);
    } else {
      permissions = permissions.permission(role, capability);
    }
    return this;
  }

  @Override
  public String getBaseUri() {
    return baseUri;
  }

  @Override
  public void setBaseUri(String uri) {
    this.baseUri = uri;
  }

  @Override
  public void setDefaultGraphUris(String... uris) {
    this.defaultGraphUris = uris;
  }

  @Override
  public void setNamedGraphUris(String... uris) {
    this.namedGraphUris = uris;
  }


  @Override
  public String[] getDefaultGraphUris() {
    return this.defaultGraphUris;
  }

  @Override
  public String[] getNamedGraphUris() {
    return this.namedGraphUris;
  }

  @Override
  public String[] getUsingGraphUris() {
    return this.usingGraphUris;
  }

  @Override
  public String[] getUsingNamedGraphUris() {
    return this.usingNamedUris;
  }

  @Override
  public void setUsingGraphUris(String... uris) {
    this.usingGraphUris = uris;
  }

  @Override
  public void setUsingNamedGraphUris(String... uris) {
    this.usingNamedUris = uris;
  }

  @Override
  public void setConstrainingQueryDefinition(QueryDefinition query) {
    if ( query instanceof RawQueryByExampleDefinition ) {
      throw new IllegalArgumentException("SPARQL queries cannot be constrained using " +
        "RawQueryByExampleDefinition");
    }
    this.constrainingQuery = query;
  }

  @Override
  public QueryDefinition getConstrainingQueryDefinition() {
    return this.constrainingQuery;
  }

  @Override
  public SPARQLQueryDefinition withConstrainingQuery(QueryDefinition query) {
    this.constrainingQuery = query;
    return this;
  }

  @Override
  public void setRulesets(SPARQLRuleset... ruleset) {
    this.ruleSets = ruleset;
  }

  @Override
  public SPARQLRuleset[] getRulesets() {
    return ruleSets;
  }

  @Override
  public SPARQLQueryDefinition withRuleset(SPARQLRuleset ruleset) {
    if (this.ruleSets == null) {
      this.ruleSets = new SPARQLRuleset[] { ruleset };
    }
    else {
      Arrays.asList(this.ruleSets).add(ruleset);
    }
    return this;
  }

  @Override
  public void setIncludeDefaultRulesets(Boolean b) {
    this.includeDefaultRulesets = b;
  }

  @Override
  public Boolean getIncludeDefaultRulesets() {
    return this.includeDefaultRulesets;
  }

  @Override
  public SPARQLQueryDefinition withIncludeDefaultRulesets(Boolean b) {
    this.includeDefaultRulesets = b;
    return this;
  }

  @Override
  public int getOptimizeLevel() {
    return optimizeLevel;
  }

  @Override
  public void setOptimizeLevel(int level) {
    this.optimizeLevel = level;
  }
}
