/*
 * Copyright 2012 MarkLogic Corporation
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
package com.marklogic.client.config;

import java.util.List;

public interface GeospatialConstraint extends Constraint {

	public Heatmap getHeatmap();
	public List<String> getGeoOptions();
	public void setHeatmap(Heatmap heatmap);
	public void setGeoOptions(List<String> geoOptions);

	public String getParentNs();
	public void setParentNs(String parentNs);
	public String getParentName();
	public void setParentName(String parentNs);
	
}
