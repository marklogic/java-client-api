package com.marklogic.client.configpojos;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

/**
 * Term defines how default search terms (those without without a corresponding constranint) are handled by the Search API
 * @see com.marklogic.client.configpojos.Constraint
 */
public class Term extends XQueryFunctionLocator  {

	@XmlElement(namespace=Options.SEARCH_NS, name="term-option")
	private List<String> termOptions;
	@XmlElement(namespace=Options.SEARCH_NS, name="empty")
	private XQueryFunctionLocator empty;
	
	private XQueryFunctionLocator xQueryFunctionLocator;
	
	@XmlElement(namespace=Options.SEARCH_NS, name="weight")
	private double weight;
	
	@XmlElement(namespace=Options.SEARCH_NS, name="default")
	// TODO formally is Word|Value|Range
	private ConstraintDefinition defaultConstraint;

	public List<String> getTermOptions() {
		return termOptions;
	}

	public XQueryFunctionLocator getTermFunction() {
		return xQueryFunctionLocator;
	}

	public TermApply getEmptyApply() {
		return TermApply.fromXmlString(empty.getApply());
	}
	
	public Term withEmptyApply(TermApply termApply) {
		empty.setApply(termApply.toXmlString());
		return this;
	}
	

	//TODO annotations
}
