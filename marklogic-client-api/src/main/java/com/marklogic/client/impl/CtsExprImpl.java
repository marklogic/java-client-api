/*
 * Copyright 2016-2019 MarkLogic Corporation
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
package com.marklogic.client.impl;

import com.marklogic.client.type.XsAnyAtomicTypeSeqVal;
import com.marklogic.client.type.XsAnyAtomicTypeVal;
import com.marklogic.client.type.XsDateTimeVal;
import com.marklogic.client.type.XsDoubleVal;
import com.marklogic.client.type.XsQNameSeqVal;
import com.marklogic.client.type.XsQNameVal;
import com.marklogic.client.type.XsStringSeqVal;
import com.marklogic.client.type.XsStringVal;

import com.marklogic.client.type.ServerExpression;
import com.marklogic.client.type.CtsBoxExpr;
import com.marklogic.client.type.CtsBoxSeqExpr;
import com.marklogic.client.type.CtsCircleExpr;
import com.marklogic.client.type.CtsCircleSeqExpr;
import com.marklogic.client.type.CtsPeriodExpr;
import com.marklogic.client.type.CtsPeriodSeqExpr;
import com.marklogic.client.type.CtsPointExpr;
import com.marklogic.client.type.CtsPointSeqExpr;
import com.marklogic.client.type.CtsPolygonExpr;
import com.marklogic.client.type.CtsPolygonSeqExpr;
import com.marklogic.client.type.CtsQueryExpr;
import com.marklogic.client.type.CtsQuerySeqExpr;
import com.marklogic.client.type.CtsReferenceExpr;
import com.marklogic.client.type.CtsReferenceSeqExpr;
import com.marklogic.client.type.CtsRegionExpr;
import com.marklogic.client.type.CtsRegionSeqExpr;

import com.marklogic.client.expression.CtsExpr;
import com.marklogic.client.impl.BaseTypeImpl;

// IMPORTANT: Do not edit. This file is generated.
class CtsExprImpl implements CtsExpr {

  final static XsExprImpl xs = XsExprImpl.xs;

  final static CtsExprImpl cts = new CtsExprImpl();

  CtsExprImpl() {
  }

    
  @Override
  public CtsQueryExpr andNotQuery(CtsQueryExpr positiveQuery, CtsQueryExpr negativeQuery) {
    if (positiveQuery == null) {
      throw new IllegalArgumentException("positiveQuery parameter for andNotQuery() cannot be null");
    }
    if (negativeQuery == null) {
      throw new IllegalArgumentException("negativeQuery parameter for andNotQuery() cannot be null");
    }
    return new QueryCallImpl("cts", "and-not-query", new Object[]{ positiveQuery, negativeQuery });
  }

  
  @Override
  public CtsQueryExpr andQuery(CtsQueryExpr... queries) {
    return andQuery(new QuerySeqListImpl(queries));
  }

  
  @Override
  public CtsQueryExpr andQuery(CtsQuerySeqExpr queries) {
    return new QueryCallImpl("cts", "and-query", new Object[]{ queries });
  }

  
  @Override
  public CtsQueryExpr andQuery(CtsQuerySeqExpr queries, String options) {
    return andQuery(queries, (options == null) ? (XsStringVal) null : xs.string(options));
  }

  
  @Override
  public CtsQueryExpr andQuery(CtsQuerySeqExpr queries, XsStringSeqVal options) {
    return new QueryCallImpl("cts", "and-query", new Object[]{ queries, options });
  }

  
  @Override
  public CtsQueryExpr boostQuery(CtsQueryExpr matchingQuery, CtsQueryExpr boostingQuery) {
    if (matchingQuery == null) {
      throw new IllegalArgumentException("matchingQuery parameter for boostQuery() cannot be null");
    }
    if (boostingQuery == null) {
      throw new IllegalArgumentException("boostingQuery parameter for boostQuery() cannot be null");
    }
    return new QueryCallImpl("cts", "boost-query", new Object[]{ matchingQuery, boostingQuery });
  }

  
  @Override
  public CtsBoxExpr box(double south, double west, double north, double east) {
    return box(xs.doubleVal(south), xs.doubleVal(west), xs.doubleVal(north), xs.doubleVal(east));
  }

  
  @Override
  public CtsBoxExpr box(XsDoubleVal south, XsDoubleVal west, XsDoubleVal north, XsDoubleVal east) {
    if (south == null) {
      throw new IllegalArgumentException("south parameter for box() cannot be null");
    }
    if (west == null) {
      throw new IllegalArgumentException("west parameter for box() cannot be null");
    }
    if (north == null) {
      throw new IllegalArgumentException("north parameter for box() cannot be null");
    }
    if (east == null) {
      throw new IllegalArgumentException("east parameter for box() cannot be null");
    }
    return new BoxCallImpl("cts", "box", new Object[]{ south, west, north, east });
  }

  
  @Override
  public CtsCircleExpr circle(double radius, CtsPointExpr center) {
    return circle(xs.doubleVal(radius), center);
  }

  
  @Override
  public CtsCircleExpr circle(XsDoubleVal radius, CtsPointExpr center) {
    if (radius == null) {
      throw new IllegalArgumentException("radius parameter for circle() cannot be null");
    }
    if (center == null) {
      throw new IllegalArgumentException("center parameter for circle() cannot be null");
    }
    return new CircleCallImpl("cts", "circle", new Object[]{ radius, center });
  }

  
  @Override
  public CtsQueryExpr collectionQuery(String uris) {
    return collectionQuery((uris == null) ? (XsStringVal) null : xs.string(uris));
  }

  
  @Override
  public CtsQueryExpr collectionQuery(XsStringSeqVal uris) {
    return new QueryCallImpl("cts", "collection-query", new Object[]{ uris });
  }

  
  @Override
  public CtsReferenceExpr collectionReference() {
    return new ReferenceCallImpl("cts", "collection-reference", new Object[]{  });
  }

  
  @Override
  public CtsReferenceExpr collectionReference(String options) {
    return collectionReference((options == null) ? (XsStringVal) null : xs.string(options));
  }

  
  @Override
  public CtsReferenceExpr collectionReference(XsStringSeqVal options) {
    return new ReferenceCallImpl("cts", "collection-reference", new Object[]{ options });
  }

  
  @Override
  public CtsQueryExpr directoryQuery(String uris) {
    return directoryQuery((uris == null) ? (XsStringVal) null : xs.string(uris));
  }

  
  @Override
  public CtsQueryExpr directoryQuery(XsStringSeqVal uris) {
    return new QueryCallImpl("cts", "directory-query", new Object[]{ uris });
  }

  
  @Override
  public CtsQueryExpr directoryQuery(String uris, String depth) {
    return directoryQuery((uris == null) ? (XsStringVal) null : xs.string(uris), (depth == null) ? (XsStringVal) null : xs.string(depth));
  }

  
  @Override
  public CtsQueryExpr directoryQuery(XsStringSeqVal uris, XsStringVal depth) {
    return new QueryCallImpl("cts", "directory-query", new Object[]{ uris, depth });
  }

  
  @Override
  public CtsQueryExpr documentFragmentQuery(CtsQueryExpr query) {
    if (query == null) {
      throw new IllegalArgumentException("query parameter for documentFragmentQuery() cannot be null");
    }
    return new QueryCallImpl("cts", "document-fragment-query", new Object[]{ query });
  }

  
  @Override
  public CtsQueryExpr documentQuery(String uris) {
    return documentQuery((uris == null) ? (XsStringVal) null : xs.string(uris));
  }

  
  @Override
  public CtsQueryExpr documentQuery(XsStringSeqVal uris) {
    return new QueryCallImpl("cts", "document-query", new Object[]{ uris });
  }

  
  @Override
  public CtsQueryExpr elementAttributePairGeospatialQuery(String elementName, String latitudeAttributeNames, String longitudeAttributeNames, CtsRegionExpr... regions) {
    return elementAttributePairGeospatialQuery((elementName == null) ? (XsQNameVal) null : xs.QName(elementName), (latitudeAttributeNames == null) ? (XsQNameVal) null : xs.QName(latitudeAttributeNames), (longitudeAttributeNames == null) ? (XsQNameVal) null : xs.QName(longitudeAttributeNames), new RegionSeqListImpl(regions));
  }

  
  @Override
  public CtsQueryExpr elementAttributePairGeospatialQuery(XsQNameSeqVal elementName, XsQNameSeqVal latitudeAttributeNames, XsQNameSeqVal longitudeAttributeNames, CtsRegionSeqExpr regions) {
    return new QueryCallImpl("cts", "element-attribute-pair-geospatial-query", new Object[]{ elementName, latitudeAttributeNames, longitudeAttributeNames, regions });
  }

  
  @Override
  public CtsQueryExpr elementAttributePairGeospatialQuery(String elementName, String latitudeAttributeNames, String longitudeAttributeNames, CtsRegionSeqExpr regions, String... options) {
    return elementAttributePairGeospatialQuery((elementName == null) ? (XsQNameVal) null : xs.QName(elementName), (latitudeAttributeNames == null) ? (XsQNameVal) null : xs.QName(latitudeAttributeNames), (longitudeAttributeNames == null) ? (XsQNameVal) null : xs.QName(longitudeAttributeNames), regions, (options == null) ? (XsStringVal) null : xs.stringSeq(options));
  }

  
  @Override
  public CtsQueryExpr elementAttributePairGeospatialQuery(XsQNameSeqVal elementName, XsQNameSeqVal latitudeAttributeNames, XsQNameSeqVal longitudeAttributeNames, CtsRegionSeqExpr regions, XsStringSeqVal options) {
    return new QueryCallImpl("cts", "element-attribute-pair-geospatial-query", new Object[]{ elementName, latitudeAttributeNames, longitudeAttributeNames, regions, options });
  }

  
  @Override
  public CtsQueryExpr elementAttributePairGeospatialQuery(String elementName, String latitudeAttributeNames, String longitudeAttributeNames, CtsRegionSeqExpr regions, String options, double weight) {
    return elementAttributePairGeospatialQuery((elementName == null) ? (XsQNameVal) null : xs.QName(elementName), (latitudeAttributeNames == null) ? (XsQNameVal) null : xs.QName(latitudeAttributeNames), (longitudeAttributeNames == null) ? (XsQNameVal) null : xs.QName(longitudeAttributeNames), regions, (options == null) ? (XsStringVal) null : xs.string(options), xs.doubleVal(weight));
  }

  
  @Override
  public CtsQueryExpr elementAttributePairGeospatialQuery(XsQNameSeqVal elementName, XsQNameSeqVal latitudeAttributeNames, XsQNameSeqVal longitudeAttributeNames, CtsRegionSeqExpr regions, XsStringSeqVal options, XsDoubleVal weight) {
    return new QueryCallImpl("cts", "element-attribute-pair-geospatial-query", new Object[]{ elementName, latitudeAttributeNames, longitudeAttributeNames, regions, options, weight });
  }

  
  @Override
  public CtsQueryExpr elementAttributeRangeQuery(String elementName, String attributeName, String operator, String value) {
    return elementAttributeRangeQuery((elementName == null) ? (XsQNameVal) null : xs.QName(elementName), (attributeName == null) ? (XsQNameVal) null : xs.QName(attributeName), (operator == null) ? (XsStringVal) null : xs.string(operator), (value == null) ? (XsAnyAtomicTypeVal) null : xs.string(value));
  }

  
  @Override
  public CtsQueryExpr elementAttributeRangeQuery(XsQNameSeqVal elementName, XsQNameSeqVal attributeName, XsStringVal operator, XsAnyAtomicTypeSeqVal value) {
    if (operator == null) {
      throw new IllegalArgumentException("operator parameter for elementAttributeRangeQuery() cannot be null");
    }
    return new QueryCallImpl("cts", "element-attribute-range-query", new Object[]{ elementName, attributeName, operator, value });
  }

  
  @Override
  public CtsQueryExpr elementAttributeRangeQuery(String elementName, String attributeName, String operator, String value, String... options) {
    return elementAttributeRangeQuery((elementName == null) ? (XsQNameVal) null : xs.QName(elementName), (attributeName == null) ? (XsQNameVal) null : xs.QName(attributeName), (operator == null) ? (XsStringVal) null : xs.string(operator), (value == null) ? (XsAnyAtomicTypeVal) null : xs.string(value), (options == null) ? (XsStringVal) null : xs.stringSeq(options));
  }

  
  @Override
  public CtsQueryExpr elementAttributeRangeQuery(XsQNameSeqVal elementName, XsQNameSeqVal attributeName, XsStringVal operator, XsAnyAtomicTypeSeqVal value, XsStringSeqVal options) {
    if (operator == null) {
      throw new IllegalArgumentException("operator parameter for elementAttributeRangeQuery() cannot be null");
    }
    return new QueryCallImpl("cts", "element-attribute-range-query", new Object[]{ elementName, attributeName, operator, value, options });
  }

  
  @Override
  public CtsQueryExpr elementAttributeRangeQuery(String elementName, String attributeName, String operator, String value, String options, double weight) {
    return elementAttributeRangeQuery((elementName == null) ? (XsQNameVal) null : xs.QName(elementName), (attributeName == null) ? (XsQNameVal) null : xs.QName(attributeName), (operator == null) ? (XsStringVal) null : xs.string(operator), (value == null) ? (XsAnyAtomicTypeVal) null : xs.string(value), (options == null) ? (XsStringVal) null : xs.string(options), xs.doubleVal(weight));
  }

  
  @Override
  public CtsQueryExpr elementAttributeRangeQuery(XsQNameSeqVal elementName, XsQNameSeqVal attributeName, XsStringVal operator, XsAnyAtomicTypeSeqVal value, XsStringSeqVal options, XsDoubleVal weight) {
    if (operator == null) {
      throw new IllegalArgumentException("operator parameter for elementAttributeRangeQuery() cannot be null");
    }
    return new QueryCallImpl("cts", "element-attribute-range-query", new Object[]{ elementName, attributeName, operator, value, options, weight });
  }

  
  @Override
  public CtsReferenceExpr elementAttributeReference(String element, String attribute) {
    return elementAttributeReference((element == null) ? (XsQNameVal) null : xs.QName(element), (attribute == null) ? (XsQNameVal) null : xs.QName(attribute));
  }

  
  @Override
  public CtsReferenceExpr elementAttributeReference(XsQNameVal element, XsQNameVal attribute) {
    if (element == null) {
      throw new IllegalArgumentException("element parameter for elementAttributeReference() cannot be null");
    }
    if (attribute == null) {
      throw new IllegalArgumentException("attribute parameter for elementAttributeReference() cannot be null");
    }
    return new ReferenceCallImpl("cts", "element-attribute-reference", new Object[]{ element, attribute });
  }

  
  @Override
  public CtsReferenceExpr elementAttributeReference(String element, String attribute, String options) {
    return elementAttributeReference((element == null) ? (XsQNameVal) null : xs.QName(element), (attribute == null) ? (XsQNameVal) null : xs.QName(attribute), (options == null) ? (XsStringVal) null : xs.string(options));
  }

  
  @Override
  public CtsReferenceExpr elementAttributeReference(XsQNameVal element, XsQNameVal attribute, XsStringSeqVal options) {
    if (element == null) {
      throw new IllegalArgumentException("element parameter for elementAttributeReference() cannot be null");
    }
    if (attribute == null) {
      throw new IllegalArgumentException("attribute parameter for elementAttributeReference() cannot be null");
    }
    return new ReferenceCallImpl("cts", "element-attribute-reference", new Object[]{ element, attribute, options });
  }

  
  @Override
  public CtsQueryExpr elementAttributeValueQuery(String elementName, String attributeName, String text) {
    return elementAttributeValueQuery((elementName == null) ? (XsQNameVal) null : xs.QName(elementName), (attributeName == null) ? (XsQNameVal) null : xs.QName(attributeName), (text == null) ? (XsStringVal) null : xs.string(text));
  }

  
  @Override
  public CtsQueryExpr elementAttributeValueQuery(XsQNameSeqVal elementName, XsQNameSeqVal attributeName, XsStringSeqVal text) {
    return new QueryCallImpl("cts", "element-attribute-value-query", new Object[]{ elementName, attributeName, text });
  }

  
  @Override
  public CtsQueryExpr elementAttributeValueQuery(String elementName, String attributeName, String text, String... options) {
    return elementAttributeValueQuery((elementName == null) ? (XsQNameVal) null : xs.QName(elementName), (attributeName == null) ? (XsQNameVal) null : xs.QName(attributeName), (text == null) ? (XsStringVal) null : xs.string(text), (options == null) ? (XsStringVal) null : xs.stringSeq(options));
  }

  
  @Override
  public CtsQueryExpr elementAttributeValueQuery(XsQNameSeqVal elementName, XsQNameSeqVal attributeName, XsStringSeqVal text, XsStringSeqVal options) {
    return new QueryCallImpl("cts", "element-attribute-value-query", new Object[]{ elementName, attributeName, text, options });
  }

  
  @Override
  public CtsQueryExpr elementAttributeValueQuery(String elementName, String attributeName, String text, String options, double weight) {
    return elementAttributeValueQuery((elementName == null) ? (XsQNameVal) null : xs.QName(elementName), (attributeName == null) ? (XsQNameVal) null : xs.QName(attributeName), (text == null) ? (XsStringVal) null : xs.string(text), (options == null) ? (XsStringVal) null : xs.string(options), xs.doubleVal(weight));
  }

  
  @Override
  public CtsQueryExpr elementAttributeValueQuery(XsQNameSeqVal elementName, XsQNameSeqVal attributeName, XsStringSeqVal text, XsStringSeqVal options, XsDoubleVal weight) {
    return new QueryCallImpl("cts", "element-attribute-value-query", new Object[]{ elementName, attributeName, text, options, weight });
  }

  
  @Override
  public CtsQueryExpr elementAttributeWordQuery(String elementName, String attributeName, String text) {
    return elementAttributeWordQuery((elementName == null) ? (XsQNameVal) null : xs.QName(elementName), (attributeName == null) ? (XsQNameVal) null : xs.QName(attributeName), (text == null) ? (XsStringVal) null : xs.string(text));
  }

  
  @Override
  public CtsQueryExpr elementAttributeWordQuery(XsQNameSeqVal elementName, XsQNameSeqVal attributeName, XsStringSeqVal text) {
    return new QueryCallImpl("cts", "element-attribute-word-query", new Object[]{ elementName, attributeName, text });
  }

  
  @Override
  public CtsQueryExpr elementAttributeWordQuery(String elementName, String attributeName, String text, String... options) {
    return elementAttributeWordQuery((elementName == null) ? (XsQNameVal) null : xs.QName(elementName), (attributeName == null) ? (XsQNameVal) null : xs.QName(attributeName), (text == null) ? (XsStringVal) null : xs.string(text), (options == null) ? (XsStringVal) null : xs.stringSeq(options));
  }

  
  @Override
  public CtsQueryExpr elementAttributeWordQuery(XsQNameSeqVal elementName, XsQNameSeqVal attributeName, XsStringSeqVal text, XsStringSeqVal options) {
    return new QueryCallImpl("cts", "element-attribute-word-query", new Object[]{ elementName, attributeName, text, options });
  }

  
  @Override
  public CtsQueryExpr elementAttributeWordQuery(String elementName, String attributeName, String text, String options, double weight) {
    return elementAttributeWordQuery((elementName == null) ? (XsQNameVal) null : xs.QName(elementName), (attributeName == null) ? (XsQNameVal) null : xs.QName(attributeName), (text == null) ? (XsStringVal) null : xs.string(text), (options == null) ? (XsStringVal) null : xs.string(options), xs.doubleVal(weight));
  }

  
  @Override
  public CtsQueryExpr elementAttributeWordQuery(XsQNameSeqVal elementName, XsQNameSeqVal attributeName, XsStringSeqVal text, XsStringSeqVal options, XsDoubleVal weight) {
    return new QueryCallImpl("cts", "element-attribute-word-query", new Object[]{ elementName, attributeName, text, options, weight });
  }

  
  @Override
  public CtsQueryExpr elementChildGeospatialQuery(String parentElementName, String childElementNames, CtsRegionExpr... regions) {
    return elementChildGeospatialQuery((parentElementName == null) ? (XsQNameVal) null : xs.QName(parentElementName), (childElementNames == null) ? (XsQNameVal) null : xs.QName(childElementNames), new RegionSeqListImpl(regions));
  }

  
  @Override
  public CtsQueryExpr elementChildGeospatialQuery(XsQNameSeqVal parentElementName, XsQNameSeqVal childElementNames, CtsRegionSeqExpr regions) {
    return new QueryCallImpl("cts", "element-child-geospatial-query", new Object[]{ parentElementName, childElementNames, regions });
  }

  
  @Override
  public CtsQueryExpr elementChildGeospatialQuery(String parentElementName, String childElementNames, CtsRegionSeqExpr regions, String... options) {
    return elementChildGeospatialQuery((parentElementName == null) ? (XsQNameVal) null : xs.QName(parentElementName), (childElementNames == null) ? (XsQNameVal) null : xs.QName(childElementNames), regions, (options == null) ? (XsStringVal) null : xs.stringSeq(options));
  }

  
  @Override
  public CtsQueryExpr elementChildGeospatialQuery(XsQNameSeqVal parentElementName, XsQNameSeqVal childElementNames, CtsRegionSeqExpr regions, XsStringSeqVal options) {
    return new QueryCallImpl("cts", "element-child-geospatial-query", new Object[]{ parentElementName, childElementNames, regions, options });
  }

  
  @Override
  public CtsQueryExpr elementChildGeospatialQuery(String parentElementName, String childElementNames, CtsRegionSeqExpr regions, String options, double weight) {
    return elementChildGeospatialQuery((parentElementName == null) ? (XsQNameVal) null : xs.QName(parentElementName), (childElementNames == null) ? (XsQNameVal) null : xs.QName(childElementNames), regions, (options == null) ? (XsStringVal) null : xs.string(options), xs.doubleVal(weight));
  }

  
  @Override
  public CtsQueryExpr elementChildGeospatialQuery(XsQNameSeqVal parentElementName, XsQNameSeqVal childElementNames, CtsRegionSeqExpr regions, XsStringSeqVal options, XsDoubleVal weight) {
    return new QueryCallImpl("cts", "element-child-geospatial-query", new Object[]{ parentElementName, childElementNames, regions, options, weight });
  }

  
  @Override
  public CtsQueryExpr elementGeospatialQuery(String elementName, CtsRegionExpr... regions) {
    return elementGeospatialQuery((elementName == null) ? (XsQNameVal) null : xs.QName(elementName), new RegionSeqListImpl(regions));
  }

  
  @Override
  public CtsQueryExpr elementGeospatialQuery(XsQNameSeqVal elementName, CtsRegionSeqExpr regions) {
    return new QueryCallImpl("cts", "element-geospatial-query", new Object[]{ elementName, regions });
  }

  
  @Override
  public CtsQueryExpr elementGeospatialQuery(String elementName, CtsRegionSeqExpr regions, String... options) {
    return elementGeospatialQuery((elementName == null) ? (XsQNameVal) null : xs.QName(elementName), regions, (options == null) ? (XsStringVal) null : xs.stringSeq(options));
  }

  
  @Override
  public CtsQueryExpr elementGeospatialQuery(XsQNameSeqVal elementName, CtsRegionSeqExpr regions, XsStringSeqVal options) {
    return new QueryCallImpl("cts", "element-geospatial-query", new Object[]{ elementName, regions, options });
  }

  
  @Override
  public CtsQueryExpr elementGeospatialQuery(String elementName, CtsRegionSeqExpr regions, String options, double weight) {
    return elementGeospatialQuery((elementName == null) ? (XsQNameVal) null : xs.QName(elementName), regions, (options == null) ? (XsStringVal) null : xs.string(options), xs.doubleVal(weight));
  }

  
  @Override
  public CtsQueryExpr elementGeospatialQuery(XsQNameSeqVal elementName, CtsRegionSeqExpr regions, XsStringSeqVal options, XsDoubleVal weight) {
    return new QueryCallImpl("cts", "element-geospatial-query", new Object[]{ elementName, regions, options, weight });
  }

  
  @Override
  public CtsQueryExpr elementPairGeospatialQuery(String elementName, String latitudeElementNames, String longitudeElementNames, CtsRegionExpr... regions) {
    return elementPairGeospatialQuery((elementName == null) ? (XsQNameVal) null : xs.QName(elementName), (latitudeElementNames == null) ? (XsQNameVal) null : xs.QName(latitudeElementNames), (longitudeElementNames == null) ? (XsQNameVal) null : xs.QName(longitudeElementNames), new RegionSeqListImpl(regions));
  }

  
  @Override
  public CtsQueryExpr elementPairGeospatialQuery(XsQNameSeqVal elementName, XsQNameSeqVal latitudeElementNames, XsQNameSeqVal longitudeElementNames, CtsRegionSeqExpr regions) {
    return new QueryCallImpl("cts", "element-pair-geospatial-query", new Object[]{ elementName, latitudeElementNames, longitudeElementNames, regions });
  }

  
  @Override
  public CtsQueryExpr elementPairGeospatialQuery(String elementName, String latitudeElementNames, String longitudeElementNames, CtsRegionSeqExpr regions, String... options) {
    return elementPairGeospatialQuery((elementName == null) ? (XsQNameVal) null : xs.QName(elementName), (latitudeElementNames == null) ? (XsQNameVal) null : xs.QName(latitudeElementNames), (longitudeElementNames == null) ? (XsQNameVal) null : xs.QName(longitudeElementNames), regions, (options == null) ? (XsStringVal) null : xs.stringSeq(options));
  }

  
  @Override
  public CtsQueryExpr elementPairGeospatialQuery(XsQNameSeqVal elementName, XsQNameSeqVal latitudeElementNames, XsQNameSeqVal longitudeElementNames, CtsRegionSeqExpr regions, XsStringSeqVal options) {
    return new QueryCallImpl("cts", "element-pair-geospatial-query", new Object[]{ elementName, latitudeElementNames, longitudeElementNames, regions, options });
  }

  
  @Override
  public CtsQueryExpr elementPairGeospatialQuery(String elementName, String latitudeElementNames, String longitudeElementNames, CtsRegionSeqExpr regions, String options, double weight) {
    return elementPairGeospatialQuery((elementName == null) ? (XsQNameVal) null : xs.QName(elementName), (latitudeElementNames == null) ? (XsQNameVal) null : xs.QName(latitudeElementNames), (longitudeElementNames == null) ? (XsQNameVal) null : xs.QName(longitudeElementNames), regions, (options == null) ? (XsStringVal) null : xs.string(options), xs.doubleVal(weight));
  }

  
  @Override
  public CtsQueryExpr elementPairGeospatialQuery(XsQNameSeqVal elementName, XsQNameSeqVal latitudeElementNames, XsQNameSeqVal longitudeElementNames, CtsRegionSeqExpr regions, XsStringSeqVal options, XsDoubleVal weight) {
    return new QueryCallImpl("cts", "element-pair-geospatial-query", new Object[]{ elementName, latitudeElementNames, longitudeElementNames, regions, options, weight });
  }

  
  @Override
  public CtsQueryExpr elementQuery(String elementName, CtsQueryExpr query) {
    return elementQuery((elementName == null) ? (XsQNameVal) null : xs.QName(elementName), query);
  }

  
  @Override
  public CtsQueryExpr elementQuery(XsQNameSeqVal elementName, CtsQueryExpr query) {
    if (query == null) {
      throw new IllegalArgumentException("query parameter for elementQuery() cannot be null");
    }
    return new QueryCallImpl("cts", "element-query", new Object[]{ elementName, query });
  }

  
  @Override
  public CtsQueryExpr elementRangeQuery(String elementName, String operator, String value) {
    return elementRangeQuery((elementName == null) ? (XsQNameVal) null : xs.QName(elementName), (operator == null) ? (XsStringVal) null : xs.string(operator), (value == null) ? (XsAnyAtomicTypeVal) null : xs.string(value));
  }

  
  @Override
  public CtsQueryExpr elementRangeQuery(XsQNameSeqVal elementName, XsStringVal operator, XsAnyAtomicTypeSeqVal value) {
    if (operator == null) {
      throw new IllegalArgumentException("operator parameter for elementRangeQuery() cannot be null");
    }
    return new QueryCallImpl("cts", "element-range-query", new Object[]{ elementName, operator, value });
  }

  
  @Override
  public CtsQueryExpr elementRangeQuery(String elementName, String operator, String value, String... options) {
    return elementRangeQuery((elementName == null) ? (XsQNameVal) null : xs.QName(elementName), (operator == null) ? (XsStringVal) null : xs.string(operator), (value == null) ? (XsAnyAtomicTypeVal) null : xs.string(value), (options == null) ? (XsStringVal) null : xs.stringSeq(options));
  }

  
  @Override
  public CtsQueryExpr elementRangeQuery(XsQNameSeqVal elementName, XsStringVal operator, XsAnyAtomicTypeSeqVal value, XsStringSeqVal options) {
    if (operator == null) {
      throw new IllegalArgumentException("operator parameter for elementRangeQuery() cannot be null");
    }
    return new QueryCallImpl("cts", "element-range-query", new Object[]{ elementName, operator, value, options });
  }

  
  @Override
  public CtsQueryExpr elementRangeQuery(String elementName, String operator, String value, String options, double weight) {
    return elementRangeQuery((elementName == null) ? (XsQNameVal) null : xs.QName(elementName), (operator == null) ? (XsStringVal) null : xs.string(operator), (value == null) ? (XsAnyAtomicTypeVal) null : xs.string(value), (options == null) ? (XsStringVal) null : xs.string(options), xs.doubleVal(weight));
  }

  
  @Override
  public CtsQueryExpr elementRangeQuery(XsQNameSeqVal elementName, XsStringVal operator, XsAnyAtomicTypeSeqVal value, XsStringSeqVal options, XsDoubleVal weight) {
    if (operator == null) {
      throw new IllegalArgumentException("operator parameter for elementRangeQuery() cannot be null");
    }
    return new QueryCallImpl("cts", "element-range-query", new Object[]{ elementName, operator, value, options, weight });
  }

  
  @Override
  public CtsReferenceExpr elementReference(String element) {
    return elementReference((element == null) ? (XsQNameVal) null : xs.QName(element));
  }

  
  @Override
  public CtsReferenceExpr elementReference(XsQNameVal element) {
    if (element == null) {
      throw new IllegalArgumentException("element parameter for elementReference() cannot be null");
    }
    return new ReferenceCallImpl("cts", "element-reference", new Object[]{ element });
  }

  
  @Override
  public CtsReferenceExpr elementReference(String element, String options) {
    return elementReference((element == null) ? (XsQNameVal) null : xs.QName(element), (options == null) ? (XsStringVal) null : xs.string(options));
  }

  
  @Override
  public CtsReferenceExpr elementReference(XsQNameVal element, XsStringSeqVal options) {
    if (element == null) {
      throw new IllegalArgumentException("element parameter for elementReference() cannot be null");
    }
    return new ReferenceCallImpl("cts", "element-reference", new Object[]{ element, options });
  }

  
  @Override
  public CtsQueryExpr elementValueQuery(String elementName) {
    return elementValueQuery((elementName == null) ? (XsQNameVal) null : xs.QName(elementName));
  }

  
  @Override
  public CtsQueryExpr elementValueQuery(XsQNameSeqVal elementName) {
    return new QueryCallImpl("cts", "element-value-query", new Object[]{ elementName });
  }

  
  @Override
  public CtsQueryExpr elementValueQuery(String elementName, String text) {
    return elementValueQuery((elementName == null) ? (XsQNameVal) null : xs.QName(elementName), (text == null) ? (XsStringVal) null : xs.string(text));
  }

  
  @Override
  public CtsQueryExpr elementValueQuery(XsQNameSeqVal elementName, XsStringSeqVal text) {
    return new QueryCallImpl("cts", "element-value-query", new Object[]{ elementName, text });
  }

  
  @Override
  public CtsQueryExpr elementValueQuery(String elementName, String text, String... options) {
    return elementValueQuery((elementName == null) ? (XsQNameVal) null : xs.QName(elementName), (text == null) ? (XsStringVal) null : xs.string(text), (options == null) ? (XsStringVal) null : xs.stringSeq(options));
  }

  
  @Override
  public CtsQueryExpr elementValueQuery(XsQNameSeqVal elementName, XsStringSeqVal text, XsStringSeqVal options) {
    return new QueryCallImpl("cts", "element-value-query", new Object[]{ elementName, text, options });
  }

  
  @Override
  public CtsQueryExpr elementValueQuery(String elementName, String text, String options, double weight) {
    return elementValueQuery((elementName == null) ? (XsQNameVal) null : xs.QName(elementName), (text == null) ? (XsStringVal) null : xs.string(text), (options == null) ? (XsStringVal) null : xs.string(options), xs.doubleVal(weight));
  }

  
  @Override
  public CtsQueryExpr elementValueQuery(XsQNameSeqVal elementName, XsStringSeqVal text, XsStringSeqVal options, XsDoubleVal weight) {
    return new QueryCallImpl("cts", "element-value-query", new Object[]{ elementName, text, options, weight });
  }

  
  @Override
  public CtsQueryExpr elementWordQuery(String elementName, String text) {
    return elementWordQuery((elementName == null) ? (XsQNameVal) null : xs.QName(elementName), (text == null) ? (XsStringVal) null : xs.string(text));
  }

  
  @Override
  public CtsQueryExpr elementWordQuery(XsQNameSeqVal elementName, XsStringSeqVal text) {
    return new QueryCallImpl("cts", "element-word-query", new Object[]{ elementName, text });
  }

  
  @Override
  public CtsQueryExpr elementWordQuery(String elementName, String text, String... options) {
    return elementWordQuery((elementName == null) ? (XsQNameVal) null : xs.QName(elementName), (text == null) ? (XsStringVal) null : xs.string(text), (options == null) ? (XsStringVal) null : xs.stringSeq(options));
  }

  
  @Override
  public CtsQueryExpr elementWordQuery(XsQNameSeqVal elementName, XsStringSeqVal text, XsStringSeqVal options) {
    return new QueryCallImpl("cts", "element-word-query", new Object[]{ elementName, text, options });
  }

  
  @Override
  public CtsQueryExpr elementWordQuery(String elementName, String text, String options, double weight) {
    return elementWordQuery((elementName == null) ? (XsQNameVal) null : xs.QName(elementName), (text == null) ? (XsStringVal) null : xs.string(text), (options == null) ? (XsStringVal) null : xs.string(options), xs.doubleVal(weight));
  }

  
  @Override
  public CtsQueryExpr elementWordQuery(XsQNameSeqVal elementName, XsStringSeqVal text, XsStringSeqVal options, XsDoubleVal weight) {
    return new QueryCallImpl("cts", "element-word-query", new Object[]{ elementName, text, options, weight });
  }

  
  @Override
  public CtsQueryExpr falseQuery() {
    return new QueryCallImpl("cts", "false-query", new Object[]{  });
  }

  
  @Override
  public CtsQueryExpr fieldRangeQuery(String fieldName, String operator, String value) {
    return fieldRangeQuery((fieldName == null) ? (XsStringVal) null : xs.string(fieldName), (operator == null) ? (XsStringVal) null : xs.string(operator), (value == null) ? (XsAnyAtomicTypeVal) null : xs.string(value));
  }

  
  @Override
  public CtsQueryExpr fieldRangeQuery(XsStringSeqVal fieldName, XsStringVal operator, XsAnyAtomicTypeSeqVal value) {
    if (operator == null) {
      throw new IllegalArgumentException("operator parameter for fieldRangeQuery() cannot be null");
    }
    return new QueryCallImpl("cts", "field-range-query", new Object[]{ fieldName, operator, value });
  }

  
  @Override
  public CtsQueryExpr fieldRangeQuery(String fieldName, String operator, String value, String... options) {
    return fieldRangeQuery((fieldName == null) ? (XsStringVal) null : xs.string(fieldName), (operator == null) ? (XsStringVal) null : xs.string(operator), (value == null) ? (XsAnyAtomicTypeVal) null : xs.string(value), (options == null) ? (XsStringVal) null : xs.stringSeq(options));
  }

  
  @Override
  public CtsQueryExpr fieldRangeQuery(XsStringSeqVal fieldName, XsStringVal operator, XsAnyAtomicTypeSeqVal value, XsStringSeqVal options) {
    if (operator == null) {
      throw new IllegalArgumentException("operator parameter for fieldRangeQuery() cannot be null");
    }
    return new QueryCallImpl("cts", "field-range-query", new Object[]{ fieldName, operator, value, options });
  }

  
  @Override
  public CtsQueryExpr fieldRangeQuery(String fieldName, String operator, String value, String options, double weight) {
    return fieldRangeQuery((fieldName == null) ? (XsStringVal) null : xs.string(fieldName), (operator == null) ? (XsStringVal) null : xs.string(operator), (value == null) ? (XsAnyAtomicTypeVal) null : xs.string(value), (options == null) ? (XsStringVal) null : xs.string(options), xs.doubleVal(weight));
  }

  
  @Override
  public CtsQueryExpr fieldRangeQuery(XsStringSeqVal fieldName, XsStringVal operator, XsAnyAtomicTypeSeqVal value, XsStringSeqVal options, XsDoubleVal weight) {
    if (operator == null) {
      throw new IllegalArgumentException("operator parameter for fieldRangeQuery() cannot be null");
    }
    return new QueryCallImpl("cts", "field-range-query", new Object[]{ fieldName, operator, value, options, weight });
  }

  
  @Override
  public CtsReferenceExpr fieldReference(String field) {
    return fieldReference((field == null) ? (XsStringVal) null : xs.string(field));
  }

  
  @Override
  public CtsReferenceExpr fieldReference(XsStringVal field) {
    if (field == null) {
      throw new IllegalArgumentException("field parameter for fieldReference() cannot be null");
    }
    return new ReferenceCallImpl("cts", "field-reference", new Object[]{ field });
  }

  
  @Override
  public CtsReferenceExpr fieldReference(String field, String options) {
    return fieldReference((field == null) ? (XsStringVal) null : xs.string(field), (options == null) ? (XsStringVal) null : xs.string(options));
  }

  
  @Override
  public CtsReferenceExpr fieldReference(XsStringVal field, XsStringSeqVal options) {
    if (field == null) {
      throw new IllegalArgumentException("field parameter for fieldReference() cannot be null");
    }
    return new ReferenceCallImpl("cts", "field-reference", new Object[]{ field, options });
  }

  
  @Override
  public CtsQueryExpr fieldValueQuery(String fieldName, String text) {
    return fieldValueQuery((fieldName == null) ? (XsStringVal) null : xs.string(fieldName), (text == null) ? (XsAnyAtomicTypeVal) null : xs.string(text));
  }

  
  @Override
  public CtsQueryExpr fieldValueQuery(XsStringSeqVal fieldName, XsAnyAtomicTypeSeqVal text) {
    return new QueryCallImpl("cts", "field-value-query", new Object[]{ fieldName, text });
  }

  
  @Override
  public CtsQueryExpr fieldValueQuery(String fieldName, String text, String... options) {
    return fieldValueQuery((fieldName == null) ? (XsStringVal) null : xs.string(fieldName), (text == null) ? (XsAnyAtomicTypeVal) null : xs.string(text), (options == null) ? (XsStringVal) null : xs.stringSeq(options));
  }

  
  @Override
  public CtsQueryExpr fieldValueQuery(XsStringSeqVal fieldName, XsAnyAtomicTypeSeqVal text, XsStringSeqVal options) {
    return new QueryCallImpl("cts", "field-value-query", new Object[]{ fieldName, text, options });
  }

  
  @Override
  public CtsQueryExpr fieldValueQuery(String fieldName, String text, String options, double weight) {
    return fieldValueQuery((fieldName == null) ? (XsStringVal) null : xs.string(fieldName), (text == null) ? (XsAnyAtomicTypeVal) null : xs.string(text), (options == null) ? (XsStringVal) null : xs.string(options), xs.doubleVal(weight));
  }

  
  @Override
  public CtsQueryExpr fieldValueQuery(XsStringSeqVal fieldName, XsAnyAtomicTypeSeqVal text, XsStringSeqVal options, XsDoubleVal weight) {
    return new QueryCallImpl("cts", "field-value-query", new Object[]{ fieldName, text, options, weight });
  }

  
  @Override
  public CtsQueryExpr fieldWordQuery(String fieldName, String text) {
    return fieldWordQuery((fieldName == null) ? (XsStringVal) null : xs.string(fieldName), (text == null) ? (XsStringVal) null : xs.string(text));
  }

  
  @Override
  public CtsQueryExpr fieldWordQuery(XsStringSeqVal fieldName, XsStringSeqVal text) {
    return new QueryCallImpl("cts", "field-word-query", new Object[]{ fieldName, text });
  }

  
  @Override
  public CtsQueryExpr fieldWordQuery(String fieldName, String text, String... options) {
    return fieldWordQuery((fieldName == null) ? (XsStringVal) null : xs.string(fieldName), (text == null) ? (XsStringVal) null : xs.string(text), (options == null) ? (XsStringVal) null : xs.stringSeq(options));
  }

  
  @Override
  public CtsQueryExpr fieldWordQuery(XsStringSeqVal fieldName, XsStringSeqVal text, XsStringSeqVal options) {
    return new QueryCallImpl("cts", "field-word-query", new Object[]{ fieldName, text, options });
  }

  
  @Override
  public CtsQueryExpr fieldWordQuery(String fieldName, String text, String options, double weight) {
    return fieldWordQuery((fieldName == null) ? (XsStringVal) null : xs.string(fieldName), (text == null) ? (XsStringVal) null : xs.string(text), (options == null) ? (XsStringVal) null : xs.string(options), xs.doubleVal(weight));
  }

  
  @Override
  public CtsQueryExpr fieldWordQuery(XsStringSeqVal fieldName, XsStringSeqVal text, XsStringSeqVal options, XsDoubleVal weight) {
    return new QueryCallImpl("cts", "field-word-query", new Object[]{ fieldName, text, options, weight });
  }

  
  @Override
  public CtsQueryExpr jsonPropertyChildGeospatialQuery(String parentPropertyName, String childPropertyNames, CtsRegionExpr... regions) {
    return jsonPropertyChildGeospatialQuery((parentPropertyName == null) ? (XsStringVal) null : xs.string(parentPropertyName), (childPropertyNames == null) ? (XsStringVal) null : xs.string(childPropertyNames), new RegionSeqListImpl(regions));
  }

  
  @Override
  public CtsQueryExpr jsonPropertyChildGeospatialQuery(XsStringSeqVal parentPropertyName, XsStringSeqVal childPropertyNames, CtsRegionSeqExpr regions) {
    return new QueryCallImpl("cts", "json-property-child-geospatial-query", new Object[]{ parentPropertyName, childPropertyNames, regions });
  }

  
  @Override
  public CtsQueryExpr jsonPropertyChildGeospatialQuery(String parentPropertyName, String childPropertyNames, CtsRegionSeqExpr regions, String... options) {
    return jsonPropertyChildGeospatialQuery((parentPropertyName == null) ? (XsStringVal) null : xs.string(parentPropertyName), (childPropertyNames == null) ? (XsStringVal) null : xs.string(childPropertyNames), regions, (options == null) ? (XsStringVal) null : xs.stringSeq(options));
  }

  
  @Override
  public CtsQueryExpr jsonPropertyChildGeospatialQuery(XsStringSeqVal parentPropertyName, XsStringSeqVal childPropertyNames, CtsRegionSeqExpr regions, XsStringSeqVal options) {
    return new QueryCallImpl("cts", "json-property-child-geospatial-query", new Object[]{ parentPropertyName, childPropertyNames, regions, options });
  }

  
  @Override
  public CtsQueryExpr jsonPropertyChildGeospatialQuery(String parentPropertyName, String childPropertyNames, CtsRegionSeqExpr regions, String options, double weight) {
    return jsonPropertyChildGeospatialQuery((parentPropertyName == null) ? (XsStringVal) null : xs.string(parentPropertyName), (childPropertyNames == null) ? (XsStringVal) null : xs.string(childPropertyNames), regions, (options == null) ? (XsStringVal) null : xs.string(options), xs.doubleVal(weight));
  }

  
  @Override
  public CtsQueryExpr jsonPropertyChildGeospatialQuery(XsStringSeqVal parentPropertyName, XsStringSeqVal childPropertyNames, CtsRegionSeqExpr regions, XsStringSeqVal options, XsDoubleVal weight) {
    return new QueryCallImpl("cts", "json-property-child-geospatial-query", new Object[]{ parentPropertyName, childPropertyNames, regions, options, weight });
  }

  
  @Override
  public CtsQueryExpr jsonPropertyGeospatialQuery(String propertyName, CtsRegionExpr... regions) {
    return jsonPropertyGeospatialQuery((propertyName == null) ? (XsStringVal) null : xs.string(propertyName), new RegionSeqListImpl(regions));
  }

  
  @Override
  public CtsQueryExpr jsonPropertyGeospatialQuery(XsStringSeqVal propertyName, CtsRegionSeqExpr regions) {
    return new QueryCallImpl("cts", "json-property-geospatial-query", new Object[]{ propertyName, regions });
  }

  
  @Override
  public CtsQueryExpr jsonPropertyGeospatialQuery(String propertyName, CtsRegionSeqExpr regions, String... options) {
    return jsonPropertyGeospatialQuery((propertyName == null) ? (XsStringVal) null : xs.string(propertyName), regions, (options == null) ? (XsStringVal) null : xs.stringSeq(options));
  }

  
  @Override
  public CtsQueryExpr jsonPropertyGeospatialQuery(XsStringSeqVal propertyName, CtsRegionSeqExpr regions, XsStringSeqVal options) {
    return new QueryCallImpl("cts", "json-property-geospatial-query", new Object[]{ propertyName, regions, options });
  }

  
  @Override
  public CtsQueryExpr jsonPropertyGeospatialQuery(String propertyName, CtsRegionSeqExpr regions, String options, double weight) {
    return jsonPropertyGeospatialQuery((propertyName == null) ? (XsStringVal) null : xs.string(propertyName), regions, (options == null) ? (XsStringVal) null : xs.string(options), xs.doubleVal(weight));
  }

  
  @Override
  public CtsQueryExpr jsonPropertyGeospatialQuery(XsStringSeqVal propertyName, CtsRegionSeqExpr regions, XsStringSeqVal options, XsDoubleVal weight) {
    return new QueryCallImpl("cts", "json-property-geospatial-query", new Object[]{ propertyName, regions, options, weight });
  }

  
  @Override
  public CtsQueryExpr jsonPropertyPairGeospatialQuery(String propertyName, String latitudePropertyNames, String longitudePropertyNames, CtsRegionExpr... regions) {
    return jsonPropertyPairGeospatialQuery((propertyName == null) ? (XsStringVal) null : xs.string(propertyName), (latitudePropertyNames == null) ? (XsStringVal) null : xs.string(latitudePropertyNames), (longitudePropertyNames == null) ? (XsStringVal) null : xs.string(longitudePropertyNames), new RegionSeqListImpl(regions));
  }

  
  @Override
  public CtsQueryExpr jsonPropertyPairGeospatialQuery(XsStringSeqVal propertyName, XsStringSeqVal latitudePropertyNames, XsStringSeqVal longitudePropertyNames, CtsRegionSeqExpr regions) {
    return new QueryCallImpl("cts", "json-property-pair-geospatial-query", new Object[]{ propertyName, latitudePropertyNames, longitudePropertyNames, regions });
  }

  
  @Override
  public CtsQueryExpr jsonPropertyPairGeospatialQuery(String propertyName, String latitudePropertyNames, String longitudePropertyNames, CtsRegionSeqExpr regions, String... options) {
    return jsonPropertyPairGeospatialQuery((propertyName == null) ? (XsStringVal) null : xs.string(propertyName), (latitudePropertyNames == null) ? (XsStringVal) null : xs.string(latitudePropertyNames), (longitudePropertyNames == null) ? (XsStringVal) null : xs.string(longitudePropertyNames), regions, (options == null) ? (XsStringVal) null : xs.stringSeq(options));
  }

  
  @Override
  public CtsQueryExpr jsonPropertyPairGeospatialQuery(XsStringSeqVal propertyName, XsStringSeqVal latitudePropertyNames, XsStringSeqVal longitudePropertyNames, CtsRegionSeqExpr regions, XsStringSeqVal options) {
    return new QueryCallImpl("cts", "json-property-pair-geospatial-query", new Object[]{ propertyName, latitudePropertyNames, longitudePropertyNames, regions, options });
  }

  
  @Override
  public CtsQueryExpr jsonPropertyPairGeospatialQuery(String propertyName, String latitudePropertyNames, String longitudePropertyNames, CtsRegionSeqExpr regions, String options, double weight) {
    return jsonPropertyPairGeospatialQuery((propertyName == null) ? (XsStringVal) null : xs.string(propertyName), (latitudePropertyNames == null) ? (XsStringVal) null : xs.string(latitudePropertyNames), (longitudePropertyNames == null) ? (XsStringVal) null : xs.string(longitudePropertyNames), regions, (options == null) ? (XsStringVal) null : xs.string(options), xs.doubleVal(weight));
  }

  
  @Override
  public CtsQueryExpr jsonPropertyPairGeospatialQuery(XsStringSeqVal propertyName, XsStringSeqVal latitudePropertyNames, XsStringSeqVal longitudePropertyNames, CtsRegionSeqExpr regions, XsStringSeqVal options, XsDoubleVal weight) {
    return new QueryCallImpl("cts", "json-property-pair-geospatial-query", new Object[]{ propertyName, latitudePropertyNames, longitudePropertyNames, regions, options, weight });
  }

  
  @Override
  public CtsQueryExpr jsonPropertyRangeQuery(String propertyName, String operator, String value) {
    return jsonPropertyRangeQuery((propertyName == null) ? (XsStringVal) null : xs.string(propertyName), (operator == null) ? (XsStringVal) null : xs.string(operator), (value == null) ? (XsAnyAtomicTypeVal) null : xs.string(value));
  }

  
  @Override
  public CtsQueryExpr jsonPropertyRangeQuery(XsStringSeqVal propertyName, XsStringVal operator, XsAnyAtomicTypeSeqVal value) {
    if (operator == null) {
      throw new IllegalArgumentException("operator parameter for jsonPropertyRangeQuery() cannot be null");
    }
    return new QueryCallImpl("cts", "json-property-range-query", new Object[]{ propertyName, operator, value });
  }

  
  @Override
  public CtsQueryExpr jsonPropertyRangeQuery(String propertyName, String operator, String value, String... options) {
    return jsonPropertyRangeQuery((propertyName == null) ? (XsStringVal) null : xs.string(propertyName), (operator == null) ? (XsStringVal) null : xs.string(operator), (value == null) ? (XsAnyAtomicTypeVal) null : xs.string(value), (options == null) ? (XsStringVal) null : xs.stringSeq(options));
  }

  
  @Override
  public CtsQueryExpr jsonPropertyRangeQuery(XsStringSeqVal propertyName, XsStringVal operator, XsAnyAtomicTypeSeqVal value, XsStringSeqVal options) {
    if (operator == null) {
      throw new IllegalArgumentException("operator parameter for jsonPropertyRangeQuery() cannot be null");
    }
    return new QueryCallImpl("cts", "json-property-range-query", new Object[]{ propertyName, operator, value, options });
  }

  
  @Override
  public CtsQueryExpr jsonPropertyRangeQuery(String propertyName, String operator, String value, String options, double weight) {
    return jsonPropertyRangeQuery((propertyName == null) ? (XsStringVal) null : xs.string(propertyName), (operator == null) ? (XsStringVal) null : xs.string(operator), (value == null) ? (XsAnyAtomicTypeVal) null : xs.string(value), (options == null) ? (XsStringVal) null : xs.string(options), xs.doubleVal(weight));
  }

  
  @Override
  public CtsQueryExpr jsonPropertyRangeQuery(XsStringSeqVal propertyName, XsStringVal operator, XsAnyAtomicTypeSeqVal value, XsStringSeqVal options, XsDoubleVal weight) {
    if (operator == null) {
      throw new IllegalArgumentException("operator parameter for jsonPropertyRangeQuery() cannot be null");
    }
    return new QueryCallImpl("cts", "json-property-range-query", new Object[]{ propertyName, operator, value, options, weight });
  }

  
  @Override
  public CtsReferenceExpr jsonPropertyReference(String property) {
    return jsonPropertyReference((property == null) ? (XsStringVal) null : xs.string(property));
  }

  
  @Override
  public CtsReferenceExpr jsonPropertyReference(XsStringVal property) {
    if (property == null) {
      throw new IllegalArgumentException("property parameter for jsonPropertyReference() cannot be null");
    }
    return new ReferenceCallImpl("cts", "json-property-reference", new Object[]{ property });
  }

  
  @Override
  public CtsReferenceExpr jsonPropertyReference(String property, String options) {
    return jsonPropertyReference((property == null) ? (XsStringVal) null : xs.string(property), (options == null) ? (XsStringVal) null : xs.string(options));
  }

  
  @Override
  public CtsReferenceExpr jsonPropertyReference(XsStringVal property, XsStringSeqVal options) {
    if (property == null) {
      throw new IllegalArgumentException("property parameter for jsonPropertyReference() cannot be null");
    }
    return new ReferenceCallImpl("cts", "json-property-reference", new Object[]{ property, options });
  }

  
  @Override
  public CtsQueryExpr jsonPropertyScopeQuery(String propertyName, CtsQueryExpr query) {
    return jsonPropertyScopeQuery((propertyName == null) ? (XsStringVal) null : xs.string(propertyName), query);
  }

  
  @Override
  public CtsQueryExpr jsonPropertyScopeQuery(XsStringSeqVal propertyName, CtsQueryExpr query) {
    if (query == null) {
      throw new IllegalArgumentException("query parameter for jsonPropertyScopeQuery() cannot be null");
    }
    return new QueryCallImpl("cts", "json-property-scope-query", new Object[]{ propertyName, query });
  }

  
  @Override
  public CtsQueryExpr jsonPropertyValueQuery(String propertyName, String value) {
    return jsonPropertyValueQuery((propertyName == null) ? (XsStringVal) null : xs.string(propertyName), (value == null) ? (XsAnyAtomicTypeVal) null : xs.string(value));
  }

  
  @Override
  public CtsQueryExpr jsonPropertyValueQuery(XsStringSeqVal propertyName, XsAnyAtomicTypeSeqVal value) {
    return new QueryCallImpl("cts", "json-property-value-query", new Object[]{ propertyName, value });
  }

  
  @Override
  public CtsQueryExpr jsonPropertyValueQuery(String propertyName, String value, String... options) {
    return jsonPropertyValueQuery((propertyName == null) ? (XsStringVal) null : xs.string(propertyName), (value == null) ? (XsAnyAtomicTypeVal) null : xs.string(value), (options == null) ? (XsStringVal) null : xs.stringSeq(options));
  }

  
  @Override
  public CtsQueryExpr jsonPropertyValueQuery(XsStringSeqVal propertyName, XsAnyAtomicTypeSeqVal value, XsStringSeqVal options) {
    return new QueryCallImpl("cts", "json-property-value-query", new Object[]{ propertyName, value, options });
  }

  
  @Override
  public CtsQueryExpr jsonPropertyValueQuery(String propertyName, String value, String options, double weight) {
    return jsonPropertyValueQuery((propertyName == null) ? (XsStringVal) null : xs.string(propertyName), (value == null) ? (XsAnyAtomicTypeVal) null : xs.string(value), (options == null) ? (XsStringVal) null : xs.string(options), xs.doubleVal(weight));
  }

  
  @Override
  public CtsQueryExpr jsonPropertyValueQuery(XsStringSeqVal propertyName, XsAnyAtomicTypeSeqVal value, XsStringSeqVal options, XsDoubleVal weight) {
    return new QueryCallImpl("cts", "json-property-value-query", new Object[]{ propertyName, value, options, weight });
  }

  
  @Override
  public CtsQueryExpr jsonPropertyWordQuery(String propertyName, String text) {
    return jsonPropertyWordQuery((propertyName == null) ? (XsStringVal) null : xs.string(propertyName), (text == null) ? (XsStringVal) null : xs.string(text));
  }

  
  @Override
  public CtsQueryExpr jsonPropertyWordQuery(XsStringSeqVal propertyName, XsStringSeqVal text) {
    return new QueryCallImpl("cts", "json-property-word-query", new Object[]{ propertyName, text });
  }

  
  @Override
  public CtsQueryExpr jsonPropertyWordQuery(String propertyName, String text, String... options) {
    return jsonPropertyWordQuery((propertyName == null) ? (XsStringVal) null : xs.string(propertyName), (text == null) ? (XsStringVal) null : xs.string(text), (options == null) ? (XsStringVal) null : xs.stringSeq(options));
  }

  
  @Override
  public CtsQueryExpr jsonPropertyWordQuery(XsStringSeqVal propertyName, XsStringSeqVal text, XsStringSeqVal options) {
    return new QueryCallImpl("cts", "json-property-word-query", new Object[]{ propertyName, text, options });
  }

  
  @Override
  public CtsQueryExpr jsonPropertyWordQuery(String propertyName, String text, String options, double weight) {
    return jsonPropertyWordQuery((propertyName == null) ? (XsStringVal) null : xs.string(propertyName), (text == null) ? (XsStringVal) null : xs.string(text), (options == null) ? (XsStringVal) null : xs.string(options), xs.doubleVal(weight));
  }

  
  @Override
  public CtsQueryExpr jsonPropertyWordQuery(XsStringSeqVal propertyName, XsStringSeqVal text, XsStringSeqVal options, XsDoubleVal weight) {
    return new QueryCallImpl("cts", "json-property-word-query", new Object[]{ propertyName, text, options, weight });
  }

  
  @Override
  public CtsQueryExpr locksFragmentQuery(CtsQueryExpr query) {
    if (query == null) {
      throw new IllegalArgumentException("query parameter for locksFragmentQuery() cannot be null");
    }
    return new QueryCallImpl("cts", "locks-fragment-query", new Object[]{ query });
  }

  
  @Override
  public CtsQueryExpr lsqtQuery(String temporalCollection) {
    return lsqtQuery((temporalCollection == null) ? (XsStringVal) null : xs.string(temporalCollection));
  }

  
  @Override
  public CtsQueryExpr lsqtQuery(XsStringVal temporalCollection) {
    if (temporalCollection == null) {
      throw new IllegalArgumentException("temporalCollection parameter for lsqtQuery() cannot be null");
    }
    return new QueryCallImpl("cts", "lsqt-query", new Object[]{ temporalCollection });
  }

  
  @Override
  public CtsQueryExpr lsqtQuery(String temporalCollection, String timestamp) {
    return lsqtQuery((temporalCollection == null) ? (XsStringVal) null : xs.string(temporalCollection), (timestamp == null) ? (XsDateTimeVal) null : xs.dateTime(timestamp));
  }

  
  @Override
  public CtsQueryExpr lsqtQuery(XsStringVal temporalCollection, XsDateTimeVal timestamp) {
    if (temporalCollection == null) {
      throw new IllegalArgumentException("temporalCollection parameter for lsqtQuery() cannot be null");
    }
    return new QueryCallImpl("cts", "lsqt-query", new Object[]{ temporalCollection, timestamp });
  }

  
  @Override
  public CtsQueryExpr lsqtQuery(String temporalCollection, String timestamp, String... options) {
    return lsqtQuery((temporalCollection == null) ? (XsStringVal) null : xs.string(temporalCollection), (timestamp == null) ? (XsDateTimeVal) null : xs.dateTime(timestamp), (options == null) ? (XsStringVal) null : xs.stringSeq(options));
  }

  
  @Override
  public CtsQueryExpr lsqtQuery(XsStringVal temporalCollection, XsDateTimeVal timestamp, XsStringSeqVal options) {
    if (temporalCollection == null) {
      throw new IllegalArgumentException("temporalCollection parameter for lsqtQuery() cannot be null");
    }
    return new QueryCallImpl("cts", "lsqt-query", new Object[]{ temporalCollection, timestamp, options });
  }

  
  @Override
  public CtsQueryExpr lsqtQuery(String temporalCollection, String timestamp, String options, double weight) {
    return lsqtQuery((temporalCollection == null) ? (XsStringVal) null : xs.string(temporalCollection), (timestamp == null) ? (XsDateTimeVal) null : xs.dateTime(timestamp), (options == null) ? (XsStringVal) null : xs.string(options), xs.doubleVal(weight));
  }

  
  @Override
  public CtsQueryExpr lsqtQuery(XsStringVal temporalCollection, XsDateTimeVal timestamp, XsStringSeqVal options, XsDoubleVal weight) {
    if (temporalCollection == null) {
      throw new IllegalArgumentException("temporalCollection parameter for lsqtQuery() cannot be null");
    }
    return new QueryCallImpl("cts", "lsqt-query", new Object[]{ temporalCollection, timestamp, options, weight });
  }

  
  @Override
  public CtsQueryExpr nearQuery(CtsQueryExpr... queries) {
    return nearQuery(new QuerySeqListImpl(queries));
  }

  
  @Override
  public CtsQueryExpr nearQuery(CtsQuerySeqExpr queries) {
    return new QueryCallImpl("cts", "near-query", new Object[]{ queries });
  }

  
  @Override
  public CtsQueryExpr nearQuery(CtsQuerySeqExpr queries, double distance) {
    return nearQuery(queries, xs.doubleVal(distance));
  }

  
  @Override
  public CtsQueryExpr nearQuery(CtsQuerySeqExpr queries, XsDoubleVal distance) {
    return new QueryCallImpl("cts", "near-query", new Object[]{ queries, distance });
  }

  
  @Override
  public CtsQueryExpr nearQuery(CtsQuerySeqExpr queries, double distance, String... options) {
    return nearQuery(queries, xs.doubleVal(distance), (options == null) ? (XsStringVal) null : xs.stringSeq(options));
  }

  
  @Override
  public CtsQueryExpr nearQuery(CtsQuerySeqExpr queries, XsDoubleVal distance, XsStringSeqVal options) {
    return new QueryCallImpl("cts", "near-query", new Object[]{ queries, distance, options });
  }

  
  @Override
  public CtsQueryExpr nearQuery(CtsQuerySeqExpr queries, double distance, String options, double distanceWeight) {
    return nearQuery(queries, xs.doubleVal(distance), (options == null) ? (XsStringVal) null : xs.string(options), xs.doubleVal(distanceWeight));
  }

  
  @Override
  public CtsQueryExpr nearQuery(CtsQuerySeqExpr queries, XsDoubleVal distance, XsStringSeqVal options, XsDoubleVal distanceWeight) {
    return new QueryCallImpl("cts", "near-query", new Object[]{ queries, distance, options, distanceWeight });
  }

  
  @Override
  public CtsQueryExpr notInQuery(CtsQueryExpr positiveQuery, CtsQueryExpr negativeQuery) {
    if (positiveQuery == null) {
      throw new IllegalArgumentException("positiveQuery parameter for notInQuery() cannot be null");
    }
    if (negativeQuery == null) {
      throw new IllegalArgumentException("negativeQuery parameter for notInQuery() cannot be null");
    }
    return new QueryCallImpl("cts", "not-in-query", new Object[]{ positiveQuery, negativeQuery });
  }

  
  @Override
  public CtsQueryExpr notQuery(CtsQueryExpr query) {
    if (query == null) {
      throw new IllegalArgumentException("query parameter for notQuery() cannot be null");
    }
    return new QueryCallImpl("cts", "not-query", new Object[]{ query });
  }

  
  @Override
  public CtsQueryExpr orQuery(CtsQueryExpr... queries) {
    return orQuery(new QuerySeqListImpl(queries));
  }

  
  @Override
  public CtsQueryExpr orQuery(CtsQuerySeqExpr queries) {
    return new QueryCallImpl("cts", "or-query", new Object[]{ queries });
  }

  
  @Override
  public CtsQueryExpr orQuery(CtsQuerySeqExpr queries, String options) {
    return orQuery(queries, (options == null) ? (XsStringVal) null : xs.string(options));
  }

  
  @Override
  public CtsQueryExpr orQuery(CtsQuerySeqExpr queries, XsStringSeqVal options) {
    return new QueryCallImpl("cts", "or-query", new Object[]{ queries, options });
  }

  
  @Override
  public CtsQueryExpr pathGeospatialQuery(String pathExpression, CtsRegionExpr... regions) {
    return pathGeospatialQuery((pathExpression == null) ? (XsStringVal) null : xs.string(pathExpression), new RegionSeqListImpl(regions));
  }

  
  @Override
  public CtsQueryExpr pathGeospatialQuery(XsStringSeqVal pathExpression, CtsRegionSeqExpr regions) {
    return new QueryCallImpl("cts", "path-geospatial-query", new Object[]{ pathExpression, regions });
  }

  
  @Override
  public CtsQueryExpr pathGeospatialQuery(String pathExpression, CtsRegionSeqExpr regions, String... options) {
    return pathGeospatialQuery((pathExpression == null) ? (XsStringVal) null : xs.string(pathExpression), regions, (options == null) ? (XsStringVal) null : xs.stringSeq(options));
  }

  
  @Override
  public CtsQueryExpr pathGeospatialQuery(XsStringSeqVal pathExpression, CtsRegionSeqExpr regions, XsStringSeqVal options) {
    return new QueryCallImpl("cts", "path-geospatial-query", new Object[]{ pathExpression, regions, options });
  }

  
  @Override
  public CtsQueryExpr pathGeospatialQuery(String pathExpression, CtsRegionSeqExpr regions, String options, double weight) {
    return pathGeospatialQuery((pathExpression == null) ? (XsStringVal) null : xs.string(pathExpression), regions, (options == null) ? (XsStringVal) null : xs.string(options), xs.doubleVal(weight));
  }

  
  @Override
  public CtsQueryExpr pathGeospatialQuery(XsStringSeqVal pathExpression, CtsRegionSeqExpr regions, XsStringSeqVal options, XsDoubleVal weight) {
    return new QueryCallImpl("cts", "path-geospatial-query", new Object[]{ pathExpression, regions, options, weight });
  }

  
  @Override
  public CtsQueryExpr pathRangeQuery(String pathExpression, String operator, String value) {
    return pathRangeQuery((pathExpression == null) ? (XsStringVal) null : xs.string(pathExpression), (operator == null) ? (XsStringVal) null : xs.string(operator), (value == null) ? (XsAnyAtomicTypeVal) null : xs.string(value));
  }

  
  @Override
  public CtsQueryExpr pathRangeQuery(XsStringSeqVal pathExpression, XsStringVal operator, XsAnyAtomicTypeSeqVal value) {
    if (operator == null) {
      throw new IllegalArgumentException("operator parameter for pathRangeQuery() cannot be null");
    }
    return new QueryCallImpl("cts", "path-range-query", new Object[]{ pathExpression, operator, value });
  }

  
  @Override
  public CtsQueryExpr pathRangeQuery(String pathExpression, String operator, String value, String... options) {
    return pathRangeQuery((pathExpression == null) ? (XsStringVal) null : xs.string(pathExpression), (operator == null) ? (XsStringVal) null : xs.string(operator), (value == null) ? (XsAnyAtomicTypeVal) null : xs.string(value), (options == null) ? (XsStringVal) null : xs.stringSeq(options));
  }

  
  @Override
  public CtsQueryExpr pathRangeQuery(XsStringSeqVal pathExpression, XsStringVal operator, XsAnyAtomicTypeSeqVal value, XsStringSeqVal options) {
    if (operator == null) {
      throw new IllegalArgumentException("operator parameter for pathRangeQuery() cannot be null");
    }
    return new QueryCallImpl("cts", "path-range-query", new Object[]{ pathExpression, operator, value, options });
  }

  
  @Override
  public CtsQueryExpr pathRangeQuery(String pathExpression, String operator, String value, String options, double weight) {
    return pathRangeQuery((pathExpression == null) ? (XsStringVal) null : xs.string(pathExpression), (operator == null) ? (XsStringVal) null : xs.string(operator), (value == null) ? (XsAnyAtomicTypeVal) null : xs.string(value), (options == null) ? (XsStringVal) null : xs.string(options), xs.doubleVal(weight));
  }

  
  @Override
  public CtsQueryExpr pathRangeQuery(XsStringSeqVal pathExpression, XsStringVal operator, XsAnyAtomicTypeSeqVal value, XsStringSeqVal options, XsDoubleVal weight) {
    if (operator == null) {
      throw new IllegalArgumentException("operator parameter for pathRangeQuery() cannot be null");
    }
    return new QueryCallImpl("cts", "path-range-query", new Object[]{ pathExpression, operator, value, options, weight });
  }

  
  @Override
  public CtsReferenceExpr pathReference(String pathExpression) {
    return pathReference((pathExpression == null) ? (XsStringVal) null : xs.string(pathExpression));
  }

  
  @Override
  public CtsReferenceExpr pathReference(XsStringVal pathExpression) {
    if (pathExpression == null) {
      throw new IllegalArgumentException("pathExpression parameter for pathReference() cannot be null");
    }
    return new ReferenceCallImpl("cts", "path-reference", new Object[]{ pathExpression });
  }

  
  @Override
  public CtsReferenceExpr pathReference(String pathExpression, String options) {
    return pathReference((pathExpression == null) ? (XsStringVal) null : xs.string(pathExpression), (options == null) ? (XsStringVal) null : xs.string(options));
  }

  
  @Override
  public CtsReferenceExpr pathReference(XsStringVal pathExpression, XsStringSeqVal options) {
    if (pathExpression == null) {
      throw new IllegalArgumentException("pathExpression parameter for pathReference() cannot be null");
    }
    return new ReferenceCallImpl("cts", "path-reference", new Object[]{ pathExpression, options });
  }

  
  @Override
  public CtsReferenceExpr pathReference(String pathExpression, String options, ServerExpression map) {
    return pathReference((pathExpression == null) ? (XsStringVal) null : xs.string(pathExpression), (options == null) ? (XsStringVal) null : xs.string(options), map);
  }

  
  @Override
  public CtsReferenceExpr pathReference(XsStringVal pathExpression, XsStringSeqVal options, ServerExpression map) {
    if (pathExpression == null) {
      throw new IllegalArgumentException("pathExpression parameter for pathReference() cannot be null");
    }
    return new ReferenceCallImpl("cts", "path-reference", new Object[]{ pathExpression, options, map });
  }

  
  @Override
  public CtsPeriodExpr period(String start, String end) {
    return period((start == null) ? (XsDateTimeVal) null : xs.dateTime(start), (end == null) ? (XsDateTimeVal) null : xs.dateTime(end));
  }

  
  @Override
  public CtsPeriodExpr period(XsDateTimeVal start, XsDateTimeVal end) {
    if (start == null) {
      throw new IllegalArgumentException("start parameter for period() cannot be null");
    }
    if (end == null) {
      throw new IllegalArgumentException("end parameter for period() cannot be null");
    }
    return new PeriodCallImpl("cts", "period", new Object[]{ start, end });
  }

  
  @Override
  public CtsQueryExpr periodCompareQuery(String axis1, String operator, String axis2) {
    return periodCompareQuery((axis1 == null) ? (XsStringVal) null : xs.string(axis1), (operator == null) ? (XsStringVal) null : xs.string(operator), (axis2 == null) ? (XsStringVal) null : xs.string(axis2));
  }

  
  @Override
  public CtsQueryExpr periodCompareQuery(XsStringVal axis1, XsStringVal operator, XsStringVal axis2) {
    if (axis1 == null) {
      throw new IllegalArgumentException("axis1 parameter for periodCompareQuery() cannot be null");
    }
    if (operator == null) {
      throw new IllegalArgumentException("operator parameter for periodCompareQuery() cannot be null");
    }
    if (axis2 == null) {
      throw new IllegalArgumentException("axis2 parameter for periodCompareQuery() cannot be null");
    }
    return new QueryCallImpl("cts", "period-compare-query", new Object[]{ axis1, operator, axis2 });
  }

  
  @Override
  public CtsQueryExpr periodCompareQuery(String axis1, String operator, String axis2, String options) {
    return periodCompareQuery((axis1 == null) ? (XsStringVal) null : xs.string(axis1), (operator == null) ? (XsStringVal) null : xs.string(operator), (axis2 == null) ? (XsStringVal) null : xs.string(axis2), (options == null) ? (XsStringVal) null : xs.string(options));
  }

  
  @Override
  public CtsQueryExpr periodCompareQuery(XsStringVal axis1, XsStringVal operator, XsStringVal axis2, XsStringSeqVal options) {
    if (axis1 == null) {
      throw new IllegalArgumentException("axis1 parameter for periodCompareQuery() cannot be null");
    }
    if (operator == null) {
      throw new IllegalArgumentException("operator parameter for periodCompareQuery() cannot be null");
    }
    if (axis2 == null) {
      throw new IllegalArgumentException("axis2 parameter for periodCompareQuery() cannot be null");
    }
    return new QueryCallImpl("cts", "period-compare-query", new Object[]{ axis1, operator, axis2, options });
  }

  
  @Override
  public CtsQueryExpr periodRangeQuery(String axisName, String operator) {
    return periodRangeQuery((axisName == null) ? (XsStringVal) null : xs.string(axisName), (operator == null) ? (XsStringVal) null : xs.string(operator));
  }

  
  @Override
  public CtsQueryExpr periodRangeQuery(XsStringSeqVal axisName, XsStringVal operator) {
    if (operator == null) {
      throw new IllegalArgumentException("operator parameter for periodRangeQuery() cannot be null");
    }
    return new QueryCallImpl("cts", "period-range-query", new Object[]{ axisName, operator });
  }

  
  @Override
  public CtsQueryExpr periodRangeQuery(String axisName, String operator, CtsPeriodExpr... period) {
    return periodRangeQuery((axisName == null) ? (XsStringVal) null : xs.string(axisName), (operator == null) ? (XsStringVal) null : xs.string(operator), new PeriodSeqListImpl(period));
  }

  
  @Override
  public CtsQueryExpr periodRangeQuery(XsStringSeqVal axisName, XsStringVal operator, CtsPeriodSeqExpr period) {
    if (operator == null) {
      throw new IllegalArgumentException("operator parameter for periodRangeQuery() cannot be null");
    }
    return new QueryCallImpl("cts", "period-range-query", new Object[]{ axisName, operator, period });
  }

  
  @Override
  public CtsQueryExpr periodRangeQuery(String axisName, String operator, CtsPeriodSeqExpr period, String options) {
    return periodRangeQuery((axisName == null) ? (XsStringVal) null : xs.string(axisName), (operator == null) ? (XsStringVal) null : xs.string(operator), period, (options == null) ? (XsStringVal) null : xs.string(options));
  }

  
  @Override
  public CtsQueryExpr periodRangeQuery(XsStringSeqVal axisName, XsStringVal operator, CtsPeriodSeqExpr period, XsStringSeqVal options) {
    if (operator == null) {
      throw new IllegalArgumentException("operator parameter for periodRangeQuery() cannot be null");
    }
    return new QueryCallImpl("cts", "period-range-query", new Object[]{ axisName, operator, period, options });
  }

  
  @Override
  public CtsPointExpr point(double latitude, double longitude) {
    return point(xs.doubleVal(latitude), xs.doubleVal(longitude));
  }

  
  @Override
  public CtsPointExpr point(XsDoubleVal latitude, XsDoubleVal longitude) {
    if (latitude == null) {
      throw new IllegalArgumentException("latitude parameter for point() cannot be null");
    }
    if (longitude == null) {
      throw new IllegalArgumentException("longitude parameter for point() cannot be null");
    }
    return new PointCallImpl("cts", "point", new Object[]{ latitude, longitude });
  }

  
  @Override
  public CtsPolygonExpr polygon(XsAnyAtomicTypeSeqVal vertices) {
    return new PolygonCallImpl("cts", "polygon", new Object[]{ vertices });
  }

  
  @Override
  public CtsQueryExpr propertiesFragmentQuery(CtsQueryExpr query) {
    if (query == null) {
      throw new IllegalArgumentException("query parameter for propertiesFragmentQuery() cannot be null");
    }
    return new QueryCallImpl("cts", "properties-fragment-query", new Object[]{ query });
  }

  
  @Override
  public ServerExpression stem(ServerExpression text) {
    if (text == null) {
      throw new IllegalArgumentException("text parameter for stem() cannot be null");
    }
    return new XsExprImpl.StringSeqCallImpl("cts", "stem", new Object[]{ text });
  }

  
  @Override
  public ServerExpression stem(ServerExpression text, String language) {
    return stem(text, (language == null) ? (ServerExpression) null : xs.string(language));
  }

  
  @Override
  public ServerExpression stem(ServerExpression text, ServerExpression language) {
    if (text == null) {
      throw new IllegalArgumentException("text parameter for stem() cannot be null");
    }
    return new XsExprImpl.StringSeqCallImpl("cts", "stem", new Object[]{ text, language });
  }

  
  @Override
  public ServerExpression stem(ServerExpression text, String language, String partOfSpeech) {
    return stem(text, (language == null) ? (ServerExpression) null : xs.string(language), (partOfSpeech == null) ? (ServerExpression) null : xs.string(partOfSpeech));
  }

  
  @Override
  public ServerExpression stem(ServerExpression text, ServerExpression language, ServerExpression partOfSpeech) {
    if (text == null) {
      throw new IllegalArgumentException("text parameter for stem() cannot be null");
    }
    return new XsExprImpl.StringSeqCallImpl("cts", "stem", new Object[]{ text, language, partOfSpeech });
  }

  
  @Override
  public ServerExpression tokenize(ServerExpression text) {
    if (text == null) {
      throw new IllegalArgumentException("text parameter for tokenize() cannot be null");
    }
    return new XsExprImpl.StringSeqCallImpl("cts", "tokenize", new Object[]{ text });
  }

  
  @Override
  public ServerExpression tokenize(ServerExpression text, String language) {
    return tokenize(text, (language == null) ? (ServerExpression) null : xs.string(language));
  }

  
  @Override
  public ServerExpression tokenize(ServerExpression text, ServerExpression language) {
    if (text == null) {
      throw new IllegalArgumentException("text parameter for tokenize() cannot be null");
    }
    return new XsExprImpl.StringSeqCallImpl("cts", "tokenize", new Object[]{ text, language });
  }

  
  @Override
  public ServerExpression tokenize(ServerExpression text, String language, String field) {
    return tokenize(text, (language == null) ? (ServerExpression) null : xs.string(language), (field == null) ? (ServerExpression) null : xs.string(field));
  }

  
  @Override
  public ServerExpression tokenize(ServerExpression text, ServerExpression language, ServerExpression field) {
    if (text == null) {
      throw new IllegalArgumentException("text parameter for tokenize() cannot be null");
    }
    return new XsExprImpl.StringSeqCallImpl("cts", "tokenize", new Object[]{ text, language, field });
  }

  
  @Override
  public CtsQueryExpr tripleRangeQuery(String subject, String predicate, String object) {
    return tripleRangeQuery((subject == null) ? (XsAnyAtomicTypeVal) null : xs.string(subject), (predicate == null) ? (XsAnyAtomicTypeVal) null : xs.string(predicate), (object == null) ? (XsAnyAtomicTypeVal) null : xs.string(object));
  }

  
  @Override
  public CtsQueryExpr tripleRangeQuery(XsAnyAtomicTypeSeqVal subject, XsAnyAtomicTypeSeqVal predicate, XsAnyAtomicTypeSeqVal object) {
    return new QueryCallImpl("cts", "triple-range-query", new Object[]{ subject, predicate, object });
  }

  
  @Override
  public CtsQueryExpr tripleRangeQuery(String subject, String predicate, String object, String operator) {
    return tripleRangeQuery((subject == null) ? (XsAnyAtomicTypeVal) null : xs.string(subject), (predicate == null) ? (XsAnyAtomicTypeVal) null : xs.string(predicate), (object == null) ? (XsAnyAtomicTypeVal) null : xs.string(object), (operator == null) ? (XsStringVal) null : xs.string(operator));
  }

  
  @Override
  public CtsQueryExpr tripleRangeQuery(XsAnyAtomicTypeSeqVal subject, XsAnyAtomicTypeSeqVal predicate, XsAnyAtomicTypeSeqVal object, XsStringSeqVal operator) {
    return new QueryCallImpl("cts", "triple-range-query", new Object[]{ subject, predicate, object, operator });
  }

  
  @Override
  public CtsQueryExpr tripleRangeQuery(String subject, String predicate, String object, String operator, String... options) {
    return tripleRangeQuery((subject == null) ? (XsAnyAtomicTypeVal) null : xs.string(subject), (predicate == null) ? (XsAnyAtomicTypeVal) null : xs.string(predicate), (object == null) ? (XsAnyAtomicTypeVal) null : xs.string(object), (operator == null) ? (XsStringVal) null : xs.string(operator), (options == null) ? (XsStringVal) null : xs.stringSeq(options));
  }

  
  @Override
  public CtsQueryExpr tripleRangeQuery(XsAnyAtomicTypeSeqVal subject, XsAnyAtomicTypeSeqVal predicate, XsAnyAtomicTypeSeqVal object, XsStringSeqVal operator, XsStringSeqVal options) {
    return new QueryCallImpl("cts", "triple-range-query", new Object[]{ subject, predicate, object, operator, options });
  }

  
  @Override
  public CtsQueryExpr tripleRangeQuery(String subject, String predicate, String object, String operator, String options, double weight) {
    return tripleRangeQuery((subject == null) ? (XsAnyAtomicTypeVal) null : xs.string(subject), (predicate == null) ? (XsAnyAtomicTypeVal) null : xs.string(predicate), (object == null) ? (XsAnyAtomicTypeVal) null : xs.string(object), (operator == null) ? (XsStringVal) null : xs.string(operator), (options == null) ? (XsStringVal) null : xs.string(options), xs.doubleVal(weight));
  }

  
  @Override
  public CtsQueryExpr tripleRangeQuery(XsAnyAtomicTypeSeqVal subject, XsAnyAtomicTypeSeqVal predicate, XsAnyAtomicTypeSeqVal object, XsStringSeqVal operator, XsStringSeqVal options, XsDoubleVal weight) {
    return new QueryCallImpl("cts", "triple-range-query", new Object[]{ subject, predicate, object, operator, options, weight });
  }

  
  @Override
  public CtsQueryExpr trueQuery() {
    return new QueryCallImpl("cts", "true-query", new Object[]{  });
  }

  
  @Override
  public CtsReferenceExpr uriReference() {
    return new ReferenceCallImpl("cts", "uri-reference", new Object[]{  });
  }

  
  @Override
  public CtsQueryExpr wordQuery(String text) {
    return wordQuery((text == null) ? (XsStringVal) null : xs.string(text));
  }

  
  @Override
  public CtsQueryExpr wordQuery(XsStringSeqVal text) {
    return new QueryCallImpl("cts", "word-query", new Object[]{ text });
  }

  
  @Override
  public CtsQueryExpr wordQuery(String text, String... options) {
    return wordQuery((text == null) ? (XsStringVal) null : xs.string(text), (options == null) ? (XsStringVal) null : xs.stringSeq(options));
  }

  
  @Override
  public CtsQueryExpr wordQuery(XsStringSeqVal text, XsStringSeqVal options) {
    return new QueryCallImpl("cts", "word-query", new Object[]{ text, options });
  }

  
  @Override
  public CtsQueryExpr wordQuery(String text, String options, double weight) {
    return wordQuery((text == null) ? (XsStringVal) null : xs.string(text), (options == null) ? (XsStringVal) null : xs.string(options), xs.doubleVal(weight));
  }

  
  @Override
  public CtsQueryExpr wordQuery(XsStringSeqVal text, XsStringSeqVal options, XsDoubleVal weight) {
    return new QueryCallImpl("cts", "word-query", new Object[]{ text, options, weight });
  }

  @Override
  public CtsBoxSeqExpr boxSeq(CtsBoxExpr... items) {
    return new BoxSeqListImpl(items);
  }
  static class BoxSeqListImpl extends BaseTypeImpl.ServerExpressionListImpl implements CtsBoxSeqExpr {
    BoxSeqListImpl(Object[] items) {
      super(items);
    }
  }
  static class BoxSeqCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements CtsBoxSeqExpr {
    BoxSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class BoxCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements CtsBoxExpr {
    BoxCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
 
  @Override
  public CtsCircleSeqExpr circleSeq(CtsCircleExpr... items) {
    return new CircleSeqListImpl(items);
  }
  static class CircleSeqListImpl extends BaseTypeImpl.ServerExpressionListImpl implements CtsCircleSeqExpr {
    CircleSeqListImpl(Object[] items) {
      super(items);
    }
  }
  static class CircleSeqCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements CtsCircleSeqExpr {
    CircleSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class CircleCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements CtsCircleExpr {
    CircleCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
 
  @Override
  public CtsPeriodSeqExpr periodSeq(CtsPeriodExpr... items) {
    return new PeriodSeqListImpl(items);
  }
  static class PeriodSeqListImpl extends BaseTypeImpl.ServerExpressionListImpl implements CtsPeriodSeqExpr {
    PeriodSeqListImpl(Object[] items) {
      super(items);
    }
  }
  static class PeriodSeqCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements CtsPeriodSeqExpr {
    PeriodSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class PeriodCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements CtsPeriodExpr {
    PeriodCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
 
  @Override
  public CtsPointSeqExpr pointSeq(CtsPointExpr... items) {
    return new PointSeqListImpl(items);
  }
  static class PointSeqListImpl extends BaseTypeImpl.ServerExpressionListImpl implements CtsPointSeqExpr {
    PointSeqListImpl(Object[] items) {
      super(items);
    }
  }
  static class PointSeqCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements CtsPointSeqExpr {
    PointSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class PointCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements CtsPointExpr {
    PointCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
 
  @Override
  public CtsPolygonSeqExpr polygonSeq(CtsPolygonExpr... items) {
    return new PolygonSeqListImpl(items);
  }
  static class PolygonSeqListImpl extends BaseTypeImpl.ServerExpressionListImpl implements CtsPolygonSeqExpr {
    PolygonSeqListImpl(Object[] items) {
      super(items);
    }
  }
  static class PolygonSeqCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements CtsPolygonSeqExpr {
    PolygonSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class PolygonCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements CtsPolygonExpr {
    PolygonCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
 
  @Override
  public CtsQuerySeqExpr querySeq(CtsQueryExpr... items) {
    return new QuerySeqListImpl(items);
  }
  static class QuerySeqListImpl extends BaseTypeImpl.ServerExpressionListImpl implements CtsQuerySeqExpr {
    QuerySeqListImpl(Object[] items) {
      super(items);
    }
  }
  static class QuerySeqCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements CtsQuerySeqExpr {
    QuerySeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class QueryCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements CtsQueryExpr {
    QueryCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
 
  @Override
  public CtsReferenceSeqExpr referenceSeq(CtsReferenceExpr... items) {
    return new ReferenceSeqListImpl(items);
  }
  static class ReferenceSeqListImpl extends BaseTypeImpl.ServerExpressionListImpl implements CtsReferenceSeqExpr {
    ReferenceSeqListImpl(Object[] items) {
      super(items);
    }
  }
  static class ReferenceSeqCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements CtsReferenceSeqExpr {
    ReferenceSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class ReferenceCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements CtsReferenceExpr {
    ReferenceCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
 
  @Override
  public CtsRegionSeqExpr regionSeq(CtsRegionExpr... items) {
    return new RegionSeqListImpl(items);
  }
  static class RegionSeqListImpl extends BaseTypeImpl.ServerExpressionListImpl implements CtsRegionSeqExpr {
    RegionSeqListImpl(Object[] items) {
      super(items);
    }
  }
  static class RegionSeqCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements CtsRegionSeqExpr {
    RegionSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class RegionCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements CtsRegionExpr {
    RegionCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }

  }
