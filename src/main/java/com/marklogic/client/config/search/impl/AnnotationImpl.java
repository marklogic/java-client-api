package com.marklogic.client.config.search.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import com.marklogic.client.config.search.Annotation;
import com.marklogic.client.test.QueryOptionsTest;



public class AnnotationImpl extends AtomicSearchOption implements Annotation {

	com.marklogic.client.config.search.jaxb.Annotation jaxbObject;

	Logger logger = (Logger) LoggerFactory.getLogger(AnnotationImpl.class);
	
	AnnotationImpl(com.marklogic.client.config.search.jaxb.Annotation ot) {
		jaxbObject = ot;
	}

	@Override
	public Object asJaxbObject() {
		return jaxbObject;
	}

	@Override
	public void setContent(List<Element> elementList) {
		jaxbObject.getContent().clear();
		for (Element e : elementList) {
		jaxbObject.getContent().add(e);
		}
	}

	@Override
	public List<Element> getContent() {
		List<Element> l = new ArrayList<Element>();
		for (Object j : jaxbObject.getContent()) {
			Element e = (Element) j;
			l.add(e);
			logger.debug("Annotation content is {}", j);
		}
		return l; 
	}

	@Override
	public void add(Element element) {
		jaxbObject.getContent().add(element);
	}

	@Override
	public void clearAnnotations() {
		jaxbObject.getContent().clear();
	}
}
