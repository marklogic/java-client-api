/*
 * Copyright 2013-2014 MarkLogic Corporation
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.bind.DatatypeConverter;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.marklogic.client.MarkLogicIOException;
import com.marklogic.client.document.DocumentManager.Metadata;
import com.marklogic.client.document.DocumentMetadataPatchBuilder;
import com.marklogic.client.document.DocumentPatchBuilder.PathLanguage;
import com.marklogic.client.io.DocumentMetadataHandle.Capability;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.util.EditableNamespaceContext;
import com.marklogic.client.util.IterableNamespaceContext;

class DocumentMetadataPatchBuilderImpl
implements DocumentMetadataPatchBuilder
{
	final static protected String REST_API_NS     = "http://marklogic.com/rest-api";
	final static protected String PROPERTY_API_NS = "http://marklogic.com/xdmp/property";

	final static protected Map<String,String> reserved = new HashMap<String,String>();
	static {
		reserved.put("rapi", REST_API_NS);
		reserved.put("prop", PROPERTY_API_NS);
		reserved.put("xsi",  XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI);
		reserved.put("xs",   XMLConstants.W3C_XML_SCHEMA_NS_URI);
	};

	
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

		public void writeDelete(JSONStringWriter serializer, String select, Cardinality cardinality) {
			serializer.writeStartObject();
			serializer.writeStartEntry("delete");
			serializer.writeStartObject();
			serializer.writeStartEntry("select");
			serializer.writeStringValue(select);
			if (cardinality != null) {
				serializer.writeStartEntry("cardinality");
				serializer.writeStringValue(cardinality.abbreviate());
			}
			serializer.writeEndObject();
			serializer.writeEndObject();
		}
		public void writeDelete(XMLOutputSerializer out, String select, Cardinality cardinality)
		throws Exception {
			XMLStreamWriter serializer = out.getSerializer();
			serializer.writeStartElement("rapi", "delete", REST_API_NS);
			serializer.writeAttribute("select",  select);
			if (cardinality != null) {
				serializer.writeAttribute("cardinality", cardinality.abbreviate());
			}
			serializer.writeEndElement();
		}
		public void writeStartInsert(
				JSONStringWriter serializer, String context, String position, Cardinality cardinality
		) {
			serializer.writeStartObject();
			serializer.writeStartEntry("insert");
			serializer.writeStartObject();
			serializer.writeStartEntry("context");
			serializer.writeStringValue(context);
			serializer.writeStartEntry("position");
			serializer.writeStringValue(position);
			if (cardinality != null) {
				serializer.writeStartEntry("cardinality");
				serializer.writeStringValue(cardinality.abbreviate());
			}
		}
		public void writeStartInsert(
				XMLOutputSerializer out, String context, String position, Cardinality cardinality
		) throws Exception {
			XMLStreamWriter serializer = out.getSerializer();
			serializer.writeStartElement("rapi", "insert", REST_API_NS);
			serializer.writeAttribute("context",  context);
			serializer.writeAttribute("position", position);
			if (cardinality != null) {
				serializer.writeAttribute("cardinality", cardinality.abbreviate());
			}
		}
		public void writeStartReplace(
				JSONStringWriter serializer, String select, Cardinality cardinality
		) {
			serializer.writeStartObject();
			serializer.writeStartEntry("replace");
			serializer.writeStartObject();
			serializer.writeStartEntry("select");
			serializer.writeStringValue(select);
			if (cardinality != null) {
				serializer.writeStartEntry("cardinality");
				serializer.writeStringValue(cardinality.abbreviate());
			}
		}
		public void writeStartReplace(
				XMLOutputSerializer out, String select, Cardinality cardinality
		) throws Exception {
			XMLStreamWriter serializer = out.getSerializer();
			serializer.writeStartElement("rapi", "replace", REST_API_NS);
			serializer.writeAttribute("select",  select);
			if (cardinality != null) {
				serializer.writeAttribute("cardinality", cardinality.abbreviate());
			}
		}
		public void writeStartReplaceInsert(
				JSONStringWriter serializer, String select, String context, String position,
				Cardinality cardinality
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
			if (cardinality != null) {
				serializer.writeStartEntry("cardinality");
				serializer.writeStringValue(cardinality.abbreviate());
			}
		}
		public void writeStartReplaceInsert(
				XMLOutputSerializer out, String select, String context, String position,
				Cardinality cardinality
		) throws Exception {
			XMLStreamWriter serializer = out.getSerializer();
			serializer.writeStartElement("rapi",  "replace-insert", REST_API_NS);
			serializer.writeAttribute("select",   select);
			serializer.writeAttribute("context",  context);
			serializer.writeAttribute("position", position);
			if (cardinality != null) {
				serializer.writeAttribute("cardinality", cardinality.abbreviate());
			}
		}
		public void writeReplaceApply(
				JSONStringWriter serializer, String select, Cardinality cardinality, CallImpl call
		) {
			if (call == null) {
				throw new IllegalArgumentException("Cannot apply a null call to a function");
			}

			serializer.writeStartObject();
			serializer.writeStartEntry("replace");
			serializer.writeStartObject();
			serializer.writeStartEntry("select");
			serializer.writeStringValue(select);
			if (cardinality != null) {
				serializer.writeStartEntry("cardinality");
				serializer.writeStringValue(cardinality.abbreviate());
			}
			serializer.writeStartEntry("apply");
			serializer.writeStringValue(call.function);
			if (call.args != null && call.args.length > 0) {
				serializer.writeStartEntry("content");
				writeCall(serializer, call);
			}
			serializer.writeEndObject();
			serializer.writeEndObject();
		}
		public void writeReplaceApply(
				XMLOutputSerializer out, String select, Cardinality cardinality, CallImpl call
		) throws Exception {
			if (call == null) {
				throw new IllegalArgumentException("Cannot apply a null call to a function");
			}

			XMLStreamWriter serializer = out.getSerializer();

			serializer.writeStartElement("rapi", "replace", REST_API_NS);
			serializer.writeAttribute("select",  select);
			if (cardinality != null) {
				serializer.writeAttribute("cardinality", cardinality.abbreviate());
			}
			serializer.writeAttribute("apply",   call.function);
			if (call.args != null && call.args.length > 0) {
				writeCall(out, call);
			}
			serializer.writeEndElement();
		}
		public void writeCall(JSONStringWriter serializer, CallImpl call) {
			if (call == null) return;

			if (call.isFragment) {
				if (call.args.length == 1) {
					serializer.writeFragment(
							(call.args[0] instanceof String) ?
									(String) call.args[0] : call.args[0].toString()
					);
				} else {
					serializer.writeStartArray();
					for (Object fragment: call.args) {
						serializer.writeFragment(
								(fragment instanceof String) ?
										(String) fragment : fragment.toString()
						);
					}
					serializer.writeEndArray();
				}
			} else {
				// TODO: datatypes
				if (call.args.length == 1) {
					serializer.writeStringValue(call.args[0]);
				} else {
					serializer.writeStartArray();
					for (Object value: call.args) {
						serializer.writeStartObject();
						serializer.writeStartEntry("value");
						serializer.writeStringValue(value);
						serializer.writeEndObject();
					}
					serializer.writeEndArray();
				}
			}
		}
		public void writeCall(XMLOutputSerializer out, CallImpl call) throws Exception {
			if (call == null) return;

			XMLStreamWriter serializer = out.getSerializer();
			if (call.isFragment) {
				serializer.writeCharacters(""); // force the tag close
				for (Object fragment: call.args) {
					out.getWriter().write(
							(fragment instanceof String) ?
									(String) fragment : fragment.toString()
					);
				}
			} else {
				if (call.args.length == 1) {
					convertFromJava(out, call.args[0]);
				} else {
					for (Object value: call.args) {
						serializer.writeStartElement("rapi", "value", REST_API_NS);
						convertFromJava(out, value);
						serializer.writeEndElement();
					}
				}
			}
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
			String pathString = serializer.getPathLanguage() == PathLanguage.JSONPATH ?
					"$.collections" : "/node()/array-node('collections')";
			writeStartInsert(serializer, pathString, "last-child", null);
			serializer.writeStartEntry("content");
			serializer.writeStringValue(collection);
			serializer.writeEndObject();
			serializer.writeEndObject();
		}
		@Override
		public void write(XMLOutputSerializer out) throws Exception {
			XMLStreamWriter serializer = out.getSerializer();
			writeStartInsert(out, "/rapi:metadata/rapi:collections", "last-child", null);
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
			String pathString = serializer.getPathLanguage() == PathLanguage.JSONPATH ?
					"$.collections[*][?(@="+JSONStringWriter.toJSON(collection)+")]" :
					"/node()/array-node('collections')/node()[.="+JSONStringWriter.toJSON(collection)+"]";
			writeDelete(serializer, pathString, null);
		}
		@Override
		public void write(XMLOutputSerializer out) throws Exception {
			writeDelete(out,
					"/rapi:metadata/rapi:collections/rapi:collection[.='"+
						DatatypeConverter.printString(collection)+
					"']",
					null
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
			String pathString = serializer.getPathLanguage() == PathLanguage.JSONPATH ?
					"$.collections[*][?(@="+JSONStringWriter.toJSON(oldCollection)+")]" :
					"/node()/array-node('collections')/node()[.="+JSONStringWriter.toJSON(oldCollection)+"]";
			
			writeStartReplace(serializer,
					pathString,
					null
					);
			serializer.writeStartEntry("content");
			serializer.writeStringValue(newCollection);
			serializer.writeEndObject();
			serializer.writeEndObject();
		}
		@Override
		public void write(XMLOutputSerializer out) throws Exception {
			XMLStreamWriter serializer = out.getSerializer();
			writeStartReplace(out,
					"/rapi:metadata/rapi:collections/rapi:collection[.='"+oldCollection+"']",
					null
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
			String pathString = serializer.getPathLanguage() == PathLanguage.JSONPATH ?
					"$.permissions" :
					"/node()/array-node('permissions')";
			writeStartInsert(serializer, pathString, "last-child", null);
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
			writeStartInsert(out, "/rapi:metadata/rapi:permissions", "last-child", null);
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
			
			String pathString = serializer.getPathLanguage() == PathLanguage.JSONPATH ?
					"$.permissions.[*][?(@.role-name="+JSONStringWriter.toJSON(role)+")]":
					"/node()/array-node('permissions')/object-node()[role-name = "+JSONStringWriter.toJSON(role)+"]";

			writeDelete(serializer,
					pathString,
					null
					);
		}
		@Override
		public void write(XMLOutputSerializer out) throws Exception {
			writeDelete(out,
					"/rapi:metadata/rapi:permissions/rapi:permission[rapi:role-name='"+role+"']",
					null
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
			String pathString = serializer.getPathLanguage() == PathLanguage.JSONPATH ?
					"$.permissions.[*][?(@.role-name="+JSONStringWriter.toJSON(oldRole)+")]":
					"/node()/array-node('permissions')/object-node()[role-name = "+JSONStringWriter.toJSON(oldRole)+"]";

			writeStartReplace(serializer,
					pathString,
					null
					);
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
					"/rapi:metadata/rapi:permissions/rapi:permission[rapi:role-name='"+oldRole+"']",
					null
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
			String pathString = serializer.getPathLanguage() == PathLanguage.JSONPATH ?
					"$.properties" :
					"/node()/node('properties')";

			writeStartInsert(serializer, pathString, "last-child", null);
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
			writeStartInsert(out, "/rapi:metadata/prop:properties", "last-child", null);

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
            String pathString = serializer.getPathLanguage() == PathLanguage.JSONPATH ?
					"$.properties.["+JSONStringWriter.toJSON(name)+"]":
                    "/node()/node('properties')/node()[name(.)="+JSONStringWriter.toJSON(name)+"]";

			writeDelete(serializer,
                    pathString,
					null
					);
		}
		@Override
		public void write(XMLOutputSerializer out) throws Exception {
			// TODO: declare namespace on root
			writeDelete(out,
					"/rapi:metadata/prop:properties/"+getLexicalName(qname, name),
					null
					);
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
            String pathString = serializer.getPathLanguage() == PathLanguage.JSONPATH ?
					"$.properties.["+JSONStringWriter.toJSON(oldName)+"]":
                    "/node()/node('properties')/node()[name(.)="+JSONStringWriter.toJSON(oldName)+"]";
			writeStartReplace(serializer,
					pathString,
					null
					);
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
			writeStartReplace(out,
					"/rapi:metadata/prop:properties/" + getLexicalName(oldQName, oldName),
					null);

			// TODO: declare namespace on root
			writeStartElement(out, newQName, newName);
			convertFromJava(out, newValue);
			serializer.writeEndElement();

			serializer.writeEndElement();
		}
	}
	static class ReplacePropertyApplyOperation extends PatchOperation {
		QName    qname;
		String   name;
		CallImpl call;
		ReplacePropertyApplyOperation(CallImpl call) {
			super();
			this.call = call;
		}
		ReplacePropertyApplyOperation(String name, CallImpl call) {
			this(call);
			this.name = name;
		}
		ReplacePropertyApplyOperation(QName qname, CallImpl call) {
			this(call);
			this.qname = qname;
		}
		@Override
		public void write(JSONStringWriter serializer) {
			// TODO: error if name empty
            String pathString = serializer.getPathLanguage() == PathLanguage.JSONPATH ?
					"$.properties.["+JSONStringWriter.toJSON(name)+"]":
                    "/node()/node('properties')/node()[name(.)="+JSONStringWriter.toJSON(name)+"]";
			writeReplaceApply(serializer, 
					pathString,
					null,
					call
					);
		}
		@Override
		public void write(XMLOutputSerializer out) throws Exception {
			// TODO: declare namespace on root
			writeReplaceApply(out,
					"/rapi:metadata/prop:properties/" + getLexicalName(qname, name),
					null,
					call
					);
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
            String pathString = serializer.getPathLanguage() == PathLanguage.JSONPATH ?
					"$.quality":
                    "/node()/node('quality')";
            
			writeStartReplace(serializer, pathString, null);
			serializer.writeStartEntry("content");
			serializer.writeNumberValue(quality);
			serializer.writeEndObject();
			serializer.writeEndObject();
		}
		@Override
		public void write(XMLOutputSerializer out) throws Exception {
			XMLStreamWriter serializer = out.getSerializer();
			writeStartReplace(out, "/rapi:metadata/rapi:quality", null);
			serializer.writeCharacters(String.valueOf(quality));
			serializer.writeEndElement();
		}
	}

	static class DocumentPatchHandleImpl
	extends StringHandle
	implements PatchHandle
	{
		private Set<Metadata> metadata;
		private boolean       onContent;
		DocumentPatchHandleImpl(Set<Metadata> metadata, boolean onContent) {
			super();
			this.metadata  = metadata;
			this.onContent = onContent;
		}
		Set<Metadata> getMetadata() {
			return metadata;
		}
		public boolean isOnContent() {
			return onContent;
		}
	}

	private CallBuilderImpl callBuilder;

	protected List<PatchOperation>      operations       = new ArrayList<PatchOperation>();
	protected EditableNamespaceContext  namespaces;
	protected String                    libraryNs;
	protected String                    libraryAt;
	protected Format                    format;
	protected Set<Metadata>             processedMetadata;
	protected boolean                   onContent = false;
	protected PathLanguage                  pathLang = PathLanguage.XPATH;

	DocumentMetadataPatchBuilderImpl(Format format) {
		super();
		this.format = format;

		if (format == Format.XML) {
			namespaces = makeNamespaces();
		}
	}

	@Override
	public IterableNamespaceContext getNamespaces() {
		return namespaces;
	}
	@Override
	public void setNamespaces(IterableNamespaceContext namespaces) {
		if (format != Format.XML) {
			throw new IllegalArgumentException(
				"Can specify namespaces only for XML patches");
		}

		EditableNamespaceContext newNamespaces = makeNamespaces();
		if (namespaces != null) {
			for (String prefix: namespaces.getAllPrefixes()) {
				String nsUri = namespaces.getNamespaceURI(prefix);
				if (!newNamespaces.containsKey(prefix)) {
					newNamespaces.put(prefix, nsUri);
				} else if (!nsUri.equals(newNamespaces.getNamespaceURI(prefix))) {
					throw new IllegalArgumentException(
							"Cannot change namespace URI for change prefix: "+prefix);
				}
			}
		}
		this.namespaces = newNamespaces;
	}
	private EditableNamespaceContext makeNamespaces() {
		EditableNamespaceContext newNamespaces = new EditableNamespaceContext();
		for (Map.Entry<String, String> entry: reserved.entrySet()) {
			newNamespaces.put(entry.getKey(), entry.getValue());
		}
		return newNamespaces;
	}

	@Override
	public DocumentMetadataPatchBuilder library(String ns, String at) {
		libraryNs = ns;
		libraryAt = at;
		return this;
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
	public DocumentMetadataPatchBuilder addPropertyValue(String name, Object value) {
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
	public DocumentMetadataPatchBuilder addPropertyValue(QName name, Object value) {
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
	public DocumentMetadataPatchBuilder replacePropertyValue(String name, Object newValue) {
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
	public DocumentMetadataPatchBuilder replacePropertyValue(QName name, Object newValue) {
		onProperties();
		operations.add(new ReplacePropertyOperation(name, name, newValue));
		return this;
	}
	@Override
	public DocumentMetadataPatchBuilder replacePropertyValue(
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
	public DocumentMetadataPatchBuilder replacePropertyValue(
			QName oldName, QName newName, Object newValue) {
		onProperties();
		operations.add(new ReplacePropertyOperation(oldName, newName, newValue));
		return this;
	}
	@Override
	public DocumentMetadataPatchBuilder replacePropertyApply(
			String name, Call call
			) {
		if (!CallImpl.class.isAssignableFrom(call.getClass()))
			throw new IllegalArgumentException(
					"Cannot use external call implementation");
		onProperties();
		operations.add(new ReplacePropertyApplyOperation(name, (CallImpl) call));
		return this;
	}
	@Override
	public DocumentMetadataPatchBuilder replacePropertyApply(
			QName name, Call call
			) {
		if (!CallImpl.class.isAssignableFrom(call.getClass()))
			throw new IllegalArgumentException(
					"Cannot use external call implementation");
		onProperties();
		operations.add(new ReplacePropertyApplyOperation(name, (CallImpl) call));
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
	public PatchHandle build() throws MarkLogicIOException {
		DocumentPatchHandleImpl handle = new DocumentPatchHandleImpl(processedMetadata, onContent);
		if (format == Format.JSON) {
			handle.setFormat(format);

			JSONStringWriter writer = new JSONStringWriter(this.pathLang);

			writer.writeStartObject();
			writer.writeStartEntry("pathlang");
			writer.writeStringValue(pathLang.toString());
			writer.writeStartEntry("patch");
			writer.writeStartArray();

			if (libraryNs != null && libraryAt != null) {
				writer.writeStartObject();
				writer.writeStartEntry("replace-library");
				writer.writeStartObject();
				writer.writeStartEntry("ns");
				writer.writeStringValue(libraryNs);
				writer.writeStartEntry("at");
				writer.writeStringValue(libraryAt);
				writer.writeEndObject();
				writer.writeEndObject();
			}

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
				factory.setProperty("javax.xml.stream.isRepairingNamespaces", true);

				StringWriter    writer     = new StringWriter();
				XMLStreamWriter serializer = factory.createXMLStreamWriter(writer);
				
				XMLOutputSerializer out = new XMLOutputSerializer(writer, serializer);

				for (String nsPrefix: namespaces.getAllPrefixes()) {
					serializer.setPrefix(
						nsPrefix, namespaces.getNamespaceURI(nsPrefix)
						);
				}

				serializer.writeStartDocument("utf-8", "1.0");

				serializer.writeStartElement("rapi", "patch", REST_API_NS);

				if (libraryNs != null && libraryAt != null) {
					serializer.writeStartElement("rapi", "replace-library", REST_API_NS);
					serializer.writeAttribute("ns", libraryNs);
					serializer.writeAttribute("at", libraryAt);
					serializer.writeEndElement();
				}

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
		int pos = name.indexOf(":");
		if (pos != -1) {
			String prefix = name.substring(0, pos);
			String nsUri  = namespaces.getNamespaceURI(prefix);
			if (!XMLConstants.NULL_NS_URI.equals(nsUri))
				return new QName(nsUri, name.substring(pos + 1), prefix);
			else
				throw new IllegalArgumentException(
						"no namespace binding for prefix: "+prefix);
		} else if (namespaces != null) {
			String nsUri = namespaces.getNamespaceURI(
					XMLConstants.DEFAULT_NS_PREFIX);
			if (!XMLConstants.NULL_NS_URI.equals(nsUri))
				return new QName(nsUri, name);
			// otherwise, fall through
		}
		return null;
	}

	@Override
	public CallBuilder call() {
		if (callBuilder == null)
			callBuilder = new CallBuilderImpl();
		return callBuilder;
	}

	static class CallImpl implements Call {
		String   function;
		boolean  isFragment = true;
		Object[] args;
		CallImpl(String function) {
			super();
			this.function = function;
		}
		CallImpl(String function, boolean isFragment, Object... args) {
			this(function);
			this.isFragment = isFragment;
			this.args       = args;
		}
	}
	static class CallBuilderImpl implements CallBuilder {
		CallBuilderImpl() {
			super();
		}
		@Override
		public Call add(Number number) {
			if (number == null)
				throw new IllegalArgumentException("Cannot add null number");
			return new CallImpl("ml.add", false, number);
		}
		@Override
		public Call subtract(Number number) {
			if (number == null)
				throw new IllegalArgumentException("Cannot subtract null number");
			return new CallImpl("ml.subtract", false, number);
		}
		@Override
		public Call multiply(Number number) {
			if (number == null)
				throw new IllegalArgumentException("Cannot multiply null number");
			return new CallImpl("ml.multiply", false, number);
		}
		@Override
		public Call divideBy(Number number) {
			if (number == null)
				throw new IllegalArgumentException("Cannot divide null number");
			return new CallImpl("ml.divide", false, number);
		}

		@Override
		public Call concatenateAfter(String prefix) {
			if (prefix == null)
				throw new IllegalArgumentException(
						"Cannot concatenate after null prefix");
			return new CallImpl("ml.concat-after", false, prefix);
		}
		@Override
		public Call concatenateBetween(String prefix, String suffix) {
			if (prefix == null || suffix == null)
				throw new IllegalArgumentException(
						"Cannot concatenate between null prefix or suffix");
			return new CallImpl("ml.concat-between", false, prefix, suffix);
		}
		@Override
		public Call concatenateBefore(String suffix) {
			if (suffix == null)
				throw new IllegalArgumentException(
						"Cannot concatenate before null suffix");
			return new CallImpl("ml.concat-before", false, suffix);
		}
		@Override
		public Call substringAfter(String prefix) {
			if (prefix == null)
				throw new IllegalArgumentException(
						"Cannot substring after null prefix");
			return new CallImpl("ml.substring-after", false, prefix);
		}
		@Override
		public Call substringBefore(String suffix) {
			if (suffix == null)
				throw new IllegalArgumentException(
						"Cannot substring before null suffix");
			return new CallImpl("ml.substring-before", false, suffix);
		}
		@Override
		public Call replaceRegex(String pattern, String replacement) {
			if (pattern == null || replacement == null)
				throw new IllegalArgumentException(
						"Cannot replace regex with null pattern or replacement");
			return new CallImpl("ml.replace-regex", false, pattern, replacement);
		}
		@Override
		public Call replaceRegex(
				String pattern, String replacement, String flags
				) {
			if (pattern == null || replacement == null || flags == null)
				throw new IllegalArgumentException(
						"Cannot replace regex with null pattern, replacement, or flags");
			return new CallImpl("ml.replace-regex", false, pattern, replacement, flags);
		}

		@Override
		public Call applyLibrary(String function) {
			return new CallImpl(function);
		}
		@Override
		public Call applyLibraryValues(String function, Object... args) {
			return new CallImpl(function, false, args);
		}
		@Override
		public Call applyLibraryFragments(String function, Object... args) {
			return new CallImpl(function, true, args);
		}
	}
}
