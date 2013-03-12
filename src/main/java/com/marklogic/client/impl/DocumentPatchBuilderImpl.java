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
			writeReplaceApply(serializer, selectPath, call);
		}
		@Override
		public void write(XMLOutputSerializer out) throws Exception {
			writeReplaceApply(out, selectPath, call);
		}
	}

	DocumentPatchBuilderImpl(Format format) {
		super(format);
	}

	@Override
	public DocumentPatchBuilder delete(String selectPath) {
		onContent();
		operations.add(new ContentDeleteOperation(selectPath));
		return this;
	}
	@Override
	public DocumentPatchBuilder insertFragment(
			String contextPath, Position position, Object fragment
			) {
		onContent();
		operations.add(
				new ContentInsertOperation(contextPath, position, fragment)
				);
		return this;
	}
	@Override
	public DocumentPatchBuilder replaceValue(String selectPath, Object value) {
		onContent();
		operations.add(new ContentReplaceOperation(selectPath, false, value));
		return this;
	}
	@Override
	public DocumentPatchBuilder replaceFragment(String selectPath, Object fragment) {
		onContent();
		operations.add(new ContentReplaceOperation(selectPath, true, fragment));
		return this;
	}
	@Override
	public DocumentPatchBuilder replaceInsertFragment(
			String selectPath, String contextPath, Position position, Object fragment
			) {
		onContent();
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
		onContent();
		operations.add(
				new ContentReplaceApplyOperation(selectPath, (CallImpl) call)
				);
		return this;
	}
	private void onContent() {
		if (!onContent) {
			onContent = true;
		}
	}
}
