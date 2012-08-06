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
package com.marklogic.client.example.batch;

import org.w3c.dom.Element;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.FailedRequestException;
import com.marklogic.client.extensions.ResourceManager;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.marker.XMLWriteHandle;
import com.marklogic.client.util.RequestParameters;

/**
 * DocumentSplitter provides an extension for splitting an input XML document
 * into multiple documents.  The root element for each split document must have
 * a "uri" attribute in the "http://marklogic.com/rest-api" namespace declaring
 * the URI for the document.
 */
public class DocumentSplitter extends ResourceManager {
	static final public String NAME = "docsplit";

	public DocumentSplitter(DatabaseClient client) {
		super();
		client.init(NAME, this);
	}

	public int split(XMLWriteHandle inputHandle) {
		if (inputHandle == null)
			throw new IllegalArgumentException("No input handle");

		DOMHandle errorHandle = new DOMHandle();
		getServices().post(
				new RequestParameters(), inputHandle, errorHandle
				);

		Element result = errorHandle.get().getDocumentElement();
		if (!"split-docs".equals(result.getLocalName()))
			throw new FailedRequestException(errorHandle.toString());

		return Integer.parseInt(result.getTextContent());
	}
}
