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
 * Uses an Optic fromLexicons query that depends on a field range index to retrieve URIs and
 * hash values. When {@code sourceUriKeyName} is configured (see
 * {@link IncrementalWriteFilter.Builder#sourceUriKeyName(String)}), the "URI" side of the
 * lookup is instead backed by a field over that metadata key, since the matched document may
 * have since been relocated to a different physical URI.
 *
 * @since 8.1.0
 */
class IncrementalWriteFromLexiconsFilter extends IncrementalWriteFilter {

	IncrementalWriteFromLexiconsFilter(IncrementalWriteConfig config) {
		super(config);
	}

	@Override
	public DocumentWriteSet apply(Context context) {
		final String[] uris = getUrisInBatch(context.getDocumentWriteSet());
		final String sourceUriKeyName = getConfig().getSourceUriKeyName();

		try {
			Map<String, Long> existingHashes = new RowTemplate(context.getDatabaseClient()).query(op ->
					op.fromLexicons(Map.of(
						"uri", sourceUriKeyName != null
							? op.cts.fieldReference(sourceUriKeyName)
							: op.cts.uriReference(),
						"hash", op.cts.fieldReference(getConfig().getHashKeyName())
					)).where(
						sourceUriKeyName != null
							? op.cts.fieldRangeQuery(op.xs.string(sourceUriKeyName), op.xs.string("="), op.xs.stringSeq(uris))
							: op.cts.documentQuery(op.xs.stringSeq(uris))
					),

				rows -> {
					Map<String, Long> map = new HashMap<>();
					rows.forEach(row -> {
						String uri = row.getString("uri");
						long existingHash = Long.parseUnsignedLong(row.getString("hash"));
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
			String message = "Unable to query for existing incremental write hashes; cause: " + e.getMessage();
			throw new FailedRequestException(message, e.getFailedRequest());
		}
	}
}
