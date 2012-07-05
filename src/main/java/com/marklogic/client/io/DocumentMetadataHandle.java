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
package com.marklogic.client.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.marklogic.client.Format;
import com.marklogic.client.MarkLogicInternalException;
import com.marklogic.client.NameMap;
import com.marklogic.client.NameMapBase;
import com.marklogic.client.impl.DOMWriter;
import com.marklogic.client.impl.ValueConverter;
import com.marklogic.client.io.marker.BufferableHandle;
import com.marklogic.client.io.marker.DocumentMetadataReadHandle;
import com.marklogic.client.io.marker.DocumentMetadataWriteHandle;

/**
 * A DocumentMetadataHandle represents the metadata for a database document
 * as a POJO (Plain Old Java Object).  
 */
public class DocumentMetadataHandle
	extends BaseHandle<InputStream, OutputStreamSender>
    implements OutputStreamSender, BufferableHandle,
    	DocumentMetadataReadHandle, DocumentMetadataWriteHandle
{
	final static private Logger logger = LoggerFactory.getLogger(DOMHandle.class);

	final static private String REST_API_NS     = "http://marklogic.com/rest-api";
	final static private String PROPERTY_API_NS = "http://marklogic.com/xdmp/property";

	/**
	 * A DocumentCollections represents the collections for a database document
	 * as a POJO (Plain Old Java Object).  
	 */
	public interface DocumentCollections extends Set<String> {
		public void addAll(String... collections);
	}
	private class CollectionsImpl extends HashSet<String> implements DocumentCollections {
	    public void addAll(String... collections) {
	    	if (collections == null || collections.length < 1)
	    		return;

	    	for (String collection: collections)
				add(collection);
		}
	}

	/**
	 * A DocumentPermissions represents the permissions for a database document
	 * as a POJO (Plain Old Java Object).  
	 */
	public interface DocumentPermissions extends Map<String,Set<Capability>> {
	    public void add(String role, Capability... capabilities);
	}
	private class PermissionsImpl extends HashMap<String,Set<Capability>> implements DocumentPermissions {
		public void add(String role, Capability... capabilities) {
			if (capabilities == null || capabilities.length < 1)
				return;

			HashSet<Capability> caps = new HashSet<Capability>(capabilities.length);
			for (Capability capability: capabilities)
				caps.add(capability);

			put(role, caps);
		}
		public void add(String role, Capability capability) {
			if (containsKey(role)) {
				get(role).add(capability);
			} else {
				HashSet<Capability> caps = new HashSet<Capability>();
				caps.add(capability);
				put(role, caps );
			}
		}
	}
	public enum Capability {
	    EXECUTE, INSERT, READ, UPDATE;
	}

	/**
	 * A DocumentProperties represents the properties for a database document
	 * as a POJO (Plain Old Java Object).  
	 */
	public interface DocumentProperties extends NameMap<Object> {
		public Object put(QName name, BigDecimal value);
		public Object put(QName name, BigInteger value);
		public Object put(QName name, Boolean    value);
		public Object put(QName name, Byte       value);
		public Object put(QName name, byte[]     value);
		public Object put(QName name, Calendar   value);
		public Object put(QName name, Double     value);
		public Object put(QName name, Float      value);
		public Object put(QName name, Integer    value);
		public Object put(QName name, Long       value);
		public Object put(QName name, NodeList   value);
		public Object put(QName name, Short      value);
		public Object put(QName name, String     value);
	}
	private class PropertiesImpl extends NameMapBase<Object> implements DocumentProperties {
		private PropertiesImpl() {
			super();
		}

		public Object put(QName name, BigDecimal value) {
			return super.put(name, value);
		}
		public Object put(QName name, BigInteger value) {
			return super.put(name, value);
		}
		public Object put(QName name, Boolean value) {
			return super.put(name, value);
		}
		public Object put(QName name, Byte value) {
			return super.put(name, value);
		}
		public Object put(QName name, byte[] value) {
			return super.put(name, value);
		}
		public Object put(QName name, Calendar value) {
			return super.put(name, value);
		}
		public Object put(QName name, Double value) {
			return super.put(name, value);
		}
		public Object put(QName name, Float value) {
			return super.put(name, value);
		}
		public Object put(QName name, Integer value) {
			return super.put(name, value);
		}
		public Object put(QName name, Long value) {
			return super.put(name, value);
		}
		public Object put(QName name, NodeList value) {
			return super.put(name, value);
		}
		public Object put(QName name, Short value) {
			return super.put(name, value);
		}
		public Object put(QName name, String value) {
			return super.put(name, value);
		}
		@Override
		public Object put(QName name, Object value) {
			if (value instanceof Number || value instanceof Boolean || value instanceof Byte || value instanceof byte[] ||
					value instanceof Calendar || value instanceof NodeList || value instanceof String)
				return super.put(name, value);
			throw new IllegalArgumentException("Invalid value for metadata property "+value.getClass().getName());
		}
	}

	private DocumentCollections collections;
    private DocumentPermissions permissions;
	private DocumentProperties  properties;
	private int                 quality = 0;
	private ValueConverter      converter;

	public DocumentMetadataHandle() {
		super();
		super.setFormat(Format.XML);
	}

	public DocumentCollections getCollections() {
		if (collections == null)
			collections = new CollectionsImpl();
		return collections;
	}
	public void setCollections(DocumentCollections collections) {
		this.collections = collections;
	}

	public DocumentPermissions getPermissions() {
		if (permissions == null)
			permissions = new PermissionsImpl();
		return permissions;
	}
	public void setPermissions(DocumentPermissions permissions) {
		this.permissions = permissions;
	}

	public DocumentProperties getProperties() {
		if (properties == null)
			properties = new PropertiesImpl();
		return properties;
	}
	public void setProperties(DocumentProperties properties) {
		this.properties = properties;
	}

	public int getQuality() {
		return quality;
	}
	public void setQuality(int quality) {
		this.quality = quality;
	}

    @Override
	public void setFormat(Format format) {
		if (format != Format.XML)
			throw new IllegalArgumentException("DocumentMetadataHandle supports the XML format only");
	}

	/**
     * fromBuffer() populates DocumentMetadataHandle from a byte array
     * buffer.  The buffer must store document metadata in XML format
     * in the UTF-8 encoding.
	 */
	@Override
	public void fromBuffer(byte[] buffer) {
		if (buffer == null || buffer.length == 0)
			receiveContent(null);
		else
			receiveContent(new ByteArrayInputStream(buffer));
	}
	@Override
	public byte[] toBuffer() {
		try {
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			write(buffer);

			return buffer.toByteArray();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected Class<InputStream> receiveAs() {
		return InputStream.class;
	}
	@Override
	protected void receiveContent(InputStream content) {
		try {
			if (logger.isInfoEnabled())
				logger.info("Parsing metadata structure from input stream");

			Document document = null;
			if (content != null) {
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				factory.setNamespaceAware(true);
				factory.setValidating(false);
				DocumentBuilder builder = factory.newDocumentBuilder();
				document = builder.parse(new InputSource(new InputStreamReader(content, "UTF-8")));
				content.close();
			}

			receiveMetadataImpl(document);
		} catch (SAXException e) {
			logger.error("Failed to parse metadata structure from input stream",e);
			throw new MarkLogicInternalException(e);
		} catch (IOException e) {
			logger.error("Failed to parse metadata structure from input stream",e);
			throw new MarkLogicInternalException(e);
		} catch (ParserConfigurationException e) {
			logger.error("Failed to parse metadata structure from input stream",e);
			throw new MarkLogicInternalException(e);
		}
	}
	@Override
	protected OutputStreamSender sendContent() {
		return this;
	}
	public void write(OutputStream out) throws IOException {
		sendMetadataImpl(out);
	}

	private void receiveMetadataImpl(Document document) {
		receiveCollectionsImpl(document);
		receivePermissionsImpl(document);
		receivePropertiesImpl(document);
		receiveQualityImpl(document);
	}
	private void receiveCollectionsImpl(Document document) {
		DocumentCollections collections = getCollections();
		collections.clear();

		if (document == null)
			return;
		
		NodeList collectionsIn = document.getElementsByTagNameNS(REST_API_NS, "collection");
		for (int i=0; i < collectionsIn.getLength(); i++) {
			collections.add(collectionsIn.item(i).getTextContent());
		}
	}
	private void receivePermissionsImpl(Document document) {
		DocumentPermissions permissions = getPermissions();
		permissions.clear();

		if (document == null)
			return;

		NodeList permissionsIn = document.getElementsByTagNameNS(REST_API_NS, "permission");
		for (int i=0; i < permissionsIn.getLength(); i++) {
			String roleName = null;
			HashSet<Capability> caps = new HashSet<Capability>();

			NodeList children = permissionsIn.item(i).getChildNodes();
			for (int j=0; j < children.getLength(); j++) {
				Node node = children.item(j);
				if (node.getNodeType() != Node.ELEMENT_NODE)
					continue;
				Element element = (Element) node;

				if ("role-name".equals(element.getLocalName()))
					roleName = element.getTextContent();
				else if ("capability".equals(element.getLocalName()))
					caps.add(Capability.valueOf(element.getTextContent().toUpperCase()));
				else if (logger.isWarnEnabled())
					logger.warn("Skipping unknown permission element", element.getTagName());
			}

			if (roleName == null || caps.size() == 0) {
				if (logger.isWarnEnabled())
					logger.warn("Could not parse permission");
				continue;
			}

			permissions.put(roleName, caps);
		}
	}
	private void receivePropertiesImpl(Document document) {
		DocumentProperties properties = getProperties();
		properties.clear();

		if (document == null)
			return;

		Node propertyContainer = document.getElementsByTagNameNS(PROPERTY_API_NS, "properties").item(0);
		if (propertyContainer == null)
			return;

		NodeList propertiesIn = propertyContainer.getChildNodes();
		for (int i=0; i < propertiesIn.getLength(); i++) {
			Node node = propertiesIn.item(i);
			if (node.getNodeType() != Node.ELEMENT_NODE)
				continue;
			Element property = (Element) node;

			QName propertyName = null;

			String namespaceURI = property.getNamespaceURI();
			if (namespaceURI != null) {
				String prefix    = property.getPrefix();
				if (prefix != null) {
					propertyName = new QName(namespaceURI, property.getLocalName(), prefix);
				} else {
					propertyName = new QName(namespaceURI, property.getTagName());
				}
			} else {
				propertyName = new QName(property.getTagName());
			}

			if (!property.hasChildNodes()) {
				properties.put(propertyName, (String) null);
				continue;
			}

			NodeList children = property.getChildNodes();
			boolean hasChildElements = false;
			int childCount = children.getLength();
			for (int j=0; j < childCount; j++) {
				Node child = children.item(j);
				if (child.getNodeType() == Node.ELEMENT_NODE) {
					hasChildElements = true;
					break;
				}
			}
			if (hasChildElements) {
				properties.put(propertyName, children);
				continue;
			}

			// TODO: casting known properties such as prop:last-modified

			String value = property.getTextContent();
			if (property.hasAttributeNS(
					XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "type")) {
				String type = property.getAttributeNS(
						XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "type");
				if (converter == null)
					converter = new ValueConverter();
				properties.put(propertyName, converter.convertXMLValue(type, value));
				continue;
			} else {
				properties.put(propertyName, value);
			}

			properties.put(propertyName, value);
		}
	}
	private void receiveQualityImpl(Document document) {
		if (document == null) {
			setQuality(0);
			return;
		}

		Node quality = document.getElementsByTagNameNS(REST_API_NS, "quality").item(0);
		if (quality == null) {
			setQuality(0);
			return;
		}

		String qualityText = quality.getTextContent();
		if (qualityText == null) {
			setQuality(0);
			return;
		}

		int qualityNum = 0;
		try {
			qualityNum = Integer.parseInt(qualityText);
			
		} catch(NumberFormatException ex) {
			if (logger.isWarnEnabled())
				logger.warn("Could not parse quality integer from", qualityText);
		}

		setQuality(qualityNum);
	}

	// TODO: select the metadata sent
	private void sendMetadataImpl(OutputStream out) {
		try {
			XMLOutputFactory factory = XMLOutputFactory.newFactory();
			factory.setProperty("javax.xml.stream.isRepairingNamespaces", new Boolean(true));

			XMLStreamWriter serializer = factory.createXMLStreamWriter(out, "UTF-8");

			serializer.setPrefix("rapi", REST_API_NS);
			serializer.setPrefix("prop", PROPERTY_API_NS);
			serializer.setPrefix("xsi",  XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI);
			serializer.setPrefix("xs",   XMLConstants.W3C_XML_SCHEMA_NS_URI);

			serializer.writeStartDocument("utf-8", "1.0");

			serializer.writeStartElement("rapi", "metadata", REST_API_NS);

			sendCollectionsImpl(serializer);
			sendPermissionsImpl(serializer);
			sendPropertiesImpl(serializer);
			sendQualityImpl(serializer);

			serializer.writeEndElement();
			serializer.writeEndDocument();
		} catch (XMLStreamException e) {
			throw new MarkLogicInternalException("Failed to serialize metadata", e);
		} catch (TransformerFactoryConfigurationError e) {
			throw new MarkLogicInternalException("Failed to serialize metadata", e);
		} catch (TransformerException e) {
			throw new MarkLogicInternalException("Failed to serialize metadata", e);
		}
	}
	private void sendCollectionsImpl(XMLStreamWriter serializer) throws XMLStreamException {
		serializer.writeStartElement("rapi", "collections", REST_API_NS);

		for (String collection: getCollections()) {
			serializer.writeStartElement("rapi", "collection", REST_API_NS);
			serializer.writeCharacters(collection);
			serializer.writeEndElement();
		}

		serializer.writeEndElement();
	}
	private void sendPermissionsImpl(XMLStreamWriter serializer) throws XMLStreamException {
		serializer.writeStartElement("rapi", "permissions", REST_API_NS);

		for (Map.Entry<String, Set<Capability>> permission: getPermissions().entrySet()) {
			serializer.writeStartElement("rapi", "permission", REST_API_NS);

			serializer.writeStartElement("rapi", "role-name", REST_API_NS);
			serializer.writeCharacters(permission.getKey());
			serializer.writeEndElement();

			for (Capability capability: permission.getValue()) {
				serializer.writeStartElement("rapi", "capability", REST_API_NS);
				serializer.writeCharacters(capability.name().toLowerCase());
				serializer.writeEndElement();
			}

			serializer.writeEndElement();
		}

		serializer.writeEndElement();
	}
	private void sendPropertiesImpl(XMLStreamWriter serializer) throws XMLStreamException, TransformerFactoryConfigurationError, TransformerException {
		serializer.writeStartElement("prop", "properties", PROPERTY_API_NS);

		for (Map.Entry<QName, Object> property: getProperties().entrySet()) {
			QName  propertyName = property.getKey();
			Object value        = property.getValue();

			boolean hasNodeValue = value instanceof NodeList;

			String namespaceURI = propertyName.getNamespaceURI();
			String prefix       = null;
			String localPart    = propertyName.getLocalPart();
			if (namespaceURI != null && namespaceURI.length() > 0) {
				if (PROPERTY_API_NS.equals(namespaceURI))
					continue;

				prefix = propertyName.getPrefix();

				serializer.writeStartElement(prefix, localPart, namespaceURI);
			} else {
				serializer.writeStartElement(localPart);
			}

			if (!hasNodeValue) {
				if (converter == null)
					converter = new ValueConverter();
				serializer.writeAttribute(
						"xsi",  XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "type",
						converter.convertedJavaType(value));
				serializer.writeCharacters(converter.convertJavaValue(value));
			} else {
				new DOMWriter(serializer).serializeNodeList((NodeList) value);
			}

			serializer.writeEndElement();
		}

		serializer.writeEndElement();
	}
	private void sendQualityImpl(XMLStreamWriter serializer) throws XMLStreamException {
		serializer.writeStartElement("rapi", "quality", REST_API_NS);
		serializer.writeCharacters(String.valueOf(getQuality()));
		serializer.writeEndElement();
	}
}
