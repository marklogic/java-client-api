package com.marklogic.client.config.search.impl;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.w3c.dom.Element;

import com.marklogic.client.config.search.IndexReference;
import com.marklogic.client.config.search.JAXBBackedQueryOption;
import com.marklogic.client.config.search.jaxb.Annotation;
import com.marklogic.client.config.search.jaxb.Constraint;




abstract class AbstractQueryOption<T> implements JAXBBackedQueryOption {

	protected IndexReference indexReferenceImpl;
	protected T jaxbObject;
	

	@Override
	public Object asJaxbObject() {
		return jaxbObject;
	}

	/*
	 * Index convenience methods.  Will throw NullPointerException for elements that don't support IndexReferences.
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
	
	public List<Element> getAnnotations(Constraint parentObject) {
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
	

	public void addAnnotation(Element annotation) {
		getJAXBChildren().add(annotation);
	}

	public List<Element> getAnnotations() {
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
		
		
