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

import com.marklogic.client.pojo.annotation.Id;
import com.marklogic.client.pojo.annotation.PathIndexProperty;
import com.marklogic.client.pojo.annotation.PathIndexProperty.ScalarType;

/*
 * This class is similar to the Artifact class. It is used to test path range index 
 * using the inventory field with Integer type.
 * Property name been annotated with @Id.
 */
public class ArtifactIndexedOnInteger {
  @Id
  public String name;
  public long id;
  private Company manufacturer;

  @PathIndexProperty(scalarType = ScalarType.INT)
  private Integer inventory;
  private int inventory1;

  public int getInventory1() {
    return inventory1;
  }

  public ArtifactIndexedOnInteger setInventory1(int inventory1) {
    this.inventory1 = inventory1;
    return this;
  }

  public long getId() {
    return id;
  }

  public ArtifactIndexedOnInteger setId(long id) {
    this.id = id;
    return this;
  }

  public String getName() {
    return name;
  }

  public ArtifactIndexedOnInteger setName(String name) {
    this.name = name;
    return this;
  }

  public Company getManufacturer() {
    return manufacturer;
  }

  public ArtifactIndexedOnInteger setManufacturer(Company manufacturer) {
    this.manufacturer = manufacturer;
    return this;
  }

  public Integer getInventory() {
    return inventory;
  }

  public ArtifactIndexedOnInteger setInventory(Integer inventory) {
    this.inventory = inventory;
    return this;
  }
}
