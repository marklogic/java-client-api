/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.dataservices.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.marklogic.client.SessionState;
import com.marklogic.client.impl.BaseProxy;
import com.marklogic.client.io.Format;

abstract class BaseCallerImpl {
    private BaseProxy baseProxy;

    BaseCallerImpl() {
        this.baseProxy = new BaseProxy();
    }

    BaseProxy getBaseProxy() {
        return baseProxy;
    }

    public SessionState newSessionState() {
        return baseProxy.newSessionState();
    }

    static abstract class ValuedefImpl {
        private String  datatype   = null;
        private boolean isNullable = false;
        private boolean isMultiple = false;
        private Format format     = null;

        ValuedefImpl(JsonNode valueDecl) {
            String datatype = getText(valueDecl.get("datatype"));
            if (datatype == null || datatype.length() == 0) {
                throw new IllegalArgumentException(
                        "no datatype in parameter or return declaration: "+valueDecl.toString()
                );
            }
            this.datatype   = datatype;
            this.isNullable = getBoolean(valueDecl.get("nullable"), false);
            this.isMultiple = getBoolean(valueDecl.get("multiple"), false);

            if (!"session".equals(datatype)) {
                this.format = Format.getFromDataType(datatype);
                if (this.format == Format.UNKNOWN && !"anyDocument".equals(datatype)) {
                    throw new IllegalArgumentException(
                            "datatype must specify a document format: " + datatype
                    );
                }
            }
        }

        public String getDataType() {
            return datatype;
        }
        public boolean isNullable() {
            return isNullable;
        }
        public boolean isMultiple() {
            return isMultiple;
        }
        public Format getFormat() {
            return format;
        }
    }
    static class ReturndefImpl extends ValuedefImpl {
        ReturndefImpl(JsonNode returnDecl) {
            super(returnDecl);
            if (getFormat() == null) {
                throw new IllegalArgumentException(
                        "must return a document data type: " + getDataType()
                );
            }
        }
    }
    static class ParamdefImpl extends ValuedefImpl {
        private String name;

        ParamdefImpl(JsonNode paramDecl) {
            super(paramDecl);

            String paramName = getText(paramDecl.get("name"));
            if (paramName == null || paramName.length() == 0) {
                throw new IllegalArgumentException(
                        "no name in parameter declaration: "+paramDecl.toString()
                );
            }
            this.name = paramName;
            if (!"session".equals(paramName) && getFormat() == null) {
                throw new IllegalArgumentException(
                        "parameter must accept a document data type: " + paramName
                );
            }
        }

        public String getParamName() {
            return name;
        }
    }

    static boolean getBoolean(JsonNode property, boolean defaultValue) {
        if (property == null)
            return defaultValue;
        return property.asBoolean(defaultValue);
    }
    static String getText(JsonNode property) {
        if (property == null)
            return null;
        return property.asText();
    }
}
