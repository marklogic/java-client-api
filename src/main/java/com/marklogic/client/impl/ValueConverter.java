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

import javax.xml.bind.DatatypeConverter;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;

import com.marklogic.client.MarkLogicInternalException;

/**
 * ValueConverter roundtrips Java atomic values to and from XML.
 *
 * Note that ValueConverter does not enable Java equivalents for the following
 * XML Schema concrete atomic datatypes:  anyUri, gYear, gMonth, gDay, gMonthDay,
 * hexBinary, ID, language, Name, NCName, xs:unsignedByte, or token.
 */
public class ValueConverter {
	/**
	 * ValueProcessor specifies a callback for processing a Java value
	 * converted to the lexical form of an XML value.
	 */
	public interface ValueProcessor {
		public void process(Object original, String type, String value);
	}

	final static private BigInteger MIN_UNSIGNED       = new BigInteger("0");
	/**
	 * The largest unsigned long.
	 */
	final static public BigInteger MAX_UNSIGNED_LONG  = new BigInteger("18446744073709551615");
	/**
	 * The largest unsigned int.
	 */
	final static public long       MAX_UNSIGNED_INT   = 4294967295l;
	/**
	 * The largest unsigned short.
	 */
	final static public int        MAX_UNSIGNED_SHORT = 65535;

	static private DatatypeFactory datatypeFactory;
	
	private ValueConverter() {
		super();
	}

