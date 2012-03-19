package com.marklogic.client.io;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.marklogic.client.Format;
import com.marklogic.client.impl.jaxb.metadata.CollectionsJAXB;
import com.marklogic.client.impl.jaxb.metadata.MetadataJAXB;
import com.marklogic.client.impl.jaxb.metadata.PermissionJAXB;
import com.marklogic.client.impl.jaxb.metadata.PermissionsJAXB;
import com.marklogic.client.impl.jaxb.metadata.PropertiesJAXB;
import com.marklogic.client.impl.jaxb.security.CapabilityJAXB;
import com.marklogic.client.io.marker.AbstractReadHandle;
import com.marklogic.client.io.marker.AbstractWriteHandle;
import com.marklogic.client.io.marker.DocumentMetadataReadHandle;
import com.marklogic.client.io.marker.DocumentMetadataWriteHandle;

public class DocumentMetadataHandle
    implements
    	DocumentMetadataReadHandle<Object>, DocumentMetadataWriteHandle<MetadataJAXB>
{
	static final private Logger logger = LoggerFactory.getLogger(DOMHandle.class);

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
		public Object put(String name, BigDecimal value);
		public Object put(String name, BigInteger value);
		public Object put(String name, Boolean    value);
		public Object put(String name, Byte       value);
		public Object put(String name, byte[]     value);
		public Object put(String name, Calendar   value);
		public Object put(String name, Double     value);
		public Object put(String name, Float      value);
		public Object put(String name, Integer    value);
		public Object put(String name, Long       value);
		public Object put(String name, NodeList   value);
		public Object put(String name, Short      value);
		public Object put(String name, String     value);
		public <T> T get(QName name, Class<T> as);
		public <T> T get(String name, Class<T> as);
	}
	private class PropertiesImpl extends HashMap<QName,Object> implements DocumentProperties {
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
			throw new RuntimeException("Invalid value for metadata property "+value.getClass().getName());
		}

		// TODO: namespace context for names with prefixes
		public Object put(String name, BigDecimal value) {
			return put(new QName(name), value);
		}
		public Object put(String name, BigInteger value) {
			return put(new QName(name), value);
		}
		public Object put(String name, Boolean value) {
			return put(new QName(name), value);
		}
		public Object put(String name, Byte value) {
			return put(new QName(name), value);
		}
		public Object put(String name, byte[] value) {
			return put(new QName(name), value);
		}
		public Object put(String name, Calendar value) {
			return put(new QName(name), value);
		}
		public Object put(String name, Double value) {
			return put(new QName(name), value);
		}
		public Object put(String name, Float value) {
			return put(new QName(name), value);
		}
		public Object put(String name, Integer value) {
			return put(new QName(name), value);
		}
		public Object put(String name, Long value) {
			return put(new QName(name), value);
		}
		public Object put(String name, NodeList value) {
			return put(new QName(name), value);
		}
		public Object put(String name, Short value) {
			return put(new QName(name), value);
		}
		public Object put(String name, String value) {
			return put(new QName(name), value);
		}

		public <T> T get(QName name, Class<T> as) {
			Object value = get(name);
			if (value == null)
				return null;
			if (value.getClass() == as)
				return (T) value;
			throw new RuntimeException("Cannot get value of "+value.getClass().getName()+" as "+as.getName());
		}
		public <T> T get(String name, Class<T> as) {
			return get(new QName(name), as);
		}
	}

	public DocumentMetadataHandle() {
		super();
	}

	private DocumentCollections collections;
	public DocumentCollections getCollections() {
		if (collections == null)
			collections = new CollectionsImpl();
		return collections;
	}
	public void setCollections(DocumentCollections collections) {
		this.collections = collections;
	}

    private DocumentPermissions permissions;
	public DocumentPermissions getPermissions() {
		if (permissions == null)
			permissions = new PermissionsImpl();
		return permissions;
	}
	public void setPermissions(DocumentPermissions permissions) {
		this.permissions = permissions;
	}

	private DocumentProperties properties;
	public DocumentProperties getProperties() {
		if (properties == null)
			properties = new PropertiesImpl();
		return properties;
	}
	public void setProperties(DocumentProperties properties) {
		this.properties = properties;
	}

	private int quality = 0;
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
			new RuntimeException("MetadataHandle supports the XML format only");
	}

	public Class receiveAs() {
		return MetadataJAXB.class;
	}
	public void receiveContent(Object content) {
		if (content == null)
			return;
		if (!(content instanceof JAXBElement))
			throw new RuntimeException("Content of unknown class "+content.getClass().getName());

		JAXBElement jaxb = (JAXBElement) content;

		Object value = jaxb.getValue();
		if (value == null)
			return;
		if (!(value instanceof MetadataJAXB))
			throw new RuntimeException("Content of unknown class "+value.getClass().getName());

		receiveMetadataImpl((MetadataJAXB) value);
	}
	public MetadataJAXB sendContent() {
		MetadataJAXB metadata = sendMetadataImpl();

		return metadata;
	}

