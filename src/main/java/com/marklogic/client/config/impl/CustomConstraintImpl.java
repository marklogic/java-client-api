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

import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.marklogic.client.config.CustomConstraint;
import com.marklogic.client.config.Facetable;
import com.marklogic.client.config.FunctionRef;
import com.marklogic.client.config.MarkLogicBindingException;
import com.marklogic.client.config.search.jaxb.Constraint;
import com.marklogic.client.config.search.jaxb.Custom;

public class CustomConstraintImpl extends ConstraintImpl<Custom> implements Facetable,
		CustomConstraint {

	private FunctionRefImpl parse, startFacet, finishFacet;

	public CustomConstraintImpl(String name) {
		super(name);
		jaxbObject = new Custom();
		jaxbConstraint.getConstraint().add(jaxbObject);
		setup();
	}

	CustomConstraintImpl(Constraint constraint, Custom custom) {
		super(constraint);
		jaxbObject = custom;
		setup();
	}
	
	private void setup() {

		parse = new FunctionRefImpl(JAXBHelper.newQNameFor("parse"));
		startFacet = new FunctionRefImpl(JAXBHelper.newQNameFor("start-facet"));
		finishFacet = new FunctionRefImpl(
				JAXBHelper.newQNameFor("finish-facet"));

		DocumentBuilder builder;
		try {
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new MarkLogicBindingException(e);
		}
		Node node = builder.newDocument();

		JAXBContext jc;
		try {
			jc = JAXBContext.newInstance("com.marklogic.client.config.search.jaxb");
			Marshaller m = jc.createMarshaller();
			m.marshal(jaxbObject, node);
			NodeList children = node.getFirstChild().getChildNodes();
			for (int i = 0; i < children.getLength(); i++) {
				Node c = children.item(i);
				if (c.getLocalName().equals("parse")) {
					parse.fillFrom(c);
				} else if (c.getLocalName().equals("start-facet")) {
					startFacet.fillFrom(c);
				} else if (c.getLocalName().equals("finish-facet")) {
					finishFacet.fillFrom(c);
				}
			}
		} catch (JAXBException e) {
			throw new MarkLogicBindingException(e);
		}

	}

	@Override
	public void setDoFacets(boolean doFacets) {
		jaxbObject.setFacet(doFacets);
	}

	@Override
	public boolean getDoFacets() {
		return jaxbObject.isSetFacet();
	}

	@Override
	public void addFacetOption(String facetOption) {

		jaxbObject.getParseOrStartFacetOrFinishFacet().add(
				JAXBHelper.wrapString(JAXBHelper.newQNameFor("facet-option"),
						facetOption));

	}


	@Override
	public void setParse(FunctionRef function) {
		jaxbObject.getParseOrStartFacetOrFinishFacet().add(
				JAXBHelper.wrapFunction(JAXBHelper.newQNameFor("parse"),
						function));
	}

	@Override
	public void setStartFacet(FunctionRef function) {
		jaxbObject.getParseOrStartFacetOrFinishFacet().add(
				JAXBHelper.wrapFunction(JAXBHelper.newQNameFor("start-facet"),
						function));
	}

	@Override
	public void setFinishFacet(FunctionRef function) {
		jaxbObject.getParseOrStartFacetOrFinishFacet().add(
				JAXBHelper.wrapFunction(JAXBHelper.newQNameFor("finish-facet"),
						function));
	}

	@Override
	public FunctionRef getParse() {
		return parse;
	}

	@Override
	public FunctionRef getStartFacet() {
		return startFacet;
	}

	@Override
	public FunctionRef getFinishFacet() {
		return finishFacet;
	}

	@Override
	/* Children are handled within this class */
	public List<Object> getJAXBChildren() {
		return null;
	}


	@Override
	public Double getWeight() {
		return JAXBHelper.getOneSimpleByElementName(this ,"weight");
	}
	
	@Override
	public void setWeight(Double weight) {
		JAXBHelper.setOneSimpleByElementName(this, "weight", weight);
	}

}
