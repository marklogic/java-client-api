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
package com.marklogic.client.document;

import javax.xml.namespace.QName;

import com.marklogic.client.MarkLogicIOException;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.marker.DocumentPatchHandle;
import com.marklogic.client.util.IterableNamespaceContext;

/**
 * A DocumentMetadataPatchBuilder specifies changes to the metadata
 * of a database document.  When using the newPatchBuilder() factory
 * method of the DocumentManager to create the builder, you identify
 * whether paths are specified with JSONPath or XPath.
 */
public interface DocumentMetadataPatchBuilder {
	/**
	 * The Cardinality enumeration indicates the number of times
	 * that a path can match without error (which defaults to
	 * ZERO_OR_MORE, meaning any number of matches).
	 */
	public enum Cardinality {
		/**
		 * Allows at most one match.
		 */
		ZERO_OR_ONE,
		/**
		 * Requires exactly one match.
		 */
		ONE,
		/**
		 * Allows any number of matches.
		 */
		ZERO_OR_MORE,
		/**
		 * Requires at least one match.
		 */
		ONE_OR_MORE;
		/**
		 * Returns the standard abbreviation for the cardinality value.
		 * @return	the abbreviation
		 */
		public String abbreviate() {
			switch(this) {
			case ZERO_OR_ONE:
				return "?";
			case ONE:
				return ".";
			case ZERO_OR_MORE:
				return "*";
			case ONE_OR_MORE:
				return "+";
			default:
				throw new InternalError("Unknown Cardinality: "+toString());
			}
		}
	}

	/**
	 * A Call specifies how to apply a built-in or library function
	 * when replacing an existing fragment.  You must construct a call
	 * using the CallBuilder.
	 */
	public interface Call {
	}

	/**
	 * A CallBuilder constructs a Call to a built-in or library function
	 * to produce the replacement for an existing fragment.  You must
	 * construct the CallBuilder using the factory method of the
	 * DocumentPatchBuilder.
	 */
	public interface CallBuilder {
		/**
		 * Calls the built-in method to add to an existing value.
		 * @param number	the added number
		 * @return	the specification of the add call
		 */
		public Call add(Number number);
		/**
		 * Calls the built-in method to subtract from an existing value.
		 * @param number	the subtracted number
		 * @return	the specification of the subtract call
		 */
		public Call subtract(Number number);
		/**
		 * Calls the built-in method to multiply an existing value.
		 * @param number	the multiplier
		 * @return	the specification of the multiply call
		 */
		public Call multiply(Number number);
		/**
		 * Calls the built-in method to divide an existing value
		 * by the supplied number.
		 * @param number	the divisor
		 * @return	the specification of the divide call
		 */
		public Call divideBy(Number number);

		/**
		 * Calls the built-in method to append an existing string
		 * to the supplied string.
		 * @param prefix	the string that appears first
		 * @return	the specification of the concatenation call
		 */
		public Call concatenateAfter(String prefix);
		/**
		 * Calls the built-in method to concatenate an existing string
		 * between the supplied strings.
		 * @param prefix	the string that appears first
		 * @param suffix	the string that appears last
		 * @return	the specification of the concatenation call
		 */
		public Call concatenateBetween(String prefix, String suffix);
		/**
		 * Calls the built-in method to concatenate an existing string
		 * before the supplied string.
		 * @param suffix	the string that appears last
		 * @return	the specification of the concatenation call
		 */
		public Call concatenateBefore(String suffix);
		/**
		 * Calls the built-in method to reduce an existing string
		 * to a trailing substring.
		 * @param prefix	the initial part of the string
		 * @return	the specification of the substring call
		 */
		public Call substringAfter(String prefix);
		/**
		 * Calls the built-in method to reduce an existing string
		 * to a leading substring.
		 * @param suffix	the final part of the string
		 * @return	the specification of the substring call
		 */
		public Call substringBefore(String suffix);
		/**
		 * Calls the built-in method to modify an existing string
		 * with a regular expression
		 * @param pattern	the matching regular expression
		 * @param replacement	the replacement for the match
		 * @return	the specification of the regex call
		 */
		public Call replaceRegex(String pattern, String replacement);
		/**
		 * Calls the built-in method to modify an existing string
		 * with a regular expression
		 * @param pattern	the matching regular expression
		 * @param replacement	the replacement for the match
		 * @param flags	the regex flags
		 * @return	the specification of the regex call
		 */
		public Call replaceRegex(
				String pattern, String replacement, String flags
				);

