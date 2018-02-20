/*
 * Copyright 2014-2017 MarkLogic Corporation
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

import com.marklogic.client.pojo.annotation.Id;

public class GeoSpecialArtifact {
  public String name;
  @Id
  public long id;
  private GeoCompany manufacturer;
  private int inventory;

  public long getId() {
    return id;
  }

  public GeoSpecialArtifact setId(long id) {
    this.id = id;
    return this;
  }

  public String getName() {
    return name;
  }

  public GeoSpecialArtifact setName(String name) {
    this.name = name;
    return this;
  }

  public GeoCompany getManufacturer() {
    return manufacturer;
  }

  public GeoSpecialArtifact setManufacturer(GeoCompany manufacturer) {
    this.manufacturer = manufacturer;
    return this;
  }

  public int getInventory() {
    return inventory;
  }

  public GeoSpecialArtifact setInventory(int inventory) {
    this.inventory = inventory;
    return this;
  }

}
