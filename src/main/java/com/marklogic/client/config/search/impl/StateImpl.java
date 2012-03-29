package com.marklogic.client.config.search.impl;

import java.util.List;

import org.w3c.dom.Element;

import com.marklogic.client.config.search.SortOrder;
import com.marklogic.client.config.search.State;
import com.marklogic.client.config.search.TransformResults;

public class StateImpl extends AbstractQueryOption<com.marklogic.client.config.search.jaxb.State> implements State {


	private com.marklogic.client.config.search.jaxb.AdditionalQuery additionalQuery;
	
	public StateImpl(com.marklogic.client.config.search.jaxb.State o) {
		jaxbObject= o;
		additionalQuery = new com.marklogic.client.config.search.jaxb.AdditionalQuery();
	}


	@Override
	public void setAdditionalQuery(Element ctsQuery) {
		additionalQuery.getValue().setAny(ctsQuery);
	}

	@Override
	public Element getAdditionalQuery() {
		return additionalQuery.getValue().getAny();
	}

	@Override
	public List<SortOrder> getSortOrders() {
		return JAXBHelper.getByClassName(this, SortOrder.class);
	}

	@Override
	public TransformResults getTransformResults() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setTransformResults(TransformResults transformResults) {
		JAXBHelper.setOneByClassName(this, transformResults);
	}

	@Override
	public long getPageLength() {
		return JAXBHelper.getOneSimpleByElementName(this, "page-length");
	}

	@Override
	public void setPageLength(Long pageLength) {
		JAXBHelper.setOneSimpleByElementName(this, "page-length", pageLength);
	}

	@Override
	public String getSearchableExpression() {
		return JAXBHelper.getOneSimpleByElementName(this, "searchable-expression");
	}

	@Override
	public void setSearchableExpression(String searchableExpression) {
		JAXBHelper.setOneSimpleByElementName(this, "searchable-expression", searchableExpression);
	}

	@Override
	public double getQualityWeight() {
		return JAXBHelper.getOneSimpleByElementName(this, "quality-weight");
	}

	@Override
	public void setQualityWeight(double qualityWeight) {
		JAXBHelper.setOneSimpleByElementName(this, "quality-weight", qualityWeight);
	}

	@Override
	public List<Long> getForests() {
		return JAXBHelper.getSimpleByElementName(this, "forest");
	}

	@Override
	public void setForests(List<Long> forests) {
		JAXBHelper.setSimpleByElementName(this, "forest", forests);
	}

	@Override
	public void setForest(Long forest) {
		JAXBHelper.setOneSimpleByElementName(this, "forest", forest);
	}

	@Override
	public boolean getDebug() {
		return JAXBHelper.getOneSimpleByElementName(this, "debug");
	}

	@Override
	public void setDebug(Boolean debug) {
		JAXBHelper.setOneSimpleByElementName(this, "debug", debug);
	}

	@Override
	public List<String> getSearchOptions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setSearchOptions(List<String> searchOptions) {
		
	}

	@Override
	public List<Object> getJAXBChildren() {
		return jaxbObject.getAdditionalQueryOrAnnotationOrDebug();
	}

	@Override
	public String getName() {
		return jaxbObject.getName();
	}

	@Override
	public void setName(String name) {
		jaxbObject.setName(name);
	}

}
