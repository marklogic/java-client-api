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
