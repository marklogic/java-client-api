package com.marklogic.client.config.search.impl;

import java.util.List;

import org.w3c.dom.Element;

import com.marklogic.client.config.search.MarkLogicBindingException;
import com.marklogic.client.config.search.PropertiesConstraint;
import com.marklogic.client.config.search.jaxb.Constraint;
import com.marklogic.client.config.search.jaxb.Properties;


public class PropertiesConstraintImpl extends ConstraintImpl implements PropertiesConstraint {

	private Properties jaxbObject;
	
	public PropertiesConstraintImpl(Constraint constraint, Properties props) {
		super(constraint);
		jaxbObject = props;
	}
	
	public PropertiesConstraintImpl(String name) {
		super(name);
		jaxbObject = new Properties();
		jaxbConstraint.getConstraint().add(jaxbObject);
	}


	@Override
	public void addAnnotation(Element annotation) {
		throw new MarkLogicBindingException("Annotations not allowed on PropertiesConstraint.");
	}

	@Override
	public List<Element> getAnnotations() {
		throw new MarkLogicBindingException("Annotations not allowed on PropertiesConstraint.");
	}

	@Override
	public List<Object> getJAXBChildren() {
		return null;
	}

}
