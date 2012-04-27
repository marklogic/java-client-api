package com.marklogic.client.configpojos;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.namespace.QName;


@XmlAccessorType(XmlAccessType.FIELD)
public final class Starter  {
	

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

	public Starter() {
		
	}
	public Starter(String text) {
		this.text = text;
	}
	public Starter withStrength(int strength) {
		this.strength = strength;
		return this;
	}
	public Starter withApply(String apply) {
		this.setApply(apply);
		return this;
	}
	public Starter withDelimiter(String delimiter) {
		this.delimiter = delimiter;
		return this;
	}
	
	
}
