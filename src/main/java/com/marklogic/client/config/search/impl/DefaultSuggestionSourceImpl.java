package com.marklogic.client.config.search.impl;

import com.marklogic.client.config.search.DefaultSuggestionSource;
import com.marklogic.client.config.search.JAXBBackedQueryOption;

public class DefaultSuggestionSourceImpl extends SuggestionSourceImpl implements
		JAXBBackedQueryOption, DefaultSuggestionSource {

	private com.marklogic.client.config.search.jaxb.DefaultSuggestionSource jaxbObject;
	
	
	public DefaultSuggestionSourceImpl(
			com.marklogic.client.config.search.jaxb.DefaultSuggestionSource ot) {
		this.jaxbObject = ot;
	    indexReferenceImpl = new IndexReferenceImpl(jaxbObject.getConstraintTypeOrWordLexiconOrSuggestionOption());
	}

	public DefaultSuggestionSourceImpl() {
       this(new com.marklogic.client.config.search.jaxb.DefaultSuggestionSource());
	}

	@Override
	public Object asJaxbObject() {
		return jaxbObject;
	}

}
