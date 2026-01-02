/*
 * Copyright (c) 2010-2026 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.datamovement.filter;

import com.marklogic.client.FailedRequestException;
import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.row.RowTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Uses an Optic query to get the existing hash values for a set of URIs.
 *
 * @since 8.1.0
 */
class IncrementalWriteOpticFilter extends IncrementalWriteFilter {

	IncrementalWriteOpticFilter(String hashKeyName, String timestampKeyName, boolean canonicalizeJson,
								Consumer<DocumentWriteOperation[]> skippedDocumentsConsumer, String[] jsonExclusions) {
		super(hashKeyName, timestampKeyName, canonicalizeJson, skippedDocumentsConsumer, jsonExclusions);
	}

	@Override
	public DocumentWriteSet apply(Context context) {
		final String[] uris = context.getDocumentWriteSet().stream()
			.filter(op -> DocumentWriteOperation.OperationType.DOCUMENT_WRITE.equals(op.getOperationType()))
			.map(DocumentWriteOperation::getUri)
			.toArray(String[]::new);

		// It doesn't seem possible yet to use a DSL query and bind an array of strings to a "uris" param, so using
		// a serialized query instead. That doesn't allow a user to override the query though.
		RowTemplate rowTemplate = new RowTemplate(context.getDatabaseClient());

		try {
			Map<String, String> existingHashes = rowTemplate.query(op ->
					op.fromLexicons(Map.of(
						"uri", op.cts.uriReference(),
						"hash", op.cts.fieldReference(super.hashKeyName)
					)).where(
						op.cts.documentQuery(op.xs.stringSeq(uris))
					),

				rows -> {
					Map<String, String> map = new HashMap<>();
					rows.forEach(row -> {
						String uri = row.getString("uri");
						String existingHash = row.getString("hash");
						map.put(uri, existingHash);
					});
					return map;
				}
			);

			return filterDocuments(context, uri -> existingHashes.get(uri));
		} catch (FailedRequestException e) {
			String message = "Unable to query for existing incremental write hashes; cause: " + e.getMessage();
			throw new FailedRequestException(message, e.getFailedRequest());
		}
	}
}
