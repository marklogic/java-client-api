/*
 * Copyright (c) 2022 MarkLogic Corporation
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

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import com.marklogic.client.query.AggregateResult;
import com.marklogic.client.query.CountedDistinctValue;
import com.marklogic.client.query.ValuesMetrics;
import java.util.List;

/**
 * A ValuesBuilder parses a set of value results.
 *
 * The values builder class is public to satisfy constraints of JAXB.
 * It is of no consequence to users of this API.
 */
public final class ValuesBuilder {
  @XmlAccessorType(XmlAccessType.FIELD)
  @XmlRootElement(namespace = Values.VALUES_NS, name = "values-response")

  public static final class Values  {
    public static final String VALUES_NS = "http://marklogic.com/appservices/search";

    @XmlAttribute(name = "name")
    private String name;

    @XmlAttribute(name = "type")
    private String type;

    @XmlElement(namespace = Values.VALUES_NS, name = "distinct-value")
    private List<CountedDistinctValue> distinctValues;

    @XmlElement(namespace = Values.VALUES_NS, name = "aggregate-result")
    private List<AggregateResult> aggregateResults;

    @XmlElement(namespace = Values.VALUES_NS, name = "metrics")
    private ValuesMetricsImpl metrics;

    public Values() {
      distinctValues = new ArrayList<>();
      aggregateResults = new ArrayList<>();
    }

    public String getName() {
      return name;
    }

    public String getType() {
      return type;
    }

    public CountedDistinctValue[] getValues() {
      return distinctValues.toArray(new CountedDistinctValue[0]);
    }

    public AggregateResult[] getAggregates() {
      return aggregateResults.toArray(new AggregateResult[0]);
    }

    public ValuesMetrics getMetrics() {
      return metrics;
    }
  }
}
