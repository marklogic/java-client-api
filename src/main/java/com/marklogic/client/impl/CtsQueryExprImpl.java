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

import com.marklogic.client.expression.XsExpr;
import com.marklogic.client.expression.XsValue;

import com.marklogic.client.expression.CtsQuery;
import com.marklogic.client.type.XsQNameSeqVal;
 import com.marklogic.client.type.XsDateTimeVal;
 import com.marklogic.client.type.CtsPointExpr;
 import com.marklogic.client.type.XsDoubleVal;
 import com.marklogic.client.type.CtsBoxExpr;
 import com.marklogic.client.type.CtsPolygonExpr;
 import com.marklogic.client.type.CtsCircleSeqExpr;
 import com.marklogic.client.type.CtsRegionExpr;
 import com.marklogic.client.type.CtsCircleExpr;
 import com.marklogic.client.type.CtsPeriodSeqExpr;
 import com.marklogic.client.type.CtsQuerySeqExpr;
 import com.marklogic.client.type.CtsPeriodExpr;
 import com.marklogic.client.type.XsStringSeqVal;
 import com.marklogic.client.type.CtsQueryExpr;
 import com.marklogic.client.type.XsStringVal;
 import com.marklogic.client.type.XsAnyAtomicTypeSeqVal;
 import com.marklogic.client.type.CtsReferenceSeqExpr;
 import com.marklogic.client.type.MapMapExpr;
 import com.marklogic.client.type.CtsPolygonSeqExpr;
 import com.marklogic.client.type.CtsReferenceExpr;
 import com.marklogic.client.type.CtsBoxSeqExpr;
 import com.marklogic.client.type.XsAnyAtomicTypeVal;
 import com.marklogic.client.type.XsQNameVal;
 import com.marklogic.client.type.CtsRegionSeqExpr;
 import com.marklogic.client.type.CtsPointSeqExpr;

import com.marklogic.client.impl.BaseTypeImpl;

// IMPORTANT: Do not edit. This file is generated.

