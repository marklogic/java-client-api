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
 * This implementation requires a TDE template to be deployed that extracts the URI and hash metadata.
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
			Map<String, String> existingHashes = rowTemplate.query(op ->
					op.fromView(getConfig().getSchemaName(), getConfig().getViewName())
						.where(op.in(op.col("uri"), op.xs.stringSeq(uris))),

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
