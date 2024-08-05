/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.impl;

import com.marklogic.client.query.AggregateResult;
import com.marklogic.client.query.Tuple;
import com.marklogic.client.query.ValuesMetrics;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

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
    private List<AggregateResult> aggregateResults;

    @XmlElement(namespace = Tuples.TUPLES_NS, name = "metrics")
    private ValuesMetricsImpl metrics;

    public String getName() {
      return name;
    }

    public Tuples() {
      tuples = new ArrayList<>();
      aggregateResults = new ArrayList<>();
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
