package com.marklogic.client.config.marker;

import java.util.List;

import com.marklogic.client.config.QueryOptionsBuilder.QueryAnnotation;

/**
 * Classes that can 
 */
public interface QueryAnnotations {


	public List<QueryAnnotation> getAnnotations();
    public void addElementAsAnnotation(org.w3c.dom.Element element);
    public void deleteAnnotations();

}
