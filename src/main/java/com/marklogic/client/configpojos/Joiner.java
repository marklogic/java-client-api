package com.marklogic.client.configpojos;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.namespace.QName;

@XmlAccessorType(XmlAccessType.FIELD)
public final class Joiner  {


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
	
	@XmlAttribute
	private String delimiter;
	@XmlAttribute
	private int strength;
	@XmlAttribute
	private QName element;
	@XmlAttribute
	private String options;
	@XmlAttribute
	private String tokenize; //TODO enum
	
	@XmlValue
	private String text;
	

	public Joiner() {};
	
	public Joiner(String text) {
		this.text = text;
	}
	public Joiner withStrength(int strength) {
		this.strength = strength;
		return this;
	}

	public Joiner withApply(String apply) {
		this.setApply(apply);
		return this;
	}
	public Joiner withElement(QName qName) {
		this.element = qName;
		return this;
	}
	public String getDelimiter() {
		return delimiter;
	}
	public int getStrength() {
		return strength;
	}
	public QName getElement() {
		return element;
	}
	public String getOptions() {
		return options;
	}
	public String getTokenize() {
		return tokenize;
	}
	public String getText() {
		return text;
	}
	public Joiner withTokenize(String tokenize) {
		this.tokenize = tokenize;
		return this;
	}
}
