package com.marklogic.client.configpojos;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import org.w3c.dom.Element;

@XmlAccessorType(XmlAccessType.FIELD)
public class Grammar  {

	@XmlElement(namespace=Options.SEARCH_NS, name="quotation")
	private String quotation;
	
	@XmlElement(namespace=Options.SEARCH_NS, name="joiner")
	private List<Joiner> joiners;
	
	@XmlElement(namespace=Options.SEARCH_NS,name="starter")
	private List<Starter> starters;
	
	@XmlElement(namespace=Options.SEARCH_NS,name="implicit")
	private AnyElement implicit;

	public Grammar() {
		joiners = new ArrayList<Joiner>();
		starters = new ArrayList<Starter>();
	}
	public String getQuotation() {
		return quotation;
	}

	public Grammar withQuotation(String quotation) {
		this.quotation = quotation;
		return this;
	}

	public List<Joiner> getJoiners() {
		return joiners;
	}

	public Grammar withJoiner(Joiner joiner) {
		this.joiners.add(joiner);
		return this;
	}

	public List<Starter> getStarters() {
		return starters;
	}

	public Grammar withStarter(Starter starter) {
		this.starters.add(starter);
		return this;
	}

	public Element getImplicit() {
		return implicit.getValue();
	}

	public Grammar withImplicit(Element implicit) {
		this.implicit = new AnyElement(implicit);
		return this;
	}
	
}
