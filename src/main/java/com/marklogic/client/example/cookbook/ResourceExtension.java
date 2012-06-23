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
package com.marklogic.client.example.cookbook;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.w3c.dom.Document;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.ExtensionMetadata;
import com.marklogic.client.MethodType;
import com.marklogic.client.RequestParameters;
import com.marklogic.client.ResourceExtensionsManager;
import com.marklogic.client.ResourceExtensionsManager.MethodParameters;
import com.marklogic.client.ResourceManager;
import com.marklogic.client.XMLDocumentManager;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.io.XMLStreamReaderHandle;

/**
 * ResourceExtension installs an extension for managing spelling dictionary resources.
 */
public class ResourceExtension {
	public static void main(String[] args) throws IOException {
		Properties props = loadProperties();

		// connection parameters for writer and admin users
		String         host            = props.getProperty("example.host");
		int            port            = Integer.parseInt(props.getProperty("example.port"));
		String         writer_user     = props.getProperty("example.writer_user");
		String         writer_password = props.getProperty("example.writer_password");
		String         admin_user      = props.getProperty("example.admin_user");
		String         admin_password  = props.getProperty("example.admin_password");
		Authentication authType        = Authentication.valueOf(
				props.getProperty("example.authentication_type").toUpperCase()
				);

		run(host, port, admin_user, admin_password, writer_user, writer_password, authType);
	}

	// install and then use the resource extension
	public static void run(String host, int port, String admin_user, String admin_password, String writer_user, String writer_password, Authentication authType) {
		System.out.println("example: "+ResourceExtension.class.getName());

		installResourceExtension(host, port, admin_user,  admin_password,  authType);

		useResource(host, port, writer_user, writer_password, authType);

		tearDownExample(host, port, admin_user, admin_password, authType);
	}

	// implement a resource extension client with a method for each service;
	//     more typically, this class would be a top-level class
	static public class DictionaryManager extends ResourceManager {
		static final public String NAME = "dictionary";
		private XMLDocumentManager docMgr;

		public DictionaryManager(DatabaseClient client) {
			super();

			// a Resource Manager must be initialized by a Database Client
			client.init(NAME, this);

			// the Dictionary Manager delegates some services to a document manager
			docMgr = client.newXMLDocumentManager();
		}

		public void createDictionary(String uri, String[] words) {
			StringBuilder builder = new StringBuilder();
			builder.append("<?xml version='1.0' encoding='UTF-8'?>\n");
			builder.append("<dictionary xmlns='http://marklogic.com/xdmp/spell'>\n");
			for (String word: words) {
				builder.append("<word>");
				builder.append(word);
				builder.append("</word>\n");
			}
			builder.append("</dictionary>\n");

			StringHandle writeHandle = new StringHandle();
			writeHandle.set(builder.toString());

			// delegate
			docMgr.write(uri,writeHandle);
		}
		public Document[] checkDictionaries(String... uris) {
			RequestParameters params = new RequestParameters();
			params.add("service", "check-dictionary");
			params.add("uris",    uris);

			DOMHandle[] readHandles = new DOMHandle[uris.length];

			// call the service
			getServices().get(params, readHandles);

			Document[] documents = new Document[readHandles.length];
			for (int i=0; i < readHandles.length; i++) {
				documents[i] = readHandles[i].get();
			}

			return documents;
		}
		public boolean isCorrect(String word, String... uris) {
			try {
				RequestParameters params = new RequestParameters();
				params.add("service", "is-correct");
				params.add("word",    word);
				params.add("uris",    uris);

				XMLStreamReaderHandle readHandle = new XMLStreamReaderHandle();

				// call the service
				getServices().get(params, readHandle);

				QName correctName = new QName(XMLConstants.DEFAULT_NS_PREFIX, "correct");

				XMLStreamReader streamReader = readHandle.get();
				while (streamReader.hasNext()) {
					int current = streamReader.next();
					if (current == XMLStreamReader.START_ELEMENT) {
						if (correctName.equals(streamReader.getName())) {
							return "true".equals(streamReader.getElementText());
						}
					}
				}

				return false;
			} catch(XMLStreamException ex) {
				throw new RuntimeException(ex);
			}
		}
		public String[] suggest(String word, Integer maximum, Integer distanceThreshold, String... uris) {
			try {
				RequestParameters params = new RequestParameters();
				params.add("service", "suggest-detailed");
				params.add("word",    word);
				params.add("uris",    uris);
				if (maximum != null)
					params.add("maximum", String.valueOf(maximum));
				if (distanceThreshold != null)
					params.add("distance-threshold", String.valueOf(distanceThreshold));

				XMLStreamReaderHandle readHandle = new XMLStreamReaderHandle();

				// call the service
				getServices().get(params, readHandle);

				XMLStreamReader streamReader = readHandle.get();

				QName wordName = new QName("http://marklogic.com/xdmp/spell", "word");

				ArrayList<String> words  = new ArrayList<String>();

				while (streamReader.hasNext()) {
					int current = streamReader.next();
					if (current == XMLStreamReader.START_ELEMENT) {
						if (wordName.equals(streamReader.getName())) {
							words.add(streamReader.getElementText());
						}
					}
				}

				return words.toArray(new String[words.size()]);
			} catch(XMLStreamException ex) {
				throw new RuntimeException(ex);
			}
		}
		public void deleteDictionary(String uri) {
			// delegate
			docMgr.delete(uri);
		}
	}

