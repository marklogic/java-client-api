/*
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

/**
 * Used in {@link com.marklogic.client.admin.config.QueryOptionsBuilder} to help construct configurations for geospatial indexes.
 * Must match the encoding method for geospatial coordinates, wich can be stored as
 * delimited values in a single element, or as values in pairs of elements or attributes.
 */
public enum GeospatialIndexType {

	ELEMENT, ELEMENT_CHILD, ELEMENT_PAIR, ATTRIBUTE_PAIR;
	
}

