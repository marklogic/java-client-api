/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.functionaltest;

import java.util.Calendar;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.marklogic.client.pojo.annotation.Id;

/*
 * This class is similar to the Artifact class. It is used to test path range index using the Calendar field
 * Property name been annotated with @Id.
 */
public class ArtifactIndexedOnCalendar {
  @Id
  public String name;
  public long id;
  private Company manufacturer;
  private int inventory;

  @JsonTypeInfo(use = JsonTypeInfo.Id.NONE, include = JsonTypeInfo.As.EXTERNAL_PROPERTY)
  public Calendar expiryDate;

  public long getId() {
    return id;
  }

  public ArtifactIndexedOnCalendar setId(long id) {
    this.id = id;
    return this;
  }

  public String getName() {
    return name;
  }

  public ArtifactIndexedOnCalendar setName(String name) {
    this.name = name;
    return this;
  }

  public Company getManufacturer() {
    return manufacturer;
  }

  public ArtifactIndexedOnCalendar setManufacturer(Company manufacturer) {
    this.manufacturer = manufacturer;
    return this;
  }

  public int getInventory() {
    return inventory;
  }

  public ArtifactIndexedOnCalendar setInventory(int inventory) {
    this.inventory = inventory;
    return this;
  }

  public Calendar getExpiryDate() {
    return expiryDate;
  }

  public ArtifactIndexedOnCalendar setExpiryDate(Calendar expiryDate) {
    this.expiryDate = expiryDate;
    return this;
  }
}
