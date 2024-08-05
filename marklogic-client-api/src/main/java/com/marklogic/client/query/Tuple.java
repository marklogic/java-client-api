/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.query;

import com.marklogic.client.impl.TuplesBuilder;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

/**
 * A Tuple is a single tuple value taken from a tuple/value query.
 */
public class Tuple {
  @XmlAttribute(name = "frequency")
  private long frequency;

  @XmlElement(namespace = TuplesBuilder.Tuples.TUPLES_NS, name = "distinct-value")
  private List<TypedDistinctValue> distinctValues;

  public Tuple() {
    distinctValues = new ArrayList<>();
  }

  /**
   * Returns the frequency of this tuple in the database.
   * @return The frequency.
   */
  public long getCount() {
    return frequency;
  }

  /**
   * Returns an array of all the values in this tuple.
   * @return The array of values.
   */
  public TypedDistinctValue[] getValues() {
    return distinctValues.toArray(new TypedDistinctValue[0]);
  }
}
