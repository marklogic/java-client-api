package com.marklogic.client.configpojos;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * Represents how query terms are to be combined.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Operator {

	@XmlElement(namespace=Options.SEARCH_NS, name="state")
	private List<State> states;
	
	@XmlAttribute
	private String name;
	
	public Operator() {
		states = new ArrayList<State>();
	}
	public List<State> getStates() {
		return states;
	}
	
	public Operator withState(State state) {
		states.add(state);
		return this;
	}

	public String getName() {
		return name;
	}

	
}
