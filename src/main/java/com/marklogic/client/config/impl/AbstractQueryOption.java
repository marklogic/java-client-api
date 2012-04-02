package com.marklogic.client.config.impl;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.w3c.dom.Element;

import com.marklogic.client.config.IndexReference;
import com.marklogic.client.config.JAXBBackedQueryOption;
import com.marklogic.client.config.search.jaxb.Annotation;
import com.marklogic.client.config.search.jaxb.Constraint;




/**
 * A class containing useful methods shared amongst most objects in the 
 * query configuration.  
 */
abstract class AbstractQueryOption<T> implements JAXBBackedQueryOption {

	/**
	 * Used only by subclasses that implement Indexable, but here as a convenience.
	 */
	protected IndexReference indexReferenceImpl;
	protected T jaxbObject;
	

	@Override
	public Object asJAXB() {
		return jaxbObject;
	}

	/*
	 * Index convenience methods.  Throws NullPointerException for elements that don't support IndexReferences.
	 * TODO put in another location.  May not be necessary at all.
	 */
	public void addElementAttributeIndex(QName elementQName, QName attributeQName) {
		indexReferenceImpl.addElementAttributeIndex(elementQName, attributeQName);
	}


	public void addElementIndex(QName elementQName) {
		indexReferenceImpl.addElementIndex(elementQName);
	}
	
	public void addFieldIndex(String fieldName) {
		indexReferenceImpl.addFieldIndex(fieldName);
	}
	
	public IndexReference getIndex() {
		return indexReferenceImpl;
	}
	
	
	public void addAnnotation(Constraint parentObject, Element annotation) {
		parentObject.getConstraint().add(annotation);
	}
	
	protected List<Element> getAnnotations(Constraint parentObject) {
		List<Element> l = new ArrayList<Element>();
		List<Object> children = parentObject.getConstraint();
		for (Object o : children ) {
			if (o instanceof Annotation) {
				Annotation a = (Annotation) o;
				for (Object elementObject : a.getContent()) {
					if (elementObject instanceof Element) {
						Element e = (Element) elementObject;
						l.add(e);
					} else if (elementObject instanceof String) {
						// FIXME don't drop strings
					}
				}
			}
		}
		return l;
	}
	

	public void addQueryAnnotation(Element annotation) {
		getJAXBChildren().add(annotation);
	}

	public List<Element> getQueryAnnotations() {
		List<Element> l = new ArrayList<Element>();
		List<Object> children = getJAXBChildren();
		for (Object o : children) {
			if (o instanceof Annotation) {
				Annotation a = (Annotation) o;
				for (Object elementObject : a.getContent()) {
					if (elementObject instanceof Element) {
						Element e = (Element) elementObject;
						l.add(e);
					} else if (elementObject instanceof String) {
						// FIXME don't drop strings
					}
				}
			}
		}
		return l;
	}

	private List<String> getStringOptions(String localName) {
		List<Object> children = getJAXBChildren();
		List<String> options = new ArrayList<String>();
		for (Object o : children) {
			if (o instanceof JAXBElement<?>) {
				JAXBElement<String> termElement = (JAXBElement<String>) o;
				if (termElement.getName() == JAXBHelper.newQNameFor(localName)) {
					options.add(termElement.getValue());
				}
			}
			else if (o instanceof String) {
				options.add((String) o); 
			}
		}
		return options;
	}
	
	private void setStringOptions(String localName, List<String> options) {
		List<Object> children = getJAXBChildren();
		for (Object o : children) {
			if (o instanceof JAXBElement<?>) {
				JAXBElement<String> jaxbStringElement = (JAXBElement<String>) o;
				if (jaxbStringElement.getName() == JAXBHelper.newQNameFor(localName)) {
					children.remove(o);
				}
			}
		}
		for (String option : options) {
			getJAXBChildren().add(JAXBHelper.wrapString(JAXBHelper.newQNameFor(localName), option));
		}
	}

	public List<String> getTermOptions() {
		return getStringOptions("term-option");
	}

	public void setTermOptions(List<String> termOptions) {
		setStringOptions("term-option", termOptions);
	}
	

	public List<String> getGeoOptions() {
		return getStringOptions("geo-option");
	}

	public void setGeoOptions(List<String> geoOptions) {
		setStringOptions("geo-option", geoOptions);
	}

	
	public List<String> getFacetOptions() {
		return getStringOptions("facet-option");
	}

	public void setFacetOptions(List<String> facetOptions) {
		setStringOptions("facet-option", facetOptions);
	}

}
		
		
