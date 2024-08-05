/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.impl;

import java.util.Calendar;

import jakarta.xml.bind.annotation.XmlValue;
import javax.xml.datatype.Duration;

/**
 * A CountedDistinctValue is a value that includes a frequency.
 */
public class ValuesMetricImpl {
  private static Calendar now = Calendar.getInstance();

  public ValuesMetricImpl() {
  }

  @XmlValue
  String value;

  /**
   * Returns the metric as a long number of milliseconds.
   *
   * @return The metric.
   */
  public long getValue() {
    return parseTime(value);
  }

  private long parseTime(String time) {
    Duration d = Utilities.getDatatypeFactory().newDurationDayTime(time);
    return d.getTimeInMillis(now);
  }
}
