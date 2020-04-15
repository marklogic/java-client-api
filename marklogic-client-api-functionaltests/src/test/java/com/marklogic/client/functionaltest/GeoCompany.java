/*
 * Copyright (c) 2019 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
