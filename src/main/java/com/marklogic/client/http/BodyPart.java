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
package com.marklogic.client.http;

import java.util.Map;

import com.marklogic.client.io.marker.AbstractReadHandle;

public interface BodyPart {
    //Map<String, List<String>> getHeaders();
    /**
     * If this part is JSON, could be any of the following types:
     * <pre>
     *   string
     *   number
     *   boolean
     *   array
     *   object
     *   null
     * </pre>
     *
     * Or if this part is XML, could be any of the following types:
     * <pre>
     *   xs:anySimpleType,
     *   xs:base64Binary,
     *   xs:boolean,
     *   xs:byte,
     *   xs:date,
     *   xs:dateTime,
     *   xs:dayTimeDuration,
     *   xs:decimal,
     *   xs:double,
     *   xs:duration,
     *   xs:float,
     *   xs:int,
     *   xs:integer,
     *   xs:long,
     *   xs:short,
     *   xs:string,
     *   xs:time,
     *   xs:unsignedInt,
     *   xs:unsignedLong,
     *   xs:unsignedShort,
     *   xs:yearMonthDuration.
     * </pre>
     */
    //public String getType(); <- what does XDBC return?
    // maybe this instead?
    // public String getMimeType();
    public <H extends AbstractReadHandle> H get(H handle);
    public <T> T getAs(Class<T> clazz);
}
