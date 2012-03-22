package com.marklogic.client.config.search;

import java.util.List;

import org.w3c.dom.Element;

public interface Annotation {

	public void add(Element element);
	public void clearAnnotations();
	public void setContent(List<Element> elementList);
	public List<Element> getContent();
	
}
