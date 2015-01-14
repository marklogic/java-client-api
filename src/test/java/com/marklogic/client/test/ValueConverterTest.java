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
package com.marklogic.client.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.marklogic.client.impl.ValueConverter;
import com.marklogic.client.impl.ValueConverter.ValueProcessor;

public class ValueConverterTest {
	static TestProcessor processor;
	@BeforeClass
	public static void beforeClass() {
		processor = new TestProcessor();
	}
	@AfterClass
	public static void afterClass() {
		processor = null;
	}

	@Test
	public void testConvertFromJavaObject() {
		Object o = new Object() {
			@Override
			public String toString() {
				return "object value";
			}
		};
		ValueConverter.convertFromJava(o, processor);
		checkProcessor("object", "xs:anySimpleType", o.toString());
	}
	@Test
	public void testConvertFromJavaBigDecimal() {
		BigDecimal b = new BigDecimal("10.02");
		ValueConverter.convertFromJava(b, processor);
		checkProcessor("decimal", "xs:decimal", b);
	}
	@Test
	public void testConvertFromJavaBigInteger() {
		BigInteger b = ValueConverter.MAX_UNSIGNED_LONG;
		ValueConverter.convertFromJava(b, processor);
		checkProcessor("unsigned long", "xs:unsignedLong", b);

		b = b.add(new BigInteger("1"));
		ValueConverter.convertFromJava(b, processor);
		checkProcessor("integer", "xs:integer", b);
	}
	@Test
	public void testConvertFromJavaBoolean() {
		boolean b = true;
		ValueConverter.convertFromJava(b, processor);
		checkProcessor("boolean", "xs:boolean", b);
	}
	@Test
	public void testConvertFromJavaByte() {
		byte b = 'a';
		ValueConverter.convertFromJava(b, processor);
		checkProcessor("byte", "xs:byte", b);
	}
	@Test
	public void testConvertFromJavaByteArray() {
		byte[] b = {'a', 'b', 'c'};
		ValueConverter.convertFromJava(b, processor);
		assertEquals("byte array type",  "xs:base64Binary", processor.type);
		byte[] c = (byte[]) ValueConverter.convertToJava(
				processor.type, processor.value
				);
		boolean isSame = true;
		if (c == null || b.length != c.length) {
			isSame = false;
		} else {
			for (int i=0; i < c.length; i++) {
				if (b[i] != c[i])
					isSame = false;
			}
		}
		assertTrue("byte array value", isSame);
	}
	@Test
	public void testConvertFromJavaCalendar() {
		Calendar c = Calendar.getInstance();
		ValueConverter.convertFromJava(c, processor);
		Calendar d = (Calendar) ValueConverter.convertToJava(
				processor.type, processor.value
				);
		assertEquals("datetime type", "xs:dateTime", processor.type);
		assertEquals("datetime value", c.getTimeInMillis(), d.getTimeInMillis());

		Calendar e = Calendar.getInstance();
		e.clear();
		e.set(Calendar.YEAR,         c.get(Calendar.YEAR));
		e.set(Calendar.MONTH,        c.get(Calendar.MONTH));
		e.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH));
		ValueConverter.convertFromJava(e, processor);
		d = (Calendar) ValueConverter.convertToJava(
				processor.type, processor.value
				);
		assertEquals("date type", "xs:date", processor.type);
		assertEquals("date value", e.getTimeInMillis(), d.getTimeInMillis());

		e = Calendar.getInstance();
		e.clear();
		e.set(Calendar.HOUR_OF_DAY, c.get(Calendar.HOUR_OF_DAY));
		e.set(Calendar.MINUTE,      c.get(Calendar.MINUTE));
		e.set(Calendar.SECOND,      c.get(Calendar.SECOND));
		e.set(Calendar.MILLISECOND, c.get(Calendar.MILLISECOND));
		ValueConverter.convertFromJava(e, processor);
		d = (Calendar) ValueConverter.convertToJava(
				processor.type, processor.value
				);
		assertEquals("time type", "xs:time", processor.type);
		assertEquals("time hour value",
				e.get(Calendar.HOUR_OF_DAY),
				d.get(Calendar.HOUR_OF_DAY));
		assertEquals("time minute value",
				e.get(Calendar.MINUTE),
				d.get(Calendar.MINUTE));
		assertEquals("time second value",
				e.get(Calendar.SECOND),
				d.get(Calendar.SECOND));
		assertEquals("time millisecond value",
				e.get(Calendar.MILLISECOND),
				d.get(Calendar.MILLISECOND));
	}
	@Test
	public void testConvertFromJavaDouble() {
		double d = 2.5d;
		ValueConverter.convertFromJava(d, processor);
		checkProcessor("double", "xs:double", d);
	}
	@Test
	public void testConvertFromJavaDuration() throws DatatypeConfigurationException {
		DatatypeFactory f = DatatypeFactory.newInstance();
		Duration d = f.newDuration(true, 1, 2, 3, 4, 5, 6);
		ValueConverter.convertFromJava(d, processor);
		checkProcessor("duration", "xs:duration", d);

		d = f.newDurationYearMonth(true, 1, 2);
		ValueConverter.convertFromJava(d, processor);
		checkProcessor("year month duration", "xs:yearMonthDuration", d);

		d = f.newDurationDayTime(true, 3, 4, 5, 6);
		ValueConverter.convertFromJava(d, processor);
		checkProcessor("day time duration", "xs:dayTimeDuration", d);
	}
	@Test
	public void testConvertFromJavaFloat() {
		float f = 2.5f;
		ValueConverter.convertFromJava(f, processor);
		checkProcessor("float", "xs:float", f);
	}
	@Test
	public void testConvertFromJavaInteger() {
		int i = ValueConverter.MAX_UNSIGNED_SHORT;
		ValueConverter.convertFromJava(i, processor);
		checkProcessor("unsigned short", "xs:unsignedShort", i);

		i++;
		ValueConverter.convertFromJava(i, processor);
		checkProcessor("int", "xs:int", i);
	}
	@Test
	public void testConvertFromJavaLong() {
		long l = ValueConverter.MAX_UNSIGNED_INT;
		ValueConverter.convertFromJava(l, processor);
		checkProcessor("unsigned int", "xs:unsignedInt", l);

		l++;
		ValueConverter.convertFromJava(l, processor);
		checkProcessor("long", "xs:long", l);
	}
	@Test
	public void testConvertFromJavaShort() {
		short s = 254;
		ValueConverter.convertFromJava(s, processor);
		checkProcessor("short", "xs:short", s);
	}
	@Test
	public void testConvertFromJavaString() {
		String s = "string value";
		ValueConverter.convertFromJava(s, processor);
		checkProcessor("string", "xs:string", s);
	}

	void checkProcessor(String type, String xsType, Object value) {
		assertEquals(type+" type",  xsType, processor.type);
		assertEquals(type+" value",
				value,
				ValueConverter.convertToJava(processor.type, processor.value)
				);
	}

	static class TestProcessor implements ValueProcessor {
		Object original;
		String type;
		String value;
		@Override
		public void process(Object original, String type, String value) {
			this.original = original;
			this.type     = type;
			this.value    = value;
		}
		public Object getOriginal() {
			return original;
		}
		public String getType() {
			return type;
		}
		public String getValue() {
			return value;
		}		
	}
}
