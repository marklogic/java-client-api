package com.marklogic.client.config.search.impl;

import java.util.List;

import com.marklogic.client.config.search.GeoElementPairConstraint;
import com.marklogic.client.config.search.jaxb.Constraint;
import com.marklogic.client.config.search.jaxb.GeoElemPair;

public class GeoElementPairConstraintImpl extends GeospatialConstraintImpl<GeoElemPair> implements GeoElementPairConstraint {

	public GeoElementPairConstraintImpl(String name) {
		super(name);
	}

    GeoElementPairConstraintImpl(Constraint constraint,
			GeoElemPair constraintSpec) {
		super(constraint);
		jaxbObject = constraintSpec;
		heatmap = new HeatmapImpl((com.marklogic.client.config.search.jaxb.Heatmap) JAXBHelper.getOneJAXBByElementName(this, "heatmap"));

	}
	
	@Override
	public List<Object> getJAXBChildren() {
		return jaxbObject.getParentOrLatOrLon();
	}


}
