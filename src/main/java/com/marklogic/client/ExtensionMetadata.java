/*
 * Copyright 2012 MarkLogic Corporation
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
package com.marklogic.client;

public class ExtensionMetadata {
	private String title;
	private String description;
	private String provider;
	private String version;

	public ExtensionMetadata() {
		super();
	}

	public String getTitle() {
    	return title;
    }
    public void  setTitle(String title) {
    	this.title = title;
    }

    public String getDescription() {
    	return description;
    }
    public void  setDescription(String description) {
    	this.description = description;
    }

    public String getProvider() {
    	return provider;
    }
    public void  setProvider(String provider) {
    	this.provider = provider;
    }

    public String getVersion() {
    	return version;
    }
    public void  setVersion(String version) {
    	this.version = version;
    }

    public RequestParameters asParameters() {
    	RequestParameters params = new RequestParameters();
    	if (title != null)
    		params.put("title",       title);
    	if (description != null)
    		params.put("description", description);
    	if (provider != null)
    		params.put("provider",    provider);
    	if (version != null)
    		params.put("version",     version);
    	return params;
	}
}