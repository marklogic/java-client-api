/*
 * Copyright (c) 2010-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.datamovement.filter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.marklogic.client.FailedRequestException;
import com.marklogic.client.datamovement.DocumentWriteSetFilter;
import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.io.JacksonHandle;

import java.util.function.Consumer;

/**
 * Uses server-side JavaScript code to get the existing hash values for a set of URIs.
 *
 * @since 8.1.0
 */
class IncrementalWriteEvalFilter extends IncrementalWriteFilter {

	private static final String EVAL_SCRIPT = """
		const tuples = cts.valueTuples([cts.uriReference(), cts.fieldReference(hashKeyName)], null, cts.documentQuery(uris));
		const response = {};
		for (var tuple of tuples) {
		  response[tuple[0]] = tuple[1];
		}
		response
		""";

	IncrementalWriteEvalFilter(String hashKeyName, String timestampKeyName, boolean canonicalizeJson,
							   Consumer<DocumentWriteOperation[]> skippedDocumentsConsumer) {
		super(hashKeyName, timestampKeyName, canonicalizeJson, skippedDocumentsConsumer);
	}

	@Override
	public DocumentWriteSet apply(DocumentWriteSetFilter.Context context) {
		ArrayNode uris = new ObjectMapper().createArrayNode();
		for (DocumentWriteOperation doc : context.getDocumentWriteSet()) {
			if (DocumentWriteOperation.OperationType.DOCUMENT_WRITE.equals(doc.getOperationType())) {
				uris.add(doc.getUri());
			}
		}

		try {
			JsonNode response = context.getDatabaseClient().newServerEval().javascript(EVAL_SCRIPT)
				.addVariable("hashKeyName", hashKeyName)
				.addVariable("uris", new JacksonHandle(uris))
				.evalAs(JsonNode.class);

			return filterDocuments(context, uri -> {
				if (response.has(uri)) {
					return response.get(uri).asText();
				}
				return null;
			});
		} catch (FailedRequestException e) {
			String message = "Unable to query for existing incremental write hashes; cause: " + e.getMessage();
			throw new FailedRequestException(message, e.getFailedRequest());
		}
	}
}
