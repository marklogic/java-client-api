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
package com.marklogic.client.config;

import java.sql.Time;
import java.util.Date;

/**
 * The DistinctValue class holds two convenience methods used to convert between XML results and typed values.
 */
public final class DistinctValue {
    /**
     * Attempts to cast the specified value into the requested type.
     *
     * <p>The following types are supported: Long, Boolean, Byte, Double, Float, Integer and String.</p>
     *
     * @param value The value to be converted.
     * @param as The Class of the target type.
     * @param <T> The target type.
     * @return the value cast as requested or a runtime exception if the conversion isn't possible.
     */
    public static <T> T getValue(String value, Class<T> as) {
        if (as == Long.class) {
            return (T) new Long(Long.parseLong(value));
        }

        if (as == Boolean.class) {
            return (T) new Boolean("true".equals(value) || "1".equals(value));
        }

        if (as == Byte.class) {
            return (T) new Byte(Byte.parseByte(value));
        }

        if (as == Double.class) {
            return (T) new Double(Double.parseDouble(value));
        }

        if (as == Float.class) {
            return (T) new Float(Float.parseFloat(value));
        }

        if (as == Integer.class) {
            return (T) new Integer(Integer.parseInt(value));
        }

        if (as == String.class) {
            return (T) new String(value);
        }

        // FIXME: Support Date.class
        // FIXME: Support Time.class

        throw new UnsupportedOperationException("Unexpected class");
    }

    /**
     * Returns a type Class for a specified XML Schema type name.
     * @param type The type name string, e.g. "xs:boolean" or "xs:date".
     * @return The type Class or null if the type name is not recognized.
     */
    public static Class getType(String type) {
        if ("xs:boolean".equals(type))
            return Boolean.class;
        if ("xs:byte".equals(type))
            return byte.class;
        if ("xs:date".equals(type))
            return Date.class;
        if ("xs:decimal".equals(type))
            return Double.class;
        if ("xs:double".equals(type))
            return Double.class;
        if ("xs:float".equals(type))
            return Float.class;
        if ("xs:hexBinary".equals(type))
            return String.class;
        if ("xs:int".equals(type))
            return Integer.class;
        if ("xs:integer".equals(type))
            return Integer.class;
        if ("xs:long".equals(type))
            return Long.class;
        // TODO: QName
        if ("xs:short".equals(type))
            return Integer.class;
        if ("xs:string".equals(type))
            return String.class;
        if ("xs:time".equals(type))
            return Time.class;
        if ("xs:unsignedInt".equals(type))
            return Integer.class;
        if ("xs:unsignedLong".equals(type))
            return Long.class;
        if ("xs:unsignedShort".equals(type))
            return Integer.class;

        return null;
    }
}
