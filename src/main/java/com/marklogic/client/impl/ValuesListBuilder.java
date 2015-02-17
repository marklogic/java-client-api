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
package com.marklogic.client.impl;

import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A ValuesListBuilder parses list of value results.
 *
 * The values list builder class is public to satisfy constraints of JAXB.
 * It is of no consequence to users of this API.
 */
public final class ValuesListBuilder {
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlRootElement(namespace = ValuesList.VALUES_LIST_NS, name = "values-list")

    public static final class ValuesList {
        public static final String VALUES_LIST_NS = "http://marklogic.com/rest-api";

        @XmlElement(namespace = ValuesList.VALUES_LIST_NS, name = "values")
        private ArrayList<Values> values;

        public ValuesList() {
            values = new ArrayList<Values>();
        }

        public HashMap<String, String> getValuesMap() {
            HashMap<String,String> map = new HashMap<String, String>();
            for (Values value : values) {
                map.put(value.getName(), value.getUri());
            }
            return map;
        }
    }

    private static final class Values {
        @XmlElement(namespace = ValuesList.VALUES_LIST_NS, name = "name")
        String name;

        @XmlElement(namespace = ValuesList.VALUES_LIST_NS, name = "uri")
        String uri;

        public String getName() {
            return name;
        }

        public String getUri() {
            return uri;
        }
    }
}
