package com.marklogic.client.config.search.impl;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.marklogic.client.ElementLocator;
import com.marklogic.client.MarkLogicInternalException;
import com.marklogic.client.config.search.FunctionRef;
import com.marklogic.client.config.search.QueryOption;
import com.marklogic.client.config.search.TransformResults;
import com.marklogic.client.impl.ElementLocatorImpl;
import com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl;

public class TransformResultsImpl implements QueryOption, TransformResults {

	private com.marklogic.client.config.search.jaxb.TransformResults jaxbObject;
	private Document document;

	public TransformResultsImpl(
			com.marklogic.client.config.search.jaxb.TransformResults ot) {
		jaxbObject = ot;
		try {
			document = new DocumentBuilderFactoryImpl().newDocumentBuilder()
					.newDocument();
		} catch (ParserConfigurationException e) {
			throw new MarkLogicInternalException(
					"Could not instantiate document builder");
		}
	}

	@Override
	public Object asJaxbObject() {
		return jaxbObject;
	}

	@Override
	public List<Object> getJAXBChildren() {
		return null;
	}

	@Override
	public String getApply() {
		return jaxbObject.getApply();
	}

	@Override
	public void setApply(String apply) {
		jaxbObject.setApply(apply);
	}

	@Override
	public FunctionRef getTransformFunction() {
		FunctionRef f = new FunctionRefImpl(new QName("", "dummyname"));
		f.setApply(jaxbObject.getApply());
		f.setNs(jaxbObject.getNs());
		f.setAt(jaxbObject.getAt());
		return f;
	}

	@Override
	public void setTransformFunction(FunctionRef function) {
		jaxbObject.setNs(function.getNs());
		jaxbObject.setApply(function.getApply());
		jaxbObject.setAt(function.getAt());
	}

	private long returnValue(String elementName) {
		for (Element e : jaxbObject.getAnyElement()) {
			if (e.getNodeName().equals(elementName)) {
				return Long.parseLong(e.getTextContent());
			}
		}
		return 0;
	}

	private void setOrAddElement(String elementName, long value) {
		for (Element e : jaxbObject.getAnyElement()) {
			if (e.getNodeName().equals(elementName)) {
				e.setTextContent(Long.toString(value));
			}
		}
		jaxbObject.getAnyElement().add(
				document.createElementNS(
						"http://marklogic.com/appervices/search", elementName));
	}

	@Override
	public void setPerMatchTokens(long perMatchTokens) {
		setOrAddElement("search:per-match-tokens", perMatchTokens);
	}

	@Override
	public long getPerMatchTokens() {
		return returnValue("per-match-tokens");
	}

	@Override
	public void setMaxMatches(long maxMatches) {
		setOrAddElement("search:max-matches", maxMatches);
	}

	@Override
	public long getMaxMatches() {
		return returnValue("search:max-matches");
	}

	@Override
	public void setMaxSnippetChars(long maxSnippetChars) {
		setOrAddElement("search:max-snippet-chars", maxSnippetChars);

	}

	@Override
	public long getMaxSnippetChars() {
		return returnValue("search:max-snippet-chars");
	}

	@Override
	public List<ElementLocator> getPreferredElements() {
		List<ElementLocator> l = new ArrayList<ElementLocator>();
		List<Element> children = jaxbObject.getAnyElement();
		for (Element e : children) {
			if (e.getNodeName().equals("search:preferred-elements")) {
				NodeList preferredElementNodes = e.getChildNodes();
				for (int i = 0; i < preferredElementNodes.getLength(); i++) {
					Element elem = (Element) preferredElementNodes.item(i);
					if (elem.getNodeName().equals("search:element")) {
						l.add(new ElementLocatorImpl(new QName(elem
								.getAttribute("ns"), elem.getAttribute("name"))));
					}
				}
			}
		}
		return l;
	}

	@Override
	public void setPreferredElements(List<ElementLocator> elements) {
		List<Element> children = jaxbObject.getAnyElement();
		List<Element> newChildren = new ArrayList<Element>();
		for (Element e : children) {
			if (e.getNodeName().equals("element")) {
				children.remove(e);
			}
		}
		for (ElementLocator el : elements) {
			Element e = document.createElementNS(
					"http://marklogic.com/appservices/search", "element");
			e.setAttribute("ns", el.getElement().getNamespaceURI());
			e.setAttribute("name", el.getElement().getLocalPart());
			children.add(e);
		}
	}

}
