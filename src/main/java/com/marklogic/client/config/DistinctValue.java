package com.marklogic.client.config;

import java.sql.Time;
import java.util.Date;

/* This class holds two static methods: getValue(), which converts a string to a value in a particular class
   and getType() which returns a particular class for a give type string.
 */

public final class DistinctValue {
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

        throw new UnsupportedOperationException("Unexpect class");
    }

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
