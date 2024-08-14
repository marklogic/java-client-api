/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.test;

import com.marklogic.client.pojo.annotation.*;
import com.marklogic.client.pojo.annotation.PathIndexProperty.ScalarType;

import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class City {
  private int geoNameId;
  private String name;
  private String asciiName;
  @PathIndexProperty(scalarType=ScalarType.STRING)
  private String[] alternateNames;
  private double latitude;
  private double longitude;
  private String latLong;
  private String countryIsoCode;
  private String countryName;
  private String continent;
  private String currencyCode;
  private String currencyName;
  @PathIndexProperty(scalarType=ScalarType.LONG)
  private long population;
  private int elevation;
  private Country country;

  @Id
  public int getGeoNameId() {
    return geoNameId;
  }

  public City setGeoNameId(int geoNameId) {
    this.geoNameId = geoNameId;
    return this;
  }

  public String getName() {
    return name;
  }

  public City setName(String name) {
    this.name = name;
    return this;
  }

  public String getAsciiName() {
    return asciiName;
  }

  public City setAsciiName(String asciiName) {
    this.asciiName = asciiName;
    return this;
  }

  public String[] getAlternateNames() {
    return alternateNames;
  }

  public City setAlternateNames(String[] alternateNames) {
    this.alternateNames = alternateNames;
    return this;
  }

  @GeospatialLatitude
  public double getLatitude() {
    return latitude;
  }

  public City setLatitude(double latitude) {
    this.latitude = latitude;
    return this;
  }

  public double getLongitude() {
    return longitude;
  }

  @GeospatialLongitude
  public City setLongitude(double longitude) {
    this.longitude = longitude;
    return this;
  }

  @GeospatialPathIndexProperty
  public String getLatLong() {
    return latLong;
  }

  public City setLatLong(String latLong) {
    this.latLong = latLong;
    return this;
  }

  public String getCountryIsoCode() {
    return countryIsoCode;
  }

  public City setCountryIsoCode(String countryIsoCode) {
    this.countryIsoCode = countryIsoCode;
    return this;
  }

  public String getCountryName() {
    return countryName;
  }

  public City setCountryName(String countryName) {
    this.countryName = countryName;
    return this;
  }

  public String getContinent() {
    return continent;
  }

  public City setContinent(String continent) {
    this.continent = continent;
    return this;
  }

  public String getCurrencyCode() {
    return currencyCode;
  }

  public City setCurrencyCode(String currencyCode) {
    this.currencyCode = currencyCode;
    return this;
  }

  public String getCurrencyName() {
    return currencyName;
  }

  public City setCurrencyName(String currencyName) {
    this.currencyName = currencyName;
    return this;
  }

  public long getPopulation() {
    return population;
  }

  public City setPopulation(long population) {
    this.population = population;
    return this;
  }

  public int getElevation() {
    return elevation;
  }

  public City setElevation(int elevation) {
    this.elevation = elevation;
    return this;
  }

  public Country getCountry() {
    return country;
  }

  public City setCountry(Country country) {
    this.country = country;
    return this;
  }
}
