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

import com.marklogic.client.query.AggregateResult;
import com.marklogic.client.query.Tuple;
import com.marklogic.client.query.ValuesMetrics;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A TuplesBuilder parses a set of tuple results.
 *
 * The tuples builder class is public to satisfy constraints of JAXB.
 * It is of no consequence to users of this API.
 */
public final class TuplesBuilder {
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlRootElement(namespace = Tuples.TUPLES_NS, name = "values-response")

    public static final class Tuples {
        public static final String TUPLES_NS = "http://marklogic.com/appservices/search";

        @XmlAttribute(name = "name")
        private String name;

        @XmlElement(namespace = Tuples.TUPLES_NS, name = "tuple")
        private List<Tuple> tuples;

        @XmlElement(namespace = Tuples.TUPLES_NS, name = "aggregate-result")
        private ArrayList<AggregateResult> aggregateResults;

        @XmlElement(namespace = Tuples.TUPLES_NS, name = "metrics")
        private ValuesMetricsImpl metrics;

        public String getName() {
            return name;
        }

        public Tuples() {
            tuples = new ArrayList<Tuple>();
            aggregateResults = new ArrayList<AggregateResult>();
        }

        public Tuple[] getTuples() {
            return tuples.toArray(new Tuple[0]);
        }

        public AggregateResult[] getAggregates() {
            return aggregateResults.toArray(new AggregateResult[0]);
        }

        public ValuesMetrics getMetrics() {
            return metrics;
        }
    }
}
