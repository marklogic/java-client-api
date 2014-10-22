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
package com.marklogic.client.pojo.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface PathIndexProperty {
    ScalarType scalarType();

    public enum ScalarType {
        STRING            ("string"),
        INT               ("int"),
        UNSIGNED_INT      ("unsignedInt"),
        LONG              ("long"),
        UNSIGNED_LONG     ("unsignedLong"),
        FLOAT             ("float"),
        DOUBLE            ("double"),
        DECIMAL           ("decimal"),
        DATETIME          ("dateTime"),
        TIME              ("time"),
        DATE              ("date"),
        GYEARMONTH        ("gYearMonth"),
        GYEAR             ("gYear"),
        GMONTH            ("gMonth"),
        GDAY              ("gDay"),
        YEARMONTH_DURATION("yearMonthDuration"),
        DAYTIME_DURATION  ("dayTimeDuration"),
        ANYURI            ("anyURI");

        private String string;
        private ScalarType(String string) {
            this.string = string;
        };

        public String toString() { return string; };
    };
}
