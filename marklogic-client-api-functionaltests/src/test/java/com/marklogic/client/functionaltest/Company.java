/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.functionaltest;

public class Company {
  // @IndexClassField
  private String name;
  private String website;
  // @GeospatialLatitude
  private double latitude;
  // @GeospatialLongitude
  private double longitude;

  public String getName() {
    return name;
  }

  public Company setName(String name) {
    this.name = name;
    return this;
  }

  public String getWebsite() {
    return website;
  }

  public Company setWebsite(String website) {
    this.website = website;
    return this;
  }

  public double getLatitude() {
    return latitude;
  }

  public Company setLatitude(double latitude) {
    this.latitude = latitude;
    return this;
  }

  public double getLongitude() {
    return longitude;
  }

  public Company setLongitude(double longitude) {
    this.longitude = longitude;
    return this;
  }

}