		/**
		 * Calls a function with no arguments other than the existing
		 * fragment.  The function must be provided by the library specified
		 * using the DocumentPatchBuilder.
		 * @param function	the name of the function
		 * @return	the specification of the function call
		 */
		public Call applyLibrary(String function);
		/**
		 * Calls a function with the existing fragment and one or more
		 * values.  The function must be provided by the library specified
		 * using the DocumentPatchBuilder.
		 * @param function	the name of the function
		 * @param args	the literal values
		 * @return	the specification of the function call
		 */
		public Call applyLibraryValues(String function, Object... args);
		/**
		 * Calls a function with the existing fragment and one or more
		 * specified fragments.  The function must be provided by the
		 * library specified using the DocumentPatchBuilder.
		 * @param function	the name of the function
		 * @param args	the fragments
		 * @return	the specification of the function call
		 */
		public Call applyLibraryFragments(String function, Object... args);
	}

	/**
	 * A PatchHandle produced by the builder can produce a string
	 * representation of the patch for saving, logging, or other uses.
	 */
	public interface PatchHandle extends DocumentPatchHandle {
		/**
		 * Returns a JSON or XML representation of the patch as a string.
		 * @return	the patch
		 */
		public String toString();
	}

	/**
	 * Returns the namespaces available for the paths
	 * of a patch against XML documents.
	 * @return	the declared namespaces
	 */
	public IterableNamespaceContext getNamespaces();
	/**
	 * Declares the namespaces available for the paths
	 * of a patch against XML documents.  You can use the
	 * {@link com.marklogic.client.util.EditableNamespaceContext}
	 * class to edit a set of namespaces.  Note that the
	 * following prefixes are predefined for internal use:
	 * rapi, prop, xsi, and xs
	 * @param namespaces	the declared namespaces
	 */
	public void setNamespaces(IterableNamespaceContext namespaces);

	/**
	 * Specifies an XQuery library installed on the server 
	 * that supplies functions for modifying existing fragments.
	 * @param ns	the XQuery library namespace
	 * @param at	the XQuery library path on the server
	 * @return	the patch builder (for convenient chaining)
	 */
	public DocumentMetadataPatchBuilder library(String ns, String at);

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
	public DocumentMetadataPatchBuilder addPropertyValue(String name, Object value);
	/**
	 * Adds a new metadata property with a namespaced name.
	 * @param name	the namespaced name of the new property
	 * @param value	the value of the new property
	 * @return	the patch builder (for convenient chaining)
	 */
	public DocumentMetadataPatchBuilder addPropertyValue(QName name, Object value);
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
	public DocumentMetadataPatchBuilder replacePropertyValue(String name, Object newValue);
	/**
	 * Replaces the existing value of a metadata property having a namespaced name.
	 * @param name	the namespaced name of the existing property
	 * @param newValue the new value of the property
	 * @return	the patch builder (for convenient chaining)
	 */
	public DocumentMetadataPatchBuilder replacePropertyValue(QName name, Object newValue);
	/**
	 * Replaces an existing metadata property with a new property having a simple name.
	 * @param oldName	the name of the existing property
	 * @param newName	the name of the replacing property
	 * @param newValue the value of the property
	 * @return	the patch builder (for convenient chaining)
	 */
	public DocumentMetadataPatchBuilder replacePropertyValue(
			String oldName, String newName, Object newValue
			);
	/**
	 * Replaces an existing metadata property with a new property having a namespaced name.
	 * @param oldName	the namespaced name of the existing property
	 * @param newName	the namespaced name of the replacing property
	 * @param newValue the value of the property
	 * @return	the patch builder (for convenient chaining)
	 */
	public DocumentMetadataPatchBuilder replacePropertyValue(
			QName oldName, QName newName, Object newValue
			);

	/**
	 * A factory method for building calls to modify an existing node
	 * by applying built-in functions or functions from a library.
	 * @return	the builder for function calls
	 */
	public CallBuilder call();

	/**
	 * Specifies a replacement operation by applying a function
	 * to a metadata property having a simple name. You must use
	 * CallBuilder to build a specification of the call.
	 * @param name	the name of the existing property
	 * @param call	the specification of the function call
	 * @return	the patch builder (for convenient chaining)
	 */
	public DocumentMetadataPatchBuilder replacePropertyApply(
			String name, Call call
			);

	/**
	 * Specifies a replacement operation by applying a function
	 * to a metadata property having a namespaced name. You must
	 * use CallBuilder to build a specification of the call.
	 * @param name	the name of the existing property
	 * @param call	the specification of the function call
	 * @return	the patch builder (for convenient chaining)
	 */
	public DocumentMetadataPatchBuilder replacePropertyApply(
			QName name, Call call
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
	public PatchHandle build() throws MarkLogicIOException;
}
