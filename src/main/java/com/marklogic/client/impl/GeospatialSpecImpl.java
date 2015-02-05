/*
 * Copyright 2012-2015 MarkLogic Corporation
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

import com.marklogic.client.admin.config.support.GeospatialIndexType;
import com.marklogic.client.admin.config.support.GeospatialSpec;
import com.marklogic.client.admin.config.support.IndexSpecImpl;

import javax.xml.namespace.QName;

/**
 * Implements methods for referencing geospatial indexes.
 * Used in QueryOptionsBuilder expression building.
 */
public class GeospatialSpecImpl extends IndexSpecImpl implements GeospatialSpec {

	private GeospatialIndexType geospatialIndexType;
	private QName parent;
	private QName latitude;
	public GeospatialIndexType getGeospatialIndexType() {
		return geospatialIndexType;
	}

	public void setGeospatialType(GeospatialIndexType geospatialIndexType) {
		this.geospatialIndexType = geospatialIndexType;
	}

	public QName getParent() {
		return parent;
	}

	public QName getLatitude() {
		return latitude;
	}

	public QName getLongitude() {
		return longitude;
	}

	private QName longitude;
	
	public void setGeospatialIndexType(GeospatialIndexType geospatialIndexType) {
		this.geospatialIndexType = geospatialIndexType;
	}

	@Override
	public void setLatitude(QName latitudeElement) {
		this.latitude = latitudeElement;
	}

	@Override
	public void setParent(QName parent) {
		this.parent = parent;
	}

	@Override
	public void setLongitude(QName longitudeElement) {
		this.longitude = longitudeElement;
	}
	

}
