/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.functionaltest;

import com.marklogic.client.pojo.annotation.Id;
import com.marklogic.client.pojo.annotation.PathIndexProperty;
import com.marklogic.client.pojo.annotation.PathIndexProperty.ScalarType;

public class ArtifactIndexedUnSupportedDataType {
  @Id
  public long id;
  private String name;

  // Note: Any Type other than supported ones in ScalarType class will have
  // compile issues.
  @PathIndexProperty(scalarType = ScalarType.STRING)
  public Company manufacturer;
  private int inventory;

  public long getId() {
    return id;
  }

  public ArtifactIndexedUnSupportedDataType setId(long id) {
    this.id = id;
    return this;
  }

  public String getName() {
    return name;
  }

  public ArtifactIndexedUnSupportedDataType setName(String name) {
    this.name = name;
    return this;
  }

  public Company getManufacturer() {
    return manufacturer;
  }

  public ArtifactIndexedUnSupportedDataType setManufacturer(Company manufacturer) {
    this.manufacturer = manufacturer;
    return this;
  }

  public int getInventory() {
    return inventory;
  }

  public ArtifactIndexedUnSupportedDataType setInventory(int inventory) {
    this.inventory = inventory;
    return this;
  }
}
