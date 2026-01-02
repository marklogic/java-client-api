/*
 * Copyright (c) 2010-2026 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.datamovement.filter;

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for applying content exclusions to documents before hash calculation.
 * Supports removing specific paths from JSON and XML documents using JSON Pointer and XPath expressions.
 *
 * @since 8.1.0
 */
public class ContentExclusionUtil {

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
	public static String applyJsonExclusions(String uri, String jsonContent, String[] jsonPointers) throws JsonProcessingException {
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
			String fieldName = pointer.last().getMatchingProperty();
			((ObjectNode) parentNode).remove(fieldName);
		} else if (parentNode.isArray()) {
			logger.warn("Array element exclusion not supported for JSONPointer '{}'. " +
				"Consider excluding the entire array property instead.", jsonPointer);
		}
	}

	// Future method for XML exclusions
	// public static String applyXmlExclusions(String xmlContent, String[] xpaths) { ... }
}
