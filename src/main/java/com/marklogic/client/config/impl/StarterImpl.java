/*
 * Copyright 2012 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.marklogic.client.config.impl;

import javax.xml.namespace.QName;

import com.marklogic.client.config.Starter;
import com.marklogic.client.config.Joiner.Tokenize;

public class StarterImpl implements Starter {

	private com.marklogic.client.config.search.jaxb.Starter jaxbObject;

	StarterImpl(com.marklogic.client.config.search.jaxb.Starter starter) {
		this.jaxbObject = starter;
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
	public String getDelimiter() {
		return jaxbObject.getDelimiter();
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
	public void setDelimiter(String delimiter) {
		jaxbObject.setDelimiter(delimiter);
	}

	@Override
	public void setTokenize(Tokenize tokenize) {
		jaxbObject.setTokenize(tokenize.toString());
	}

}
