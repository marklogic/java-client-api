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
package com.marklogic.client.impl;

import java.util.Arrays;
import java.util.Locale;

import com.marklogic.client.document.ServerTransform;
import com.marklogic.client.impl.SPARQLBindingsImpl;
import com.marklogic.client.io.marker.TextWriteHandle;
import com.marklogic.client.query.QueryDefinition;
import com.marklogic.client.semantics.Capability;
import com.marklogic.client.semantics.GraphPermissions;
import com.marklogic.client.semantics.SPARQLBindings;
import com.marklogic.client.semantics.SPARQLQueryDefinition;
import com.marklogic.client.semantics.SPARQLRuleset;

public class SPARQLQueryDefinitionImpl implements SPARQLQueryDefinition {

    private String sparql;
    private SPARQLBindings bindings = new SPARQLBindingsImpl();
    private String[] defaultGraphUris;
    private String[] namedGraphUris;
    private String[] usingGraphUris;
    private String[] usingNamedUris;
    private SPARQLRuleset[] ruleSets;
	private Boolean includeDefaultRulesets;

    public SPARQLQueryDefinitionImpl(String sparql) {
        setSparql(sparql);
    }

    public SPARQLQueryDefinitionImpl(TextWriteHandle sparql) {
        setSparql(sparql);
    }

    @Override
    public String getOptionsName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setOptionsName(String name) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public String[] getCollections() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setCollections(String... collections) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public String getDirectory() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setDirectory(String directory) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public ServerTransform getResponseTransform() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setResponseTransform(ServerTransform transform) {
        // TODO Auto-generated method stub
        
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
    public SPARQLQueryDefinition withBinding(String name, String value, String type) {
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
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setUpdatePermissions(GraphPermissions permissions) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public GraphPermissions getUpdatePermissions() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SPARQLQueryDefinition withUpdatePermission(String role,
            Capability capability) {
        // TODO Auto-generated method stub
        return this;
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
    public void setConstrainingQueryDefinintion(QueryDefinition query) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public QueryDefinition getConstrainingQueryDefinintion() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SPARQLQueryDefinition withConstrainingQuery(QueryDefinition query) {
        // TODO Auto-generated method stub
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
    public SPARQLQueryDefinition withStructuredQuery(
            QueryDefinition structuredQuery) {
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
		return null;
	}

}
