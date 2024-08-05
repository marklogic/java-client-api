/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.functionaltest;

import com.marklogic.client.pojo.annotation.Id;
import com.marklogic.client.pojo.annotation.PathIndexProperty;
import com.marklogic.client.pojo.annotation.PathIndexProperty.ScalarType;

/*
 * This class is similar to the Artifact class. It is used to test path range index using the id field
 * indexed as string.
 * Property name been annotated with @Id.
 */
public class ArtifactIndexedOnIntAsString {
  @Id
  public String name;
  public long id;
  private Company manufacturer;

  // Note int is annotated as a string
  @PathIndexProperty(scalarType = ScalarType.STRING)
  private int inventory;

  public long getId() {
    return id;
  }

  public ArtifactIndexedOnIntAsString setId(long id) {
    this.id = id;
    return this;
  }

  public String getName() {
    return name;
  }

  public ArtifactIndexedOnIntAsString setName(String name) {
    this.name = name;
    return this;
  }

  public Company getManufacturer() {
    return manufacturer;
  }

  public ArtifactIndexedOnIntAsString setManufacturer(Company manufacturer) {
    this.manufacturer = manufacturer;
    return this;
  }

  public int getInventory() {
    return inventory;
  }

  public ArtifactIndexedOnIntAsString setInventory(int inventory) {
    this.inventory = inventory;
    return this;
  }
}
