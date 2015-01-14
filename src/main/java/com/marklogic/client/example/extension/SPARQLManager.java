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
package com.marklogic.client.example.extension;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.admin.ExtensionMetadata;
import com.marklogic.client.admin.MethodType;
import com.marklogic.client.admin.ResourceExtensionsManager;
import com.marklogic.client.admin.ResourceExtensionsManager.MethodParameters;
import com.marklogic.client.example.cookbook.Util;
import com.marklogic.client.extensions.ResourceManager;
import com.marklogic.client.io.BaseHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.client.io.marker.XMLReadHandle;
import com.marklogic.client.io.marker.XMLWriteHandle;
import com.marklogic.client.util.RequestParameters;

public class SPARQLManager extends ResourceManager {
	final static public String NAME = "sparql";

	enum QueryFormat {
		HTML, JSON, NQUAD, TURTLE, XML;
	}

	public SPARQLManager(DatabaseClient client) {
		super();
		client.init(NAME, this);
	}

	public <T extends XMLReadHandle> T search(XMLWriteHandle query,
			QueryFormat format, T response) {
		return search(null, null, query, format, response);
	}

	public <T extends XMLReadHandle> T search(String defaultGraphURI,
			XMLWriteHandle query, QueryFormat format, T response) {
		return search(defaultGraphURI, null, query, format, response);
	}

	public <T extends XMLReadHandle> T search(String[] namedGraphURI,
			XMLWriteHandle query, QueryFormat format, T response) {
		return search(null, namedGraphURI, query, format, response);
	}

	@SuppressWarnings("rawtypes")
	public <T extends XMLReadHandle> T search(String defaultGraphURI,
			String[] namedGraphURI, XMLWriteHandle query, QueryFormat format,
			T response) {
		// TODO: check derivation from BaseHandle
		if (format == QueryFormat.HTML || format == QueryFormat.XML) {
			((BaseHandle) query).setFormat(Format.XML);
		} else {
			((BaseHandle) query).setFormat(Format.TEXT);
		}

		RequestParameters params = new RequestParameters();
		if (defaultGraphURI != null)
			params.add("default-graph-uri", defaultGraphURI);
		if (namedGraphURI != null && namedGraphURI.length > 0)
			params.add("named-graph-uri", namedGraphURI);
		if (format != null)
			params.add("format", format.toString().toLowerCase());

		return getServices().post(params, query, response);
	}

	public static void install(DatabaseClient client) throws IOException {
		ResourceExtensionsManager resourceMgr = client.newServerConfigManager()
				.newResourceExtensionsManager();

		ExtensionMetadata metadata = new ExtensionMetadata();
		metadata.setTitle("SPARQL Resource Services");
		metadata.setDescription("This plugin supports SPARQL operations");
		metadata.setProvider("MarkLogic");
		metadata.setVersion("0.1");

		InputStream sourceStream = Util.openStream("scripts" + File.separator
				+ "sparql-service.xqy");
		if (sourceStream == null)
			throw new RuntimeException(
					"Could not read sparql service resource extension");

		InputStreamHandle handle = new InputStreamHandle(sourceStream);

		MethodParameters post = new MethodParameters(MethodType.POST);
		post.add("default-graph-uri", "xs:string");
		post.add("named-graph-uri", "xs:string");
		post.add("format", "xs:string");

		resourceMgr.writeServices(NAME, handle, metadata, post);
	}

	public static void uninstall(DatabaseClient client) throws IOException {
		ResourceExtensionsManager resourceMgr = client.newServerConfigManager()
				.newResourceExtensionsManager();

		resourceMgr.deleteServices(NAME);
	}
}
