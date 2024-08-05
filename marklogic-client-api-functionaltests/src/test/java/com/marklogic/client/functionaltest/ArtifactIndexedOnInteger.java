/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
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
