/*
 * Copyright 2016 MarkLogic Corporation
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

import java.util.Arrays;

import com.marklogic.client.expression.RdfValue;
import com.marklogic.client.type.RdfLangStringSeqVal;
import com.marklogic.client.type.RdfLangStringVal;

public class RdfValueImpl implements RdfValue {
	@Override
	public RdfLangStringVal langString(String string, String lang) {
		return new RdfLangStringValImpl(string, lang);
	}
	@Override
	public RdfLangStringSeqVal langStrings(RdfLangStringVal... langStrings) {
		return new RdfLangStringSeqValImpl(langStrings);
	}
	static class RdfLangStringSeqValImpl
	extends BaseTypeImpl.BaseListImpl<RdfLangStringValImpl>
	implements RdfLangStringSeqVal, BaseTypeImpl.BaseArgImpl {
		RdfLangStringSeqValImpl(RdfLangStringVal[] values) {
			this((RdfLangStringValImpl[]) Arrays.stream(values)
	                .map(val -> {
	                	if (!(val instanceof RdfLangStringValImpl)) {
	                		throw new IllegalArgumentException("argument with unknown class "+val.getClass().getName());
	                	}
	                	return (RdfLangStringValImpl) val;
	                	})
	                .toArray(size -> new RdfLangStringValImpl[size]));
		}
		RdfLangStringSeqValImpl(RdfLangStringValImpl[] values) {
			super(values);
		}
		@Override
		public RdfLangStringVal[] getLangStringItems() {
			return getItems();
		}
	}
	static class RdfLangStringValImpl implements RdfLangStringVal, BaseTypeImpl.BaseArgImpl {
    	private String string = null;
    	private String lang   = null;
    	RdfLangStringValImpl(String string, String lang) {
    		if (string == null) {
    			throw new IllegalArgumentException("cannot take null string");
    		}
    		if (lang == null) {
    			throw new IllegalArgumentException("cannot take null lang");
    		}
    		this.string = string;
    		this.lang   = lang;
    	}
		@Override
        public String getString() {
        	return string;
        }
		@Override
        public String getLang() {
        	return lang;
        }
		@Override
		public RdfLangStringVal[] getLangStringItems() {
			return getItems();
		}
		@Override
		public RdfLangStringVal[] getItems() {
			return new RdfLangStringVal[]{this};
		}
		@Override
        public String toString() {
			return getString();
		}
		@Override
		public StringBuilder exportAst(StringBuilder strb) {
			return strb.append("{\"ns\":\"rdf\", \"fn\":\"langString\", \"args\":[\"")
					.append(getString()).append("\", \"").append(getLang())
					.append("\"]}");
		}
	}
}
