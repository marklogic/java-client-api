package com.marklogic.client.config.impl;

import com.marklogic.client.config.DefaultSuggestionSource;
import com.marklogic.client.config.JAXBBackedQueryOption;

public class DefaultSuggestionSourceImpl extends SuggestionSourceImpl implements
		JAXBBackedQueryOption, DefaultSuggestionSource {

	private com.marklogic.client.config.search.jaxb.DefaultSuggestionSource jaxbObject;
	
	
	DefaultSuggestionSourceImpl(
			com.marklogic.client.config.search.jaxb.DefaultSuggestionSource ot) {
		this.jaxbObject = ot;
	    indexReferenceImpl = new IndexReferenceImpl(jaxbObject.getConstraintTypeOrWordLexiconOrSuggestionOption());
	}

	public DefaultSuggestionSourceImpl() {
       this(new com.marklogic.client.config.search.jaxb.DefaultSuggestionSource());
	}

	@Override
	public Object asJAXB() {
		return jaxbObject;
	}

}
