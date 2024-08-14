/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.query;

import com.marklogic.client.impl.ValueConverter;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlValue;

/**
 * A TypedDistinctValue is a value that includes a type.
 */
public class TypedDistinctValue {
  @XmlAttribute(namespace = "http://www.w3.org/2001/XMLSchema-instance", name = "type")
  String type;

  @XmlValue
  String value;

  /**
   * Returns the type of the value.
   *
   * <p>Value types are returned as XSD type names, for example, "xs:integer" or "xs:date".</p>
   *
   * @return The type name.
   */
  public String getType() {
    return type;
  }

  /**
   * Returns the value as an instance of the specified class.
   *
   * This method converts the value according to its type and then casts it
   * to the specified class.
   *
   * @param as The instance class.
   * @param <T> The type.
   * @return The value cast to the specified type.
   */
  public <T> T get(Class<T> as) {
    return ValueConverter.convertToJava(type, value, as);
  }
}
