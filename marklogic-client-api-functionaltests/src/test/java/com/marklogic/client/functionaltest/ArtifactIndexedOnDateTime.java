/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.functionaltest;

import java.util.Calendar;

import com.marklogic.client.pojo.annotation.Id;
import com.marklogic.client.pojo.annotation.PathIndexProperty;
import com.marklogic.client.pojo.annotation.PathIndexProperty.ScalarType;

/*
 * This class is similar to the Artifact class. It is used to test path range index using the DateTime field
 * Property name been annotated with @Id.
 */
public class ArtifactIndexedOnDateTime {
  @Id
  public String name;
  public long id;
  private Company manufacturer;
  private int inventory;

  @PathIndexProperty(scalarType = ScalarType.DATETIME)
  private Calendar expiryDate;

  public long getId() {
    return id;
  }

  public ArtifactIndexedOnDateTime setId(long id) {
    this.id = id;
    return this;
  }

  public String getName() {
    return name;
  }

  public ArtifactIndexedOnDateTime setName(String name) {
    this.name = name;
    return this;
  }

  public Company getManufacturer() {
    return manufacturer;
  }

  public ArtifactIndexedOnDateTime setManufacturer(Company manufacturer) {
    this.manufacturer = manufacturer;
    return this;
  }

  public int getInventory() {
    return inventory;
  }

  public ArtifactIndexedOnDateTime setInventory(int inventory) {
    this.inventory = inventory;
    return this;
  }

  public Calendar getExpiryDate() {
    return expiryDate;
  }

  public ArtifactIndexedOnDateTime setExpiryDate(Calendar expiryDate) {
    this.expiryDate = expiryDate;
    return this;
  }
}
