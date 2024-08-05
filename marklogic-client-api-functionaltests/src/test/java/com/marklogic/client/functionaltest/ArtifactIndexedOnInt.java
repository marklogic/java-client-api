/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.functionaltest;

import com.marklogic.client.pojo.annotation.Id;
import com.marklogic.client.pojo.annotation.PathIndexProperty;
import com.marklogic.client.pojo.annotation.PathIndexProperty.ScalarType;

/*
 * This class is similar to the Artifact class. It is used to test path range index using the inventory field
 * Property name been annotated with @Id.
 */
public class ArtifactIndexedOnInt {
  @Id
  public String name;
  public long id;
  private Company manufacturer;

  @PathIndexProperty(scalarType = ScalarType.INT)
  private int inventory;

  public long getId() {
    return id;
  }

  public ArtifactIndexedOnInt setId(long id) {
    this.id = id;
    return this;
  }

  public String getName() {
    return name;
  }

  public ArtifactIndexedOnInt setName(String name) {
    this.name = name;
    return this;
  }

  public Company getManufacturer() {
    return manufacturer;
  }

  public ArtifactIndexedOnInt setManufacturer(Company manufacturer) {
    this.manufacturer = manufacturer;
    return this;
  }

  public int getInventory() {
    return inventory;
  }

  public ArtifactIndexedOnInt setInventory(int inventory) {
    this.inventory = inventory;
    return this;
  }
}
