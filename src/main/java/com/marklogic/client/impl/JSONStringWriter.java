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

import java.io.CharArrayWriter;

import com.marklogic.client.document.DocumentPatchBuilder.PathLanguage;

public class JSONStringWriter {
	private StringBuilder builder;
	private boolean       isFirst = false;
	private PathLanguage pathLang;

	JSONStringWriter(PathLanguage pathLang) {
		super();
		this.pathLang = pathLang;
		builder = new StringBuilder();
	}
	
	public PathLanguage getPathLanguage() {
		return this.pathLang;
	}

	public void writeStartObject() {
		builder.append("{");
		isFirst = true;
	}
	public void writeStartEntry(String key) {
		if (isFirst)
			isFirst = false;
		else
			builder.append(", ");
		builder.append(toJSON(key));
		builder.append(":");
	}
	public void writeEndObject() {
		if (isFirst)
			isFirst = false;
		builder.append("}");
	}
	public void writeStartArray() {
		builder.append("[");
		isFirst = true;
	}
	public void writeStartItem() {
		if (isFirst)
			isFirst = false;
		else
			builder.append(", ");
	}
	public void writeEndArray() {
		if (isFirst)
			isFirst = false;
		builder.append("]");
	}
	public void writeFragment(String fragment) {
		builder.append(fragment);
	}
	public void writeStringValue(Object value) {
		builder.append(toJSON(value));
	}
	public void writeNumberValue(Object value) {
		builder.append(value);
	}
	public void writeBooleanValue(Object value) {
		builder.append(value);
	}
	@Override
	public String toString() {
		return builder.toString();
	}

	public static String toJSON(Object value) {
		if (value == null) {
			return "null";
		}

		if (value instanceof Number || value instanceof Boolean) {
			return value.toString();
		}

		String str = (value instanceof String) ?
				(String) value : value.toString();

		return toJSON(str);
	}
	public static String toJSON(String value) {
		int valen = value.length();
		CharArrayWriter out = new CharArrayWriter(valen + 2);

		out.append('"');
		for (int i=0; i < valen; i++) {
			char ch = value.charAt(i);
			/* Per RFC 4627, only quotation mark, reverse solidus,
			   and the control characters (U+0000 through U+001F)
			   must be escaped.  Two-character sequence escape
			   representations may be used for popular characters.
			 */
			switch (ch) {
			case '"':
				out.append('\\');
				out.append('"');
				break;
			case '\\':
				out.append('\\');
				out.append('\\');
				break;
			case '\b':
				out.append('\\');
				out.append('b');
				break;
			case '\f':
				out.append('\\');
				out.append('f');
				break;
			case '\n':
				out.append('\\');
				out.append('n');
				break;
			case '\r':
				out.append('\\');
				out.append('r');
				break;
			case '\t':
				out.append('\\');
				out.append('t');
				break;
			default:
				// also matches '\u007F' through '\u009F' but these
				// characters may be escaped
				if (Character.isISOControl(ch)) {
					out.append(String.format("\\u%1$04x", (int) ch));
				} else {
					out.append(ch);
				}
				break;
			}
		}
		out.append('"');

		return out.toString();
	}
}
