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
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.marklogic.client.MarkLogicIOException;
import com.marklogic.client.config.FunctionRef;



public class FunctionRefImpl implements FunctionRef {

	Element elem;

	public FunctionRefImpl(QName elementName) {


		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		factory.setValidating(false);
		Document document;
		try {
			document = factory.newDocumentBuilder().newDocument();
		} catch (ParserConfigurationException e) {
			throw new MarkLogicIOException("Couldn't construct XML element", e);
		}
	    elem = document.createElementNS(elementName.getNamespaceURI(), elementName.getLocalPart());
	}
	

	@Override
	public void setApply(String apply) {
		elem.setAttribute("apply", apply);
	}

	@Override
	public void setNs(String namespace) {
		elem.setAttribute("ns", namespace);
	}

	@Override
	public void setAt(String at) {
		elem.setAttribute("at", at);
	}

	@Override
	public String getApply() {
		return elem.getAttribute("apply");
	}

	@Override
	public String getNs() {
		return elem.getAttribute("ns");
	}

	@Override
	public String getAt() {
		return elem.getAttribute("at");
	}


	public void fillFrom(Node c) {
		this.setApply(c.getAttributes().getNamedItem("apply").getNodeValue());
		this.setNs(c.getAttributes().getNamedItem("ns").getNodeValue());
		this.setAt(c.getAttributes().getNamedItem("at").getNodeValue());
	}

}
