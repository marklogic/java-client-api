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
package com.marklogic.client.admin.config.support;

import javax.xml.namespace.QName;

import com.marklogic.client.admin.config.support.GeospatialIndexType;

/**
 * Defines an interface to the methods for storing
 * geospatial values in MarkLogic indexes.
 * Depending on the {@link com.marklogic.client.admin.config.support.GeospatialIndexType}, different
 * methods will actually be used in order to build
 * the geospatial configuration.
 * Used only in {@link com.marklogic.client.admin.config.QueryOptionsBuilder} expressions.
 */
public interface GeospatialSpec {


	public QName getElement();
	
	public GeospatialIndexType getGeospatialIndexType();

	public QName getLatitude();

	public QName getLongitude();
	
	public QName getParent();
	
	public void setElement(QName element);
	
	public void setGeospatialIndexType(GeospatialIndexType geospatialIndexType);

	public void setLatitude(QName latitudeElement);	

	public void setLongitude(QName longitudeElement);

	public void setParent(QName parent);
	
	

}
