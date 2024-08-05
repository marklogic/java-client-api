/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.functionaltest;

import com.marklogic.client.pojo.annotation.PathIndexProperty;
import com.marklogic.client.pojo.annotation.PathIndexProperty.ScalarType;

/* This class is used to test range path index creation, when the Annotation is
 * in the super class and also in sub class on the same class property with same data type.
 *
 * Used to test annotation with collisions in a hierarchy.
 */

public class ArtifactMultipleIndexedOnInt extends ArtifactIndexedOnInt {
  @PathIndexProperty(scalarType = ScalarType.INT)
  private int inventory;

  public int getInventory() {
    return inventory;
  }

  public ArtifactMultipleIndexedOnInt setInventory(int inventory) {
    this.inventory = inventory;
    return this;
  }
}
