package com.marklogic.client.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.SAXException;

import com.marklogic.client.Format;
import com.marklogic.client.MarkLogicInternalException;
import com.marklogic.client.impl.BasicXMLSerializer;
import com.marklogic.client.io.marker.DocumentMetadataReadHandle;
import com.marklogic.client.io.marker.DocumentMetadataWriteHandle;
import com.marklogic.client.io.marker.OutputStreamSender;

// import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

public class DocumentMetadataHandle
    implements OutputStreamSender,
    	DocumentMetadataReadHandle<InputStream>, DocumentMetadataWriteHandle<OutputStreamSender>
{
	final static private Logger logger = LoggerFactory.getLogger(DOMHandle.class);

	final static private String REST_API_NS     = "http://marklogic.com/rest-api";
	final static private String PROPERTY_API_NS = "http://marklogic.com/xdmp/property";

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
	public interface DocumentProperties extends Map<QName,Object> {
		public NamespaceContext getNamespaceContext();
		public void setNamespaceContext(NamespaceContext context);

		public boolean containsKey(String key);

		public Object get(String key);

		public <T> T get(QName name, Class<T> as);
		public <T> T get(String name, Class<T> as);

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

		public Object put(String name, Object value);

		public Object remove(String key);
	}
	private class PropertiesImpl extends HashMap<QName,Object> implements DocumentProperties {
		private NamespaceContext context;
		public NamespaceContext getNamespaceContext() {
			return context;
		}
		public void setNamespaceContext(NamespaceContext context) {
			this.context = context;
		}

		private QName makeQName(String name) {
			if (name == null) return null;

			if (name.contains(":")) {
				if (context == null)
					throw new IllegalStateException("No namespace context for resolving key with prefix: "+name);
				String[] parts = name.split(":", 2);
				String prefix = parts[0];
				if (prefix == null)
					throw new IllegalArgumentException("Empty prefix in key: "+name);
				String localPart = parts[1];
				if (localPart == null)
					throw new IllegalArgumentException("Empty local part in key: "+name);
				String uri = context.getNamespaceURI(prefix);
				if (uri == null || XMLConstants.NULL_NS_URI.equals(uri))
					throw new IllegalStateException("No namespace uri defined in context for prefix "+prefix+" of key: "+name);
				return new QName(uri,localPart, prefix);
			} else if (context != null) {
				String uri = context.getNamespaceURI(XMLConstants.DEFAULT_NS_PREFIX);
				if (uri != null && !XMLConstants.NULL_NS_URI.equals(uri))
					return new QName(uri,name);
			}

			return new QName(name);
		}

		public boolean containsKey(String name) {
			return super.containsKey(makeQName(name));
		}

		public Object get(String name) {
			return super.get(makeQName(name));
		}

		public <T> T get(QName name, Class<T> as) {
			Object value = get(name);
			if (value == null)
				return null;
			if (value.getClass() == as)
				return (T) value;
			throw new IllegalArgumentException("Cannot get value of "+value.getClass().getName()+" as "+as.getName());
		}
		public <T> T get(String name, Class<T> as) {
			return get(makeQName(name), as);
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
		public Object put(QName name, Object value) {
			if (value instanceof Number || value instanceof Boolean || value instanceof Byte || value instanceof byte[] ||
					value instanceof Calendar || value instanceof NodeList || value instanceof String)
				return super.put(name, value);
			throw new IllegalArgumentException("Invalid value for metadata property "+value.getClass().getName());
		}

		public Object put(String name, Object value) {
			return put(makeQName(name), value);
		}

		public Object remove(String name) {
			return super.remove(makeQName(name));
		}
	}

	private DocumentCollections collections;
    private DocumentPermissions permissions;
	private DocumentProperties  properties;
	private int                 quality = 0;

	public DocumentMetadataHandle() {
		super();
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

	public Format getFormat() {
		return Format.XML;
	}
	public void setFormat(Format format) {
		if (format != Format.XML)
			new IllegalArgumentException("MetadataHandle supports the XML format only");
	}
	public DocumentMetadataHandle withFormat(Format format) {
		setFormat(format);
		return this;
	}

	public Class<InputStream> receiveAs() {
		return InputStream.class;
	}
	public void receiveContent(InputStream content) {
		try {
			logger.info("Parsing metadata structure from input stream");

			Document document = null;
			if (content != null) {
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				factory.setNamespaceAware(true);
				factory.setValidating(false);
				DocumentBuilder builder = factory.newDocumentBuilder();
				document = builder.parse(content);
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
	public OutputStreamSender sendContent() {
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
				else
					logger.warn("Skipping unknown permission element", element.getTagName());
			}

			if (roleName == null || caps.size() == 0) {
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
				properties.put(propertyName, convertXMLValue(type, value));
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
			logger.warn("Could not parse quality integer from", qualityText);
		}

		setQuality(qualityNum);
	}

	// TODO: select the metadata sent
	private void sendMetadataImpl(OutputStream out) {
		try {
			// org.apache.xml.serialize.XMLSerializer
			BasicXMLSerializer serializer = new BasicXMLSerializer();
			serializer.writeXMLProlog(out);
			serializer.writeContainerOpenStart(out, "rapi:metadata");
			serializer.writeNamespace(out, "rapi", REST_API_NS);
			serializer.writeNamespace(out, "prop", PROPERTY_API_NS);
			serializer.writeNamespace(out, "xsi",  XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI);
			serializer.writeNamespace(out, "xs",   XMLConstants.W3C_XML_SCHEMA_NS_URI);
			serializer.writeOpenEnd(out);

			sendCollectionsImpl(serializer, out);
			sendPermissionsImpl(serializer, out);
			sendPropertiesImpl(serializer, out);
			sendQualityImpl(serializer, out);

			serializer.writeClose(out, "rapi:metadata");
		} catch (IOException e) {
			throw new MarkLogicInternalException("Failed to serialize metadata", e);
		}
	}
	private void sendCollectionsImpl(BasicXMLSerializer serializer, OutputStream out) throws IOException {
		serializer.writeContainerOpen(out, "rapi:collections");

		for (String collection: getCollections()) {
			serializer.writeOpen(out, "rapi:collection");
			serializer.writeText(out, collection);
			serializer.writeClose(out, "rapi:collection");
		}

		serializer.writeClose(out, "rapi:collections");
	}
	private void sendPermissionsImpl(BasicXMLSerializer serializer, OutputStream out) throws IOException {
		serializer.writeContainerOpen(out, "rapi:permissions");

		for (Map.Entry<String, Set<Capability>> permission: getPermissions().entrySet()) {
			serializer.writeContainerOpen(out, "rapi:permission");

			serializer.writeOpen(out, "rapi:role-name");
			serializer.writeText(out, permission.getKey());
			serializer.writeClose(out, "rapi:role-name");

			for (Capability capability: permission.getValue()) {
				serializer.writeOpen(out, "rapi:capability");
				serializer.writeText(out, capability.name().toLowerCase());
				serializer.writeClose(out, "rapi:capability");
			}

			serializer.writeClose(out, "rapi:permission");
		}

		serializer.writeClose(out, "rapi:permissions");
	}
	private void sendPropertiesImpl(BasicXMLSerializer serializer, OutputStream out) throws IOException {
		serializer.writeContainerOpen(out, "prop:properties");

		LSOutput     domOutput     = null;
		LSSerializer domSerializer = null;
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

					serializer.writeOpenStart(out, prefix, localPart);
					serializer.writeNamespace(out, prefix, namespaceURI);
				} else {
					serializer.writeOpenStart(out, localPart);
				}

				if (!hasNodeValue) {
					serializer.writeEscapedAttribute(out, "xsi:type", convertedJavaType(value));
				}

				serializer.writeOpenEnd(out);

				if (!hasNodeValue) {
					serializer.writeText(out, convertJavaValue(value));
				} else {
					NodeList children = (NodeList) value;
					for (int i=0; i < children.getLength(); i++) {
						Node node = children.item(i);

						if (domOutput == null || domSerializer == null) {
							Document document = (node instanceof Document) ? (Document) node : node.getOwnerDocument();

							DOMImplementationLS domImpl = (DOMImplementationLS) document.getImplementation();

							domOutput = domImpl.createLSOutput();
							domOutput.setByteStream(out);
							domSerializer = domImpl.createLSSerializer();
							domSerializer.getDomConfig().setParameter("xml-declaration", false);
						}

						domSerializer.write(node, domOutput);
					}
				}

				serializer.writeClose(out, prefix, localPart);
		}

		serializer.writeClose(out, "prop:properties");
	}
	private void sendQualityImpl(BasicXMLSerializer serializer, OutputStream out) throws IOException {
		serializer.writeOpen(out, "rapi:quality");
		serializer.writeText(out, String.valueOf(getQuality()));
		serializer.writeClose(out, "rapi:quality");
	}

	// TODO: move to utility class
	private Object convertXMLValue(String type, String value) {
		// TODO: supplement JAXB conversion with javax.xml.datatype.*
		if ("xs:anySimpleType".equals(type))
			return javax.xml.bind.DatatypeConverter.parseAnySimpleType(value);
		if ("xs:base64Binary".equals(type))
			return javax.xml.bind.DatatypeConverter.parseBase64Binary(value);
		if ("xs:boolean".equals(type))
			return javax.xml.bind.DatatypeConverter.parseBoolean(value);
		if ("xs:byte".equals(type))
			return javax.xml.bind.DatatypeConverter.parseByte(value);
		if ("xs:date".equals(type))
			return javax.xml.bind.DatatypeConverter.parseDate(value);
		if ("xs:decimal".equals(type))
			return javax.xml.bind.DatatypeConverter.parseDecimal(value);
		if ("xs:double".equals(type))
			return javax.xml.bind.DatatypeConverter.parseDouble(value);
		if ("xs:float".equals(type))
			return javax.xml.bind.DatatypeConverter.parseFloat(value);
		if ("xs:hexBinary".equals(type))
			return javax.xml.bind.DatatypeConverter.parseHexBinary(value);
		if ("xs:int".equals(type))
			return javax.xml.bind.DatatypeConverter.parseInt(value);
		if ("xs:integer".equals(type))
			return javax.xml.bind.DatatypeConverter.parseInteger(value);
		if ("xs:long".equals(type))
			return javax.xml.bind.DatatypeConverter.parseLong(value);
		// TODO: QName
		if ("xs:short".equals(type))
			return javax.xml.bind.DatatypeConverter.parseShort(value);
		if ("xs:string".equals(type))
			return javax.xml.bind.DatatypeConverter.parseString(value);
		if ("xs:time".equals(type))
			return javax.xml.bind.DatatypeConverter.parseTime(value);
		if ("xs:unsignedInt".equals(type))
			return javax.xml.bind.DatatypeConverter.parseUnsignedInt(value);
		if ("xs:unsignedLong".equals(type))
			// JAXB doesn't provide parseUnsignedLong()
			return javax.xml.bind.DatatypeConverter.parseInteger(value);
		if ("xs:unsignedShort".equals(type))
			return javax.xml.bind.DatatypeConverter.parseUnsignedShort(value);
		return value;
	}
	private String convertedJavaType(Object value) {
		// maintain in parallel with convertJavaValue()
		if (value instanceof byte[])
			return "xs:base64Binary";
		if (value instanceof Boolean)
			return "xs:boolean";
		if (value instanceof Byte)
			return "xs:byte";
		if (value instanceof Calendar)
			return "xs:datetime";
		if (value instanceof BigDecimal)
			return "xs:decimal";
		if (value instanceof Double)
			return "xs:double";
		if (value instanceof Float)
			return "xs:float";
		if (value instanceof Integer)
			return "xs:int";
		if (value instanceof BigInteger)
			return "xs:integer";
		if (value instanceof Long)
			return "xs:long";
		if (value instanceof Short)
			return "xs:short";
		if (value instanceof String)
			return "xs:string";
		return "xs:string";
	}
	private String convertJavaValue(Object value) {
		// TODO: supplement JAXB conversion with javax.xml.datatype.*
		// TODO: distinguish base64Binary from hexBinary
		if (value instanceof byte[])
			return javax.xml.bind.DatatypeConverter.printBase64Binary((byte[]) value);
		if (value instanceof Boolean)
			return javax.xml.bind.DatatypeConverter.printBoolean((Boolean) value);
		if (value instanceof Byte)
			return javax.xml.bind.DatatypeConverter.printByte((Byte) value);
		// TODO: support Date, distinguish datetime, date, and time Calendars
		if (value instanceof Calendar)
			return javax.xml.bind.DatatypeConverter.printDateTime((Calendar) value);
		if (value instanceof BigDecimal)
			return javax.xml.bind.DatatypeConverter.printDecimal((BigDecimal) value);
		if (value instanceof Double)
			return javax.xml.bind.DatatypeConverter.printDouble((Double) value);
		if (value instanceof Float)
			return javax.xml.bind.DatatypeConverter.printFloat((Float) value);
		// TODO: distinguish unsigned short from integer
		if (value instanceof Integer)
			return javax.xml.bind.DatatypeConverter.printInt((Integer) value);
		// TODO: distinguish integer from unsigned long
		if (value instanceof BigInteger)
			return javax.xml.bind.DatatypeConverter.printInteger((BigInteger) value);
		// TODO: distinguish long from unsigned int
		if (value instanceof Long)
			return javax.xml.bind.DatatypeConverter.printLong((Long) value);
		// TODO: QName
		if (value instanceof Short)
			return javax.xml.bind.DatatypeConverter.printShort((Short) value);
		if (value instanceof String)
			return javax.xml.bind.DatatypeConverter.printString((String) value);
		return value.toString();
	}
}
