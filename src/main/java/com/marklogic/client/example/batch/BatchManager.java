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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.FailedRequestException;
import com.marklogic.client.MarkLogicIOException;
import com.marklogic.client.document.DocumentDescriptor;
import com.marklogic.client.document.DocumentManager;
import com.marklogic.client.extensions.ResourceManager;
import com.marklogic.client.extensions.ResourceServices.ServiceResult;
import com.marklogic.client.extensions.ResourceServices.ServiceResultIterator;
import com.marklogic.client.io.BaseHandle;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.io.marker.AbstractReadHandle;
import com.marklogic.client.io.marker.AbstractWriteHandle;
import com.marklogic.client.io.marker.XMLReadHandle;
import com.marklogic.client.util.RequestParameters;

/**
 * BatchManager provides an extension for executing a batch of document requests.
 */
public class BatchManager extends ResourceManager {
	public class BatchRequest {
		private LinkedHashMap<String,InputItem> items = new LinkedHashMap<String,InputItem>();

		BatchRequest() {
			super();
		}

		public Set<DocumentManager.Metadata> listCategories(DocumentManager.Metadata... categories) {
			if (categories == null || categories.length == 0) {
				return null;
			}

			Set<DocumentManager.Metadata> catSet = new HashSet<DocumentManager.Metadata>();
			for (DocumentManager.Metadata category: categories) {
				catSet.add(category);
			}

			return catSet;
		}

// TODO: DocumentDescriptor overloads
		public BatchRequest withDelete(String uri) {
			items.put(uri, new DeleteInput());
			return this;
		}
		public BatchRequest withRead(String uri, String mimetype) {
			return withRead(uri, (Set<DocumentManager.Metadata>) null, mimetype);
		}
		public BatchRequest withRead(String uri, DocumentManager.Metadata category, String mimetype) {
			return withRead(uri, listCategories(category), mimetype);
		}
		public BatchRequest withRead(String uri, Set<DocumentManager.Metadata> categories, String mimetype) {
			items.put(uri, new ReadInput().withMetadata(categories).withMimetype(mimetype));
			return this;
		}
		public BatchRequest withWrite(String uri, AbstractWriteHandle content) {
			return withWrite(uri, null, content);
		}
		// TODO: allow any metadata handle
		public BatchRequest withWrite(String uri, DocumentMetadataHandle metadata, AbstractWriteHandle content) {
			items.put(uri, new WriteInput().withMetadata(metadata).withContent(content));
			return this;
		}
	}

// TODO: assemble a single thread-safe queue instead of two separate queues
	public class BatchResponse implements Iterator<OutputItem> {
		boolean success = false;
		Iterator<OutputItem> items;
		ServiceResultIterator results;
		BatchResponse() {
			super();
		}
		public boolean getSuccess() {
			return success;
		}
		@Override
		public boolean hasNext() {
			if (items == null)
				return false;
			return items.hasNext();
		}
		@Override
		public OutputItem next() {
			if (items == null)
				return null;
			OutputItem item = items.next();
			if (item.exceptionMimetype != null) {
				if (results == null || !results.hasNext()) {
					throw new IllegalStateException("unable to get exception for request");
				}
				item.exception = results.next();
			} else if (item instanceof ReadOutput) {
				ReadOutput ritem = (ReadOutput) item;
				if (ritem.metadataMimetype != null) {
					if (results == null || !results.hasNext()) {
						throw new IllegalStateException("unable to get metadata for read request");
					}
					ritem.metadata = results.next();
				}
				if (ritem.contentMimetype != null) {
					if (results == null || !results.hasNext()) {
						throw new IllegalStateException("unable to get content for read request");
					}
					ritem.content = results.next();
				}
			}
			if (results != null && !results.hasNext()) {
				results.close();
				results = null;
			}
			return item;
		}
		@Override
		public void remove() {
			throw new UnsupportedOperationException("cannot remove output item");
		}
		public void close() {
			if (results != null) {
				results.close();
				results = null;
			}
			items = null;
		}
		@Override
		protected void finalize() throws Throwable {
			close();
			super.finalize();
		}
	}

	class InputItem {
		DocumentDescriptor desc;
		InputItem withDescriptor(DocumentDescriptor desc) {
			this.desc = desc;
			return this;
		}
	}
	class DeleteInput extends InputItem {
	}
	class ReadInput extends InputItem {
		Set<DocumentManager.Metadata>  categories;
		String mimetype;
		ReadInput withMetadata(Set<DocumentManager.Metadata>  categories) {
			this.categories = categories;
			return this;
		}
		ReadInput withMimetype(String mimetype) {
			this.mimetype = mimetype;
			return this;
		}
	}
	class WriteInput extends InputItem {
		DocumentMetadataHandle metadata;
		AbstractWriteHandle    content;
		WriteInput withMetadata(DocumentMetadataHandle metadata) {
			this.metadata = metadata;
			return this;
		}
		WriteInput withContent(AbstractWriteHandle content) {
			if (content == null)
				return this;
			if (!(content instanceof BaseHandle)) {
				throw new MarkLogicIOException(
						"Write handle does not extend base handle:"+
						content.getClass().getName());
			}
			this.content = content;
			return this;
		}
		@SuppressWarnings("rawtypes")
		String getContentMimetype() {
			return ((BaseHandle) content).getMimetype();
		}
	}

