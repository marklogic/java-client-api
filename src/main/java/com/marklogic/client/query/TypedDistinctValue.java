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
 * A TypedDistinctValue is a value that includes a type.
 */
public class TypedDistinctValue {
    @XmlAttribute(namespace = "http://www.w3.org/2001/XMLSchema-instance", name = "type")
    String type;

    @XmlValue
    String value;

    /**
     * Returns the type of the value.
     *
     * <p>Value types are returned as XSD type names, for example, "xs:integer" or "xs:date".</p>
     *
     * @return The type name.
     */
    public String getType() {
        return type;
    }

    /**
     * Returns the value as an instance of the specified class.
     *
     * This method converts the value according to its type and then casts it
     * to the specified class.
     *
     * @param as The instance class.
     * @param <T> The type.
     * @return The value cast to the specified type.
     */
    public <T> T get(Class<T> as) {
        return ValueConverter.convertToJava(type, value, as);
    }
}
