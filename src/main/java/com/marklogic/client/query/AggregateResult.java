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
package com.marklogic.client.query;

import com.marklogic.client.impl.ValueConverter;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

/**
 * A CountedDistinctValue is a value that includes a frequency.
 */
public class AggregateResult {
    @XmlAttribute(name = "name")
    String name;

    @XmlValue
    String value;

    /**
     * Returns the name of this aggregate.
     * @return The name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the value of this aggregate as a string.
     *
     * <p>Use get() to convert the string value into a typed value.</p>
     *
     * @return The aggrevate value.
     */
    public String getValue() {
        return value;
    }

    /**
     * Returns the value cast to the specified type.
     *
     * This method converts the value according to the supplied type and then casts it
     * to the specified class.
     *
     * <p>The following types are supported:
     * <code>xs:anySimpleType</code>,
     * <code>xs:base64Binary</code>, <code>xs:boolean</code>,
     * <code>xs:byte</code>, <code>xs:date</code>,
     * <code>xs:dateTime</code>, <code>xs:dayTimeDuration</code>,
     * <code>xs:decimal</code>, <code>xs:double</code>,
     * <code>xs:duration</code>, <code>xs:float</code>,
     * <code>xs:int</code>, <code>xs:integer</code>,
     * <code>xs:long</code>, <code>xs:short</code>,
     * <code>xs:string</code>, <code>xs:time</code>,
     * <code>xs:unsignedInt</code>, <code>xs:unsignedLong</code>,
     * <code>xs:unsignedShort</code>, and
     * <code>xs:yearMonthDuration</code>.</p>
     *
     * @see com.marklogic.client.impl.ValueConverter#convertToJava(String,String) label
     * @param type The name of the XSD type to use for conversion.
     * @param as The class parameter
     * @param <T> The class to cast to
     * @return The value, cast to the specified type or
     */
    public <T> T get(String type, Class<T> as) {
        return ValueConverter.convertToJava(type, value, as);
    }
}