	/**
	 * Enumerates the operations supported on a document in the batch.
	 */
	public enum OperationType {
		DELETE, READ, WRITE;
	}
	abstract public class OutputItem {
		String uri;
//		DocumentDescriptor desc;
		boolean success = false;
		String exceptionMimetype;
		ServiceResult exception;
		OutputItem() {
			super();
		}
		abstract public OperationType getOperationType();
		public String getUri() {
			return uri;
		}
/*
		public DocumentDescriptor getDesc() {
			return desc;
		}
 */
		public boolean getSuccess() {
			return success;
		}
		public boolean hasException() {
			return (exception != null);
		}
		// TODO: expose throwable exception
		public <R extends XMLReadHandle> R getException(R handle) {
			if (exception == null)
				throw new IllegalStateException("could not get exception for result");
			return exception.getContent(handle);
		}
	}
	public class DeleteOutput extends OutputItem {
		DeleteOutput() {
			super();
		}
		public OperationType getOperationType() {
			return OperationType.DELETE;
		}
	}
	public class ReadOutput extends OutputItem {
		String        metadataMimetype;
		ServiceResult metadata;
		String        contentMimetype;
		ServiceResult content;
		ReadOutput() {
			super();
		}
		public OperationType getOperationType() {
			return OperationType.READ;
		}
		public boolean hasMetadata() {
			return (metadata != null);
		}
		public DocumentMetadataHandle getMetadata() {
			if (metadata == null)
				throw new IllegalStateException("could not get metadata for result");
			return metadata.getContent(new DocumentMetadataHandle());
		}
		public <R extends XMLReadHandle> R getMetadata(R handle) {
			if (metadata == null)
				throw new IllegalStateException("could not get metadata for result");
			return metadata.getContent(handle);
		}
		public boolean hasContent() {
			return (content != null);
		}
		public String getContentMimetype() {
			return contentMimetype;
		}
		public <R extends AbstractReadHandle> R getContent(R handle) {
			if (content == null)
				throw new IllegalStateException("could not get content for result");
			return content.getContent(handle);
		}
	}
	public class WriteOutput extends OutputItem {
		WriteOutput() {
			super();
		}
		public OperationType getOperationType() {
			return OperationType.WRITE;
		}
	}

	static final public String NAME = "docbatch";

