package com.marklogic.client.configpojos;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="custom")
@XmlAccessorType(XmlAccessType.FIELD)
public class Custom extends ConstraintDefinition<Custom> {

	@XmlAttribute(name="facet")
	private boolean doFacets;
	
	@XmlElement(namespace=Options.SEARCH_NS, name="finish-facet")
	private XQueryFunctionLocator finishFacet;
	
	@XmlElement(namespace=Options.SEARCH_NS, name="start-facet")
	private XQueryFunctionLocator startFacet;
	
	@XmlElement(namespace=Options.SEARCH_NS, name="parse")
	private XQueryFunctionLocator parse;
	
	
	public XQueryFunctionLocator getFinishFacet() {
		return finishFacet;
	}

	public XQueryFunctionLocator getStartFacet() {
		return startFacet;
	}

	public XQueryFunctionLocator getParse() {
		return parse;
	}

	public boolean getDoFacets() {
		return doFacets;
	}

}
