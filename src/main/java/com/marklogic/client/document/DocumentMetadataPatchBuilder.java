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
package com.marklogic.client.document;

import javax.xml.namespace.QName;

import com.marklogic.client.MarkLogicIOException;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.marker.DocumentPatchHandle;

/**
 * A DocumentMetadataPatchBuilder specifies changes to the metadata
 * of a database document.  When using the newPatchBuilder() factory
 * method of the DocumentManager to create the builder, you identify
 * whether paths are specified with JSONPath or XPath.
 */
public interface DocumentMetadataPatchBuilder {
	/**
	 * Adds the specified collections.
	 * @param collections	the collection identifiers
	 * @return	the patch builder (for convenient chaining)
	 */
	public DocumentMetadataPatchBuilder addCollection(String... collections);
	/**
	 * Deletes the specified collections.
	 * @param collections	the collection identifiers
	 * @return	the patch builder (for convenient chaining)
	 */
	public DocumentMetadataPatchBuilder deleteCollection(String... collections);
	/**
	 * Replaces the specified collection.
	 * @param oldCollection	the identifier for the existing collection
	 * @param newCollection	the identifier for the new collection
	 * @return	the patch builder (for convenient chaining)
	 */
	public DocumentMetadataPatchBuilder replaceCollection(String oldCollection, String newCollection);

	/**
	 * Adds a role with the specified capabilities
	 * @param role	the name of the role
	 * @param capabilities	the set of capabilities
	 * @return	the patch builder (for convenient chaining)
	 */
	public DocumentMetadataPatchBuilder addPermission(
			String role, DocumentMetadataHandle.Capability... capabilities
			);
	/**
	 * Deletes the specified roles.
	 * @param roles	the names of the roles
	 * @return	the patch builder (for convenient chaining)
	 */
	public DocumentMetadataPatchBuilder deletePermission(String... roles);
	/**
	 * Replaces the existing capabilities of a role.
	 * @param role	the name of the role
	 * @param newCapabilities	the replacing set of capabilities
	 * @return	the patch builder (for convenient chaining)
	 */
	public DocumentMetadataPatchBuilder replacePermission(
			String role, DocumentMetadataHandle.Capability... newCapabilities
			);
	/**
	 * Replaces an existing role with a new role.
	 * @param oldRole	the name of the replaced role
	 * @param newRole	the name of the replacing role
	 * @param newCapabilities	the capabilities of the replacing role
	 * @return	the patch builder (for convenient chaining)
	 */
	public DocumentMetadataPatchBuilder replacePermission(
			String oldRole, String newRole, DocumentMetadataHandle.Capability... newCapabilities
			);

	/**
	 * Adds a new metadata property with a simple name.
	 * @param name	the name of the new property
	 * @param value	the value of the new property
	 * @return	the patch builder (for convenient chaining)
	 */
	public DocumentMetadataPatchBuilder addProperty(String name, Object value);
	/**
	 * Adds a new metadata property with a namespaced name.
	 * @param name	the namespaced name of the new property
	 * @param value	the value of the new property
	 * @return	the patch builder (for convenient chaining)
	 */
	public DocumentMetadataPatchBuilder addProperty(QName name, Object value);
	/**
	 * Deletes the specified metadata properties with simple names.
	 * @param names	the property names
	 * @return	the patch builder (for convenient chaining)
	 */
	public DocumentMetadataPatchBuilder deleteProperty(String... names);
	/**
	 * Deletes the specified metadata properties with namespaced names.
	 * @param names	the namespaced property names
	 * @return	the patch builder (for convenient chaining)
	 */
	public DocumentMetadataPatchBuilder deleteProperty(QName... names);
	/**
	 * Replaces the existing value of a metadata property having a simple name.
	 * @param name	the name of the existing property
	 * @param newValue the new value of the property
	 * @return	the patch builder (for convenient chaining)
	 */
	public DocumentMetadataPatchBuilder replaceProperty(String name, Object newValue);
	/**
	 * Replaces the existing value of a metadata property having a namespaced name.
	 * @param name	the namespaced name of the existing property
	 * @param newValue the new value of the property
	 * @return	the patch builder (for convenient chaining)
	 */
	public DocumentMetadataPatchBuilder replaceProperty(QName name, Object newValue);
	/**
	 * Replaces an existing metadata property with a new property having a simple name.
	 * @param oldName	the name of the existing property
	 * @param newName	the name of the replacing property
	 * @param newValue the value of the property
	 * @return	the patch builder (for convenient chaining)
	 */
	public DocumentMetadataPatchBuilder replaceProperty(
			String oldName, String newName, Object newValue
			);
	/**
	 * Replaces an existing metadata property with a new property having a namespaced name.
	 * @param oldName	the namespaced name of the existing property
	 * @param newName	the namespaced name of the replacing property
	 * @param newValue the value of the property
	 * @return	the patch builder (for convenient chaining)
	 */
	public DocumentMetadataPatchBuilder replaceProperty(
			QName oldName, QName newName, Object newValue
			);

	/**
	 * Sets the search quality of the document.
	 * @param quality	the new value for search quality
	 * @return	the patch builder (for convenient chaining)
	 */
	public DocumentMetadataPatchBuilder setQuality(int quality);

	/**
	 * Builds the patch that modifies the metadata or content of the
	 * database document and provides a handle for sending the patch
	 * to the server using the patch() method of the DocumentManager.
	 * Once the patch is built, specifying additional operation with
	 * the patch builder do not alter the patch built previously.
	 * @return	the handle on the built patch
	 */
	public DocumentPatchHandle build() throws MarkLogicIOException;
}
