package com.marklogic.client.config.search.impl;

import java.util.List;

import org.w3c.dom.Element;

import com.marklogic.client.config.search.Term;

public class TermImpl extends AbstractQueryOption implements Term {
	
	private com.marklogic.client.config.search.jaxb.Term jaxbObject;
	
	public TermImpl(com.marklogic.client.config.search.jaxb.Term ot) {
		jaxbObject = ot;
	}

	@Override
	public Object asJaxbObject() {
		return jaxbObject;
	}

	@Override
	public List<Object> getJAXBChildren() {
		return jaxbObject.getTermOptionOrEmpty();
	}

	@Override
	public void addAnnotation(Element annotation) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Element> getAnnotations() {
		// TODO Auto-generated method stub
		return null;
	}

}
