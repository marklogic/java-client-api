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

import java.io.IOException;
import java.io.Reader;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import au.com.bytecode.opencsv.CSVReader;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.io.DOMHandle;

/**
 * An OpenCSV Batcher writes a CSV stream to the database in XML document batches.
 */
public class OpenCSVBatcher
{
	final static public String RESTAPI_NS     = "http://marklogic.com/rest-api";
	final static public String RESTAPI_PREFIX = "rapi:";

	private BatchProcessor processor;

	private int     batchSize = 100;
	private boolean hasHeader = false;

	public OpenCSVBatcher(BatchProcessor processor) {
		super();
		this.processor = processor;
	}
	public OpenCSVBatcher(DatabaseClient client) {
		this(new BatchSplitter(client));
	}

	public int getBatchSize() {
		return batchSize;
	}
	public void setBatchSize(int batchSize) {
		this.batchSize = batchSize;
	}

	public boolean getHasHeader() {
		return hasHeader;
	}
	public void setHasHeader(boolean hasHeader) {
		this.hasHeader = hasHeader;
	}

	protected CSVReader makeParser(Reader content) {
		return new CSVReader(content);
	}

	public long write(Reader content)
	throws IOException, ParserConfigurationException {
		return write(content, null, (QName) null, (QName[]) null);
	}
	public long write(Reader content, String directory)
	throws IOException, ParserConfigurationException {
		return write(content, directory, (QName) null, (QName[]) null);
	}
	public long write(Reader content, QName rowName)
	throws IOException, ParserConfigurationException {
		return write(content, null, rowName, (QName[]) null);
	}
	public long write(Reader content, String directory, String rowName)
	throws IOException, ParserConfigurationException {
		return write(content, directory, new QName(rowName), (QName[]) null);
	}
	public long write(Reader content, String directory, QName rowName)
	throws IOException, ParserConfigurationException {
		return write(content, directory, rowName, (QName[]) null);
	}
	public long write(
			Reader content, String directory, String rowName, QName... colNames
	) throws IOException, ParserConfigurationException {
		return write(content, directory, new QName(rowName), colNames);
	}
	public long write(
			Reader content, String directory, QName rowName, QName... colNames
	) throws IOException, ParserConfigurationException {
		if (rowName == null)
			rowName = new QName("row");
		if (directory == null)
			directory = "/"+rowName.getLocalPart()+"-docs/";
		else if (!directory.endsWith("/"))
			directory = directory+"/";

		CSVReader parser = makeParser(content);

		// Potential improvement:
		// configure a list of xsi:types for validating and annotating

		if (hasHeader) {
			String[] headerNames = parser.readNext();
			if (headerNames == null || headerNames.length == 0)
				throw new IllegalArgumentException("empty header");
			colNames = new QName[headerNames.length];
			for (int i=0; i < headerNames.length; i++) {
				colNames[i] = new QName(
						NameConverter.mangleToNCName(headerNames[i])
						);
			}
		}

		DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

		String path = directory + rowName;

		long docs = 0;

		boolean hasNext = true;
		while (hasNext) {
			Document document  = null;
			Element  batchRoot = null;
			for (int i=0; i < getBatchSize(); i++) {
				String[] line = parser.readNext();
				hasNext = (line != null);
				if (!hasNext)
					break;
				if (line.length == 0)
					continue;

				if (document == null) {
					document  = docBuilder.newDocument();
					batchRoot = document.createElementNS(RESTAPI_NS, RESTAPI_PREFIX+"root");
					document.appendChild(batchRoot);
				}

				// Potential improvement:
				// configure key columns for deriving uris from data

				docs++;

				String uri = path+docs+".xml";

				Element row = createElement(document, rowName);
				row.setAttributeNS(RESTAPI_NS, RESTAPI_PREFIX+"uri", uri);
				row.setAttribute("docnum", String.valueOf(docs));
				batchRoot.appendChild(row);

				for (int j=0; j < line.length; j++) {
					Element column =
						(colNames == null || colNames.length < j) ?
						document.createElement("column"+j) :
						createElement(document, colNames[j]);
					column.setTextContent(line[j]);
					row.appendChild(column);
				}
			}

			if (document == null)
				break;

			if (!processor.processAndContinue(document))
				break;
		}

		content.close();

		return docs;
	}

	private Element createElement(Document document, QName name) {
		String local = name.getLocalPart();

		String ns = name.getNamespaceURI();
		if (ns == null || ns.length() == 0)
			return document.createElement(local);

		String prefix = name.getPrefix();
		if (prefix == null || prefix.length() == 0)
			return document.createElementNS(ns, local);

		return document.createElementNS(ns, prefix + ":" + local);
	}

	/**
	 * A processor for a batch of CSV rows.
	 */
	static public interface BatchProcessor {
		public boolean processAndContinue(Document batch);
	}

	/**
	 * BatchSplitter processes a batch of CSV rows by using DocumentSplitter
	 * to split them into separate documents on the server. 
	 */
	static public class BatchSplitter implements BatchProcessor {
		private DocumentSplitter splitter;

		public BatchSplitter(DatabaseClient client) {
			super();
			splitter = new DocumentSplitter(client);
		}

		@Override
		public boolean processAndContinue(Document batch) {
			splitter.split(new DOMHandle(batch));
			return true;
		}

	}
}
