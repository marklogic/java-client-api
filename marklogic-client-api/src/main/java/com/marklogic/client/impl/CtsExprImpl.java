/*
 * Copyright (c) 2024 MarkLogic Corporation
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
import com.marklogic.client.type.XsIntegerVal;
import com.marklogic.client.type.XsQNameSeqVal;
import com.marklogic.client.type.XsQNameVal;
import com.marklogic.client.type.XsStringSeqVal;
import com.marklogic.client.type.XsStringVal;
import com.marklogic.client.type.XsUnsignedLongVal;

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
  public CtsQueryExpr afterQuery(ServerExpression timestamp) {
    if (timestamp == null) {
      throw new IllegalArgumentException("timestamp parameter for afterQuery() cannot be null");
    }
    return new QueryCallImpl("cts", "after-query", new Object[]{ timestamp });
  }

  
  @Override
  public CtsQueryExpr andNotQuery(ServerExpression positiveQuery, ServerExpression negativeQuery) {
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
  public CtsQueryExpr andQuery(ServerExpression queries) {
    return new QueryCallImpl("cts", "and-query", new Object[]{ queries });
  }

  
  @Override
  public CtsQueryExpr andQuery(ServerExpression queries, String options) {
    return andQuery(queries, (options == null) ? (XsStringVal) null : xs.string(options));
  }

  
  @Override
  public CtsQueryExpr andQuery(ServerExpression queries, XsStringSeqVal options) {
    return new QueryCallImpl("cts", "and-query", new Object[]{ queries, options });
  }

  
  @Override
  public CtsQueryExpr beforeQuery(ServerExpression timestamp) {
    if (timestamp == null) {
      throw new IllegalArgumentException("timestamp parameter for beforeQuery() cannot be null");
    }
    return new QueryCallImpl("cts", "before-query", new Object[]{ timestamp });
  }

  
  @Override
  public CtsQueryExpr boostQuery(ServerExpression matchingQuery, ServerExpression boostingQuery) {
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
  public CtsBoxExpr box(ServerExpression south, ServerExpression west, ServerExpression north, ServerExpression east) {
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
  public ServerExpression boxEast(ServerExpression box) {
    if (box == null) {
      throw new IllegalArgumentException("box parameter for boxEast() cannot be null");
    }
    return new XsExprImpl.NumericCallImpl("cts", "box-east", new Object[]{ box });
  }

  
  @Override
  public ServerExpression boxNorth(ServerExpression box) {
    if (box == null) {
      throw new IllegalArgumentException("box parameter for boxNorth() cannot be null");
    }
    return new XsExprImpl.NumericCallImpl("cts", "box-north", new Object[]{ box });
  }

  
  @Override
  public ServerExpression boxSouth(ServerExpression box) {
    if (box == null) {
      throw new IllegalArgumentException("box parameter for boxSouth() cannot be null");
    }
    return new XsExprImpl.NumericCallImpl("cts", "box-south", new Object[]{ box });
  }

  
  @Override
  public ServerExpression boxWest(ServerExpression box) {
    if (box == null) {
      throw new IllegalArgumentException("box parameter for boxWest() cannot be null");
    }
    return new XsExprImpl.NumericCallImpl("cts", "box-west", new Object[]{ box });
  }

  
  @Override
  public CtsCircleExpr circle(double radius, ServerExpression center) {
    return circle(xs.doubleVal(radius), center);
  }

  
  @Override
  public CtsCircleExpr circle(ServerExpression radius, ServerExpression center) {
    if (radius == null) {
      throw new IllegalArgumentException("radius parameter for circle() cannot be null");
    }
    if (center == null) {
      throw new IllegalArgumentException("center parameter for circle() cannot be null");
    }
    return new CircleCallImpl("cts", "circle", new Object[]{ radius, center });
  }

  
  @Override
  public CtsPointExpr circleCenter(ServerExpression circle) {
    if (circle == null) {
      throw new IllegalArgumentException("circle parameter for circleCenter() cannot be null");
    }
    return new PointCallImpl("cts", "circle-center", new Object[]{ circle });
  }

  
  @Override
  public ServerExpression circleRadius(ServerExpression circle) {
    if (circle == null) {
      throw new IllegalArgumentException("circle parameter for circleRadius() cannot be null");
    }
    return new XsExprImpl.NumericCallImpl("cts", "circle-radius", new Object[]{ circle });
  }

  
  @Override
  public CtsQueryExpr collectionQuery(String uris) {
    return collectionQuery((uris == null) ? (XsStringVal) null : xs.string(uris));
  }

  
  @Override
  public CtsQueryExpr collectionQuery(ServerExpression uris) {
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
  public CtsReferenceExpr collectionReference(ServerExpression options) {
    return new ReferenceCallImpl("cts", "collection-reference", new Object[]{ options });
  }

  
  @Override
  public CtsQueryExpr columnRangeQuery(String schema, String view, String column, String value) {
    return columnRangeQuery((schema == null) ? (XsStringVal) null : xs.string(schema), (view == null) ? (XsStringVal) null : xs.string(view), (column == null) ? (XsStringVal) null : xs.string(column), (value == null) ? (XsAnyAtomicTypeVal) null : xs.string(value));
  }

  
  @Override
  public CtsQueryExpr columnRangeQuery(ServerExpression schema, ServerExpression view, ServerExpression column, ServerExpression value) {
    if (schema == null) {
      throw new IllegalArgumentException("schema parameter for columnRangeQuery() cannot be null");
    }
    if (view == null) {
      throw new IllegalArgumentException("view parameter for columnRangeQuery() cannot be null");
    }
    if (column == null) {
      throw new IllegalArgumentException("column parameter for columnRangeQuery() cannot be null");
    }
    return new QueryCallImpl("cts", "column-range-query", new Object[]{ schema, view, column, value });
  }

  
  @Override
  public CtsQueryExpr columnRangeQuery(String schema, String view, String column, String value, String operator) {
    return columnRangeQuery((schema == null) ? (XsStringVal) null : xs.string(schema), (view == null) ? (XsStringVal) null : xs.string(view), (column == null) ? (XsStringVal) null : xs.string(column), (value == null) ? (XsAnyAtomicTypeVal) null : xs.string(value), (operator == null) ? (XsStringVal) null : xs.string(operator));
  }

  
  @Override
  public CtsQueryExpr columnRangeQuery(ServerExpression schema, ServerExpression view, ServerExpression column, ServerExpression value, ServerExpression operator) {
    if (schema == null) {
      throw new IllegalArgumentException("schema parameter for columnRangeQuery() cannot be null");
    }
    if (view == null) {
      throw new IllegalArgumentException("view parameter for columnRangeQuery() cannot be null");
    }
    if (column == null) {
      throw new IllegalArgumentException("column parameter for columnRangeQuery() cannot be null");
    }
    return new QueryCallImpl("cts", "column-range-query", new Object[]{ schema, view, column, value, operator });
  }

  
  @Override
  public CtsQueryExpr columnRangeQuery(String schema, String view, String column, String value, String operator, String... options) {
    return columnRangeQuery((schema == null) ? (XsStringVal) null : xs.string(schema), (view == null) ? (XsStringVal) null : xs.string(view), (column == null) ? (XsStringVal) null : xs.string(column), (value == null) ? (XsAnyAtomicTypeVal) null : xs.string(value), (operator == null) ? (XsStringVal) null : xs.string(operator), (options == null) ? (XsStringVal) null : xs.stringSeq(options));
  }

  
  @Override
  public CtsQueryExpr columnRangeQuery(ServerExpression schema, ServerExpression view, ServerExpression column, ServerExpression value, ServerExpression operator, ServerExpression options) {
    if (schema == null) {
      throw new IllegalArgumentException("schema parameter for columnRangeQuery() cannot be null");
    }
    if (view == null) {
      throw new IllegalArgumentException("view parameter for columnRangeQuery() cannot be null");
    }
    if (column == null) {
      throw new IllegalArgumentException("column parameter for columnRangeQuery() cannot be null");
    }
    return new QueryCallImpl("cts", "column-range-query", new Object[]{ schema, view, column, value, operator, options });
  }

  
  @Override
  public CtsQueryExpr columnRangeQuery(String schema, String view, String column, String value, String operator, String options, double weight) {
    return columnRangeQuery((schema == null) ? (XsStringVal) null : xs.string(schema), (view == null) ? (XsStringVal) null : xs.string(view), (column == null) ? (XsStringVal) null : xs.string(column), (value == null) ? (XsAnyAtomicTypeVal) null : xs.string(value), (operator == null) ? (XsStringVal) null : xs.string(operator), (options == null) ? (XsStringVal) null : xs.string(options), xs.doubleVal(weight));
  }

  
  @Override
  public CtsQueryExpr columnRangeQuery(ServerExpression schema, ServerExpression view, ServerExpression column, ServerExpression value, ServerExpression operator, ServerExpression options, ServerExpression weight) {
    if (schema == null) {
      throw new IllegalArgumentException("schema parameter for columnRangeQuery() cannot be null");
    }
    if (view == null) {
      throw new IllegalArgumentException("view parameter for columnRangeQuery() cannot be null");
    }
    if (column == null) {
      throw new IllegalArgumentException("column parameter for columnRangeQuery() cannot be null");
    }
    return new QueryCallImpl("cts", "column-range-query", new Object[]{ schema, view, column, value, operator, options, weight });
  }

  
  @Override
  public CtsPolygonExpr complexPolygon(ServerExpression outer, ServerExpression inner) {
    if (outer == null) {
      throw new IllegalArgumentException("outer parameter for complexPolygon() cannot be null");
    }
    return new PolygonCallImpl("cts", "complex-polygon", new Object[]{ outer, inner });
  }

  
  @Override
  public CtsQueryExpr directoryQuery(String uris) {
    return directoryQuery((uris == null) ? (XsStringVal) null : xs.string(uris));
  }

  
  @Override
  public CtsQueryExpr directoryQuery(ServerExpression uris) {
    return new QueryCallImpl("cts", "directory-query", new Object[]{ uris });
  }

  
  @Override
  public CtsQueryExpr directoryQuery(String uris, String depth) {
    return directoryQuery((uris == null) ? (XsStringVal) null : xs.string(uris), (depth == null) ? (XsStringVal) null : xs.string(depth));
  }

  
  @Override
  public CtsQueryExpr directoryQuery(ServerExpression uris, ServerExpression depth) {
    return new QueryCallImpl("cts", "directory-query", new Object[]{ uris, depth });
  }

  
  @Override
  public CtsQueryExpr documentFormatQuery(String format) {
    return documentFormatQuery((format == null) ? (XsStringVal) null : xs.string(format));
  }

  
  @Override
  public CtsQueryExpr documentFormatQuery(ServerExpression format) {
    if (format == null) {
      throw new IllegalArgumentException("format parameter for documentFormatQuery() cannot be null");
    }
    return new QueryCallImpl("cts", "document-format-query", new Object[]{ format });
  }

  
  @Override
  public CtsQueryExpr documentFragmentQuery(ServerExpression query) {
    if (query == null) {
      throw new IllegalArgumentException("query parameter for documentFragmentQuery() cannot be null");
    }
    return new QueryCallImpl("cts", "document-fragment-query", new Object[]{ query });
  }

  
  @Override
  public CtsQueryExpr documentPermissionQuery(String role, String capability) {
    return documentPermissionQuery((role == null) ? (XsStringVal) null : xs.string(role), (capability == null) ? (XsStringVal) null : xs.string(capability));
  }

  
  @Override
  public CtsQueryExpr documentPermissionQuery(ServerExpression role, ServerExpression capability) {
    if (role == null) {
      throw new IllegalArgumentException("role parameter for documentPermissionQuery() cannot be null");
    }
    if (capability == null) {
      throw new IllegalArgumentException("capability parameter for documentPermissionQuery() cannot be null");
    }
    return new QueryCallImpl("cts", "document-permission-query", new Object[]{ role, capability });
  }

  
  @Override
  public CtsQueryExpr documentQuery(String uris) {
    return documentQuery((uris == null) ? (XsStringVal) null : xs.string(uris));
  }

  
  @Override
  public CtsQueryExpr documentQuery(ServerExpression uris) {
    return new QueryCallImpl("cts", "document-query", new Object[]{ uris });
  }

  
  @Override
  public CtsQueryExpr documentRootQuery(String root) {
    return documentRootQuery((root == null) ? (XsQNameVal) null : xs.QName(root));
  }

  
  @Override
  public CtsQueryExpr documentRootQuery(ServerExpression root) {
    if (root == null) {
      throw new IllegalArgumentException("root parameter for documentRootQuery() cannot be null");
    }
    return new QueryCallImpl("cts", "document-root-query", new Object[]{ root });
  }

  
  @Override
  public CtsQueryExpr elementAttributePairGeospatialQuery(String elementName, String latitudeName, String longitudeName, CtsRegionExpr... region) {
    return elementAttributePairGeospatialQuery((elementName == null) ? (XsQNameVal) null : xs.QName(elementName), (latitudeName == null) ? (XsQNameVal) null : xs.QName(latitudeName), (longitudeName == null) ? (XsQNameVal) null : xs.QName(longitudeName), new RegionSeqListImpl(region));
  }

  
  @Override
  public CtsQueryExpr elementAttributePairGeospatialQuery(ServerExpression elementName, ServerExpression latitudeName, ServerExpression longitudeName, ServerExpression region) {
    return new QueryCallImpl("cts", "element-attribute-pair-geospatial-query", new Object[]{ elementName, latitudeName, longitudeName, region });
  }

  
  @Override
  public CtsQueryExpr elementAttributePairGeospatialQuery(String elementName, String latitudeName, String longitudeName, ServerExpression region, String... options) {
    return elementAttributePairGeospatialQuery((elementName == null) ? (XsQNameVal) null : xs.QName(elementName), (latitudeName == null) ? (XsQNameVal) null : xs.QName(latitudeName), (longitudeName == null) ? (XsQNameVal) null : xs.QName(longitudeName), region, (options == null) ? (XsStringVal) null : xs.stringSeq(options));
  }

  
  @Override
  public CtsQueryExpr elementAttributePairGeospatialQuery(ServerExpression elementName, ServerExpression latitudeName, ServerExpression longitudeName, ServerExpression region, ServerExpression options) {
    return new QueryCallImpl("cts", "element-attribute-pair-geospatial-query", new Object[]{ elementName, latitudeName, longitudeName, region, options });
  }

  
  @Override
  public CtsQueryExpr elementAttributePairGeospatialQuery(String elementName, String latitudeName, String longitudeName, ServerExpression region, String options, double weight) {
    return elementAttributePairGeospatialQuery((elementName == null) ? (XsQNameVal) null : xs.QName(elementName), (latitudeName == null) ? (XsQNameVal) null : xs.QName(latitudeName), (longitudeName == null) ? (XsQNameVal) null : xs.QName(longitudeName), region, (options == null) ? (XsStringVal) null : xs.string(options), xs.doubleVal(weight));
  }

  
  @Override
  public CtsQueryExpr elementAttributePairGeospatialQuery(ServerExpression elementName, ServerExpression latitudeName, ServerExpression longitudeName, ServerExpression region, ServerExpression options, ServerExpression weight) {
    return new QueryCallImpl("cts", "element-attribute-pair-geospatial-query", new Object[]{ elementName, latitudeName, longitudeName, region, options, weight });
  }

  
  @Override
  public CtsQueryExpr elementAttributeRangeQuery(String elementName, String attributeName, String operator, String value) {
    return elementAttributeRangeQuery((elementName == null) ? (XsQNameVal) null : xs.QName(elementName), (attributeName == null) ? (XsQNameVal) null : xs.QName(attributeName), (operator == null) ? (XsStringVal) null : xs.string(operator), (value == null) ? (XsAnyAtomicTypeVal) null : xs.string(value));
  }

  
  @Override
  public CtsQueryExpr elementAttributeRangeQuery(ServerExpression elementName, ServerExpression attributeName, ServerExpression operator, ServerExpression value) {
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
  public CtsQueryExpr elementAttributeRangeQuery(ServerExpression elementName, ServerExpression attributeName, ServerExpression operator, ServerExpression value, ServerExpression options) {
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
  public CtsQueryExpr elementAttributeRangeQuery(ServerExpression elementName, ServerExpression attributeName, ServerExpression operator, ServerExpression value, ServerExpression options, ServerExpression weight) {
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
  public CtsReferenceExpr elementAttributeReference(ServerExpression element, ServerExpression attribute) {
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
  public CtsReferenceExpr elementAttributeReference(ServerExpression element, ServerExpression attribute, ServerExpression options) {
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
  public CtsQueryExpr elementAttributeValueQuery(ServerExpression elementName, ServerExpression attributeName, ServerExpression text) {
    return new QueryCallImpl("cts", "element-attribute-value-query", new Object[]{ elementName, attributeName, text });
  }

  
  @Override
  public CtsQueryExpr elementAttributeValueQuery(String elementName, String attributeName, String text, String... options) {
    return elementAttributeValueQuery((elementName == null) ? (XsQNameVal) null : xs.QName(elementName), (attributeName == null) ? (XsQNameVal) null : xs.QName(attributeName), (text == null) ? (XsStringVal) null : xs.string(text), (options == null) ? (XsStringVal) null : xs.stringSeq(options));
  }

  
  @Override
  public CtsQueryExpr elementAttributeValueQuery(ServerExpression elementName, ServerExpression attributeName, ServerExpression text, ServerExpression options) {
    return new QueryCallImpl("cts", "element-attribute-value-query", new Object[]{ elementName, attributeName, text, options });
  }

  
  @Override
  public CtsQueryExpr elementAttributeValueQuery(String elementName, String attributeName, String text, String options, double weight) {
    return elementAttributeValueQuery((elementName == null) ? (XsQNameVal) null : xs.QName(elementName), (attributeName == null) ? (XsQNameVal) null : xs.QName(attributeName), (text == null) ? (XsStringVal) null : xs.string(text), (options == null) ? (XsStringVal) null : xs.string(options), xs.doubleVal(weight));
  }

  
  @Override
  public CtsQueryExpr elementAttributeValueQuery(ServerExpression elementName, ServerExpression attributeName, ServerExpression text, ServerExpression options, ServerExpression weight) {
    return new QueryCallImpl("cts", "element-attribute-value-query", new Object[]{ elementName, attributeName, text, options, weight });
  }

  
  @Override
  public CtsQueryExpr elementAttributeWordQuery(String elementName, String attributeName, String text) {
    return elementAttributeWordQuery((elementName == null) ? (XsQNameVal) null : xs.QName(elementName), (attributeName == null) ? (XsQNameVal) null : xs.QName(attributeName), (text == null) ? (XsStringVal) null : xs.string(text));
  }

  
  @Override
  public CtsQueryExpr elementAttributeWordQuery(ServerExpression elementName, ServerExpression attributeName, ServerExpression text) {
    return new QueryCallImpl("cts", "element-attribute-word-query", new Object[]{ elementName, attributeName, text });
  }

  
  @Override
  public CtsQueryExpr elementAttributeWordQuery(String elementName, String attributeName, String text, String... options) {
    return elementAttributeWordQuery((elementName == null) ? (XsQNameVal) null : xs.QName(elementName), (attributeName == null) ? (XsQNameVal) null : xs.QName(attributeName), (text == null) ? (XsStringVal) null : xs.string(text), (options == null) ? (XsStringVal) null : xs.stringSeq(options));
  }

  
  @Override
  public CtsQueryExpr elementAttributeWordQuery(ServerExpression elementName, ServerExpression attributeName, ServerExpression text, ServerExpression options) {
    return new QueryCallImpl("cts", "element-attribute-word-query", new Object[]{ elementName, attributeName, text, options });
  }

  
  @Override
  public CtsQueryExpr elementAttributeWordQuery(String elementName, String attributeName, String text, String options, double weight) {
    return elementAttributeWordQuery((elementName == null) ? (XsQNameVal) null : xs.QName(elementName), (attributeName == null) ? (XsQNameVal) null : xs.QName(attributeName), (text == null) ? (XsStringVal) null : xs.string(text), (options == null) ? (XsStringVal) null : xs.string(options), xs.doubleVal(weight));
  }

  
  @Override
  public CtsQueryExpr elementAttributeWordQuery(ServerExpression elementName, ServerExpression attributeName, ServerExpression text, ServerExpression options, ServerExpression weight) {
    return new QueryCallImpl("cts", "element-attribute-word-query", new Object[]{ elementName, attributeName, text, options, weight });
  }

  
  @Override
  public CtsQueryExpr elementChildGeospatialQuery(String elementName, String childName, CtsRegionExpr... region) {
    return elementChildGeospatialQuery((elementName == null) ? (XsQNameVal) null : xs.QName(elementName), (childName == null) ? (XsQNameVal) null : xs.QName(childName), new RegionSeqListImpl(region));
  }

  
  @Override
  public CtsQueryExpr elementChildGeospatialQuery(ServerExpression elementName, ServerExpression childName, ServerExpression region) {
    return new QueryCallImpl("cts", "element-child-geospatial-query", new Object[]{ elementName, childName, region });
  }

  
  @Override
  public CtsQueryExpr elementChildGeospatialQuery(String elementName, String childName, ServerExpression region, String... options) {
    return elementChildGeospatialQuery((elementName == null) ? (XsQNameVal) null : xs.QName(elementName), (childName == null) ? (XsQNameVal) null : xs.QName(childName), region, (options == null) ? (XsStringVal) null : xs.stringSeq(options));
  }

  
  @Override
  public CtsQueryExpr elementChildGeospatialQuery(ServerExpression elementName, ServerExpression childName, ServerExpression region, ServerExpression options) {
    return new QueryCallImpl("cts", "element-child-geospatial-query", new Object[]{ elementName, childName, region, options });
  }

  
  @Override
  public CtsQueryExpr elementChildGeospatialQuery(String elementName, String childName, ServerExpression region, String options, double weight) {
    return elementChildGeospatialQuery((elementName == null) ? (XsQNameVal) null : xs.QName(elementName), (childName == null) ? (XsQNameVal) null : xs.QName(childName), region, (options == null) ? (XsStringVal) null : xs.string(options), xs.doubleVal(weight));
  }

  
  @Override
  public CtsQueryExpr elementChildGeospatialQuery(ServerExpression elementName, ServerExpression childName, ServerExpression region, ServerExpression options, ServerExpression weight) {
    return new QueryCallImpl("cts", "element-child-geospatial-query", new Object[]{ elementName, childName, region, options, weight });
  }

  
  @Override
  public CtsQueryExpr elementGeospatialQuery(String elementName, CtsRegionExpr... region) {
    return elementGeospatialQuery((elementName == null) ? (XsQNameVal) null : xs.QName(elementName), new RegionSeqListImpl(region));
  }

  
  @Override
  public CtsQueryExpr elementGeospatialQuery(ServerExpression elementName, ServerExpression region) {
    return new QueryCallImpl("cts", "element-geospatial-query", new Object[]{ elementName, region });
  }

  
  @Override
  public CtsQueryExpr elementGeospatialQuery(String elementName, ServerExpression region, String... options) {
    return elementGeospatialQuery((elementName == null) ? (XsQNameVal) null : xs.QName(elementName), region, (options == null) ? (XsStringVal) null : xs.stringSeq(options));
  }

  
  @Override
  public CtsQueryExpr elementGeospatialQuery(ServerExpression elementName, ServerExpression region, ServerExpression options) {
    return new QueryCallImpl("cts", "element-geospatial-query", new Object[]{ elementName, region, options });
  }

  
  @Override
  public CtsQueryExpr elementGeospatialQuery(String elementName, ServerExpression region, String options, double weight) {
    return elementGeospatialQuery((elementName == null) ? (XsQNameVal) null : xs.QName(elementName), region, (options == null) ? (XsStringVal) null : xs.string(options), xs.doubleVal(weight));
  }

  
  @Override
  public CtsQueryExpr elementGeospatialQuery(ServerExpression elementName, ServerExpression region, ServerExpression options, ServerExpression weight) {
    return new QueryCallImpl("cts", "element-geospatial-query", new Object[]{ elementName, region, options, weight });
  }

  
  @Override
  public CtsQueryExpr elementPairGeospatialQuery(String elementName, String latitudeName, String longitudeName, CtsRegionExpr... region) {
    return elementPairGeospatialQuery((elementName == null) ? (XsQNameVal) null : xs.QName(elementName), (latitudeName == null) ? (XsQNameVal) null : xs.QName(latitudeName), (longitudeName == null) ? (XsQNameVal) null : xs.QName(longitudeName), new RegionSeqListImpl(region));
  }

  
  @Override
  public CtsQueryExpr elementPairGeospatialQuery(ServerExpression elementName, ServerExpression latitudeName, ServerExpression longitudeName, ServerExpression region) {
    return new QueryCallImpl("cts", "element-pair-geospatial-query", new Object[]{ elementName, latitudeName, longitudeName, region });
  }

  
  @Override
  public CtsQueryExpr elementPairGeospatialQuery(String elementName, String latitudeName, String longitudeName, ServerExpression region, String... options) {
    return elementPairGeospatialQuery((elementName == null) ? (XsQNameVal) null : xs.QName(elementName), (latitudeName == null) ? (XsQNameVal) null : xs.QName(latitudeName), (longitudeName == null) ? (XsQNameVal) null : xs.QName(longitudeName), region, (options == null) ? (XsStringVal) null : xs.stringSeq(options));
  }

  
  @Override
  public CtsQueryExpr elementPairGeospatialQuery(ServerExpression elementName, ServerExpression latitudeName, ServerExpression longitudeName, ServerExpression region, ServerExpression options) {
    return new QueryCallImpl("cts", "element-pair-geospatial-query", new Object[]{ elementName, latitudeName, longitudeName, region, options });
  }

  
  @Override
  public CtsQueryExpr elementPairGeospatialQuery(String elementName, String latitudeName, String longitudeName, ServerExpression region, String options, double weight) {
    return elementPairGeospatialQuery((elementName == null) ? (XsQNameVal) null : xs.QName(elementName), (latitudeName == null) ? (XsQNameVal) null : xs.QName(latitudeName), (longitudeName == null) ? (XsQNameVal) null : xs.QName(longitudeName), region, (options == null) ? (XsStringVal) null : xs.string(options), xs.doubleVal(weight));
  }

  
  @Override
  public CtsQueryExpr elementPairGeospatialQuery(ServerExpression elementName, ServerExpression latitudeName, ServerExpression longitudeName, ServerExpression region, ServerExpression options, ServerExpression weight) {
    return new QueryCallImpl("cts", "element-pair-geospatial-query", new Object[]{ elementName, latitudeName, longitudeName, region, options, weight });
  }

  
  @Override
  public CtsQueryExpr elementQuery(String elementName, ServerExpression query) {
    return elementQuery((elementName == null) ? (XsQNameVal) null : xs.QName(elementName), query);
  }

  
  @Override
  public CtsQueryExpr elementQuery(ServerExpression elementName, ServerExpression query) {
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
  public CtsQueryExpr elementRangeQuery(ServerExpression elementName, ServerExpression operator, ServerExpression value) {
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
  public CtsQueryExpr elementRangeQuery(ServerExpression elementName, ServerExpression operator, ServerExpression value, ServerExpression options) {
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
  public CtsQueryExpr elementRangeQuery(ServerExpression elementName, ServerExpression operator, ServerExpression value, ServerExpression options, ServerExpression weight) {
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
  public CtsReferenceExpr elementReference(ServerExpression element) {
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
  public CtsReferenceExpr elementReference(ServerExpression element, ServerExpression options) {
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
  public CtsQueryExpr elementValueQuery(ServerExpression elementName) {
    return new QueryCallImpl("cts", "element-value-query", new Object[]{ elementName });
  }

  
  @Override
  public CtsQueryExpr elementValueQuery(String elementName, String text) {
    return elementValueQuery((elementName == null) ? (XsQNameVal) null : xs.QName(elementName), (text == null) ? (XsStringVal) null : xs.string(text));
  }

  
  @Override
  public CtsQueryExpr elementValueQuery(ServerExpression elementName, ServerExpression text) {
    return new QueryCallImpl("cts", "element-value-query", new Object[]{ elementName, text });
  }

  
  @Override
  public CtsQueryExpr elementValueQuery(String elementName, String text, String... options) {
    return elementValueQuery((elementName == null) ? (XsQNameVal) null : xs.QName(elementName), (text == null) ? (XsStringVal) null : xs.string(text), (options == null) ? (XsStringVal) null : xs.stringSeq(options));
  }

  
  @Override
  public CtsQueryExpr elementValueQuery(ServerExpression elementName, ServerExpression text, ServerExpression options) {
    return new QueryCallImpl("cts", "element-value-query", new Object[]{ elementName, text, options });
  }

  
  @Override
  public CtsQueryExpr elementValueQuery(String elementName, String text, String options, double weight) {
    return elementValueQuery((elementName == null) ? (XsQNameVal) null : xs.QName(elementName), (text == null) ? (XsStringVal) null : xs.string(text), (options == null) ? (XsStringVal) null : xs.string(options), xs.doubleVal(weight));
  }

  
  @Override
  public CtsQueryExpr elementValueQuery(ServerExpression elementName, ServerExpression text, ServerExpression options, ServerExpression weight) {
    return new QueryCallImpl("cts", "element-value-query", new Object[]{ elementName, text, options, weight });
  }

  
  @Override
  public CtsQueryExpr elementWordQuery(String elementName, String text) {
    return elementWordQuery((elementName == null) ? (XsQNameVal) null : xs.QName(elementName), (text == null) ? (XsStringVal) null : xs.string(text));
  }

  
  @Override
  public CtsQueryExpr elementWordQuery(ServerExpression elementName, ServerExpression text) {
    return new QueryCallImpl("cts", "element-word-query", new Object[]{ elementName, text });
  }

  
  @Override
  public CtsQueryExpr elementWordQuery(String elementName, String text, String... options) {
    return elementWordQuery((elementName == null) ? (XsQNameVal) null : xs.QName(elementName), (text == null) ? (XsStringVal) null : xs.string(text), (options == null) ? (XsStringVal) null : xs.stringSeq(options));
  }

  
  @Override
  public CtsQueryExpr elementWordQuery(ServerExpression elementName, ServerExpression text, ServerExpression options) {
    return new QueryCallImpl("cts", "element-word-query", new Object[]{ elementName, text, options });
  }

  
  @Override
  public CtsQueryExpr elementWordQuery(String elementName, String text, String options, double weight) {
    return elementWordQuery((elementName == null) ? (XsQNameVal) null : xs.QName(elementName), (text == null) ? (XsStringVal) null : xs.string(text), (options == null) ? (XsStringVal) null : xs.string(options), xs.doubleVal(weight));
  }

  
  @Override
  public CtsQueryExpr elementWordQuery(ServerExpression elementName, ServerExpression text, ServerExpression options, ServerExpression weight) {
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
  public CtsQueryExpr fieldRangeQuery(ServerExpression fieldName, ServerExpression operator, ServerExpression value) {
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
  public CtsQueryExpr fieldRangeQuery(ServerExpression fieldName, ServerExpression operator, ServerExpression value, ServerExpression options) {
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
  public CtsQueryExpr fieldRangeQuery(ServerExpression fieldName, ServerExpression operator, ServerExpression value, ServerExpression options, ServerExpression weight) {
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
  public CtsReferenceExpr fieldReference(ServerExpression field) {
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
  public CtsReferenceExpr fieldReference(ServerExpression field, ServerExpression options) {
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
  public CtsQueryExpr fieldValueQuery(ServerExpression fieldName, ServerExpression text) {
    return new QueryCallImpl("cts", "field-value-query", new Object[]{ fieldName, text });
  }

  
  @Override
  public CtsQueryExpr fieldValueQuery(String fieldName, String text, String... options) {
    return fieldValueQuery((fieldName == null) ? (XsStringVal) null : xs.string(fieldName), (text == null) ? (XsAnyAtomicTypeVal) null : xs.string(text), (options == null) ? (XsStringVal) null : xs.stringSeq(options));
  }

  
  @Override
  public CtsQueryExpr fieldValueQuery(ServerExpression fieldName, ServerExpression text, ServerExpression options) {
    return new QueryCallImpl("cts", "field-value-query", new Object[]{ fieldName, text, options });
  }

  
  @Override
  public CtsQueryExpr fieldValueQuery(String fieldName, String text, String options, double weight) {
    return fieldValueQuery((fieldName == null) ? (XsStringVal) null : xs.string(fieldName), (text == null) ? (XsAnyAtomicTypeVal) null : xs.string(text), (options == null) ? (XsStringVal) null : xs.string(options), xs.doubleVal(weight));
  }

  
  @Override
  public CtsQueryExpr fieldValueQuery(ServerExpression fieldName, ServerExpression text, ServerExpression options, ServerExpression weight) {
    return new QueryCallImpl("cts", "field-value-query", new Object[]{ fieldName, text, options, weight });
  }

  
  @Override
  public CtsQueryExpr fieldWordQuery(String fieldName, String text) {
    return fieldWordQuery((fieldName == null) ? (XsStringVal) null : xs.string(fieldName), (text == null) ? (XsStringVal) null : xs.string(text));
  }

  
  @Override
  public CtsQueryExpr fieldWordQuery(ServerExpression fieldName, ServerExpression text) {
    return new QueryCallImpl("cts", "field-word-query", new Object[]{ fieldName, text });
  }

  
  @Override
  public CtsQueryExpr fieldWordQuery(String fieldName, String text, String... options) {
    return fieldWordQuery((fieldName == null) ? (XsStringVal) null : xs.string(fieldName), (text == null) ? (XsStringVal) null : xs.string(text), (options == null) ? (XsStringVal) null : xs.stringSeq(options));
  }

  
  @Override
  public CtsQueryExpr fieldWordQuery(ServerExpression fieldName, ServerExpression text, ServerExpression options) {
    return new QueryCallImpl("cts", "field-word-query", new Object[]{ fieldName, text, options });
  }

  
  @Override
  public CtsQueryExpr fieldWordQuery(String fieldName, String text, String options, double weight) {
    return fieldWordQuery((fieldName == null) ? (XsStringVal) null : xs.string(fieldName), (text == null) ? (XsStringVal) null : xs.string(text), (options == null) ? (XsStringVal) null : xs.string(options), xs.doubleVal(weight));
  }

  
  @Override
  public CtsQueryExpr fieldWordQuery(ServerExpression fieldName, ServerExpression text, ServerExpression options, ServerExpression weight) {
    return new QueryCallImpl("cts", "field-word-query", new Object[]{ fieldName, text, options, weight });
  }

  
  @Override
  public CtsReferenceExpr geospatialPathReference(String pathExpression) {
    return geospatialPathReference((pathExpression == null) ? (XsStringVal) null : xs.string(pathExpression));
  }

  
  @Override
  public CtsReferenceExpr geospatialPathReference(ServerExpression pathExpression) {
    if (pathExpression == null) {
      throw new IllegalArgumentException("pathExpression parameter for geospatialPathReference() cannot be null");
    }
    return new ReferenceCallImpl("cts", "geospatial-path-reference", new Object[]{ pathExpression });
  }

  
  @Override
  public CtsReferenceExpr geospatialPathReference(String pathExpression, String options) {
    return geospatialPathReference((pathExpression == null) ? (XsStringVal) null : xs.string(pathExpression), (options == null) ? (XsStringVal) null : xs.string(options));
  }

  
  @Override
  public CtsReferenceExpr geospatialPathReference(ServerExpression pathExpression, ServerExpression options) {
    if (pathExpression == null) {
      throw new IllegalArgumentException("pathExpression parameter for geospatialPathReference() cannot be null");
    }
    return new ReferenceCallImpl("cts", "geospatial-path-reference", new Object[]{ pathExpression, options });
  }

  
  @Override
  public CtsReferenceExpr geospatialPathReference(String pathExpression, String options, ServerExpression map) {
    return geospatialPathReference((pathExpression == null) ? (XsStringVal) null : xs.string(pathExpression), (options == null) ? (XsStringVal) null : xs.string(options), map);
  }

  
  @Override
  public CtsReferenceExpr geospatialPathReference(ServerExpression pathExpression, ServerExpression options, ServerExpression map) {
    if (pathExpression == null) {
      throw new IllegalArgumentException("pathExpression parameter for geospatialPathReference() cannot be null");
    }
    return new ReferenceCallImpl("cts", "geospatial-path-reference", new Object[]{ pathExpression, options, map });
  }

  
  @Override
  public CtsReferenceExpr geospatialRegionPathReference(String pathExpression) {
    return geospatialRegionPathReference((pathExpression == null) ? (XsStringVal) null : xs.string(pathExpression));
  }

  
  @Override
  public CtsReferenceExpr geospatialRegionPathReference(ServerExpression pathExpression) {
    if (pathExpression == null) {
      throw new IllegalArgumentException("pathExpression parameter for geospatialRegionPathReference() cannot be null");
    }
    return new ReferenceCallImpl("cts", "geospatial-region-path-reference", new Object[]{ pathExpression });
  }

  
  @Override
  public CtsReferenceExpr geospatialRegionPathReference(String pathExpression, String options) {
    return geospatialRegionPathReference((pathExpression == null) ? (XsStringVal) null : xs.string(pathExpression), (options == null) ? (XsStringVal) null : xs.string(options));
  }

  
  @Override
  public CtsReferenceExpr geospatialRegionPathReference(ServerExpression pathExpression, ServerExpression options) {
    if (pathExpression == null) {
      throw new IllegalArgumentException("pathExpression parameter for geospatialRegionPathReference() cannot be null");
    }
    return new ReferenceCallImpl("cts", "geospatial-region-path-reference", new Object[]{ pathExpression, options });
  }

  
  @Override
  public CtsReferenceExpr geospatialRegionPathReference(String pathExpression, String options, ServerExpression namespaces) {
    return geospatialRegionPathReference((pathExpression == null) ? (XsStringVal) null : xs.string(pathExpression), (options == null) ? (XsStringVal) null : xs.string(options), namespaces);
  }

  
  @Override
  public CtsReferenceExpr geospatialRegionPathReference(ServerExpression pathExpression, ServerExpression options, ServerExpression namespaces) {
    if (pathExpression == null) {
      throw new IllegalArgumentException("pathExpression parameter for geospatialRegionPathReference() cannot be null");
    }
    return new ReferenceCallImpl("cts", "geospatial-region-path-reference", new Object[]{ pathExpression, options, namespaces });
  }

  
  @Override
  public CtsReferenceExpr geospatialRegionPathReference(String pathExpression, String options, ServerExpression namespaces, long geohashPrecision) {
    return geospatialRegionPathReference((pathExpression == null) ? (XsStringVal) null : xs.string(pathExpression), (options == null) ? (XsStringVal) null : xs.string(options), namespaces, xs.integer(geohashPrecision));
  }

  
  @Override
  public CtsReferenceExpr geospatialRegionPathReference(ServerExpression pathExpression, ServerExpression options, ServerExpression namespaces, ServerExpression geohashPrecision) {
    if (pathExpression == null) {
      throw new IllegalArgumentException("pathExpression parameter for geospatialRegionPathReference() cannot be null");
    }
    return new ReferenceCallImpl("cts", "geospatial-region-path-reference", new Object[]{ pathExpression, options, namespaces, geohashPrecision });
  }

  
  @Override
  public CtsReferenceExpr geospatialRegionPathReference(String pathExpression, String options, ServerExpression namespaces, long geohashPrecision, String units) {
    return geospatialRegionPathReference((pathExpression == null) ? (XsStringVal) null : xs.string(pathExpression), (options == null) ? (XsStringVal) null : xs.string(options), namespaces, xs.integer(geohashPrecision), (units == null) ? (XsStringVal) null : xs.string(units));
  }

  
  @Override
  public CtsReferenceExpr geospatialRegionPathReference(ServerExpression pathExpression, ServerExpression options, ServerExpression namespaces, ServerExpression geohashPrecision, ServerExpression units) {
    if (pathExpression == null) {
      throw new IllegalArgumentException("pathExpression parameter for geospatialRegionPathReference() cannot be null");
    }
    return new ReferenceCallImpl("cts", "geospatial-region-path-reference", new Object[]{ pathExpression, options, namespaces, geohashPrecision, units });
  }

  
  @Override
  public CtsReferenceExpr geospatialRegionPathReference(String pathExpression, String options, ServerExpression namespaces, long geohashPrecision, String units, String invalidValues) {
    return geospatialRegionPathReference((pathExpression == null) ? (XsStringVal) null : xs.string(pathExpression), (options == null) ? (XsStringVal) null : xs.string(options), namespaces, xs.integer(geohashPrecision), (units == null) ? (XsStringVal) null : xs.string(units), (invalidValues == null) ? (XsStringVal) null : xs.string(invalidValues));
  }

  
  @Override
  public CtsReferenceExpr geospatialRegionPathReference(ServerExpression pathExpression, ServerExpression options, ServerExpression namespaces, ServerExpression geohashPrecision, ServerExpression units, ServerExpression invalidValues) {
    if (pathExpression == null) {
      throw new IllegalArgumentException("pathExpression parameter for geospatialRegionPathReference() cannot be null");
    }
    return new ReferenceCallImpl("cts", "geospatial-region-path-reference", new Object[]{ pathExpression, options, namespaces, geohashPrecision, units, invalidValues });
  }

  
  @Override
  public CtsQueryExpr geospatialRegionQuery(ServerExpression reference, String operation, CtsRegionExpr... region) {
    return geospatialRegionQuery(reference, (operation == null) ? (XsStringVal) null : xs.string(operation), new RegionSeqListImpl(region));
  }

  
  @Override
  public CtsQueryExpr geospatialRegionQuery(ServerExpression reference, ServerExpression operation, ServerExpression region) {
    if (operation == null) {
      throw new IllegalArgumentException("operation parameter for geospatialRegionQuery() cannot be null");
    }
    return new QueryCallImpl("cts", "geospatial-region-query", new Object[]{ reference, operation, region });
  }

  
  @Override
  public CtsQueryExpr geospatialRegionQuery(ServerExpression reference, String operation, ServerExpression region, String... options) {
    return geospatialRegionQuery(reference, (operation == null) ? (XsStringVal) null : xs.string(operation), region, (options == null) ? (XsStringVal) null : xs.stringSeq(options));
  }

  
  @Override
  public CtsQueryExpr geospatialRegionQuery(ServerExpression reference, ServerExpression operation, ServerExpression region, ServerExpression options) {
    if (operation == null) {
      throw new IllegalArgumentException("operation parameter for geospatialRegionQuery() cannot be null");
    }
    return new QueryCallImpl("cts", "geospatial-region-query", new Object[]{ reference, operation, region, options });
  }

  
  @Override
  public CtsQueryExpr geospatialRegionQuery(ServerExpression reference, String operation, ServerExpression region, String options, double weight) {
    return geospatialRegionQuery(reference, (operation == null) ? (XsStringVal) null : xs.string(operation), region, (options == null) ? (XsStringVal) null : xs.string(options), xs.doubleVal(weight));
  }

  
  @Override
  public CtsQueryExpr geospatialRegionQuery(ServerExpression reference, ServerExpression operation, ServerExpression region, ServerExpression options, ServerExpression weight) {
    if (operation == null) {
      throw new IllegalArgumentException("operation parameter for geospatialRegionQuery() cannot be null");
    }
    return new QueryCallImpl("cts", "geospatial-region-query", new Object[]{ reference, operation, region, options, weight });
  }

  
  @Override
  public CtsReferenceExpr iriReference() {
    return new ReferenceCallImpl("cts", "iri-reference", new Object[]{  });
  }

  
  @Override
  public CtsQueryExpr jsonPropertyChildGeospatialQuery(String propertyName, String childName, CtsRegionExpr... region) {
    return jsonPropertyChildGeospatialQuery((propertyName == null) ? (XsStringVal) null : xs.string(propertyName), (childName == null) ? (XsStringVal) null : xs.string(childName), new RegionSeqListImpl(region));
  }

  
  @Override
  public CtsQueryExpr jsonPropertyChildGeospatialQuery(ServerExpression propertyName, ServerExpression childName, ServerExpression region) {
    return new QueryCallImpl("cts", "json-property-child-geospatial-query", new Object[]{ propertyName, childName, region });
  }

  
  @Override
  public CtsQueryExpr jsonPropertyChildGeospatialQuery(String propertyName, String childName, ServerExpression region, String... options) {
    return jsonPropertyChildGeospatialQuery((propertyName == null) ? (XsStringVal) null : xs.string(propertyName), (childName == null) ? (XsStringVal) null : xs.string(childName), region, (options == null) ? (XsStringVal) null : xs.stringSeq(options));
  }

  
  @Override
  public CtsQueryExpr jsonPropertyChildGeospatialQuery(ServerExpression propertyName, ServerExpression childName, ServerExpression region, ServerExpression options) {
    return new QueryCallImpl("cts", "json-property-child-geospatial-query", new Object[]{ propertyName, childName, region, options });
  }

  
  @Override
  public CtsQueryExpr jsonPropertyChildGeospatialQuery(String propertyName, String childName, ServerExpression region, String options, double weight) {
    return jsonPropertyChildGeospatialQuery((propertyName == null) ? (XsStringVal) null : xs.string(propertyName), (childName == null) ? (XsStringVal) null : xs.string(childName), region, (options == null) ? (XsStringVal) null : xs.string(options), xs.doubleVal(weight));
  }

  
  @Override
  public CtsQueryExpr jsonPropertyChildGeospatialQuery(ServerExpression propertyName, ServerExpression childName, ServerExpression region, ServerExpression options, ServerExpression weight) {
    return new QueryCallImpl("cts", "json-property-child-geospatial-query", new Object[]{ propertyName, childName, region, options, weight });
  }

  
  @Override
  public CtsQueryExpr jsonPropertyGeospatialQuery(String propertyName, CtsRegionExpr... region) {
    return jsonPropertyGeospatialQuery((propertyName == null) ? (XsStringVal) null : xs.string(propertyName), new RegionSeqListImpl(region));
  }

  
  @Override
  public CtsQueryExpr jsonPropertyGeospatialQuery(ServerExpression propertyName, ServerExpression region) {
    return new QueryCallImpl("cts", "json-property-geospatial-query", new Object[]{ propertyName, region });
  }

  
  @Override
  public CtsQueryExpr jsonPropertyGeospatialQuery(String propertyName, ServerExpression region, String... options) {
    return jsonPropertyGeospatialQuery((propertyName == null) ? (XsStringVal) null : xs.string(propertyName), region, (options == null) ? (XsStringVal) null : xs.stringSeq(options));
  }

  
  @Override
  public CtsQueryExpr jsonPropertyGeospatialQuery(ServerExpression propertyName, ServerExpression region, ServerExpression options) {
    return new QueryCallImpl("cts", "json-property-geospatial-query", new Object[]{ propertyName, region, options });
  }

  
  @Override
  public CtsQueryExpr jsonPropertyGeospatialQuery(String propertyName, ServerExpression region, String options, double weight) {
    return jsonPropertyGeospatialQuery((propertyName == null) ? (XsStringVal) null : xs.string(propertyName), region, (options == null) ? (XsStringVal) null : xs.string(options), xs.doubleVal(weight));
  }

  
  @Override
  public CtsQueryExpr jsonPropertyGeospatialQuery(ServerExpression propertyName, ServerExpression region, ServerExpression options, ServerExpression weight) {
    return new QueryCallImpl("cts", "json-property-geospatial-query", new Object[]{ propertyName, region, options, weight });
  }

  
  @Override
  public CtsQueryExpr jsonPropertyPairGeospatialQuery(String propertyName, String latitudeName, String longitudeName, CtsRegionExpr... region) {
    return jsonPropertyPairGeospatialQuery((propertyName == null) ? (XsStringVal) null : xs.string(propertyName), (latitudeName == null) ? (XsStringVal) null : xs.string(latitudeName), (longitudeName == null) ? (XsStringVal) null : xs.string(longitudeName), new RegionSeqListImpl(region));
  }

  
  @Override
  public CtsQueryExpr jsonPropertyPairGeospatialQuery(ServerExpression propertyName, ServerExpression latitudeName, ServerExpression longitudeName, ServerExpression region) {
    return new QueryCallImpl("cts", "json-property-pair-geospatial-query", new Object[]{ propertyName, latitudeName, longitudeName, region });
  }

  
  @Override
  public CtsQueryExpr jsonPropertyPairGeospatialQuery(String propertyName, String latitudeName, String longitudeName, ServerExpression region, String... options) {
    return jsonPropertyPairGeospatialQuery((propertyName == null) ? (XsStringVal) null : xs.string(propertyName), (latitudeName == null) ? (XsStringVal) null : xs.string(latitudeName), (longitudeName == null) ? (XsStringVal) null : xs.string(longitudeName), region, (options == null) ? (XsStringVal) null : xs.stringSeq(options));
  }

  
  @Override
  public CtsQueryExpr jsonPropertyPairGeospatialQuery(ServerExpression propertyName, ServerExpression latitudeName, ServerExpression longitudeName, ServerExpression region, ServerExpression options) {
    return new QueryCallImpl("cts", "json-property-pair-geospatial-query", new Object[]{ propertyName, latitudeName, longitudeName, region, options });
  }

  
  @Override
  public CtsQueryExpr jsonPropertyPairGeospatialQuery(String propertyName, String latitudeName, String longitudeName, ServerExpression region, String options, double weight) {
    return jsonPropertyPairGeospatialQuery((propertyName == null) ? (XsStringVal) null : xs.string(propertyName), (latitudeName == null) ? (XsStringVal) null : xs.string(latitudeName), (longitudeName == null) ? (XsStringVal) null : xs.string(longitudeName), region, (options == null) ? (XsStringVal) null : xs.string(options), xs.doubleVal(weight));
  }

  
  @Override
  public CtsQueryExpr jsonPropertyPairGeospatialQuery(ServerExpression propertyName, ServerExpression latitudeName, ServerExpression longitudeName, ServerExpression region, ServerExpression options, ServerExpression weight) {
    return new QueryCallImpl("cts", "json-property-pair-geospatial-query", new Object[]{ propertyName, latitudeName, longitudeName, region, options, weight });
  }

  
  @Override
  public CtsQueryExpr jsonPropertyRangeQuery(String propertyName, String operator, String value) {
    return jsonPropertyRangeQuery((propertyName == null) ? (XsStringVal) null : xs.string(propertyName), (operator == null) ? (XsStringVal) null : xs.string(operator), (value == null) ? (XsAnyAtomicTypeVal) null : xs.string(value));
  }

  
  @Override
  public CtsQueryExpr jsonPropertyRangeQuery(ServerExpression propertyName, ServerExpression operator, ServerExpression value) {
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
  public CtsQueryExpr jsonPropertyRangeQuery(ServerExpression propertyName, ServerExpression operator, ServerExpression value, ServerExpression options) {
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
  public CtsQueryExpr jsonPropertyRangeQuery(ServerExpression propertyName, ServerExpression operator, ServerExpression value, ServerExpression options, ServerExpression weight) {
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
  public CtsReferenceExpr jsonPropertyReference(ServerExpression property) {
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
  public CtsReferenceExpr jsonPropertyReference(ServerExpression property, ServerExpression options) {
    if (property == null) {
      throw new IllegalArgumentException("property parameter for jsonPropertyReference() cannot be null");
    }
    return new ReferenceCallImpl("cts", "json-property-reference", new Object[]{ property, options });
  }

  
  @Override
  public CtsQueryExpr jsonPropertyScopeQuery(String propertyName, ServerExpression query) {
    return jsonPropertyScopeQuery((propertyName == null) ? (XsStringVal) null : xs.string(propertyName), query);
  }

  
  @Override
  public CtsQueryExpr jsonPropertyScopeQuery(ServerExpression propertyName, ServerExpression query) {
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
  public CtsQueryExpr jsonPropertyValueQuery(ServerExpression propertyName, ServerExpression value) {
    return new QueryCallImpl("cts", "json-property-value-query", new Object[]{ propertyName, value });
  }

  
  @Override
  public CtsQueryExpr jsonPropertyValueQuery(String propertyName, String value, String... options) {
    return jsonPropertyValueQuery((propertyName == null) ? (XsStringVal) null : xs.string(propertyName), (value == null) ? (XsAnyAtomicTypeVal) null : xs.string(value), (options == null) ? (XsStringVal) null : xs.stringSeq(options));
  }

  
  @Override
  public CtsQueryExpr jsonPropertyValueQuery(ServerExpression propertyName, ServerExpression value, ServerExpression options) {
    return new QueryCallImpl("cts", "json-property-value-query", new Object[]{ propertyName, value, options });
  }

  
  @Override
  public CtsQueryExpr jsonPropertyValueQuery(String propertyName, String value, String options, double weight) {
    return jsonPropertyValueQuery((propertyName == null) ? (XsStringVal) null : xs.string(propertyName), (value == null) ? (XsAnyAtomicTypeVal) null : xs.string(value), (options == null) ? (XsStringVal) null : xs.string(options), xs.doubleVal(weight));
  }

  
  @Override
  public CtsQueryExpr jsonPropertyValueQuery(ServerExpression propertyName, ServerExpression value, ServerExpression options, ServerExpression weight) {
    return new QueryCallImpl("cts", "json-property-value-query", new Object[]{ propertyName, value, options, weight });
  }

  
  @Override
  public CtsQueryExpr jsonPropertyWordQuery(String propertyName, String text) {
    return jsonPropertyWordQuery((propertyName == null) ? (XsStringVal) null : xs.string(propertyName), (text == null) ? (XsStringVal) null : xs.string(text));
  }

  
  @Override
  public CtsQueryExpr jsonPropertyWordQuery(ServerExpression propertyName, ServerExpression text) {
    return new QueryCallImpl("cts", "json-property-word-query", new Object[]{ propertyName, text });
  }

  
  @Override
  public CtsQueryExpr jsonPropertyWordQuery(String propertyName, String text, String... options) {
    return jsonPropertyWordQuery((propertyName == null) ? (XsStringVal) null : xs.string(propertyName), (text == null) ? (XsStringVal) null : xs.string(text), (options == null) ? (XsStringVal) null : xs.stringSeq(options));
  }

  
  @Override
  public CtsQueryExpr jsonPropertyWordQuery(ServerExpression propertyName, ServerExpression text, ServerExpression options) {
    return new QueryCallImpl("cts", "json-property-word-query", new Object[]{ propertyName, text, options });
  }

  
  @Override
  public CtsQueryExpr jsonPropertyWordQuery(String propertyName, String text, String options, double weight) {
    return jsonPropertyWordQuery((propertyName == null) ? (XsStringVal) null : xs.string(propertyName), (text == null) ? (XsStringVal) null : xs.string(text), (options == null) ? (XsStringVal) null : xs.string(options), xs.doubleVal(weight));
  }

  
  @Override
  public CtsQueryExpr jsonPropertyWordQuery(ServerExpression propertyName, ServerExpression text, ServerExpression options, ServerExpression weight) {
    return new QueryCallImpl("cts", "json-property-word-query", new Object[]{ propertyName, text, options, weight });
  }

  
  @Override
  public ServerExpression linestring(String vertices) {
    return linestring((vertices == null) ? (XsAnyAtomicTypeVal) null : xs.string(vertices));
  }

  
  @Override
  public ServerExpression linestring(ServerExpression vertices) {
    return new RegionCallImpl("cts", "linestring", new Object[]{ vertices });
  }

  
  @Override
  public CtsQueryExpr locksFragmentQuery(ServerExpression query) {
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
  public CtsQueryExpr lsqtQuery(ServerExpression temporalCollection) {
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
  public CtsQueryExpr lsqtQuery(ServerExpression temporalCollection, ServerExpression timestamp) {
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
  public CtsQueryExpr lsqtQuery(ServerExpression temporalCollection, ServerExpression timestamp, ServerExpression options) {
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
  public CtsQueryExpr lsqtQuery(ServerExpression temporalCollection, ServerExpression timestamp, ServerExpression options, ServerExpression weight) {
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
  public CtsQueryExpr nearQuery(ServerExpression queries) {
    return new QueryCallImpl("cts", "near-query", new Object[]{ queries });
  }

  
  @Override
  public CtsQueryExpr nearQuery(ServerExpression queries, double distance) {
    return nearQuery(queries, xs.doubleVal(distance));
  }

  
  @Override
  public CtsQueryExpr nearQuery(ServerExpression queries, XsDoubleVal distance) {
    return new QueryCallImpl("cts", "near-query", new Object[]{ queries, distance });
  }

  
  @Override
  public CtsQueryExpr nearQuery(ServerExpression queries, double distance, String... options) {
    return nearQuery(queries, xs.doubleVal(distance), (options == null) ? (XsStringVal) null : xs.stringSeq(options));
  }

  
  @Override
  public CtsQueryExpr nearQuery(ServerExpression queries, ServerExpression distance, ServerExpression options) {
    return new QueryCallImpl("cts", "near-query", new Object[]{ queries, distance, options });
  }

  
  @Override
  public CtsQueryExpr nearQuery(ServerExpression queries, double distance, String options, double weight) {
    return nearQuery(queries, xs.doubleVal(distance), (options == null) ? (XsStringVal) null : xs.string(options), xs.doubleVal(weight));
  }

  
  @Override
  public CtsQueryExpr nearQuery(ServerExpression queries, ServerExpression distance, ServerExpression options, ServerExpression weight) {
    return new QueryCallImpl("cts", "near-query", new Object[]{ queries, distance, options, weight });
  }

  
  @Override
  public CtsQueryExpr notInQuery(ServerExpression positiveQuery, ServerExpression negativeQuery) {
    if (positiveQuery == null) {
      throw new IllegalArgumentException("positiveQuery parameter for notInQuery() cannot be null");
    }
    if (negativeQuery == null) {
      throw new IllegalArgumentException("negativeQuery parameter for notInQuery() cannot be null");
    }
    return new QueryCallImpl("cts", "not-in-query", new Object[]{ positiveQuery, negativeQuery });
  }

  
  @Override
  public CtsQueryExpr notQuery(ServerExpression query) {
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
  public CtsQueryExpr orQuery(ServerExpression queries) {
    return new QueryCallImpl("cts", "or-query", new Object[]{ queries });
  }

  
  @Override
  public CtsQueryExpr orQuery(ServerExpression queries, String options) {
    return orQuery(queries, (options == null) ? (XsStringVal) null : xs.string(options));
  }

  
  @Override
  public CtsQueryExpr orQuery(ServerExpression queries, XsStringSeqVal options) {
    return new QueryCallImpl("cts", "or-query", new Object[]{ queries, options });
  }

  
  @Override
  public ServerExpression partOfSpeech(ServerExpression token) {
    if (token == null) {
      throw new IllegalArgumentException("token parameter for partOfSpeech() cannot be null");
    }
    return new XsExprImpl.StringCallImpl("cts", "part-of-speech", new Object[]{ token });
  }

  
  @Override
  public CtsQueryExpr pathGeospatialQuery(String pathExpression, CtsRegionExpr... region) {
    return pathGeospatialQuery((pathExpression == null) ? (XsStringVal) null : xs.string(pathExpression), new RegionSeqListImpl(region));
  }

  
  @Override
  public CtsQueryExpr pathGeospatialQuery(ServerExpression pathExpression, ServerExpression region) {
    return new QueryCallImpl("cts", "path-geospatial-query", new Object[]{ pathExpression, region });
  }

  
  @Override
  public CtsQueryExpr pathGeospatialQuery(String pathExpression, ServerExpression region, String... options) {
    return pathGeospatialQuery((pathExpression == null) ? (XsStringVal) null : xs.string(pathExpression), region, (options == null) ? (XsStringVal) null : xs.stringSeq(options));
  }

  
  @Override
  public CtsQueryExpr pathGeospatialQuery(ServerExpression pathExpression, ServerExpression region, ServerExpression options) {
    return new QueryCallImpl("cts", "path-geospatial-query", new Object[]{ pathExpression, region, options });
  }

  
  @Override
  public CtsQueryExpr pathGeospatialQuery(String pathExpression, ServerExpression region, String options, double weight) {
    return pathGeospatialQuery((pathExpression == null) ? (XsStringVal) null : xs.string(pathExpression), region, (options == null) ? (XsStringVal) null : xs.string(options), xs.doubleVal(weight));
  }

  
  @Override
  public CtsQueryExpr pathGeospatialQuery(ServerExpression pathExpression, ServerExpression region, ServerExpression options, ServerExpression weight) {
    return new QueryCallImpl("cts", "path-geospatial-query", new Object[]{ pathExpression, region, options, weight });
  }

  
  @Override
  public CtsQueryExpr pathRangeQuery(String pathName, String operator, String value) {
    return pathRangeQuery((pathName == null) ? (XsStringVal) null : xs.string(pathName), (operator == null) ? (XsStringVal) null : xs.string(operator), (value == null) ? (XsAnyAtomicTypeVal) null : xs.string(value));
  }

  
  @Override
  public CtsQueryExpr pathRangeQuery(ServerExpression pathName, ServerExpression operator, ServerExpression value) {
    if (operator == null) {
      throw new IllegalArgumentException("operator parameter for pathRangeQuery() cannot be null");
    }
    return new QueryCallImpl("cts", "path-range-query", new Object[]{ pathName, operator, value });
  }

  
  @Override
  public CtsQueryExpr pathRangeQuery(String pathName, String operator, String value, String... options) {
    return pathRangeQuery((pathName == null) ? (XsStringVal) null : xs.string(pathName), (operator == null) ? (XsStringVal) null : xs.string(operator), (value == null) ? (XsAnyAtomicTypeVal) null : xs.string(value), (options == null) ? (XsStringVal) null : xs.stringSeq(options));
  }

  
  @Override
  public CtsQueryExpr pathRangeQuery(ServerExpression pathName, ServerExpression operator, ServerExpression value, ServerExpression options) {
    if (operator == null) {
      throw new IllegalArgumentException("operator parameter for pathRangeQuery() cannot be null");
    }
    return new QueryCallImpl("cts", "path-range-query", new Object[]{ pathName, operator, value, options });
  }

  
  @Override
  public CtsQueryExpr pathRangeQuery(String pathName, String operator, String value, String options, double weight) {
    return pathRangeQuery((pathName == null) ? (XsStringVal) null : xs.string(pathName), (operator == null) ? (XsStringVal) null : xs.string(operator), (value == null) ? (XsAnyAtomicTypeVal) null : xs.string(value), (options == null) ? (XsStringVal) null : xs.string(options), xs.doubleVal(weight));
  }

  
  @Override
  public CtsQueryExpr pathRangeQuery(ServerExpression pathName, ServerExpression operator, ServerExpression value, ServerExpression options, ServerExpression weight) {
    if (operator == null) {
      throw new IllegalArgumentException("operator parameter for pathRangeQuery() cannot be null");
    }
    return new QueryCallImpl("cts", "path-range-query", new Object[]{ pathName, operator, value, options, weight });
  }

  
  @Override
  public CtsReferenceExpr pathReference(String pathExpression) {
    return pathReference((pathExpression == null) ? (XsStringVal) null : xs.string(pathExpression));
  }

  
  @Override
  public CtsReferenceExpr pathReference(ServerExpression pathExpression) {
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
  public CtsReferenceExpr pathReference(ServerExpression pathExpression, ServerExpression options) {
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
  public CtsReferenceExpr pathReference(ServerExpression pathExpression, ServerExpression options, ServerExpression map) {
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
  public CtsPeriodExpr period(ServerExpression start, ServerExpression end) {
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
  public CtsQueryExpr periodCompareQuery(ServerExpression axis1, ServerExpression operator, ServerExpression axis2) {
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
  public CtsQueryExpr periodCompareQuery(ServerExpression axis1, ServerExpression operator, ServerExpression axis2, ServerExpression options) {
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
  public CtsQueryExpr periodRangeQuery(String axis, String operator) {
    return periodRangeQuery((axis == null) ? (XsStringVal) null : xs.string(axis), (operator == null) ? (XsStringVal) null : xs.string(operator));
  }

  
  @Override
  public CtsQueryExpr periodRangeQuery(ServerExpression axis, ServerExpression operator) {
    if (operator == null) {
      throw new IllegalArgumentException("operator parameter for periodRangeQuery() cannot be null");
    }
    return new QueryCallImpl("cts", "period-range-query", new Object[]{ axis, operator });
  }

  
  @Override
  public CtsQueryExpr periodRangeQuery(String axis, String operator, CtsPeriodExpr... period) {
    return periodRangeQuery((axis == null) ? (XsStringVal) null : xs.string(axis), (operator == null) ? (XsStringVal) null : xs.string(operator), new PeriodSeqListImpl(period));
  }

  
  @Override
  public CtsQueryExpr periodRangeQuery(ServerExpression axis, ServerExpression operator, ServerExpression period) {
    if (operator == null) {
      throw new IllegalArgumentException("operator parameter for periodRangeQuery() cannot be null");
    }
    return new QueryCallImpl("cts", "period-range-query", new Object[]{ axis, operator, period });
  }

  
  @Override
  public CtsQueryExpr periodRangeQuery(String axis, String operator, ServerExpression period, String options) {
    return periodRangeQuery((axis == null) ? (XsStringVal) null : xs.string(axis), (operator == null) ? (XsStringVal) null : xs.string(operator), period, (options == null) ? (XsStringVal) null : xs.string(options));
  }

  
  @Override
  public CtsQueryExpr periodRangeQuery(ServerExpression axis, ServerExpression operator, ServerExpression period, ServerExpression options) {
    if (operator == null) {
      throw new IllegalArgumentException("operator parameter for periodRangeQuery() cannot be null");
    }
    return new QueryCallImpl("cts", "period-range-query", new Object[]{ axis, operator, period, options });
  }

  
  @Override
  public CtsPointExpr point(double latitude, double longitude) {
    return point(xs.doubleVal(latitude), xs.doubleVal(longitude));
  }

  
  @Override
  public CtsPointExpr point(ServerExpression latitude, ServerExpression longitude) {
    if (latitude == null) {
      throw new IllegalArgumentException("latitude parameter for point() cannot be null");
    }
    if (longitude == null) {
      throw new IllegalArgumentException("longitude parameter for point() cannot be null");
    }
    return new PointCallImpl("cts", "point", new Object[]{ latitude, longitude });
  }

  
  @Override
  public ServerExpression pointLatitude(ServerExpression point) {
    if (point == null) {
      throw new IllegalArgumentException("point parameter for pointLatitude() cannot be null");
    }
    return new XsExprImpl.NumericCallImpl("cts", "point-latitude", new Object[]{ point });
  }

  
  @Override
  public ServerExpression pointLongitude(ServerExpression point) {
    if (point == null) {
      throw new IllegalArgumentException("point parameter for pointLongitude() cannot be null");
    }
    return new XsExprImpl.NumericCallImpl("cts", "point-longitude", new Object[]{ point });
  }

  
  @Override
  public CtsPolygonExpr polygon(ServerExpression vertices) {
    return new PolygonCallImpl("cts", "polygon", new Object[]{ vertices });
  }

  
  @Override
  public CtsQueryExpr propertiesFragmentQuery(ServerExpression query) {
    if (query == null) {
      throw new IllegalArgumentException("query parameter for propertiesFragmentQuery() cannot be null");
    }
    return new QueryCallImpl("cts", "properties-fragment-query", new Object[]{ query });
  }

  
  @Override
  public CtsQueryExpr rangeQuery(ServerExpression index, String operator, String value) {
    return rangeQuery(index, (operator == null) ? (XsStringVal) null : xs.string(operator), (value == null) ? (XsAnyAtomicTypeVal) null : xs.string(value));
  }

  
  @Override
  public CtsQueryExpr rangeQuery(ServerExpression index, ServerExpression operator, ServerExpression value) {
    if (operator == null) {
      throw new IllegalArgumentException("operator parameter for rangeQuery() cannot be null");
    }
    return new QueryCallImpl("cts", "range-query", new Object[]{ index, operator, value });
  }

  
  @Override
  public CtsQueryExpr rangeQuery(ServerExpression index, String operator, String value, String... options) {
    return rangeQuery(index, (operator == null) ? (XsStringVal) null : xs.string(operator), (value == null) ? (XsAnyAtomicTypeVal) null : xs.string(value), (options == null) ? (XsStringVal) null : xs.stringSeq(options));
  }

  
  @Override
  public CtsQueryExpr rangeQuery(ServerExpression index, ServerExpression operator, ServerExpression value, ServerExpression options) {
    if (operator == null) {
      throw new IllegalArgumentException("operator parameter for rangeQuery() cannot be null");
    }
    return new QueryCallImpl("cts", "range-query", new Object[]{ index, operator, value, options });
  }

  
  @Override
  public CtsQueryExpr rangeQuery(ServerExpression index, String operator, String value, String options, double weight) {
    return rangeQuery(index, (operator == null) ? (XsStringVal) null : xs.string(operator), (value == null) ? (XsAnyAtomicTypeVal) null : xs.string(value), (options == null) ? (XsStringVal) null : xs.string(options), xs.doubleVal(weight));
  }

  
  @Override
  public CtsQueryExpr rangeQuery(ServerExpression index, ServerExpression operator, ServerExpression value, ServerExpression options, ServerExpression weight) {
    if (operator == null) {
      throw new IllegalArgumentException("operator parameter for rangeQuery() cannot be null");
    }
    return new QueryCallImpl("cts", "range-query", new Object[]{ index, operator, value, options, weight });
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
  public CtsQueryExpr tripleRangeQuery(ServerExpression subject, ServerExpression predicate, ServerExpression object) {
    return new QueryCallImpl("cts", "triple-range-query", new Object[]{ subject, predicate, object });
  }

  
  @Override
  public CtsQueryExpr tripleRangeQuery(String subject, String predicate, String object, String operator) {
    return tripleRangeQuery((subject == null) ? (XsAnyAtomicTypeVal) null : xs.string(subject), (predicate == null) ? (XsAnyAtomicTypeVal) null : xs.string(predicate), (object == null) ? (XsAnyAtomicTypeVal) null : xs.string(object), (operator == null) ? (XsStringVal) null : xs.string(operator));
  }

  
  @Override
  public CtsQueryExpr tripleRangeQuery(ServerExpression subject, ServerExpression predicate, ServerExpression object, ServerExpression operator) {
    return new QueryCallImpl("cts", "triple-range-query", new Object[]{ subject, predicate, object, operator });
  }

  
  @Override
  public CtsQueryExpr tripleRangeQuery(String subject, String predicate, String object, String operator, String... options) {
    return tripleRangeQuery((subject == null) ? (XsAnyAtomicTypeVal) null : xs.string(subject), (predicate == null) ? (XsAnyAtomicTypeVal) null : xs.string(predicate), (object == null) ? (XsAnyAtomicTypeVal) null : xs.string(object), (operator == null) ? (XsStringVal) null : xs.string(operator), (options == null) ? (XsStringVal) null : xs.stringSeq(options));
  }

  
  @Override
  public CtsQueryExpr tripleRangeQuery(ServerExpression subject, ServerExpression predicate, ServerExpression object, ServerExpression operator, ServerExpression options) {
    return new QueryCallImpl("cts", "triple-range-query", new Object[]{ subject, predicate, object, operator, options });
  }

  
  @Override
  public CtsQueryExpr tripleRangeQuery(String subject, String predicate, String object, String operator, String options, double weight) {
    return tripleRangeQuery((subject == null) ? (XsAnyAtomicTypeVal) null : xs.string(subject), (predicate == null) ? (XsAnyAtomicTypeVal) null : xs.string(predicate), (object == null) ? (XsAnyAtomicTypeVal) null : xs.string(object), (operator == null) ? (XsStringVal) null : xs.string(operator), (options == null) ? (XsStringVal) null : xs.string(options), xs.doubleVal(weight));
  }

  
  @Override
  public CtsQueryExpr tripleRangeQuery(ServerExpression subject, ServerExpression predicate, ServerExpression object, ServerExpression operator, ServerExpression options, ServerExpression weight) {
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
  public CtsQueryExpr wordQuery(ServerExpression text) {
    return new QueryCallImpl("cts", "word-query", new Object[]{ text });
  }

  
  @Override
  public CtsQueryExpr wordQuery(String text, String... options) {
    return wordQuery((text == null) ? (XsStringVal) null : xs.string(text), (options == null) ? (XsStringVal) null : xs.stringSeq(options));
  }

  
  @Override
  public CtsQueryExpr wordQuery(ServerExpression text, ServerExpression options) {
    return new QueryCallImpl("cts", "word-query", new Object[]{ text, options });
  }

  
  @Override
  public CtsQueryExpr wordQuery(String text, String options, double weight) {
    return wordQuery((text == null) ? (XsStringVal) null : xs.string(text), (options == null) ? (XsStringVal) null : xs.string(options), xs.doubleVal(weight));
  }

  
  @Override
  public CtsQueryExpr wordQuery(ServerExpression text, ServerExpression options, ServerExpression weight) {
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
