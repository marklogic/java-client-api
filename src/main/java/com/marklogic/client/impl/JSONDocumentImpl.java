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
package com.marklogic.client.impl;

import java.util.HashMap;
import java.util.Map;

import com.marklogic.client.Format;
import com.marklogic.client.JSONDocumentManager;
import com.marklogic.client.io.marker.JSONReadHandle;
import com.marklogic.client.io.marker.JSONWriteHandle;

public class JSONDocumentImpl
    extends AbstractDocumentImpl<JSONReadHandle, JSONWriteHandle> 
    implements JSONDocumentManager
{
	private String language;

	JSONDocumentImpl(RESTServices services) {
		super(services,Format.JSON);
	}

	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}

	protected Map<String,String> getWriteParams() {
		if (language == null)
			return null;

		HashMap<String,String> params = new HashMap<String,String>();
		params.put("lang", language);

		return params;
	}
}
