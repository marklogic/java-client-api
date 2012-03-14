package com.marklogic.client.config.search;




public class Constraint implements SearchOption {

	protected com.marklogic.client.config.search.jaxb.Constraint jaxbConstraint;

	public Constraint(String name) {
		jaxbConstraint = new com.marklogic.client.config.search.jaxb.Constraint();
		jaxbConstraint.setName(name);
	}

	public Object asJaxbObject() {
		return jaxbConstraint;
	}

	public String getName() {
		return jaxbConstraint.getName();
	}



}
