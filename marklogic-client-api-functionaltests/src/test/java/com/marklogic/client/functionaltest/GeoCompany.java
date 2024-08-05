/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.functionaltest;

import com.marklogic.client.pojo.annotation.GeospatialLatitude;
import com.marklogic.client.pojo.annotation.GeospatialLongitude;
import com.marklogic.client.pojo.annotation.GeospatialPathIndexProperty;
import com.marklogic.client.pojo.annotation.Id;

public class GeoCompany {

  private String name;
  private String state;
  @GeospatialLatitude
  public double latitude;
  @GeospatialLongitude
  public double longitude;
  @GeospatialPathIndexProperty
  public String latlongPoint;

  public String getName() {
    return name;
  }

  @Id
  public GeoCompany setName(String name) {
    this.name = name;
    return this;
  }

  public String getState() {
    return state;
  }

  public GeoCompany setState(String state) {
    this.state = state;
    return this;
  }

  @GeospatialLatitude
  public double getLatitude() {
    return latitude;
  }

  public GeoCompany setLatitude(double latitude) {
    this.latitude = latitude;
    return this;
  }

  public GeoCompany setLatLongPoint(String latlong) {
    this.latlongPoint = latlong;
    return this;
  }

  public String getLatLongPoint() {
    return this.latlongPoint;
  }

  // @GeospatialLongitude
  public double getLongitude() {
    return longitude;
  }

  public GeoCompany setLongitude(double longitude) {
    this.longitude = longitude;
    return this;
  }

}
