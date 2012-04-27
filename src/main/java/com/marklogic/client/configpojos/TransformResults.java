package com.marklogic.client.configpojos;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;

import org.w3c.dom.Element;

@XmlAccessorType(XmlAccessType.FIELD)
//TODO examine schema for this -- not sufficient for all the use cases we have.
public class TransformResults {

	@XmlAttribute
	private String ns;
	@XmlAttribute
	private String at;
	@XmlAttribute
	private String apply;
	public String getNs() {
		return ns;
	}
	public void setNs(String ns) {
		this.ns = ns;
	}
	public String getAt() {
		return at;
	}
	public void setAt(String at) {
		this.at = at;
	}
	public String getApply() {
		return apply;
	}
	public void setApply(String apply) {
		this.apply = apply;
	}

	@XmlAnyElement
	private List<Element> children;

	public TransformResults() {
		
	}

	public TransformResults withNs(String ns) {
		setNs(ns);
		return this;
	}


	public TransformResults withApply(String apply) {
		setApply(apply);
		return this;
	}

	public TransformResults withAt(String at) {
		setAt(at);
		return this;
	}


}
