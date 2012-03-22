package com.marklogic.client.config.search.impl;

import java.util.List;

import org.w3c.dom.Element;

import com.marklogic.client.config.search.Annotation;

public abstract class AtomicSearchOption extends AbstractSearchOption {

	@Override
	public List<Object> getJAXBChildren() {
		return null;
	}

	@Override
	public void addAnnotation(Element annotation) {
		// TODO anything?
	}

	@Override
	public List<Element> getAnnotations() {
		// TODO Auto-generated method stub
		return null;
	}

}
