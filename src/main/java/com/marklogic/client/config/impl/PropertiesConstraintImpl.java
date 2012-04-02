package com.marklogic.client.config.impl;

import java.util.List;

import org.w3c.dom.Element;

import com.marklogic.client.config.MarkLogicBindingException;
import com.marklogic.client.config.PropertiesConstraint;
import com.marklogic.client.config.search.jaxb.Constraint;
import com.marklogic.client.config.search.jaxb.Properties;


public class PropertiesConstraintImpl extends ConstraintImpl<Properties> implements PropertiesConstraint {

	
	PropertiesConstraintImpl(Constraint constraint, Properties props) {
		super(constraint);
		jaxbObject = props;
	}
	
	public PropertiesConstraintImpl(String name) {
		super(name);
		jaxbObject = new Properties();
		jaxbConstraint.getConstraint().add(jaxbObject);
	}


	@Override
	public void addQueryAnnotation(Element annotation) {
		throw new MarkLogicBindingException("Annotations not allowed on PropertiesConstraint.");
	}

	@Override
	public List<Element> getQueryAnnotations() {
		throw new MarkLogicBindingException("Annotations not allowed on PropertiesConstraint.");
	}

	@Override
	public List<Object> getJAXBChildren() {
		return null;
	}

}
