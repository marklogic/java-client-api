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
import com.marklogic.client.impl.HandleAccessor;
import com.marklogic.client.io.BaseHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.client.io.marker.AbstractWriteHandle;
import com.marklogic.client.io.marker.GenericReadHandle;
import com.marklogic.client.io.marker.GenericWriteHandle;
import com.marklogic.client.io.marker.TextReadHandle;
import com.marklogic.client.io.marker.XMLReadHandle;
import com.marklogic.client.util.RequestParameters;

/**
 * GraphManager is an extension that provides experimental graph CRUD functionality to the Java API.
 * Please note that future graph support will NOT use this code. 
 */
public class GraphManager extends ResourceManager {
	final static public String NAME = "graph";

	enum GraphFormat {
		HTML, JSON, NQUAD, TURTLE, XML, NTRIPLE;
		@Override
		public String toString() {
			switch(this) {
			case JSON:
				return "rdfjson";
			case XML:
				return "rdfxml";
			}
			return super.toString().toLowerCase();
		}
	}

	public GraphManager(DatabaseClient client) {
		super();
		client.init(NAME, this);
	}

	public <T extends TextReadHandle> T list(T response) {
		return getServices().get(null, response);
	}

	/**
	 * Deletes the deafult graph
	 */
	public void delete() {
		RequestParameters params = new RequestParameters();
		params.add("default", "true");
		getServices().delete(params, null);
	}

	/**
	 * Deletes a named graph.
	 * @param graphUri The URI of the graph to delete.
	 */
	public void delete(String graphUri) {
		RequestParameters params = new RequestParameters();
		params.add("graph", graphUri);
		getServices().delete(params, null);
	}

	/**
	 * Reads the default RDF graph from the server
	 * @param format The format in which to serialize the graph.
	 * @param response a templated parameter to hold the response type.
	 * @return THe serialized graph.
	 */
	public <T extends GenericReadHandle> T read(GraphFormat format, T response) {
		RequestParameters params = new RequestParameters();
		params.add("default", "true");
		params.add("format", format.toString());
		return getServices().get(params, response);
	}

	/**
	 * Reads a named graph from the server
	 * @param graphUri URI of the graph to retrieve.
	 * @param format The format in which to serialize the graph.
	 * @param response a templated parameter to hold the response type.
	 * @return The serialized graph.
	 */
	public <T extends XMLReadHandle> T read(String graphUri,
			GraphFormat format, T response) {
		RequestParameters params = new RequestParameters();
		params.add("graph", graphUri);
		params.add("format", format.toString());
		return getServices().get(params, response);
	}

	/**
	 * Appends a graph to the default graph on the REST server
	 * @param format Format of the graph serialization
	 * @param graph The graph to write.
	 */
	@SuppressWarnings("rawtypes")
	public void insert(GraphFormat format, GenericWriteHandle graph) {
		HandleAccessor.checkHandle(graph, "graph");
		
		if (format == GraphFormat.HTML || format == GraphFormat.XML) {
			((BaseHandle) graph).setFormat(Format.XML);
		} else {
			((BaseHandle) graph).setFormat(Format.TEXT);
		}

		RequestParameters params = new RequestParameters();
		params.add("default", "true");
		params.add("format", format.toString());

		getServices().post(params, (AbstractWriteHandle) graph);
	}

	/**
	 * Appends a named graph to the REST server, or creates it if that name doesn't yet exist on the graph store.
	 * @param graphUri The graph URI
	 * @param format the serialization format of the graph payload
	 * @param graph The graph to write.
	 */
	@SuppressWarnings("rawtypes")
	public void insert(String graphUri, GraphFormat format, GenericWriteHandle graph) {
		HandleAccessor.checkHandle(graph, "graph");
		
		if (format == GraphFormat.HTML || format == GraphFormat.XML) {
			((BaseHandle) graph).setFormat(Format.XML);
		} else {
			((BaseHandle) graph).setFormat(Format.TEXT);
		}

		RequestParameters params = new RequestParameters();
		params.add("graph", graphUri);
		params.add("format", format.toString());

		getServices().post(params, (AbstractWriteHandle) graph);
	}

	/**
	 * Replaces the default graph on the REST server/
	 * @param format the serialization format of the graph payload
	 * @param graph The graph to write.
	 */
	@SuppressWarnings("rawtypes")
	public void replace(GraphFormat format, GenericWriteHandle graph) {
		HandleAccessor.checkHandle(graph, "graph");
		
		if (format == GraphFormat.HTML || format == GraphFormat.XML) {
			((BaseHandle) graph).setFormat(Format.XML);
		} else {
			((BaseHandle) graph).setFormat(Format.TEXT);
		}

		RequestParameters params = new RequestParameters();
		params.add("default", "true");
		params.add("format", format.toString());

		getServices().put(params, (AbstractWriteHandle) graph, null);
	}

	/**
	 * Replaces a named graph on the REST server
	 * @param graphUri The graph URI
	 * @param format the serialization format of the graph payload
	 * @param graph The graph to write.
	 */
	@SuppressWarnings("rawtypes")
	public void replace(String graphUri, GraphFormat format,
			GenericWriteHandle graph) {
		HandleAccessor.checkHandle(graph, "graph");
		
		if (format == GraphFormat.HTML || format == GraphFormat.XML) {
			((BaseHandle) graph).setFormat(Format.XML);
		} else {
			((BaseHandle) graph).setFormat(Format.TEXT);
		}

		RequestParameters params = new RequestParameters();
		params.add("graph", graphUri);
		params.add("format", format.toString());

		getServices().put(params, (AbstractWriteHandle) graph, null);
	}

	/**
	 * Installs this extension on the REST server
	 * @param client the database client connection.
	 * @throws IOException Thrown in case of an IO problem.
	 */
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
		get.add("format", "xs:string");

		MethodParameters post = new MethodParameters(MethodType.POST);
		post.add("graph", "xs:string");
		post.add("format", "xs:string");

		MethodParameters put = new MethodParameters(MethodType.PUT);
		put.add("graph", "xs:string");
		put.add("format", "xs:string");

		resourceMgr.writeServices(NAME, handle, metadata, delete, get, post,
				put);
	}

	/**
	 * Uninstalls this extension from the REST server
	 * @param client the database client connection.
	 * @throws IOException Thrown in the case of some IO issue.
	 */
	public static void uninstall(DatabaseClient client) throws IOException {
		ResourceExtensionsManager resourceMgr = client.newServerConfigManager()
				.newResourceExtensionsManager();

		resourceMgr.deleteServices(NAME);
	}
}
