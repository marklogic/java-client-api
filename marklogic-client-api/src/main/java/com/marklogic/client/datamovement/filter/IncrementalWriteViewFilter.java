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

/**
 * Uses an Optic query with fromView to get the existing hash values for a set of URIs from a TDE view.
 * This implementation requires a TDE template to be deployed that contains at minimum a "uri" column
 * and a column matching the configured hash key name, plus any other columns desired.
 * The query uses a {@code where} with a {@code cts.documentQuery} to filter rows by URI, which is
 * significantly faster than filtering via {@code op.in}.
 *
 * @since 8.1.0
 */
class IncrementalWriteViewFilter extends IncrementalWriteFilter {

	IncrementalWriteViewFilter(IncrementalWriteConfig config) {
		super(config);
	}

	@Override
	public DocumentWriteSet apply(Context context) {
		final String[] uris = context.getDocumentWriteSet().stream()
			.filter(op -> DocumentWriteOperation.OperationType.DOCUMENT_WRITE.equals(op.getOperationType()))
			.map(DocumentWriteOperation::getUri)
			.toArray(String[]::new);

		RowTemplate rowTemplate = new RowTemplate(context.getDatabaseClient());

		try {
			Map<String, Long> existingHashes = rowTemplate.query(op ->
					op.fromView(getConfig().getSchemaName(), getConfig().getViewName(), "")
						.where(op.cts.documentQuery(op.xs.stringSeq(uris)))
				,
				rows -> {
					Map<String, Long> map = new HashMap<>();
					rows.forEach(row -> {
						String uri = row.getString("uri");
						String hashString = row.getString(getConfig().getHashKeyName());
						if (hashString != null && !hashString.isEmpty()) {
							long existingHash = Long.parseUnsignedLong(hashString);
							map.put(uri, existingHash);
						}
					});
					return map;
				});

			if (logger.isDebugEnabled()) {
				logger.debug("Retrieved {} existing hashes for batch of size {}", existingHashes.size(), uris.length);
			}

			return filterDocuments(context, uri -> existingHashes.get(uri));
		} catch (FailedRequestException e) {
			String message = "Unable to query for existing incremental write hashes from view " + getConfig().getSchemaName() + "." + getConfig().getViewName() + "; cause: " + e.getMessage();
			throw new FailedRequestException(message, e.getFailedRequest());
		}
	}
}
