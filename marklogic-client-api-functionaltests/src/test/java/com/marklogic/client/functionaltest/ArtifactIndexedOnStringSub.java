/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.functionaltest;

import com.marklogic.client.pojo.annotation.PathIndexProperty;
import com.marklogic.client.pojo.annotation.PathIndexProperty.ScalarType;

/* This class is used to test range path index creation, when the Annotation is
 * in the super class and sub class adds an additional annotation entry.
 *
 * Used to test annotation in a hierarchy.
 */
public class ArtifactIndexedOnStringSub extends ArtifactIndexedOnString {
  @PathIndexProperty(scalarType = ScalarType.DOUBLE)
  public double artifactWeight;

  public double getArtifactWeight() {
    return artifactWeight;
  }

  public ArtifactIndexedOnStringSub setArtifactWeight(double artifactWeight) {
    this.artifactWeight = artifactWeight;
    return this;
  }
}
