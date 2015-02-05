/*
 * Copyright 2012-2015 MarkLogic Corporation
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
package com.marklogic.client.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.marklogic.client.util.EditableNamespaceContext;
import com.marklogic.client.FailedRequestException;
import com.marklogic.client.ForbiddenUserException;
import com.marklogic.client.MarkLogicInternalException;
import com.marklogic.client.admin.NamespacesManager;
import com.marklogic.client.ResourceNotFoundException;

class NamespacesManagerImpl
    extends AbstractLoggingManager
    implements NamespacesManager
{
	static final private Logger logger = LoggerFactory.getLogger(NamespacesManagerImpl.class);

	static final private Pattern NAMESPACE_PATTERN = Pattern.compile(
		"<([^: >]+:)?uri(\\s[^>]+)?>([^<>]+)</([^: >]+:)?uri>"
		);

	private RESTServices services;

	NamespacesManagerImpl(RESTServices services) {
		super();
		this.services = services;
	}

	@Override
	public String readPrefix(String prefix) throws ForbiddenUserException, FailedRequestException {
		if (prefix == null)
			throw new IllegalArgumentException("Cannot read namespace for null prefix");
		if (prefix.length() == 0)
			throw new IllegalArgumentException("Server does not maintain a default namespace");

		String binding = services.getValue(
				requestLogger, "config/namespaces", prefix, true, "application/xml", String.class);
		if (binding == null)
			return null;

		Matcher matcher = NAMESPACE_PATTERN.matcher(binding);
		if (!matcher.find()) {
			if (logger.isWarnEnabled())
				logger.warn("Failed to extract namespace from {}", binding);
			return null;
		}

		return matcher.toMatchResult().group(3);
	}
	@Override
	public NamespaceContext readAll() throws ForbiddenUserException, FailedRequestException {
		EditableNamespaceContext context = new EditableNamespaceContext();

		try {
			InputStream stream = services.getValues(requestLogger, "config/namespaces", "application/xml", InputStream.class);
			if (stream == null)
				return null;

			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			factory.setValidating(false);

			Document document = factory.newDocumentBuilder().parse(stream);
			NodeList bindings =
				document.getElementsByTagNameNS("http://marklogic.com/rest-api", "namespace");
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
					else if ("uri".equals(element.getLocalName()))
						namespaceUri = element.getTextContent();
				}
				if (prefix == null || namespaceUri == null)
					continue;

				context.put(prefix, namespaceUri);
			}
		} catch (SAXException e) {
			logger.error("Failed to parse DOM document for namespace bindings",e);
			throw new MarkLogicInternalException(e);
		} catch (IOException e) {
			logger.error("Failed to parse DOM document for namespace bindings",e);
			throw new MarkLogicInternalException(e);
		} catch (ParserConfigurationException e) {
			logger.error("Failed to parse DOM document for namespace bindings",e);
			throw new MarkLogicInternalException(e);
		}

		return context;
	}
	@Override
	public void addPrefix(String prefix, String namespaceUri) throws ForbiddenUserException, FailedRequestException {
		if (prefix == null)
			throw new IllegalArgumentException("Cannot write binding for null prefix");
		if (prefix.length() == 0)
			throw new IllegalArgumentException("Cannot specify a default namespace");
		if (namespaceUri == null)
			throw new IllegalArgumentException("Cannot write binding for null namespaceUri");

		String structure =
			"<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"+
			"<namespace-bindings xmlns=\"http://marklogic.com/rest-api\">" +
			"<namespace>\n"+
			"    <prefix>"+prefix+"</prefix>\n"+
			"    <uri>"+namespaceUri+"</uri>\n"+
			"</namespace>\n"+
			"</namespace-bindings>";

		services.postValue(requestLogger, "config/namespaces", prefix, "application/xml", structure);
	}
	@Override
	public void updatePrefix(String prefix, String namespaceUri) throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
		if (prefix == null)
			throw new IllegalArgumentException("Cannot write binding for null prefix");
		if (prefix.length() == 0)
			throw new IllegalArgumentException("Cannot specify a default namespace");
		if (namespaceUri == null)
			throw new IllegalArgumentException("Cannot write binding for null namespaceUri");

		String structure =
			"<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"+
			"<namespace xmlns=\"http://marklogic.com/rest-api\">\n"+
			"    <prefix>"+prefix+"</prefix>\n"+
			"    <uri>"+namespaceUri+"</uri>\n"+
			"</namespace>\n";

		services.putValue(requestLogger, "config/namespaces", prefix, "application/xml", structure);
	}
	@Override
	public void deletePrefix(String prefix) throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
		if (prefix == null)
			throw new IllegalArgumentException("Cannot delete binding for null prefix");
		if (prefix.length() == 0)
			throw new IllegalArgumentException("Server does not maintain a default namespace");

		services.deleteValue(requestLogger, "config/namespaces", prefix);
	}
	@Override
	public void deleteAll() throws ForbiddenUserException, FailedRequestException {
		services.deleteValues(requestLogger, "config/namespaces");
	}
}
