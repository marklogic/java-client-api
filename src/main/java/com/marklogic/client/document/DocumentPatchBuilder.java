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
	 * Specifies an XQuery library installed on the server 
	 * that supplies functions for modifying existing fragments.
	 * @param name	the library name
	 * @return	the patch builder (for convenient chaining)
	 */
	public DocumentPatchBuilder library(String name);

	/**
	 * Specifies an operation to delete an existing JSON or XML fragment.
	 * @param selectPath	the location of the JSON or XML fragment
	 * @return	the patch builder (for convenient chaining)
	 */
	public DocumentPatchBuilder delete(
			String selectPath
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
	 * Specifies a value to replace the existing value of a JSON or XML node.
	 * @param selectPath	the location of the node with the replaced value
	 * @param value	the new value for the node
	 * @return	the patch builder (for convenient chaining)
	 */
	public DocumentPatchBuilder replaceValue(
			String selectPath, Object value
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
	 * A factory method for building calls to modify an existing node
	 * by applying built-in functions or functions from a library.
	 * @return	the builder for function calls
	 */
	public CallBuilder call();
	/**
	 * Specifies a replacement operation by applying a function to 
	 * an existing JSON or XML fragment. You must use CallBuilder
	 * to build a specification of the call.
	 * @param selectPath	the location of the replaced node
	 * @param call	the specification of the function call
	 * @return	the patch builder (for convenient chaining)
	 */
	public DocumentPatchBuilder replaceApply(String selectPath, Call call);
}
