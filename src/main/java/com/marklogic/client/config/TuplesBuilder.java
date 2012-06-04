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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;
import java.util.ArrayList;
import java.util.List;

public final class TuplesBuilder {
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlRootElement(namespace = Tuples.TUPLES_NS, name = "values-response")

    public static final class Tuples {
        public static final String TUPLES_NS = "http://marklogic.com/appservices/search";

        @XmlAttribute(name = "name")
        private String name;

        @XmlElement(namespace = Tuples.TUPLES_NS, name = "tuple")
        private List<Tuple> tuples;

        public Tuples() {
            tuples = new ArrayList<Tuple>();
        }

        public Tuple[] getTuples() {
            return tuples.toArray(new Tuple[0]);
        }
    }
}
