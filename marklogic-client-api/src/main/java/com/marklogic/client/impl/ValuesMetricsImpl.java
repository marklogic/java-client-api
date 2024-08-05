/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.impl;

import jakarta.xml.bind.annotation.XmlElement;

import com.marklogic.client.impl.ValuesBuilder.Values;
import com.marklogic.client.query.ValuesMetrics;

/**
 * A CountedDistinctValue is a value that includes a frequency.
 */
public class ValuesMetricsImpl implements ValuesMetrics {
  @XmlElement(namespace = Values.VALUES_NS, name = "values-resolution-time")
  private ValuesMetricImpl valuesResolutionTime;

  @XmlElement(namespace = Values.VALUES_NS, name = "aggregate-resolution-time")
  private ValuesMetricImpl aggregateResolutionTime;

  @XmlElement(namespace = Values.VALUES_NS, name = "total-time")
  private ValuesMetricImpl totalTime;

  @Override
  public long getValuesResolutionTime() {
    if (valuesResolutionTime == null) {
      return -1;
    }
    return valuesResolutionTime.getValue();
  }

  @Override
  public long getAggregateResolutionTime() {
    if (aggregateResolutionTime == null) {
      return -1;
    }
    return aggregateResolutionTime.getValue();
  }

  @Override
  public long getTotalTime() {
    if (totalTime == null) {
      return -1;
    }
    return totalTime.getValue();
  }
}
