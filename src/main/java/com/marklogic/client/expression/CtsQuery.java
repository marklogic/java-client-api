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
package com.marklogic.client.expression;

// TODO: single import
import com.marklogic.client.expression.BaseType;

import com.marklogic.client.expression.XsValue;


// IMPORTANT: Do not edit. This file is generated. 
public interface CtsQuery {
    public CtsQuery.QueryExpr andNotQuery(CtsQuery.QueryExpr positiveQuery, CtsQuery.QueryExpr negativeQuery);
    public CtsQuery.QueryExpr andQuery(CtsQuery.QueryExpr... queries);
    public CtsQuery.QueryExpr andQuery(CtsQuery.QuerySeqExpr queries);
    public CtsQuery.QueryExpr andQuery(CtsQuery.QuerySeqExpr queries, String... options);
    public CtsQuery.QueryExpr andQuery(CtsQuery.QuerySeqExpr queries, XsValue.StringSeqVal options);
    public CtsQuery.QueryExpr boostQuery(CtsQuery.QueryExpr matchingQuery, CtsQuery.QueryExpr boostingQuery);
    public CtsQuery.BoxExpr box(double south, double west, double north, double east);
    public CtsQuery.BoxExpr box(XsValue.DoubleVal south, XsValue.DoubleVal west, XsValue.DoubleVal north, XsValue.DoubleVal east);
    public CtsQuery.CircleExpr circle(double radius, CtsQuery.PointExpr center);
    public CtsQuery.CircleExpr circle(XsValue.DoubleVal radius, CtsQuery.PointExpr center);
    public CtsQuery.QueryExpr collectionQuery(String... uris);
    public CtsQuery.QueryExpr collectionQuery(XsValue.StringSeqVal uris);
    public CtsQuery.QueryExpr directoryQuery(String... uris);
    public CtsQuery.QueryExpr directoryQuery(XsValue.StringSeqVal uris);
    public CtsQuery.QueryExpr directoryQuery(String uris, String depth);
    public CtsQuery.QueryExpr directoryQuery(XsValue.StringSeqVal uris, XsValue.StringVal depth);
    public CtsQuery.QueryExpr documentFragmentQuery(CtsQuery.QueryExpr query);
    public CtsQuery.QueryExpr documentQuery(String... uris);
    public CtsQuery.QueryExpr documentQuery(XsValue.StringSeqVal uris);
    public CtsQuery.QueryExpr elementAttributePairGeospatialQuery(XsValue.QNameSeqVal elementName, XsValue.QNameSeqVal latitudeAttributeNames, XsValue.QNameSeqVal longitudeAttributeNames, CtsQuery.RegionSeqExpr regions);
    public CtsQuery.QueryExpr elementAttributePairGeospatialQuery(XsValue.QNameSeqVal elementName, XsValue.QNameSeqVal latitudeAttributeNames, XsValue.QNameSeqVal longitudeAttributeNames, CtsQuery.RegionSeqExpr regions, String... options);
    public CtsQuery.QueryExpr elementAttributePairGeospatialQuery(XsValue.QNameSeqVal elementName, XsValue.QNameSeqVal latitudeAttributeNames, XsValue.QNameSeqVal longitudeAttributeNames, CtsQuery.RegionSeqExpr regions, XsValue.StringSeqVal options);
    public CtsQuery.QueryExpr elementAttributePairGeospatialQuery(XsValue.QNameSeqVal elementName, XsValue.QNameSeqVal latitudeAttributeNames, XsValue.QNameSeqVal longitudeAttributeNames, CtsQuery.RegionSeqExpr regions, String options, double weight);
    public CtsQuery.QueryExpr elementAttributePairGeospatialQuery(XsValue.QNameSeqVal elementName, XsValue.QNameSeqVal latitudeAttributeNames, XsValue.QNameSeqVal longitudeAttributeNames, CtsQuery.RegionSeqExpr regions, XsValue.StringSeqVal options, XsValue.DoubleVal weight);
    public CtsQuery.QueryExpr elementAttributeRangeQuery(XsValue.QNameSeqVal elementName, XsValue.QNameSeqVal attributeName, String operator, XsValue.AnyAtomicTypeVal... value);
    public CtsQuery.QueryExpr elementAttributeRangeQuery(XsValue.QNameSeqVal elementName, XsValue.QNameSeqVal attributeName, XsValue.StringVal operator, XsValue.AnyAtomicTypeSeqVal value);
    public CtsQuery.QueryExpr elementAttributeRangeQuery(XsValue.QNameSeqVal elementName, XsValue.QNameSeqVal attributeName, String operator, XsValue.AnyAtomicTypeSeqVal value, String... options);
    public CtsQuery.QueryExpr elementAttributeRangeQuery(XsValue.QNameSeqVal elementName, XsValue.QNameSeqVal attributeName, XsValue.StringVal operator, XsValue.AnyAtomicTypeSeqVal value, XsValue.StringSeqVal options);
    public CtsQuery.QueryExpr elementAttributeRangeQuery(XsValue.QNameSeqVal elementName, XsValue.QNameSeqVal attributeName, String operator, XsValue.AnyAtomicTypeSeqVal value, String options, double weight);
    public CtsQuery.QueryExpr elementAttributeRangeQuery(XsValue.QNameSeqVal elementName, XsValue.QNameSeqVal attributeName, XsValue.StringVal operator, XsValue.AnyAtomicTypeSeqVal value, XsValue.StringSeqVal options, XsValue.DoubleVal weight);
    public CtsQuery.QueryExpr elementAttributeValueQuery(XsValue.QNameSeqVal elementName, XsValue.QNameSeqVal attributeName, String... text);
    public CtsQuery.QueryExpr elementAttributeValueQuery(XsValue.QNameSeqVal elementName, XsValue.QNameSeqVal attributeName, XsValue.StringSeqVal text);
    public CtsQuery.QueryExpr elementAttributeValueQuery(XsValue.QNameSeqVal elementName, XsValue.QNameSeqVal attributeName, String text, String... options);
    public CtsQuery.QueryExpr elementAttributeValueQuery(XsValue.QNameSeqVal elementName, XsValue.QNameSeqVal attributeName, XsValue.StringSeqVal text, XsValue.StringSeqVal options);
    public CtsQuery.QueryExpr elementAttributeValueQuery(XsValue.QNameSeqVal elementName, XsValue.QNameSeqVal attributeName, String text, String options, double weight);
    public CtsQuery.QueryExpr elementAttributeValueQuery(XsValue.QNameSeqVal elementName, XsValue.QNameSeqVal attributeName, XsValue.StringSeqVal text, XsValue.StringSeqVal options, XsValue.DoubleVal weight);
    public CtsQuery.QueryExpr elementAttributeWordQuery(XsValue.QNameSeqVal elementName, XsValue.QNameSeqVal attributeName);
    public CtsQuery.QueryExpr elementAttributeWordQuery(XsValue.QNameSeqVal elementName, XsValue.QNameSeqVal attributeName, String... text);
    public CtsQuery.QueryExpr elementAttributeWordQuery(XsValue.QNameSeqVal elementName, XsValue.QNameSeqVal attributeName, XsValue.StringSeqVal text);
    public CtsQuery.QueryExpr elementAttributeWordQuery(XsValue.QNameSeqVal elementName, XsValue.QNameSeqVal attributeName, String text, String... options);
    public CtsQuery.QueryExpr elementAttributeWordQuery(XsValue.QNameSeqVal elementName, XsValue.QNameSeqVal attributeName, XsValue.StringSeqVal text, XsValue.StringSeqVal options);
    public CtsQuery.QueryExpr elementAttributeWordQuery(XsValue.QNameSeqVal elementName, XsValue.QNameSeqVal attributeName, String text, String options, double weight);
    public CtsQuery.QueryExpr elementAttributeWordQuery(XsValue.QNameSeqVal elementName, XsValue.QNameSeqVal attributeName, XsValue.StringSeqVal text, XsValue.StringSeqVal options, XsValue.DoubleVal weight);
    public CtsQuery.QueryExpr elementChildGeospatialQuery(XsValue.QNameSeqVal parentElementName, XsValue.QNameSeqVal childElementNames, CtsQuery.RegionSeqExpr regions);
    public CtsQuery.QueryExpr elementChildGeospatialQuery(XsValue.QNameSeqVal parentElementName, XsValue.QNameSeqVal childElementNames, CtsQuery.RegionSeqExpr regions, String... options);
    public CtsQuery.QueryExpr elementChildGeospatialQuery(XsValue.QNameSeqVal parentElementName, XsValue.QNameSeqVal childElementNames, CtsQuery.RegionSeqExpr regions, XsValue.StringSeqVal options);
    public CtsQuery.QueryExpr elementChildGeospatialQuery(XsValue.QNameSeqVal parentElementName, XsValue.QNameSeqVal childElementNames, CtsQuery.RegionSeqExpr regions, String options, double weight);
    public CtsQuery.QueryExpr elementChildGeospatialQuery(XsValue.QNameSeqVal parentElementName, XsValue.QNameSeqVal childElementNames, CtsQuery.RegionSeqExpr regions, XsValue.StringSeqVal options, XsValue.DoubleVal weight);
    public CtsQuery.QueryExpr elementGeospatialQuery(XsValue.QNameSeqVal elementName, CtsQuery.RegionSeqExpr regions);
    public CtsQuery.QueryExpr elementGeospatialQuery(XsValue.QNameSeqVal elementName, CtsQuery.RegionSeqExpr regions, String... options);
    public CtsQuery.QueryExpr elementGeospatialQuery(XsValue.QNameSeqVal elementName, CtsQuery.RegionSeqExpr regions, XsValue.StringSeqVal options);
    public CtsQuery.QueryExpr elementGeospatialQuery(XsValue.QNameSeqVal elementName, CtsQuery.RegionSeqExpr regions, String options, double weight);
    public CtsQuery.QueryExpr elementGeospatialQuery(XsValue.QNameSeqVal elementName, CtsQuery.RegionSeqExpr regions, XsValue.StringSeqVal options, XsValue.DoubleVal weight);
    public CtsQuery.QueryExpr elementPairGeospatialQuery(XsValue.QNameSeqVal elementName, XsValue.QNameSeqVal latitudeElementNames, XsValue.QNameSeqVal longitudeElementNames, CtsQuery.RegionSeqExpr regions);
    public CtsQuery.QueryExpr elementPairGeospatialQuery(XsValue.QNameSeqVal elementName, XsValue.QNameSeqVal latitudeElementNames, XsValue.QNameSeqVal longitudeElementNames, CtsQuery.RegionSeqExpr regions, String... options);
    public CtsQuery.QueryExpr elementPairGeospatialQuery(XsValue.QNameSeqVal elementName, XsValue.QNameSeqVal latitudeElementNames, XsValue.QNameSeqVal longitudeElementNames, CtsQuery.RegionSeqExpr regions, XsValue.StringSeqVal options);
    public CtsQuery.QueryExpr elementPairGeospatialQuery(XsValue.QNameSeqVal elementName, XsValue.QNameSeqVal latitudeElementNames, XsValue.QNameSeqVal longitudeElementNames, CtsQuery.RegionSeqExpr regions, String options, double weight);
    public CtsQuery.QueryExpr elementPairGeospatialQuery(XsValue.QNameSeqVal elementName, XsValue.QNameSeqVal latitudeElementNames, XsValue.QNameSeqVal longitudeElementNames, CtsQuery.RegionSeqExpr regions, XsValue.StringSeqVal options, XsValue.DoubleVal weight);
    public CtsQuery.QueryExpr elementQuery(XsValue.QNameSeqVal elementName, CtsQuery.QueryExpr query);
    public CtsQuery.QueryExpr elementRangeQuery(XsValue.QNameSeqVal elementName, String operator, XsValue.AnyAtomicTypeVal... value);
    public CtsQuery.QueryExpr elementRangeQuery(XsValue.QNameSeqVal elementName, XsValue.StringVal operator, XsValue.AnyAtomicTypeSeqVal value);
    public CtsQuery.QueryExpr elementRangeQuery(XsValue.QNameSeqVal elementName, String operator, XsValue.AnyAtomicTypeSeqVal value, String... options);
    public CtsQuery.QueryExpr elementRangeQuery(XsValue.QNameSeqVal elementName, XsValue.StringVal operator, XsValue.AnyAtomicTypeSeqVal value, XsValue.StringSeqVal options);
    public CtsQuery.QueryExpr elementRangeQuery(XsValue.QNameSeqVal elementName, String operator, XsValue.AnyAtomicTypeSeqVal value, String options, double weight);
    public CtsQuery.QueryExpr elementRangeQuery(XsValue.QNameSeqVal elementName, XsValue.StringVal operator, XsValue.AnyAtomicTypeSeqVal value, XsValue.StringSeqVal options, XsValue.DoubleVal weight);
    public CtsQuery.QueryExpr elementValueQuery(XsValue.QNameSeqVal elementName);
    public CtsQuery.QueryExpr elementValueQuery(XsValue.QNameSeqVal elementName, String... text);
    public CtsQuery.QueryExpr elementValueQuery(XsValue.QNameSeqVal elementName, XsValue.StringSeqVal text);
    public CtsQuery.QueryExpr elementValueQuery(XsValue.QNameSeqVal elementName, String text, String... options);
    public CtsQuery.QueryExpr elementValueQuery(XsValue.QNameSeqVal elementName, XsValue.StringSeqVal text, XsValue.StringSeqVal options);
    public CtsQuery.QueryExpr elementValueQuery(XsValue.QNameSeqVal elementName, String text, String options, double weight);
    public CtsQuery.QueryExpr elementValueQuery(XsValue.QNameSeqVal elementName, XsValue.StringSeqVal text, XsValue.StringSeqVal options, XsValue.DoubleVal weight);
    public CtsQuery.QueryExpr elementWordQuery(XsValue.QNameSeqVal elementName, String... text);
    public CtsQuery.QueryExpr elementWordQuery(XsValue.QNameSeqVal elementName, XsValue.StringSeqVal text);
    public CtsQuery.QueryExpr elementWordQuery(XsValue.QNameSeqVal elementName, String text, String... options);
    public CtsQuery.QueryExpr elementWordQuery(XsValue.QNameSeqVal elementName, XsValue.StringSeqVal text, XsValue.StringSeqVal options);
    public CtsQuery.QueryExpr elementWordQuery(XsValue.QNameSeqVal elementName, String text, String options, double weight);
    public CtsQuery.QueryExpr elementWordQuery(XsValue.QNameSeqVal elementName, XsValue.StringSeqVal text, XsValue.StringSeqVal options, XsValue.DoubleVal weight);
    public CtsQuery.QueryExpr falseQuery();
    public CtsQuery.QueryExpr fieldRangeQuery(String fieldName, String operator, XsValue.AnyAtomicTypeVal... value);
    public CtsQuery.QueryExpr fieldRangeQuery(XsValue.StringSeqVal fieldName, XsValue.StringVal operator, XsValue.AnyAtomicTypeSeqVal value);
    public CtsQuery.QueryExpr fieldRangeQuery(String fieldName, String operator, XsValue.AnyAtomicTypeSeqVal value, String... options);
    public CtsQuery.QueryExpr fieldRangeQuery(XsValue.StringSeqVal fieldName, XsValue.StringVal operator, XsValue.AnyAtomicTypeSeqVal value, XsValue.StringSeqVal options);
    public CtsQuery.QueryExpr fieldRangeQuery(String fieldName, String operator, XsValue.AnyAtomicTypeSeqVal value, String options, double weight);
    public CtsQuery.QueryExpr fieldRangeQuery(XsValue.StringSeqVal fieldName, XsValue.StringVal operator, XsValue.AnyAtomicTypeSeqVal value, XsValue.StringSeqVal options, XsValue.DoubleVal weight);
    public CtsQuery.QueryExpr fieldValueQuery(String fieldName, XsValue.AnyAtomicTypeVal... text);
    public CtsQuery.QueryExpr fieldValueQuery(XsValue.StringSeqVal fieldName, XsValue.AnyAtomicTypeSeqVal text);
    public CtsQuery.QueryExpr fieldValueQuery(String fieldName, XsValue.AnyAtomicTypeSeqVal text, String... options);
    public CtsQuery.QueryExpr fieldValueQuery(XsValue.StringSeqVal fieldName, XsValue.AnyAtomicTypeSeqVal text, XsValue.StringSeqVal options);
    public CtsQuery.QueryExpr fieldValueQuery(String fieldName, XsValue.AnyAtomicTypeSeqVal text, String options, double weight);
    public CtsQuery.QueryExpr fieldValueQuery(XsValue.StringSeqVal fieldName, XsValue.AnyAtomicTypeSeqVal text, XsValue.StringSeqVal options, XsValue.DoubleVal weight);
    public CtsQuery.QueryExpr fieldWordQuery(String fieldName, String... text);
    public CtsQuery.QueryExpr fieldWordQuery(XsValue.StringSeqVal fieldName, XsValue.StringSeqVal text);
    public CtsQuery.QueryExpr fieldWordQuery(String fieldName, String text, String... options);
    public CtsQuery.QueryExpr fieldWordQuery(XsValue.StringSeqVal fieldName, XsValue.StringSeqVal text, XsValue.StringSeqVal options);
    public CtsQuery.QueryExpr fieldWordQuery(String fieldName, String text, String options, double weight);
    public CtsQuery.QueryExpr fieldWordQuery(XsValue.StringSeqVal fieldName, XsValue.StringSeqVal text, XsValue.StringSeqVal options, XsValue.DoubleVal weight);
    public CtsQuery.QueryExpr jsonPropertyChildGeospatialQuery(String parentPropertyName, String childPropertyNames, CtsQuery.RegionExpr... regions);
    public CtsQuery.QueryExpr jsonPropertyChildGeospatialQuery(XsValue.StringSeqVal parentPropertyName, XsValue.StringSeqVal childPropertyNames, CtsQuery.RegionSeqExpr regions);
    public CtsQuery.QueryExpr jsonPropertyChildGeospatialQuery(String parentPropertyName, String childPropertyNames, CtsQuery.RegionSeqExpr regions, String... options);
    public CtsQuery.QueryExpr jsonPropertyChildGeospatialQuery(XsValue.StringSeqVal parentPropertyName, XsValue.StringSeqVal childPropertyNames, CtsQuery.RegionSeqExpr regions, XsValue.StringSeqVal options);
    public CtsQuery.QueryExpr jsonPropertyChildGeospatialQuery(String parentPropertyName, String childPropertyNames, CtsQuery.RegionSeqExpr regions, String options, double weight);
    public CtsQuery.QueryExpr jsonPropertyChildGeospatialQuery(XsValue.StringSeqVal parentPropertyName, XsValue.StringSeqVal childPropertyNames, CtsQuery.RegionSeqExpr regions, XsValue.StringSeqVal options, XsValue.DoubleVal weight);
    public CtsQuery.QueryExpr jsonPropertyGeospatialQuery(String propertyName, CtsQuery.RegionExpr... regions);
    public CtsQuery.QueryExpr jsonPropertyGeospatialQuery(XsValue.StringSeqVal propertyName, CtsQuery.RegionSeqExpr regions);
    public CtsQuery.QueryExpr jsonPropertyGeospatialQuery(String propertyName, CtsQuery.RegionSeqExpr regions, String... options);
    public CtsQuery.QueryExpr jsonPropertyGeospatialQuery(XsValue.StringSeqVal propertyName, CtsQuery.RegionSeqExpr regions, XsValue.StringSeqVal options);
    public CtsQuery.QueryExpr jsonPropertyGeospatialQuery(String propertyName, CtsQuery.RegionSeqExpr regions, String options, double weight);
    public CtsQuery.QueryExpr jsonPropertyGeospatialQuery(XsValue.StringSeqVal propertyName, CtsQuery.RegionSeqExpr regions, XsValue.StringSeqVal options, XsValue.DoubleVal weight);
    public CtsQuery.QueryExpr jsonPropertyPairGeospatialQuery(String propertyName, String latitudePropertyNames, String longitudePropertyNames, CtsQuery.RegionExpr... regions);
    public CtsQuery.QueryExpr jsonPropertyPairGeospatialQuery(XsValue.StringSeqVal propertyName, XsValue.StringSeqVal latitudePropertyNames, XsValue.StringSeqVal longitudePropertyNames, CtsQuery.RegionSeqExpr regions);
    public CtsQuery.QueryExpr jsonPropertyPairGeospatialQuery(String propertyName, String latitudePropertyNames, String longitudePropertyNames, CtsQuery.RegionSeqExpr regions, String... options);
    public CtsQuery.QueryExpr jsonPropertyPairGeospatialQuery(XsValue.StringSeqVal propertyName, XsValue.StringSeqVal latitudePropertyNames, XsValue.StringSeqVal longitudePropertyNames, CtsQuery.RegionSeqExpr regions, XsValue.StringSeqVal options);
    public CtsQuery.QueryExpr jsonPropertyPairGeospatialQuery(String propertyName, String latitudePropertyNames, String longitudePropertyNames, CtsQuery.RegionSeqExpr regions, String options, double weight);
    public CtsQuery.QueryExpr jsonPropertyPairGeospatialQuery(XsValue.StringSeqVal propertyName, XsValue.StringSeqVal latitudePropertyNames, XsValue.StringSeqVal longitudePropertyNames, CtsQuery.RegionSeqExpr regions, XsValue.StringSeqVal options, XsValue.DoubleVal weight);
    public CtsQuery.QueryExpr jsonPropertyRangeQuery(String propertyName, String operator, XsValue.AnyAtomicTypeVal... value);
    public CtsQuery.QueryExpr jsonPropertyRangeQuery(XsValue.StringSeqVal propertyName, XsValue.StringVal operator, XsValue.AnyAtomicTypeSeqVal value);
    public CtsQuery.QueryExpr jsonPropertyRangeQuery(String propertyName, String operator, XsValue.AnyAtomicTypeSeqVal value, String... options);
    public CtsQuery.QueryExpr jsonPropertyRangeQuery(XsValue.StringSeqVal propertyName, XsValue.StringVal operator, XsValue.AnyAtomicTypeSeqVal value, XsValue.StringSeqVal options);
    public CtsQuery.QueryExpr jsonPropertyRangeQuery(String propertyName, String operator, XsValue.AnyAtomicTypeSeqVal value, String options, double weight);
    public CtsQuery.QueryExpr jsonPropertyRangeQuery(XsValue.StringSeqVal propertyName, XsValue.StringVal operator, XsValue.AnyAtomicTypeSeqVal value, XsValue.StringSeqVal options, XsValue.DoubleVal weight);
    public CtsQuery.QueryExpr jsonPropertyScopeQuery(String propertyName, CtsQuery.QueryExpr query);
    public CtsQuery.QueryExpr jsonPropertyScopeQuery(XsValue.StringSeqVal propertyName, CtsQuery.QueryExpr query);
    public CtsQuery.QueryExpr jsonPropertyValueQuery(String propertyName, XsValue.AnyAtomicTypeVal... value);
    public CtsQuery.QueryExpr jsonPropertyValueQuery(XsValue.StringSeqVal propertyName, XsValue.AnyAtomicTypeSeqVal value);
    public CtsQuery.QueryExpr jsonPropertyValueQuery(String propertyName, XsValue.AnyAtomicTypeSeqVal value, String... options);
    public CtsQuery.QueryExpr jsonPropertyValueQuery(XsValue.StringSeqVal propertyName, XsValue.AnyAtomicTypeSeqVal value, XsValue.StringSeqVal options);
    public CtsQuery.QueryExpr jsonPropertyValueQuery(String propertyName, XsValue.AnyAtomicTypeSeqVal value, String options, double weight);
    public CtsQuery.QueryExpr jsonPropertyValueQuery(XsValue.StringSeqVal propertyName, XsValue.AnyAtomicTypeSeqVal value, XsValue.StringSeqVal options, XsValue.DoubleVal weight);
    public CtsQuery.QueryExpr jsonPropertyWordQuery(String propertyName, String... text);
    public CtsQuery.QueryExpr jsonPropertyWordQuery(XsValue.StringSeqVal propertyName, XsValue.StringSeqVal text);
    public CtsQuery.QueryExpr jsonPropertyWordQuery(String propertyName, String text, String... options);
    public CtsQuery.QueryExpr jsonPropertyWordQuery(XsValue.StringSeqVal propertyName, XsValue.StringSeqVal text, XsValue.StringSeqVal options);
    public CtsQuery.QueryExpr jsonPropertyWordQuery(String propertyName, String text, String options, double weight);
    public CtsQuery.QueryExpr jsonPropertyWordQuery(XsValue.StringSeqVal propertyName, XsValue.StringSeqVal text, XsValue.StringSeqVal options, XsValue.DoubleVal weight);
    public CtsQuery.QueryExpr locksFragmentQuery(CtsQuery.QueryExpr query);
    public CtsQuery.QueryExpr locksQuery(CtsQuery.QueryExpr arg1);
    public CtsQuery.QueryExpr lsqtQuery(String temporalCollection);
    public CtsQuery.QueryExpr lsqtQuery(XsValue.StringVal temporalCollection);
    public CtsQuery.QueryExpr lsqtQuery(String temporalCollection, XsValue.DateTimeVal timestamp);
    public CtsQuery.QueryExpr lsqtQuery(XsValue.StringVal temporalCollection, XsValue.DateTimeVal timestamp);
    public CtsQuery.QueryExpr lsqtQuery(String temporalCollection, XsValue.DateTimeVal timestamp, String... options);
    public CtsQuery.QueryExpr lsqtQuery(XsValue.StringVal temporalCollection, XsValue.DateTimeVal timestamp, XsValue.StringSeqVal options);
    public CtsQuery.QueryExpr lsqtQuery(String temporalCollection, XsValue.DateTimeVal timestamp, String options, double weight);
    public CtsQuery.QueryExpr lsqtQuery(XsValue.StringVal temporalCollection, XsValue.DateTimeVal timestamp, XsValue.StringSeqVal options, XsValue.DoubleVal weight);
    public CtsQuery.QueryExpr nearQuery(CtsQuery.QueryExpr... queries);
    public CtsQuery.QueryExpr nearQuery(CtsQuery.QuerySeqExpr queries);
    public CtsQuery.QueryExpr nearQuery(CtsQuery.QuerySeqExpr queries, double distance);
    public CtsQuery.QueryExpr nearQuery(CtsQuery.QuerySeqExpr queries, XsValue.DoubleVal distance);
    public CtsQuery.QueryExpr nearQuery(CtsQuery.QuerySeqExpr queries, double distance, String... options);
    public CtsQuery.QueryExpr nearQuery(CtsQuery.QuerySeqExpr queries, XsValue.DoubleVal distance, XsValue.StringSeqVal options);
    public CtsQuery.QueryExpr nearQuery(CtsQuery.QuerySeqExpr queries, double distance, String options, double distanceWeight);
    public CtsQuery.QueryExpr nearQuery(CtsQuery.QuerySeqExpr queries, XsValue.DoubleVal distance, XsValue.StringSeqVal options, XsValue.DoubleVal distanceWeight);
    public CtsQuery.QueryExpr notInQuery(CtsQuery.QueryExpr positiveQuery, CtsQuery.QueryExpr negativeQuery);
    public CtsQuery.QueryExpr notQuery(CtsQuery.QueryExpr query);
    public CtsQuery.QueryExpr orQuery(CtsQuery.QueryExpr... queries);
    public CtsQuery.QueryExpr orQuery(CtsQuery.QuerySeqExpr queries);
    public CtsQuery.QueryExpr orQuery(CtsQuery.QuerySeqExpr queries, String... options);
    public CtsQuery.QueryExpr orQuery(CtsQuery.QuerySeqExpr queries, XsValue.StringSeqVal options);
    public CtsQuery.QueryExpr pathGeospatialQuery(String pathExpression, CtsQuery.RegionExpr... regions);
    public CtsQuery.QueryExpr pathGeospatialQuery(XsValue.StringSeqVal pathExpression, CtsQuery.RegionSeqExpr regions);
    public CtsQuery.QueryExpr pathGeospatialQuery(String pathExpression, CtsQuery.RegionSeqExpr regions, String... options);
    public CtsQuery.QueryExpr pathGeospatialQuery(XsValue.StringSeqVal pathExpression, CtsQuery.RegionSeqExpr regions, XsValue.StringSeqVal options);
    public CtsQuery.QueryExpr pathGeospatialQuery(String pathExpression, CtsQuery.RegionSeqExpr regions, String options, double weight);
    public CtsQuery.QueryExpr pathGeospatialQuery(XsValue.StringSeqVal pathExpression, CtsQuery.RegionSeqExpr regions, XsValue.StringSeqVal options, XsValue.DoubleVal weight);
    public CtsQuery.QueryExpr pathRangeQuery(String pathExpression, String operator, XsValue.AnyAtomicTypeVal... value);
    public CtsQuery.QueryExpr pathRangeQuery(XsValue.StringSeqVal pathExpression, XsValue.StringVal operator, XsValue.AnyAtomicTypeSeqVal value);
    public CtsQuery.QueryExpr pathRangeQuery(String pathExpression, String operator, XsValue.AnyAtomicTypeSeqVal value, String... options);
    public CtsQuery.QueryExpr pathRangeQuery(XsValue.StringSeqVal pathExpression, XsValue.StringVal operator, XsValue.AnyAtomicTypeSeqVal value, XsValue.StringSeqVal options);
    public CtsQuery.QueryExpr pathRangeQuery(String pathExpression, String operator, XsValue.AnyAtomicTypeSeqVal value, String options, double weight);
    public CtsQuery.QueryExpr pathRangeQuery(XsValue.StringSeqVal pathExpression, XsValue.StringVal operator, XsValue.AnyAtomicTypeSeqVal value, XsValue.StringSeqVal options, XsValue.DoubleVal weight);
    public CtsQuery.PeriodExpr period(XsValue.DateTimeVal start);
    public CtsQuery.PeriodExpr period(XsValue.DateTimeVal start, XsValue.DateTimeVal end);
    public CtsQuery.QueryExpr periodCompareQuery(String axis1, String operator, String axis2);
    public CtsQuery.QueryExpr periodCompareQuery(XsValue.StringVal axis1, XsValue.StringVal operator, XsValue.StringVal axis2);
    public CtsQuery.QueryExpr periodCompareQuery(String axis1, String operator, String axis2, String... options);
    public CtsQuery.QueryExpr periodCompareQuery(XsValue.StringVal axis1, XsValue.StringVal operator, XsValue.StringVal axis2, XsValue.StringSeqVal options);
    public CtsQuery.QueryExpr periodRangeQuery(String axisName, String operator);
    public CtsQuery.QueryExpr periodRangeQuery(XsValue.StringSeqVal axisName, XsValue.StringVal operator);
    public CtsQuery.QueryExpr periodRangeQuery(String axisName, String operator, CtsQuery.PeriodExpr... period);
    public CtsQuery.QueryExpr periodRangeQuery(XsValue.StringSeqVal axisName, XsValue.StringVal operator, CtsQuery.PeriodSeqExpr period);
    public CtsQuery.QueryExpr periodRangeQuery(String axisName, String operator, CtsQuery.PeriodSeqExpr period, String... options);
    public CtsQuery.QueryExpr periodRangeQuery(XsValue.StringSeqVal axisName, XsValue.StringVal operator, CtsQuery.PeriodSeqExpr period, XsValue.StringSeqVal options);
    public CtsQuery.PointExpr point(double latitude, double longitude);
    public CtsQuery.PointExpr point(XsValue.DoubleVal latitude, XsValue.DoubleVal longitude);
    public CtsQuery.PolygonExpr polygon(XsValue.AnyAtomicTypeSeqVal vertices);
    public CtsQuery.QueryExpr propertiesFragmentQuery(CtsQuery.QueryExpr query);
    public CtsQuery.QueryExpr propertiesQuery(CtsQuery.QueryExpr arg1);
    public CtsQuery.QueryExpr tripleRangeQuery(XsValue.AnyAtomicTypeSeqVal subject, XsValue.AnyAtomicTypeSeqVal predicate, XsValue.AnyAtomicTypeSeqVal object);
    public CtsQuery.QueryExpr tripleRangeQuery(XsValue.AnyAtomicTypeSeqVal subject, XsValue.AnyAtomicTypeSeqVal predicate, XsValue.AnyAtomicTypeSeqVal object, String... operator);
    public CtsQuery.QueryExpr tripleRangeQuery(XsValue.AnyAtomicTypeSeqVal subject, XsValue.AnyAtomicTypeSeqVal predicate, XsValue.AnyAtomicTypeSeqVal object, XsValue.StringSeqVal operator);
    public CtsQuery.QueryExpr tripleRangeQuery(XsValue.AnyAtomicTypeSeqVal subject, XsValue.AnyAtomicTypeSeqVal predicate, XsValue.AnyAtomicTypeSeqVal object, String operator, String... options);
    public CtsQuery.QueryExpr tripleRangeQuery(XsValue.AnyAtomicTypeSeqVal subject, XsValue.AnyAtomicTypeSeqVal predicate, XsValue.AnyAtomicTypeSeqVal object, XsValue.StringSeqVal operator, XsValue.StringSeqVal options);
    public CtsQuery.QueryExpr tripleRangeQuery(XsValue.AnyAtomicTypeSeqVal subject, XsValue.AnyAtomicTypeSeqVal predicate, XsValue.AnyAtomicTypeSeqVal object, String operator, String options, double weight);
    public CtsQuery.QueryExpr tripleRangeQuery(XsValue.AnyAtomicTypeSeqVal subject, XsValue.AnyAtomicTypeSeqVal predicate, XsValue.AnyAtomicTypeSeqVal object, XsValue.StringSeqVal operator, XsValue.StringSeqVal options, XsValue.DoubleVal weight);
    public CtsQuery.QueryExpr trueQuery();
    public CtsQuery.QueryExpr wordQuery(String... text);
    public CtsQuery.QueryExpr wordQuery(XsValue.StringSeqVal text);
    public CtsQuery.QueryExpr wordQuery(String text, String... options);
    public CtsQuery.QueryExpr wordQuery(XsValue.StringSeqVal text, XsValue.StringSeqVal options);
    public CtsQuery.QueryExpr wordQuery(String text, String options, double weight);
    public CtsQuery.QueryExpr wordQuery(XsValue.StringSeqVal text, XsValue.StringSeqVal options, XsValue.DoubleVal weight);     public CtsQuery.BoxSeqExpr box(CtsQuery.BoxExpr... items);
     public CtsQuery.CircleSeqExpr circle(CtsQuery.CircleExpr... items);
     public CtsQuery.PeriodSeqExpr period(CtsQuery.PeriodExpr... items);
     public CtsQuery.PointSeqExpr point(CtsQuery.PointExpr... items);
     public CtsQuery.PolygonSeqExpr polygon(CtsQuery.PolygonExpr... items);
     public CtsQuery.QuerySeqExpr query(CtsQuery.QueryExpr... items);
     public CtsQuery.RegionSeqExpr region(CtsQuery.RegionExpr... items);
        public interface BoxSeqExpr extends RegionSeqExpr { }
        public interface BoxExpr extends BoxSeqExpr, RegionExpr { }
         public interface CircleSeqExpr extends RegionSeqExpr { }
        public interface CircleExpr extends CircleSeqExpr, RegionExpr { }
         public interface PeriodSeqExpr extends BaseType.ItemSeqExpr { }
        public interface PeriodExpr extends PeriodSeqExpr, BaseType.ItemExpr { }
         public interface PointSeqExpr extends RegionSeqExpr { }
        public interface PointExpr extends PointSeqExpr, RegionExpr { }
         public interface PolygonSeqExpr extends RegionSeqExpr { }
        public interface PolygonExpr extends PolygonSeqExpr, RegionExpr { }
         public interface QuerySeqExpr extends BaseType.ItemSeqExpr { }
        public interface QueryExpr extends QuerySeqExpr, BaseType.ItemExpr { }
         public interface RegionSeqExpr extends BaseType.ItemSeqExpr { }
        public interface RegionExpr extends RegionSeqExpr, BaseType.ItemExpr { }

}
