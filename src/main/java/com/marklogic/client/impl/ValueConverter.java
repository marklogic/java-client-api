/*
 * Copyright 2012 MarkLogic Corporation
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

public class ValueConverter {
	static public Object convertXMLValue(String type, String value) {
		// TODO: supplement JAXB conversion with javax.xml.datatype.*
		if ("xs:anySimpleType".equals(type))
			return javax.xml.bind.DatatypeConverter.parseAnySimpleType(value);
		if ("xs:base64Binary".equals(type))
			return javax.xml.bind.DatatypeConverter.parseBase64Binary(value);
		if ("xs:boolean".equals(type))
			return javax.xml.bind.DatatypeConverter.parseBoolean(value);
		if ("xs:byte".equals(type))
			return javax.xml.bind.DatatypeConverter.parseByte(value);
		if ("xs:date".equals(type))
			return javax.xml.bind.DatatypeConverter.parseDate(value);
		if ("xs:decimal".equals(type))
			return javax.xml.bind.DatatypeConverter.parseDecimal(value);
		if ("xs:double".equals(type))
			return javax.xml.bind.DatatypeConverter.parseDouble(value);
		if ("xs:float".equals(type))
			return javax.xml.bind.DatatypeConverter.parseFloat(value);
		if ("xs:hexBinary".equals(type))
			return javax.xml.bind.DatatypeConverter.parseHexBinary(value);
		if ("xs:int".equals(type))
			return javax.xml.bind.DatatypeConverter.parseInt(value);
		if ("xs:integer".equals(type))
			return javax.xml.bind.DatatypeConverter.parseInteger(value);
		if ("xs:long".equals(type))
			return javax.xml.bind.DatatypeConverter.parseLong(value);
		// TODO: QName
		if ("xs:short".equals(type))
			return javax.xml.bind.DatatypeConverter.parseShort(value);
		if ("xs:string".equals(type))
			return javax.xml.bind.DatatypeConverter.parseString(value);
		if ("xs:time".equals(type))
			return javax.xml.bind.DatatypeConverter.parseTime(value);
		if ("xs:unsignedInt".equals(type))
			return javax.xml.bind.DatatypeConverter.parseUnsignedInt(value);
		if ("xs:unsignedLong".equals(type))
			// JAXB doesn't provide parseUnsignedLong()
			return javax.xml.bind.DatatypeConverter.parseInteger(value);
		if ("xs:unsignedShort".equals(type))
			return javax.xml.bind.DatatypeConverter.parseUnsignedShort(value);
		return value;
	}
	static public String convertedJavaType(Object value) {
		// maintain in parallel with convertJavaValue()
		if (value instanceof byte[])
			return "xs:base64Binary";
		if (value instanceof Boolean)
			return "xs:boolean";
		if (value instanceof Byte)
			return "xs:byte";
		if (value instanceof Calendar)
			return "xs:datetime";
		if (value instanceof BigDecimal)
			return "xs:decimal";
		if (value instanceof Double)
			return "xs:double";
		if (value instanceof Float)
			return "xs:float";
		if (value instanceof Integer)
			return "xs:int";
		if (value instanceof BigInteger)
			return "xs:integer";
		if (value instanceof Long)
			return "xs:long";
		if (value instanceof Short)
			return "xs:short";
		if (value instanceof String)
			return "xs:string";
		return "xs:string";
	}
	static public String convertJavaValue(Object value) {
		// TODO: supplement JAXB conversion with javax.xml.datatype.*
		// TODO: distinguish base64Binary from hexBinary
		if (value instanceof byte[])
			return javax.xml.bind.DatatypeConverter.printBase64Binary((byte[]) value);
		if (value instanceof Boolean)
			return javax.xml.bind.DatatypeConverter.printBoolean((Boolean) value);
		if (value instanceof Byte)
			return javax.xml.bind.DatatypeConverter.printByte((Byte) value);
		// TODO: support Date, distinguish datetime, date, and time Calendars
		if (value instanceof Calendar)
			return javax.xml.bind.DatatypeConverter.printDateTime((Calendar) value);
		if (value instanceof BigDecimal)
			return javax.xml.bind.DatatypeConverter.printDecimal((BigDecimal) value);
		if (value instanceof Double)
			return javax.xml.bind.DatatypeConverter.printDouble((Double) value);
		if (value instanceof Float)
			return javax.xml.bind.DatatypeConverter.printFloat((Float) value);
		// TODO: distinguish unsigned short from integer
		if (value instanceof Integer)
			return javax.xml.bind.DatatypeConverter.printInt((Integer) value);
		// TODO: distinguish integer from unsigned long
		if (value instanceof BigInteger)
			return javax.xml.bind.DatatypeConverter.printInteger((BigInteger) value);
		// TODO: distinguish long from unsigned int
		if (value instanceof Long)
			return javax.xml.bind.DatatypeConverter.printLong((Long) value);
		// TODO: QName
		if (value instanceof Short)
			return javax.xml.bind.DatatypeConverter.printShort((Short) value);
		if (value instanceof String)
			return javax.xml.bind.DatatypeConverter.printString((String) value);
		return value.toString();
	}
}
