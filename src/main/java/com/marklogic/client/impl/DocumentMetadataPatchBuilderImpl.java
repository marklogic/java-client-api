/*
 * Copyright 2013 MarkLogic Corporation
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

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.bind.DatatypeConverter;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.marklogic.client.MarkLogicIOException;
import com.marklogic.client.document.DocumentMetadataPatchBuilder;
import com.marklogic.client.document.DocumentManager.Metadata;
import com.marklogic.client.io.DocumentMetadataHandle.Capability;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.io.marker.DocumentPatchHandle;
import com.marklogic.client.util.IterableNamespaceContext;

class DocumentMetadataPatchBuilderImpl
implements DocumentMetadataPatchBuilder
{
	final static protected String REST_API_NS     = "http://marklogic.com/rest-api";
	final static protected String PROPERTY_API_NS = "http://marklogic.com/xdmp/property";

	static class XMLOutputSerializer {
		private StringWriter    writer;
		private XMLStreamWriter serializer;

		XMLOutputSerializer(StringWriter writer, XMLStreamWriter serializer) throws XMLStreamException {
			super();
			this.writer     = writer;
			this.serializer = serializer;
		}
		StringWriter getWriter() throws XMLStreamException {
			return writer;
		}
		XMLStreamWriter getSerializer() {
			return serializer;
		}
	}

	static abstract class PatchOperation implements ValueConverter.ValueProcessor {
		private XMLOutputSerializer out;

		abstract public void write(JSONStringWriter serializer);
		abstract public void write(XMLOutputSerializer out) throws Exception;

		public void writeDelete(JSONStringWriter serializer, String select) {
			serializer.writeStartObject();
			serializer.writeStartEntry("delete");
			serializer.writeStartObject();
			serializer.writeStartEntry("select");
			serializer.writeStringValue(select);
			serializer.writeEndObject();
			serializer.writeEndObject();
		}
		public void writeDelete(XMLOutputSerializer out, String select) throws Exception {
			XMLStreamWriter serializer = out.getSerializer();
			serializer.writeStartElement("rapi", "delete", REST_API_NS);
			serializer.writeAttribute("select",  select);
			serializer.writeEndElement();
		}
		public void writeStartInsert(JSONStringWriter serializer, String context, String position) {
			serializer.writeStartObject();
			serializer.writeStartEntry("insert");
			serializer.writeStartObject();
			serializer.writeStartEntry("context");
			serializer.writeStringValue(context);
			serializer.writeStartEntry("position");
			serializer.writeStringValue(position);
		}
		public void writeStartInsert(XMLOutputSerializer out, String context, String position)
		throws Exception {
			XMLStreamWriter serializer = out.getSerializer();
			serializer.writeStartElement("rapi", "insert", REST_API_NS);
			serializer.writeAttribute("context",  context);
			serializer.writeAttribute("position", position);
		}
		public void writeStartReplace(JSONStringWriter serializer, String select) {
			serializer.writeStartObject();
			serializer.writeStartEntry("replace");
			serializer.writeStartObject();
			serializer.writeStartEntry("select");
			serializer.writeStringValue(select);
		}
		public void writeStartReplace(XMLOutputSerializer out, String select) throws Exception {
			XMLStreamWriter serializer = out.getSerializer();
			serializer.writeStartElement("rapi", "replace", REST_API_NS);
			serializer.writeAttribute("select",  select);
		}
		public void writeStartReplaceInsert(
				JSONStringWriter serializer, String select, String context, String position
		) {
			serializer.writeStartObject();
			serializer.writeStartEntry("replace-insert");
			serializer.writeStartObject();
			serializer.writeStartEntry("select");
			serializer.writeStringValue(select);
			serializer.writeStartEntry("context");
			serializer.writeStringValue(context);
			serializer.writeStartEntry("position");
			serializer.writeStringValue(position);
		}
		public void writeStartReplaceInsert(
				XMLOutputSerializer out, String select, String context, String position
		) throws Exception {
			XMLStreamWriter serializer = out.getSerializer();
			serializer.writeStartElement("rapi",  "replace-insert", REST_API_NS);
			serializer.writeAttribute("select",   select);
			serializer.writeAttribute("context",  context);
			serializer.writeAttribute("position", position);
		}
		public void writeStartReplaceApply(JSONStringWriter serializer, String select, String function) {
			serializer.writeStartObject();
			serializer.writeStartEntry("replace");
			serializer.writeStartObject();
			serializer.writeStartEntry("select");
			serializer.writeStringValue(select);
			serializer.writeStartEntry("apply");
			serializer.writeStringValue(function);
		}
		public void writeStartReplaceApply(XMLOutputSerializer out, String select, String function)
		throws Exception {
			XMLStreamWriter serializer = out.getSerializer();
			serializer.writeStartElement("rapi",  "replace-apply", REST_API_NS);
			serializer.writeAttribute("select",   select);
			serializer.writeAttribute("apply",    function);
		}

		public void writeStartElement(XMLOutputSerializer out, QName qname, String name)
		throws Exception {
			XMLStreamWriter serializer = out.getSerializer();
			if (qname != null) {
				String nsUri = qname.getNamespaceURI();
				if (nsUri != null && nsUri.length() > 0) {
					serializer.writeStartElement(
							qname.getPrefix(),qname.getLocalPart(),nsUri
							);
				} else {
					serializer.writeStartElement(qname.getLocalPart());
				}				
			} else {
				serializer.writeStartElement(name);
			}
		}

		public String getLexicalName(QName qname, String name) {
			if (qname == null) {
				return name;
			} else {
				String prefix = qname.getPrefix();
				return (prefix == null) ?
						qname.getLocalPart() : prefix+":"+qname.getLocalPart();
			}
		}

		protected void convertFromJava(XMLOutputSerializer out, Object value) {
			this.out = out;
			ValueConverter.convertFromJava(value, this);
			this.out = null;
		}

		@Override
		public void process(Object original, String type, String value) {
			XMLStreamWriter serializer = out.getSerializer();
			if (original == null)
				return;
			try {
				serializer.writeAttribute(
					"xsi",  XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "type", type);
				serializer.writeCharacters(value);
			} catch(XMLStreamException e) {
				throw new MarkLogicIOException(e);
			}
		}
	}

	static class AddCollectionOperation extends PatchOperation {
		String collection;
		AddCollectionOperation(String collection) {
			super();
			this.collection = collection;
		}
		@Override
		public void write(JSONStringWriter serializer) {
			writeStartInsert(serializer, "$.collections", "last-child");
			serializer.writeStartEntry("content");
			serializer.writeStringValue(collection);
			serializer.writeEndObject();
			serializer.writeEndObject();
		}
		@Override
		public void write(XMLOutputSerializer out) throws Exception {
			XMLStreamWriter serializer = out.getSerializer();
			writeStartInsert(out, "/rapi:metadata/rapi:collections", "last-child");
			serializer.writeStartElement("rapi", "collection", REST_API_NS);
			serializer.writeCharacters(collection);
			serializer.writeEndElement();
			serializer.writeEndElement();
		}
	}
	static class DeleteCollectionOperation extends PatchOperation {
		String collection;
		DeleteCollectionOperation(String collection) {
			super();
			this.collection = collection;
		}
		@Override
		public void write(JSONStringWriter serializer) {
			writeDelete(serializer, "$.collections[*][?(@="+JSONStringWriter.toJSON(collection)+")]");
		}
		@Override
		public void write(XMLOutputSerializer out) throws Exception {
			writeDelete(out,
					"/rapi:metadata/rapi:collections/rapi:collection[.='"+
					DatatypeConverter.printString(collection)+
					"']"
					);
		}
	}
	static class ReplaceCollectionOperation extends PatchOperation {
		String oldCollection;
		String newCollection;
		ReplaceCollectionOperation(String oldCollection, String newCollection) {
			super();
			this.oldCollection = oldCollection;
			this.newCollection = newCollection;
		}
		@Override
		public void write(JSONStringWriter serializer) {
			writeStartReplace(serializer, "$.collections[*][?(@="+JSONStringWriter.toJSON(oldCollection)+")]");
			serializer.writeStartEntry("content");
			serializer.writeStringValue(newCollection);
			serializer.writeEndObject();
			serializer.writeEndObject();
		}
		@Override
		public void write(XMLOutputSerializer out) throws Exception {
			XMLStreamWriter serializer = out.getSerializer();
			writeStartReplace(out,
					"/rapi:metadata/rapi:collections/rapi:collection[.='"+oldCollection+"']"
					);
			serializer.writeStartElement("rapi", "collection", REST_API_NS);
			serializer.writeCharacters(newCollection);
			serializer.writeEndElement();
			serializer.writeEndElement();
		}
	}

	static class AddPermissionOperation extends PatchOperation {
		String       role;
		Capability[] capabilities;
		AddPermissionOperation(String role, Capability... capabilities) {
			super();
			this.role         = role;
			this.capabilities = capabilities;
		}
		@Override
		public void write(JSONStringWriter serializer) {
			writeStartInsert(serializer, "$.permissions", "last-child");
			serializer.writeStartEntry("content");
			serializer.writeStartObject();
			serializer.writeStartEntry("role-name");
			serializer.writeStringValue(role);
			serializer.writeStartEntry("capabilities");
			serializer.writeStartArray();
			for (Capability capability: capabilities) {
				serializer.writeStartItem();
				serializer.writeStringValue(capability.toString().toLowerCase());
			}
			serializer.writeEndArray();
			serializer.writeEndObject();
			serializer.writeEndObject();
			serializer.writeEndObject();
		}
		@Override
		public void write(XMLOutputSerializer out) throws Exception {
			XMLStreamWriter serializer = out.getSerializer();
			writeStartInsert(out, "/rapi:metadata/rapi:permissions", "last-child");
			serializer.writeStartElement("rapi", "permission", REST_API_NS);

			serializer.writeStartElement("rapi", "role-name", REST_API_NS);
			serializer.writeCharacters(role);
			serializer.writeEndElement();

			for (Capability capability: capabilities) {
				serializer.writeStartElement("rapi", "capability", REST_API_NS);
				serializer.writeCharacters(capability.toString().toLowerCase());
				serializer.writeEndElement();
			}

			serializer.writeEndElement();
			serializer.writeEndElement();
		}
	}
	static class DeletePermissionOperation extends PatchOperation {
		String role;
		DeletePermissionOperation(String role) {
			super();
			this.role = role;
		}
		@Override
		public void write(JSONStringWriter serializer) {
			writeDelete(serializer, "$.permissions.[*][?(@[\"role-name\"]="+JSONStringWriter.toJSON(role)+")]");
		}
		@Override
		public void write(XMLOutputSerializer out) throws Exception {
			writeDelete(out,
					"/rapi:metadata/rapi:permissions/rapi:permission[rapi:role-name='"+role+"']"
					);
		}
	}
	static class ReplacePermissionOperation extends PatchOperation {
		String       oldRole;
		String       newRole;
		Capability[] newCapabilities;
		ReplacePermissionOperation(String oldRole, String newRole, Capability... newCapabilities) {
			super();
			this.oldRole         = oldRole;
			this.newRole         = newRole;
			this.newCapabilities = newCapabilities;
		}
		@Override
		public void write(JSONStringWriter serializer) {
			writeStartReplace(serializer, "$.permissions.[*][?(@[\"role-name\"]="+JSONStringWriter.toJSON(oldRole)+")]");
			serializer.writeStartEntry("content");
			serializer.writeStartObject();
			serializer.writeStartEntry("role-name");
			serializer.writeStringValue(newRole);
			serializer.writeStartEntry("capabilities");
			serializer.writeStartArray();
			for (Capability capability: newCapabilities) {
				serializer.writeStartItem();
				serializer.writeStringValue(capability.toString().toLowerCase());
			}
			serializer.writeEndArray();
			serializer.writeEndObject();
			serializer.writeEndObject();
			serializer.writeEndObject();
		}
		@Override
		public void write(XMLOutputSerializer out) throws Exception {
			XMLStreamWriter serializer = out.getSerializer();
			writeStartReplace(out,
					"/rapi:metadata/rapi:permissions/rapi:permission[rapi:role-name='"+oldRole+"']"
					);
			serializer.writeStartElement("rapi", "permission", REST_API_NS);

			serializer.writeStartElement("rapi", "role-name", REST_API_NS);
			serializer.writeCharacters(newRole);
			serializer.writeEndElement();

			for (Capability capability: newCapabilities) {
				serializer.writeStartElement("rapi", "capability", REST_API_NS);
				serializer.writeCharacters(capability.toString().toLowerCase());
				serializer.writeEndElement();
			}

			serializer.writeEndElement();
			serializer.writeEndElement();
		}
	}

	static class AddPropertyOperation extends PatchOperation {
		QName  qname;
		String name;
		Object value;
		AddPropertyOperation(Object value) {
			super();
			this.value = value;
		}
		AddPropertyOperation(String name, Object value) {
			this(value);
			this.name = name;
		}
		AddPropertyOperation(QName qname, Object value) {
			this(value);
			this.qname = qname;
		}
		@Override
		public void write(JSONStringWriter serializer) {
			// TODO: error if name empty
			writeStartInsert(serializer, "$.properties", "last-child");
			serializer.writeStartEntry("content");
			serializer.writeStartObject();
			serializer.writeStartEntry(name);
			// TODO: typed
			serializer.writeStringValue(value.toString());
			serializer.writeEndObject();
			serializer.writeEndObject();
			serializer.writeEndObject();
		}
		@Override
		public void write(XMLOutputSerializer out) throws Exception {
			XMLStreamWriter serializer = out.getSerializer();
			writeStartInsert(out, "/rapi:metadata/prop:properties", "last-child");

			// TODO: declare namespace on root
			writeStartElement(out, qname, name);
			convertFromJava(out, value);
			serializer.writeEndElement();

			serializer.writeEndElement();
		}
	}
	static class DeletePropertyOperation extends PatchOperation {
		QName  qname;
		String name;
		DeletePropertyOperation() {
			super();
		}
		DeletePropertyOperation(String name) {
			this();
			this.name = name;
		}
		DeletePropertyOperation(QName qname) {
			this();
			this.qname = qname;
		}
		@Override
		public void write(JSONStringWriter serializer) {
			// TODO: error if name empty
			writeDelete(serializer, "$.properties.["+JSONStringWriter.toJSON(name)+"]");
		}
		@Override
		public void write(XMLOutputSerializer out) throws Exception {
			// TODO: declare namespace on root
			writeDelete(out, "/rapi:metadata/prop:properties/"+
					getLexicalName(qname, name));
		}
	}
	static class ReplacePropertyOperation extends PatchOperation {
		QName  oldQName;
		QName  newQName;
		String oldName;
		String newName;
		Object newValue;
		ReplacePropertyOperation(Object newValue) {
			super();
			this.newValue = newValue;
		}
		ReplacePropertyOperation(String oldName, String newName, Object newValue) {
			this(newValue);
			this.oldName = oldName;
			this.newName = newName;
		}
		ReplacePropertyOperation(QName oldQName, QName newQName, Object newValue) {
			this(newValue);
			this.oldQName = oldQName;
			this.newQName = newQName;
		}
		@Override
		public void write(JSONStringWriter serializer) {
			// TODO: error if name empty
			writeStartReplace(serializer, "$.properties.["+JSONStringWriter.toJSON(oldName)+"]");
			serializer.writeStartEntry("content");
			serializer.writeStartObject();
			serializer.writeStartEntry(newName);
			serializer.writeStringValue(newValue.toString());
			serializer.writeEndObject();
			serializer.writeEndObject();
			serializer.writeEndObject();
		}
		@Override
		public void write(XMLOutputSerializer out) throws Exception {
			// TODO: declare namespace on root
			XMLStreamWriter serializer = out.getSerializer();
			writeStartReplace(out, "/rapi:metadata/prop:properties/" +
					getLexicalName(oldQName, oldName));

			// TODO: declare namespace on root
			writeStartElement(out, newQName, newName);
			convertFromJava(out, newValue);
			serializer.writeEndElement();

			serializer.writeEndElement();
		}
	}

	static class SetQualityOperation extends PatchOperation {
		int quality;
		SetQualityOperation(int quality) {
			super();
			this.quality = quality;
		}
		@Override
		public void write(JSONStringWriter serializer) {
			writeStartReplace(serializer, "$.quality");
			serializer.writeStartEntry("content");
			serializer.writeNumberValue(quality);
			serializer.writeEndObject();
			serializer.writeEndObject();
		}
		@Override
		public void write(XMLOutputSerializer out) throws Exception {
			XMLStreamWriter serializer = out.getSerializer();
			writeStartReplace(out, "/rapi:metadata/rapi:quality");
			serializer.writeCharacters(String.valueOf(quality));
			serializer.writeEndElement();
		}
	}

	static class DocumentPatchHandleImpl extends StringHandle {
		private Set<Metadata> metadata;
		DocumentPatchHandleImpl(Set<Metadata> metadata) {
			super();
			this.metadata = metadata;
		}
		Set<Metadata> getMetadata() {
			return metadata;
		}
	}

	protected List<PatchOperation>      operations       = new ArrayList<PatchOperation>();
	protected IterableNamespaceContext  namespaceContext;
	protected String                    library;
	protected Format                    format;
	protected Set<Metadata>             processedMetadata;

	DocumentMetadataPatchBuilderImpl(Format format) {
		super();
		this.format = format;
	}

	@Override
	public DocumentMetadataPatchBuilder addCollection(String... collections) {
		onCollections();
		for (String collection: collections) {
			operations.add(new AddCollectionOperation(collection));
		}
		return this;
	}
	@Override
	public DocumentMetadataPatchBuilder deleteCollection(String... collections) {
		onCollections();
		for (String collection: collections) {
			operations.add(new DeleteCollectionOperation(collection));
		}
		return this;
	}
	@Override
	public DocumentMetadataPatchBuilder replaceCollection(
			String oldCollection, String newCollection
			) {
		onCollections();
		operations.add(new ReplaceCollectionOperation(oldCollection, newCollection));
		return this;
	}

	@Override
	public DocumentMetadataPatchBuilder addPermission(String role, Capability... capabilities) {
		onPermissions();
		operations.add(new AddPermissionOperation(role, capabilities));
		return this;
	}
	@Override
	public DocumentMetadataPatchBuilder deletePermission(String... roles) {
		onPermissions();
		for (String role: roles) {
			operations.add(new DeletePermissionOperation(role));
		}
		return this;
	}
	@Override
	public DocumentMetadataPatchBuilder replacePermission(
			String role, Capability... newCapabilities) {
		onPermissions();
		operations.add(new ReplacePermissionOperation(role, role, newCapabilities));
		return this;
	}
	@Override
	public DocumentMetadataPatchBuilder replacePermission(
			String oldRole, String newRole, Capability... newCapabilities) {
		onPermissions();
		operations.add(new ReplacePermissionOperation(oldRole, newRole, newCapabilities));
		return this;
	}

	@Override
	public DocumentMetadataPatchBuilder addProperty(String name, Object value) {
		onProperties();
		QName qname = asQName(name);
		operations.add(
				(qname != null) ?
				new AddPropertyOperation(qname, value) :
				new AddPropertyOperation(name, value)
				);
		return this;
	}
	@Override
	public DocumentMetadataPatchBuilder addProperty(QName name, Object value) {
		onProperties();
		operations.add(new AddPropertyOperation(name, value));
		return this;
	}
	@Override
	public DocumentMetadataPatchBuilder deleteProperty(String... names) {
		onProperties();
		for (String name: names) {
			QName qname = asQName(name);
			operations.add(
					(qname != null) ?
					new DeletePropertyOperation(qname) :
					new DeletePropertyOperation(name)
					);
		}
		return this;
	}
	@Override
	public DocumentMetadataPatchBuilder deleteProperty(QName... names) {
		onProperties();
		for (QName name: names) {
			operations.add(new DeletePropertyOperation(name));
		}
		return this;
	}
	@Override
	public DocumentMetadataPatchBuilder replaceProperty(String name, Object newValue) {
		onProperties();
		QName qname = asQName(name);
		operations.add(
				(qname != null) ?
				new ReplacePropertyOperation(qname, qname, newValue) :
				new ReplacePropertyOperation(name,  name,  newValue)
				);
		return this;
	}
	@Override
	public DocumentMetadataPatchBuilder replaceProperty(QName name, Object newValue) {
		onProperties();
		operations.add(new ReplacePropertyOperation(name, name, newValue));
		return this;
	}
	@Override
	public DocumentMetadataPatchBuilder replaceProperty(
			String oldName, String newName, Object newValue) {
		onProperties();
		QName oldQName = asQName(oldName);
		QName newQName = asQName(newName);
		operations.add(
				(oldQName != null && newQName != null) ?
				new ReplacePropertyOperation(oldQName, newQName, newValue) :
				new ReplacePropertyOperation(oldName,  newName,  newValue)
				);
		return this;
	}
	@Override
	public DocumentMetadataPatchBuilder replaceProperty(
			QName oldName, QName newName, Object newValue) {
		onProperties();
		operations.add(new ReplacePropertyOperation(oldName, newName, newValue));
		return this;
	}

	@Override
	public DocumentMetadataPatchBuilder setQuality(int quality) {
		onQuality();
		operations.add(new SetQualityOperation(quality));
		return this;
	}

	private void onMetadata(Metadata category) {
		if (processedMetadata == null) {
			processedMetadata = new HashSet<Metadata>();
		} else if (processedMetadata.contains(category)) {
			return;
		}
		processedMetadata.add(category);
	}
	private void onCollections() {
		onMetadata(Metadata.COLLECTIONS);
	}
	private void onPermissions() {
		onMetadata(Metadata.PERMISSIONS);
	}
	private void onProperties() {
		onMetadata(Metadata.PROPERTIES);
	}
	private void onQuality() {
		onMetadata(Metadata.QUALITY);
	}

	@Override
	public DocumentPatchHandle build() throws MarkLogicIOException {
		DocumentPatchHandleImpl handle = new DocumentPatchHandleImpl(processedMetadata);
		if (format == Format.JSON) {
			handle.setFormat(format);

			JSONStringWriter writer = new JSONStringWriter();

			writer.writeStartObject();
			writer.writeStartEntry("patch");

			writer.writeStartArray();
			for (PatchOperation operation: operations) {
				writer.writeStartItem();
				operation.write(writer);
			}
			writer.writeEndArray();

			writer.writeEndObject();
			handle.set(writer.toString());
		} else {
			handle.setFormat(Format.XML);
			try {
				XMLOutputFactory factory = XMLOutputFactory.newInstance();
				factory.setProperty("javax.xml.stream.isRepairingNamespaces", new Boolean(true));

				StringWriter    writer     = new StringWriter();
				XMLStreamWriter serializer = factory.createXMLStreamWriter(writer);
				
				XMLOutputSerializer out = new XMLOutputSerializer(writer, serializer);

				serializer.setPrefix("rapi", REST_API_NS);
				serializer.setPrefix("prop", PROPERTY_API_NS);
				serializer.setPrefix("xsi",  XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI);
				serializer.setPrefix("xs",   XMLConstants.W3C_XML_SCHEMA_NS_URI);
				if (namespaceContext != null) {
					for (String nsPrefix: namespaceContext.getAllPrefixes()) {
						if (nsPrefix != "rapi" && nsPrefix != "prop" && nsPrefix != "xsi" && nsPrefix != "xs")
							serializer.setPrefix(
									nsPrefix, namespaceContext.getNamespaceURI(nsPrefix)
									);
					}
				}

				serializer.writeStartDocument("utf-8", "1.0");

				serializer.writeStartElement("rapi", "patch", REST_API_NS);

				for (PatchOperation operation: operations) {
					operation.write(out);
				}

				serializer.writeEndElement();

				serializer.writeEndDocument();

				serializer.flush();
				serializer.close();

				handle.set(writer.toString());
			} catch (Exception e) {
				throw new MarkLogicIOException(e);
			}
		}

		return handle;
	}

	protected QName asQName(String name) {
		if (namespaceContext == null)
			return null;

		int pos = name.indexOf(":");
		if (pos != -1) {
			String prefix = name.substring(0, pos);
			String nsUri  = namespaceContext.getNamespaceURI(prefix);
			if (!XMLConstants.NULL_NS_URI.equals(nsUri))
				return new QName(nsUri, name.substring(pos + 1), prefix);
			else
				throw new IllegalArgumentException(
						"no namespace binding for prefix: "+prefix);
		} else {
			String nsUri = namespaceContext.getNamespaceURI(
					XMLConstants.DEFAULT_NS_PREFIX);
			if (!XMLConstants.NULL_NS_URI.equals(nsUri))
				return new QName(nsUri, name);
			// otherwise, fall through
		}
		return null;
	}
}
