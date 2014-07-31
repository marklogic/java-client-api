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
package com.marklogic.client.document;

/**
 * A DocumentPatchBuilder specifies changes to the metadata, content,
 * or both of a database document.  You can only change content for
 * a JSON or XML document.  Paths selecting existing fragments for
 * changes must be specified in JSONPath for JSON documents and in
 * XPath for XML documents.  Values can be supplied as a Java primitive
 * or autoboxed equivalent.  Fragments can be supplied as a string or
 * as an object with a toString() method that provides the serialized
 * fragment.
 */
public interface DocumentPatchBuilder
extends DocumentMetadataPatchBuilder
{
	/**
	 * The Position enumeration indicates the location relative
	 * to the context for inserting content.
	 */
	public enum Position {
		/**
		 * Indicates that content should be inserted before the context.
		 */
		BEFORE,
		/**
		 * Indicates that content should be inserted after the context.
		 */
		AFTER,
		/**
		 * Indicates that content should be inserted at the end of the
		 * child list for the context.
		 */
		LAST_CHILD {
			@Override
			public String toString() {
				return "last-child";
			}
		};
		@Override
		public String toString() {
			return super.toString().toLowerCase();
		}
	}


	/**
	 * MarkLogic's REST API patch operations support two path languages for JSON,
	 * XPATH and JSONPATH.  Default for MarkLogic 8 is XPATH,
	 * but you can use the backwards-compatible JSONPATH too.
	 */
	public enum PathLanguage {
		
		/**
		 * Indicates that the given patch uses the XPATH language.
		 */
		XPATH, 
		
		/**
		 * Indicates that the given patch uses the JSONPATH language.
		 */
		JSONPATH;
		
		@Override
		public String toString() {
			return super.toString().toLowerCase();
		};
		
	}
	
	/**
	 * Specifies the language for this patch to use
	 */
	public DocumentPatchBuilder pathLanguage(PathLanguage pathLang);
	
	/**
	 * Specifies an operation to delete an existing JSON or XML fragment.
	 * @param selectPath	the location of the JSON or XML fragment
	 * @return	the patch builder (for convenient chaining)
	 */
	public DocumentPatchBuilder delete(
			String selectPath
			);
	/**
	 * Specifies an operation to delete an existing JSON or XML fragment.
	 * @param selectPath	the location of the JSON or XML fragment
	 * @param cardinality	the number of times the patch can match without error
	 * @return	the patch builder (for convenient chaining)
	 */
	public DocumentPatchBuilder delete(
			String selectPath, Cardinality cardinality
			);
	/**
	 * Specifies an operation to insert a new JSON or XML fragment.
	 * @param contextPath	the location of an existing node
	 * @param position	a location relative to the existing node
	 * @param fragment	the inserted fragment
	 * @return	the patch builder (for convenient chaining)
	 */
	public DocumentPatchBuilder insertFragment(
			String contextPath, Position position, Object fragment
			);
	/**
	 * Specifies an operation to insert a new JSON or XML fragment.
	 * @param contextPath	the location of an existing node
	 * @param position	a location relative to the existing node
	 * @param cardinality	the number of times the patch can match without error
	 * @param fragment	the inserted fragment
	 * @return	the patch builder (for convenient chaining)
	 */
	public DocumentPatchBuilder insertFragment(
			String contextPath, Position position, Cardinality cardinality, Object fragment
			);
	/**
	 * Specifies a value to replace the existing value of a JSON or XML node.
	 * @param selectPath	the location of the node with the replaced value
	 * @param value	the new value for the node
	 * @return	the patch builder (for convenient chaining)
	 */
	public DocumentPatchBuilder replaceValue(
			String selectPath, Object value
			);
	/**
	 * Specifies a value to replace the existing value of a JSON or XML node.
	 * @param selectPath	the location of the node with the replaced value
	 * @param cardinality	the number of times the patch can match without error
	 * @param value	the new value for the node
	 * @return	the patch builder (for convenient chaining)
	 */
	public DocumentPatchBuilder replaceValue(
			String selectPath, Cardinality cardinality, Object value
			);
	/**
	 * Specifies a fragment to replace an existing JSON or XML fragment.
	 * @param selectPath	the location of the replaced node
	 * @param fragment	the replacing fragment
	 * @return	the patch builder (for convenient chaining)
	 */
	public DocumentPatchBuilder replaceFragment(
			String selectPath, Object fragment
			);
	/**
	 * Specifies a fragment to replace an existing JSON or XML fragment.
	 * @param selectPath	the location of the replaced node
	 * @param cardinality	the number of times the patch can match without error
	 * @param fragment	the replacing fragment
	 * @return	the patch builder (for convenient chaining)
	 */
	public DocumentPatchBuilder replaceFragment(
			String selectPath, Cardinality cardinality, Object fragment
			);
	/**
	 * Specifies a fragment to replace an existing JSON or XML fragment
	 * or (if the existing fragment doesn't exist) to insert relative
	 * to a different existing JSON or XML fragment.
	 * The selectPath for the replaced node may be relative to the
	 * contextPath for the insert operation.
	 * @param selectPath	the location of the replaced node
	 * @param contextPath	the location of an existing node
	 * @param position	a location relative to the existing node
	 * @param fragment	the replacing or inserted fragment
	 * @return	the patch builder (for convenient chaining)
	 */
	public DocumentPatchBuilder replaceInsertFragment(
			String selectPath, String contextPath, Position position, Object fragment
			);
	/**
	 * Specifies a fragment to replace an existing JSON or XML fragment
	 * or (if the existing fragment doesn't exist) to insert relative
	 * to a different existing JSON or XML fragment.
	 * The selectPath for the replaced node may be relative to the
	 * contextPath for the insert operation.
	 * @param selectPath	the location of the replaced node
	 * @param contextPath	the location of an existing node
	 * @param position	a location relative to the existing node
	 * @param cardinality	the number of times the patch can match without error
	 * @param fragment	the replacing or inserted fragment
	 * @return	the patch builder (for convenient chaining)
	 */
	public DocumentPatchBuilder replaceInsertFragment(
			String selectPath, String contextPath, Position position, Cardinality cardinality,
			Object fragment
			);

	/**
	 * Specifies a replacement operation by applying a function to 
	 * an existing JSON or XML fragment. You must use CallBuilder
	 * to build a specification of the call.
	 * @param selectPath	the location of the replaced node
	 * @param call	the specification of the function call
	 * @return	the patch builder (for convenient chaining)
	 */
	public DocumentPatchBuilder replaceApply(String selectPath, Call call);
	/**
	 * Specifies a replacement operation by applying a function to 
	 * an existing JSON or XML fragment. You must use CallBuilder
	 * to build a specification of the call.
	 * @param selectPath	the location of the replaced node
	 * @param cardinality	the number of times the patch can match without error
	 * @param call	the specification of the function call
	 * @return	the patch builder (for convenient chaining)
	 */
	public DocumentPatchBuilder replaceApply(String selectPath, Cardinality cardinality, Call call);
}