	// install the resource extension on the server
	public static void installResourceExtension(String host, int port, String user, String password, Authentication authType) {
		// create the client
		DatabaseClient client = DatabaseClientFactory.newClient(host, port, user, password, authType);

		// create a manager for resource extensions
		ResourceExtensionsManager resourceMgr = client.newServerConfigManager().newResourceExtensionsManager();

		// specify metadata about the resource extension
		ExtensionMetadata metadata = new ExtensionMetadata();
		metadata.setTitle("Spelling Dictionary Resource Services");
		metadata.setDescription("This plugin supports spelling dictionaries");
		metadata.setProvider("MarkLogic");
		metadata.setVersion("0.1");

		// acquire the resource extension source code
		InputStream sourceStream = DocumentWrite.class.getClassLoader().getResourceAsStream(
			"scripts"+File.separator+DictionaryManager.NAME+".xqy");
		if (sourceStream == null)
			throw new RuntimeException("Could not read example resource extension");

		// create a handle on the extension source code
		InputStreamHandle handle = new InputStreamHandle(sourceStream);
		handle.set(sourceStream);

		// write the resource extension to the database
		resourceMgr.writeServices(DictionaryManager.NAME, handle, metadata,
				new MethodParameters(MethodType.GET));

		System.out.println("Installed the resource extension on the server");

		// release the client
		client.release();
	}

	// use the resource manager
	public static void useResource(String host, int port, String user, String password, Authentication authType) {
		// create the client
		DatabaseClient client = DatabaseClientFactory.newClient(host, port, user, password, authType);

		// create the resource extension client
		DictionaryManager dictionaryMgr = new DictionaryManager(client);

		// specify the identifier for the dictionary
		String uri = "/example/metasyn.xml";

		// create the dictionary
		String[] words = {
			"foo", "bar", "baz", "qux", "quux", "wibble", "wobble", "wubble"
		};
		dictionaryMgr.createDictionary(uri, words);

		System.out.println("Created a dictionary on the server at "+uri);

		String word = "biz";

		// use a resource service to check the correctness of a word
		if (!dictionaryMgr.isCorrect(word, uri)) {
			System.out.println("Confirmed that '"+word+"' is not in the dictionary at "+uri);

			// use a resource service to look up suggestions
			String[] suggestions = dictionaryMgr.suggest(word, null, null, uri);

			System.out.println("Nearest matches for '"+word+"' in the dictionary at "+uri);
			for (String suggestion: suggestions) {
				System.out.println("    "+suggestion);
			}
		}

		// delete the dictionary
		dictionaryMgr.deleteDictionary(uri);

		// release the client
		client.release();
	}

	// clean up by deleting the example resource extension
	public static void tearDownExample(
			String host, int port, String user, String password, Authentication authType) {
		DatabaseClient client = DatabaseClientFactory.newClient(host, port, user, password, authType);

		ResourceExtensionsManager resourceMgr = client.newServerConfigManager().newResourceExtensionsManager();

		resourceMgr.deleteServices(DictionaryManager.NAME);

		client.release();
	}

	// get the configuration for the example
	public static Properties loadProperties() throws IOException {
		String propsName = "Example.properties";
		InputStream propsStream =
			DocumentWriteTransform.class.getClassLoader().getResourceAsStream(propsName);
		if (propsStream == null)
			throw new RuntimeException("Could not read example properties");

		Properties props = new Properties();
		props.load(propsStream);

		return props;
	}
}
