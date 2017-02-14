/*
 * Copyright 2016-2017 MarkLogic Corporation
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

import com.marklogic.client.type.MapMapExpr;
import com.marklogic.client.type.XsAnyAtomicTypeExpr;
import com.marklogic.client.type.XsAnyAtomicTypeSeqExpr;
import com.marklogic.client.type.XsDateTimeExpr;
import com.marklogic.client.type.XsDoubleExpr;
import com.marklogic.client.type.XsQNameExpr;
import com.marklogic.client.type.XsQNameSeqExpr;
import com.marklogic.client.type.XsStringExpr;
import com.marklogic.client.type.XsStringSeqExpr;

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
        return andQuery(queries, (options == null) ? (XsStringExpr) null : xs.string(options));
    }

    
    @Override
    public CtsQueryExpr andQuery(CtsQuerySeqExpr queries, XsStringSeqExpr options) {
        return new QueryCallImpl("cts", "and-query", new Object[]{ queries, options });
    }

    
    @Override
    public CtsQueryExpr boostQuery(CtsQueryExpr matchingQuery, CtsQueryExpr boostingQuery) {
        return new QueryCallImpl("cts", "boost-query", new Object[]{ matchingQuery, boostingQuery });
    }

    
    @Override
    public CtsBoxExpr box(XsDoubleExpr south, double west, double north, double east) {
        return box(south, xs.doubleVal(west), xs.doubleVal(north), xs.doubleVal(east));
    }

    
    @Override
    public CtsBoxExpr box(XsDoubleExpr south, XsDoubleExpr west, XsDoubleExpr north, XsDoubleExpr east) {
        return new BoxCallImpl("cts", "box", new Object[]{ south, west, north, east });
    }

    
    @Override
    public CtsCircleExpr circle(XsDoubleExpr radius, CtsPointExpr center) {
        return new CircleCallImpl("cts", "circle", new Object[]{ radius, center });
    }

    
    @Override
    public CtsQueryExpr collectionQuery(String uris) {
        return collectionQuery((uris == null) ? (XsStringExpr) null : xs.string(uris));
    }

    
    @Override
    public CtsQueryExpr collectionQuery(XsStringSeqExpr uris) {
        return new QueryCallImpl("cts", "collection-query", new Object[]{ uris });
    }

    
    @Override
    public CtsReferenceExpr collectionReference() {
        return new ReferenceCallImpl("cts", "collection-reference", new Object[]{  });
    }

    
    @Override
    public CtsReferenceExpr collectionReference(String options) {
        return collectionReference((options == null) ? (XsStringExpr) null : xs.string(options));
    }

    
    @Override
    public CtsReferenceExpr collectionReference(XsStringSeqExpr options) {
        return new ReferenceCallImpl("cts", "collection-reference", new Object[]{ options });
    }

    
    @Override
    public CtsQueryExpr directoryQuery(String uris) {
        return directoryQuery((uris == null) ? (XsStringExpr) null : xs.string(uris));
    }

    
    @Override
    public CtsQueryExpr directoryQuery(XsStringSeqExpr uris) {
        return new QueryCallImpl("cts", "directory-query", new Object[]{ uris });
    }

    
    @Override
    public CtsQueryExpr directoryQuery(String uris, String depth) {
        return directoryQuery((uris == null) ? (XsStringExpr) null : xs.string(uris), (depth == null) ? (XsStringExpr) null : xs.string(depth));
    }

    
    @Override
    public CtsQueryExpr directoryQuery(XsStringSeqExpr uris, XsStringExpr depth) {
        return new QueryCallImpl("cts", "directory-query", new Object[]{ uris, depth });
    }

    
    @Override
    public CtsQueryExpr documentFragmentQuery(CtsQueryExpr query) {
        return new QueryCallImpl("cts", "document-fragment-query", new Object[]{ query });
    }

    
    @Override
    public CtsQueryExpr documentQuery(String uris) {
        return documentQuery((uris == null) ? (XsStringExpr) null : xs.string(uris));
    }

    
    @Override
    public CtsQueryExpr documentQuery(XsStringSeqExpr uris) {
        return new QueryCallImpl("cts", "document-query", new Object[]{ uris });
    }

    
    @Override
    public CtsQueryExpr elementAttributePairGeospatialQuery(String elementName, String latitudeAttributeNames, String longitudeAttributeNames, CtsRegionExpr... regions) {
        return elementAttributePairGeospatialQuery((elementName == null) ? (XsQNameExpr) null : xs.QName(elementName), (latitudeAttributeNames == null) ? (XsQNameExpr) null : xs.QName(latitudeAttributeNames), (longitudeAttributeNames == null) ? (XsQNameExpr) null : xs.QName(longitudeAttributeNames), new RegionSeqListImpl(regions));
    }

    
    @Override
    public CtsQueryExpr elementAttributePairGeospatialQuery(XsQNameSeqExpr elementName, XsQNameSeqExpr latitudeAttributeNames, XsQNameSeqExpr longitudeAttributeNames, CtsRegionSeqExpr regions) {
        return new QueryCallImpl("cts", "element-attribute-pair-geospatial-query", new Object[]{ elementName, latitudeAttributeNames, longitudeAttributeNames, regions });
    }

    
    @Override
    public CtsQueryExpr elementAttributePairGeospatialQuery(String elementName, String latitudeAttributeNames, String longitudeAttributeNames, CtsRegionSeqExpr regions, String... options) {
        return elementAttributePairGeospatialQuery((elementName == null) ? (XsQNameExpr) null : xs.QName(elementName), (latitudeAttributeNames == null) ? (XsQNameExpr) null : xs.QName(latitudeAttributeNames), (longitudeAttributeNames == null) ? (XsQNameExpr) null : xs.QName(longitudeAttributeNames), regions, (options == null) ? (XsStringExpr) null : xs.stringSeq(options));
    }

    
    @Override
    public CtsQueryExpr elementAttributePairGeospatialQuery(XsQNameSeqExpr elementName, XsQNameSeqExpr latitudeAttributeNames, XsQNameSeqExpr longitudeAttributeNames, CtsRegionSeqExpr regions, XsStringSeqExpr options) {
        return new QueryCallImpl("cts", "element-attribute-pair-geospatial-query", new Object[]{ elementName, latitudeAttributeNames, longitudeAttributeNames, regions, options });
    }

    
    @Override
    public CtsQueryExpr elementAttributePairGeospatialQuery(String elementName, String latitudeAttributeNames, String longitudeAttributeNames, CtsRegionSeqExpr regions, String options, double weight) {
        return elementAttributePairGeospatialQuery((elementName == null) ? (XsQNameExpr) null : xs.QName(elementName), (latitudeAttributeNames == null) ? (XsQNameExpr) null : xs.QName(latitudeAttributeNames), (longitudeAttributeNames == null) ? (XsQNameExpr) null : xs.QName(longitudeAttributeNames), regions, (options == null) ? (XsStringExpr) null : xs.string(options), xs.doubleVal(weight));
    }

    
    @Override
    public CtsQueryExpr elementAttributePairGeospatialQuery(XsQNameSeqExpr elementName, XsQNameSeqExpr latitudeAttributeNames, XsQNameSeqExpr longitudeAttributeNames, CtsRegionSeqExpr regions, XsStringSeqExpr options, XsDoubleExpr weight) {
        return new QueryCallImpl("cts", "element-attribute-pair-geospatial-query", new Object[]{ elementName, latitudeAttributeNames, longitudeAttributeNames, regions, options, weight });
    }

    
    @Override
    public CtsQueryExpr elementAttributeRangeQuery(String elementName, String attributeName, String operator, String value) {
        return elementAttributeRangeQuery((elementName == null) ? (XsQNameExpr) null : xs.QName(elementName), (attributeName == null) ? (XsQNameExpr) null : xs.QName(attributeName), (operator == null) ? (XsStringExpr) null : xs.string(operator), (value == null) ? (XsAnyAtomicTypeExpr) null : xs.string(value));
    }

    
    @Override
    public CtsQueryExpr elementAttributeRangeQuery(XsQNameSeqExpr elementName, XsQNameSeqExpr attributeName, XsStringExpr operator, XsAnyAtomicTypeSeqExpr value) {
        return new QueryCallImpl("cts", "element-attribute-range-query", new Object[]{ elementName, attributeName, operator, value });
    }

    
    @Override
    public CtsQueryExpr elementAttributeRangeQuery(String elementName, String attributeName, String operator, String value, String... options) {
        return elementAttributeRangeQuery((elementName == null) ? (XsQNameExpr) null : xs.QName(elementName), (attributeName == null) ? (XsQNameExpr) null : xs.QName(attributeName), (operator == null) ? (XsStringExpr) null : xs.string(operator), (value == null) ? (XsAnyAtomicTypeExpr) null : xs.string(value), (options == null) ? (XsStringExpr) null : xs.stringSeq(options));
    }

    
    @Override
    public CtsQueryExpr elementAttributeRangeQuery(XsQNameSeqExpr elementName, XsQNameSeqExpr attributeName, XsStringExpr operator, XsAnyAtomicTypeSeqExpr value, XsStringSeqExpr options) {
        return new QueryCallImpl("cts", "element-attribute-range-query", new Object[]{ elementName, attributeName, operator, value, options });
    }

    
    @Override
    public CtsQueryExpr elementAttributeRangeQuery(String elementName, String attributeName, String operator, String value, String options, double weight) {
        return elementAttributeRangeQuery((elementName == null) ? (XsQNameExpr) null : xs.QName(elementName), (attributeName == null) ? (XsQNameExpr) null : xs.QName(attributeName), (operator == null) ? (XsStringExpr) null : xs.string(operator), (value == null) ? (XsAnyAtomicTypeExpr) null : xs.string(value), (options == null) ? (XsStringExpr) null : xs.string(options), xs.doubleVal(weight));
    }

    
    @Override
    public CtsQueryExpr elementAttributeRangeQuery(XsQNameSeqExpr elementName, XsQNameSeqExpr attributeName, XsStringExpr operator, XsAnyAtomicTypeSeqExpr value, XsStringSeqExpr options, XsDoubleExpr weight) {
        return new QueryCallImpl("cts", "element-attribute-range-query", new Object[]{ elementName, attributeName, operator, value, options, weight });
    }

    
    @Override
    public CtsReferenceExpr elementAttributeReference(String element, String attribute) {
        return elementAttributeReference((element == null) ? (XsQNameExpr) null : xs.QName(element), (attribute == null) ? (XsQNameExpr) null : xs.QName(attribute));
    }

    
    @Override
    public CtsReferenceExpr elementAttributeReference(XsQNameExpr element, XsQNameExpr attribute) {
        return new ReferenceCallImpl("cts", "element-attribute-reference", new Object[]{ element, attribute });
    }

    
    @Override
    public CtsReferenceExpr elementAttributeReference(String element, String attribute, String options) {
        return elementAttributeReference((element == null) ? (XsQNameExpr) null : xs.QName(element), (attribute == null) ? (XsQNameExpr) null : xs.QName(attribute), (options == null) ? (XsStringExpr) null : xs.string(options));
    }

    
    @Override
    public CtsReferenceExpr elementAttributeReference(XsQNameExpr element, XsQNameExpr attribute, XsStringSeqExpr options) {
        return new ReferenceCallImpl("cts", "element-attribute-reference", new Object[]{ element, attribute, options });
    }

    
    @Override
    public CtsQueryExpr elementAttributeValueQuery(String elementName, String attributeName, String text) {
        return elementAttributeValueQuery((elementName == null) ? (XsQNameExpr) null : xs.QName(elementName), (attributeName == null) ? (XsQNameExpr) null : xs.QName(attributeName), (text == null) ? (XsStringExpr) null : xs.string(text));
    }

    
    @Override
    public CtsQueryExpr elementAttributeValueQuery(XsQNameSeqExpr elementName, XsQNameSeqExpr attributeName, XsStringSeqExpr text) {
        return new QueryCallImpl("cts", "element-attribute-value-query", new Object[]{ elementName, attributeName, text });
    }

    
    @Override
    public CtsQueryExpr elementAttributeValueQuery(String elementName, String attributeName, String text, String... options) {
        return elementAttributeValueQuery((elementName == null) ? (XsQNameExpr) null : xs.QName(elementName), (attributeName == null) ? (XsQNameExpr) null : xs.QName(attributeName), (text == null) ? (XsStringExpr) null : xs.string(text), (options == null) ? (XsStringExpr) null : xs.stringSeq(options));
    }

    
    @Override
    public CtsQueryExpr elementAttributeValueQuery(XsQNameSeqExpr elementName, XsQNameSeqExpr attributeName, XsStringSeqExpr text, XsStringSeqExpr options) {
        return new QueryCallImpl("cts", "element-attribute-value-query", new Object[]{ elementName, attributeName, text, options });
    }

    
    @Override
    public CtsQueryExpr elementAttributeValueQuery(String elementName, String attributeName, String text, String options, double weight) {
        return elementAttributeValueQuery((elementName == null) ? (XsQNameExpr) null : xs.QName(elementName), (attributeName == null) ? (XsQNameExpr) null : xs.QName(attributeName), (text == null) ? (XsStringExpr) null : xs.string(text), (options == null) ? (XsStringExpr) null : xs.string(options), xs.doubleVal(weight));
    }

    
    @Override
    public CtsQueryExpr elementAttributeValueQuery(XsQNameSeqExpr elementName, XsQNameSeqExpr attributeName, XsStringSeqExpr text, XsStringSeqExpr options, XsDoubleExpr weight) {
        return new QueryCallImpl("cts", "element-attribute-value-query", new Object[]{ elementName, attributeName, text, options, weight });
    }

    
    @Override
    public CtsQueryExpr elementAttributeWordQuery(String elementName, String attributeName, String text) {
        return elementAttributeWordQuery((elementName == null) ? (XsQNameExpr) null : xs.QName(elementName), (attributeName == null) ? (XsQNameExpr) null : xs.QName(attributeName), (text == null) ? (XsStringExpr) null : xs.string(text));
    }

    
    @Override
    public CtsQueryExpr elementAttributeWordQuery(XsQNameSeqExpr elementName, XsQNameSeqExpr attributeName, XsStringSeqExpr text) {
        return new QueryCallImpl("cts", "element-attribute-word-query", new Object[]{ elementName, attributeName, text });
    }

    
    @Override
    public CtsQueryExpr elementAttributeWordQuery(String elementName, String attributeName, String text, String... options) {
        return elementAttributeWordQuery((elementName == null) ? (XsQNameExpr) null : xs.QName(elementName), (attributeName == null) ? (XsQNameExpr) null : xs.QName(attributeName), (text == null) ? (XsStringExpr) null : xs.string(text), (options == null) ? (XsStringExpr) null : xs.stringSeq(options));
    }

    
    @Override
    public CtsQueryExpr elementAttributeWordQuery(XsQNameSeqExpr elementName, XsQNameSeqExpr attributeName, XsStringSeqExpr text, XsStringSeqExpr options) {
        return new QueryCallImpl("cts", "element-attribute-word-query", new Object[]{ elementName, attributeName, text, options });
    }

    
    @Override
    public CtsQueryExpr elementAttributeWordQuery(String elementName, String attributeName, String text, String options, double weight) {
        return elementAttributeWordQuery((elementName == null) ? (XsQNameExpr) null : xs.QName(elementName), (attributeName == null) ? (XsQNameExpr) null : xs.QName(attributeName), (text == null) ? (XsStringExpr) null : xs.string(text), (options == null) ? (XsStringExpr) null : xs.string(options), xs.doubleVal(weight));
    }

    
    @Override
    public CtsQueryExpr elementAttributeWordQuery(XsQNameSeqExpr elementName, XsQNameSeqExpr attributeName, XsStringSeqExpr text, XsStringSeqExpr options, XsDoubleExpr weight) {
        return new QueryCallImpl("cts", "element-attribute-word-query", new Object[]{ elementName, attributeName, text, options, weight });
    }

    
    @Override
    public CtsQueryExpr elementChildGeospatialQuery(String parentElementName, String childElementNames, CtsRegionExpr... regions) {
        return elementChildGeospatialQuery((parentElementName == null) ? (XsQNameExpr) null : xs.QName(parentElementName), (childElementNames == null) ? (XsQNameExpr) null : xs.QName(childElementNames), new RegionSeqListImpl(regions));
    }

    
    @Override
    public CtsQueryExpr elementChildGeospatialQuery(XsQNameSeqExpr parentElementName, XsQNameSeqExpr childElementNames, CtsRegionSeqExpr regions) {
        return new QueryCallImpl("cts", "element-child-geospatial-query", new Object[]{ parentElementName, childElementNames, regions });
    }

    
    @Override
    public CtsQueryExpr elementChildGeospatialQuery(String parentElementName, String childElementNames, CtsRegionSeqExpr regions, String... options) {
        return elementChildGeospatialQuery((parentElementName == null) ? (XsQNameExpr) null : xs.QName(parentElementName), (childElementNames == null) ? (XsQNameExpr) null : xs.QName(childElementNames), regions, (options == null) ? (XsStringExpr) null : xs.stringSeq(options));
    }

    
    @Override
    public CtsQueryExpr elementChildGeospatialQuery(XsQNameSeqExpr parentElementName, XsQNameSeqExpr childElementNames, CtsRegionSeqExpr regions, XsStringSeqExpr options) {
        return new QueryCallImpl("cts", "element-child-geospatial-query", new Object[]{ parentElementName, childElementNames, regions, options });
    }

    
    @Override
    public CtsQueryExpr elementChildGeospatialQuery(String parentElementName, String childElementNames, CtsRegionSeqExpr regions, String options, double weight) {
        return elementChildGeospatialQuery((parentElementName == null) ? (XsQNameExpr) null : xs.QName(parentElementName), (childElementNames == null) ? (XsQNameExpr) null : xs.QName(childElementNames), regions, (options == null) ? (XsStringExpr) null : xs.string(options), xs.doubleVal(weight));
    }

    
    @Override
    public CtsQueryExpr elementChildGeospatialQuery(XsQNameSeqExpr parentElementName, XsQNameSeqExpr childElementNames, CtsRegionSeqExpr regions, XsStringSeqExpr options, XsDoubleExpr weight) {
        return new QueryCallImpl("cts", "element-child-geospatial-query", new Object[]{ parentElementName, childElementNames, regions, options, weight });
    }

    
    @Override
    public CtsQueryExpr elementGeospatialQuery(String elementName, CtsRegionExpr... regions) {
        return elementGeospatialQuery((elementName == null) ? (XsQNameExpr) null : xs.QName(elementName), new RegionSeqListImpl(regions));
    }

    
    @Override
    public CtsQueryExpr elementGeospatialQuery(XsQNameSeqExpr elementName, CtsRegionSeqExpr regions) {
        return new QueryCallImpl("cts", "element-geospatial-query", new Object[]{ elementName, regions });
    }

    
    @Override
    public CtsQueryExpr elementGeospatialQuery(String elementName, CtsRegionSeqExpr regions, String... options) {
        return elementGeospatialQuery((elementName == null) ? (XsQNameExpr) null : xs.QName(elementName), regions, (options == null) ? (XsStringExpr) null : xs.stringSeq(options));
    }

    
    @Override
    public CtsQueryExpr elementGeospatialQuery(XsQNameSeqExpr elementName, CtsRegionSeqExpr regions, XsStringSeqExpr options) {
        return new QueryCallImpl("cts", "element-geospatial-query", new Object[]{ elementName, regions, options });
    }

    
    @Override
    public CtsQueryExpr elementGeospatialQuery(String elementName, CtsRegionSeqExpr regions, String options, double weight) {
        return elementGeospatialQuery((elementName == null) ? (XsQNameExpr) null : xs.QName(elementName), regions, (options == null) ? (XsStringExpr) null : xs.string(options), xs.doubleVal(weight));
    }

    
    @Override
    public CtsQueryExpr elementGeospatialQuery(XsQNameSeqExpr elementName, CtsRegionSeqExpr regions, XsStringSeqExpr options, XsDoubleExpr weight) {
        return new QueryCallImpl("cts", "element-geospatial-query", new Object[]{ elementName, regions, options, weight });
    }

    
    @Override
    public CtsQueryExpr elementPairGeospatialQuery(String elementName, String latitudeElementNames, String longitudeElementNames, CtsRegionExpr... regions) {
        return elementPairGeospatialQuery((elementName == null) ? (XsQNameExpr) null : xs.QName(elementName), (latitudeElementNames == null) ? (XsQNameExpr) null : xs.QName(latitudeElementNames), (longitudeElementNames == null) ? (XsQNameExpr) null : xs.QName(longitudeElementNames), new RegionSeqListImpl(regions));
    }

    
    @Override
    public CtsQueryExpr elementPairGeospatialQuery(XsQNameSeqExpr elementName, XsQNameSeqExpr latitudeElementNames, XsQNameSeqExpr longitudeElementNames, CtsRegionSeqExpr regions) {
        return new QueryCallImpl("cts", "element-pair-geospatial-query", new Object[]{ elementName, latitudeElementNames, longitudeElementNames, regions });
    }

    
    @Override
    public CtsQueryExpr elementPairGeospatialQuery(String elementName, String latitudeElementNames, String longitudeElementNames, CtsRegionSeqExpr regions, String... options) {
        return elementPairGeospatialQuery((elementName == null) ? (XsQNameExpr) null : xs.QName(elementName), (latitudeElementNames == null) ? (XsQNameExpr) null : xs.QName(latitudeElementNames), (longitudeElementNames == null) ? (XsQNameExpr) null : xs.QName(longitudeElementNames), regions, (options == null) ? (XsStringExpr) null : xs.stringSeq(options));
    }

    
    @Override
    public CtsQueryExpr elementPairGeospatialQuery(XsQNameSeqExpr elementName, XsQNameSeqExpr latitudeElementNames, XsQNameSeqExpr longitudeElementNames, CtsRegionSeqExpr regions, XsStringSeqExpr options) {
        return new QueryCallImpl("cts", "element-pair-geospatial-query", new Object[]{ elementName, latitudeElementNames, longitudeElementNames, regions, options });
    }

    
    @Override
    public CtsQueryExpr elementPairGeospatialQuery(String elementName, String latitudeElementNames, String longitudeElementNames, CtsRegionSeqExpr regions, String options, double weight) {
        return elementPairGeospatialQuery((elementName == null) ? (XsQNameExpr) null : xs.QName(elementName), (latitudeElementNames == null) ? (XsQNameExpr) null : xs.QName(latitudeElementNames), (longitudeElementNames == null) ? (XsQNameExpr) null : xs.QName(longitudeElementNames), regions, (options == null) ? (XsStringExpr) null : xs.string(options), xs.doubleVal(weight));
    }

    
    @Override
    public CtsQueryExpr elementPairGeospatialQuery(XsQNameSeqExpr elementName, XsQNameSeqExpr latitudeElementNames, XsQNameSeqExpr longitudeElementNames, CtsRegionSeqExpr regions, XsStringSeqExpr options, XsDoubleExpr weight) {
        return new QueryCallImpl("cts", "element-pair-geospatial-query", new Object[]{ elementName, latitudeElementNames, longitudeElementNames, regions, options, weight });
    }

    
    @Override
    public CtsQueryExpr elementQuery(String elementName, CtsQueryExpr query) {
        return elementQuery((elementName == null) ? (XsQNameExpr) null : xs.QName(elementName), query);
    }

    
    @Override
    public CtsQueryExpr elementQuery(XsQNameSeqExpr elementName, CtsQueryExpr query) {
        return new QueryCallImpl("cts", "element-query", new Object[]{ elementName, query });
    }

    
    @Override
    public CtsQueryExpr elementRangeQuery(String elementName, String operator, String value) {
        return elementRangeQuery((elementName == null) ? (XsQNameExpr) null : xs.QName(elementName), (operator == null) ? (XsStringExpr) null : xs.string(operator), (value == null) ? (XsAnyAtomicTypeExpr) null : xs.string(value));
    }

    
    @Override
    public CtsQueryExpr elementRangeQuery(XsQNameSeqExpr elementName, XsStringExpr operator, XsAnyAtomicTypeSeqExpr value) {
        return new QueryCallImpl("cts", "element-range-query", new Object[]{ elementName, operator, value });
    }

    
    @Override
    public CtsQueryExpr elementRangeQuery(String elementName, String operator, String value, String... options) {
        return elementRangeQuery((elementName == null) ? (XsQNameExpr) null : xs.QName(elementName), (operator == null) ? (XsStringExpr) null : xs.string(operator), (value == null) ? (XsAnyAtomicTypeExpr) null : xs.string(value), (options == null) ? (XsStringExpr) null : xs.stringSeq(options));
    }

    
    @Override
    public CtsQueryExpr elementRangeQuery(XsQNameSeqExpr elementName, XsStringExpr operator, XsAnyAtomicTypeSeqExpr value, XsStringSeqExpr options) {
        return new QueryCallImpl("cts", "element-range-query", new Object[]{ elementName, operator, value, options });
    }

    
    @Override
    public CtsQueryExpr elementRangeQuery(String elementName, String operator, String value, String options, double weight) {
        return elementRangeQuery((elementName == null) ? (XsQNameExpr) null : xs.QName(elementName), (operator == null) ? (XsStringExpr) null : xs.string(operator), (value == null) ? (XsAnyAtomicTypeExpr) null : xs.string(value), (options == null) ? (XsStringExpr) null : xs.string(options), xs.doubleVal(weight));
    }

    
    @Override
    public CtsQueryExpr elementRangeQuery(XsQNameSeqExpr elementName, XsStringExpr operator, XsAnyAtomicTypeSeqExpr value, XsStringSeqExpr options, XsDoubleExpr weight) {
        return new QueryCallImpl("cts", "element-range-query", new Object[]{ elementName, operator, value, options, weight });
    }

    
    @Override
    public CtsReferenceExpr elementReference(String element) {
        return elementReference((element == null) ? (XsQNameExpr) null : xs.QName(element));
    }

    
    @Override
    public CtsReferenceExpr elementReference(XsQNameExpr element) {
        return new ReferenceCallImpl("cts", "element-reference", new Object[]{ element });
    }

    
    @Override
    public CtsReferenceExpr elementReference(String element, String options) {
        return elementReference((element == null) ? (XsQNameExpr) null : xs.QName(element), (options == null) ? (XsStringExpr) null : xs.string(options));
    }

    
    @Override
    public CtsReferenceExpr elementReference(XsQNameExpr element, XsStringSeqExpr options) {
        return new ReferenceCallImpl("cts", "element-reference", new Object[]{ element, options });
    }

    
    @Override
    public CtsQueryExpr elementValueQuery(String elementName) {
        return elementValueQuery((elementName == null) ? (XsQNameExpr) null : xs.QName(elementName));
    }

    
    @Override
    public CtsQueryExpr elementValueQuery(XsQNameSeqExpr elementName) {
        return new QueryCallImpl("cts", "element-value-query", new Object[]{ elementName });
    }

    
    @Override
    public CtsQueryExpr elementValueQuery(String elementName, String text) {
        return elementValueQuery((elementName == null) ? (XsQNameExpr) null : xs.QName(elementName), (text == null) ? (XsStringExpr) null : xs.string(text));
    }

    
    @Override
    public CtsQueryExpr elementValueQuery(XsQNameSeqExpr elementName, XsStringSeqExpr text) {
        return new QueryCallImpl("cts", "element-value-query", new Object[]{ elementName, text });
    }

    
    @Override
    public CtsQueryExpr elementValueQuery(String elementName, String text, String... options) {
        return elementValueQuery((elementName == null) ? (XsQNameExpr) null : xs.QName(elementName), (text == null) ? (XsStringExpr) null : xs.string(text), (options == null) ? (XsStringExpr) null : xs.stringSeq(options));
    }

    
    @Override
    public CtsQueryExpr elementValueQuery(XsQNameSeqExpr elementName, XsStringSeqExpr text, XsStringSeqExpr options) {
        return new QueryCallImpl("cts", "element-value-query", new Object[]{ elementName, text, options });
    }

    
    @Override
    public CtsQueryExpr elementValueQuery(String elementName, String text, String options, double weight) {
        return elementValueQuery((elementName == null) ? (XsQNameExpr) null : xs.QName(elementName), (text == null) ? (XsStringExpr) null : xs.string(text), (options == null) ? (XsStringExpr) null : xs.string(options), xs.doubleVal(weight));
    }

    
    @Override
    public CtsQueryExpr elementValueQuery(XsQNameSeqExpr elementName, XsStringSeqExpr text, XsStringSeqExpr options, XsDoubleExpr weight) {
        return new QueryCallImpl("cts", "element-value-query", new Object[]{ elementName, text, options, weight });
    }

    
    @Override
    public CtsQueryExpr elementWordQuery(String elementName, String text) {
        return elementWordQuery((elementName == null) ? (XsQNameExpr) null : xs.QName(elementName), (text == null) ? (XsStringExpr) null : xs.string(text));
    }

    
    @Override
    public CtsQueryExpr elementWordQuery(XsQNameSeqExpr elementName, XsStringSeqExpr text) {
        return new QueryCallImpl("cts", "element-word-query", new Object[]{ elementName, text });
    }

    
    @Override
    public CtsQueryExpr elementWordQuery(String elementName, String text, String... options) {
        return elementWordQuery((elementName == null) ? (XsQNameExpr) null : xs.QName(elementName), (text == null) ? (XsStringExpr) null : xs.string(text), (options == null) ? (XsStringExpr) null : xs.stringSeq(options));
    }

    
    @Override
    public CtsQueryExpr elementWordQuery(XsQNameSeqExpr elementName, XsStringSeqExpr text, XsStringSeqExpr options) {
        return new QueryCallImpl("cts", "element-word-query", new Object[]{ elementName, text, options });
    }

    
    @Override
    public CtsQueryExpr elementWordQuery(String elementName, String text, String options, double weight) {
        return elementWordQuery((elementName == null) ? (XsQNameExpr) null : xs.QName(elementName), (text == null) ? (XsStringExpr) null : xs.string(text), (options == null) ? (XsStringExpr) null : xs.string(options), xs.doubleVal(weight));
    }

    
    @Override
    public CtsQueryExpr elementWordQuery(XsQNameSeqExpr elementName, XsStringSeqExpr text, XsStringSeqExpr options, XsDoubleExpr weight) {
        return new QueryCallImpl("cts", "element-word-query", new Object[]{ elementName, text, options, weight });
    }

    
    @Override
    public CtsQueryExpr falseQuery() {
        return new QueryCallImpl("cts", "false-query", new Object[]{  });
    }

    
    @Override
    public CtsQueryExpr fieldRangeQuery(String fieldName, String operator, String value) {
        return fieldRangeQuery((fieldName == null) ? (XsStringExpr) null : xs.string(fieldName), (operator == null) ? (XsStringExpr) null : xs.string(operator), (value == null) ? (XsAnyAtomicTypeExpr) null : xs.string(value));
    }

    
    @Override
    public CtsQueryExpr fieldRangeQuery(XsStringSeqExpr fieldName, XsStringExpr operator, XsAnyAtomicTypeSeqExpr value) {
        return new QueryCallImpl("cts", "field-range-query", new Object[]{ fieldName, operator, value });
    }

    
    @Override
    public CtsQueryExpr fieldRangeQuery(String fieldName, String operator, String value, String... options) {
        return fieldRangeQuery((fieldName == null) ? (XsStringExpr) null : xs.string(fieldName), (operator == null) ? (XsStringExpr) null : xs.string(operator), (value == null) ? (XsAnyAtomicTypeExpr) null : xs.string(value), (options == null) ? (XsStringExpr) null : xs.stringSeq(options));
    }

    
    @Override
    public CtsQueryExpr fieldRangeQuery(XsStringSeqExpr fieldName, XsStringExpr operator, XsAnyAtomicTypeSeqExpr value, XsStringSeqExpr options) {
        return new QueryCallImpl("cts", "field-range-query", new Object[]{ fieldName, operator, value, options });
    }

    
    @Override
    public CtsQueryExpr fieldRangeQuery(String fieldName, String operator, String value, String options, double weight) {
        return fieldRangeQuery((fieldName == null) ? (XsStringExpr) null : xs.string(fieldName), (operator == null) ? (XsStringExpr) null : xs.string(operator), (value == null) ? (XsAnyAtomicTypeExpr) null : xs.string(value), (options == null) ? (XsStringExpr) null : xs.string(options), xs.doubleVal(weight));
    }

    
    @Override
    public CtsQueryExpr fieldRangeQuery(XsStringSeqExpr fieldName, XsStringExpr operator, XsAnyAtomicTypeSeqExpr value, XsStringSeqExpr options, XsDoubleExpr weight) {
        return new QueryCallImpl("cts", "field-range-query", new Object[]{ fieldName, operator, value, options, weight });
    }

    
    @Override
    public CtsReferenceExpr fieldReference(String field) {
        return fieldReference((field == null) ? (XsStringExpr) null : xs.string(field));
    }

    
    @Override
    public CtsReferenceExpr fieldReference(XsStringExpr field) {
        return new ReferenceCallImpl("cts", "field-reference", new Object[]{ field });
    }

    
    @Override
    public CtsReferenceExpr fieldReference(String field, String options) {
        return fieldReference((field == null) ? (XsStringExpr) null : xs.string(field), (options == null) ? (XsStringExpr) null : xs.string(options));
    }

    
    @Override
    public CtsReferenceExpr fieldReference(XsStringExpr field, XsStringSeqExpr options) {
        return new ReferenceCallImpl("cts", "field-reference", new Object[]{ field, options });
    }

    
    @Override
    public CtsQueryExpr fieldValueQuery(String fieldName, String text) {
        return fieldValueQuery((fieldName == null) ? (XsStringExpr) null : xs.string(fieldName), (text == null) ? (XsAnyAtomicTypeExpr) null : xs.string(text));
    }

    
    @Override
    public CtsQueryExpr fieldValueQuery(XsStringSeqExpr fieldName, XsAnyAtomicTypeSeqExpr text) {
        return new QueryCallImpl("cts", "field-value-query", new Object[]{ fieldName, text });
    }

    
    @Override
    public CtsQueryExpr fieldValueQuery(String fieldName, String text, String... options) {
        return fieldValueQuery((fieldName == null) ? (XsStringExpr) null : xs.string(fieldName), (text == null) ? (XsAnyAtomicTypeExpr) null : xs.string(text), (options == null) ? (XsStringExpr) null : xs.stringSeq(options));
    }

    
    @Override
    public CtsQueryExpr fieldValueQuery(XsStringSeqExpr fieldName, XsAnyAtomicTypeSeqExpr text, XsStringSeqExpr options) {
        return new QueryCallImpl("cts", "field-value-query", new Object[]{ fieldName, text, options });
    }

    
    @Override
    public CtsQueryExpr fieldValueQuery(String fieldName, String text, String options, double weight) {
        return fieldValueQuery((fieldName == null) ? (XsStringExpr) null : xs.string(fieldName), (text == null) ? (XsAnyAtomicTypeExpr) null : xs.string(text), (options == null) ? (XsStringExpr) null : xs.string(options), xs.doubleVal(weight));
    }

    
    @Override
    public CtsQueryExpr fieldValueQuery(XsStringSeqExpr fieldName, XsAnyAtomicTypeSeqExpr text, XsStringSeqExpr options, XsDoubleExpr weight) {
        return new QueryCallImpl("cts", "field-value-query", new Object[]{ fieldName, text, options, weight });
    }

    
    @Override
    public CtsQueryExpr fieldWordQuery(String fieldName, String text) {
        return fieldWordQuery((fieldName == null) ? (XsStringExpr) null : xs.string(fieldName), (text == null) ? (XsStringExpr) null : xs.string(text));
    }

    
    @Override
    public CtsQueryExpr fieldWordQuery(XsStringSeqExpr fieldName, XsStringSeqExpr text) {
        return new QueryCallImpl("cts", "field-word-query", new Object[]{ fieldName, text });
    }

    
    @Override
    public CtsQueryExpr fieldWordQuery(String fieldName, String text, String... options) {
        return fieldWordQuery((fieldName == null) ? (XsStringExpr) null : xs.string(fieldName), (text == null) ? (XsStringExpr) null : xs.string(text), (options == null) ? (XsStringExpr) null : xs.stringSeq(options));
    }

    
    @Override
    public CtsQueryExpr fieldWordQuery(XsStringSeqExpr fieldName, XsStringSeqExpr text, XsStringSeqExpr options) {
        return new QueryCallImpl("cts", "field-word-query", new Object[]{ fieldName, text, options });
    }

    
    @Override
    public CtsQueryExpr fieldWordQuery(String fieldName, String text, String options, double weight) {
        return fieldWordQuery((fieldName == null) ? (XsStringExpr) null : xs.string(fieldName), (text == null) ? (XsStringExpr) null : xs.string(text), (options == null) ? (XsStringExpr) null : xs.string(options), xs.doubleVal(weight));
    }

    
    @Override
    public CtsQueryExpr fieldWordQuery(XsStringSeqExpr fieldName, XsStringSeqExpr text, XsStringSeqExpr options, XsDoubleExpr weight) {
        return new QueryCallImpl("cts", "field-word-query", new Object[]{ fieldName, text, options, weight });
    }

    
    @Override
    public CtsQueryExpr jsonPropertyChildGeospatialQuery(String parentPropertyName, String childPropertyNames, CtsRegionExpr... regions) {
        return jsonPropertyChildGeospatialQuery((parentPropertyName == null) ? (XsStringExpr) null : xs.string(parentPropertyName), (childPropertyNames == null) ? (XsStringExpr) null : xs.string(childPropertyNames), new RegionSeqListImpl(regions));
    }

    
    @Override
    public CtsQueryExpr jsonPropertyChildGeospatialQuery(XsStringSeqExpr parentPropertyName, XsStringSeqExpr childPropertyNames, CtsRegionSeqExpr regions) {
        return new QueryCallImpl("cts", "json-property-child-geospatial-query", new Object[]{ parentPropertyName, childPropertyNames, regions });
    }

    
    @Override
    public CtsQueryExpr jsonPropertyChildGeospatialQuery(String parentPropertyName, String childPropertyNames, CtsRegionSeqExpr regions, String... options) {
        return jsonPropertyChildGeospatialQuery((parentPropertyName == null) ? (XsStringExpr) null : xs.string(parentPropertyName), (childPropertyNames == null) ? (XsStringExpr) null : xs.string(childPropertyNames), regions, (options == null) ? (XsStringExpr) null : xs.stringSeq(options));
    }

    
    @Override
    public CtsQueryExpr jsonPropertyChildGeospatialQuery(XsStringSeqExpr parentPropertyName, XsStringSeqExpr childPropertyNames, CtsRegionSeqExpr regions, XsStringSeqExpr options) {
        return new QueryCallImpl("cts", "json-property-child-geospatial-query", new Object[]{ parentPropertyName, childPropertyNames, regions, options });
    }

    
    @Override
    public CtsQueryExpr jsonPropertyChildGeospatialQuery(String parentPropertyName, String childPropertyNames, CtsRegionSeqExpr regions, String options, double weight) {
        return jsonPropertyChildGeospatialQuery((parentPropertyName == null) ? (XsStringExpr) null : xs.string(parentPropertyName), (childPropertyNames == null) ? (XsStringExpr) null : xs.string(childPropertyNames), regions, (options == null) ? (XsStringExpr) null : xs.string(options), xs.doubleVal(weight));
    }

    
    @Override
    public CtsQueryExpr jsonPropertyChildGeospatialQuery(XsStringSeqExpr parentPropertyName, XsStringSeqExpr childPropertyNames, CtsRegionSeqExpr regions, XsStringSeqExpr options, XsDoubleExpr weight) {
        return new QueryCallImpl("cts", "json-property-child-geospatial-query", new Object[]{ parentPropertyName, childPropertyNames, regions, options, weight });
    }

    
    @Override
    public CtsQueryExpr jsonPropertyGeospatialQuery(String propertyName, CtsRegionExpr... regions) {
        return jsonPropertyGeospatialQuery((propertyName == null) ? (XsStringExpr) null : xs.string(propertyName), new RegionSeqListImpl(regions));
    }

    
    @Override
    public CtsQueryExpr jsonPropertyGeospatialQuery(XsStringSeqExpr propertyName, CtsRegionSeqExpr regions) {
        return new QueryCallImpl("cts", "json-property-geospatial-query", new Object[]{ propertyName, regions });
    }

    
    @Override
    public CtsQueryExpr jsonPropertyGeospatialQuery(String propertyName, CtsRegionSeqExpr regions, String... options) {
        return jsonPropertyGeospatialQuery((propertyName == null) ? (XsStringExpr) null : xs.string(propertyName), regions, (options == null) ? (XsStringExpr) null : xs.stringSeq(options));
    }

    
    @Override
    public CtsQueryExpr jsonPropertyGeospatialQuery(XsStringSeqExpr propertyName, CtsRegionSeqExpr regions, XsStringSeqExpr options) {
        return new QueryCallImpl("cts", "json-property-geospatial-query", new Object[]{ propertyName, regions, options });
    }

    
    @Override
    public CtsQueryExpr jsonPropertyGeospatialQuery(String propertyName, CtsRegionSeqExpr regions, String options, double weight) {
        return jsonPropertyGeospatialQuery((propertyName == null) ? (XsStringExpr) null : xs.string(propertyName), regions, (options == null) ? (XsStringExpr) null : xs.string(options), xs.doubleVal(weight));
    }

    
    @Override
    public CtsQueryExpr jsonPropertyGeospatialQuery(XsStringSeqExpr propertyName, CtsRegionSeqExpr regions, XsStringSeqExpr options, XsDoubleExpr weight) {
        return new QueryCallImpl("cts", "json-property-geospatial-query", new Object[]{ propertyName, regions, options, weight });
    }

    
    @Override
    public CtsQueryExpr jsonPropertyPairGeospatialQuery(String propertyName, String latitudePropertyNames, String longitudePropertyNames, CtsRegionExpr... regions) {
        return jsonPropertyPairGeospatialQuery((propertyName == null) ? (XsStringExpr) null : xs.string(propertyName), (latitudePropertyNames == null) ? (XsStringExpr) null : xs.string(latitudePropertyNames), (longitudePropertyNames == null) ? (XsStringExpr) null : xs.string(longitudePropertyNames), new RegionSeqListImpl(regions));
    }

    
    @Override
    public CtsQueryExpr jsonPropertyPairGeospatialQuery(XsStringSeqExpr propertyName, XsStringSeqExpr latitudePropertyNames, XsStringSeqExpr longitudePropertyNames, CtsRegionSeqExpr regions) {
        return new QueryCallImpl("cts", "json-property-pair-geospatial-query", new Object[]{ propertyName, latitudePropertyNames, longitudePropertyNames, regions });
    }

    
    @Override
    public CtsQueryExpr jsonPropertyPairGeospatialQuery(String propertyName, String latitudePropertyNames, String longitudePropertyNames, CtsRegionSeqExpr regions, String... options) {
        return jsonPropertyPairGeospatialQuery((propertyName == null) ? (XsStringExpr) null : xs.string(propertyName), (latitudePropertyNames == null) ? (XsStringExpr) null : xs.string(latitudePropertyNames), (longitudePropertyNames == null) ? (XsStringExpr) null : xs.string(longitudePropertyNames), regions, (options == null) ? (XsStringExpr) null : xs.stringSeq(options));
    }

    
    @Override
    public CtsQueryExpr jsonPropertyPairGeospatialQuery(XsStringSeqExpr propertyName, XsStringSeqExpr latitudePropertyNames, XsStringSeqExpr longitudePropertyNames, CtsRegionSeqExpr regions, XsStringSeqExpr options) {
        return new QueryCallImpl("cts", "json-property-pair-geospatial-query", new Object[]{ propertyName, latitudePropertyNames, longitudePropertyNames, regions, options });
    }

    
    @Override
    public CtsQueryExpr jsonPropertyPairGeospatialQuery(String propertyName, String latitudePropertyNames, String longitudePropertyNames, CtsRegionSeqExpr regions, String options, double weight) {
        return jsonPropertyPairGeospatialQuery((propertyName == null) ? (XsStringExpr) null : xs.string(propertyName), (latitudePropertyNames == null) ? (XsStringExpr) null : xs.string(latitudePropertyNames), (longitudePropertyNames == null) ? (XsStringExpr) null : xs.string(longitudePropertyNames), regions, (options == null) ? (XsStringExpr) null : xs.string(options), xs.doubleVal(weight));
    }

    
    @Override
    public CtsQueryExpr jsonPropertyPairGeospatialQuery(XsStringSeqExpr propertyName, XsStringSeqExpr latitudePropertyNames, XsStringSeqExpr longitudePropertyNames, CtsRegionSeqExpr regions, XsStringSeqExpr options, XsDoubleExpr weight) {
        return new QueryCallImpl("cts", "json-property-pair-geospatial-query", new Object[]{ propertyName, latitudePropertyNames, longitudePropertyNames, regions, options, weight });
    }

    
    @Override
    public CtsQueryExpr jsonPropertyRangeQuery(String propertyName, String operator, String value) {
        return jsonPropertyRangeQuery((propertyName == null) ? (XsStringExpr) null : xs.string(propertyName), (operator == null) ? (XsStringExpr) null : xs.string(operator), (value == null) ? (XsAnyAtomicTypeExpr) null : xs.string(value));
    }

    
    @Override
    public CtsQueryExpr jsonPropertyRangeQuery(XsStringSeqExpr propertyName, XsStringExpr operator, XsAnyAtomicTypeSeqExpr value) {
        return new QueryCallImpl("cts", "json-property-range-query", new Object[]{ propertyName, operator, value });
    }

    
    @Override
    public CtsQueryExpr jsonPropertyRangeQuery(String propertyName, String operator, String value, String... options) {
        return jsonPropertyRangeQuery((propertyName == null) ? (XsStringExpr) null : xs.string(propertyName), (operator == null) ? (XsStringExpr) null : xs.string(operator), (value == null) ? (XsAnyAtomicTypeExpr) null : xs.string(value), (options == null) ? (XsStringExpr) null : xs.stringSeq(options));
    }

    
    @Override
    public CtsQueryExpr jsonPropertyRangeQuery(XsStringSeqExpr propertyName, XsStringExpr operator, XsAnyAtomicTypeSeqExpr value, XsStringSeqExpr options) {
        return new QueryCallImpl("cts", "json-property-range-query", new Object[]{ propertyName, operator, value, options });
    }

    
    @Override
    public CtsQueryExpr jsonPropertyRangeQuery(String propertyName, String operator, String value, String options, double weight) {
        return jsonPropertyRangeQuery((propertyName == null) ? (XsStringExpr) null : xs.string(propertyName), (operator == null) ? (XsStringExpr) null : xs.string(operator), (value == null) ? (XsAnyAtomicTypeExpr) null : xs.string(value), (options == null) ? (XsStringExpr) null : xs.string(options), xs.doubleVal(weight));
    }

    
    @Override
    public CtsQueryExpr jsonPropertyRangeQuery(XsStringSeqExpr propertyName, XsStringExpr operator, XsAnyAtomicTypeSeqExpr value, XsStringSeqExpr options, XsDoubleExpr weight) {
        return new QueryCallImpl("cts", "json-property-range-query", new Object[]{ propertyName, operator, value, options, weight });
    }

    
    @Override
    public CtsReferenceExpr jsonPropertyReference(String property) {
        return jsonPropertyReference((property == null) ? (XsStringExpr) null : xs.string(property));
    }

    
    @Override
    public CtsReferenceExpr jsonPropertyReference(XsStringExpr property) {
        return new ReferenceCallImpl("cts", "json-property-reference", new Object[]{ property });
    }

    
    @Override
    public CtsReferenceExpr jsonPropertyReference(String property, String options) {
        return jsonPropertyReference((property == null) ? (XsStringExpr) null : xs.string(property), (options == null) ? (XsStringExpr) null : xs.string(options));
    }

    
    @Override
    public CtsReferenceExpr jsonPropertyReference(XsStringExpr property, XsStringSeqExpr options) {
        return new ReferenceCallImpl("cts", "json-property-reference", new Object[]{ property, options });
    }

    
    @Override
    public CtsQueryExpr jsonPropertyScopeQuery(String propertyName, CtsQueryExpr query) {
        return jsonPropertyScopeQuery((propertyName == null) ? (XsStringExpr) null : xs.string(propertyName), query);
    }

    
    @Override
    public CtsQueryExpr jsonPropertyScopeQuery(XsStringSeqExpr propertyName, CtsQueryExpr query) {
        return new QueryCallImpl("cts", "json-property-scope-query", new Object[]{ propertyName, query });
    }

    
    @Override
    public CtsQueryExpr jsonPropertyValueQuery(String propertyName, String value) {
        return jsonPropertyValueQuery((propertyName == null) ? (XsStringExpr) null : xs.string(propertyName), (value == null) ? (XsAnyAtomicTypeExpr) null : xs.string(value));
    }

    
    @Override
    public CtsQueryExpr jsonPropertyValueQuery(XsStringSeqExpr propertyName, XsAnyAtomicTypeSeqExpr value) {
        return new QueryCallImpl("cts", "json-property-value-query", new Object[]{ propertyName, value });
    }

    
    @Override
    public CtsQueryExpr jsonPropertyValueQuery(String propertyName, String value, String... options) {
        return jsonPropertyValueQuery((propertyName == null) ? (XsStringExpr) null : xs.string(propertyName), (value == null) ? (XsAnyAtomicTypeExpr) null : xs.string(value), (options == null) ? (XsStringExpr) null : xs.stringSeq(options));
    }

    
    @Override
    public CtsQueryExpr jsonPropertyValueQuery(XsStringSeqExpr propertyName, XsAnyAtomicTypeSeqExpr value, XsStringSeqExpr options) {
        return new QueryCallImpl("cts", "json-property-value-query", new Object[]{ propertyName, value, options });
    }

    
    @Override
    public CtsQueryExpr jsonPropertyValueQuery(String propertyName, String value, String options, double weight) {
        return jsonPropertyValueQuery((propertyName == null) ? (XsStringExpr) null : xs.string(propertyName), (value == null) ? (XsAnyAtomicTypeExpr) null : xs.string(value), (options == null) ? (XsStringExpr) null : xs.string(options), xs.doubleVal(weight));
    }

    
    @Override
    public CtsQueryExpr jsonPropertyValueQuery(XsStringSeqExpr propertyName, XsAnyAtomicTypeSeqExpr value, XsStringSeqExpr options, XsDoubleExpr weight) {
        return new QueryCallImpl("cts", "json-property-value-query", new Object[]{ propertyName, value, options, weight });
    }

    
    @Override
    public CtsQueryExpr jsonPropertyWordQuery(String propertyName, String text) {
        return jsonPropertyWordQuery((propertyName == null) ? (XsStringExpr) null : xs.string(propertyName), (text == null) ? (XsStringExpr) null : xs.string(text));
    }

    
    @Override
    public CtsQueryExpr jsonPropertyWordQuery(XsStringSeqExpr propertyName, XsStringSeqExpr text) {
        return new QueryCallImpl("cts", "json-property-word-query", new Object[]{ propertyName, text });
    }

    
    @Override
    public CtsQueryExpr jsonPropertyWordQuery(String propertyName, String text, String... options) {
        return jsonPropertyWordQuery((propertyName == null) ? (XsStringExpr) null : xs.string(propertyName), (text == null) ? (XsStringExpr) null : xs.string(text), (options == null) ? (XsStringExpr) null : xs.stringSeq(options));
    }

    
    @Override
    public CtsQueryExpr jsonPropertyWordQuery(XsStringSeqExpr propertyName, XsStringSeqExpr text, XsStringSeqExpr options) {
        return new QueryCallImpl("cts", "json-property-word-query", new Object[]{ propertyName, text, options });
    }

    
    @Override
    public CtsQueryExpr jsonPropertyWordQuery(String propertyName, String text, String options, double weight) {
        return jsonPropertyWordQuery((propertyName == null) ? (XsStringExpr) null : xs.string(propertyName), (text == null) ? (XsStringExpr) null : xs.string(text), (options == null) ? (XsStringExpr) null : xs.string(options), xs.doubleVal(weight));
    }

    
    @Override
    public CtsQueryExpr jsonPropertyWordQuery(XsStringSeqExpr propertyName, XsStringSeqExpr text, XsStringSeqExpr options, XsDoubleExpr weight) {
        return new QueryCallImpl("cts", "json-property-word-query", new Object[]{ propertyName, text, options, weight });
    }

    
    @Override
    public CtsQueryExpr locksFragmentQuery(CtsQueryExpr query) {
        return new QueryCallImpl("cts", "locks-fragment-query", new Object[]{ query });
    }

    
    @Override
    public CtsQueryExpr lsqtQuery(String temporalCollection) {
        return lsqtQuery((temporalCollection == null) ? (XsStringExpr) null : xs.string(temporalCollection));
    }

    
    @Override
    public CtsQueryExpr lsqtQuery(XsStringExpr temporalCollection) {
        return new QueryCallImpl("cts", "lsqt-query", new Object[]{ temporalCollection });
    }

    
    @Override
    public CtsQueryExpr lsqtQuery(String temporalCollection, String timestamp) {
        return lsqtQuery((temporalCollection == null) ? (XsStringExpr) null : xs.string(temporalCollection), (timestamp == null) ? (XsDateTimeExpr) null : xs.dateTime(timestamp));
    }

    
    @Override
    public CtsQueryExpr lsqtQuery(XsStringExpr temporalCollection, XsDateTimeExpr timestamp) {
        return new QueryCallImpl("cts", "lsqt-query", new Object[]{ temporalCollection, timestamp });
    }

    
    @Override
    public CtsQueryExpr lsqtQuery(String temporalCollection, String timestamp, String... options) {
        return lsqtQuery((temporalCollection == null) ? (XsStringExpr) null : xs.string(temporalCollection), (timestamp == null) ? (XsDateTimeExpr) null : xs.dateTime(timestamp), (options == null) ? (XsStringExpr) null : xs.stringSeq(options));
    }

    
    @Override
    public CtsQueryExpr lsqtQuery(XsStringExpr temporalCollection, XsDateTimeExpr timestamp, XsStringSeqExpr options) {
        return new QueryCallImpl("cts", "lsqt-query", new Object[]{ temporalCollection, timestamp, options });
    }

    
    @Override
    public CtsQueryExpr lsqtQuery(String temporalCollection, String timestamp, String options, double weight) {
        return lsqtQuery((temporalCollection == null) ? (XsStringExpr) null : xs.string(temporalCollection), (timestamp == null) ? (XsDateTimeExpr) null : xs.dateTime(timestamp), (options == null) ? (XsStringExpr) null : xs.string(options), xs.doubleVal(weight));
    }

    
    @Override
    public CtsQueryExpr lsqtQuery(XsStringExpr temporalCollection, XsDateTimeExpr timestamp, XsStringSeqExpr options, XsDoubleExpr weight) {
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
    public CtsQueryExpr nearQuery(CtsQuerySeqExpr queries, XsDoubleExpr distance) {
        return new QueryCallImpl("cts", "near-query", new Object[]{ queries, distance });
    }

    
    @Override
    public CtsQueryExpr nearQuery(CtsQuerySeqExpr queries, double distance, String... options) {
        return nearQuery(queries, xs.doubleVal(distance), (options == null) ? (XsStringExpr) null : xs.stringSeq(options));
    }

    
    @Override
    public CtsQueryExpr nearQuery(CtsQuerySeqExpr queries, XsDoubleExpr distance, XsStringSeqExpr options) {
        return new QueryCallImpl("cts", "near-query", new Object[]{ queries, distance, options });
    }

    
    @Override
    public CtsQueryExpr nearQuery(CtsQuerySeqExpr queries, double distance, String options, double distanceWeight) {
        return nearQuery(queries, xs.doubleVal(distance), (options == null) ? (XsStringExpr) null : xs.string(options), xs.doubleVal(distanceWeight));
    }

    
    @Override
    public CtsQueryExpr nearQuery(CtsQuerySeqExpr queries, XsDoubleExpr distance, XsStringSeqExpr options, XsDoubleExpr distanceWeight) {
        return new QueryCallImpl("cts", "near-query", new Object[]{ queries, distance, options, distanceWeight });
    }

    
    @Override
    public CtsQueryExpr notInQuery(CtsQueryExpr positiveQuery, CtsQueryExpr negativeQuery) {
        return new QueryCallImpl("cts", "not-in-query", new Object[]{ positiveQuery, negativeQuery });
    }

    
    @Override
    public CtsQueryExpr notQuery(CtsQueryExpr query) {
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
        return orQuery(queries, (options == null) ? (XsStringExpr) null : xs.string(options));
    }

    
    @Override
    public CtsQueryExpr orQuery(CtsQuerySeqExpr queries, XsStringSeqExpr options) {
        return new QueryCallImpl("cts", "or-query", new Object[]{ queries, options });
    }

    
    @Override
    public CtsQueryExpr pathGeospatialQuery(String pathExpression, CtsRegionExpr... regions) {
        return pathGeospatialQuery((pathExpression == null) ? (XsStringExpr) null : xs.string(pathExpression), new RegionSeqListImpl(regions));
    }

    
    @Override
    public CtsQueryExpr pathGeospatialQuery(XsStringSeqExpr pathExpression, CtsRegionSeqExpr regions) {
        return new QueryCallImpl("cts", "path-geospatial-query", new Object[]{ pathExpression, regions });
    }

    
    @Override
    public CtsQueryExpr pathGeospatialQuery(String pathExpression, CtsRegionSeqExpr regions, String... options) {
        return pathGeospatialQuery((pathExpression == null) ? (XsStringExpr) null : xs.string(pathExpression), regions, (options == null) ? (XsStringExpr) null : xs.stringSeq(options));
    }

    
    @Override
    public CtsQueryExpr pathGeospatialQuery(XsStringSeqExpr pathExpression, CtsRegionSeqExpr regions, XsStringSeqExpr options) {
        return new QueryCallImpl("cts", "path-geospatial-query", new Object[]{ pathExpression, regions, options });
    }

    
    @Override
    public CtsQueryExpr pathGeospatialQuery(String pathExpression, CtsRegionSeqExpr regions, String options, double weight) {
        return pathGeospatialQuery((pathExpression == null) ? (XsStringExpr) null : xs.string(pathExpression), regions, (options == null) ? (XsStringExpr) null : xs.string(options), xs.doubleVal(weight));
    }

    
    @Override
    public CtsQueryExpr pathGeospatialQuery(XsStringSeqExpr pathExpression, CtsRegionSeqExpr regions, XsStringSeqExpr options, XsDoubleExpr weight) {
        return new QueryCallImpl("cts", "path-geospatial-query", new Object[]{ pathExpression, regions, options, weight });
    }

    
    @Override
    public CtsQueryExpr pathRangeQuery(String pathExpression, String operator, String value) {
        return pathRangeQuery((pathExpression == null) ? (XsStringExpr) null : xs.string(pathExpression), (operator == null) ? (XsStringExpr) null : xs.string(operator), (value == null) ? (XsAnyAtomicTypeExpr) null : xs.string(value));
    }

    
    @Override
    public CtsQueryExpr pathRangeQuery(XsStringSeqExpr pathExpression, XsStringExpr operator, XsAnyAtomicTypeSeqExpr value) {
        return new QueryCallImpl("cts", "path-range-query", new Object[]{ pathExpression, operator, value });
    }

    
    @Override
    public CtsQueryExpr pathRangeQuery(String pathExpression, String operator, String value, String... options) {
        return pathRangeQuery((pathExpression == null) ? (XsStringExpr) null : xs.string(pathExpression), (operator == null) ? (XsStringExpr) null : xs.string(operator), (value == null) ? (XsAnyAtomicTypeExpr) null : xs.string(value), (options == null) ? (XsStringExpr) null : xs.stringSeq(options));
    }

    
    @Override
    public CtsQueryExpr pathRangeQuery(XsStringSeqExpr pathExpression, XsStringExpr operator, XsAnyAtomicTypeSeqExpr value, XsStringSeqExpr options) {
        return new QueryCallImpl("cts", "path-range-query", new Object[]{ pathExpression, operator, value, options });
    }

    
    @Override
    public CtsQueryExpr pathRangeQuery(String pathExpression, String operator, String value, String options, double weight) {
        return pathRangeQuery((pathExpression == null) ? (XsStringExpr) null : xs.string(pathExpression), (operator == null) ? (XsStringExpr) null : xs.string(operator), (value == null) ? (XsAnyAtomicTypeExpr) null : xs.string(value), (options == null) ? (XsStringExpr) null : xs.string(options), xs.doubleVal(weight));
    }

    
    @Override
    public CtsQueryExpr pathRangeQuery(XsStringSeqExpr pathExpression, XsStringExpr operator, XsAnyAtomicTypeSeqExpr value, XsStringSeqExpr options, XsDoubleExpr weight) {
        return new QueryCallImpl("cts", "path-range-query", new Object[]{ pathExpression, operator, value, options, weight });
    }

    
    @Override
    public CtsReferenceExpr pathReference(String pathExpression) {
        return pathReference((pathExpression == null) ? (XsStringExpr) null : xs.string(pathExpression));
    }

    
    @Override
    public CtsReferenceExpr pathReference(XsStringExpr pathExpression) {
        return new ReferenceCallImpl("cts", "path-reference", new Object[]{ pathExpression });
    }

    
    @Override
    public CtsReferenceExpr pathReference(String pathExpression, String options) {
        return pathReference((pathExpression == null) ? (XsStringExpr) null : xs.string(pathExpression), (options == null) ? (XsStringExpr) null : xs.string(options));
    }

    
    @Override
    public CtsReferenceExpr pathReference(XsStringExpr pathExpression, XsStringSeqExpr options) {
        return new ReferenceCallImpl("cts", "path-reference", new Object[]{ pathExpression, options });
    }

    
    @Override
    public CtsReferenceExpr pathReference(String pathExpression, String options, MapMapExpr map) {
        return pathReference((pathExpression == null) ? (XsStringExpr) null : xs.string(pathExpression), (options == null) ? (XsStringExpr) null : xs.string(options), map);
    }

    
    @Override
    public CtsReferenceExpr pathReference(XsStringExpr pathExpression, XsStringSeqExpr options, MapMapExpr map) {
        return new ReferenceCallImpl("cts", "path-reference", new Object[]{ pathExpression, options, map });
    }

    
    @Override
    public CtsPeriodExpr period(String start, String end) {
        return period((start == null) ? (XsDateTimeExpr) null : xs.dateTime(start), (end == null) ? (XsDateTimeExpr) null : xs.dateTime(end));
    }

    
    @Override
    public CtsPeriodExpr period(XsDateTimeExpr start, XsDateTimeExpr end) {
        return new PeriodCallImpl("cts", "period", new Object[]{ start, end });
    }

    
    @Override
    public CtsQueryExpr periodCompareQuery(String axis1, String operator, String axis2) {
        return periodCompareQuery((axis1 == null) ? (XsStringExpr) null : xs.string(axis1), (operator == null) ? (XsStringExpr) null : xs.string(operator), (axis2 == null) ? (XsStringExpr) null : xs.string(axis2));
    }

    
    @Override
    public CtsQueryExpr periodCompareQuery(XsStringExpr axis1, XsStringExpr operator, XsStringExpr axis2) {
        return new QueryCallImpl("cts", "period-compare-query", new Object[]{ axis1, operator, axis2 });
    }

    
    @Override
    public CtsQueryExpr periodCompareQuery(String axis1, String operator, String axis2, String options) {
        return periodCompareQuery((axis1 == null) ? (XsStringExpr) null : xs.string(axis1), (operator == null) ? (XsStringExpr) null : xs.string(operator), (axis2 == null) ? (XsStringExpr) null : xs.string(axis2), (options == null) ? (XsStringExpr) null : xs.string(options));
    }

    
    @Override
    public CtsQueryExpr periodCompareQuery(XsStringExpr axis1, XsStringExpr operator, XsStringExpr axis2, XsStringSeqExpr options) {
        return new QueryCallImpl("cts", "period-compare-query", new Object[]{ axis1, operator, axis2, options });
    }

    
    @Override
    public CtsQueryExpr periodRangeQuery(String axisName, String operator) {
        return periodRangeQuery((axisName == null) ? (XsStringExpr) null : xs.string(axisName), (operator == null) ? (XsStringExpr) null : xs.string(operator));
    }

    
    @Override
    public CtsQueryExpr periodRangeQuery(XsStringSeqExpr axisName, XsStringExpr operator) {
        return new QueryCallImpl("cts", "period-range-query", new Object[]{ axisName, operator });
    }

    
    @Override
    public CtsQueryExpr periodRangeQuery(String axisName, String operator, CtsPeriodExpr... period) {
        return periodRangeQuery((axisName == null) ? (XsStringExpr) null : xs.string(axisName), (operator == null) ? (XsStringExpr) null : xs.string(operator), new PeriodSeqListImpl(period));
    }

    
    @Override
    public CtsQueryExpr periodRangeQuery(XsStringSeqExpr axisName, XsStringExpr operator, CtsPeriodSeqExpr period) {
        return new QueryCallImpl("cts", "period-range-query", new Object[]{ axisName, operator, period });
    }

    
    @Override
    public CtsQueryExpr periodRangeQuery(String axisName, String operator, CtsPeriodSeqExpr period, String options) {
        return periodRangeQuery((axisName == null) ? (XsStringExpr) null : xs.string(axisName), (operator == null) ? (XsStringExpr) null : xs.string(operator), period, (options == null) ? (XsStringExpr) null : xs.string(options));
    }

    
    @Override
    public CtsQueryExpr periodRangeQuery(XsStringSeqExpr axisName, XsStringExpr operator, CtsPeriodSeqExpr period, XsStringSeqExpr options) {
        return new QueryCallImpl("cts", "period-range-query", new Object[]{ axisName, operator, period, options });
    }

    
    @Override
    public CtsPointExpr point(XsDoubleExpr latitude, double longitude) {
        return point(latitude, xs.doubleVal(longitude));
    }

    
    @Override
    public CtsPointExpr point(XsDoubleExpr latitude, XsDoubleExpr longitude) {
        return new PointCallImpl("cts", "point", new Object[]{ latitude, longitude });
    }

    
    @Override
    public CtsPolygonExpr polygon(XsAnyAtomicTypeSeqExpr vertices) {
        return new PolygonCallImpl("cts", "polygon", new Object[]{ vertices });
    }

    
    @Override
    public CtsQueryExpr propertiesFragmentQuery(CtsQueryExpr query) {
        return new QueryCallImpl("cts", "properties-fragment-query", new Object[]{ query });
    }

    
    @Override
    public XsStringSeqExpr stem(XsStringExpr text) {
        return new XsExprImpl.StringSeqCallImpl("cts", "stem", new Object[]{ text });
    }

    
    @Override
    public XsStringSeqExpr stem(XsStringExpr text, String language) {
        return stem(text, (language == null) ? (XsStringExpr) null : xs.string(language));
    }

    
    @Override
    public XsStringSeqExpr stem(XsStringExpr text, XsStringExpr language) {
        return new XsExprImpl.StringSeqCallImpl("cts", "stem", new Object[]{ text, language });
    }

    
    @Override
    public XsStringSeqExpr stem(XsStringExpr text, String language, String partOfSpeech) {
        return stem(text, (language == null) ? (XsStringExpr) null : xs.string(language), (partOfSpeech == null) ? (XsStringExpr) null : xs.string(partOfSpeech));
    }

    
    @Override
    public XsStringSeqExpr stem(XsStringExpr text, XsStringExpr language, XsStringExpr partOfSpeech) {
        return new XsExprImpl.StringSeqCallImpl("cts", "stem", new Object[]{ text, language, partOfSpeech });
    }

    
    @Override
    public XsStringSeqExpr tokenize(XsStringExpr text) {
        return new XsExprImpl.StringSeqCallImpl("cts", "tokenize", new Object[]{ text });
    }

    
    @Override
    public XsStringSeqExpr tokenize(XsStringExpr text, String language) {
        return tokenize(text, (language == null) ? (XsStringExpr) null : xs.string(language));
    }

    
    @Override
    public XsStringSeqExpr tokenize(XsStringExpr text, XsStringExpr language) {
        return new XsExprImpl.StringSeqCallImpl("cts", "tokenize", new Object[]{ text, language });
    }

    
    @Override
    public XsStringSeqExpr tokenize(XsStringExpr text, String language, String field) {
        return tokenize(text, (language == null) ? (XsStringExpr) null : xs.string(language), (field == null) ? (XsStringExpr) null : xs.string(field));
    }

    
    @Override
    public XsStringSeqExpr tokenize(XsStringExpr text, XsStringExpr language, XsStringExpr field) {
        return new XsExprImpl.StringSeqCallImpl("cts", "tokenize", new Object[]{ text, language, field });
    }

    
    @Override
    public CtsQueryExpr tripleRangeQuery(String subject, String predicate, String object) {
        return tripleRangeQuery((subject == null) ? (XsAnyAtomicTypeExpr) null : xs.string(subject), (predicate == null) ? (XsAnyAtomicTypeExpr) null : xs.string(predicate), (object == null) ? (XsAnyAtomicTypeExpr) null : xs.string(object));
    }

    
    @Override
    public CtsQueryExpr tripleRangeQuery(XsAnyAtomicTypeSeqExpr subject, XsAnyAtomicTypeSeqExpr predicate, XsAnyAtomicTypeSeqExpr object) {
        return new QueryCallImpl("cts", "triple-range-query", new Object[]{ subject, predicate, object });
    }

    
    @Override
    public CtsQueryExpr tripleRangeQuery(String subject, String predicate, String object, String operator) {
        return tripleRangeQuery((subject == null) ? (XsAnyAtomicTypeExpr) null : xs.string(subject), (predicate == null) ? (XsAnyAtomicTypeExpr) null : xs.string(predicate), (object == null) ? (XsAnyAtomicTypeExpr) null : xs.string(object), (operator == null) ? (XsStringExpr) null : xs.string(operator));
    }

    
    @Override
    public CtsQueryExpr tripleRangeQuery(XsAnyAtomicTypeSeqExpr subject, XsAnyAtomicTypeSeqExpr predicate, XsAnyAtomicTypeSeqExpr object, XsStringSeqExpr operator) {
        return new QueryCallImpl("cts", "triple-range-query", new Object[]{ subject, predicate, object, operator });
    }

    
    @Override
    public CtsQueryExpr tripleRangeQuery(String subject, String predicate, String object, String operator, String... options) {
        return tripleRangeQuery((subject == null) ? (XsAnyAtomicTypeExpr) null : xs.string(subject), (predicate == null) ? (XsAnyAtomicTypeExpr) null : xs.string(predicate), (object == null) ? (XsAnyAtomicTypeExpr) null : xs.string(object), (operator == null) ? (XsStringExpr) null : xs.string(operator), (options == null) ? (XsStringExpr) null : xs.stringSeq(options));
    }

    
    @Override
    public CtsQueryExpr tripleRangeQuery(XsAnyAtomicTypeSeqExpr subject, XsAnyAtomicTypeSeqExpr predicate, XsAnyAtomicTypeSeqExpr object, XsStringSeqExpr operator, XsStringSeqExpr options) {
        return new QueryCallImpl("cts", "triple-range-query", new Object[]{ subject, predicate, object, operator, options });
    }

    
    @Override
    public CtsQueryExpr tripleRangeQuery(String subject, String predicate, String object, String operator, String options, double weight) {
        return tripleRangeQuery((subject == null) ? (XsAnyAtomicTypeExpr) null : xs.string(subject), (predicate == null) ? (XsAnyAtomicTypeExpr) null : xs.string(predicate), (object == null) ? (XsAnyAtomicTypeExpr) null : xs.string(object), (operator == null) ? (XsStringExpr) null : xs.string(operator), (options == null) ? (XsStringExpr) null : xs.string(options), xs.doubleVal(weight));
    }

    
    @Override
    public CtsQueryExpr tripleRangeQuery(XsAnyAtomicTypeSeqExpr subject, XsAnyAtomicTypeSeqExpr predicate, XsAnyAtomicTypeSeqExpr object, XsStringSeqExpr operator, XsStringSeqExpr options, XsDoubleExpr weight) {
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
        return wordQuery((text == null) ? (XsStringExpr) null : xs.string(text));
    }

    
    @Override
    public CtsQueryExpr wordQuery(XsStringSeqExpr text) {
        return new QueryCallImpl("cts", "word-query", new Object[]{ text });
    }

    
    @Override
    public CtsQueryExpr wordQuery(String text, String... options) {
        return wordQuery((text == null) ? (XsStringExpr) null : xs.string(text), (options == null) ? (XsStringExpr) null : xs.stringSeq(options));
    }

    
    @Override
    public CtsQueryExpr wordQuery(XsStringSeqExpr text, XsStringSeqExpr options) {
        return new QueryCallImpl("cts", "word-query", new Object[]{ text, options });
    }

    
    @Override
    public CtsQueryExpr wordQuery(String text, String options, double weight) {
        return wordQuery((text == null) ? (XsStringExpr) null : xs.string(text), (options == null) ? (XsStringExpr) null : xs.string(options), xs.doubleVal(weight));
    }

    
    @Override
    public CtsQueryExpr wordQuery(XsStringSeqExpr text, XsStringSeqExpr options, XsDoubleExpr weight) {
        return new QueryCallImpl("cts", "word-query", new Object[]{ text, options, weight });
    }

    @Override
    public CtsBoxSeqExpr boxSeq(CtsBoxExpr... items) {
        return new BoxSeqListImpl(items);
    }
    static class BoxSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements CtsBoxSeqExpr {
        BoxSeqListImpl(Object[] items) {
            super(BaseTypeImpl.convertList(items));
        }
    }
    static class BoxSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements CtsBoxSeqExpr {
        BoxSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
            super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
        }
    }
    static class BoxCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements CtsBoxExpr {
        BoxCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
            super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
        }
    }
 
    @Override
    public CtsCircleSeqExpr circleSeq(CtsCircleExpr... items) {
        return new CircleSeqListImpl(items);
    }
    static class CircleSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements CtsCircleSeqExpr {
        CircleSeqListImpl(Object[] items) {
            super(BaseTypeImpl.convertList(items));
        }
    }
    static class CircleSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements CtsCircleSeqExpr {
        CircleSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
            super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
        }
    }
    static class CircleCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements CtsCircleExpr {
        CircleCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
            super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
        }
    }
 
    @Override
    public CtsPeriodSeqExpr periodSeq(CtsPeriodExpr... items) {
        return new PeriodSeqListImpl(items);
    }
    static class PeriodSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements CtsPeriodSeqExpr {
        PeriodSeqListImpl(Object[] items) {
            super(BaseTypeImpl.convertList(items));
        }
    }
    static class PeriodSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements CtsPeriodSeqExpr {
        PeriodSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
            super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
        }
    }
    static class PeriodCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements CtsPeriodExpr {
        PeriodCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
            super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
        }
    }
 
    @Override
    public CtsPointSeqExpr pointSeq(CtsPointExpr... items) {
        return new PointSeqListImpl(items);
    }
    static class PointSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements CtsPointSeqExpr {
        PointSeqListImpl(Object[] items) {
            super(BaseTypeImpl.convertList(items));
        }
    }
    static class PointSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements CtsPointSeqExpr {
        PointSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
            super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
        }
    }
    static class PointCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements CtsPointExpr {
        PointCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
            super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
        }
    }
 
    @Override
    public CtsPolygonSeqExpr polygonSeq(CtsPolygonExpr... items) {
        return new PolygonSeqListImpl(items);
    }
    static class PolygonSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements CtsPolygonSeqExpr {
        PolygonSeqListImpl(Object[] items) {
            super(BaseTypeImpl.convertList(items));
        }
    }
    static class PolygonSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements CtsPolygonSeqExpr {
        PolygonSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
            super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
        }
    }
    static class PolygonCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements CtsPolygonExpr {
        PolygonCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
            super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
        }
    }
 
    @Override
    public CtsQuerySeqExpr querySeq(CtsQueryExpr... items) {
        return new QuerySeqListImpl(items);
    }
    static class QuerySeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements CtsQuerySeqExpr {
        QuerySeqListImpl(Object[] items) {
            super(BaseTypeImpl.convertList(items));
        }
    }
    static class QuerySeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements CtsQuerySeqExpr {
        QuerySeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
            super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
        }
    }
    static class QueryCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements CtsQueryExpr {
        QueryCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
            super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
        }
    }
 
    @Override
    public CtsReferenceSeqExpr referenceSeq(CtsReferenceExpr... items) {
        return new ReferenceSeqListImpl(items);
    }
    static class ReferenceSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements CtsReferenceSeqExpr {
        ReferenceSeqListImpl(Object[] items) {
            super(BaseTypeImpl.convertList(items));
        }
    }
    static class ReferenceSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements CtsReferenceSeqExpr {
        ReferenceSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
            super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
        }
    }
    static class ReferenceCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements CtsReferenceExpr {
        ReferenceCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
            super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
        }
    }
 
    @Override
    public CtsRegionSeqExpr regionSeq(CtsRegionExpr... items) {
        return new RegionSeqListImpl(items);
    }
    static class RegionSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements CtsRegionSeqExpr {
        RegionSeqListImpl(Object[] items) {
            super(BaseTypeImpl.convertList(items));
        }
    }
    static class RegionSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements CtsRegionSeqExpr {
        RegionSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
            super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
        }
    }
    static class RegionCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements CtsRegionExpr {
        RegionCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
            super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
        }
    }

    }
