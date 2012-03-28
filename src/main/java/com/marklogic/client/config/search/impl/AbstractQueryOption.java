package com.marklogic.client.config.search.impl;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.w3c.dom.Element;

import com.marklogic.client.config.search.Annotate;
import com.marklogic.client.config.search.IndexReference;
import com.marklogic.client.config.search.Indexable;
import com.marklogic.client.config.search.QueryOption;
import com.marklogic.client.config.search.TermOptions;
import com.marklogic.client.config.search.jaxb.Annotation;




public abstract class AbstractQueryOption implements QueryOption, Indexable, TermOptions {

	protected IndexReference indexReferenceImpl;

	/*
	 * Index convenience methods.  Will throw NullPointerException for elements that don't support IndexReferences.
	 * TODO put in another location.  May not be necessary at all.
	 */
	@Override
	public void addElementAttributeIndex(QName elementQName, QName attributeQName) {
		indexReferenceImpl.addElementAttributeIndex(elementQName, attributeQName);
	}


	@Override
	public void addElementIndex(QName elementQName) {
		indexReferenceImpl.addElementIndex(elementQName);
	}
	
	@Override
	public void addFieldIndex(String fieldName) {
		indexReferenceImpl.addFieldIndex(fieldName);
	}
	
	@Override
	public IndexReference getIndex() {
		return indexReferenceImpl;
	}
	
	
	public void addAnnotation(QueryOption parentObject, Element annotation) {
		parentObject.getJAXBChildren().add(annotation);
	}
	
	public List<Element> getAnnotations(QueryOption parentObject) {
		List<Element> l = new ArrayList<Element>();
		List<Object> children = parentObject.getJAXBChildren();
		for (Object o : children ) {
			if (o instanceof com.marklogic.client.config.search.jaxb.Annotation) {
				Annotation a = (Annotation) o;
				for (Object elementObject : a.getContent()) {
					Element e = (Element) elementObject;
					l.add(e);
				}
			}
		}
		return l;
	}


	@Override
	public List<String> getTermOptions() {
		List<Object> children = getJAXBChildren();
		List<String> termOptions = new ArrayList<String>();
		for (Object o : children) {
			if (o instanceof JAXBElement<?>) {
				JAXBElement<String> termElement = (JAXBElement<String>) o;
				if (termElement.getName() == JAXBHelper.newQNameFor("term-option")) {
					termOptions.add(termElement.getValue());
				}
			}
			else if (o instanceof String) {
				termOptions.add((String) o);  //TODO this might not work across elements.
			}
		}
		return termOptions;
	}

	@Override
	public void setTermOptions(List<String> termOptions) {
		List<Object> children = getJAXBChildren();
		for (Object o : children) {
			if (o instanceof JAXBElement<?>) {
				JAXBElement<String> termElement = (JAXBElement<String>) o;
				if (termElement.getName() == JAXBHelper.newQNameFor("term-option")) {
					children.remove(o);
				}
			}
		}
		for (String termOption : termOptions) {
			getJAXBChildren().add(JAXBHelper.wrapString(JAXBHelper.newQNameFor("term-option"), termOption));
		}
	}
	

}
		
		