	public BatchManager(DatabaseClient client) {
		super();
		client.init(NAME, this);
	}
	public BatchRequest newBatchRequest() {
		return new BatchRequest();
	}
	public BatchResponse apply(BatchRequest request) {
		if (request == null)
			return null;

		StringHandle requestManifest  = new StringHandle();

		ArrayList<AbstractWriteHandle> requestHandles = new ArrayList<AbstractWriteHandle>();
		requestHandles.add(requestManifest);

		StringBuilder manifestBuilder = new StringBuilder();
		manifestBuilder.append("<?xml version='1.0' encoding='UTF-8'?>\n");
		manifestBuilder.append("<rapi:batch-requests xmlns:rapi='http://marklogic.com/rest-api'>\n");

		ArrayList<String> readMimetypes = new ArrayList<String>();
		// read the response manifest first
		readMimetypes.add("application/xml");
		for (Map.Entry<String,InputItem> entry: request.items.entrySet()) {
			String    uri  = entry.getKey();
			InputItem item = entry.getValue();

			if (item instanceof DeleteInput) {
				manifestBuilder.append("<rapi:delete-request>\n");

				manifestBuilder.append("<rapi:uri>");
				manifestBuilder.append(uri);
				manifestBuilder.append("</rapi:uri>\n");

				manifestBuilder.append("</rapi:delete-request>\n");
			} else if (item instanceof ReadInput) {
				ReadInput ritem = (ReadInput) item;
				manifestBuilder.append("<rapi:get-request>\n");

				manifestBuilder.append("<rapi:uri>");
				manifestBuilder.append(uri);
				manifestBuilder.append("</rapi:uri>\n");

				if (ritem.categories != null && ritem.categories.size() > 0) {
					StringBuilder categoryBuilder = new StringBuilder();
					categoryBuilder.append("<rapi:metadata>\n");

					for (DocumentManager.Metadata category: ritem.categories) {
						categoryBuilder.append("<rapi:");
						categoryBuilder.append(category.name().toLowerCase());
						categoryBuilder.append("/>\n");
					}

					categoryBuilder.append("</rapi:metadata>\n");

					manifestBuilder.append(categoryBuilder.toString());
				}

				if (ritem.mimetype != null) {
					readMimetypes.add(ritem.mimetype);

					manifestBuilder.append("<rapi:content-mimetype>");
					manifestBuilder.append(ritem.mimetype);
					manifestBuilder.append("</rapi:content-mimetype>\n");
				}
				
				manifestBuilder.append("</rapi:get-request>\n");
			} else if (item instanceof WriteInput) {
				WriteInput witem = (WriteInput) item;
				manifestBuilder.append("<rapi:put-request>\n");

				manifestBuilder.append("<rapi:uri>");
				manifestBuilder.append(uri);
				manifestBuilder.append("</rapi:uri>\n");

				if (witem.content != null) {
					manifestBuilder.append("<rapi:content-mimetype>");
					manifestBuilder.append(witem.getContentMimetype());
					manifestBuilder.append("</rapi:content-mimetype>\n");

					requestHandles.add(witem.content);
				}

				manifestBuilder.append("</rapi:put-request>\n");
			} 
		}

		manifestBuilder.append("</rapi:batch-requests>\n");
		requestManifest.set(manifestBuilder.toString());
		requestManifest.setFormat(Format.XML);

		String[] requestMimetypes = new String[readMimetypes.size()];

		ServiceResultIterator resultItr = getServices().post(
				new RequestParameters(),
				requestHandles.toArray(new AbstractWriteHandle[requestHandles.size()]),
				requestMimetypes
				);

		if (!resultItr.hasNext())
			throw new FailedRequestException("Could not executed batch request");
		
		DOMHandle responseManifest = resultItr.next().getContent(new DOMHandle());

		List<OutputItem> items = new ArrayList<OutputItem>();

		boolean requestSuccess = true;

		NodeList responseItems = responseManifest.get().getDocumentElement().getChildNodes();
		int      responseCount = responseItems.getLength();
		for (int i=0; i < responseCount; i++) {
			Node responseNode = responseItems.item(i);
			if (responseNode.getNodeType() != Node.ELEMENT_NODE)
				continue;
			Element responseItem = (Element) responseNode;

			String  itemUri       = null;
			boolean itemSuccess   = false;
			String  itemMetadata  = null;
			String  itemContent   = null;
			String  itemException = null;

			NodeList fieldItems = responseItem.getChildNodes();
			int      fieldCount = fieldItems.getLength();
			for (int j=0; j < fieldCount; j++) {
				Node fieldNode = fieldItems.item(j);
				if (fieldNode.getNodeType() != Node.ELEMENT_NODE)
					continue;
				Element fieldItem = (Element) fieldNode;
				String  fieldName = fieldItem.getLocalName();

				if ("uri".equals(fieldName)) {
					itemUri       = fieldItem.getTextContent();
				} else if ("request-succeeded".equals(fieldName)) {
					itemSuccess   = "true".equals(fieldItem.getTextContent());
				} else if ("metadata-mimetype".equals(fieldName)) {
					itemMetadata  = fieldItem.getTextContent();
				} else if ("content-mimetype".equals(fieldName)) {
					itemContent   = fieldItem.getTextContent();
				} else if ("request-failure".equals(fieldName)) {
					itemException = fieldItem.getTextContent();
				} else {
					// TODO: warn
				}
			}

			if (requestSuccess && !itemSuccess)
				requestSuccess = false;

			String responseName = responseItem.getLocalName();
			if ("delete-response".equals(responseName)) {
				DeleteOutput deleteOutput = new DeleteOutput();
				deleteOutput.uri = itemUri;
				deleteOutput.success = itemSuccess;
				if (itemException != null) {
					deleteOutput.exceptionMimetype = itemException;
				}
				items.add(deleteOutput);
			} else if ("get-response".equals(responseName)) {
				ReadOutput readOutput = new ReadOutput();
				readOutput.uri = itemUri;
				readOutput.success = itemSuccess;
				if (itemException != null) {
					readOutput.exceptionMimetype = itemException;
				} else {
					if (itemMetadata != null) {
						readOutput.metadataMimetype = itemMetadata;
					}
					if (itemContent != null) {
						// TODO: set format
						readOutput.contentMimetype = itemContent;
					}
				}
				items.add(readOutput);
			} else if ("put-response".equals(responseName)) {
				WriteOutput writeOutput = new WriteOutput();
				writeOutput.uri = itemUri;
				writeOutput.success = itemSuccess;
				if (itemException != null) {
					writeOutput.exceptionMimetype = itemException;
				}
				items.add(writeOutput);
			}
		}

		BatchResponse response = new BatchResponse();
		response.success = requestSuccess;
		response.items   = new ConcurrentLinkedQueue<OutputItem>(items).iterator();
		response.results = resultItr;

		return response;
	}
}
