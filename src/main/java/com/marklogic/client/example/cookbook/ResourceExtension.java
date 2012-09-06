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
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.w3c.dom.Document;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
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
 * ResourceExtension installs an extension for managing spelling dictionary resources.
 */
public class ResourceExtension {
	public static void main(String[] args) throws IOException {
		run(Util.loadProperties());
	}

	// install and then use the resource extension
	public static void run(ExampleProperties props) throws IOException {
		System.out.println("example: "+ResourceExtension.class.getName());

		installResourceExtension(props.host, props.port,
				props.adminUser, props.adminPassword, props.authType);

		useResource(props.host, props.port,
				props.writerUser, props.writerPassword, props.authType);

		tearDownExample(props.host, props.port,
				props.adminUser, props.adminPassword, props.authType);
	}

	/**
	 * DictionaryManager provides an example of a class that implements
	 * a resource extension client, exposing a method for each service.
	 * Typically, this class would be a top-level class.
	 */
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

			// specify the mime type for each expected document returned
			String[] mimetypes = new String[uris.length];
			for (int i=0; i < uris.length; i++) {
				mimetypes[i] = "application/xml";
			}

			// call the service
			ServiceResultIterator resultItr = getServices().get(params, mimetypes);

			// iterate over the results
			List<Document> documents = new ArrayList<Document>();
			DOMHandle readHandle = new DOMHandle();
			while (resultItr.hasNext()) {
				ServiceResult result = resultItr.next();

				// get the result content
				result.getContent(readHandle);
				documents.add(readHandle.get());
			}

			// release the iterator resources
			resultItr.close();

			return documents.toArray(new Document[documents.size()]);
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
	public static void installResourceExtension(String host, int port, String user, String password, Authentication authType) throws IOException {
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
		InputStream sourceStream = Util.openStream(
				"scripts"+File.separator+DictionaryManager.NAME+".xqy");
		if (sourceStream == null)
			throw new IOException("Could not read example resource extension");

		// create a handle on the extension source code
		InputStreamHandle handle = new InputStreamHandle();
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

		// check the validity of the dictionary
		Document[] list = dictionaryMgr.checkDictionaries(uri);
		if (list == null || list.length == 0)
			System.out.println("Could not check the validity of the dictionary at "+uri);
		else
			System.out.println(
				"Checked the validity of the dictionary at "+uri+": "+
				!"invalid".equals(list[0].getDocumentElement().getNodeName())
				);

		// use a resource service to check the correctness of a word
		String word = "biz";
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
}
