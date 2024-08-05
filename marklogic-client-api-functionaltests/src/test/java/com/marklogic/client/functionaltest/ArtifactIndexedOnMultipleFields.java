/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.functionaltest;

import com.marklogic.client.pojo.annotation.Id;
import com.marklogic.client.pojo.annotation.PathIndexProperty;
import com.marklogic.client.pojo.annotation.PathIndexProperty.ScalarType;

/*
 * This class is similar to the Artifact class. It is used to test path range index on multiple class fields
 * Property name been annotated with @Id.
 *
 */
public class ArtifactIndexedOnMultipleFields {
  @Id
  @PathIndexProperty(scalarType = ScalarType.STRING)
  public String name;
  public long id;
  private Company manufacturer;

  @PathIndexProperty(scalarType = ScalarType.INT)
  private int inventory;

  public long getId() {
    return id;
  }

  public ArtifactIndexedOnMultipleFields setId(long id) {
    this.id = id;
    return this;
  }

  public String getName() {
    return name;
  }

  public ArtifactIndexedOnMultipleFields setName(String name) {
    this.name = name;
    return this;
  }

  public Company getManufacturer() {
    return manufacturer;
  }

  public ArtifactIndexedOnMultipleFields setManufacturer(Company manufacturer) {
    this.manufacturer = manufacturer;
    return this;
  }

  public int getInventory() {
    return inventory;
  }

  public ArtifactIndexedOnMultipleFields setInventory(int inventory) {
    this.inventory = inventory;
    return this;
  }
}
