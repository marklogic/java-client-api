/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.functionaltest;

import com.marklogic.client.pojo.annotation.Id;

public class Artifact {
  @Id
  public long id;
  private String name;
  private Company manufacturer;
  // @IndexClassField
  private int inventory;

  public long getId() {
    return id;
  }

  public Artifact setId(long id) {
    this.id = id;
    return this;
  }

  public String getName() {
    return name;
  }

  public Artifact setName(String name) {
    this.name = name;
    return this;
  }

  public Company getManufacturer() {
    return manufacturer;
  }

  public Artifact setManufacturer(Company manufacturer) {
    this.manufacturer = manufacturer;
    return this;
  }

  public int getInventory() {
    return inventory;
  }

  public Artifact setInventory(int inventory) {
    this.inventory = inventory;
    return this;
  }
}