// TODO: select the metadata sent

	private void receiveMetadataImpl(MetadataJAXB in) {
		receiveCollectionsImpl(in.getCollections());
		receivePermissionsImpl(in.getPermissions());
		receivePropertiesImpl(in.getProperties());
		setQuality(in.getQuality());
	}
	private void receiveCollectionsImpl(CollectionsJAXB in) {
		getCollections().clear();
		if (in == null)
			return;

		getCollections().addAll(in.getCollection());
	}
	private void receivePermissionsImpl(PermissionsJAXB in) {
		getPermissions().clear();
		if (in == null)
			return;

		List<PermissionJAXB> permissions = in.getPermission();
		if (permissions.size() < 1)
			return;

		for (PermissionJAXB permission: permissions) {
			List<CapabilityJAXB> capabilities = permission.getCapability();
			if (capabilities.size() < 1)
				continue;

			HashSet<Capability> caps = new HashSet<Capability>(capabilities.size());
			for (CapabilityJAXB capability: capabilities) {
				caps.add(Capability.valueOf(capability.name()));
			}

			getPermissions().put(permission.getRoleName(), caps);
		}
	}
	private void receivePropertiesImpl(PropertiesJAXB in) {
// TEMPORARY
if (true) return;

		DocumentProperties properties = getProperties();
		properties.clear();

		if (in == null)
			return;

		List<Element> propertiesIn = in.getAny();
		if (properties == null || properties.size() < 1)
			return;

		for (Element property: propertiesIn) {
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
			for (int i=0; i < childCount; i++) {
				Node child = children.item(i);
				if (child.getNodeType() == Node.ELEMENT_NODE) {
					hasChildElements = true;
					break;
				}
			}
			if (hasChildElements) {
				properties.put(propertyName, children);
				continue;
			}

			String value = property.getTextContent();
			if (property.hasAttributeNS(
					XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "type")) {
				String type = property.getAttributeNS(
						XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "type");
				properties.put(propertyName, convertXMLValue(type, value));
				continue;
			}

			properties.put(propertyName, value);
		}
	}

	private MetadataJAXB sendMetadataImpl() {
		MetadataJAXB metadataJAXB = new MetadataJAXB();
		CollectionsJAXB collectionsJAXB = sendCollectionsImpl();
		if (collectionsJAXB != null)
			metadataJAXB.setCollections(collectionsJAXB);

		PermissionsJAXB permissionsJAXB = sendPermissionsImpl();
		if (permissionsJAXB != null)
			metadataJAXB.setPermissions(permissionsJAXB);

		PropertiesJAXB propertiesJAXB = sendPropertiesImpl();
		if (propertiesJAXB != null)
			metadataJAXB.setProperties(propertiesJAXB);

		metadataJAXB.setQuality(getQuality());

		return metadataJAXB;
	}
	private CollectionsJAXB sendCollectionsImpl() {
		DocumentCollections collections = getCollections();
		if (collections == null || collections.size() < 1)
			return null;

		CollectionsJAXB collectionsJAXB = new CollectionsJAXB();
		List<String> collectionList = collectionsJAXB.getCollection();
		for (String collection: collections)
			collectionList.add(collection);

		return collectionsJAXB;
	}
	private PermissionsJAXB sendPermissionsImpl() {
		DocumentPermissions permissions = getPermissions();
		if (permissions == null || permissions.size() < 1)
			return null;

		PermissionsJAXB permissionsJAXB = new PermissionsJAXB();

		List<PermissionJAXB> permissionList = permissionsJAXB.getPermission();
		for (Map.Entry<String, Set<Capability>> permission: permissions.entrySet()) {
			PermissionJAXB permissionJAXB = new PermissionJAXB();

			permissionJAXB.setRoleName(permission.getKey());

			List<CapabilityJAXB> capabilityList = permissionJAXB.getCapability();
			for (Capability capability: permission.getValue()) {
				capabilityList.add(CapabilityJAXB.valueOf(capability.name()));
			}

			permissionList.add(permissionJAXB);
		}

		return permissionsJAXB;
	}
	private PropertiesJAXB sendPropertiesImpl() {
		try {
			DocumentProperties properties = getProperties();
			if (properties == null || properties.size() < 1)
				return null;

			Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

			PropertiesJAXB propertiesJAXB = new PropertiesJAXB();

// TEMPORARY
if (true) return propertiesJAXB;

			List<Element> propertyList = propertiesJAXB.getAny();
			for (Map.Entry<QName, Object> property: properties.entrySet()) {
				QName  propertyName = property.getKey();
				Object value        = property.getValue();

				String namespaceURI = propertyName.getNamespaceURI();
				String localPart = propertyName.getLocalPart();
				Element element = null;
				if (namespaceURI != null && namespaceURI.length() > 0) {
					String prefix = propertyName.getPrefix();
					if (prefix != null && prefix.length() > 0) {
						element = document.createElementNS(namespaceURI, prefix+":"+localPart);
					} else {
						element = document.createElementNS(namespaceURI, localPart);
					}
				} else {
					element = document.createElement(localPart);
				}

// failed hack to declare xs namespace
//				element.setAttributeNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:ns", "");

				// TODO: allow for structures and other kinds of DOM representations

				if (value instanceof NodeList) {
					NodeList children = (NodeList) value;
					for (int i=0; i < children.getLength(); i++) {
						element.appendChild(children.item(i));
					}
					propertyList.add(element);
					continue;
				}

				element.setAttributeNS(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI,
						"xsi:type",
						convertedJavaType(value));
				element.setTextContent(convertJavaValue(value));
				propertyList.add(element);
			}

			return propertiesJAXB;
		} catch (ParserConfigurationException e) {
			throw new RuntimeException("Failed to create properties",e);
		}
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
