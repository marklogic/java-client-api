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
package com.marklogic.client.config.impl;

import java.util.List;

import com.marklogic.client.config.GeoAttrPairConstraint;
import com.marklogic.client.config.Heatmap;
import com.marklogic.client.config.search.jaxb.Constraint;
import com.marklogic.client.config.search.jaxb.GeoAttrPair;

public class GeoAttrPairConstraintImpl extends GeospatialConstraintImpl<GeoAttrPair> implements GeoAttrPairConstraint {

	
	public GeoAttrPairConstraintImpl(String name) {
		super(name);
	}

	GeoAttrPairConstraintImpl(Constraint constraint,
			GeoAttrPair constraintSpec) {
		super(constraint);
		jaxbObject = constraintSpec;
		heatmap = JAXBHelper.getOneByClassName(this, Heatmap.class);
	}

	@Override
	public List<Object> getJAXBChildren() {
		return jaxbObject.getParentOrLatOrLon();
	}


}
