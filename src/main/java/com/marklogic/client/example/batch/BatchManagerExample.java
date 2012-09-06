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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.admin.ExtensionMetadata;
import com.marklogic.client.admin.MethodType;
import com.marklogic.client.admin.ResourceExtensionsManager;
import com.marklogic.client.admin.ResourceExtensionsManager.MethodParameters;
import com.marklogic.client.document.DocumentManager.Metadata;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.example.cookbook.Util;
import com.marklogic.client.example.cookbook.Util.ExampleProperties;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.client.io.StringHandle;

/**
 * BatchManagerExample illustrates executing a batch of document read, write,
 * or delete requests using the BatchManager example of a Resource Extension.
 */
public class BatchManagerExample {
	public static void main(String[] args) throws IOException {
		run(Util.loadProperties());
	}

	// install and then use the resource extension
	public static void run(ExampleProperties props) throws IOException {
		System.out.println("example: "+BatchManagerExample.class.getName());

		installResourceExtension(props.host, props.port,
				props.adminUser, props.adminPassword, props.authType);

		useResource(props.host, props.port,
				props.writerUser, props.writerPassword, props.authType);

		tearDownExample(props.host, props.port,
				props.adminUser, props.adminPassword, props.authType);
	}

	// install the resource extension on the server
	public static void installResourceExtension(String host, int port, String user, String password, Authentication authType)
	throws IOException {
		// create the client
		DatabaseClient client = DatabaseClientFactory.newClient(host, port, user, password, authType);

		// create a manager for resource extensions
		ResourceExtensionsManager resourceMgr = client.newServerConfigManager().newResourceExtensionsManager();

		// specify metadata about the resource extension
		ExtensionMetadata metadata = new ExtensionMetadata();
		metadata.setTitle("Document Batch Resource Services");
		metadata.setDescription("This plugin supports batch processing for documents");
		metadata.setProvider("MarkLogic");
		metadata.setVersion("0.1");

		// acquire the resource extension source code
		InputStream sourceStream = Util.openStream(
			"scripts"+File.separator+BatchManager.NAME+".xqy");
		if (sourceStream == null)
			throw new RuntimeException("Could not read example resource extension");

		// create a handle on the extension source code
		InputStreamHandle handle = new InputStreamHandle(sourceStream);
		handle.set(sourceStream);

		// write the resource extension to the database
		resourceMgr.writeServices(BatchManager.NAME, handle, metadata,
				new MethodParameters(MethodType.POST));

		System.out.println("Installed the resource extension on the server");

		// release the client
		client.release();
	}

	// use the resource manager
	public static void useResource(String host, int port, String user, String password, Authentication authType) {
		// create the client
		DatabaseClient client = DatabaseClientFactory.newClient(host, port, user, password, authType);

		setUpExample(client);

		// create the batch manager
		BatchManager batchMgr = new BatchManager(client);

		// collect the request items
		BatchManager.BatchRequest request = batchMgr.newBatchRequest();
		request.withRead("/batch/read1.xml", "application/xml");
		request.withRead("/batch/read2.xml", Metadata.COLLECTIONS, "application/xml");
		request.withDelete("/batch/delete1.xml");
		request.withWrite("/batch/write1.xml",
				new StringHandle().withFormat(Format.XML).with("<write1/>"));
		DocumentMetadataHandle meta2 = new DocumentMetadataHandle();
		meta2.getCollections().add("/batch/collection2");
		request.withWrite("/batch/write2.xml",
				meta2,
				new StringHandle().withFormat(Format.XML).with("<write2/>"));

		// apply the request
		BatchManager.BatchResponse response = batchMgr.apply(request);

		System.out.println("Applied the batch request with " +
				(response.getSuccess() ? "success" : "failure")+":");

		// iterate over the response items
		while (response.hasNext()) {
			BatchManager.OutputItem item = response.next();
			BatchManager.OperationType itemType = item.getOperationType();
			String itemUri = item.getUri();
			if (item.getSuccess()) {
				System.out.println(itemType.name().toLowerCase()+" on "+itemUri);

				// inspect the response for a document read request
				if (itemType == BatchManager.OperationType.READ) {
					BatchManager.ReadOutput readItem = (BatchManager.ReadOutput) item;

					// show the document metadata read from the database
					if (readItem.hasMetadata()) {
						StringHandle metadataHandle = readItem.getMetadata(new StringHandle());
						System.out.println("with metadata:\n"+metadataHandle.get());
					}
					// show the document content read from the database
					if (readItem.hasContent()) {
						StringHandle contentHandle = readItem.getContent(new StringHandle());
						System.out.println("with content:\n"+contentHandle.get());
					}
				}
			} else {
				System.out.println(itemType.name().toLowerCase()+" failed on "+itemUri);
				if (item.hasException()) {
					StringHandle exceptionHandle = item.getException(new StringHandle());
					System.out.println("with exception:\n"+exceptionHandle.get());
				}
			}
		}

		// release the client
		client.release();
	}

	// create some documents to work with
	public static void setUpExample(DatabaseClient client) {
		XMLDocumentManager docMgr = client.newXMLDocumentManager();

		StringHandle handle = new StringHandle();

		docMgr.write("/batch/read1.xml",   handle.with("<read1/>"));
		DocumentMetadataHandle meta2 = new DocumentMetadataHandle();
		meta2.getCollections().add("/batch/collection2");
		docMgr.write("/batch/read2.xml",   meta2, handle.with("<read2/>"));
		docMgr.write("/batch/delete1.xml", handle.with("<delete1/>"));
	}

	// clean up by deleting the example resource extension
	public static void tearDownExample(
			String host, int port, String user, String password, Authentication authType) {
		DatabaseClient client = DatabaseClientFactory.newClient(host, port, user, password, authType);

		XMLDocumentManager docMgr = client.newXMLDocumentManager();
		docMgr.delete("/batch/read1.xml");
		docMgr.delete("/batch/read2.xml");
		docMgr.delete("/batch/write1.xml");
		docMgr.delete("/batch/write2.xml");

		ResourceExtensionsManager resourceMgr = client.newServerConfigManager().newResourceExtensionsManager();

		resourceMgr.deleteServices(BatchManager.NAME);

		client.release();
	}
}
