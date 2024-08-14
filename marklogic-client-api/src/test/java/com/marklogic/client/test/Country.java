/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.test;

import com.marklogic.client.pojo.annotation.PathIndexProperty;
import com.marklogic.client.pojo.annotation.PathIndexProperty.ScalarType;

public class Country {
  private String name, continent, currencyCode, currencyName, isoCode;

  public String getIsoCode() {
    return isoCode;
  }

  public Country setIsoCode(String isoCode) {
    this.isoCode = isoCode;
    return this;
  }

  public String getName() {
    return name;
  }

  public Country setName(String name) {
    this.name = name;
    return this;
  }

  @PathIndexProperty(scalarType=ScalarType.STRING)
  public String getContinent() {
    return continent;
  }

  public Country setContinent(String continent) {
    this.continent = continent;
    return this;
  }

  public String getCurrencyCode() {
    return currencyCode;
  }

  public Country setCurrencyCode(String currencyCode) {
    this.currencyCode = currencyCode;
    return this;
  }

  public String getCurrencyName() {
    return currencyName;
  }

  public Country setCurrencyName(String currencyName) {
    this.currencyName = currencyName;
    return this;
  }
}
