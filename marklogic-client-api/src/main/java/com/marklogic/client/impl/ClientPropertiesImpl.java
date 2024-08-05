/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;

import javax.xml.datatype.Duration;
import javax.xml.namespace.QName;

import org.w3c.dom.NodeList;

@SuppressWarnings("serial")
public class ClientPropertiesImpl extends NameMapBase<Object>{

  public Object put(QName name, BigDecimal value) {
    return super.put(name, value);
  }
  public Object put(QName name, BigInteger value) {
    return super.put(name, value);
  }
  public Object put(QName name, Boolean value) {
    return super.put(name, value);
  }
  public Object put(QName name, Byte value) {
    return super.put(name, value);
  }
  public Object put(QName name, byte[] value) {
    return super.put(name, value);
  }
  public Object put(QName name, Calendar value) {
    return super.put(name, value);
  }
  public Object put(QName name, Double value) {
    return super.put(name, value);
  }
  public Object put(QName name, Duration value) {
    return super.put(name, value);
  }
  public Object put(QName name, Float value) {
    return super.put(name, value);
  }
  public Object put(QName name, Integer value) {
    return super.put(name, value);
  }
  public Object put(QName name, Long value) {
    return super.put(name, value);
  }
  public Object put(QName name, NodeList value) {
    return super.put(name, value);
  }
  public Object put(QName name, Short value) {
    return super.put(name, value);
  }
  public Object put(QName name, String value) {
    return super.put(name, value);
  }
  @Override
  public Object put(QName name, Object value) {
    // Number includes BigDecimal, BigInteger, Byte, Double, Float, Integer, Long, Short
    if (value instanceof Boolean || value instanceof byte[] ||
      value instanceof Calendar || value instanceof Duration ||
      value instanceof NodeList || value instanceof Number ||
      value instanceof String)
      return super.put(name, value);
    throw new IllegalArgumentException("Invalid value for metadata property "+value.getClass().getName());
  }
}
