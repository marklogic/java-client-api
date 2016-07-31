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

import com.marklogic.client.expression.SemValue;
import com.marklogic.client.type.SemIriSeqVal;
import com.marklogic.client.type.SemIriVal;

public class SemValueImpl implements SemValue {
	@Override
	public SemIriVal iri(String stringIri) {
		return new SemIriValImpl(stringIri);
	}
	@Override
	public SemIriSeqVal iris(String... stringIris) {
		return new SemIriSeqValImpl(stringIris);
	}
	static class SemIriSeqValImpl
	extends BaseTypeImpl.BaseListImpl<SemIriValImpl>
	implements SemIriSeqVal, BaseTypeImpl.BaseArgImpl {
		SemIriSeqValImpl(String[] values) {
			this((SemIriValImpl[]) Arrays.stream(values)
	                .map(val -> new SemIriValImpl(val))
	                .toArray(size -> new SemIriValImpl[size]));
		}
		SemIriSeqValImpl(SemIriVal[] values) {
			this((SemIriValImpl[]) Arrays.stream(values)
	                .map(val -> {
	                	if (!(val instanceof SemIriValImpl)) {
	                		throw new IllegalArgumentException("argument with unknown class "+val.getClass().getName());
	                	}
	                	return (SemIriValImpl) val;
	                	})
	                .toArray(size -> new SemIriValImpl[size]));
		}
		SemIriSeqValImpl(SemIriValImpl[] values) {
			super(values);
		}
		@Override
		public SemIriVal[] getIriItems() {
			return getItems();
		}
	}
	static class SemIriValImpl implements SemIriVal, BaseTypeImpl.BaseArgImpl {
    	private String value = null;
    	SemIriValImpl(String value) {
    		if (value == null) {
    			throw new IllegalArgumentException("cannot take null value");
    		}
    		this.value = value;
    	}
		@Override
        public String getString() {
        	return value;
        }
		@Override
		public SemIriVal[] getIriItems() {
			return getItems();
		}
		@Override
		public SemIriVal[] getItems() {
			return new SemIriVal[]{this};
		}
		@Override
        public String toString() {
			return getString();
		}
		@Override
		public StringBuilder exportAst(StringBuilder strb) {
			return strb.append("{\"ns\":\"sem\", \"fn\":\"iri\", \"args\":[\"").append(getString()).append("\"]}");
		}
	}
}
