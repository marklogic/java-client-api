/*
 * Copyright 2014-2019 MarkLogic Corporation
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

import java.util.Calendar;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.marklogic.client.pojo.annotation.Id;

/*
 * This class is similar to the Artifact class. It is used to test path range index using the Calendar field
 * Property name been annotated with @Id.
 */
public class ArtifactIndexedOnCalendar {
  @Id
  public String name;
  public long id;
  private Company manufacturer;
  private int inventory;

  @JsonTypeInfo(use = JsonTypeInfo.Id.NONE, include = JsonTypeInfo.As.EXTERNAL_PROPERTY)
  public Calendar expiryDate;

  public long getId() {
    return id;
  }

  public ArtifactIndexedOnCalendar setId(long id) {
    this.id = id;
    return this;
  }

  public String getName() {
    return name;
  }

  public ArtifactIndexedOnCalendar setName(String name) {
    this.name = name;
    return this;
  }

  public Company getManufacturer() {
    return manufacturer;
  }

  public ArtifactIndexedOnCalendar setManufacturer(Company manufacturer) {
    this.manufacturer = manufacturer;
    return this;
  }

  public int getInventory() {
    return inventory;
  }

  public ArtifactIndexedOnCalendar setInventory(int inventory) {
    this.inventory = inventory;
    return this;
  }

  public Calendar getExpiryDate() {
    return expiryDate;
  }

  public ArtifactIndexedOnCalendar setExpiryDate(Calendar expiryDate) {
    this.expiryDate = expiryDate;
    return this;
  }
}
