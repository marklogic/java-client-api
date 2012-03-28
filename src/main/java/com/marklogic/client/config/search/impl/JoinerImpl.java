package com.marklogic.client.config.search.impl;

import javax.xml.namespace.QName;

import com.marklogic.client.config.search.Joiner;

public class JoinerImpl implements Joiner {

	com.marklogic.client.config.search.jaxb.Joiner jaxbObject;
	
	public JoinerImpl(com.marklogic.client.config.search.jaxb.Joiner joiner) {
		this.jaxbObject = joiner;
	}

	@Override
	public void setApply(String apply) {
		jaxbObject.setApply(apply);
	}

	@Override
	public void setNs(String namespace) {
		jaxbObject.setNs(namespace);
	}

	@Override
	public void setAt(String at) {
		jaxbObject.setAt(at);
	}

	@Override
	public String getApply() {
		return jaxbObject.getApply();
	}

	@Override
	public String getNs() {
		return jaxbObject.getNs();
	}

	@Override
	public String getAt() {
		return jaxbObject.getAt();
	}

	@Override
	public int getStrength() {
		return jaxbObject.getStrength();
	}

	@Override
	public QName getElement() {
		return jaxbObject.getElement();
	}

	@Override
	public String getOptions() {
		return jaxbObject.getOptions();
	}

	@Override
	public String getCompare() {
		return jaxbObject.getCompare();
	}

	@Override
	public int getConsume() {
		return jaxbObject.getConsume();
	}

	@Override
	public String getText() {
		return jaxbObject.getContent();
	}

	@Override
	public Tokenize getTokenize() {
		return Tokenize.valueOf(jaxbObject.getTokenize());
	}

	@Override
	public void setStrength(int strength) {
		jaxbObject.setStrength(strength);
	}

	@Override
	public void setElement(QName element) {
		jaxbObject.setElement(element);
	}

	@Override
	public void setOptions(String options) {
		jaxbObject.setOptions(options);
	}

	@Override
	public void setCompare(String compare) {
		jaxbObject.setCompare(compare);
	}

	@Override
	public void setTokenize(Tokenize tokenize) {
		jaxbObject.setTokenize(tokenize.toString());
	}

	@Override
	public void setText(String text) {
		jaxbObject.setContent(text);
	}


}
