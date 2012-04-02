package com.marklogic.client.config.impl;

import java.util.List;

import com.marklogic.client.config.GeoElementConstraint;
import com.marklogic.client.config.search.jaxb.Constraint;
import com.marklogic.client.config.search.jaxb.GeoElem;

public class GeoElementConstraintImpl extends GeospatialConstraintImpl<GeoElem> implements GeoElementConstraint {


	public GeoElementConstraintImpl(String name) {
		super(name);
		jaxbObject = new GeoElem();
	}
	
    GeoElementConstraintImpl(Constraint constraint, GeoElem geoElement) {
		super(constraint);
		jaxbObject = geoElement;
		heatmap = new HeatmapImpl((com.marklogic.client.config.search.jaxb.Heatmap) JAXBHelper.getOneJAXBByElementName(this, "heatmap"));

	}

	@Override
	public List<Object> getJAXBChildren() {
		return jaxbObject.getParentOrElementOrFacetOption();
	}



}
