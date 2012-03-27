package com.marklogic.client.config.search.impl;

import java.util.List;

import javax.xml.namespace.QName;

import com.marklogic.client.ElementLocator;
import com.marklogic.client.config.search.FunctionRef;
import com.marklogic.client.config.search.QueryOption;
import com.marklogic.client.config.search.TransformResults;

public class TransformResultsImpl implements QueryOption, TransformResults {

	private com.marklogic.client.config.search.jaxb.TransformResults jaxbObject;

	public TransformResultsImpl(
			com.marklogic.client.config.search.jaxb.TransformResults ot) {
		jaxbObject = ot;
	}

	@Override
	public Object asJaxbObject() {
		return jaxbObject;
	}

	@Override
	public List<Object> getJAXBChildren() {
		return null;
	}

	@Override
	public String getApply() {
		return jaxbObject.getApply();
	}

	@Override
	public void setApply(String apply) {
		jaxbObject.setApply(apply);
	}

	@Override
	public FunctionRef getTransformFunction() {
		FunctionRef f = new FunctionRefImpl(new QName("", "dummyname"));
		f.setApply(jaxbObject.getApply());
		f.setNs(jaxbObject.getNs());
		f.setAt(jaxbObject.getAt());
		return f;
	}

	@Override
	public void setTransformFunction(FunctionRef function) {
		jaxbObject.setNs(function.getNs());
		jaxbObject.setApply(function.getApply());
		jaxbObject.setAt(function.getAt());
	}

	@Override
	public void setPerMatchTokens(long perMatchTokens) {
		//;
	}

	@Override
	public long getPerMatchTokens() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setMaxMatches(long maxMatches) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public long getMaxMatches() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setMaxSnippetChars(long maxSnippetChars) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public long getMaxSnippetChars() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<ElementLocator> getPreferredElements() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setPreferredElements(List<ElementLocator> elements) {
		// TODO Auto-generated method stub
		
	}
	
	

}
