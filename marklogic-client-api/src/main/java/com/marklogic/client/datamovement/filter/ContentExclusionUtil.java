/*
 * Copyright (c) 2010-2026 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.datamovement.filter;

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.impl.XmlFactories;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

/**
 * Utility class for applying content exclusions to documents before hash calculation.
 * Supports removing specific paths from JSON and XML documents using JSON Pointer and XPath expressions.
 *
 * @since 8.1.0
 */
class ContentExclusionUtil {

	private static final Logger logger = LoggerFactory.getLogger(ContentExclusionUtil.class);
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	/**
	 * Applies JSON Pointer exclusions to JSON content by removing the specified paths.
	 *
	 * @param uri          the document URI (used for logging purposes)
	 * @param jsonContent  the JSON content as a string
	 * @param jsonPointers array of RFC 6901 JSON Pointer expressions identifying properties to exclude
	 * @return the modified JSON content with specified paths removed
	 * @throws JsonProcessingException if the JSON content cannot be parsed or serialized
	 */
	static String applyJsonExclusions(String uri, String jsonContent, String[] jsonPointers) throws JsonProcessingException {
		if (jsonPointers == null || jsonPointers.length == 0) {
			return jsonContent;
		}

		JsonNode rootNode = OBJECT_MAPPER.readTree(jsonContent);
		for (String jsonPointer : jsonPointers) {
			removeNodeAtPointer(uri, rootNode, jsonPointer);
		}
		return OBJECT_MAPPER.writeValueAsString(rootNode);
	}

	/**
	 * Removes a node at the specified JSON Pointer path from the given root node.
	 *
	 * @param uri         the document URI (used for logging purposes)
	 * @param rootNode    the root JSON node
	 * @param jsonPointer the JSON Pointer expression identifying the node to remove
	 */
	private static void removeNodeAtPointer(String uri, JsonNode rootNode, String jsonPointer) {
		JsonPointer pointer = JsonPointer.compile(jsonPointer);
		JsonNode targetNode = rootNode.at(pointer);

		if (targetNode.isMissingNode()) {
			logger.debug("JSONPointer '{}' does not exist in document {}, skipping", jsonPointer, uri);
			return;
		}

		// Use Jackson's JsonPointer API to get parent and field name
		JsonPointer parentPointer = pointer.head();
		JsonNode parentNode = rootNode.at(parentPointer);

		if (parentNode.isObject()) {
			JsonPointer lastSegment = pointer.last();
			if (lastSegment != null) {
				String fieldName = lastSegment.getMatchingProperty();
				((ObjectNode) parentNode).remove(fieldName);
			}
		} else if (parentNode.isArray()) {
			logger.warn("Array element exclusion not supported for JSONPointer '{}'. " +
				"Consider excluding the entire array property instead.", jsonPointer);
		}
	}

	/**
	 * Applies XPath exclusions to XML content by removing the specified elements.
	 *
	 * @param uri              the document URI (used for logging purposes)
	 * @param xmlContent       the XML content as a string
	 * @param xpathExpressions array of XPath expressions identifying elements to exclude
	 * @return the modified XML content with specified elements removed
	 * @throws Exception if the XML content cannot be parsed or serialized
	 */
	static String applyXmlExclusions(String uri, String xmlContent, String... xpathExpressions) throws Exception {
		if (xpathExpressions == null || xpathExpressions.length == 0) {
			return xmlContent;
		}

		DocumentBuilder builder = XmlFactories.getDocumentBuilderFactory().newDocumentBuilder();
		Document document = builder.parse(new ByteArrayInputStream(xmlContent.getBytes(StandardCharsets.UTF_8)));
		applyXmlExclusions(uri, document, xpathExpressions);
		return serializeDocument(document);
	}

	private static void applyXmlExclusions(String uri, Document document, String[] xpathExpressions) {
		final XPath xpath = XmlFactories.getXPathFactory().newXPath();
		for (String xpathExpression : xpathExpressions) {
			try {
				XPathExpression expr = xpath.compile(xpathExpression);
				QName returnType = XPathConstants.NODESET;
				NodeList nodes = (NodeList) expr.evaluate(document, returnType);

				if (nodes.getLength() == 0) {
					logger.debug("XPath '{}' does not match any nodes in document {}, skipping", xpathExpression, uri);
					continue;
				}

				// Remove nodes in reverse order to avoid index issues
				for (int i = nodes.getLength() - 1; i >= 0; i--) {
					Node node = nodes.item(i);
					Node parent = node.getParentNode();
					if (parent != null) {
						parent.removeChild(node);
					}
				}
			} catch (XPathExpressionException e) {
				logger.warn("Invalid XPath expression '{}' for document {}: {}", xpathExpression, uri, e.getMessage());
			}
		}
	}

	private static String serializeDocument(Document document) throws TransformerException {
		Transformer transformer = XmlFactories.newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		transformer.setOutputProperty(OutputKeys.INDENT, "no");
		StringWriter writer = new StringWriter();
		transformer.transform(new DOMSource(document), new StreamResult(writer));
		return writer.toString();
	}
}
