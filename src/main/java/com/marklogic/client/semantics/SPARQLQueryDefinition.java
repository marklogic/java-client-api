/*
 * Copyright 2012-2015 MarkLogic Corporation
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
package com.marklogic.client.semantics;

import java.util.Locale;

import com.marklogic.client.query.QueryDefinition;
import com.marklogic.client.semantics.Capability;

/**
 * Represents a SPARQL query.
 * For details about semantics in MarkLogic see
 * {@link https://docs.marklogic.com/guide/semantics Semantics Developer's Guide}
 */
public interface SPARQLQueryDefinition extends QueryDefinition {
    public void setSparql(String sparql);
    public String getSparql();
    public SPARQLQueryDefinition withSparql(String sparql);

    public void setBindings(SPARQLBindings bindings);
    public SPARQLBindings getBindings();
    public SPARQLQueryDefinition withBinding(String name, String value);
    public SPARQLQueryDefinition withBinding(String name, String value, String type);
    public SPARQLQueryDefinition withBinding(String name, String value, Locale languageTag);
    public SPARQLQueryDefinition clearBindings();

    public void setUpdatePermissions(GraphPermissions permissions);
    public GraphPermissions getUpdatePermissions();
    public SPARQLQueryDefinition withUpdatePermission(String role, Capability capability);

    public String[] getDefaultGraphUris();
    public String[] getNamedGraphUris();
    public String[] getUsingGraphUris();
    public String[] getUsingNamedGraphUris();
    public void setDefaultGraphUris(String... uris);
    public void setNamedGraphUris(String... uris);
    public void setUsingGraphUris(String... uris);
    public void setUsingNamedGraphUris(String... uris);

    public void setConstrainingQueryDefinintion(QueryDefinition query);
    public QueryDefinition getConstrainingQueryDefinintion();
    public SPARQLQueryDefinition withConstrainingQuery(QueryDefinition query);

    public void setRulesets(SPARQLRuleset... ruleset);
    public SPARQLRuleset[] getRulesets();
    public SPARQLQueryDefinition withRuleset(SPARQLRuleset ruleset);
    public SPARQLQueryDefinition withStructuredQuery(QueryDefinition structuredQuery);
    
}