public class CtsQueryExprImpl implements CtsQuery {
    private XsExprImpl xs = null;
    public CtsQueryExprImpl(XsExprImpl xs) {
        this.xs = xs;
    }
     @Override
        public CtsQueryExpr andNotQuery(CtsQueryExpr positiveQuery, CtsQueryExpr negativeQuery) {
        return new CtsQueryExprImpl.CtsQueryCallImpl("cts", "and-not-query", new Object[]{ positiveQuery, negativeQuery });
    }
    @Override
        public CtsQueryExpr andQuery(CtsQueryExpr... queries) {
        return andQuery(queries); 
    }
    @Override
        public CtsQueryExpr andQuery(CtsQuerySeqExpr queries) {
        return new CtsQueryExprImpl.CtsQueryCallImpl("cts", "and-query", new Object[]{ queries });
    }
    @Override
        public CtsQueryExpr andQuery(CtsQuerySeqExpr queries, String... options) {
        return andQuery(queries, (options == null) ? null : xs.strings(options)); 
    }
    @Override
        public CtsQueryExpr andQuery(CtsQuerySeqExpr queries, XsStringSeqVal options) {
        return new CtsQueryExprImpl.CtsQueryCallImpl("cts", "and-query", new Object[]{ queries, options });
    }
    @Override
        public CtsQueryExpr boostQuery(CtsQueryExpr matchingQuery, CtsQueryExpr boostingQuery) {
        return new CtsQueryExprImpl.CtsQueryCallImpl("cts", "boost-query", new Object[]{ matchingQuery, boostingQuery });
    }
    @Override
        public CtsQueryExpr collectionQuery(String... uris) {
        return collectionQuery(xs.strings(uris)); 
    }
    @Override
        public CtsQueryExpr collectionQuery(XsStringSeqVal uris) {
        return new CtsQueryExprImpl.CtsQueryCallImpl("cts", "collection-query", new Object[]{ uris });
    }
    @Override
        public CtsQueryExpr directoryQuery(String... uris) {
        return directoryQuery(xs.strings(uris)); 
    }
    @Override
        public CtsQueryExpr directoryQuery(XsStringSeqVal uris) {
        return new CtsQueryExprImpl.CtsQueryCallImpl("cts", "directory-query", new Object[]{ uris });
    }
    @Override
        public CtsQueryExpr directoryQuery(XsStringSeqVal uris, XsStringVal depth) {
        return new CtsQueryExprImpl.CtsQueryCallImpl("cts", "directory-query", new Object[]{ uris, depth });
    }
    @Override
        public CtsQueryExpr documentFragmentQuery(CtsQueryExpr query) {
        return new CtsQueryExprImpl.CtsQueryCallImpl("cts", "document-fragment-query", new Object[]{ query });
    }
    @Override
        public CtsQueryExpr documentQuery(String... uris) {
        return documentQuery(xs.strings(uris)); 
    }
    @Override
        public CtsQueryExpr documentQuery(XsStringSeqVal uris) {
        return new CtsQueryExprImpl.CtsQueryCallImpl("cts", "document-query", new Object[]{ uris });
    }
    @Override
        public CtsQueryExpr elementAttributePairGeospatialQuery(XsQNameSeqVal elementName, XsQNameSeqVal latitudeAttributeNames, XsQNameSeqVal longitudeAttributeNames, CtsRegionSeqExpr regions) {
        return new CtsQueryExprImpl.CtsQueryCallImpl("cts", "element-attribute-pair-geospatial-query", new Object[]{ elementName, latitudeAttributeNames, longitudeAttributeNames, regions });
    }
    @Override
        public CtsQueryExpr elementAttributePairGeospatialQuery(XsQNameSeqVal elementName, XsQNameSeqVal latitudeAttributeNames, XsQNameSeqVal longitudeAttributeNames, CtsRegionSeqExpr regions, String... options) {
        return elementAttributePairGeospatialQuery(elementName, latitudeAttributeNames, longitudeAttributeNames, regions, (options == null) ? null : xs.strings(options)); 
    }
    @Override
        public CtsQueryExpr elementAttributePairGeospatialQuery(XsQNameSeqVal elementName, XsQNameSeqVal latitudeAttributeNames, XsQNameSeqVal longitudeAttributeNames, CtsRegionSeqExpr regions, XsStringSeqVal options) {
        return new CtsQueryExprImpl.CtsQueryCallImpl("cts", "element-attribute-pair-geospatial-query", new Object[]{ elementName, latitudeAttributeNames, longitudeAttributeNames, regions, options });
    }
    @Override
        public CtsQueryExpr elementAttributePairGeospatialQuery(XsQNameSeqVal elementName, XsQNameSeqVal latitudeAttributeNames, XsQNameSeqVal longitudeAttributeNames, CtsRegionSeqExpr regions, String options, double weight) {
        return elementAttributePairGeospatialQuery(elementName, latitudeAttributeNames, longitudeAttributeNames, regions, (options == null) ? null : xs.strings(options), xs.doubleVal(weight)); 
    }
    @Override
        public CtsQueryExpr elementAttributePairGeospatialQuery(XsQNameSeqVal elementName, XsQNameSeqVal latitudeAttributeNames, XsQNameSeqVal longitudeAttributeNames, CtsRegionSeqExpr regions, XsStringSeqVal options, XsDoubleVal weight) {
        return new CtsQueryExprImpl.CtsQueryCallImpl("cts", "element-attribute-pair-geospatial-query", new Object[]{ elementName, latitudeAttributeNames, longitudeAttributeNames, regions, options, weight });
    }
    @Override
        public CtsQueryExpr elementAttributeRangeQuery(XsQNameSeqVal elementName, XsQNameSeqVal attributeName, String operator, XsAnyAtomicTypeVal... value) {
        return elementAttributeRangeQuery(elementName, attributeName, xs.string(operator), xs.anyAtomicTypes(value)); 
    }
    @Override
        public CtsQueryExpr elementAttributeRangeQuery(XsQNameSeqVal elementName, XsQNameSeqVal attributeName, XsStringVal operator, XsAnyAtomicTypeSeqVal value) {
        return new CtsQueryExprImpl.CtsQueryCallImpl("cts", "element-attribute-range-query", new Object[]{ elementName, attributeName, operator, value });
    }
    @Override
        public CtsQueryExpr elementAttributeRangeQuery(XsQNameSeqVal elementName, XsQNameSeqVal attributeName, String operator, XsAnyAtomicTypeSeqVal value, String... options) {
        return elementAttributeRangeQuery(elementName, attributeName, xs.string(operator), value, (options == null) ? null : xs.strings(options)); 
    }
    @Override
        public CtsQueryExpr elementAttributeRangeQuery(XsQNameSeqVal elementName, XsQNameSeqVal attributeName, XsStringVal operator, XsAnyAtomicTypeSeqVal value, XsStringSeqVal options) {
        return new CtsQueryExprImpl.CtsQueryCallImpl("cts", "element-attribute-range-query", new Object[]{ elementName, attributeName, operator, value, options });
    }
    @Override
        public CtsQueryExpr elementAttributeRangeQuery(XsQNameSeqVal elementName, XsQNameSeqVal attributeName, String operator, XsAnyAtomicTypeSeqVal value, String options, double weight) {
        return elementAttributeRangeQuery(elementName, attributeName, xs.string(operator), value, (options == null) ? null : xs.strings(options), xs.doubleVal(weight)); 
    }
    @Override
        public CtsQueryExpr elementAttributeRangeQuery(XsQNameSeqVal elementName, XsQNameSeqVal attributeName, XsStringVal operator, XsAnyAtomicTypeSeqVal value, XsStringSeqVal options, XsDoubleVal weight) {
        return new CtsQueryExprImpl.CtsQueryCallImpl("cts", "element-attribute-range-query", new Object[]{ elementName, attributeName, operator, value, options, weight });
    }
    @Override
        public CtsQueryExpr elementAttributeValueQuery(XsQNameSeqVal elementName, XsQNameSeqVal attributeName, String... text) {
        return elementAttributeValueQuery(elementName, attributeName, xs.strings(text)); 
    }
    @Override
        public CtsQueryExpr elementAttributeValueQuery(XsQNameSeqVal elementName, XsQNameSeqVal attributeName, XsStringSeqVal text) {
        return new CtsQueryExprImpl.CtsQueryCallImpl("cts", "element-attribute-value-query", new Object[]{ elementName, attributeName, text });
    }
    @Override
        public CtsQueryExpr elementAttributeValueQuery(XsQNameSeqVal elementName, XsQNameSeqVal attributeName, XsStringSeqVal text, XsStringSeqVal options) {
        return new CtsQueryExprImpl.CtsQueryCallImpl("cts", "element-attribute-value-query", new Object[]{ elementName, attributeName, text, options });
    }
    @Override
        public CtsQueryExpr elementAttributeValueQuery(XsQNameSeqVal elementName, XsQNameSeqVal attributeName, XsStringSeqVal text, XsStringSeqVal options, double weight) {
        return elementAttributeValueQuery(elementName, attributeName, text, options, xs.doubleVal(weight)); 
    }
    @Override
        public CtsQueryExpr elementAttributeValueQuery(XsQNameSeqVal elementName, XsQNameSeqVal attributeName, XsStringSeqVal text, XsStringSeqVal options, XsDoubleVal weight) {
        return new CtsQueryExprImpl.CtsQueryCallImpl("cts", "element-attribute-value-query", new Object[]{ elementName, attributeName, text, options, weight });
    }
    @Override
        public CtsQueryExpr elementAttributeWordQuery(XsQNameSeqVal elementName, XsQNameSeqVal attributeName) {
        return new CtsQueryExprImpl.CtsQueryCallImpl("cts", "element-attribute-word-query", new Object[]{ elementName, attributeName });
    }
    @Override
        public CtsQueryExpr elementAttributeWordQuery(XsQNameSeqVal elementName, XsQNameSeqVal attributeName, String... text) {
        return elementAttributeWordQuery(elementName, attributeName, (text == null) ? null : xs.strings(text)); 
    }
    @Override
        public CtsQueryExpr elementAttributeWordQuery(XsQNameSeqVal elementName, XsQNameSeqVal attributeName, XsStringSeqVal text) {
        return new CtsQueryExprImpl.CtsQueryCallImpl("cts", "element-attribute-word-query", new Object[]{ elementName, attributeName, text });
    }
    @Override
        public CtsQueryExpr elementAttributeWordQuery(XsQNameSeqVal elementName, XsQNameSeqVal attributeName, XsStringSeqVal text, XsStringSeqVal options) {
        return new CtsQueryExprImpl.CtsQueryCallImpl("cts", "element-attribute-word-query", new Object[]{ elementName, attributeName, text, options });
    }
    @Override
        public CtsQueryExpr elementAttributeWordQuery(XsQNameSeqVal elementName, XsQNameSeqVal attributeName, XsStringSeqVal text, XsStringSeqVal options, double weight) {
        return elementAttributeWordQuery(elementName, attributeName, text, options, xs.doubleVal(weight)); 
    }
    @Override
        public CtsQueryExpr elementAttributeWordQuery(XsQNameSeqVal elementName, XsQNameSeqVal attributeName, XsStringSeqVal text, XsStringSeqVal options, XsDoubleVal weight) {
        return new CtsQueryExprImpl.CtsQueryCallImpl("cts", "element-attribute-word-query", new Object[]{ elementName, attributeName, text, options, weight });
    }
    @Override
        public CtsQueryExpr elementChildGeospatialQuery(XsQNameSeqVal parentElementName, XsQNameSeqVal childElementNames, CtsRegionSeqExpr regions) {
        return new CtsQueryExprImpl.CtsQueryCallImpl("cts", "element-child-geospatial-query", new Object[]{ parentElementName, childElementNames, regions });
    }
    @Override
        public CtsQueryExpr elementChildGeospatialQuery(XsQNameSeqVal parentElementName, XsQNameSeqVal childElementNames, CtsRegionSeqExpr regions, String... options) {
        return elementChildGeospatialQuery(parentElementName, childElementNames, regions, (options == null) ? null : xs.strings(options)); 
    }
    @Override
        public CtsQueryExpr elementChildGeospatialQuery(XsQNameSeqVal parentElementName, XsQNameSeqVal childElementNames, CtsRegionSeqExpr regions, XsStringSeqVal options) {
        return new CtsQueryExprImpl.CtsQueryCallImpl("cts", "element-child-geospatial-query", new Object[]{ parentElementName, childElementNames, regions, options });
    }
    @Override
        public CtsQueryExpr elementChildGeospatialQuery(XsQNameSeqVal parentElementName, XsQNameSeqVal childElementNames, CtsRegionSeqExpr regions, String options, double weight) {
        return elementChildGeospatialQuery(parentElementName, childElementNames, regions, (options == null) ? null : xs.strings(options), xs.doubleVal(weight)); 
    }
    @Override
        public CtsQueryExpr elementChildGeospatialQuery(XsQNameSeqVal parentElementName, XsQNameSeqVal childElementNames, CtsRegionSeqExpr regions, XsStringSeqVal options, XsDoubleVal weight) {
        return new CtsQueryExprImpl.CtsQueryCallImpl("cts", "element-child-geospatial-query", new Object[]{ parentElementName, childElementNames, regions, options, weight });
    }
    @Override
        public CtsQueryExpr elementGeospatialQuery(XsQNameSeqVal elementName, CtsRegionSeqExpr regions) {
        return new CtsQueryExprImpl.CtsQueryCallImpl("cts", "element-geospatial-query", new Object[]{ elementName, regions });
    }
    @Override
        public CtsQueryExpr elementGeospatialQuery(XsQNameSeqVal elementName, CtsRegionSeqExpr regions, String... options) {
        return elementGeospatialQuery(elementName, regions, (options == null) ? null : xs.strings(options)); 
    }
    @Override
        public CtsQueryExpr elementGeospatialQuery(XsQNameSeqVal elementName, CtsRegionSeqExpr regions, XsStringSeqVal options) {
        return new CtsQueryExprImpl.CtsQueryCallImpl("cts", "element-geospatial-query", new Object[]{ elementName, regions, options });
    }
    @Override
        public CtsQueryExpr elementGeospatialQuery(XsQNameSeqVal elementName, CtsRegionSeqExpr regions, String options, double weight) {
        return elementGeospatialQuery(elementName, regions, (options == null) ? null : xs.strings(options), xs.doubleVal(weight)); 
    }
    @Override
        public CtsQueryExpr elementGeospatialQuery(XsQNameSeqVal elementName, CtsRegionSeqExpr regions, XsStringSeqVal options, XsDoubleVal weight) {
        return new CtsQueryExprImpl.CtsQueryCallImpl("cts", "element-geospatial-query", new Object[]{ elementName, regions, options, weight });
    }
    @Override
        public CtsQueryExpr elementPairGeospatialQuery(XsQNameSeqVal elementName, XsQNameSeqVal latitudeElementNames, XsQNameSeqVal longitudeElementNames, CtsRegionSeqExpr regions) {
        return new CtsQueryExprImpl.CtsQueryCallImpl("cts", "element-pair-geospatial-query", new Object[]{ elementName, latitudeElementNames, longitudeElementNames, regions });
    }
    @Override
        public CtsQueryExpr elementPairGeospatialQuery(XsQNameSeqVal elementName, XsQNameSeqVal latitudeElementNames, XsQNameSeqVal longitudeElementNames, CtsRegionSeqExpr regions, String... options) {
        return elementPairGeospatialQuery(elementName, latitudeElementNames, longitudeElementNames, regions, (options == null) ? null : xs.strings(options)); 
    }
    @Override
        public CtsQueryExpr elementPairGeospatialQuery(XsQNameSeqVal elementName, XsQNameSeqVal latitudeElementNames, XsQNameSeqVal longitudeElementNames, CtsRegionSeqExpr regions, XsStringSeqVal options) {
        return new CtsQueryExprImpl.CtsQueryCallImpl("cts", "element-pair-geospatial-query", new Object[]{ elementName, latitudeElementNames, longitudeElementNames, regions, options });
    }
    @Override
        public CtsQueryExpr elementPairGeospatialQuery(XsQNameSeqVal elementName, XsQNameSeqVal latitudeElementNames, XsQNameSeqVal longitudeElementNames, CtsRegionSeqExpr regions, String options, double weight) {
        return elementPairGeospatialQuery(elementName, latitudeElementNames, longitudeElementNames, regions, (options == null) ? null : xs.strings(options), xs.doubleVal(weight)); 
    }
    @Override
        public CtsQueryExpr elementPairGeospatialQuery(XsQNameSeqVal elementName, XsQNameSeqVal latitudeElementNames, XsQNameSeqVal longitudeElementNames, CtsRegionSeqExpr regions, XsStringSeqVal options, XsDoubleVal weight) {
        return new CtsQueryExprImpl.CtsQueryCallImpl("cts", "element-pair-geospatial-query", new Object[]{ elementName, latitudeElementNames, longitudeElementNames, regions, options, weight });
    }
    @Override
        public CtsQueryExpr elementQuery(XsQNameSeqVal elementName, CtsQueryExpr query) {
        return new CtsQueryExprImpl.CtsQueryCallImpl("cts", "element-query", new Object[]{ elementName, query });
    }
    @Override
        public CtsQueryExpr elementRangeQuery(XsQNameSeqVal elementName, String operator, XsAnyAtomicTypeVal... value) {
        return elementRangeQuery(elementName, xs.string(operator), xs.anyAtomicTypes(value)); 
    }
    @Override
        public CtsQueryExpr elementRangeQuery(XsQNameSeqVal elementName, XsStringVal operator, XsAnyAtomicTypeSeqVal value) {
        return new CtsQueryExprImpl.CtsQueryCallImpl("cts", "element-range-query", new Object[]{ elementName, operator, value });
    }
    @Override
        public CtsQueryExpr elementRangeQuery(XsQNameSeqVal elementName, String operator, XsAnyAtomicTypeSeqVal value, String... options) {
        return elementRangeQuery(elementName, xs.string(operator), value, (options == null) ? null : xs.strings(options)); 
    }
    @Override
        public CtsQueryExpr elementRangeQuery(XsQNameSeqVal elementName, XsStringVal operator, XsAnyAtomicTypeSeqVal value, XsStringSeqVal options) {
        return new CtsQueryExprImpl.CtsQueryCallImpl("cts", "element-range-query", new Object[]{ elementName, operator, value, options });
    }
    @Override
        public CtsQueryExpr elementRangeQuery(XsQNameSeqVal elementName, String operator, XsAnyAtomicTypeSeqVal value, String options, double weight) {
        return elementRangeQuery(elementName, xs.string(operator), value, (options == null) ? null : xs.strings(options), xs.doubleVal(weight)); 
    }
    @Override
        public CtsQueryExpr elementRangeQuery(XsQNameSeqVal elementName, XsStringVal operator, XsAnyAtomicTypeSeqVal value, XsStringSeqVal options, XsDoubleVal weight) {
        return new CtsQueryExprImpl.CtsQueryCallImpl("cts", "element-range-query", new Object[]{ elementName, operator, value, options, weight });
    }
    @Override
        public CtsQueryExpr elementValueQuery(XsQNameSeqVal elementName) {
        return new CtsQueryExprImpl.CtsQueryCallImpl("cts", "element-value-query", new Object[]{ elementName });
    }
    @Override
        public CtsQueryExpr elementValueQuery(XsQNameSeqVal elementName, String... text) {
        return elementValueQuery(elementName, (text == null) ? null : xs.strings(text)); 
    }
    @Override
        public CtsQueryExpr elementValueQuery(XsQNameSeqVal elementName, XsStringSeqVal text) {
        return new CtsQueryExprImpl.CtsQueryCallImpl("cts", "element-value-query", new Object[]{ elementName, text });
    }
    @Override
        public CtsQueryExpr elementValueQuery(XsQNameSeqVal elementName, XsStringSeqVal text, XsStringSeqVal options) {
        return new CtsQueryExprImpl.CtsQueryCallImpl("cts", "element-value-query", new Object[]{ elementName, text, options });
    }
    @Override
        public CtsQueryExpr elementValueQuery(XsQNameSeqVal elementName, XsStringSeqVal text, XsStringSeqVal options, double weight) {
        return elementValueQuery(elementName, text, options, xs.doubleVal(weight)); 
    }
    @Override
        public CtsQueryExpr elementValueQuery(XsQNameSeqVal elementName, XsStringSeqVal text, XsStringSeqVal options, XsDoubleVal weight) {
        return new CtsQueryExprImpl.CtsQueryCallImpl("cts", "element-value-query", new Object[]{ elementName, text, options, weight });
    }
    @Override
        public CtsQueryExpr elementWordQuery(XsQNameSeqVal elementName, String... text) {
        return elementWordQuery(elementName, xs.strings(text)); 
    }
    @Override
        public CtsQueryExpr elementWordQuery(XsQNameSeqVal elementName, XsStringSeqVal text) {
        return new CtsQueryExprImpl.CtsQueryCallImpl("cts", "element-word-query", new Object[]{ elementName, text });
    }
    @Override
        public CtsQueryExpr elementWordQuery(XsQNameSeqVal elementName, XsStringSeqVal text, XsStringSeqVal options) {
        return new CtsQueryExprImpl.CtsQueryCallImpl("cts", "element-word-query", new Object[]{ elementName, text, options });
    }
    @Override
        public CtsQueryExpr elementWordQuery(XsQNameSeqVal elementName, XsStringSeqVal text, XsStringSeqVal options, double weight) {
        return elementWordQuery(elementName, text, options, xs.doubleVal(weight)); 
    }
    @Override
        public CtsQueryExpr elementWordQuery(XsQNameSeqVal elementName, XsStringSeqVal text, XsStringSeqVal options, XsDoubleVal weight) {
        return new CtsQueryExprImpl.CtsQueryCallImpl("cts", "element-word-query", new Object[]{ elementName, text, options, weight });
    }
    @Override
    public CtsQueryExpr falseQuery() {
        return new CtsQueryExprImpl.CtsQueryCallImpl("cts", "false-query", null);
    }
    @Override
        public CtsQueryExpr fieldRangeQuery(String fieldName, String operator, XsAnyAtomicTypeVal... value) {
        return fieldRangeQuery(xs.strings(fieldName), xs.string(operator), xs.anyAtomicTypes(value)); 
    }
    @Override
        public CtsQueryExpr fieldRangeQuery(XsStringSeqVal fieldName, XsStringVal operator, XsAnyAtomicTypeSeqVal value) {
        return new CtsQueryExprImpl.CtsQueryCallImpl("cts", "field-range-query", new Object[]{ fieldName, operator, value });
    }
    @Override
        public CtsQueryExpr fieldRangeQuery(String fieldName, String operator, XsAnyAtomicTypeSeqVal value, String... options) {
        return fieldRangeQuery(xs.strings(fieldName), xs.string(operator), value, (options == null) ? null : xs.strings(options)); 
    }
    @Override
        public CtsQueryExpr fieldRangeQuery(XsStringSeqVal fieldName, XsStringVal operator, XsAnyAtomicTypeSeqVal value, XsStringSeqVal options) {
        return new CtsQueryExprImpl.CtsQueryCallImpl("cts", "field-range-query", new Object[]{ fieldName, operator, value, options });
    }
    @Override
        public CtsQueryExpr fieldRangeQuery(String fieldName, String operator, XsAnyAtomicTypeSeqVal value, String options, double weight) {
        return fieldRangeQuery(xs.strings(fieldName), xs.string(operator), value, (options == null) ? null : xs.strings(options), xs.doubleVal(weight)); 
    }
    @Override
        public CtsQueryExpr fieldRangeQuery(XsStringSeqVal fieldName, XsStringVal operator, XsAnyAtomicTypeSeqVal value, XsStringSeqVal options, XsDoubleVal weight) {
        return new CtsQueryExprImpl.CtsQueryCallImpl("cts", "field-range-query", new Object[]{ fieldName, operator, value, options, weight });
    }
    @Override
        public CtsQueryExpr fieldValueQuery(String fieldName, XsAnyAtomicTypeVal... text) {
        return fieldValueQuery(xs.strings(fieldName), xs.anyAtomicTypes(text)); 
    }
    @Override
        public CtsQueryExpr fieldValueQuery(XsStringSeqVal fieldName, XsAnyAtomicTypeSeqVal text) {
        return new CtsQueryExprImpl.CtsQueryCallImpl("cts", "field-value-query", new Object[]{ fieldName, text });
    }
    @Override
        public CtsQueryExpr fieldValueQuery(String fieldName, XsAnyAtomicTypeSeqVal text, String... options) {
        return fieldValueQuery(xs.strings(fieldName), text, (options == null) ? null : xs.strings(options)); 
    }
    @Override
        public CtsQueryExpr fieldValueQuery(XsStringSeqVal fieldName, XsAnyAtomicTypeSeqVal text, XsStringSeqVal options) {
        return new CtsQueryExprImpl.CtsQueryCallImpl("cts", "field-value-query", new Object[]{ fieldName, text, options });
    }
    @Override
        public CtsQueryExpr fieldValueQuery(String fieldName, XsAnyAtomicTypeSeqVal text, String options, double weight) {
        return fieldValueQuery(xs.strings(fieldName), text, (options == null) ? null : xs.strings(options), xs.doubleVal(weight)); 
    }
    @Override
        public CtsQueryExpr fieldValueQuery(XsStringSeqVal fieldName, XsAnyAtomicTypeSeqVal text, XsStringSeqVal options, XsDoubleVal weight) {
        return new CtsQueryExprImpl.CtsQueryCallImpl("cts", "field-value-query", new Object[]{ fieldName, text, options, weight });
    }
    @Override
        public CtsQueryExpr fieldWordQuery(String fieldName, String... text) {
        return fieldWordQuery(xs.strings(fieldName), xs.strings(text)); 
    }
    @Override
        public CtsQueryExpr fieldWordQuery(XsStringSeqVal fieldName, XsStringSeqVal text) {
        return new CtsQueryExprImpl.CtsQueryCallImpl("cts", "field-word-query", new Object[]{ fieldName, text });
    }
    @Override
        public CtsQueryExpr fieldWordQuery(String fieldName, XsStringSeqVal text, XsStringVal... options) {
        return fieldWordQuery(xs.strings(fieldName), text, (options == null || options.length == 0) ? null : xs.strings(options)); 
    }
    @Override
        public CtsQueryExpr fieldWordQuery(XsStringSeqVal fieldName, XsStringSeqVal text, XsStringSeqVal options) {
        return new CtsQueryExprImpl.CtsQueryCallImpl("cts", "field-word-query", new Object[]{ fieldName, text, options });
    }
    @Override
        public CtsQueryExpr fieldWordQuery(String fieldName, XsStringSeqVal text, XsStringSeqVal options, double weight) {
        return fieldWordQuery(xs.strings(fieldName), text, options, xs.doubleVal(weight)); 
    }
    @Override
        public CtsQueryExpr fieldWordQuery(XsStringSeqVal fieldName, XsStringSeqVal text, XsStringSeqVal options, XsDoubleVal weight) {
        return new CtsQueryExprImpl.CtsQueryCallImpl("cts", "field-word-query", new Object[]{ fieldName, text, options, weight });
    }
    @Override
        public CtsQueryExpr jsonPropertyChildGeospatialQuery(String parentPropertyName, String childPropertyNames, CtsRegionExpr... regions) {
        return jsonPropertyChildGeospatialQuery(xs.strings(parentPropertyName), xs.strings(childPropertyNames), this.region(regions)); 
    }
    @Override
        public CtsQueryExpr jsonPropertyChildGeospatialQuery(XsStringSeqVal parentPropertyName, XsStringSeqVal childPropertyNames, CtsRegionSeqExpr regions) {
        return new CtsQueryExprImpl.CtsQueryCallImpl("cts", "json-property-child-geospatial-query", new Object[]{ parentPropertyName, childPropertyNames, regions });
    }
    @Override
        public CtsQueryExpr jsonPropertyChildGeospatialQuery(String parentPropertyName, String childPropertyNames, CtsRegionSeqExpr regions, String... options) {
        return jsonPropertyChildGeospatialQuery(xs.strings(parentPropertyName), xs.strings(childPropertyNames), regions, (options == null) ? null : xs.strings(options)); 
    }
    @Override
        public CtsQueryExpr jsonPropertyChildGeospatialQuery(XsStringSeqVal parentPropertyName, XsStringSeqVal childPropertyNames, CtsRegionSeqExpr regions, XsStringSeqVal options) {
        return new CtsQueryExprImpl.CtsQueryCallImpl("cts", "json-property-child-geospatial-query", new Object[]{ parentPropertyName, childPropertyNames, regions, options });
    }
    @Override
        public CtsQueryExpr jsonPropertyChildGeospatialQuery(String parentPropertyName, String childPropertyNames, CtsRegionSeqExpr regions, String options, double weight) {
        return jsonPropertyChildGeospatialQuery(xs.strings(parentPropertyName), xs.strings(childPropertyNames), regions, (options == null) ? null : xs.strings(options), xs.doubleVal(weight)); 
    }
    @Override
        public CtsQueryExpr jsonPropertyChildGeospatialQuery(XsStringSeqVal parentPropertyName, XsStringSeqVal childPropertyNames, CtsRegionSeqExpr regions, XsStringSeqVal options, XsDoubleVal weight) {
        return new CtsQueryExprImpl.CtsQueryCallImpl("cts", "json-property-child-geospatial-query", new Object[]{ parentPropertyName, childPropertyNames, regions, options, weight });
    }
    @Override
        public CtsQueryExpr jsonPropertyGeospatialQuery(String propertyName, CtsRegionExpr... regions) {
        return jsonPropertyGeospatialQuery(xs.strings(propertyName), this.region(regions)); 
    }
    @Override
        public CtsQueryExpr jsonPropertyGeospatialQuery(XsStringSeqVal propertyName, CtsRegionSeqExpr regions) {
        return new CtsQueryExprImpl.CtsQueryCallImpl("cts", "json-property-geospatial-query", new Object[]{ propertyName, regions });
    }
    @Override
        public CtsQueryExpr jsonPropertyGeospatialQuery(String propertyName, CtsRegionSeqExpr regions, String... options) {
        return jsonPropertyGeospatialQuery(xs.strings(propertyName), regions, (options == null) ? null : xs.strings(options)); 
    }
    @Override
        public CtsQueryExpr jsonPropertyGeospatialQuery(XsStringSeqVal propertyName, CtsRegionSeqExpr regions, XsStringSeqVal options) {
        return new CtsQueryExprImpl.CtsQueryCallImpl("cts", "json-property-geospatial-query", new Object[]{ propertyName, regions, options });
    }
    @Override
        public CtsQueryExpr jsonPropertyGeospatialQuery(String propertyName, CtsRegionSeqExpr regions, String options, double weight) {
        return jsonPropertyGeospatialQuery(xs.strings(propertyName), regions, (options == null) ? null : xs.strings(options), xs.doubleVal(weight)); 
    }
    @Override
        public CtsQueryExpr jsonPropertyGeospatialQuery(XsStringSeqVal propertyName, CtsRegionSeqExpr regions, XsStringSeqVal options, XsDoubleVal weight) {
        return new CtsQueryExprImpl.CtsQueryCallImpl("cts", "json-property-geospatial-query", new Object[]{ propertyName, regions, options, weight });
    }
    @Override
        public CtsQueryExpr jsonPropertyPairGeospatialQuery(String propertyName, String latitudePropertyNames, String longitudePropertyNames, CtsRegionExpr... regions) {
        return jsonPropertyPairGeospatialQuery(xs.strings(propertyName), xs.strings(latitudePropertyNames), xs.strings(longitudePropertyNames), this.region(regions)); 
    }
    @Override
        public CtsQueryExpr jsonPropertyPairGeospatialQuery(XsStringSeqVal propertyName, XsStringSeqVal latitudePropertyNames, XsStringSeqVal longitudePropertyNames, CtsRegionSeqExpr regions) {
        return new CtsQueryExprImpl.CtsQueryCallImpl("cts", "json-property-pair-geospatial-query", new Object[]{ propertyName, latitudePropertyNames, longitudePropertyNames, regions });
    }
    @Override
        public CtsQueryExpr jsonPropertyPairGeospatialQuery(String propertyName, String latitudePropertyNames, String longitudePropertyNames, CtsRegionSeqExpr regions, String... options) {
        return jsonPropertyPairGeospatialQuery(xs.strings(propertyName), xs.strings(latitudePropertyNames), xs.strings(longitudePropertyNames), regions, (options == null) ? null : xs.strings(options)); 
    }
    @Override
        public CtsQueryExpr jsonPropertyPairGeospatialQuery(XsStringSeqVal propertyName, XsStringSeqVal latitudePropertyNames, XsStringSeqVal longitudePropertyNames, CtsRegionSeqExpr regions, XsStringSeqVal options) {
        return new CtsQueryExprImpl.CtsQueryCallImpl("cts", "json-property-pair-geospatial-query", new Object[]{ propertyName, latitudePropertyNames, longitudePropertyNames, regions, options });
    }
    @Override
        public CtsQueryExpr jsonPropertyPairGeospatialQuery(String propertyName, String latitudePropertyNames, String longitudePropertyNames, CtsRegionSeqExpr regions, String options, double weight) {
        return jsonPropertyPairGeospatialQuery(xs.strings(propertyName), xs.strings(latitudePropertyNames), xs.strings(longitudePropertyNames), regions, (options == null) ? null : xs.strings(options), xs.doubleVal(weight)); 
    }
    @Override
        public CtsQueryExpr jsonPropertyPairGeospatialQuery(XsStringSeqVal propertyName, XsStringSeqVal latitudePropertyNames, XsStringSeqVal longitudePropertyNames, CtsRegionSeqExpr regions, XsStringSeqVal options, XsDoubleVal weight) {
        return new CtsQueryExprImpl.CtsQueryCallImpl("cts", "json-property-pair-geospatial-query", new Object[]{ propertyName, latitudePropertyNames, longitudePropertyNames, regions, options, weight });
    }
    @Override
        public CtsQueryExpr jsonPropertyRangeQuery(String propertyName, String operator, XsAnyAtomicTypeVal... value) {
        return jsonPropertyRangeQuery(xs.strings(propertyName), xs.string(operator), xs.anyAtomicTypes(value)); 
    }
    @Override
        public CtsQueryExpr jsonPropertyRangeQuery(XsStringSeqVal propertyName, XsStringVal operator, XsAnyAtomicTypeSeqVal value) {
        return new CtsQueryExprImpl.CtsQueryCallImpl("cts", "json-property-range-query", new Object[]{ propertyName, operator, value });
    }
    @Override
        public CtsQueryExpr jsonPropertyRangeQuery(String propertyName, String operator, XsAnyAtomicTypeSeqVal value, String... options) {
        return jsonPropertyRangeQuery(xs.strings(propertyName), xs.string(operator), value, (options == null) ? null : xs.strings(options)); 
    }
    @Override
        public CtsQueryExpr jsonPropertyRangeQuery(XsStringSeqVal propertyName, XsStringVal operator, XsAnyAtomicTypeSeqVal value, XsStringSeqVal options) {
        return new CtsQueryExprImpl.CtsQueryCallImpl("cts", "json-property-range-query", new Object[]{ propertyName, operator, value, options });
    }
    @Override
        public CtsQueryExpr jsonPropertyRangeQuery(String propertyName, String operator, XsAnyAtomicTypeSeqVal value, String options, double weight) {
        return jsonPropertyRangeQuery(xs.strings(propertyName), xs.string(operator), value, (options == null) ? null : xs.strings(options), xs.doubleVal(weight)); 
    }
    @Override
        public CtsQueryExpr jsonPropertyRangeQuery(XsStringSeqVal propertyName, XsStringVal operator, XsAnyAtomicTypeSeqVal value, XsStringSeqVal options, XsDoubleVal weight) {
        return new CtsQueryExprImpl.CtsQueryCallImpl("cts", "json-property-range-query", new Object[]{ propertyName, operator, value, options, weight });
    }
    @Override
        public CtsQueryExpr jsonPropertyScopeQuery(String propertyName, CtsQueryExpr query) {
        return jsonPropertyScopeQuery(xs.strings(propertyName), query); 
    }
    @Override
        public CtsQueryExpr jsonPropertyScopeQuery(XsStringSeqVal propertyName, CtsQueryExpr query) {
        return new CtsQueryExprImpl.CtsQueryCallImpl("cts", "json-property-scope-query", new Object[]{ propertyName, query });
    }
    @Override
        public CtsQueryExpr jsonPropertyValueQuery(String propertyName, XsAnyAtomicTypeVal... value) {
        return jsonPropertyValueQuery(xs.strings(propertyName), xs.anyAtomicTypes(value)); 
    }
    @Override
        public CtsQueryExpr jsonPropertyValueQuery(XsStringSeqVal propertyName, XsAnyAtomicTypeSeqVal value) {
        return new CtsQueryExprImpl.CtsQueryCallImpl("cts", "json-property-value-query", new Object[]{ propertyName, value });
    }
    @Override
        public CtsQueryExpr jsonPropertyValueQuery(String propertyName, XsAnyAtomicTypeSeqVal value, String... options) {
        return jsonPropertyValueQuery(xs.strings(propertyName), value, (options == null) ? null : xs.strings(options)); 
    }
    @Override
        public CtsQueryExpr jsonPropertyValueQuery(XsStringSeqVal propertyName, XsAnyAtomicTypeSeqVal value, XsStringSeqVal options) {
        return new CtsQueryExprImpl.CtsQueryCallImpl("cts", "json-property-value-query", new Object[]{ propertyName, value, options });
    }
    @Override
        public CtsQueryExpr jsonPropertyValueQuery(String propertyName, XsAnyAtomicTypeSeqVal value, String options, double weight) {
        return jsonPropertyValueQuery(xs.strings(propertyName), value, (options == null) ? null : xs.strings(options), xs.doubleVal(weight)); 
    }
    @Override
        public CtsQueryExpr jsonPropertyValueQuery(XsStringSeqVal propertyName, XsAnyAtomicTypeSeqVal value, XsStringSeqVal options, XsDoubleVal weight) {
        return new CtsQueryExprImpl.CtsQueryCallImpl("cts", "json-property-value-query", new Object[]{ propertyName, value, options, weight });
    }
    @Override
        public CtsQueryExpr jsonPropertyWordQuery(String propertyName, String... text) {
        return jsonPropertyWordQuery(xs.strings(propertyName), xs.strings(text)); 
    }
    @Override
        public CtsQueryExpr jsonPropertyWordQuery(XsStringSeqVal propertyName, XsStringSeqVal text) {
        return new CtsQueryExprImpl.CtsQueryCallImpl("cts", "json-property-word-query", new Object[]{ propertyName, text });
    }
    @Override
        public CtsQueryExpr jsonPropertyWordQuery(String propertyName, XsStringSeqVal text, XsStringVal... options) {
        return jsonPropertyWordQuery(xs.strings(propertyName), text, (options == null || options.length == 0) ? null : xs.strings(options)); 
    }
    @Override
        public CtsQueryExpr jsonPropertyWordQuery(XsStringSeqVal propertyName, XsStringSeqVal text, XsStringSeqVal options) {
        return new CtsQueryExprImpl.CtsQueryCallImpl("cts", "json-property-word-query", new Object[]{ propertyName, text, options });
    }
    @Override
        public CtsQueryExpr jsonPropertyWordQuery(String propertyName, XsStringSeqVal text, XsStringSeqVal options, double weight) {
        return jsonPropertyWordQuery(xs.strings(propertyName), text, options, xs.doubleVal(weight)); 
    }
    @Override
        public CtsQueryExpr jsonPropertyWordQuery(XsStringSeqVal propertyName, XsStringSeqVal text, XsStringSeqVal options, XsDoubleVal weight) {
        return new CtsQueryExprImpl.CtsQueryCallImpl("cts", "json-property-word-query", new Object[]{ propertyName, text, options, weight });
    }
    @Override
        public CtsQueryExpr locksFragmentQuery(CtsQueryExpr query) {
        return new CtsQueryExprImpl.CtsQueryCallImpl("cts", "locks-fragment-query", new Object[]{ query });
    }
    @Override
        public CtsQueryExpr lsqtQuery(String temporalCollection) {
        return lsqtQuery(xs.string(temporalCollection)); 
    }
    @Override
        public CtsQueryExpr lsqtQuery(XsStringVal temporalCollection) {
        return new CtsQueryExprImpl.CtsQueryCallImpl("cts", "lsqt-query", new Object[]{ temporalCollection });
    }
    @Override
        public CtsQueryExpr lsqtQuery(String temporalCollection, XsDateTimeVal timestamp) {
        return lsqtQuery(xs.string(temporalCollection), timestamp); 
    }
    @Override
        public CtsQueryExpr lsqtQuery(XsStringVal temporalCollection, XsDateTimeVal timestamp) {
        return new CtsQueryExprImpl.CtsQueryCallImpl("cts", "lsqt-query", new Object[]{ temporalCollection, timestamp });
    }
    @Override
        public CtsQueryExpr lsqtQuery(String temporalCollection, XsDateTimeVal timestamp, String... options) {
        return lsqtQuery(xs.string(temporalCollection), timestamp, (options == null) ? null : xs.strings(options)); 
    }
    @Override
        public CtsQueryExpr lsqtQuery(XsStringVal temporalCollection, XsDateTimeVal timestamp, XsStringSeqVal options) {
        return new CtsQueryExprImpl.CtsQueryCallImpl("cts", "lsqt-query", new Object[]{ temporalCollection, timestamp, options });
    }
    @Override
        public CtsQueryExpr lsqtQuery(String temporalCollection, XsDateTimeVal timestamp, String options, double weight) {
        return lsqtQuery(xs.string(temporalCollection), timestamp, (options == null) ? null : xs.strings(options), xs.doubleVal(weight)); 
    }
    @Override
        public CtsQueryExpr lsqtQuery(XsStringVal temporalCollection, XsDateTimeVal timestamp, XsStringSeqVal options, XsDoubleVal weight) {
        return new CtsQueryExprImpl.CtsQueryCallImpl("cts", "lsqt-query", new Object[]{ temporalCollection, timestamp, options, weight });
    }
    @Override
        public CtsQueryExpr nearQuery(CtsQueryExpr... queries) {
        return nearQuery(queries); 
    }
    @Override
        public CtsQueryExpr nearQuery(CtsQuerySeqExpr queries) {
        return new CtsQueryExprImpl.CtsQueryCallImpl("cts", "near-query", new Object[]{ queries });
    }
    @Override
        public CtsQueryExpr nearQuery(CtsQuerySeqExpr queries, double distance) {
        return nearQuery(queries, xs.doubleVal(distance)); 
    }
    @Override
        public CtsQueryExpr nearQuery(CtsQuerySeqExpr queries, XsDoubleVal distance) {
        return new CtsQueryExprImpl.CtsQueryCallImpl("cts", "near-query", new Object[]{ queries, distance });
    }
    @Override
        public CtsQueryExpr nearQuery(CtsQuerySeqExpr queries, double distance, String... options) {
        return nearQuery(queries, xs.doubleVal(distance), (options == null) ? null : xs.strings(options)); 
    }
    @Override
        public CtsQueryExpr nearQuery(CtsQuerySeqExpr queries, XsDoubleVal distance, XsStringSeqVal options) {
        return new CtsQueryExprImpl.CtsQueryCallImpl("cts", "near-query", new Object[]{ queries, distance, options });
    }
    @Override
        public CtsQueryExpr nearQuery(CtsQuerySeqExpr queries, double distance, String options, double distanceWeight) {
        return nearQuery(queries, xs.doubleVal(distance), (options == null) ? null : xs.strings(options), xs.doubleVal(distanceWeight)); 
    }
    @Override
        public CtsQueryExpr nearQuery(CtsQuerySeqExpr queries, XsDoubleVal distance, XsStringSeqVal options, XsDoubleVal distanceWeight) {
        return new CtsQueryExprImpl.CtsQueryCallImpl("cts", "near-query", new Object[]{ queries, distance, options, distanceWeight });
    }
    @Override
        public CtsQueryExpr notInQuery(CtsQueryExpr positiveQuery, CtsQueryExpr negativeQuery) {
        return new CtsQueryExprImpl.CtsQueryCallImpl("cts", "not-in-query", new Object[]{ positiveQuery, negativeQuery });
    }
    @Override
        public CtsQueryExpr notQuery(CtsQueryExpr query) {
        return new CtsQueryExprImpl.CtsQueryCallImpl("cts", "not-query", new Object[]{ query });
    }
    @Override
        public CtsQueryExpr orQuery(CtsQueryExpr... queries) {
        return orQuery(queries); 
    }
    @Override
        public CtsQueryExpr orQuery(CtsQuerySeqExpr queries) {
        return new CtsQueryExprImpl.CtsQueryCallImpl("cts", "or-query", new Object[]{ queries });
    }
    @Override
        public CtsQueryExpr orQuery(CtsQuerySeqExpr queries, String... options) {
        return orQuery(queries, (options == null) ? null : xs.strings(options)); 
    }
    @Override
        public CtsQueryExpr orQuery(CtsQuerySeqExpr queries, XsStringSeqVal options) {
        return new CtsQueryExprImpl.CtsQueryCallImpl("cts", "or-query", new Object[]{ queries, options });
    }
    @Override
        public CtsQueryExpr pathGeospatialQuery(String pathExpression, CtsRegionExpr... regions) {
        return pathGeospatialQuery(xs.strings(pathExpression), this.region(regions)); 
    }
    @Override
        public CtsQueryExpr pathGeospatialQuery(XsStringSeqVal pathExpression, CtsRegionSeqExpr regions) {
        return new CtsQueryExprImpl.CtsQueryCallImpl("cts", "path-geospatial-query", new Object[]{ pathExpression, regions });
    }
    @Override
        public CtsQueryExpr pathGeospatialQuery(String pathExpression, CtsRegionSeqExpr regions, String... options) {
        return pathGeospatialQuery(xs.strings(pathExpression), regions, (options == null) ? null : xs.strings(options)); 
    }
    @Override
        public CtsQueryExpr pathGeospatialQuery(XsStringSeqVal pathExpression, CtsRegionSeqExpr regions, XsStringSeqVal options) {
        return new CtsQueryExprImpl.CtsQueryCallImpl("cts", "path-geospatial-query", new Object[]{ pathExpression, regions, options });
    }
    @Override
        public CtsQueryExpr pathGeospatialQuery(String pathExpression, CtsRegionSeqExpr regions, String options, double weight) {
        return pathGeospatialQuery(xs.strings(pathExpression), regions, (options == null) ? null : xs.strings(options), xs.doubleVal(weight)); 
    }
    @Override
        public CtsQueryExpr pathGeospatialQuery(XsStringSeqVal pathExpression, CtsRegionSeqExpr regions, XsStringSeqVal options, XsDoubleVal weight) {
        return new CtsQueryExprImpl.CtsQueryCallImpl("cts", "path-geospatial-query", new Object[]{ pathExpression, regions, options, weight });
    }
    @Override
        public CtsQueryExpr pathRangeQuery(String pathExpression, String operator, XsAnyAtomicTypeVal... value) {
        return pathRangeQuery(xs.strings(pathExpression), xs.string(operator), xs.anyAtomicTypes(value)); 
    }
    @Override
        public CtsQueryExpr pathRangeQuery(XsStringSeqVal pathExpression, XsStringVal operator, XsAnyAtomicTypeSeqVal value) {
        return new CtsQueryExprImpl.CtsQueryCallImpl("cts", "path-range-query", new Object[]{ pathExpression, operator, value });
    }
    @Override
        public CtsQueryExpr pathRangeQuery(String pathExpression, String operator, XsAnyAtomicTypeSeqVal value, String... options) {
        return pathRangeQuery(xs.strings(pathExpression), xs.string(operator), value, (options == null) ? null : xs.strings(options)); 
    }
    @Override
        public CtsQueryExpr pathRangeQuery(XsStringSeqVal pathExpression, XsStringVal operator, XsAnyAtomicTypeSeqVal value, XsStringSeqVal options) {
        return new CtsQueryExprImpl.CtsQueryCallImpl("cts", "path-range-query", new Object[]{ pathExpression, operator, value, options });
    }
    @Override
        public CtsQueryExpr pathRangeQuery(String pathExpression, String operator, XsAnyAtomicTypeSeqVal value, String options, double weight) {
        return pathRangeQuery(xs.strings(pathExpression), xs.string(operator), value, (options == null) ? null : xs.strings(options), xs.doubleVal(weight)); 
    }
    @Override
        public CtsQueryExpr pathRangeQuery(XsStringSeqVal pathExpression, XsStringVal operator, XsAnyAtomicTypeSeqVal value, XsStringSeqVal options, XsDoubleVal weight) {
        return new CtsQueryExprImpl.CtsQueryCallImpl("cts", "path-range-query", new Object[]{ pathExpression, operator, value, options, weight });
    }
    @Override
        public CtsPeriodExpr period(XsDateTimeVal start) {
        return new CtsQueryExprImpl.CtsPeriodCallImpl("cts", "period", new Object[]{ start });
    }
    @Override
        public CtsPeriodExpr period(XsDateTimeVal start, XsDateTimeVal end) {
        return new CtsQueryExprImpl.CtsPeriodCallImpl("cts", "period", new Object[]{ start, end });
    }
    @Override
        public CtsQueryExpr periodCompareQuery(String axis1, String operator, String axis2) {
        return periodCompareQuery(xs.string(axis1), xs.string(operator), xs.string(axis2)); 
    }
    @Override
        public CtsQueryExpr periodCompareQuery(XsStringVal axis1, XsStringVal operator, XsStringVal axis2) {
        return new CtsQueryExprImpl.CtsQueryCallImpl("cts", "period-compare-query", new Object[]{ axis1, operator, axis2 });
    }
    @Override
        public CtsQueryExpr periodCompareQuery(String axis1, String operator, String axis2, String... options) {
        return periodCompareQuery(xs.string(axis1), xs.string(operator), xs.string(axis2), (options == null) ? null : xs.strings(options)); 
    }
    @Override
        public CtsQueryExpr periodCompareQuery(XsStringVal axis1, XsStringVal operator, XsStringVal axis2, XsStringSeqVal options) {
        return new CtsQueryExprImpl.CtsQueryCallImpl("cts", "period-compare-query", new Object[]{ axis1, operator, axis2, options });
    }
    @Override
        public CtsQueryExpr periodRangeQuery(String axisName, String operator) {
        return periodRangeQuery(xs.strings(axisName), xs.string(operator)); 
    }
    @Override
        public CtsQueryExpr periodRangeQuery(XsStringSeqVal axisName, XsStringVal operator) {
        return new CtsQueryExprImpl.CtsQueryCallImpl("cts", "period-range-query", new Object[]{ axisName, operator });
    }
    @Override
        public CtsQueryExpr periodRangeQuery(String axisName, String operator, CtsPeriodExpr... period) {
        return periodRangeQuery(xs.strings(axisName), xs.string(operator), (period == null || period.length == 0) ? null : this.period(period)); 
    }
    @Override
        public CtsQueryExpr periodRangeQuery(XsStringSeqVal axisName, XsStringVal operator, CtsPeriodSeqExpr period) {
        return new CtsQueryExprImpl.CtsQueryCallImpl("cts", "period-range-query", new Object[]{ axisName, operator, period });
    }
    @Override
        public CtsQueryExpr periodRangeQuery(String axisName, String operator, CtsPeriodSeqExpr period, String... options) {
        return periodRangeQuery(xs.strings(axisName), xs.string(operator), period, (options == null) ? null : xs.strings(options)); 
    }
    @Override
        public CtsQueryExpr periodRangeQuery(XsStringSeqVal axisName, XsStringVal operator, CtsPeriodSeqExpr period, XsStringSeqVal options) {
        return new CtsQueryExprImpl.CtsQueryCallImpl("cts", "period-range-query", new Object[]{ axisName, operator, period, options });
    }
    @Override
        public CtsQueryExpr propertiesFragmentQuery(CtsQueryExpr query) {
        return new CtsQueryExprImpl.CtsQueryCallImpl("cts", "properties-fragment-query", new Object[]{ query });
    }
    @Override
        public CtsQueryExpr tripleRangeQuery(XsAnyAtomicTypeSeqVal subject, XsAnyAtomicTypeSeqVal predicate, XsAnyAtomicTypeSeqVal object) {
        return new CtsQueryExprImpl.CtsQueryCallImpl("cts", "triple-range-query", new Object[]{ subject, predicate, object });
    }
    @Override
        public CtsQueryExpr tripleRangeQuery(XsAnyAtomicTypeSeqVal subject, XsAnyAtomicTypeSeqVal predicate, XsAnyAtomicTypeSeqVal object, String... operator) {
        return tripleRangeQuery(subject, predicate, object, (operator == null) ? null : xs.strings(operator)); 
    }
    @Override
        public CtsQueryExpr tripleRangeQuery(XsAnyAtomicTypeSeqVal subject, XsAnyAtomicTypeSeqVal predicate, XsAnyAtomicTypeSeqVal object, XsStringSeqVal operator) {
        return new CtsQueryExprImpl.CtsQueryCallImpl("cts", "triple-range-query", new Object[]{ subject, predicate, object, operator });
    }
    @Override
        public CtsQueryExpr tripleRangeQuery(XsAnyAtomicTypeSeqVal subject, XsAnyAtomicTypeSeqVal predicate, XsAnyAtomicTypeSeqVal object, XsStringSeqVal operator, XsStringSeqVal options) {
        return new CtsQueryExprImpl.CtsQueryCallImpl("cts", "triple-range-query", new Object[]{ subject, predicate, object, operator, options });
    }
    @Override
        public CtsQueryExpr tripleRangeQuery(XsAnyAtomicTypeSeqVal subject, XsAnyAtomicTypeSeqVal predicate, XsAnyAtomicTypeSeqVal object, XsStringSeqVal operator, XsStringSeqVal options, double weight) {
        return tripleRangeQuery(subject, predicate, object, operator, options, xs.doubleVal(weight)); 
    }
    @Override
        public CtsQueryExpr tripleRangeQuery(XsAnyAtomicTypeSeqVal subject, XsAnyAtomicTypeSeqVal predicate, XsAnyAtomicTypeSeqVal object, XsStringSeqVal operator, XsStringSeqVal options, XsDoubleVal weight) {
        return new CtsQueryExprImpl.CtsQueryCallImpl("cts", "triple-range-query", new Object[]{ subject, predicate, object, operator, options, weight });
    }
    @Override
    public CtsQueryExpr trueQuery() {
        return new CtsQueryExprImpl.CtsQueryCallImpl("cts", "true-query", null);
    }
    @Override
        public CtsQueryExpr wordQuery(String... text) {
        return wordQuery(xs.strings(text)); 
    }
    @Override
        public CtsQueryExpr wordQuery(XsStringSeqVal text) {
        return new CtsQueryExprImpl.CtsQueryCallImpl("cts", "word-query", new Object[]{ text });
    }
    @Override
        public CtsQueryExpr wordQuery(XsStringSeqVal text, XsStringSeqVal options) {
        return new CtsQueryExprImpl.CtsQueryCallImpl("cts", "word-query", new Object[]{ text, options });
    }
    @Override
        public CtsQueryExpr wordQuery(XsStringSeqVal text, XsStringSeqVal options, double weight) {
        return wordQuery(text, options, xs.doubleVal(weight)); 
    }
    @Override
        public CtsQueryExpr wordQuery(XsStringSeqVal text, XsStringSeqVal options, XsDoubleVal weight) {
        return new CtsQueryExprImpl.CtsQueryCallImpl("cts", "word-query", new Object[]{ text, options, weight });
    }
    @Override
        public CtsBoxExpr box(double south, double west, double north, double east) {
        return box(xs.doubleVal(south), xs.doubleVal(west), xs.doubleVal(north), xs.doubleVal(east)); 
    }
    @Override
        public CtsBoxExpr box(XsDoubleVal south, XsDoubleVal west, XsDoubleVal north, XsDoubleVal east) {
        return new CtsQueryExprImpl.CtsBoxCallImpl("cts", "box", new Object[]{ south, west, north, east });
    }
    @Override
        public CtsCircleExpr circle(double radius, CtsPointExpr center) {
        return circle(xs.doubleVal(radius), center); 
    }
    @Override
        public CtsCircleExpr circle(XsDoubleVal radius, CtsPointExpr center) {
        return new CtsQueryExprImpl.CtsCircleCallImpl("cts", "circle", new Object[]{ radius, center });
    }
    @Override
        public CtsPointExpr point(double latitude, double longitude) {
        return point(xs.doubleVal(latitude), xs.doubleVal(longitude)); 
    }
    @Override
        public CtsPointExpr point(XsDoubleVal latitude, XsDoubleVal longitude) {
        return new CtsQueryExprImpl.CtsPointCallImpl("cts", "point", new Object[]{ latitude, longitude });
    }
    @Override
        public CtsPolygonExpr polygon(XsAnyAtomicTypeSeqVal vertices) {
        return new CtsQueryExprImpl.CtsPolygonCallImpl("cts", "polygon", new Object[]{ vertices });
    }
    @Override
        public CtsReferenceExpr collectionReference() {
        return new CtsQueryExprImpl.CtsReferenceCallImpl("cts", "collection-reference", new Object[]{  });
    }
    @Override
        public CtsReferenceExpr collectionReference(String... options) {
        return collectionReference((options == null) ? null : xs.strings(options)); 
    }
    @Override
        public CtsReferenceExpr collectionReference(XsStringSeqVal options) {
        return new CtsQueryExprImpl.CtsReferenceCallImpl("cts", "collection-reference", new Object[]{ options });
    }
    @Override
        public CtsReferenceExpr elementAttributeReference(XsQNameVal element, XsQNameVal attribute) {
        return new CtsQueryExprImpl.CtsReferenceCallImpl("cts", "element-attribute-reference", new Object[]{ element, attribute });
    }
    @Override
        public CtsReferenceExpr elementAttributeReference(XsQNameVal element, XsQNameVal attribute, String... options) {
        return elementAttributeReference(element, attribute, (options == null) ? null : xs.strings(options)); 
    }
    @Override
        public CtsReferenceExpr elementAttributeReference(XsQNameVal element, XsQNameVal attribute, XsStringSeqVal options) {
        return new CtsQueryExprImpl.CtsReferenceCallImpl("cts", "element-attribute-reference", new Object[]{ element, attribute, options });
    }
    @Override
        public CtsReferenceExpr elementReference(XsQNameVal element) {
        return new CtsQueryExprImpl.CtsReferenceCallImpl("cts", "element-reference", new Object[]{ element });
    }
    @Override
        public CtsReferenceExpr elementReference(XsQNameVal element, String... options) {
        return elementReference(element, (options == null) ? null : xs.strings(options)); 
    }
    @Override
        public CtsReferenceExpr elementReference(XsQNameVal element, XsStringSeqVal options) {
        return new CtsQueryExprImpl.CtsReferenceCallImpl("cts", "element-reference", new Object[]{ element, options });
    }
    @Override
        public CtsReferenceExpr fieldReference(String field) {
        return fieldReference(xs.string(field)); 
    }
    @Override
        public CtsReferenceExpr fieldReference(XsStringVal field) {
        return new CtsQueryExprImpl.CtsReferenceCallImpl("cts", "field-reference", new Object[]{ field });
    }
    @Override
        public CtsReferenceExpr fieldReference(String field, String... options) {
        return fieldReference(xs.string(field), (options == null) ? null : xs.strings(options)); 
    }
    @Override
        public CtsReferenceExpr fieldReference(XsStringVal field, XsStringSeqVal options) {
        return new CtsQueryExprImpl.CtsReferenceCallImpl("cts", "field-reference", new Object[]{ field, options });
    }
    @Override
        public CtsReferenceExpr jsonPropertyReference(String property) {
        return jsonPropertyReference(xs.string(property)); 
    }
    @Override
        public CtsReferenceExpr jsonPropertyReference(XsStringVal property) {
        return new CtsQueryExprImpl.CtsReferenceCallImpl("cts", "json-property-reference", new Object[]{ property });
    }
    @Override
        public CtsReferenceExpr jsonPropertyReference(String property, String... options) {
        return jsonPropertyReference(xs.string(property), (options == null) ? null : xs.strings(options)); 
    }
    @Override
        public CtsReferenceExpr jsonPropertyReference(XsStringVal property, XsStringSeqVal options) {
        return new CtsQueryExprImpl.CtsReferenceCallImpl("cts", "json-property-reference", new Object[]{ property, options });
    }
    @Override
        public CtsReferenceExpr pathReference(String pathExpression) {
        return pathReference(xs.string(pathExpression)); 
    }
    @Override
        public CtsReferenceExpr pathReference(XsStringVal pathExpression) {
        return new CtsQueryExprImpl.CtsReferenceCallImpl("cts", "path-reference", new Object[]{ pathExpression });
    }
    @Override
        public CtsReferenceExpr pathReference(String pathExpression, String... options) {
        return pathReference(xs.string(pathExpression), (options == null) ? null : xs.strings(options)); 
    }
    @Override
        public CtsReferenceExpr pathReference(XsStringVal pathExpression, XsStringSeqVal options) {
        return new CtsQueryExprImpl.CtsReferenceCallImpl("cts", "path-reference", new Object[]{ pathExpression, options });
    }
    @Override
        public CtsReferenceExpr pathReference(String pathExpression, String options, MapMapExpr map) {
        return pathReference(xs.string(pathExpression), (options == null) ? null : xs.strings(options), map); 
    }
    @Override
        public CtsReferenceExpr pathReference(XsStringVal pathExpression, XsStringSeqVal options, MapMapExpr map) {
        return new CtsQueryExprImpl.CtsReferenceCallImpl("cts", "path-reference", new Object[]{ pathExpression, options, map });
    }
    @Override
    public CtsReferenceExpr uriReference() {
        return new CtsQueryExprImpl.CtsReferenceCallImpl("cts", "uri-reference", null);
    }     @Override
    public CtsBoxSeqExpr box(CtsBoxExpr... items) {
        return new CtsBoxSeqListImpl(items);
    }
     @Override
    public CtsCircleSeqExpr circle(CtsCircleExpr... items) {
        return new CtsCircleSeqListImpl(items);
    }
     @Override
    public CtsPeriodSeqExpr period(CtsPeriodExpr... items) {
        return new CtsPeriodSeqListImpl(items);
    }
     @Override
    public CtsPointSeqExpr point(CtsPointExpr... items) {
        return new CtsPointSeqListImpl(items);
    }
     @Override
    public CtsPolygonSeqExpr polygon(CtsPolygonExpr... items) {
        return new CtsPolygonSeqListImpl(items);
    }
     @Override
    public CtsQuerySeqExpr query(CtsQueryExpr... items) {
        return new CtsQuerySeqListImpl(items);
    }
     @Override
    public CtsReferenceSeqExpr reference(CtsReferenceExpr... items) {
        return new CtsReferenceSeqListImpl(items);
    }
     @Override
    public CtsRegionSeqExpr region(CtsRegionExpr... items) {
        return new CtsRegionSeqListImpl(items);
    }
        static class CtsBoxSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements CtsBoxSeqExpr {
            CtsBoxSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class CtsBoxSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements CtsBoxSeqExpr {
            CtsBoxSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class CtsBoxCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements CtsBoxExpr {
            CtsBoxCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class CtsCircleSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements CtsCircleSeqExpr {
            CtsCircleSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class CtsCircleSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements CtsCircleSeqExpr {
            CtsCircleSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class CtsCircleCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements CtsCircleExpr {
            CtsCircleCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class CtsPeriodSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements CtsPeriodSeqExpr {
            CtsPeriodSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class CtsPeriodSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements CtsPeriodSeqExpr {
            CtsPeriodSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class CtsPeriodCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements CtsPeriodExpr {
            CtsPeriodCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class CtsPointSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements CtsPointSeqExpr {
            CtsPointSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class CtsPointSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements CtsPointSeqExpr {
            CtsPointSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class CtsPointCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements CtsPointExpr {
            CtsPointCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class CtsPolygonSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements CtsPolygonSeqExpr {
            CtsPolygonSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class CtsPolygonSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements CtsPolygonSeqExpr {
            CtsPolygonSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class CtsPolygonCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements CtsPolygonExpr {
            CtsPolygonCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class CtsQuerySeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements CtsQuerySeqExpr {
            CtsQuerySeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class CtsQuerySeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements CtsQuerySeqExpr {
            CtsQuerySeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class CtsQueryCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements CtsQueryExpr {
            CtsQueryCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class CtsReferenceSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements CtsReferenceSeqExpr {
            CtsReferenceSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class CtsReferenceSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements CtsReferenceSeqExpr {
            CtsReferenceSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class CtsReferenceCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements CtsReferenceExpr {
            CtsReferenceCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class CtsRegionSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements CtsRegionSeqExpr {
            CtsRegionSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class CtsRegionSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements CtsRegionSeqExpr {
            CtsRegionSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class CtsRegionCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements CtsRegionExpr {
            CtsRegionCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }

}
