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

import java.util.HashSet;

import com.marklogic.client.document.ServerTransform;
import com.marklogic.client.query.QueryDefinition;

public abstract class AbstractQueryDefinition implements QueryDefinition {
    protected String optionsUri = null;
    private HashSet<String> collections = new HashSet<String>();
    private String          directory   = null;
	private ServerTransform transform   = null;

    @Override
    public String getOptionsName() {
        return optionsUri;
    }
    @Override
    public void setOptionsName(String uri) {
        optionsUri = uri;
    }

    @Override
    public String[] getCollections() {
        return collections.toArray(new String[0]);
    }
    @Override
    public void setCollections(String... collections) {
        this.collections.clear();

        for (String collection : collections) {
            this.collections.add(collection);
        }
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
}
