/*
 * Copyright 2016 MarkLogic Corporation
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

// TODO: single import
import com.marklogic.client.expression.BaseType;
import com.marklogic.client.expression.Xs;

import com.marklogic.client.expression.CtsQuery;
import com.marklogic.client.expression.XsValue;

import com.marklogic.client.impl.BaseTypeImpl;

// IMPORTANT: Do not edit. This file is generated.

public class CtsQueryExprImpl implements CtsQuery {
    private XsValue xs = null;
    public CtsQueryExprImpl(XsValue xs) {
        this.xs = xs;
    }
     @Override
        public CtsQuery.QueryExpr andNotQuery(CtsQuery.QueryExpr positiveQuery, CtsQuery.QueryExpr negativeQuery) {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "and-not-query", new Object[]{ positiveQuery, negativeQuery });
    }
    @Override
        public CtsQuery.QueryExpr andQuery(CtsQuery.QueryExpr... queries) {
        return andQuery(queries); 
    }
    @Override
        public CtsQuery.QueryExpr andQuery(CtsQuery.QuerySeqExpr queries) {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "and-query", new Object[]{ queries });
    }
    @Override
        public CtsQuery.QueryExpr andQuery(CtsQuery.QuerySeqExpr queries, String... options) {
        return andQuery(queries, (options == null) ? null : xs.strings(options)); 
    }
    @Override
        public CtsQuery.QueryExpr andQuery(CtsQuery.QuerySeqExpr queries, XsValue.StringSeqVal options) {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "and-query", new Object[]{ queries, options });
    }
    @Override
        public CtsQuery.QueryExpr boostQuery(CtsQuery.QueryExpr matchingQuery, CtsQuery.QueryExpr boostingQuery) {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "boost-query", new Object[]{ matchingQuery, boostingQuery });
    }
    @Override
        public CtsQuery.BoxExpr box(double south, double west, double north, double east) {
        return box(xs.doubleVal(south), xs.doubleVal(west), xs.doubleVal(north), xs.doubleVal(east)); 
    }
    @Override
        public CtsQuery.BoxExpr box(XsValue.DoubleVal south, XsValue.DoubleVal west, XsValue.DoubleVal north, XsValue.DoubleVal east) {
        return new CtsQueryExprImpl.BoxCallImpl("cts", "box", new Object[]{ south, west, north, east });
    }
    @Override
        public CtsQuery.CircleExpr circle(double radius, CtsQuery.PointExpr center) {
        return circle(xs.doubleVal(radius), center); 
    }
    @Override
        public CtsQuery.CircleExpr circle(XsValue.DoubleVal radius, CtsQuery.PointExpr center) {
        return new CtsQueryExprImpl.CircleCallImpl("cts", "circle", new Object[]{ radius, center });
    }
    @Override
        public CtsQuery.QueryExpr collectionQuery(String... uris) {
        return collectionQuery((uris == null) ? null : xs.strings(uris)); 
    }
    @Override
        public CtsQuery.QueryExpr collectionQuery(XsValue.StringSeqVal uris) {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "collection-query", new Object[]{ uris });
    }
    @Override
        public CtsQuery.QueryExpr directoryQuery(String... uris) {
        return directoryQuery((uris == null) ? null : xs.strings(uris)); 
    }
    @Override
        public CtsQuery.QueryExpr directoryQuery(XsValue.StringSeqVal uris) {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "directory-query", new Object[]{ uris });
    }
    @Override
        public CtsQuery.QueryExpr directoryQuery(String uris, String depth) {
        return directoryQuery((uris == null) ? null : xs.strings(uris), (depth == null) ? null : xs.string(depth)); 
    }
    @Override
        public CtsQuery.QueryExpr directoryQuery(XsValue.StringSeqVal uris, XsValue.StringVal depth) {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "directory-query", new Object[]{ uris, depth });
    }
    @Override
        public CtsQuery.QueryExpr documentFragmentQuery(CtsQuery.QueryExpr query) {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "document-fragment-query", new Object[]{ query });
    }
    @Override
        public CtsQuery.QueryExpr documentQuery(String... uris) {
        return documentQuery((uris == null) ? null : xs.strings(uris)); 
    }
    @Override
        public CtsQuery.QueryExpr documentQuery(XsValue.StringSeqVal uris) {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "document-query", new Object[]{ uris });
    }
    @Override
        public CtsQuery.QueryExpr elementAttributePairGeospatialQuery(XsValue.QNameSeqVal elementName, XsValue.QNameSeqVal latitudeAttributeNames, XsValue.QNameSeqVal longitudeAttributeNames, CtsQuery.RegionSeqExpr regions) {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "element-attribute-pair-geospatial-query", new Object[]{ elementName, latitudeAttributeNames, longitudeAttributeNames, regions });
    }
    @Override
        public CtsQuery.QueryExpr elementAttributePairGeospatialQuery(XsValue.QNameSeqVal elementName, XsValue.QNameSeqVal latitudeAttributeNames, XsValue.QNameSeqVal longitudeAttributeNames, CtsQuery.RegionSeqExpr regions, String... options) {
        return elementAttributePairGeospatialQuery(elementName, latitudeAttributeNames, longitudeAttributeNames, regions, (options == null) ? null : xs.strings(options)); 
    }
    @Override
        public CtsQuery.QueryExpr elementAttributePairGeospatialQuery(XsValue.QNameSeqVal elementName, XsValue.QNameSeqVal latitudeAttributeNames, XsValue.QNameSeqVal longitudeAttributeNames, CtsQuery.RegionSeqExpr regions, XsValue.StringSeqVal options) {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "element-attribute-pair-geospatial-query", new Object[]{ elementName, latitudeAttributeNames, longitudeAttributeNames, regions, options });
    }
    @Override
        public CtsQuery.QueryExpr elementAttributePairGeospatialQuery(XsValue.QNameSeqVal elementName, XsValue.QNameSeqVal latitudeAttributeNames, XsValue.QNameSeqVal longitudeAttributeNames, CtsQuery.RegionSeqExpr regions, String options, double weight) {
        return elementAttributePairGeospatialQuery(elementName, latitudeAttributeNames, longitudeAttributeNames, regions, (options == null) ? null : xs.strings(options), xs.doubleVal(weight)); 
    }
    @Override
        public CtsQuery.QueryExpr elementAttributePairGeospatialQuery(XsValue.QNameSeqVal elementName, XsValue.QNameSeqVal latitudeAttributeNames, XsValue.QNameSeqVal longitudeAttributeNames, CtsQuery.RegionSeqExpr regions, XsValue.StringSeqVal options, XsValue.DoubleVal weight) {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "element-attribute-pair-geospatial-query", new Object[]{ elementName, latitudeAttributeNames, longitudeAttributeNames, regions, options, weight });
    }
    @Override
        public CtsQuery.QueryExpr elementAttributeRangeQuery(XsValue.QNameSeqVal elementName, XsValue.QNameSeqVal attributeName, String operator, XsValue.AnyAtomicTypeVal... value) {
        return elementAttributeRangeQuery(elementName, attributeName, xs.string(operator), XsValueImpl.anyAtomicTypes(value)); 
    }
    @Override
        public CtsQuery.QueryExpr elementAttributeRangeQuery(XsValue.QNameSeqVal elementName, XsValue.QNameSeqVal attributeName, XsValue.StringVal operator, XsValue.AnyAtomicTypeSeqVal value) {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "element-attribute-range-query", new Object[]{ elementName, attributeName, operator, value });
    }
    @Override
        public CtsQuery.QueryExpr elementAttributeRangeQuery(XsValue.QNameSeqVal elementName, XsValue.QNameSeqVal attributeName, String operator, XsValue.AnyAtomicTypeSeqVal value, String... options) {
        return elementAttributeRangeQuery(elementName, attributeName, xs.string(operator), value, (options == null) ? null : xs.strings(options)); 
    }
    @Override
        public CtsQuery.QueryExpr elementAttributeRangeQuery(XsValue.QNameSeqVal elementName, XsValue.QNameSeqVal attributeName, XsValue.StringVal operator, XsValue.AnyAtomicTypeSeqVal value, XsValue.StringSeqVal options) {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "element-attribute-range-query", new Object[]{ elementName, attributeName, operator, value, options });
    }
    @Override
        public CtsQuery.QueryExpr elementAttributeRangeQuery(XsValue.QNameSeqVal elementName, XsValue.QNameSeqVal attributeName, String operator, XsValue.AnyAtomicTypeSeqVal value, String options, double weight) {
        return elementAttributeRangeQuery(elementName, attributeName, xs.string(operator), value, (options == null) ? null : xs.strings(options), xs.doubleVal(weight)); 
    }
    @Override
        public CtsQuery.QueryExpr elementAttributeRangeQuery(XsValue.QNameSeqVal elementName, XsValue.QNameSeqVal attributeName, XsValue.StringVal operator, XsValue.AnyAtomicTypeSeqVal value, XsValue.StringSeqVal options, XsValue.DoubleVal weight) {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "element-attribute-range-query", new Object[]{ elementName, attributeName, operator, value, options, weight });
    }
    @Override
        public CtsQuery.QueryExpr elementAttributeValueQuery(XsValue.QNameSeqVal elementName, XsValue.QNameSeqVal attributeName, String... text) {
        return elementAttributeValueQuery(elementName, attributeName, (text == null) ? null : xs.strings(text)); 
    }
    @Override
        public CtsQuery.QueryExpr elementAttributeValueQuery(XsValue.QNameSeqVal elementName, XsValue.QNameSeqVal attributeName, XsValue.StringSeqVal text) {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "element-attribute-value-query", new Object[]{ elementName, attributeName, text });
    }
    @Override
        public CtsQuery.QueryExpr elementAttributeValueQuery(XsValue.QNameSeqVal elementName, XsValue.QNameSeqVal attributeName, String text, String... options) {
        return elementAttributeValueQuery(elementName, attributeName, (text == null) ? null : xs.strings(text), (options == null) ? null : xs.strings(options)); 
    }
    @Override
        public CtsQuery.QueryExpr elementAttributeValueQuery(XsValue.QNameSeqVal elementName, XsValue.QNameSeqVal attributeName, XsValue.StringSeqVal text, XsValue.StringSeqVal options) {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "element-attribute-value-query", new Object[]{ elementName, attributeName, text, options });
    }
    @Override
        public CtsQuery.QueryExpr elementAttributeValueQuery(XsValue.QNameSeqVal elementName, XsValue.QNameSeqVal attributeName, String text, String options, double weight) {
        return elementAttributeValueQuery(elementName, attributeName, (text == null) ? null : xs.strings(text), (options == null) ? null : xs.strings(options), xs.doubleVal(weight)); 
    }
    @Override
        public CtsQuery.QueryExpr elementAttributeValueQuery(XsValue.QNameSeqVal elementName, XsValue.QNameSeqVal attributeName, XsValue.StringSeqVal text, XsValue.StringSeqVal options, XsValue.DoubleVal weight) {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "element-attribute-value-query", new Object[]{ elementName, attributeName, text, options, weight });
    }
    @Override
        public CtsQuery.QueryExpr elementAttributeWordQuery(XsValue.QNameSeqVal elementName, XsValue.QNameSeqVal attributeName) {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "element-attribute-word-query", new Object[]{ elementName, attributeName });
    }
    @Override
        public CtsQuery.QueryExpr elementAttributeWordQuery(XsValue.QNameSeqVal elementName, XsValue.QNameSeqVal attributeName, String... text) {
        return elementAttributeWordQuery(elementName, attributeName, (text == null) ? null : xs.strings(text)); 
    }
    @Override
        public CtsQuery.QueryExpr elementAttributeWordQuery(XsValue.QNameSeqVal elementName, XsValue.QNameSeqVal attributeName, XsValue.StringSeqVal text) {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "element-attribute-word-query", new Object[]{ elementName, attributeName, text });
    }
    @Override
        public CtsQuery.QueryExpr elementAttributeWordQuery(XsValue.QNameSeqVal elementName, XsValue.QNameSeqVal attributeName, String text, String... options) {
        return elementAttributeWordQuery(elementName, attributeName, (text == null) ? null : xs.strings(text), (options == null) ? null : xs.strings(options)); 
    }
    @Override
        public CtsQuery.QueryExpr elementAttributeWordQuery(XsValue.QNameSeqVal elementName, XsValue.QNameSeqVal attributeName, XsValue.StringSeqVal text, XsValue.StringSeqVal options) {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "element-attribute-word-query", new Object[]{ elementName, attributeName, text, options });
    }
    @Override
        public CtsQuery.QueryExpr elementAttributeWordQuery(XsValue.QNameSeqVal elementName, XsValue.QNameSeqVal attributeName, String text, String options, double weight) {
        return elementAttributeWordQuery(elementName, attributeName, (text == null) ? null : xs.strings(text), (options == null) ? null : xs.strings(options), xs.doubleVal(weight)); 
    }
    @Override
        public CtsQuery.QueryExpr elementAttributeWordQuery(XsValue.QNameSeqVal elementName, XsValue.QNameSeqVal attributeName, XsValue.StringSeqVal text, XsValue.StringSeqVal options, XsValue.DoubleVal weight) {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "element-attribute-word-query", new Object[]{ elementName, attributeName, text, options, weight });
    }
    @Override
        public CtsQuery.QueryExpr elementChildGeospatialQuery(XsValue.QNameSeqVal parentElementName, XsValue.QNameSeqVal childElementNames, CtsQuery.RegionSeqExpr regions) {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "element-child-geospatial-query", new Object[]{ parentElementName, childElementNames, regions });
    }
    @Override
        public CtsQuery.QueryExpr elementChildGeospatialQuery(XsValue.QNameSeqVal parentElementName, XsValue.QNameSeqVal childElementNames, CtsQuery.RegionSeqExpr regions, String... options) {
        return elementChildGeospatialQuery(parentElementName, childElementNames, regions, (options == null) ? null : xs.strings(options)); 
    }
    @Override
        public CtsQuery.QueryExpr elementChildGeospatialQuery(XsValue.QNameSeqVal parentElementName, XsValue.QNameSeqVal childElementNames, CtsQuery.RegionSeqExpr regions, XsValue.StringSeqVal options) {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "element-child-geospatial-query", new Object[]{ parentElementName, childElementNames, regions, options });
    }
    @Override
        public CtsQuery.QueryExpr elementChildGeospatialQuery(XsValue.QNameSeqVal parentElementName, XsValue.QNameSeqVal childElementNames, CtsQuery.RegionSeqExpr regions, String options, double weight) {
        return elementChildGeospatialQuery(parentElementName, childElementNames, regions, (options == null) ? null : xs.strings(options), xs.doubleVal(weight)); 
    }
    @Override
        public CtsQuery.QueryExpr elementChildGeospatialQuery(XsValue.QNameSeqVal parentElementName, XsValue.QNameSeqVal childElementNames, CtsQuery.RegionSeqExpr regions, XsValue.StringSeqVal options, XsValue.DoubleVal weight) {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "element-child-geospatial-query", new Object[]{ parentElementName, childElementNames, regions, options, weight });
    }
    @Override
        public CtsQuery.QueryExpr elementGeospatialQuery(XsValue.QNameSeqVal elementName, CtsQuery.RegionSeqExpr regions) {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "element-geospatial-query", new Object[]{ elementName, regions });
    }
    @Override
        public CtsQuery.QueryExpr elementGeospatialQuery(XsValue.QNameSeqVal elementName, CtsQuery.RegionSeqExpr regions, String... options) {
        return elementGeospatialQuery(elementName, regions, (options == null) ? null : xs.strings(options)); 
    }
    @Override
        public CtsQuery.QueryExpr elementGeospatialQuery(XsValue.QNameSeqVal elementName, CtsQuery.RegionSeqExpr regions, XsValue.StringSeqVal options) {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "element-geospatial-query", new Object[]{ elementName, regions, options });
    }
    @Override
        public CtsQuery.QueryExpr elementGeospatialQuery(XsValue.QNameSeqVal elementName, CtsQuery.RegionSeqExpr regions, String options, double weight) {
        return elementGeospatialQuery(elementName, regions, (options == null) ? null : xs.strings(options), xs.doubleVal(weight)); 
    }
    @Override
        public CtsQuery.QueryExpr elementGeospatialQuery(XsValue.QNameSeqVal elementName, CtsQuery.RegionSeqExpr regions, XsValue.StringSeqVal options, XsValue.DoubleVal weight) {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "element-geospatial-query", new Object[]{ elementName, regions, options, weight });
    }
    @Override
        public CtsQuery.QueryExpr elementPairGeospatialQuery(XsValue.QNameSeqVal elementName, XsValue.QNameSeqVal latitudeElementNames, XsValue.QNameSeqVal longitudeElementNames, CtsQuery.RegionSeqExpr regions) {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "element-pair-geospatial-query", new Object[]{ elementName, latitudeElementNames, longitudeElementNames, regions });
    }
    @Override
        public CtsQuery.QueryExpr elementPairGeospatialQuery(XsValue.QNameSeqVal elementName, XsValue.QNameSeqVal latitudeElementNames, XsValue.QNameSeqVal longitudeElementNames, CtsQuery.RegionSeqExpr regions, String... options) {
        return elementPairGeospatialQuery(elementName, latitudeElementNames, longitudeElementNames, regions, (options == null) ? null : xs.strings(options)); 
    }
    @Override
        public CtsQuery.QueryExpr elementPairGeospatialQuery(XsValue.QNameSeqVal elementName, XsValue.QNameSeqVal latitudeElementNames, XsValue.QNameSeqVal longitudeElementNames, CtsQuery.RegionSeqExpr regions, XsValue.StringSeqVal options) {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "element-pair-geospatial-query", new Object[]{ elementName, latitudeElementNames, longitudeElementNames, regions, options });
    }
    @Override
        public CtsQuery.QueryExpr elementPairGeospatialQuery(XsValue.QNameSeqVal elementName, XsValue.QNameSeqVal latitudeElementNames, XsValue.QNameSeqVal longitudeElementNames, CtsQuery.RegionSeqExpr regions, String options, double weight) {
        return elementPairGeospatialQuery(elementName, latitudeElementNames, longitudeElementNames, regions, (options == null) ? null : xs.strings(options), xs.doubleVal(weight)); 
    }
    @Override
        public CtsQuery.QueryExpr elementPairGeospatialQuery(XsValue.QNameSeqVal elementName, XsValue.QNameSeqVal latitudeElementNames, XsValue.QNameSeqVal longitudeElementNames, CtsQuery.RegionSeqExpr regions, XsValue.StringSeqVal options, XsValue.DoubleVal weight) {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "element-pair-geospatial-query", new Object[]{ elementName, latitudeElementNames, longitudeElementNames, regions, options, weight });
    }
    @Override
        public CtsQuery.QueryExpr elementQuery(XsValue.QNameSeqVal elementName, CtsQuery.QueryExpr query) {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "element-query", new Object[]{ elementName, query });
    }
    @Override
        public CtsQuery.QueryExpr elementRangeQuery(XsValue.QNameSeqVal elementName, String operator, XsValue.AnyAtomicTypeVal... value) {
        return elementRangeQuery(elementName, xs.string(operator), XsValueImpl.anyAtomicTypes(value)); 
    }
    @Override
        public CtsQuery.QueryExpr elementRangeQuery(XsValue.QNameSeqVal elementName, XsValue.StringVal operator, XsValue.AnyAtomicTypeSeqVal value) {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "element-range-query", new Object[]{ elementName, operator, value });
    }
    @Override
        public CtsQuery.QueryExpr elementRangeQuery(XsValue.QNameSeqVal elementName, String operator, XsValue.AnyAtomicTypeSeqVal value, String... options) {
        return elementRangeQuery(elementName, xs.string(operator), value, (options == null) ? null : xs.strings(options)); 
    }
    @Override
        public CtsQuery.QueryExpr elementRangeQuery(XsValue.QNameSeqVal elementName, XsValue.StringVal operator, XsValue.AnyAtomicTypeSeqVal value, XsValue.StringSeqVal options) {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "element-range-query", new Object[]{ elementName, operator, value, options });
    }
    @Override
        public CtsQuery.QueryExpr elementRangeQuery(XsValue.QNameSeqVal elementName, String operator, XsValue.AnyAtomicTypeSeqVal value, String options, double weight) {
        return elementRangeQuery(elementName, xs.string(operator), value, (options == null) ? null : xs.strings(options), xs.doubleVal(weight)); 
    }
    @Override
        public CtsQuery.QueryExpr elementRangeQuery(XsValue.QNameSeqVal elementName, XsValue.StringVal operator, XsValue.AnyAtomicTypeSeqVal value, XsValue.StringSeqVal options, XsValue.DoubleVal weight) {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "element-range-query", new Object[]{ elementName, operator, value, options, weight });
    }
    @Override
        public CtsQuery.QueryExpr elementValueQuery(XsValue.QNameSeqVal elementName) {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "element-value-query", new Object[]{ elementName });
    }
    @Override
        public CtsQuery.QueryExpr elementValueQuery(XsValue.QNameSeqVal elementName, String... text) {
        return elementValueQuery(elementName, (text == null) ? null : xs.strings(text)); 
    }
    @Override
        public CtsQuery.QueryExpr elementValueQuery(XsValue.QNameSeqVal elementName, XsValue.StringSeqVal text) {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "element-value-query", new Object[]{ elementName, text });
    }
    @Override
        public CtsQuery.QueryExpr elementValueQuery(XsValue.QNameSeqVal elementName, String text, String... options) {
        return elementValueQuery(elementName, (text == null) ? null : xs.strings(text), (options == null) ? null : xs.strings(options)); 
    }
    @Override
        public CtsQuery.QueryExpr elementValueQuery(XsValue.QNameSeqVal elementName, XsValue.StringSeqVal text, XsValue.StringSeqVal options) {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "element-value-query", new Object[]{ elementName, text, options });
    }
    @Override
        public CtsQuery.QueryExpr elementValueQuery(XsValue.QNameSeqVal elementName, String text, String options, double weight) {
        return elementValueQuery(elementName, (text == null) ? null : xs.strings(text), (options == null) ? null : xs.strings(options), xs.doubleVal(weight)); 
    }
    @Override
        public CtsQuery.QueryExpr elementValueQuery(XsValue.QNameSeqVal elementName, XsValue.StringSeqVal text, XsValue.StringSeqVal options, XsValue.DoubleVal weight) {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "element-value-query", new Object[]{ elementName, text, options, weight });
    }
    @Override
        public CtsQuery.QueryExpr elementWordQuery(XsValue.QNameSeqVal elementName, String... text) {
        return elementWordQuery(elementName, (text == null) ? null : xs.strings(text)); 
    }
    @Override
        public CtsQuery.QueryExpr elementWordQuery(XsValue.QNameSeqVal elementName, XsValue.StringSeqVal text) {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "element-word-query", new Object[]{ elementName, text });
    }
    @Override
        public CtsQuery.QueryExpr elementWordQuery(XsValue.QNameSeqVal elementName, String text, String... options) {
        return elementWordQuery(elementName, (text == null) ? null : xs.strings(text), (options == null) ? null : xs.strings(options)); 
    }
    @Override
        public CtsQuery.QueryExpr elementWordQuery(XsValue.QNameSeqVal elementName, XsValue.StringSeqVal text, XsValue.StringSeqVal options) {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "element-word-query", new Object[]{ elementName, text, options });
    }
    @Override
        public CtsQuery.QueryExpr elementWordQuery(XsValue.QNameSeqVal elementName, String text, String options, double weight) {
        return elementWordQuery(elementName, (text == null) ? null : xs.strings(text), (options == null) ? null : xs.strings(options), xs.doubleVal(weight)); 
    }
    @Override
        public CtsQuery.QueryExpr elementWordQuery(XsValue.QNameSeqVal elementName, XsValue.StringSeqVal text, XsValue.StringSeqVal options, XsValue.DoubleVal weight) {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "element-word-query", new Object[]{ elementName, text, options, weight });
    }
    @Override
    public CtsQuery.QueryExpr falseQuery() {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "false-query", null);
    }
    @Override
        public CtsQuery.QueryExpr fieldRangeQuery(String fieldName, String operator, XsValue.AnyAtomicTypeVal... value) {
        return fieldRangeQuery((fieldName == null) ? null : xs.strings(fieldName), xs.string(operator), XsValueImpl.anyAtomicTypes(value)); 
    }
    @Override
        public CtsQuery.QueryExpr fieldRangeQuery(XsValue.StringSeqVal fieldName, XsValue.StringVal operator, XsValue.AnyAtomicTypeSeqVal value) {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "field-range-query", new Object[]{ fieldName, operator, value });
    }
    @Override
        public CtsQuery.QueryExpr fieldRangeQuery(String fieldName, String operator, XsValue.AnyAtomicTypeSeqVal value, String... options) {
        return fieldRangeQuery((fieldName == null) ? null : xs.strings(fieldName), xs.string(operator), value, (options == null) ? null : xs.strings(options)); 
    }
    @Override
        public CtsQuery.QueryExpr fieldRangeQuery(XsValue.StringSeqVal fieldName, XsValue.StringVal operator, XsValue.AnyAtomicTypeSeqVal value, XsValue.StringSeqVal options) {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "field-range-query", new Object[]{ fieldName, operator, value, options });
    }
    @Override
        public CtsQuery.QueryExpr fieldRangeQuery(String fieldName, String operator, XsValue.AnyAtomicTypeSeqVal value, String options, double weight) {
        return fieldRangeQuery((fieldName == null) ? null : xs.strings(fieldName), xs.string(operator), value, (options == null) ? null : xs.strings(options), xs.doubleVal(weight)); 
    }
    @Override
        public CtsQuery.QueryExpr fieldRangeQuery(XsValue.StringSeqVal fieldName, XsValue.StringVal operator, XsValue.AnyAtomicTypeSeqVal value, XsValue.StringSeqVal options, XsValue.DoubleVal weight) {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "field-range-query", new Object[]{ fieldName, operator, value, options, weight });
    }
    @Override
        public CtsQuery.QueryExpr fieldValueQuery(String fieldName, XsValue.AnyAtomicTypeVal... text) {
        return fieldValueQuery((fieldName == null) ? null : xs.strings(fieldName), XsValueImpl.anyAtomicTypes(text)); 
    }
    @Override
        public CtsQuery.QueryExpr fieldValueQuery(XsValue.StringSeqVal fieldName, XsValue.AnyAtomicTypeSeqVal text) {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "field-value-query", new Object[]{ fieldName, text });
    }
    @Override
        public CtsQuery.QueryExpr fieldValueQuery(String fieldName, XsValue.AnyAtomicTypeSeqVal text, String... options) {
        return fieldValueQuery((fieldName == null) ? null : xs.strings(fieldName), text, (options == null) ? null : xs.strings(options)); 
    }
    @Override
        public CtsQuery.QueryExpr fieldValueQuery(XsValue.StringSeqVal fieldName, XsValue.AnyAtomicTypeSeqVal text, XsValue.StringSeqVal options) {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "field-value-query", new Object[]{ fieldName, text, options });
    }
    @Override
        public CtsQuery.QueryExpr fieldValueQuery(String fieldName, XsValue.AnyAtomicTypeSeqVal text, String options, double weight) {
        return fieldValueQuery((fieldName == null) ? null : xs.strings(fieldName), text, (options == null) ? null : xs.strings(options), xs.doubleVal(weight)); 
    }
    @Override
        public CtsQuery.QueryExpr fieldValueQuery(XsValue.StringSeqVal fieldName, XsValue.AnyAtomicTypeSeqVal text, XsValue.StringSeqVal options, XsValue.DoubleVal weight) {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "field-value-query", new Object[]{ fieldName, text, options, weight });
    }
    @Override
        public CtsQuery.QueryExpr fieldWordQuery(String fieldName, String... text) {
        return fieldWordQuery((fieldName == null) ? null : xs.strings(fieldName), (text == null) ? null : xs.strings(text)); 
    }
    @Override
        public CtsQuery.QueryExpr fieldWordQuery(XsValue.StringSeqVal fieldName, XsValue.StringSeqVal text) {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "field-word-query", new Object[]{ fieldName, text });
    }
    @Override
        public CtsQuery.QueryExpr fieldWordQuery(String fieldName, String text, String... options) {
        return fieldWordQuery((fieldName == null) ? null : xs.strings(fieldName), (text == null) ? null : xs.strings(text), (options == null) ? null : xs.strings(options)); 
    }
    @Override
        public CtsQuery.QueryExpr fieldWordQuery(XsValue.StringSeqVal fieldName, XsValue.StringSeqVal text, XsValue.StringSeqVal options) {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "field-word-query", new Object[]{ fieldName, text, options });
    }
    @Override
        public CtsQuery.QueryExpr fieldWordQuery(String fieldName, String text, String options, double weight) {
        return fieldWordQuery((fieldName == null) ? null : xs.strings(fieldName), (text == null) ? null : xs.strings(text), (options == null) ? null : xs.strings(options), xs.doubleVal(weight)); 
    }
    @Override
        public CtsQuery.QueryExpr fieldWordQuery(XsValue.StringSeqVal fieldName, XsValue.StringSeqVal text, XsValue.StringSeqVal options, XsValue.DoubleVal weight) {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "field-word-query", new Object[]{ fieldName, text, options, weight });
    }
    @Override
        public CtsQuery.QueryExpr jsonPropertyChildGeospatialQuery(String parentPropertyName, String childPropertyNames, CtsQuery.RegionExpr... regions) {
        return jsonPropertyChildGeospatialQuery((parentPropertyName == null) ? null : xs.strings(parentPropertyName), (childPropertyNames == null) ? null : xs.strings(childPropertyNames), this.region(regions)); 
    }
    @Override
        public CtsQuery.QueryExpr jsonPropertyChildGeospatialQuery(XsValue.StringSeqVal parentPropertyName, XsValue.StringSeqVal childPropertyNames, CtsQuery.RegionSeqExpr regions) {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "json-property-child-geospatial-query", new Object[]{ parentPropertyName, childPropertyNames, regions });
    }
    @Override
        public CtsQuery.QueryExpr jsonPropertyChildGeospatialQuery(String parentPropertyName, String childPropertyNames, CtsQuery.RegionSeqExpr regions, String... options) {
        return jsonPropertyChildGeospatialQuery((parentPropertyName == null) ? null : xs.strings(parentPropertyName), (childPropertyNames == null) ? null : xs.strings(childPropertyNames), regions, (options == null) ? null : xs.strings(options)); 
    }
    @Override
        public CtsQuery.QueryExpr jsonPropertyChildGeospatialQuery(XsValue.StringSeqVal parentPropertyName, XsValue.StringSeqVal childPropertyNames, CtsQuery.RegionSeqExpr regions, XsValue.StringSeqVal options) {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "json-property-child-geospatial-query", new Object[]{ parentPropertyName, childPropertyNames, regions, options });
    }
    @Override
        public CtsQuery.QueryExpr jsonPropertyChildGeospatialQuery(String parentPropertyName, String childPropertyNames, CtsQuery.RegionSeqExpr regions, String options, double weight) {
        return jsonPropertyChildGeospatialQuery((parentPropertyName == null) ? null : xs.strings(parentPropertyName), (childPropertyNames == null) ? null : xs.strings(childPropertyNames), regions, (options == null) ? null : xs.strings(options), xs.doubleVal(weight)); 
    }
    @Override
        public CtsQuery.QueryExpr jsonPropertyChildGeospatialQuery(XsValue.StringSeqVal parentPropertyName, XsValue.StringSeqVal childPropertyNames, CtsQuery.RegionSeqExpr regions, XsValue.StringSeqVal options, XsValue.DoubleVal weight) {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "json-property-child-geospatial-query", new Object[]{ parentPropertyName, childPropertyNames, regions, options, weight });
    }
    @Override
        public CtsQuery.QueryExpr jsonPropertyGeospatialQuery(String propertyName, CtsQuery.RegionExpr... regions) {
        return jsonPropertyGeospatialQuery((propertyName == null) ? null : xs.strings(propertyName), this.region(regions)); 
    }
    @Override
        public CtsQuery.QueryExpr jsonPropertyGeospatialQuery(XsValue.StringSeqVal propertyName, CtsQuery.RegionSeqExpr regions) {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "json-property-geospatial-query", new Object[]{ propertyName, regions });
    }
    @Override
        public CtsQuery.QueryExpr jsonPropertyGeospatialQuery(String propertyName, CtsQuery.RegionSeqExpr regions, String... options) {
        return jsonPropertyGeospatialQuery((propertyName == null) ? null : xs.strings(propertyName), regions, (options == null) ? null : xs.strings(options)); 
    }
    @Override
        public CtsQuery.QueryExpr jsonPropertyGeospatialQuery(XsValue.StringSeqVal propertyName, CtsQuery.RegionSeqExpr regions, XsValue.StringSeqVal options) {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "json-property-geospatial-query", new Object[]{ propertyName, regions, options });
    }
    @Override
        public CtsQuery.QueryExpr jsonPropertyGeospatialQuery(String propertyName, CtsQuery.RegionSeqExpr regions, String options, double weight) {
        return jsonPropertyGeospatialQuery((propertyName == null) ? null : xs.strings(propertyName), regions, (options == null) ? null : xs.strings(options), xs.doubleVal(weight)); 
    }
    @Override
        public CtsQuery.QueryExpr jsonPropertyGeospatialQuery(XsValue.StringSeqVal propertyName, CtsQuery.RegionSeqExpr regions, XsValue.StringSeqVal options, XsValue.DoubleVal weight) {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "json-property-geospatial-query", new Object[]{ propertyName, regions, options, weight });
    }
    @Override
        public CtsQuery.QueryExpr jsonPropertyPairGeospatialQuery(String propertyName, String latitudePropertyNames, String longitudePropertyNames, CtsQuery.RegionExpr... regions) {
        return jsonPropertyPairGeospatialQuery((propertyName == null) ? null : xs.strings(propertyName), (latitudePropertyNames == null) ? null : xs.strings(latitudePropertyNames), (longitudePropertyNames == null) ? null : xs.strings(longitudePropertyNames), this.region(regions)); 
    }
    @Override
        public CtsQuery.QueryExpr jsonPropertyPairGeospatialQuery(XsValue.StringSeqVal propertyName, XsValue.StringSeqVal latitudePropertyNames, XsValue.StringSeqVal longitudePropertyNames, CtsQuery.RegionSeqExpr regions) {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "json-property-pair-geospatial-query", new Object[]{ propertyName, latitudePropertyNames, longitudePropertyNames, regions });
    }
    @Override
        public CtsQuery.QueryExpr jsonPropertyPairGeospatialQuery(String propertyName, String latitudePropertyNames, String longitudePropertyNames, CtsQuery.RegionSeqExpr regions, String... options) {
        return jsonPropertyPairGeospatialQuery((propertyName == null) ? null : xs.strings(propertyName), (latitudePropertyNames == null) ? null : xs.strings(latitudePropertyNames), (longitudePropertyNames == null) ? null : xs.strings(longitudePropertyNames), regions, (options == null) ? null : xs.strings(options)); 
    }
    @Override
        public CtsQuery.QueryExpr jsonPropertyPairGeospatialQuery(XsValue.StringSeqVal propertyName, XsValue.StringSeqVal latitudePropertyNames, XsValue.StringSeqVal longitudePropertyNames, CtsQuery.RegionSeqExpr regions, XsValue.StringSeqVal options) {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "json-property-pair-geospatial-query", new Object[]{ propertyName, latitudePropertyNames, longitudePropertyNames, regions, options });
    }
    @Override
        public CtsQuery.QueryExpr jsonPropertyPairGeospatialQuery(String propertyName, String latitudePropertyNames, String longitudePropertyNames, CtsQuery.RegionSeqExpr regions, String options, double weight) {
        return jsonPropertyPairGeospatialQuery((propertyName == null) ? null : xs.strings(propertyName), (latitudePropertyNames == null) ? null : xs.strings(latitudePropertyNames), (longitudePropertyNames == null) ? null : xs.strings(longitudePropertyNames), regions, (options == null) ? null : xs.strings(options), xs.doubleVal(weight)); 
    }
    @Override
        public CtsQuery.QueryExpr jsonPropertyPairGeospatialQuery(XsValue.StringSeqVal propertyName, XsValue.StringSeqVal latitudePropertyNames, XsValue.StringSeqVal longitudePropertyNames, CtsQuery.RegionSeqExpr regions, XsValue.StringSeqVal options, XsValue.DoubleVal weight) {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "json-property-pair-geospatial-query", new Object[]{ propertyName, latitudePropertyNames, longitudePropertyNames, regions, options, weight });
    }
    @Override
        public CtsQuery.QueryExpr jsonPropertyRangeQuery(String propertyName, String operator, XsValue.AnyAtomicTypeVal... value) {
        return jsonPropertyRangeQuery((propertyName == null) ? null : xs.strings(propertyName), xs.string(operator), XsValueImpl.anyAtomicTypes(value)); 
    }
    @Override
        public CtsQuery.QueryExpr jsonPropertyRangeQuery(XsValue.StringSeqVal propertyName, XsValue.StringVal operator, XsValue.AnyAtomicTypeSeqVal value) {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "json-property-range-query", new Object[]{ propertyName, operator, value });
    }
    @Override
        public CtsQuery.QueryExpr jsonPropertyRangeQuery(String propertyName, String operator, XsValue.AnyAtomicTypeSeqVal value, String... options) {
        return jsonPropertyRangeQuery((propertyName == null) ? null : xs.strings(propertyName), xs.string(operator), value, (options == null) ? null : xs.strings(options)); 
    }
    @Override
        public CtsQuery.QueryExpr jsonPropertyRangeQuery(XsValue.StringSeqVal propertyName, XsValue.StringVal operator, XsValue.AnyAtomicTypeSeqVal value, XsValue.StringSeqVal options) {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "json-property-range-query", new Object[]{ propertyName, operator, value, options });
    }
    @Override
        public CtsQuery.QueryExpr jsonPropertyRangeQuery(String propertyName, String operator, XsValue.AnyAtomicTypeSeqVal value, String options, double weight) {
        return jsonPropertyRangeQuery((propertyName == null) ? null : xs.strings(propertyName), xs.string(operator), value, (options == null) ? null : xs.strings(options), xs.doubleVal(weight)); 
    }
    @Override
        public CtsQuery.QueryExpr jsonPropertyRangeQuery(XsValue.StringSeqVal propertyName, XsValue.StringVal operator, XsValue.AnyAtomicTypeSeqVal value, XsValue.StringSeqVal options, XsValue.DoubleVal weight) {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "json-property-range-query", new Object[]{ propertyName, operator, value, options, weight });
    }
    @Override
        public CtsQuery.QueryExpr jsonPropertyScopeQuery(String propertyName, CtsQuery.QueryExpr query) {
        return jsonPropertyScopeQuery((propertyName == null) ? null : xs.strings(propertyName), query); 
    }
    @Override
        public CtsQuery.QueryExpr jsonPropertyScopeQuery(XsValue.StringSeqVal propertyName, CtsQuery.QueryExpr query) {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "json-property-scope-query", new Object[]{ propertyName, query });
    }
    @Override
        public CtsQuery.QueryExpr jsonPropertyValueQuery(String propertyName, XsValue.AnyAtomicTypeVal... value) {
        return jsonPropertyValueQuery((propertyName == null) ? null : xs.strings(propertyName), XsValueImpl.anyAtomicTypes(value)); 
    }
    @Override
        public CtsQuery.QueryExpr jsonPropertyValueQuery(XsValue.StringSeqVal propertyName, XsValue.AnyAtomicTypeSeqVal value) {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "json-property-value-query", new Object[]{ propertyName, value });
    }
    @Override
        public CtsQuery.QueryExpr jsonPropertyValueQuery(String propertyName, XsValue.AnyAtomicTypeSeqVal value, String... options) {
        return jsonPropertyValueQuery((propertyName == null) ? null : xs.strings(propertyName), value, (options == null) ? null : xs.strings(options)); 
    }
    @Override
        public CtsQuery.QueryExpr jsonPropertyValueQuery(XsValue.StringSeqVal propertyName, XsValue.AnyAtomicTypeSeqVal value, XsValue.StringSeqVal options) {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "json-property-value-query", new Object[]{ propertyName, value, options });
    }
    @Override
        public CtsQuery.QueryExpr jsonPropertyValueQuery(String propertyName, XsValue.AnyAtomicTypeSeqVal value, String options, double weight) {
        return jsonPropertyValueQuery((propertyName == null) ? null : xs.strings(propertyName), value, (options == null) ? null : xs.strings(options), xs.doubleVal(weight)); 
    }
    @Override
        public CtsQuery.QueryExpr jsonPropertyValueQuery(XsValue.StringSeqVal propertyName, XsValue.AnyAtomicTypeSeqVal value, XsValue.StringSeqVal options, XsValue.DoubleVal weight) {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "json-property-value-query", new Object[]{ propertyName, value, options, weight });
    }
    @Override
        public CtsQuery.QueryExpr jsonPropertyWordQuery(String propertyName, String... text) {
        return jsonPropertyWordQuery((propertyName == null) ? null : xs.strings(propertyName), (text == null) ? null : xs.strings(text)); 
    }
    @Override
        public CtsQuery.QueryExpr jsonPropertyWordQuery(XsValue.StringSeqVal propertyName, XsValue.StringSeqVal text) {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "json-property-word-query", new Object[]{ propertyName, text });
    }
    @Override
        public CtsQuery.QueryExpr jsonPropertyWordQuery(String propertyName, String text, String... options) {
        return jsonPropertyWordQuery((propertyName == null) ? null : xs.strings(propertyName), (text == null) ? null : xs.strings(text), (options == null) ? null : xs.strings(options)); 
    }
    @Override
        public CtsQuery.QueryExpr jsonPropertyWordQuery(XsValue.StringSeqVal propertyName, XsValue.StringSeqVal text, XsValue.StringSeqVal options) {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "json-property-word-query", new Object[]{ propertyName, text, options });
    }
    @Override
        public CtsQuery.QueryExpr jsonPropertyWordQuery(String propertyName, String text, String options, double weight) {
        return jsonPropertyWordQuery((propertyName == null) ? null : xs.strings(propertyName), (text == null) ? null : xs.strings(text), (options == null) ? null : xs.strings(options), xs.doubleVal(weight)); 
    }
    @Override
        public CtsQuery.QueryExpr jsonPropertyWordQuery(XsValue.StringSeqVal propertyName, XsValue.StringSeqVal text, XsValue.StringSeqVal options, XsValue.DoubleVal weight) {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "json-property-word-query", new Object[]{ propertyName, text, options, weight });
    }
    @Override
        public CtsQuery.QueryExpr locksFragmentQuery(CtsQuery.QueryExpr query) {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "locks-fragment-query", new Object[]{ query });
    }
    @Override
        public CtsQuery.QueryExpr locksQuery(CtsQuery.QueryExpr arg1) {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "locks-query", new Object[]{ arg1 });
    }
    @Override
        public CtsQuery.QueryExpr lsqtQuery(String temporalCollection) {
        return lsqtQuery(xs.string(temporalCollection)); 
    }
    @Override
        public CtsQuery.QueryExpr lsqtQuery(XsValue.StringVal temporalCollection) {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "lsqt-query", new Object[]{ temporalCollection });
    }
    @Override
        public CtsQuery.QueryExpr lsqtQuery(String temporalCollection, XsValue.DateTimeVal timestamp) {
        return lsqtQuery(xs.string(temporalCollection), timestamp); 
    }
    @Override
        public CtsQuery.QueryExpr lsqtQuery(XsValue.StringVal temporalCollection, XsValue.DateTimeVal timestamp) {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "lsqt-query", new Object[]{ temporalCollection, timestamp });
    }
    @Override
        public CtsQuery.QueryExpr lsqtQuery(String temporalCollection, XsValue.DateTimeVal timestamp, String... options) {
        return lsqtQuery(xs.string(temporalCollection), timestamp, (options == null) ? null : xs.strings(options)); 
    }
    @Override
        public CtsQuery.QueryExpr lsqtQuery(XsValue.StringVal temporalCollection, XsValue.DateTimeVal timestamp, XsValue.StringSeqVal options) {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "lsqt-query", new Object[]{ temporalCollection, timestamp, options });
    }
    @Override
        public CtsQuery.QueryExpr lsqtQuery(String temporalCollection, XsValue.DateTimeVal timestamp, String options, double weight) {
        return lsqtQuery(xs.string(temporalCollection), timestamp, (options == null) ? null : xs.strings(options), xs.doubleVal(weight)); 
    }
    @Override
        public CtsQuery.QueryExpr lsqtQuery(XsValue.StringVal temporalCollection, XsValue.DateTimeVal timestamp, XsValue.StringSeqVal options, XsValue.DoubleVal weight) {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "lsqt-query", new Object[]{ temporalCollection, timestamp, options, weight });
    }
    @Override
        public CtsQuery.QueryExpr nearQuery(CtsQuery.QueryExpr... queries) {
        return nearQuery(queries); 
    }
    @Override
        public CtsQuery.QueryExpr nearQuery(CtsQuery.QuerySeqExpr queries) {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "near-query", new Object[]{ queries });
    }
    @Override
        public CtsQuery.QueryExpr nearQuery(CtsQuery.QuerySeqExpr queries, double distance) {
        return nearQuery(queries, xs.doubleVal(distance)); 
    }
    @Override
        public CtsQuery.QueryExpr nearQuery(CtsQuery.QuerySeqExpr queries, XsValue.DoubleVal distance) {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "near-query", new Object[]{ queries, distance });
    }
    @Override
        public CtsQuery.QueryExpr nearQuery(CtsQuery.QuerySeqExpr queries, double distance, String... options) {
        return nearQuery(queries, xs.doubleVal(distance), (options == null) ? null : xs.strings(options)); 
    }
    @Override
        public CtsQuery.QueryExpr nearQuery(CtsQuery.QuerySeqExpr queries, XsValue.DoubleVal distance, XsValue.StringSeqVal options) {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "near-query", new Object[]{ queries, distance, options });
    }
    @Override
        public CtsQuery.QueryExpr nearQuery(CtsQuery.QuerySeqExpr queries, double distance, String options, double distanceWeight) {
        return nearQuery(queries, xs.doubleVal(distance), (options == null) ? null : xs.strings(options), xs.doubleVal(distanceWeight)); 
    }
    @Override
        public CtsQuery.QueryExpr nearQuery(CtsQuery.QuerySeqExpr queries, XsValue.DoubleVal distance, XsValue.StringSeqVal options, XsValue.DoubleVal distanceWeight) {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "near-query", new Object[]{ queries, distance, options, distanceWeight });
    }
    @Override
        public CtsQuery.QueryExpr notInQuery(CtsQuery.QueryExpr positiveQuery, CtsQuery.QueryExpr negativeQuery) {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "not-in-query", new Object[]{ positiveQuery, negativeQuery });
    }
    @Override
        public CtsQuery.QueryExpr notQuery(CtsQuery.QueryExpr query) {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "not-query", new Object[]{ query });
    }
    @Override
        public CtsQuery.QueryExpr orQuery(CtsQuery.QueryExpr... queries) {
        return orQuery(queries); 
    }
    @Override
        public CtsQuery.QueryExpr orQuery(CtsQuery.QuerySeqExpr queries) {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "or-query", new Object[]{ queries });
    }
    @Override
        public CtsQuery.QueryExpr orQuery(CtsQuery.QuerySeqExpr queries, String... options) {
        return orQuery(queries, (options == null) ? null : xs.strings(options)); 
    }
    @Override
        public CtsQuery.QueryExpr orQuery(CtsQuery.QuerySeqExpr queries, XsValue.StringSeqVal options) {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "or-query", new Object[]{ queries, options });
    }
    @Override
        public CtsQuery.QueryExpr pathGeospatialQuery(String pathExpression, CtsQuery.RegionExpr... regions) {
        return pathGeospatialQuery((pathExpression == null) ? null : xs.strings(pathExpression), this.region(regions)); 
    }
    @Override
        public CtsQuery.QueryExpr pathGeospatialQuery(XsValue.StringSeqVal pathExpression, CtsQuery.RegionSeqExpr regions) {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "path-geospatial-query", new Object[]{ pathExpression, regions });
    }
    @Override
        public CtsQuery.QueryExpr pathGeospatialQuery(String pathExpression, CtsQuery.RegionSeqExpr regions, String... options) {
        return pathGeospatialQuery((pathExpression == null) ? null : xs.strings(pathExpression), regions, (options == null) ? null : xs.strings(options)); 
    }
    @Override
        public CtsQuery.QueryExpr pathGeospatialQuery(XsValue.StringSeqVal pathExpression, CtsQuery.RegionSeqExpr regions, XsValue.StringSeqVal options) {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "path-geospatial-query", new Object[]{ pathExpression, regions, options });
    }
    @Override
        public CtsQuery.QueryExpr pathGeospatialQuery(String pathExpression, CtsQuery.RegionSeqExpr regions, String options, double weight) {
        return pathGeospatialQuery((pathExpression == null) ? null : xs.strings(pathExpression), regions, (options == null) ? null : xs.strings(options), xs.doubleVal(weight)); 
    }
    @Override
        public CtsQuery.QueryExpr pathGeospatialQuery(XsValue.StringSeqVal pathExpression, CtsQuery.RegionSeqExpr regions, XsValue.StringSeqVal options, XsValue.DoubleVal weight) {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "path-geospatial-query", new Object[]{ pathExpression, regions, options, weight });
    }
    @Override
        public CtsQuery.QueryExpr pathRangeQuery(String pathExpression, String operator, XsValue.AnyAtomicTypeVal... value) {
        return pathRangeQuery((pathExpression == null) ? null : xs.strings(pathExpression), xs.string(operator), XsValueImpl.anyAtomicTypes(value)); 
    }
    @Override
        public CtsQuery.QueryExpr pathRangeQuery(XsValue.StringSeqVal pathExpression, XsValue.StringVal operator, XsValue.AnyAtomicTypeSeqVal value) {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "path-range-query", new Object[]{ pathExpression, operator, value });
    }
    @Override
        public CtsQuery.QueryExpr pathRangeQuery(String pathExpression, String operator, XsValue.AnyAtomicTypeSeqVal value, String... options) {
        return pathRangeQuery((pathExpression == null) ? null : xs.strings(pathExpression), xs.string(operator), value, (options == null) ? null : xs.strings(options)); 
    }
    @Override
        public CtsQuery.QueryExpr pathRangeQuery(XsValue.StringSeqVal pathExpression, XsValue.StringVal operator, XsValue.AnyAtomicTypeSeqVal value, XsValue.StringSeqVal options) {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "path-range-query", new Object[]{ pathExpression, operator, value, options });
    }
    @Override
        public CtsQuery.QueryExpr pathRangeQuery(String pathExpression, String operator, XsValue.AnyAtomicTypeSeqVal value, String options, double weight) {
        return pathRangeQuery((pathExpression == null) ? null : xs.strings(pathExpression), xs.string(operator), value, (options == null) ? null : xs.strings(options), xs.doubleVal(weight)); 
    }
    @Override
        public CtsQuery.QueryExpr pathRangeQuery(XsValue.StringSeqVal pathExpression, XsValue.StringVal operator, XsValue.AnyAtomicTypeSeqVal value, XsValue.StringSeqVal options, XsValue.DoubleVal weight) {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "path-range-query", new Object[]{ pathExpression, operator, value, options, weight });
    }
    @Override
        public CtsQuery.PeriodExpr period(XsValue.DateTimeVal start) {
        return new CtsQueryExprImpl.PeriodCallImpl("cts", "period", new Object[]{ start });
    }
    @Override
        public CtsQuery.PeriodExpr period(XsValue.DateTimeVal start, XsValue.DateTimeVal end) {
        return new CtsQueryExprImpl.PeriodCallImpl("cts", "period", new Object[]{ start, end });
    }
    @Override
        public CtsQuery.QueryExpr periodCompareQuery(String axis1, String operator, String axis2) {
        return periodCompareQuery(xs.string(axis1), xs.string(operator), xs.string(axis2)); 
    }
    @Override
        public CtsQuery.QueryExpr periodCompareQuery(XsValue.StringVal axis1, XsValue.StringVal operator, XsValue.StringVal axis2) {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "period-compare-query", new Object[]{ axis1, operator, axis2 });
    }
    @Override
        public CtsQuery.QueryExpr periodCompareQuery(String axis1, String operator, String axis2, String... options) {
        return periodCompareQuery(xs.string(axis1), xs.string(operator), xs.string(axis2), (options == null) ? null : xs.strings(options)); 
    }
    @Override
        public CtsQuery.QueryExpr periodCompareQuery(XsValue.StringVal axis1, XsValue.StringVal operator, XsValue.StringVal axis2, XsValue.StringSeqVal options) {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "period-compare-query", new Object[]{ axis1, operator, axis2, options });
    }
    @Override
        public CtsQuery.QueryExpr periodRangeQuery(String axisName, String operator) {
        return periodRangeQuery((axisName == null) ? null : xs.strings(axisName), xs.string(operator)); 
    }
    @Override
        public CtsQuery.QueryExpr periodRangeQuery(XsValue.StringSeqVal axisName, XsValue.StringVal operator) {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "period-range-query", new Object[]{ axisName, operator });
    }
    @Override
        public CtsQuery.QueryExpr periodRangeQuery(String axisName, String operator, CtsQuery.PeriodExpr... period) {
        return periodRangeQuery((axisName == null) ? null : xs.strings(axisName), xs.string(operator), this.period(period)); 
    }
    @Override
        public CtsQuery.QueryExpr periodRangeQuery(XsValue.StringSeqVal axisName, XsValue.StringVal operator, CtsQuery.PeriodSeqExpr period) {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "period-range-query", new Object[]{ axisName, operator, period });
    }
    @Override
        public CtsQuery.QueryExpr periodRangeQuery(String axisName, String operator, CtsQuery.PeriodSeqExpr period, String... options) {
        return periodRangeQuery((axisName == null) ? null : xs.strings(axisName), xs.string(operator), period, (options == null) ? null : xs.strings(options)); 
    }
    @Override
        public CtsQuery.QueryExpr periodRangeQuery(XsValue.StringSeqVal axisName, XsValue.StringVal operator, CtsQuery.PeriodSeqExpr period, XsValue.StringSeqVal options) {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "period-range-query", new Object[]{ axisName, operator, period, options });
    }
    @Override
        public CtsQuery.PointExpr point(double latitude, double longitude) {
        return point(xs.doubleVal(latitude), xs.doubleVal(longitude)); 
    }
    @Override
        public CtsQuery.PointExpr point(XsValue.DoubleVal latitude, XsValue.DoubleVal longitude) {
        return new CtsQueryExprImpl.PointCallImpl("cts", "point", new Object[]{ latitude, longitude });
    }
    @Override
        public CtsQuery.PolygonExpr polygon(XsValue.AnyAtomicTypeSeqVal vertices) {
        return new CtsQueryExprImpl.PolygonCallImpl("cts", "polygon", new Object[]{ vertices });
    }
    @Override
        public CtsQuery.QueryExpr propertiesFragmentQuery(CtsQuery.QueryExpr query) {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "properties-fragment-query", new Object[]{ query });
    }
    @Override
        public CtsQuery.QueryExpr propertiesQuery(CtsQuery.QueryExpr arg1) {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "properties-query", new Object[]{ arg1 });
    }
    @Override
        public CtsQuery.QueryExpr tripleRangeQuery(XsValue.AnyAtomicTypeSeqVal subject, XsValue.AnyAtomicTypeSeqVal predicate, XsValue.AnyAtomicTypeSeqVal object) {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "triple-range-query", new Object[]{ subject, predicate, object });
    }
    @Override
        public CtsQuery.QueryExpr tripleRangeQuery(XsValue.AnyAtomicTypeSeqVal subject, XsValue.AnyAtomicTypeSeqVal predicate, XsValue.AnyAtomicTypeSeqVal object, String... operator) {
        return tripleRangeQuery(subject, predicate, object, (operator == null) ? null : xs.strings(operator)); 
    }
    @Override
        public CtsQuery.QueryExpr tripleRangeQuery(XsValue.AnyAtomicTypeSeqVal subject, XsValue.AnyAtomicTypeSeqVal predicate, XsValue.AnyAtomicTypeSeqVal object, XsValue.StringSeqVal operator) {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "triple-range-query", new Object[]{ subject, predicate, object, operator });
    }
    @Override
        public CtsQuery.QueryExpr tripleRangeQuery(XsValue.AnyAtomicTypeSeqVal subject, XsValue.AnyAtomicTypeSeqVal predicate, XsValue.AnyAtomicTypeSeqVal object, String operator, String... options) {
        return tripleRangeQuery(subject, predicate, object, (operator == null) ? null : xs.strings(operator), (options == null) ? null : xs.strings(options)); 
    }
    @Override
        public CtsQuery.QueryExpr tripleRangeQuery(XsValue.AnyAtomicTypeSeqVal subject, XsValue.AnyAtomicTypeSeqVal predicate, XsValue.AnyAtomicTypeSeqVal object, XsValue.StringSeqVal operator, XsValue.StringSeqVal options) {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "triple-range-query", new Object[]{ subject, predicate, object, operator, options });
    }
    @Override
        public CtsQuery.QueryExpr tripleRangeQuery(XsValue.AnyAtomicTypeSeqVal subject, XsValue.AnyAtomicTypeSeqVal predicate, XsValue.AnyAtomicTypeSeqVal object, String operator, String options, double weight) {
        return tripleRangeQuery(subject, predicate, object, (operator == null) ? null : xs.strings(operator), (options == null) ? null : xs.strings(options), xs.doubleVal(weight)); 
    }
    @Override
        public CtsQuery.QueryExpr tripleRangeQuery(XsValue.AnyAtomicTypeSeqVal subject, XsValue.AnyAtomicTypeSeqVal predicate, XsValue.AnyAtomicTypeSeqVal object, XsValue.StringSeqVal operator, XsValue.StringSeqVal options, XsValue.DoubleVal weight) {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "triple-range-query", new Object[]{ subject, predicate, object, operator, options, weight });
    }
    @Override
    public CtsQuery.QueryExpr trueQuery() {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "true-query", null);
    }
    @Override
        public CtsQuery.QueryExpr wordQuery(String... text) {
        return wordQuery((text == null) ? null : xs.strings(text)); 
    }
    @Override
        public CtsQuery.QueryExpr wordQuery(XsValue.StringSeqVal text) {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "word-query", new Object[]{ text });
    }
    @Override
        public CtsQuery.QueryExpr wordQuery(String text, String... options) {
        return wordQuery((text == null) ? null : xs.strings(text), (options == null) ? null : xs.strings(options)); 
    }
    @Override
        public CtsQuery.QueryExpr wordQuery(XsValue.StringSeqVal text, XsValue.StringSeqVal options) {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "word-query", new Object[]{ text, options });
    }
    @Override
        public CtsQuery.QueryExpr wordQuery(String text, String options, double weight) {
        return wordQuery((text == null) ? null : xs.strings(text), (options == null) ? null : xs.strings(options), xs.doubleVal(weight)); 
    }
    @Override
        public CtsQuery.QueryExpr wordQuery(XsValue.StringSeqVal text, XsValue.StringSeqVal options, XsValue.DoubleVal weight) {
        return new CtsQueryExprImpl.QueryCallImpl("cts", "word-query", new Object[]{ text, options, weight });
    }     @Override
    public CtsQuery.BoxSeqExpr box(CtsQuery.BoxExpr... items) {
        return new CtsQueryExprImpl.BoxSeqListImpl(items);
    }
     @Override
    public CtsQuery.CircleSeqExpr circle(CtsQuery.CircleExpr... items) {
        return new CtsQueryExprImpl.CircleSeqListImpl(items);
    }
     @Override
    public CtsQuery.PeriodSeqExpr period(CtsQuery.PeriodExpr... items) {
        return new CtsQueryExprImpl.PeriodSeqListImpl(items);
    }
     @Override
    public CtsQuery.PointSeqExpr point(CtsQuery.PointExpr... items) {
        return new CtsQueryExprImpl.PointSeqListImpl(items);
    }
     @Override
    public CtsQuery.PolygonSeqExpr polygon(CtsQuery.PolygonExpr... items) {
        return new CtsQueryExprImpl.PolygonSeqListImpl(items);
    }
     @Override
    public CtsQuery.QuerySeqExpr query(CtsQuery.QueryExpr... items) {
        return new CtsQueryExprImpl.QuerySeqListImpl(items);
    }
     @Override
    public CtsQuery.RegionSeqExpr region(CtsQuery.RegionExpr... items) {
        return new CtsQueryExprImpl.RegionSeqListImpl(items);
    }
        static class BoxSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements BoxSeqExpr {
            BoxSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class BoxSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements BoxSeqExpr {
            BoxSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class BoxCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements BoxExpr {
            BoxCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class CircleSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements CircleSeqExpr {
            CircleSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class CircleSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements CircleSeqExpr {
            CircleSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class CircleCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements CircleExpr {
            CircleCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class PeriodSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements PeriodSeqExpr {
            PeriodSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class PeriodSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements PeriodSeqExpr {
            PeriodSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class PeriodCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements PeriodExpr {
            PeriodCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class PointSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements PointSeqExpr {
            PointSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class PointSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements PointSeqExpr {
            PointSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class PointCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements PointExpr {
            PointCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class PolygonSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements PolygonSeqExpr {
            PolygonSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class PolygonSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements PolygonSeqExpr {
            PolygonSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class PolygonCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements PolygonExpr {
            PolygonCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class QuerySeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements QuerySeqExpr {
            QuerySeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class QuerySeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements QuerySeqExpr {
            QuerySeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class QueryCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements QueryExpr {
            QueryCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class RegionSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements RegionSeqExpr {
            RegionSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class RegionSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements RegionSeqExpr {
            RegionSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class RegionCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements RegionExpr {
            RegionCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }

}
