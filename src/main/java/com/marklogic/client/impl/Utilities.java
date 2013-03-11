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
package com.marklogic.client.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import org.xml.sax.SAXException;

import com.marklogic.client.MarkLogicIOException;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.OutputStreamSender;
import com.marklogic.client.io.marker.XMLReadHandle;
import com.marklogic.client.io.marker.XMLWriteHandle;

public final class Utilities {

	private static DocumentBuilderFactory factory;

	private static DocumentBuilderFactory getFactory()
			throws ParserConfigurationException {
		if (factory == null)
			factory = makeDocumentBuilderFactory();
		return factory;
	}

	private static DocumentBuilderFactory makeDocumentBuilderFactory()
			throws ParserConfigurationException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		factory.setValidating(false);
		return factory;
	}

	/**
	 * Construct a dom Element from a string. A utility function for creating
	 * DOM elements when needed for other builder functions.
	 * 
	 * @param xmlString
	 *            XML for an element.
	 * @return w3c.dom.Element representation the provided XML.
	 */
	public static org.w3c.dom.Element domElement(String xmlString) {
		org.w3c.dom.Element element = null;
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(
					xmlString.getBytes());
			element = getFactory().newDocumentBuilder().parse(bais)
					.getDocumentElement();
		} catch (SAXException e) {
			throw new MarkLogicIOException(
					"Could not make Element from xmlString" + xmlString, e);
		} catch (IOException e) {
			throw new MarkLogicIOException(
					"Could not make Element from xmlString" + xmlString, e);
		} catch (ParserConfigurationException e) {
			throw new MarkLogicIOException(
					"Could not make Element from xmlString" + xmlString, e);
		}
		return element;
	}

	public static List<XMLEvent> importXML(XMLWriteHandle writeHandle) {
		List<XMLEvent> importedXML = new ArrayList<XMLEvent>();

		@SuppressWarnings("rawtypes")
		HandleImplementation baseHandle = HandleAccessor.checkHandle(
				writeHandle, "import");

		XMLInputFactory factory = XMLInputFactory.newInstance();
		XMLEventReader reader;
		Object content = baseHandle.sendContent();
		try {
			if (content instanceof byte[]) {

				ByteArrayInputStream bais = new ByteArrayInputStream(
						(byte[]) content);
				reader = factory.createXMLEventReader(bais);

			} else if (content instanceof String) {
				reader = factory.createXMLEventReader(new StringReader(
						(String) content));
			} else if (content instanceof Reader) {
				reader = factory.createXMLEventReader((Reader) content);
			} else if (content instanceof File) {
				reader = factory.createXMLEventReader(new FileInputStream(
						(File) content));
			} else if (content instanceof InputStream) {
				reader = factory.createXMLEventReader((InputStream) content);
			} else if (content instanceof OutputStreamSender) {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				((OutputStreamSender) content).write(baos);
				byte[] bytes = baos.toByteArray();
				reader = factory.createXMLEventReader(new ByteArrayInputStream(
						bytes));
			} else {
				throw new IllegalArgumentException(
						"Unrecognized handle for XML import");
			}
			while (reader.hasNext()) {
				XMLEvent e = reader.nextEvent();
				if (e.getEventType() == XMLStreamConstants.START_DOCUMENT) {
					// skip prolog
				} else if (e.getEventType() == XMLStreamConstants.END_DOCUMENT) {

				} else {
					importedXML.add(e);
				}
			}
		} catch (XMLStreamException e) {
			throw new MarkLogicIOException(e);
		} catch (FileNotFoundException e) {
			throw new MarkLogicIOException(e);
		} catch (IOException e) {
			throw new MarkLogicIOException(e);
		}
		return importedXML;
	}

	@SuppressWarnings({ "unchecked", "resource" })
	public static <T extends XMLReadHandle> T exportXML(
			List<XMLEvent> toExport, T handle) {
		XMLOutputFactory factory = XMLOutputFactory.newInstance();

		@SuppressWarnings("rawtypes")
		HandleImplementation baseHandle = HandleAccessor.checkHandle(handle,
				"export");

		if (toExport == null) {
			return null;
		}
		XMLEventWriter eventWriter;
		ByteArrayOutputStream baos = null;
		OutputStream out = null;
		
		@SuppressWarnings("rawtypes")
		Class as = baseHandle.receiveAs();
		File tempFile = null;
		
		// use handle's output stream, or a new ByteArrayOutputStream
		if (handle instanceof FileHandle) {
			try {
				tempFile = File.createTempFile("tmp", "");
				out = new FileOutputStream(tempFile);
			} catch (FileNotFoundException e) {
				throw new MarkLogicIOException(e);
			} catch (IOException e) {
				throw new MarkLogicIOException(e);
			}
		} else {
			baos = new ByteArrayOutputStream();
			out = baos;
		}

		try {
			eventWriter = factory.createXMLEventWriter(out, "UTF-8");

			for (XMLEvent event : toExport) {
				eventWriter.add(event);
			}
		} catch (XMLStreamException e) {
			throw new MarkLogicIOException(e);
		}
		try {
			out.close();
		} catch (IOException e) {
			throw new MarkLogicIOException(e);
		}

		if (!HandleAccessor.isHandle(baseHandle)) {
			throw new IllegalArgumentException(
					"You can only export to handles that extend BaseHandle");
		} else {
			if (byte[].class.isAssignableFrom(as)) {
				baseHandle.receiveContent(baos.toByteArray());
			} else if (InputStream.class.isAssignableFrom(as)) {
				baseHandle.receiveContent(new ByteArrayInputStream(baos
						.toByteArray()));
			} else if (String.class.isAssignableFrom(as)) {
				baseHandle.receiveContent(new String(baos.toByteArray(), Charset.forName("UTF-8")));
			} else if (Reader.class.isAssignableFrom(as)) {
				baseHandle.receiveContent(new StringReader(new String(baos
						.toByteArray(), Charset.forName("UTF-8"))));
			} else if (File.class.isAssignableFrom(as)) {
				baseHandle.receiveContent(tempFile);
			} else {
				throw new IllegalArgumentException(
						"Unrecognized handle for XML export");
			}

			return handle;

		}
	}
}
