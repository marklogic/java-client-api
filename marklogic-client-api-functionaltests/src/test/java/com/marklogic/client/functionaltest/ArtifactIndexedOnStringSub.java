/*
 * Copyright (c) 2023 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
