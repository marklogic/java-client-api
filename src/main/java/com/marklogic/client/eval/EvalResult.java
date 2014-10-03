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

import com.marklogic.client.io.Format;
import com.marklogic.client.io.marker.AbstractReadHandle;

public interface EvalResult {
    enum Type {
        XML, JSON,
        STRING, BOOLEAN, NULL, OTHER,
        ANYURI, BASE64BINARY, DATE, DATETIME, DECIMAL, DOUBLE, DURATION,
        FLOAT, GDAY, GMONTH, GMONTHDAY, GYEAR, GYEARMONTH, HEXBINARY, INTEGER, QNAME, TIME,
        ATTRIBUTE, BINARY, COMMENT, PROCESSINGINSTRUCTION, TEXTNODE
    };
    public Type getType();
    public Format getFormat();
    public <H extends AbstractReadHandle> H get(H handle);
    public <T> T getAs(Class<T> clazz);
    public String getString();
    public Number getNumber();
    public Boolean getBoolean();
}
