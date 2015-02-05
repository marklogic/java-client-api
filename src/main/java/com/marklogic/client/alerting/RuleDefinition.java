/*
 * Copyright 2013-2015 MarkLogic Corporation
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
package com.marklogic.client.alerting;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.marklogic.client.MarkLogicBindingException;
import com.marklogic.client.MarkLogicIOException;
import com.marklogic.client.RequestConstants;
import com.marklogic.client.impl.ClientPropertiesImpl;
import com.marklogic.client.impl.DOMWriter;
import com.marklogic.client.impl.HandleAccessor;
import com.marklogic.client.impl.Utilities;
import com.marklogic.client.impl.ValueConverter;
import com.marklogic.client.io.BaseHandle;
import com.marklogic.client.io.BytesHandle;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.OutputStreamSender;
import com.marklogic.client.io.marker.RuleReadHandle;
import com.marklogic.client.io.marker.RuleWriteHandle;
import com.marklogic.client.io.marker.XMLReadHandle;
import com.marklogic.client.io.marker.XMLWriteHandle;
import com.marklogic.client.util.NameMap;

/**
 * A RuleDefinition represents a set of criteria that describe a named condition.
 */
public class RuleDefinition extends BaseHandle<InputStream, OutputStreamSender>
		implements OutputStreamSender, RuleReadHandle, RuleWriteHandle {

	private static final Logger logger = (Logger) LoggerFactory
			.getLogger(RuleDefinition.class);

	private static XMLOutputFactory factory = XMLOutputFactory.newFactory();
	
	/**
	 * A RuleMetadata represents optional client-supplied metadata that is stored alongside a RuleDefinition.
	 */
	public interface RuleMetadata extends NameMap<Object> {
	}

	@SuppressWarnings("serial")
	private static class RuleMetadataImpl extends ClientPropertiesImpl implements
			RuleMetadata {
		private RuleMetadataImpl() {
			super();
		}

	}

	private ValueSerializer valueSerializer;

	private String name;
	private String description;
	private List<XMLEvent> queryPayload;
	private RuleMetadata metadata;

	/**
	 * Make a new rule definition
	 * 
	 * @param name
	 *            The name of the rule. Should be unique among rule names on the
	 *            REST server.
	 * @param description
	 *            Text description of the rule.
	 */
	public RuleDefinition(String name, String description) {
		this();
		this.setName(name);
		this.setDescription(description);
	}

	/**
	 * Make a new rule definition, no argument constructor.
	 */
	public RuleDefinition() {
		factory.setProperty("javax.xml.stream.isRepairingNamespaces", true);
		this.metadata = new RuleMetadataImpl();
	}

	/**
	 * Sets the name of the rule.
	 * 
	 * @param name
	 *            The rule's name.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the name of the rule.
	 * 
	 * @return The rule's name.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Sets the description of the rule.
	 * 
	 * @param description
	 *            The rule's description.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Returns the rule's definition.
	 * 
	 * @return The definition.
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * Imports an XML combined search definition that defines the matching
	 * criteria for this rule.
	 * 
	 * @param queryDef
	 *            A combined raw query definition serialized as XML.
	 */
	public void importQueryDefinition(XMLWriteHandle queryDef) {
		List<XMLEvent> importedList = Utilities.importFromHandle(queryDef);
		// modify XMLEvent list if the imported XML was a structured query.
		XMLEvent firstEvent = importedList.get(0);
		if (firstEvent.getEventType() ==  XMLStreamConstants.START_ELEMENT) {
			logger.info("Get element.");
			StartElement startElement = firstEvent.asStartElement();
			if (startElement.getName().getNamespaceURI() == RequestConstants.SEARCH_NS &&
					startElement.getName().getLocalPart().equals("query")) {
				logger.info("It's a structured query!!!");
				//wrap in search.
				List<XMLEvent> wrappedList = new ArrayList<XMLEvent>();
				XMLEventFactory  eventFactory = XMLEventFactory.newInstance();
				XMLEvent startSearchElement = eventFactory.createStartElement("search", RequestConstants.SEARCH_NS, "search");
				XMLEvent endSearchElement = eventFactory.createEndElement("search", RequestConstants.SEARCH_NS, "search");
				
				wrappedList.add(startSearchElement);
				wrappedList.addAll(importedList);
				wrappedList.add(endSearchElement);
				this.queryPayload = wrappedList;
			}
			else {
				this.queryPayload = importedList;
			}		
		}
		else {
			logger.warn("Expected imported XML to be an element, but it's not.");
			this.queryPayload = importedList;
		}
			
	}

	/**
	 * Exports the embedded query definitions and options to a handle
	 * 
	 * @param handle
	 *            The handle to use for export.
	 * @return The handle, with a combined query and options included as XML.
	 */
	public <T extends XMLReadHandle> T exportQueryDefinition(T handle) {
		return Utilities.exportToHandle(this.queryPayload, handle);
	}

	/**
	 * Gets the metadata object associated with this rule
	 * 
	 * @return The metadata.
	 */
	public RuleMetadata getMetadata() {
		return metadata;
	}

	/**
	 * Sets the metadata object for this rule.
	 * 
	 * @param metadata
	 *            The metadata
	 */
	public void setMetadata(RuleMetadata metadata) {
		this.metadata = metadata;
	}

	/**
	 * Writes a serialized RuleDefinition to an OutputStream as XML.
	 */
	@Override
	public void write(OutputStream out) throws IOException {
		try {
			valueSerializer = null;

			XMLStreamWriter serializer = factory.createXMLStreamWriter(out,
					"UTF-8");
			serializer.setPrefix(RequestConstants.RESTAPI_PREFIX,
					RequestConstants.RESTAPI_NS);
			serializer.setPrefix(RequestConstants.SEARCH_PREFIX,
					RequestConstants.SEARCH_NS);
			serializer.setPrefix("xsi",
					XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI);
			serializer.setPrefix("xs", XMLConstants.W3C_XML_SCHEMA_NS_URI);

			serializer.writeStartDocument("utf-8", "1.0");

			serializer.writeStartElement(RequestConstants.RESTAPI_PREFIX,
					"rule", RequestConstants.RESTAPI_NS);

			serializer.writeStartElement(RequestConstants.RESTAPI_PREFIX,
					"name", RequestConstants.RESTAPI_NS);
			serializer.writeCharacters(getName());
			serializer.writeEndElement();

			serializer.writeStartElement(RequestConstants.RESTAPI_PREFIX,
					"description", RequestConstants.RESTAPI_NS);
			serializer.writeCharacters(getDescription());
			serializer.writeEndElement();
			serializer.flush();

			// logger.debug("Send: " + new String(queryPayload));
			XMLEventWriter eventWriter = factory.createXMLEventWriter(out);
			for (XMLEvent event : this.queryPayload) {
				eventWriter.add(event);
			}
			eventWriter.flush();
			out.flush();

			writeMetadataElement(serializer);

			serializer.writeEndElement();
			serializer.writeEndDocument();
		} catch (XMLStreamException e) {
			throw new MarkLogicIOException("Failed to serialize rule", e);
		} catch (TransformerFactoryConfigurationError e) {
			throw new MarkLogicIOException("Failed to serialize rule", e);
		} catch (TransformerException e) {
			throw new MarkLogicIOException("Failed to serialize rule", e);
		} finally {
			valueSerializer = null;
		}

	}

	private Element getChildByName(Element element, String ns, String localName) {
		NodeList nl = element.getElementsByTagNameNS(ns, localName);
		if (nl.getLength() == 1) {
			return (Element) nl.item(0);
		} else {
			throw new MarkLogicBindingException(
					"Invalid rule - must have one element only named "
							+ localName);
		}
	}

	protected void receiveContent(InputStream content) {
		DOMHandle handle = new DOMHandle();
		HandleAccessor.receiveContent(handle, content);
		Element ruleElement = handle.get().getDocumentElement();
		receiveElement(ruleElement);
	}

	void receiveElement(Element ruleElement) {
		Element nameElement = getChildByName(ruleElement,
				RequestConstants.RESTAPI_NS, "name");
		this.setName(nameElement.getTextContent());
		Element descElement = getChildByName(ruleElement,
				RequestConstants.RESTAPI_NS, "description");
		this.setDescription(descElement.getTextContent());

		Element searchElement = getChildByName(ruleElement,
				RequestConstants.SEARCH_NS, "search");

		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			TransformerFactory transfac = TransformerFactory.newInstance();
			Transformer trans = transfac.newTransformer();
			trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			trans.setOutputProperty(OutputKeys.VERSION, "1.0");
			trans.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			trans.setOutputProperty(OutputKeys.INDENT, "no");

			trans.transform(new DOMSource(searchElement),
					new StreamResult(baos));
			importQueryDefinition(new BytesHandle(baos.toByteArray())
					.withFormat(Format.XML));
		} catch (TransformerConfigurationException e) {
			throw new MarkLogicIOException(
					"Could not get query from rule payload");
		} catch (TransformerException e) {
			throw new MarkLogicIOException(
					"Could not get query from rule payload");
		}

		receiveRuleMetadataImpl(ruleElement);
	}

	private void receiveRuleMetadataImpl(Element ruleElement) {
		RuleMetadata ruleMetadata = getMetadata();
		ruleMetadata.clear();

		Node metadataContainer = ruleElement.getElementsByTagNameNS(
				RequestConstants.RESTAPI_NS, "rule-metadata").item(0);
		if (metadataContainer == null)
			return;

		NodeList metadataIn = metadataContainer.getChildNodes();
		for (int i = 0; i < metadataIn.getLength(); i++) {
			Node node = metadataIn.item(i);
			if (node.getNodeType() != Node.ELEMENT_NODE)
				continue;
			Element metadataElement = (Element) node;

			QName metadataPropertyName = null;

			String namespaceURI = metadataElement.getNamespaceURI();
			if (namespaceURI != null) {
				String prefix = metadataElement.getPrefix();
				if (prefix != null) {
					metadataPropertyName = new QName(namespaceURI,
							metadataElement.getLocalName(), prefix);
				} else {
					metadataPropertyName = new QName(namespaceURI,
							metadataElement.getTagName());
				}
			} else {
				metadataPropertyName = new QName(metadataElement.getTagName());
			}

			if (!metadataElement.hasChildNodes()) {
				metadata.put(metadataPropertyName, (String) null);
				continue;
			}

			NodeList children = metadataElement.getChildNodes();
			boolean hasChildElements = false;
			int childCount = children.getLength();
			for (int j = 0; j < childCount; j++) {
				Node child = children.item(j);
				if (child.getNodeType() == Node.ELEMENT_NODE) {
					hasChildElements = true;
					break;
				}
			}
			if (hasChildElements) {
				metadata.put(metadataPropertyName, children);
				continue;
			}

			String value = metadataElement.getTextContent();
			if (metadataElement.hasAttributeNS(
					XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "type")) {
				String type = metadataElement.getAttributeNS(
						XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "type");
				metadata.put(metadataPropertyName,
						ValueConverter.convertToJava(type, value));
				continue;
			} else {
				metadata.put(metadataPropertyName, value);
			}

			metadata.put(metadataPropertyName, value);
		}
	}

	private void writeMetadataElement(final XMLStreamWriter serializer)
			throws XMLStreamException, TransformerFactoryConfigurationError,
			TransformerException {
		serializer.writeStartElement("rapi", "rule-metadata",
				RequestConstants.RESTAPI_NS);

		for (Map.Entry<QName, Object> metadataProperty : getMetadata()
				.entrySet()) {
			QName propertyName = metadataProperty.getKey();
			Object value = metadataProperty.getValue();

			boolean hasNodeValue = value instanceof NodeList;

			String namespaceURI = propertyName.getNamespaceURI();
			String prefix = null;
			String localPart = propertyName.getLocalPart();
			if (namespaceURI != null && namespaceURI.length() > 0) {
				if (RequestConstants.RESTAPI_NS.equals(namespaceURI))
					continue;

				prefix = propertyName.getPrefix();

				serializer.writeStartElement(prefix, localPart, namespaceURI);
			} else {
				serializer.writeStartElement(localPart);
			}

			if (!hasNodeValue) {
				if (valueSerializer == null)
					valueSerializer = new ValueSerializer(serializer);

				ValueConverter.convertFromJava(value, valueSerializer);
			} else {
				new DOMWriter(serializer).serializeNodeList((NodeList) value);
			}

			serializer.writeEndElement();
		}

		serializer.writeEndElement();
	}

	static private class ValueSerializer implements
			ValueConverter.ValueProcessor {
		private XMLStreamWriter serializer;

		public ValueSerializer(XMLStreamWriter serializer) {
			super();
			this.serializer = serializer;
		}

		@Override
		public void process(Object original, String type, String value) {
			if (original == null)
				return;
			try {
				serializer.writeAttribute("xsi",
						XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "type",
						type);
				serializer.writeCharacters(value);
			} catch (XMLStreamException e) {
				throw new MarkLogicIOException(e);
			}
		}

	}

	protected Class<InputStream> receiveAs() {
		return InputStream.class;
	}

	protected OutputStreamSender sendContent() {
		return this;
	}

}
