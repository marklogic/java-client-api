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
package com.marklogic.client.example.cookbook;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.w3c.dom.Document;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.FailedRequestException;
import com.marklogic.client.ForbiddenUserException;
import com.marklogic.client.ResourceNotFoundException;
import com.marklogic.client.admin.ExtensionMetadata;
import com.marklogic.client.admin.MethodType;
import com.marklogic.client.admin.ResourceExtensionsManager;
import com.marklogic.client.admin.ResourceExtensionsManager.MethodParameters;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.example.cookbook.Util.ExampleProperties;
import com.marklogic.client.extensions.ResourceManager;
import com.marklogic.client.extensions.ResourceServices.ServiceResult;
import com.marklogic.client.extensions.ResourceServices.ServiceResultIterator;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.io.XMLStreamReaderHandle;
import com.marklogic.client.util.RequestParameters;

/**
 * JavascriptResourceExtension installs an extension for managing spelling dictionary resources.
 */
public class JavascriptResourceExtension {
	public static void main(String[] args)
	throws IOException, ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
		run(Util.loadProperties());
	}

	// install and then use the resource extension
	public static void run(ExampleProperties props)
	throws IOException, ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
		//System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.wire", "debug");

		System.out.println("example: "+JavascriptResourceExtension.class.getName());

		installResourceExtension(props.host, props.port,
				props.adminUser, props.adminPassword, props.authType);

		useResource(props.host, props.port,
				props.writerUser, props.writerPassword, props.authType);

		tearDownExample(props.host, props.port,
				props.adminUser, props.adminPassword, props.authType);
	}

	/**
	 * HelloWorld provides an example of a class that implements
	 * a resource extension client, exposing a method for each service.
	 * Typically, this class would be a top-level class.
	 */
	static public class HelloWorld extends ResourceManager {
		static final public String NAME = "helloWorld";
		static final public ExtensionMetadata.ScriptLanguage scriptLanguage 
		  = ExtensionMetadata.JAVASCRIPT;
		private XMLDocumentManager docMgr;

		public HelloWorld(DatabaseClient client) {
			super();

			// a Resource Manager must be initialized by a Database Client
			client.init(NAME, this);

			// the Dictionary Manager delegates some services to a document manager
			docMgr = client.newXMLDocumentManager();
		}

		public String sayHello() {
			RequestParameters params = new RequestParameters();
			params.add("service", "hello");
			params.add("planet", "Earth");

			// specify the mime type for each expected document returned
			String[] mimetypes = new String[] {"text/plain"};

			// call the service
			ServiceResultIterator resultItr = getServices().get(params, mimetypes);

			// iterate over the results
			List<String> responses = new ArrayList<String>();
			StringHandle readHandle = new StringHandle();
			while (resultItr.hasNext()) {
				ServiceResult result = resultItr.next();

				// get the result content
				result.getContent(readHandle);
				responses.add(readHandle.get());
			}

			// release the iterator resources
			resultItr.close();

			return responses.get(0);
		}

	}

	// install the resource extension on the server
	public static void installResourceExtension(String host, int port, String user, String password, Authentication authType) throws IOException {
		// create the client
		DatabaseClient client = DatabaseClientFactory.newClient(host, port, user, password, authType);

		// use either shortcut or strong typed IO
		installResourceExtension(client);

		// release the client
		client.release();
	}
	public static void installResourceExtension(DatabaseClient client) throws IOException {
		// create a manager for resource extensions
		ResourceExtensionsManager resourceMgr = client.newServerConfigManager().newResourceExtensionsManager();

		// specify metadata about the resource extension
		ExtensionMetadata metadata = new ExtensionMetadata();
		metadata.setTitle("Hello World Resource Services");
		metadata.setDescription("This resource extension is written in javascript");
		metadata.setProvider("MarkLogic");
		metadata.setVersion("0.1");
		metadata.setScriptLanguage(HelloWorld.scriptLanguage);

		// acquire the resource extension source code
		InputStream sourceStream = Util.openStream(
				"scripts"+File.separator+HelloWorld.NAME+".sjs");
		if (sourceStream == null)
			throw new IOException("Could not read example resource extension");

		// write the resource extension to the database
		resourceMgr.writeServicesAs(HelloWorld.NAME, sourceStream, metadata,
				new MethodParameters(MethodType.GET));

		System.out.println("(Shortcut) Installed the resource extension on the server");
	}

	// use the resource manager
	public static void useResource(String host, int port, String user, String password, Authentication authType)
	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
		// create the client
		DatabaseClient client = DatabaseClientFactory.newClient(host, port, user, password, authType);

		// create the resource extension client
		HelloWorld hello = new HelloWorld(client);

		String response = hello.sayHello();
		System.out.println("Called hello worlds service, got response:["+ response + "]");

		// release the client
		client.release();
	}

	// clean up by deleting the example resource extension
	public static void tearDownExample(
			String host, int port, String user, String password, Authentication authType) {
		DatabaseClient client = DatabaseClientFactory.newClient(host, port, user, password, authType);

		ResourceExtensionsManager resourceMgr = client.newServerConfigManager().newResourceExtensionsManager();

		resourceMgr.deleteServices(HelloWorld.NAME);

		client.release();
	}
}
