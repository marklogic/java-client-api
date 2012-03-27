package com.marklogic.client.config.search.impl;

import java.util.List;

import com.marklogic.client.config.search.Grammar;
import com.marklogic.client.config.search.QueryOption;

public class GrammarImpl implements QueryOption, Grammar {


	private com.marklogic.client.config.search.jaxb.Grammar jaxbObject;
	
	public GrammarImpl(com.marklogic.client.config.search.jaxb.Grammar ot) {
		jaxbObject = ot;
	}

	@Override
	public Object asJaxbObject() {
		return jaxbObject;
	}

	@Override
	public List<Object> getJAXBChildren() {
		return jaxbObject.getQuotationOrImplicitOrJoiner();
	}

}
