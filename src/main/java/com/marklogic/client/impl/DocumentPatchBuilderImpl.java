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

import javax.xml.stream.XMLStreamWriter;

import com.marklogic.client.document.DocumentPatchBuilder;
import com.marklogic.client.io.Format;

class DocumentPatchBuilderImpl
extends DocumentMetadataPatchBuilderImpl
implements DocumentPatchBuilder
{
/* TODO:
	insert values for JSON array items?
	awareness of popular fragment sources

    metadata - accept predicate for permission or property

    collect QName prefix bindings for output on root
 */

	static class ContentDeleteOperation extends PatchOperation {
		String      selectPath;
		Cardinality cardinality;
		ContentDeleteOperation(String selectPath, Cardinality cardinality) {
			super();
			this.selectPath  = selectPath;
			this.cardinality = cardinality;
		}
		@Override
		public void write(JSONStringWriter serializer) {
			writeDelete(serializer, selectPath, cardinality);
		}
		@Override
		public void write(XMLOutputSerializer out) throws Exception {
			writeDelete(out, selectPath, cardinality);
		}
	}
	static class ContentInsertOperation extends PatchOperation {
		String      contextPath;
		Position    position;
		Cardinality cardinality;
		String      fragment;
		ContentInsertOperation(
				String contextPath, Position position, Cardinality cardinality, Object fragment
				) {
			super();
			this.contextPath = contextPath;
			this.position    = position;
			this.cardinality = cardinality;
			this.fragment    = (fragment instanceof String) ?
				(String) fragment : fragment.toString();
		}
		@Override
		public void write(JSONStringWriter serializer) {
			writeStartInsert(serializer, contextPath, position.toString(), cardinality);
			serializer.writeStartEntry("content");
			serializer.writeFragment(fragment);
			serializer.writeEndObject();
			serializer.writeEndObject();
		}
		@Override
		public void write(XMLOutputSerializer out) throws Exception {
			XMLStreamWriter serializer = out.getSerializer();
			writeStartInsert(out, contextPath, position.toString(), cardinality);
			serializer.writeCharacters(""); // force the tag close
			out.getWriter().write(fragment);
			serializer.writeEndElement();
		}
	}
	static class ContentReplaceOperation extends PatchOperation {
		String      selectPath;
		Cardinality cardinality;
		boolean     isFragment = true;
		String      input;
		ContentReplaceOperation(String selectPath, Cardinality cardinality, boolean isFragment,
				Object input
				) {
			super();
			this.selectPath  = selectPath;
			this.cardinality = cardinality;
			this.isFragment  = isFragment;
			this.input       = (input instanceof String) ?
					(String) input : input.toString();
		}
		@Override
		public void write(JSONStringWriter serializer) {
			writeStartReplace(serializer, selectPath, cardinality);
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
			writeStartReplace(out, selectPath, cardinality);
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
		String      selectPath;
		String      contextPath;
		Position    position;
		Cardinality cardinality;
		String      fragment;
		ContentReplaceInsertOperation(
				String selectPath, String contextPath, Position position, Cardinality cardinality,
				Object fragment
				) {
			super();
			this.selectPath  = selectPath;
			this.contextPath = contextPath;
			this.position    = position;
			this.cardinality = cardinality;
			this.fragment    = (fragment instanceof String) ?
					(String) fragment : fragment.toString();
		}
		@Override
		public void write(JSONStringWriter serializer) {
			writeStartReplaceInsert(
					serializer, selectPath, contextPath, position.toString(), cardinality
					);
			serializer.writeStartEntry("content");
			serializer.writeFragment(fragment);
			serializer.writeEndObject();
			serializer.writeEndObject();
		}
		@Override
		public void write(XMLOutputSerializer out) throws Exception {
			XMLStreamWriter serializer = out.getSerializer();
			writeStartReplaceInsert(
					out, selectPath, contextPath, position.toString(), cardinality
					);
			serializer.writeCharacters(""); // force the tag close
			out.getWriter().write(fragment);
			serializer.writeEndElement();
		}
	}
	static class ContentReplaceApplyOperation extends PatchOperation {
		String      selectPath;
		Cardinality cardinality;
		CallImpl    call;
		ContentReplaceApplyOperation(String selectPath, Cardinality cardinality, CallImpl call) {
			super();
			this.selectPath  = selectPath;
			this.cardinality = cardinality;
			this.call        = call;
		}
		@Override
		public void write(JSONStringWriter serializer) {
			writeReplaceApply(serializer, selectPath, cardinality, call);
		}
		@Override
		public void write(XMLOutputSerializer out) throws Exception {
			writeReplaceApply(out, selectPath, cardinality, call);
		}
	}

	DocumentPatchBuilderImpl(Format format) {
		super(format);
	}

	@Override
	public DocumentPatchBuilder delete(String selectPath) {
		return delete(selectPath, null);
	}
	@Override
	public DocumentPatchBuilder delete(String selectPath, Cardinality cardinality) {
		onContent();
		operations.add(new ContentDeleteOperation(selectPath, cardinality));
		return this;
	}
	@Override
	public DocumentPatchBuilder insertFragment(
			String contextPath, Position position, Object fragment
			) {
		return insertFragment(contextPath, position, null, fragment);
	}
	@Override
	public DocumentPatchBuilder insertFragment(
			String contextPath, Position position, Cardinality cardinality, Object fragment
			) {
		onContent();
		operations.add(
				new ContentInsertOperation(contextPath, position, cardinality, fragment)
				);
		return this;
	}
	@Override
	public DocumentPatchBuilder replaceValue(String selectPath, Object value) {
		return replaceValue(selectPath, null, value);
	}
	@Override
	public DocumentPatchBuilder replaceValue(
			String selectPath, Cardinality cardinality, Object value
			) {
		onContent();
		operations.add(new ContentReplaceOperation(selectPath, cardinality, false, value));
		return this;
	}
	@Override
	public DocumentPatchBuilder replaceFragment(String selectPath, Object fragment) {
		return replaceFragment(selectPath, null, fragment);
	}
	@Override
	public DocumentPatchBuilder replaceFragment(
			String selectPath, Cardinality cardinality, Object fragment
			) {
		onContent();
		operations.add(new ContentReplaceOperation(selectPath, cardinality, true, fragment));
		return this;
	}
	@Override
	public DocumentPatchBuilder replaceInsertFragment(
			String selectPath, String contextPath, Position position, Object fragment
	) {
		return replaceInsertFragment(
				selectPath, contextPath, position, null, fragment
				);
	}
	@Override
	public DocumentPatchBuilder replaceInsertFragment(
			String selectPath, String contextPath, Position position, Cardinality cardinality,
			Object fragment
			) {
		onContent();
		operations.add(
				new ContentReplaceInsertOperation(
						selectPath, contextPath, position, cardinality, fragment
						)
				);
		return this;
	}
	@Override
	public DocumentPatchBuilder replaceApply(String selectPath, Call call) {
		return replaceApply(selectPath, null, call);
	}
	@Override
	public DocumentPatchBuilder replaceApply(
			String selectPath, Cardinality cardinality, Call call
			) {
		if (!CallImpl.class.isAssignableFrom(call.getClass()))
			throw new IllegalArgumentException(
					"Cannot use external call implementation");
		onContent();
		operations.add(
				new ContentReplaceApplyOperation(selectPath, cardinality, (CallImpl) call)
				);
		return this;
	}
	private void onContent() {
		if (!onContent) {
			onContent = true;
		}
	}

	@Override
	public DocumentPatchBuilder pathLanguage(PathLanguage pathLang) {
		this.pathLang = pathLang;
		return this;
	}
}