	static public void convertFromJava(Object value, ValueProcessor processor) {
		if (value == null)
			processor.process(null, null, null);
		else if (value instanceof BigDecimal)
			convertFromJava((BigDecimal) value, processor);
		else if (value instanceof BigInteger)
			convertFromJava((BigInteger) value, processor);
		else if (value instanceof Boolean)
			convertFromJava((Boolean) value, processor);
		else if (value instanceof Byte)
			convertFromJava((Byte) value, processor);
		else if (value instanceof byte[])
			convertFromJava((byte[]) value, processor);
		else if (value instanceof Calendar)
			convertFromJava((Calendar) value, processor);
		else if (value instanceof Double)
			convertFromJava((Double) value, processor);
		else if (value instanceof Duration)
			convertFromJava((Duration) value, processor);
		else if (value instanceof Float)
			convertFromJava((Float) value, processor);
		else if (value instanceof Integer)
			convertFromJava((Integer) value, processor);
		else if (value instanceof Long)
			convertFromJava((Long) value, processor);
		else if (value instanceof Short)
			convertFromJava((Short) value, processor);
		else if (value instanceof String)
			convertFromJava((String) value, processor);
		else
			processor.process(
					value,
					"xs:anySimpleType",
					DatatypeConverter.printString(value.toString())
					);
	}
	static public void convertFromJava(BigDecimal value, ValueProcessor processor) {
		if (value == null) {
			processor.process(null, null, null);
			return;
		}
		processor.process(
				value,
				"xs:decimal",
				DatatypeConverter.printDecimal(value)
				);
	}
	static public void convertFromJava(BigInteger value, ValueProcessor processor) {
		if (value == null) {
			processor.process(null, null, null);
			return;
		}
		if (MIN_UNSIGNED.compareTo(value) <= 0 &&
				MAX_UNSIGNED_LONG.compareTo(value) >= 0)
			// DatatypeConverter doesn't provide printUnsignedLong()
			processor.process(
				value,
				"xs:unsignedLong",
				DatatypeConverter.printInteger(value)
				);
		else
			processor.process(
				value,
				"xs:integer",
				DatatypeConverter.printInteger(value)
				);
	}
	static public void convertFromJava(Boolean value, ValueProcessor processor) {
		if (value == null) {
			processor.process(null, null, null);
			return;
		}
		processor.process(
				value,
				"xs:boolean",
				DatatypeConverter.printBoolean(value)
				);
	}
	static public void convertFromJava(Byte value, ValueProcessor processor) {
		if (value == null) {
			processor.process(null, null, null);
			return;
		}
		processor.process(
				value,
				"xs:byte",
				DatatypeConverter.printByte(value)
				);
	}
	static public void convertFromJava(byte[] value, ValueProcessor processor) {
		if (value == null) {
			processor.process(null, null, null);
			return;
		}
		processor.process(
				value,
				"xs:base64Binary",
				DatatypeConverter.printBase64Binary(value)
				);
	}
	static public void convertFromJava(Calendar value, ValueProcessor processor) {
		if (value == null) {
			processor.process(null, null, null);
			return;
		}
		if (!value.isSet(Calendar.DAY_OF_MONTH))
			processor.process(
					value,
					"xs:time",
					DatatypeConverter.printTime(value)
					);
		else if (value.isSet(Calendar.HOUR_OF_DAY))
			processor.process(
					value,
					"xs:dateTime",
					DatatypeConverter.printDateTime(value)
					);
		else
			processor.process(
					value,
					"xs:date",
					DatatypeConverter.printDate(value)
					);
	}
	static public void convertFromJava(Double value, ValueProcessor processor) {
		if (value == null) {
			processor.process(null, null, null);
			return;
		}
		processor.process(
				value,
				"xs:double",
				DatatypeConverter.printDouble(value)
				);
	}
	static public void convertFromJava(Duration value, ValueProcessor processor) {
		if (value == null) {
			processor.process(null, null, null);
			return;
		}
		processor.process(
				value,
				"xs:"+value.getXMLSchemaType().getLocalPart(),
				value.toString()
				);
	}
	static public void convertFromJava(Float value, ValueProcessor processor) {
		if (value == null) {
			processor.process(null, null, null);
			return;
		}
		processor.process(
				value,
				"xs:float",
				DatatypeConverter.printFloat(value)
				);
	}
	static public void convertFromJava(Integer value, ValueProcessor processor) {
		if (value == null) {
			processor.process(null, null, null);
			return;
		}
		int ival = value.intValue();
		if (0 <= ival && ival <= MAX_UNSIGNED_SHORT)
			processor.process(
				value,
				"xs:unsignedShort",
				DatatypeConverter.printUnsignedShort(ival)
				);
		else
			processor.process(
				value,
				"xs:int",
				DatatypeConverter.printInt(ival)
				);
	}
	static public void convertFromJava(Long value, ValueProcessor processor) {
		if (value == null) {
			processor.process(null, null, null);
			return;
		}
		long longVal = ((Long) value).longValue();
		if (0 <= longVal && longVal <= MAX_UNSIGNED_INT)
			processor.process(
				value,
				"xs:unsignedInt",
				DatatypeConverter.printUnsignedInt(longVal)
				);
		else
			processor.process(
				value,
				"xs:long",
				DatatypeConverter.printLong(longVal)
				);
	}
	static public void convertFromJava(Short value, ValueProcessor processor) {
		if (value == null) {
			processor.process(null, null, null);
			return;
		}
		processor.process(
			value,
			"xs:short",
			DatatypeConverter.printShort(value)
			);
	}
	static public void convertFromJava(String value, ValueProcessor processor) {
		if (value == null) {
			processor.process(null, null, null);
			return;
		}
		processor.process(
				value,
				"xs:string",
				DatatypeConverter.printString(value)
				);
	}
	static public Object convertToJava(String type, String value) {
		if ("xs:anySimpleType".equals(type))
			return DatatypeConverter.parseAnySimpleType(value);
		if ("xs:base64Binary".equals(type))
			return DatatypeConverter.parseBase64Binary(value);
		if ("xs:boolean".equals(type))
			return DatatypeConverter.parseBoolean(value);
		if ("xs:byte".equals(type))
			return DatatypeConverter.parseByte(value);
		if ("xs:date".equals(type))
			return DatatypeConverter.parseDate(value);
		if ("xs:dateTime".equals(type))
			return DatatypeConverter.parseDateTime(value);
		if ("xs:dayTimeDuration".equals(type))
			return getFactory().newDurationDayTime(value);
		if ("xs:decimal".equals(type))
			return DatatypeConverter.parseDecimal(value);
		if ("xs:double".equals(type))
			return DatatypeConverter.parseDouble(value);
		if ("xs:duration".equals(type))
			return getFactory().newDuration(value);
		if ("xs:float".equals(type))
			return DatatypeConverter.parseFloat(value);
		if ("xs:int".equals(type))
			return DatatypeConverter.parseInt(value);
		if ("xs:integer".equals(type))
			return DatatypeConverter.parseInteger(value);
		if ("xs:long".equals(type))
			return DatatypeConverter.parseLong(value);
		if ("xs:short".equals(type))
			return DatatypeConverter.parseShort(value);
		if ("xs:string".equals(type))
			return DatatypeConverter.parseString(value);
		if ("xs:time".equals(type))
			return DatatypeConverter.parseTime(value);
		if ("xs:unsignedInt".equals(type))
			return DatatypeConverter.parseUnsignedInt(value);
        if ("xs:unsignedLong".equals(type)) {
            BigInteger bi = DatatypeConverter.parseInteger(value);
            if (bi.compareTo(MAX_UNSIGNED_LONG) < 0) {
                return bi.longValue();
            } else {
                return bi;
            }
        } if ("xs:unsignedShort".equals(type))
            return DatatypeConverter.parseUnsignedShort(value);
		if ("xs:yearMonthDuration".equals(type))
			return getFactory().newDurationYearMonth(value);
		return value;
	}
    @SuppressWarnings("unchecked")
	static public <T> T convertToJava(String type, String value, Class<T> as) {
        return (T) convertToJava(type, value);
    }
	static private DatatypeFactory getFactory() {
		if (datatypeFactory == null) {
		try {
			datatypeFactory = DatatypeFactory.newInstance();
		} catch (DatatypeConfigurationException e) {
			throw new MarkLogicInternalException(e);
		}
		}
		return datatypeFactory;
	}
}
