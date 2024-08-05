/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.functionaltest;

import java.net.URI;

import com.marklogic.client.pojo.annotation.Id;
import com.marklogic.client.pojo.annotation.PathIndexProperty;
import com.marklogic.client.pojo.annotation.PathIndexProperty.ScalarType;

/*
 * This class is similar to the Artifact class. It is used to test path range index using the URI field
 * Property name annotated with @Id.
 */
public class ArtifactIndexedOnUri {
  @Id
  public String name;

  public long id;
  private Company manufacturer;
  private int inventory;

  @PathIndexProperty(scalarType = ScalarType.ANYURI)
  private URI artifactUri;

  public URI getArtifactUri() {
    return artifactUri;
  }

  public void setArtifactUri(URI artifactUri) {
    this.artifactUri = artifactUri;
  }

  public long getId() {
    return id;
  }

  public ArtifactIndexedOnUri setId(long id) {
    this.id = id;
    return this;
  }

  public String getName() {
    return name;
  }

  public ArtifactIndexedOnUri setName(String name) {
    this.name = name;
    return this;
  }

  public Company getManufacturer() {
    return manufacturer;
  }

  public ArtifactIndexedOnUri setManufacturer(Company manufacturer) {
    this.manufacturer = manufacturer;
    return this;
  }

  public int getInventory() {
    return inventory;
  }

  public ArtifactIndexedOnUri setInventory(int inventory) {
    this.inventory = inventory;
    return this;
  }
}
