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
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.xml.sax.SAXException;

import com.marklogic.client.MarkLogicIOException;
import com.marklogic.client.io.BaseHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.OutputStreamSender;
import com.marklogic.client.io.marker.AbstractReadHandle;
import com.marklogic.client.io.marker.AbstractWriteHandle;
import com.marklogic.client.io.marker.ContentHandle;
import com.marklogic.client.io.marker.StructureWriteHandle;
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
					xmlString.getBytes(Charset.forName("UTF-8")));
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

	public static List<XMLEvent> importFromHandle(AbstractWriteHandle writeHandle) {
		if (writeHandle == null) {
			return null;
		}

		@SuppressWarnings("rawtypes")
		HandleImplementation baseHandle = HandleAccessor.checkHandle(writeHandle,
				"import");

		return objectToEvents(baseHandle.sendContent());
	}
	static public List<XMLEvent> objectToEvents(Object content) {
		if (content == null) {
			return null;
		} else if (content instanceof byte[]) {
			return bytesToEvents((byte[]) content);
		} else if (content instanceof File) {
			return fileToEvents((File) content);
		} else if (content instanceof InputStream) {
			return inputStreamToEvents((InputStream) content);
		} else if (content instanceof Reader) {
			return readerToEvents((Reader) content);
		} else if (content instanceof String) {
			return stringToEvents((String) content);
		} else if (content instanceof OutputStreamSender) {
			return outputSenderToEvents((OutputStreamSender) content);
		} else {
			throw new IllegalArgumentException(
					"Unrecognized class for import: "+content.getClass().getName()
					);
		}
	}
	static public List<XMLEvent> bytesToEvents(byte[] bytes) {
		return readerToEvents(readBytes(bytes));
	}
	static public List<XMLEvent> fileToEvents(File file) {
		return readerToEvents(readFile(file));
	}
	static public List<XMLEvent> inputStreamToEvents(InputStream stream) {
		return readerToEvents(readInputStream(stream));
	}
	static public List<XMLEvent> outputSenderToEvents(OutputStreamSender sender) {
		return readerToEvents(readOutputSender(sender));
	}
	static public List<XMLEvent> readerToEvents(Reader reader) {
		return readerToEvents(readReader(reader));
	}
	static public List<XMLEvent> stringToEvents(String string) {
		return readerToEvents(readString(string));
	}
	static XMLEventReader readBytes(byte[] bytes) {
		if (bytes == null || bytes.length == 0) {
			return null;
		}
		return readInputStream(new ByteArrayInputStream(bytes));
	}
	static XMLEventReader readFile(File file) {
		try {
			if (file == null) {
				return null;
			}
			return readInputStream(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			throw new MarkLogicIOException(e);
		}
	}
	static XMLEventReader readInputStream(InputStream stream) {
		try {
			if (stream == null) {
				return null;
			}
			return makeInputFactory().createXMLEventReader(stream);
		} catch (XMLStreamException e) {
			throw new MarkLogicIOException(e);
		}
	}
	static XMLEventReader readOutputSender(OutputStreamSender sender) {
		try {
			if (sender == null) {
				return null;
			}
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			sender.write(baos);
			return readBytes(baos.toByteArray());
		} catch (IOException e) {
			throw new MarkLogicIOException(e);
		}
	}
	static XMLEventReader readReader(Reader reader) {
		try {
			if (reader == null) {
				return null;
			}
			return makeInputFactory().createXMLEventReader(reader);
		} catch (XMLStreamException e) {
			throw new MarkLogicIOException(e);
		}
	}
	static XMLEventReader readString(String string) {
		if (string == null) {
			return null;
		}
		return readReader(new StringReader(string));
	}
	static XMLInputFactory makeInputFactory() {
		XMLInputFactory factory = XMLInputFactory.newInstance();
		factory.setProperty("javax.xml.stream.isNamespaceAware", true);
		factory.setProperty("javax.xml.stream.isValidating",     false);

		return factory;
	}
	static public List<XMLEvent> readerToEvents(XMLEventReader reader) {
		try {
			if (reader == null) {
				return null;
			}

			List<XMLEvent> events = new ArrayList<XMLEvent>();
			while (reader.hasNext()) {
				XMLEvent event = reader.nextEvent();
				switch (event.getEventType()) {
				case XMLEvent.START_DOCUMENT:
				case XMLEvent.END_DOCUMENT:
					// skip prolog and end
					break;
				default:
					events.add(event);
					break;
				}
			}

			if (events.size() == 0) {
				return null;
			}
			return events;
		} catch (XMLStreamException e) {
			throw new MarkLogicIOException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T extends AbstractReadHandle> T exportToHandle(
			List<XMLEvent> events, T handle
			) {
		if (handle == null) {
			return null;
		}

		@SuppressWarnings("rawtypes")
		HandleImplementation baseHandle = HandleAccessor.checkHandle(handle,
				"export");

		baseHandle.receiveContent(
				eventsToObject(events, baseHandle.receiveAs())
				);

		return handle;
	}
	public static Object eventsToObject(List<XMLEvent> events, Class<?> as) {
		if (events == null || events.size() == 0) {
			return null;
		}

		if (byte[].class.isAssignableFrom(as)) {
			return eventsToBytes(events);
		} else if (File.class.isAssignableFrom(as)) {
			return eventsToFile(events, ".xml");
		} else if (InputStream.class.isAssignableFrom(as)) {
			return eventsToInputStream(events);
		} else if (Reader.class.isAssignableFrom(as)) {
			return eventsToReader(events);
		} else if (String.class.isAssignableFrom(as)) {
			return eventsToString(events);
		} else {
			throw new IllegalArgumentException(
					"Unrecognized class for export: "+as.getName()
					);
		}
	}
	public static byte[] eventsToBytes(List<XMLEvent> events) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		if (!writeEvents(events, baos)) {
			return null;
		}

		return baos.toByteArray();
	}
	public static File eventsToFile(List<XMLEvent> events, String extension) {
		try {
			File tempFile = File.createTempFile("tmp", extension);
			if (!writeEvents(events, new FileOutputStream(tempFile))) {
				if (tempFile.exists()) {
					tempFile.delete();
				}

				return null;
			}

			return tempFile;
		} catch (FileNotFoundException e) {
			throw new MarkLogicIOException(e);
		} catch (IOException e) {
			throw new MarkLogicIOException(e);
		}
	}
	public static InputStream eventsToInputStream(List<XMLEvent> events) {
		byte[] bytes = eventsToBytes(events);
		if (bytes == null || bytes.length == 0) {
			return null;
		}

		return new ByteArrayInputStream(bytes);
	}
	public static Reader eventsToReader(List<XMLEvent> events) {
		String string = eventsToString(events);
		if (string == null) {
			return null;
		}

		return new StringReader(string);
	}
	public static String eventsToString(List<XMLEvent> events) {
		byte[] bytes = eventsToBytes(events);
		if (bytes == null || bytes.length == 0) {
			return null;
		}

		return new String(bytes, Charset.forName("UTF-8"));
	}
	public static boolean writeEvents(List<XMLEvent> events, OutputStream out) {
		if (events == null || events.size() == 0) {
			return false;
		}

		try {
			XMLOutputFactory factory = XMLOutputFactory.newInstance();
			factory.setProperty("javax.xml.stream.isRepairingNamespaces", true);

			XMLEventWriter eventWriter = factory.createXMLEventWriter(out, "UTF-8");

			for (XMLEvent event: events) {
				eventWriter.add(event);
			}

			eventWriter.flush();
			eventWriter.close();

			return true;
		} catch (XMLStreamException e) {
			throw new MarkLogicIOException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T extends AbstractReadHandle> T exportTextToHandle(
			List<XMLEvent> events, T handle
			) {
		if (handle == null) {
			return null;
		}

		@SuppressWarnings("rawtypes")
		HandleImplementation baseHandle = HandleAccessor.checkHandle(handle,
				"export");

		baseHandle.receiveContent(
				eventTextToObject(events, baseHandle.receiveAs())
				);

		return handle;
	}
	public static Object eventTextToObject(List<XMLEvent> events, Class<?> as) {
		if (events == null || events.size() == 0) {
			return null;
		}

		if (byte[].class.isAssignableFrom(as)) {
			return eventTextToBytes(events);
		} else if (File.class.isAssignableFrom(as)) {
			return eventTextToFile(events, ".txt");
		} else if (InputStream.class.isAssignableFrom(as)) {
			return eventTextToInputStream(events);
		} else if (Reader.class.isAssignableFrom(as)) {
			return eventTextToReader(events);
		} else if (String.class.isAssignableFrom(as)) {
			return eventTextToString(events);
		} else {
			throw new IllegalArgumentException(
					"Unrecognized class for text export: "+as.getName()
					);
		}
	}
	public static byte[] eventTextToBytes(List<XMLEvent> events) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		if (!writeEventText(events, baos)) {
			return null;
		}

		return baos.toByteArray();
	}
	public static File eventTextToFile(List<XMLEvent> events, String extension) {
		try {
			File tempFile = File.createTempFile("tmp", extension);
			if (!writeEventText(events, new FileOutputStream(tempFile))) {
				if (tempFile.exists()) {
					tempFile.delete();
				}

				return null;
			}

			return tempFile;
		} catch (FileNotFoundException e) {
			throw new MarkLogicIOException(e);
		} catch (IOException e) {
			throw new MarkLogicIOException(e);
		}
	}
	public static InputStream eventTextToInputStream(List<XMLEvent> events) {
		byte[] bytes = eventTextToBytes(events);
		if (bytes == null || bytes.length == 0) {
			return null;
		}

		return new ByteArrayInputStream(bytes);
	}
	public static Reader eventTextToReader(List<XMLEvent> events) {
		String string = eventTextToString(events);
		if (string == null) {
			return null;
		}

		return new StringReader(string);
	}
	public static String eventTextToString(List<XMLEvent> events) {
		if (events == null || events.size() == 0) {
			return null;
		}

		StringBuilder buf = new StringBuilder();
		for (XMLEvent event: events) {
			if (event.isCharacters()) {
				buf.append(event.asCharacters().getData());
			}
		}

		return buf.toString();
	}
	public static boolean writeEventText(List<XMLEvent> events, OutputStream out) {
		if (events == null || events.size() == 0) {
			return false;
		}

		try {
			for (XMLEvent event: events) {
				if (event.isCharacters()) {
					out.write(event.asCharacters().getData().getBytes());
				}
			}

			out.flush();
			out.close();

			return true;
		} catch (IOException e) {
			throw new MarkLogicIOException(e);
		}
	}

	public static Source handleToSource(XMLWriteHandle handle) {
		try {
			if (handle == null) {
				return null;
			}

			@SuppressWarnings("rawtypes")
			HandleImplementation baseHandle =
				HandleAccessor.checkHandle(handle, "source");

			Object content = baseHandle.sendContent();
			if (content instanceof byte[]) {
				return new StreamSource(
						new ByteArrayInputStream((byte[]) content));
			} else if (content instanceof File) {
				return new StreamSource(
						new FileInputStream((File) content));
			} else if (content instanceof InputStream) {
				return new StreamSource((InputStream) content);
			} else if (content instanceof OutputStreamSender) {
				ByteArrayOutputStream buf = new ByteArrayOutputStream();
				((OutputStreamSender) content).write(buf);
				return new StreamSource(
						new ByteArrayInputStream(buf.toByteArray()));
			} else if (content instanceof Reader) {
				return new StreamSource((Reader) content);
			} else if (content instanceof String) {
				return new StreamSource(
						new StringReader((String) content));
			} else {
				throw new IllegalArgumentException("Unrecognized handle for source");
			}
		} catch (FileNotFoundException e) {
			throw new MarkLogicIOException(e);
		} catch (IOException e) {
			throw new MarkLogicIOException(e);
		}
	}

	static public XMLEventReader makeEventListReader(List<XMLEvent> events) {
		if (events == null || events.size() == 0) {
			return null;
		}

		return new XMLEventListReader(events);
	}
	static class XMLEventListReader implements XMLEventReader {
		private List<XMLEvent> events;
		private int curr = -1;

		XMLEventListReader(List<XMLEvent> events) {
			super();
			this.events = events;
		}
		@Override
		public Object next() {
			return nextEvent();
		}
		@Override
		public void remove() {
			if (!hasItem(curr)) {
				return;
			}
			events.remove(curr);
		}
		@Override
		public XMLEvent nextEvent() {
			if (!hasNext()) {
				return null;
			}
			return events.get(++curr);
		}
		@Override
		public boolean hasNext() {
			return hasItem(curr + 1);
		}
		boolean hasItem(int i) {
			return (events == null || i < events.size());
		}
		@Override
		public XMLEvent peek() {
			int peek = curr + 1;
			if (!hasItem(peek)) {
				return null;
			}
			return events.get(peek);
		}
		@Override
		public String getElementText() throws XMLStreamException {
			if (!hasNext() || !events.get(curr).isStartElement()) {
				throw new XMLStreamException("no start element for text");
			}

			StringBuilder buf = new StringBuilder();
			while (++curr < events.size()) {
				XMLEvent event = events.get(curr);
				int eventType = event.getEventType();
				if (eventType == XMLEvent.CHARACTERS || eventType == XMLEvent.CDATA ||
						eventType == XMLEvent.SPACE) {
					buf.append(event.asCharacters().getData());
				} else if (eventType == XMLEvent.END_ELEMENT) {
					break;
				} else if (eventType == XMLEvent.START_ELEMENT) {
					throw new XMLStreamException("found subelement instead of text");
				}
			}

			return buf.toString();
		}
		@Override
		public XMLEvent nextTag() throws XMLStreamException {
			if (!hasNext()) {
				throw new XMLStreamException("no next tag");
			}
			XMLEvent event = events.get(curr);
			int eventType = event.getEventType();
			if (eventType != XMLEvent.START_ELEMENT && eventType != XMLEvent.END_ELEMENT) {
				throw new XMLStreamException("no start tag for next tag");
			}

			while (++curr < events.size()) {
				event = events.get(curr);
				eventType = event.getEventType();
				if (eventType == XMLEvent.START_ELEMENT || eventType == XMLEvent.END_ELEMENT) {
					break;
				} else if (eventType != XMLEvent.SPACE) {
					throw new XMLStreamException("event other than space before next tag");
				}
			}

			return event;
		}
		@Override
		public Object getProperty(String name) {
			if (name == null) return null;
			if ("javax.xml.stream.isValidating".equals(name))                 return false;
			if ("javax.xml.stream.isNamespaceAware".equals(name))             return true;
			if ("javax.xml.stream.isCoalescing".equals(name))                 return false;
			if ("javax.xml.stream.isReplacingEntityReferences".equals(name))  return false;
			if ("javax.xml.stream.isSupportingExternalEntities".equals(name)) return false;
			if ("javax.xml.stream.supportDTD".equals(name))                   return false;
			return null;
		}
		@Override
		public void close() {
			events = null;
			curr   = -1;
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	static public void setHandleContent(ContentHandle handle, Object content) {
		if (handle == null) {
			return;
		}

		handle.set(content);
	}
	@SuppressWarnings("rawtypes")
	static public void setHandleStructuredFormat(ContentHandle handle, Format format) {
		if (handle == null || format == null) {
			return;
		}

		if (BaseHandle.class.isAssignableFrom(handle.getClass())) {
			setHandleStructuredFormat((BaseHandle) handle, format);
		}
	}
	@SuppressWarnings("rawtypes")
	static public void setHandleStructuredFormat(StructureWriteHandle handle, Format format) {
		if (handle == null || format == null) {
			return;
		}

		if (BaseHandle.class.isAssignableFrom(handle.getClass())) {
			setHandleStructuredFormat((BaseHandle) handle, format);
		}
	}
	@SuppressWarnings("rawtypes") 
	static private void setHandleStructuredFormat(BaseHandle handle, Format format) {
		if (format != Format.JSON && format != Format.XML) {
			throw new IllegalArgumentException("Received "+format.name()+" format instead of JSON or XML");
		}

		handle.setFormat(format);
	}
}
