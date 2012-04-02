package com.marklogic.client.config.impl;

import java.util.List;

import com.marklogic.client.config.Anchor;
import com.marklogic.client.config.FunctionRef;

public class AnchorImpl extends AbstractQueryOption<com.marklogic.client.config.search.jaxb.Anchor> implements Anchor {

	
	public AnchorImpl(String name, FunctionRef function) {
		jaxbObject = new com.marklogic.client.config.search.jaxb.Anchor();
		jaxbObject.setApply(function.getApply());
		jaxbObject.setNs(function.getNs());
		jaxbObject.setName(name);
		jaxbObject.setAt(function.getApply());
	}

	AnchorImpl(com.marklogic.client.config.search.jaxb.Anchor anchor) {
		jaxbObject = anchor;
	}

	@Override
	public List<Object> getJAXBChildren() {
		return null;
	}

	@Override
	public void setApply(String apply) {
		jaxbObject.setApply(apply);
	}

	@Override
	public void setNs(String namespace) {
		jaxbObject.setNs(namespace);
	}

	@Override
	public void setAt(String at) {
		jaxbObject.setAt(at);
	}

	@Override
	public String getApply() {
		return jaxbObject.getApply();
	}

	@Override
	public String getNs() {
		return jaxbObject.getNs();
	}

	@Override
	public String getAt() {
		return jaxbObject.getAt();
	}


}
