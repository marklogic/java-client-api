/*
 * Copyright 2012-2015 MarkLogic Corporation
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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;

import javax.xml.datatype.Duration;
import javax.xml.namespace.QName;

import org.w3c.dom.NodeList;

@SuppressWarnings("serial")
public class ClientPropertiesImpl extends NameMapBase<Object>{

	public Object put(QName name, BigDecimal value) {
		return super.put(name, value);
	}
	public Object put(QName name, BigInteger value) {
		return super.put(name, value);
	}
	public Object put(QName name, Boolean value) {
		return super.put(name, value);
	}
	public Object put(QName name, Byte value) {
		return super.put(name, value);
	}
	public Object put(QName name, byte[] value) {
		return super.put(name, value);
	}
	public Object put(QName name, Calendar value) {
		return super.put(name, value);
	}
	public Object put(QName name, Double value) {
		return super.put(name, value);
	}
	public Object put(QName name, Duration value) {
		return super.put(name, value);
	}
	public Object put(QName name, Float value) {
		return super.put(name, value);
	}
	public Object put(QName name, Integer value) {
		return super.put(name, value);
	}
	public Object put(QName name, Long value) {
		return super.put(name, value);
	}
	public Object put(QName name, NodeList value) {
		return super.put(name, value);
	}
	public Object put(QName name, Short value) {
		return super.put(name, value);
	}
	public Object put(QName name, String value) {
		return super.put(name, value);
	}
	public Object put(QName name, Object value) {
		// Number includes BigDecimal, BigInteger, Byte, Double, Float, Integer, Long, Short
		if (value instanceof Boolean || value instanceof byte[] ||
				value instanceof Calendar || value instanceof Duration ||
				value instanceof NodeList || value instanceof Number ||
				value instanceof String)
			return super.put(name, value);
		throw new IllegalArgumentException("Invalid value for metadata property "+value.getClass().getName());
	}
}
