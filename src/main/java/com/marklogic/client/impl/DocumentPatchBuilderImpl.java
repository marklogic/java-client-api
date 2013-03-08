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

import javax.xml.stream.XMLStreamWriter;

import com.marklogic.client.document.DocumentPatchBuilder;
import com.marklogic.client.io.Format;

class DocumentPatchBuilderImpl
extends DocumentMetadataPatchBuilderImpl
implements DocumentPatchBuilder
{
/* TODO:
    if null metadata categories, patch content

	insert values for JSON array items
	awareness of popular fragment sources

    metadata - accept predicate for permission or property

    collect QName prefix bindings for output on root
 */

	static class ContentDeleteOperation extends PatchOperation {
		String selectPath;
		ContentDeleteOperation(String selectPath) {
			super();
			this.selectPath = selectPath;
		}
		@Override
		public void write(JSONStringWriter serializer) {
			writeDelete(serializer, selectPath);
		}
		@Override
		public void write(XMLOutputSerializer out) throws Exception {
			writeDelete(out, selectPath);
		}
	}
	static class ContentInsertOperation extends PatchOperation {
		String   contextPath;
		Position position;
		String   fragment;
		ContentInsertOperation(
				String contextPath, Position position, Object fragment
				) {
			super();
			this.contextPath = contextPath;
			this.position    = position;
			this.fragment    = (fragment instanceof String) ?
				(String) fragment : fragment.toString();
		}
		@Override
		public void write(JSONStringWriter serializer) {
			writeStartInsert(serializer, contextPath, position.toString());
			serializer.writeStartEntry("content");
			serializer.writeFragment(fragment);
			serializer.writeEndObject();
			serializer.writeEndObject();
		}
		@Override
		public void write(XMLOutputSerializer out) throws Exception {
			XMLStreamWriter serializer = out.getSerializer();
			writeStartInsert(out, contextPath, position.toString());
			serializer.writeCharacters(""); // force the tag close
			out.getWriter().write(fragment);
			serializer.writeEndElement();
		}
	}
	static class ContentReplaceOperation extends PatchOperation {
		String  selectPath;
		boolean isFragment = true;
		String  input;
		ContentReplaceOperation(String selectPath, boolean isFragment, Object input) {
			super();
			this.selectPath = selectPath;
			this.isFragment = isFragment;
			this.input      = (input instanceof String) ?
					(String) input : input.toString();
		}
		@Override
		public void write(JSONStringWriter serializer) {
			writeStartReplace(serializer, selectPath);
			serializer.writeStartEntry("content");
			if (isFragment) {
				serializer.writeFragment(input);
			} else {
				serializer.writeStringValue(input);
			}
			serializer.writeEndObject();
			serializer.writeEndObject();
		}
		@Override
		public void write(XMLOutputSerializer out) throws Exception {
			XMLStreamWriter serializer = out.getSerializer();
			writeStartReplace(out, selectPath);
			if (isFragment) {
				serializer.writeCharacters(""); // force the tag close
				out.getWriter().write(input);
			} else {
				serializer.writeCharacters(input);
			}
			serializer.writeEndElement();
		}
	}
	static class ContentReplaceInsertOperation extends PatchOperation {
		String   selectPath;
		String   contextPath;
		Position position;
		String   fragment;
		ContentReplaceInsertOperation(
				String selectPath, String contextPath, Position position, Object fragment
				) {
			super();
			this.selectPath  = selectPath;
			this.contextPath = contextPath;
			this.position    = position;
			this.fragment    = (fragment instanceof String) ?
					(String) fragment : fragment.toString();
		}
		@Override
		public void write(JSONStringWriter serializer) {
			writeStartReplaceInsert(serializer, selectPath, contextPath, position.toString());
			serializer.writeStartEntry("content");
			serializer.writeFragment(fragment);
			serializer.writeEndObject();
			serializer.writeEndObject();
		}
		@Override
		public void write(XMLOutputSerializer out) throws Exception {
			XMLStreamWriter serializer = out.getSerializer();
			writeStartReplaceInsert(out, selectPath, contextPath, position.toString());
			serializer.writeCharacters(""); // force the tag close
			out.getWriter().write(fragment);
			serializer.writeEndElement();
		}
	}
	static class ContentReplaceApplyOperation extends PatchOperation {
		String   selectPath;
		CallImpl call;
		ContentReplaceApplyOperation(String selectPath, CallImpl call) {
			super();
			this.selectPath = selectPath;
			this.call       = call;
		}
		@Override
		public void write(JSONStringWriter serializer) {
			writeStartReplaceApply(serializer, selectPath, call.function);
			if (call.args != null || call.args.length == 0) {
				serializer.writeStartEntry("content");
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
			serializer.writeEndObject();
			serializer.writeEndObject();
		}
		@Override
		public void write(XMLOutputSerializer out) throws Exception {
			XMLStreamWriter serializer = out.getSerializer();
			writeStartReplaceApply(out, selectPath, call.function);

			if (call.args != null || call.args.length == 0) {
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

			serializer.writeEndElement();
		}
	}

	private CallBuilderImpl callBuilder;

	DocumentPatchBuilderImpl(Format format) {
		super(format);
	}

	@Override
	public DocumentPatchBuilder library(String name) {
		library = name;
		return this;
	}

	@Override
	public DocumentPatchBuilder delete(String selectPath) {
		operations.add(new ContentDeleteOperation(selectPath));
		return this;
	}
	@Override
	public DocumentPatchBuilder insertFragment(
			String contextPath, Position position, Object fragment
			) {
		operations.add(
				new ContentInsertOperation(contextPath, position, fragment)
				);
		return this;
	}
	@Override
	public DocumentPatchBuilder replaceValue(String selectPath, Object value) {
		operations.add(new ContentReplaceOperation(selectPath, false, value));
		return this;
	}
	@Override
	public DocumentPatchBuilder replaceFragment(String selectPath, Object fragment) {
		operations.add(new ContentReplaceOperation(selectPath, true, fragment));
		return this;
	}
	@Override
	public DocumentPatchBuilder replaceInsertFragment(
			String selectPath, String contextPath, Position position, Object fragment
			) {
		operations.add(
				new ContentReplaceInsertOperation(selectPath, contextPath, position, fragment)
				);
		return this;
	}
	@Override
	public DocumentPatchBuilder replaceApply(String selectPath, Call call) {
		if (!CallImpl.class.isAssignableFrom(call.getClass()))
			throw new IllegalArgumentException(
					"Cannot use external call implementation");
		operations.add(
				new ContentReplaceApplyOperation(selectPath, (CallImpl) call)
				);
		return this;
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
			return new CallImpl("ml.add", false, number.toString());
		}
		@Override
		public Call subtract(Number number) {
			if (number == null)
				throw new IllegalArgumentException("Cannot subtract null number");
			return new CallImpl("ml.subtract", false, number.toString());
		}
		@Override
		public Call multiply(Number number) {
			if (number == null)
				throw new IllegalArgumentException("Cannot multiply null number");
			return new CallImpl("ml.multiply", false, number.toString());
		}
		@Override
		public Call divideBy(Number number) {
			if (number == null)
				throw new IllegalArgumentException("Cannot divide null number");
			return new CallImpl("ml.divide", false, number.toString());
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
