/*
 * Copyright 2012-2014 MarkLogic Corporation
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
package com.marklogic.client.eval;

import com.marklogic.client.io.marker.AbstractReadHandle;

public interface EvalResult {
    enum Type {
        STRING,
        BOOLEAN,
        JS_NUMBER,
        JS_ARRAY,
        JS_OBJECT,
        NULL,
        XDM_ATOMIC,
        XDM_ATTRIBUTE,
        XDM_BINARY,
        XDM_COMMENT,
        XDM_DOCUMENT,
        XDM_DURATION,
        XDM_ELEMENT,
        XDM_ITEM,
        XDM_NODE,
        XDM_PROCESSINGINSTRUCTION,
        XDM_SEQUENCE,
        XDM_TEXT,
        XDM_VALUE,
        XDM_VARIABLE,
        XS_ANYURI,
        XS_BASE64BINARY,
        XS_DATE,
        XS_DATETIME,
        XS_DAYTIMEDURATION,
        XS_DECIMAL,
        XS_DOUBLE,
        XS_DURATION,
        XS_FLOAT,
        XS_GDAY,
        XS_GMONTH,
        XS_GMONTHDAY,
        XS_GYEAR,
        XS_GYEARMONTH,
        XS_HEXBINARY,
        XS_INTEGER,
        XS_QNAME,
        XS_TIME
    };
    public Type getType();
    public <H extends AbstractReadHandle> H get(H handle);
    public <T> T getAs(Class<T> clazz);
    public String getString();
    public Number getNumber();
    public boolean getBoolean();
}
