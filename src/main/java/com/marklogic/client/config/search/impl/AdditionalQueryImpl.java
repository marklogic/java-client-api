package com.marklogic.client.config.search.impl;

import org.w3c.dom.Element;

import com.marklogic.client.config.search.AdditionalQuery;
import com.marklogic.client.config.search.SearchOption;

public class AdditionalQueryImpl extends AtomicSearchOption implements SearchOption, AdditionalQuery {

	com.marklogic.client.config.search.jaxb.AdditionalQuery jaxbObject;
	
	public AdditionalQueryImpl(
			com.marklogic.client.config.search.jaxb.AdditionalQuery ot) {
		jaxbObject = ot;
		}

	@Override
	public Object asJaxbObject() {
		return jaxbObject;
	}

	@Override
	public void setQuery(Element ctsQuery) {		
		jaxbObject.getValue().setAny(ctsQuery);
}
	@Override
	public Element getQuery() {
		return jaxbObject.getValue().getAny();
	}


}
