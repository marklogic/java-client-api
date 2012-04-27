package com.marklogic.client.configpojos;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class State {

	@XmlAttribute(name="name")
	private String name;
	
	@XmlElement(namespace=Options.SEARCH_NS, name="sort-order")
	private List<SortOrder> sortOrders;
	
	public State() {
		sortOrders = new ArrayList<SortOrder>();
	}
	
	public State(String name) {
		this();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public List<SortOrder> getSortOrders() {
		return sortOrders;
	}
	
	public State withSortOrder(SortOrder sortOrder) {
		sortOrders.add(sortOrder);
		return this;
	}

}
