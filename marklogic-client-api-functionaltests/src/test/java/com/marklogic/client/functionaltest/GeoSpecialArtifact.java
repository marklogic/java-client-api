/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
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
