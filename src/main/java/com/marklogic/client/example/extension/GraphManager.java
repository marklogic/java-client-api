/*
 * Copyright 2012-2013 MarkLogic Corporation
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
import com.marklogic.client.impl.HandleAccessor;
import com.marklogic.client.impl.HandleImplementation;
import com.marklogic.client.io.BaseHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.client.io.marker.AbstractWriteHandle;
import com.marklogic.client.io.marker.TextReadHandle;
import com.marklogic.client.io.marker.XMLReadHandle;
import com.marklogic.client.io.marker.XMLWriteHandle;
import com.marklogic.client.util.RequestParameters;

public class GraphManager extends ResourceManager {
	final static public String NAME = "graph";

	enum GraphFormat {
		HTML, JSON, NQUAD, TURTLE, XML, NTRIPLE;
	}

	public GraphManager(DatabaseClient client) {
		super();
		client.init(NAME, this);
	}

	// function that installs XQuery module
	// use data/foaf1.nt

	public <T extends TextReadHandle> T list(T response) {
		return getServices().get(null, response);
	}

	public void delete() {
		RequestParameters params = new RequestParameters();
		params.add("default", "true");
		getServices().delete(params, null);
	}

	public void delete(String graphUri) {
		RequestParameters params = new RequestParameters();
		params.add("graph", graphUri);
		getServices().delete(params, null);
	}

	public <T extends XMLReadHandle> T read(GraphFormat format, T response) {
		RequestParameters params = new RequestParameters();
		params.add("default", "true");
		params.add("output-format", format.toString().toLowerCase());
		return getServices().get(params, response);
	}

	public <T extends XMLReadHandle> T read(String graphUri,
			GraphFormat format, T response) {
		RequestParameters params = new RequestParameters();
		params.add("graph", graphUri);
		params.add("output-format", format.toString().toLowerCase());
		return getServices().get(params, response);
	}

	public void insert(GraphFormat format, XMLWriteHandle graph) {
		HandleImplementation baseHandle = HandleAccessor.checkHandle(graph, "graph");
		
		if (format == GraphFormat.HTML || format == GraphFormat.XML) {
			((BaseHandle) graph).setFormat(Format.XML);
		} else {
			((BaseHandle) graph).setFormat(Format.TEXT);
		}

		RequestParameters params = new RequestParameters();
		params.add("default", "true");
		params.add("input-format", format.toString().toLowerCase());

		getServices().post(params, (AbstractWriteHandle) graph);
	}

	public void insert(String graphUri, GraphFormat format, XMLWriteHandle graph) {
		HandleImplementation baseHandle = HandleAccessor.checkHandle(graph, "graph");
		
		if (format == GraphFormat.HTML || format == GraphFormat.XML) {
			((BaseHandle) graph).setFormat(Format.XML);
		} else {
			((BaseHandle) graph).setFormat(Format.TEXT);
		}

		RequestParameters params = new RequestParameters();
		params.add("graph", graphUri);
		params.add("input-format", format.toString().toLowerCase());

		getServices().post(params, (AbstractWriteHandle) graph);
	}

	public void replace(GraphFormat format, XMLWriteHandle graph) {
		HandleImplementation baseHandle = HandleAccessor.checkHandle(graph, "graph");
		
		if (format == GraphFormat.HTML || format == GraphFormat.XML) {
			((BaseHandle) graph).setFormat(Format.XML);
		} else {
			((BaseHandle) graph).setFormat(Format.TEXT);
		}

		RequestParameters params = new RequestParameters();
		params.add("default", "true");
		params.add("input-format", format.toString().toLowerCase());

		getServices().put(params, (AbstractWriteHandle) graph, null);
	}

	public void replace(String graphUri, GraphFormat format,
			XMLWriteHandle graph) {
		HandleImplementation baseHandle = HandleAccessor.checkHandle(graph, "graph");
		
		if (format == GraphFormat.HTML || format == GraphFormat.XML) {
			((BaseHandle) graph).setFormat(Format.XML);
		} else {
			((BaseHandle) graph).setFormat(Format.TEXT);
		}

		RequestParameters params = new RequestParameters();
		params.add("graph", graphUri);
		params.add("input-format", format.toString().toLowerCase());

		getServices().put(params, (AbstractWriteHandle) graph, null);
	}

	public static void install(DatabaseClient client) throws IOException {
		ResourceExtensionsManager resourceMgr = client.newServerConfigManager()
				.newResourceExtensionsManager();

		ExtensionMetadata metadata = new ExtensionMetadata();
		metadata.setTitle("Graph Resource Services");
		metadata.setDescription("This plugin supports graph operations");
		metadata.setProvider("MarkLogic");
		metadata.setVersion("0.1");

		InputStream sourceStream = Util.openStream("scripts" + File.separator
				+ "graph-service.xqy");
		if (sourceStream == null)
			throw new RuntimeException(
					"Could not read graph service resource extension");

		InputStreamHandle handle = new InputStreamHandle(sourceStream);

		MethodParameters delete = new MethodParameters(MethodType.DELETE);
		delete.add("graph", "xs:string");

		MethodParameters get = new MethodParameters(MethodType.GET);
		get.add("graph", "xs:string");
		get.add("output-format", "xs:string");

		MethodParameters post = new MethodParameters(MethodType.POST);
		post.add("graph", "xs:string");
		post.add("input-format", "xs:string");

		MethodParameters put = new MethodParameters(MethodType.PUT);
		put.add("graph", "xs:string");
		put.add("input-format", "xs:string");

		resourceMgr.writeServices(NAME, handle, metadata, delete, get, post,
				put);
	}

	public static void uninstall(DatabaseClient client) throws IOException {
		ResourceExtensionsManager resourceMgr = client.newServerConfigManager()
				.newResourceExtensionsManager();

		resourceMgr.deleteServices(NAME);
	}
}
