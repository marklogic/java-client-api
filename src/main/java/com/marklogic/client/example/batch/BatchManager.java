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

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DocumentDescriptor;
import com.marklogic.client.DocumentManager;
import com.marklogic.client.Format;
import com.marklogic.client.MarkLogicIOException;
import com.marklogic.client.RequestParameters;
import com.marklogic.client.ResourceManager;
import com.marklogic.client.io.BaseHandle;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.io.marker.AbstractReadHandle;
import com.marklogic.client.io.marker.AbstractWriteHandle;

/**
 * 
 */
public class BatchManager extends ResourceManager {
	public class BatchRequest {
		private LinkedHashMap<String,InputItem> items      =
			new LinkedHashMap<String,InputItem>();

		private BatchRequest() {
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
		public BatchRequest withRead(String uri, AbstractReadHandle content) {
			return withRead(uri, (Set<DocumentManager.Metadata>) null, content);
		}
		public BatchRequest withRead(String uri, DocumentManager.Metadata category, AbstractReadHandle content) {
			return withRead(uri, listCategories(category), content);
		}
		public BatchRequest withRead(String uri, Set<DocumentManager.Metadata> categories, AbstractReadHandle content) {
			items.put(uri, new ReadInput().withMetadata(categories).withContent(content));
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

	public class BatchResponse implements Iterator<OutputItem> {
		private boolean success = false;
		private Iterator<OutputItem> items;
		private BatchResponse() {
			super();
		}
		public boolean getSuccess() {
			return success;
		}
		public void setSuccess(boolean success) {
			this.success = success;
		}
		@Override
		public boolean hasNext() {
			return items.hasNext();
		}
		@Override
		public OutputItem next() {
			return items.next();
		}
		@Override
		public void remove() {
			throw new UnsupportedOperationException("cannot remove output item");
		}
	}

	private class InputItem {
		DocumentDescriptor desc;
		InputItem withDescriptor(DocumentDescriptor desc) {
			this.desc = desc;
			return this;
		}
	}
	private class DeleteInput extends InputItem {
	}
	private class ReadInput extends InputItem {
		Set<DocumentManager.Metadata>  categories;
		AbstractReadHandle content;
		ReadInput withMetadata(Set<DocumentManager.Metadata>  categories) {
			this.categories = categories;
			return this;
		}
		ReadInput withContent(AbstractReadHandle content) {
			if (content == null)
				return this;
			if (!(content instanceof BaseHandle)) {
				throw new MarkLogicIOException(
						"Read handle does not extend base handle:"+
						content.getClass().getName());
			}
			this.content = content;
			return this;
		}
		String getContentMimetype() {
			return ((BaseHandle) content).getMimetype();
		}
	}
	private class WriteInput extends InputItem {
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
		String getContentMimetype() {
			return ((BaseHandle) content).getMimetype();
		}
	}

	public enum OperationType {
		DELETE, READ, WRITE;
	}
	abstract public class OutputItem {
		private String uri;
//		private DocumentDescriptor desc;
		private boolean success = false;
		private Element exception;
		private OutputItem() {
			super();
		}
		abstract public OperationType getOperationType();
		public String getUri() {
			return uri;
		}
		public void setUri(String uri) {
			this.uri = uri;
		}
/*
		public DocumentDescriptor getDesc() {
			return desc;
		}
		public void setDesc(DocumentDescriptor desc) {
			this.desc = desc;
		}
 */
		public boolean getSuccess() {
			return success;
		}
		public void setSuccess(boolean success) {
			this.success = success;
		}
		// TODO: expose throwable exception
		public Element getException() {
			return exception;
		}
		public void setException(Element exception) {
			this.exception = exception;
		}
	}
	public class DeleteOutput extends OutputItem {
		private DeleteOutput() {
			super();
		}
		public OperationType getOperationType() {
			return OperationType.DELETE;
		}
	}
	public class ReadOutput extends OutputItem {
		private Element metadata;
		private AbstractReadHandle content;
		private ReadOutput() {
			super();
		}
		public OperationType getOperationType() {
			return OperationType.READ;
		}
		// TODO: expose as DocumentMetadataHandle
		public Element getMetadata() {
			return metadata;
		}
		public void setMetadata(Element metadata) {
			this.metadata = metadata;
		}
		public AbstractReadHandle getContent() {
			return content;
		}
		public void setContent(AbstractReadHandle content) {
			this.content = content;
		}
	}
	public class WriteOutput extends OutputItem {
		private WriteOutput() {
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
		DOMHandle    responseManifest = new DOMHandle();

		ArrayList<AbstractWriteHandle> requestHandles = new ArrayList<AbstractWriteHandle>();
		requestHandles.add(requestManifest);

		ArrayList<AbstractReadHandle> respondsHandles = new ArrayList<AbstractReadHandle>();
		respondsHandles.add(responseManifest);

		StringBuilder manifestBuilder = new StringBuilder();
		manifestBuilder.append("<?xml version='1.0' encoding='UTF-8'?>\n");
		manifestBuilder.append("<rapi:batch-requests xmlns:rapi='http://marklogic.com/rest-api'>\n");

		for (Map.Entry<String,InputItem> entry: request.items.entrySet()) {
			String    uri  = entry.getKey();
			InputItem item = entry.getValue();

			if (item instanceof DeleteInput) {
				DeleteInput ditem = (DeleteInput) item;
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

				if (ritem.content != null) {
					manifestBuilder.append("<rapi:content-mimetype>");
					manifestBuilder.append(ritem.getContentMimetype());
					manifestBuilder.append("</rapi:content-mimetype>\n");

					respondsHandles.add(ritem.content);
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

		int readCount = respondsHandles.size();

		AbstractReadHandle[] results = getServices().post(
				new RequestParameters(),
				requestHandles.toArray(new AbstractWriteHandle[requestHandles.size()]),
				respondsHandles.toArray(new AbstractReadHandle[readCount])
				);

		List<OutputItem> items = new ArrayList<OutputItem>();

		boolean requestSuccess = true;
		int     nextRead       = 1;        // skip the response manifest

		NodeList responseItems = responseManifest.get().getDocumentElement().getChildNodes();
		int      responseCount = responseItems.getLength();
		for (int i=0; i < responseCount; i++) {
			Node responseNode = responseItems.item(i);
			if (responseNode.getNodeType() != Node.ELEMENT_NODE)
				continue;
			Element responseItem = (Element) responseNode;

			String  itemUri      = null;
			boolean itemSuccess  = false;
			Element itemMetadata = null;
			String  itemMimetype = null;
			Element itemFailure  = null;

			NodeList fieldItems = responseItem.getChildNodes();
			int      fieldCount = fieldItems.getLength();
			for (int j=0; j < fieldCount; j++) {
				Node fieldNode = fieldItems.item(j);
				if (fieldNode.getNodeType() != Node.ELEMENT_NODE)
					continue;
				Element fieldItem = (Element) fieldNode;
				String  fieldName = fieldItem.getLocalName();

				if ("uri".equals(fieldName)) {
					itemUri = fieldItem.getTextContent();
				} else if ("request-succeeded".equals(fieldName)) {
					itemSuccess = "true".equals(fieldItem.getTextContent());
				} else if ("metadata".equals(fieldName)) {
					itemMetadata = fieldItem;
				} else if ("content-mimetype".equals(fieldName)) {
					itemMimetype = fieldItem.getTextContent();
				} else if ("request-failure".equals(fieldName)) {
					itemFailure = fieldItem;
				} else {
					// TODO: warn
				}
			}

			if (requestSuccess && !itemSuccess)
				requestSuccess = false;

			String responseName = responseItem.getLocalName();
			if ("delete-response".equals(responseName)) {
				DeleteOutput deleteOutput = new DeleteOutput();
				deleteOutput.setUri(itemUri);
				deleteOutput.setSuccess(itemSuccess);
				if (itemFailure != null)
					deleteOutput.setException(itemFailure);
				items.add(deleteOutput);
			} else if ("get-response".equals(responseName)) {
				ReadOutput readOutput = new ReadOutput();
				readOutput.setUri(itemUri);
				readOutput.setSuccess(itemSuccess);
				if (itemMetadata != null)
					readOutput.setMetadata(itemMetadata);
				if (itemMimetype != null) {
					if (nextRead >= readCount)
						throw new RuntimeException("read count mismatch");
					AbstractReadHandle itemContent = respondsHandles.get(nextRead);
					// TODO: set format
					readOutput.setContent(itemContent);
					nextRead++;
				}
				if (itemFailure != null)
					readOutput.setException(itemFailure);
				items.add(readOutput);
			} else if ("put-response".equals(responseName)) {
				WriteOutput writeOutput = new WriteOutput();
				writeOutput.setUri(itemUri);
				writeOutput.setSuccess(itemSuccess);
				if (itemFailure != null)
					writeOutput.setException(itemFailure);
				items.add(writeOutput);
			}
		}
		if (nextRead != readCount)
			throw new RuntimeException("failed to read all content");

		BatchResponse response = new BatchResponse();
		response.setSuccess(requestSuccess);
		response.items = items.iterator();

		return response;
	}
}
