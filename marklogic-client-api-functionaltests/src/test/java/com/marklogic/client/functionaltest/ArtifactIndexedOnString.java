/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.functionaltest;

import com.marklogic.client.pojo.annotation.Id;
import com.marklogic.client.pojo.annotation.PathIndexProperty;
import com.marklogic.client.pojo.annotation.PathIndexProperty.ScalarType;

/*
 * This class is similar to the Artifact class. It is used to test path range index using the name field
 * which has been annotated with @Id also.
 */
public class ArtifactIndexedOnString {
  @Id
  @PathIndexProperty(scalarType = ScalarType.STRING)
  public String name;

  public long id;
  private Company manufacturer;
  private int inventory;

  public long getId() {
    return id;
  }

  public ArtifactIndexedOnString setId(long id) {
    this.id = id;
    return this;
  }

  public String getName() {
    return name;
  }

  public ArtifactIndexedOnString setName(String name) {
    this.name = name;
    return this;
  }

  public Company getManufacturer() {
    return manufacturer;
  }

  public ArtifactIndexedOnString setManufacturer(Company manufacturer) {
    this.manufacturer = manufacturer;
    return this;
  }

  public int getInventory() {
    return inventory;
  }

  public ArtifactIndexedOnString setInventory(int inventory) {
    this.inventory = inventory;
    return this;
  }
}
