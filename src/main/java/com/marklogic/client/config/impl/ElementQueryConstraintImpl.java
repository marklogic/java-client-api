package com.marklogic.client.config.impl;

import java.util.List;

import org.w3c.dom.Element;

import com.marklogic.client.config.MarkLogicBindingException;
import com.marklogic.client.config.search.jaxb.Constraint;
import com.marklogic.client.config.search.jaxb.ElementQuery;

public class ElementQueryConstraintImpl extends ConstraintImpl<ElementQuery> {

	
	public ElementQueryConstraintImpl(String name) {
		super(name);
		jaxbObject = new ElementQuery();
		jaxbConstraint.getConstraint().add(jaxbObject);
	}

    ElementQueryConstraintImpl(Constraint constraint,
			com.marklogic.client.config.search.jaxb.ElementQuery elementQuery) {
		super(constraint);
		this.jaxbObject = elementQuery;
		jaxbConstraint.getConstraint().add(jaxbObject);

	}

	/**
     * Gets the value of the ns property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNs() {
        return jaxbObject.getNs();
    }

    /**
     * Sets the value of the ns property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNs(String ns) {
        jaxbObject.setNs(ns);
    }


    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return jaxbObject.getName();
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String name) {
        jaxbObject.setName(name);
    }

	@Override
	public List<Object> getJAXBChildren() {
		return null;
	}

	@Override
	public void addQueryAnnotation(Element annotation) {
		throw new MarkLogicBindingException("Annotations not allowed on CollectionConstraint.");
	}

	@Override
	public List<Element> getQueryAnnotations() {
		throw new MarkLogicBindingException("Annotations not allowed on CollectionConstraint.");
	}



}
