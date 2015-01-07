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

import com.marklogic.client.io.Format;
import com.marklogic.client.bitemporal.TemporalDocumentManager;
import com.marklogic.client.document.DocumentPatchBuilder;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.util.RequestParameters;
import com.marklogic.client.io.marker.JSONReadHandle;
import com.marklogic.client.io.marker.JSONWriteHandle;

public class JSONDocumentImpl
    extends DocumentManagerImpl<JSONReadHandle, JSONWriteHandle> 
    implements JSONDocumentManager
{
	private String language;

	JSONDocumentImpl(RESTServices services) {
		super(services,Format.JSON);
	}

	@Override
    public DocumentPatchBuilder newPatchBuilder() {
    	return new DocumentPatchBuilderImpl(Format.JSON);
    }

	@Override
	public String getLanguage() {
		return language;
	}
	@Override
	public void setLanguage(String language) {
		this.language = language;
	}

	protected RequestParameters getWriteParams() {
		if (language == null)
			return null;

		RequestParameters params = new RequestParameters();
		params.put("lang", language);

		return params;
	}
}
