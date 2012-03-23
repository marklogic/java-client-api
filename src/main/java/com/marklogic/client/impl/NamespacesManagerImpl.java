package com.marklogic.client.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.marklogic.client.EditableNamespaceContext;
import com.marklogic.client.NamespacesManager;

class NamespacesManagerImpl implements NamespacesManager {
	static final private Logger logger = LoggerFactory.getLogger(NamespacesManagerImpl.class);

	private RESTServices services;

	private final static Pattern NAMESPACE_PATTERN = Pattern.compile(
		"<([^: >]+:)?namespace-uri(\\s[^>]+)?>([^<>]+)</([^: >]+:)?namespace-uri>"
		);

	NamespacesManagerImpl(RESTServices services) {
		super();
		this.services = services;
	}

	@Override
	public String readPrefix(String prefix) {
		if (prefix == null)
			throw new IllegalArgumentException("Cannot read namespace for null prefix");

		String binding = services.getValue("config/namespaces", prefix, "application/xml", String.class);
		if (binding == null)
			return null;

		Matcher matcher = NAMESPACE_PATTERN.matcher(binding);
		if (!matcher.find()) {
			logger.warn("Failed to extract namespace from {}", binding);
			return null;
		}

		return matcher.toMatchResult().group(3);
	}
	@Override
	public EditableNamespaceContext readAll() {
		EditableNamespaceContext context = new EditableNamespaceContext();

		try {
			InputStream stream = services.getValues("config/namespaces", "application/xml", InputStream.class);
			if (stream == null)
				return null;

			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			factory.setValidating(false);

			Document document = factory.newDocumentBuilder().parse(stream);
			NodeList bindings =
				document.getElementsByTagNameNS("http://marklogic.com/xdmp/group", "namespace");
			if (bindings == null)
				return null;
	
			int bindingsCount = bindings.getLength();
			if (bindingsCount < 1)
				return null;

			for (int i=0; i < bindingsCount; i++) {
				Node binding = bindings.item(i);

				NodeList children = binding.getChildNodes();
				if (children == null)
					continue;

				String prefix = null;
				String namespaceUri = null;
				for (int j=0; j < children.getLength(); j++) {
					Node child = children.item(j);
					if (child.getNodeType() != Node.ELEMENT_NODE)
						continue;

					Element element = (Element) child;
					if ("prefix".equals(element.getLocalName()))
						prefix = element.getTextContent();
					else if ("namespace-uri".equals(element.getLocalName()))
						namespaceUri = element.getTextContent();
				}
				if (prefix == null || namespaceUri == null)
					continue;

				context.put(prefix, namespaceUri);
			}
		} catch (SAXException e) {
			logger.error("Failed to parse DOM document for namespace bindings",e);
			throw new RuntimeException(e);
		} catch (IOException e) {
			logger.error("Failed to parse DOM document for namespace bindings",e);
			throw new RuntimeException(e);
		} catch (ParserConfigurationException e) {
			logger.error("Failed to parse DOM document for namespace bindings",e);
			throw new RuntimeException(e);
		}

		return context;
	}
	@Override
	public void addPrefix(String prefix, String namespaceUri) {
		if (prefix == null)
			throw new IllegalArgumentException("Cannot write binding for null prefix");
		if (namespaceUri == null)
			throw new IllegalArgumentException("Cannot write binding for null namespaceUri");

		String structure =
			"<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"+
			"<namespace xmlns=\"http://marklogic.com/xdmp/group\">\n"+
			"    <prefix>"+prefix+"</prefix>\n"+
			"    <namespace-uri>"+namespaceUri+"</namespace-uri>\n"+
			"</namespace>\n";

		services.postValue("config/namespaces", prefix, "application/xml", structure);
	}
	@Override
	public void updatePrefix(String prefix, String namespaceUri) {
		if (prefix == null)
			throw new IllegalArgumentException("Cannot write binding for null prefix");
		if (namespaceUri == null)
			throw new IllegalArgumentException("Cannot write binding for null namespaceUri");

		String structure =
			"<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"+
			"<namespace xmlns=\"http://marklogic.com/xdmp/group\">\n"+
			"    <prefix>"+prefix+"</prefix>\n"+
			"    <namespace-uri>"+namespaceUri+"</namespace-uri>\n"+
			"</namespace>\n";

		services.putValue("config/namespaces", prefix, "application/xml", structure);
	}
	@Override
	public void deletePrefix(String prefix) {
		if (prefix == null)
			throw new IllegalArgumentException("Cannot delete binding for null prefix");

		services.deleteValue("config/namespaces", prefix);
	}
	@Override
	public void deleteAll() {
		services.deleteValues("config/namespaces");
	}
}
