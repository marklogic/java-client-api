/*
 * Copyright (c) 2010-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.datamovement.filter;

import com.marklogic.client.datamovement.DocumentWriteSetFilter;
import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.impl.DocumentWriteOperationImpl;
import com.marklogic.client.impl.HandleAccessor;
import com.marklogic.client.io.BaseHandle;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.marker.AbstractWriteHandle;
import net.openhft.hashing.LongHashFunction;
import org.erdtman.jcs.JsonCanonicalizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A DocumentWriteSetFilter that skips writing documents whose content has not changed since the last write
 * based on a hash value stored in a MarkLogic field.
 *
 * @since 8.1.0
 */
public abstract class IncrementalWriteFilter implements DocumentWriteSetFilter {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	public static Builder newBuilder() {
		return new Builder();
	}

	public static class Builder {

		private String fieldName = "incrementalWriteHash";
		private boolean canonicalizeJson = true;
		private boolean useEvalQuery = false;
		private Consumer<DocumentWriteOperation[]> skippedDocumentsConsumer;

		/**
		 * @param fieldName the name of the MarkLogic field that will hold the hash value; defaults to "incrementalWriteHash".
		 */
		public Builder fieldName(String fieldName) {
			this.fieldName = fieldName;
			return this;
		}

		/**
		 * @param canonicalizeJson whether to canonicalize JSON content before hashing; defaults to true.
		 *                         Delegates to https://github.com/erdtman/java-json-canonicalization for canonicalization.
		 */
		public Builder canonicalizeJson(boolean canonicalizeJson) {
			this.canonicalizeJson = canonicalizeJson;
			return this;
		}

		/**
		 * @param useEvalQuery if true, evaluate server-side JavaScript instead of an Optic query for retrieving hash values; defaults to false.
		 */
		public Builder useEvalQuery(boolean useEvalQuery) {
			this.useEvalQuery = useEvalQuery;
			return this;
		}

		/**
		 * @param skippedDocumentsConsumer a consumer that will be called with any documents in a batch that were skipped because their content had not changed.
		 */
		public Builder onDocumentsSkipped(Consumer<DocumentWriteOperation[]> skippedDocumentsConsumer) {
			this.skippedDocumentsConsumer = skippedDocumentsConsumer;
			return this;
		}

		public IncrementalWriteFilter build() {
			if (useEvalQuery) {
				return new IncrementalWriteEvalFilter(fieldName, canonicalizeJson, skippedDocumentsConsumer);
			}
			return new IncrementalWriteOpticFilter(fieldName, canonicalizeJson, skippedDocumentsConsumer);
		}
	}

	protected final String fieldName;
	private final boolean canonicalizeJson;
	private final Consumer<DocumentWriteOperation[]> skippedDocumentsConsumer;

	// Hardcoding this for now, with a good general purpose hashing function.
	// See https://xxhash.com for benchmarks.
	private final LongHashFunction hashFunction = LongHashFunction.xx3();

	public IncrementalWriteFilter(String fieldName, boolean canonicalizeJson, Consumer<DocumentWriteOperation[]> skippedDocumentsConsumer) {
		this.fieldName = fieldName;
		this.canonicalizeJson = canonicalizeJson;
		this.skippedDocumentsConsumer = skippedDocumentsConsumer;
	}

	protected final DocumentWriteSet filterDocuments(Context context, Function<String, String> hashRetriever) {
		final DocumentWriteSet newWriteSet = context.getDatabaseClient().newDocumentManager().newWriteSet();
		final List<DocumentWriteOperation> skippedDocuments = new ArrayList<>();

		for (DocumentWriteOperation doc : context.getDocumentWriteSet()) {
			if (!DocumentWriteOperation.OperationType.DOCUMENT_WRITE.equals(doc.getOperationType())) {
				newWriteSet.add(doc);
				continue;
			}

			final String content = serializeContent(doc.getContent());
			final String contentHash = computeHash(content);
			final String existingHash = hashRetriever.apply(doc.getUri());
			if (logger.isTraceEnabled()) {
				logger.trace("URI: {}, existing Hash: {}, new Hash: {}", doc.getUri(), existingHash, contentHash);
			}

			if (existingHash != null) {
				if (!existingHash.equals(contentHash)) {
					newWriteSet.add(addHashToMetadata(doc, fieldName, contentHash));
				} else if (skippedDocumentsConsumer != null) {
					skippedDocuments.add(doc);
				}
			} else {
				newWriteSet.add(addHashToMetadata(doc, fieldName, contentHash));
			}
		}

		if (!skippedDocuments.isEmpty()) {
			skippedDocumentsConsumer.accept(skippedDocuments.toArray(new DocumentWriteOperation[0]));
		}

		return newWriteSet;
	}

	private String serializeContent(AbstractWriteHandle contentHandle) {
		String content = HandleAccessor.contentAsString(contentHandle);

		Format format = null;
		if (contentHandle instanceof BaseHandle<?, ?> baseHandle) {
			format = baseHandle.getFormat();
		}

		if (canonicalizeJson && (Format.JSON.equals(format) || content.startsWith("{"))) {
			JsonCanonicalizer jc;
			try {
				jc = new JsonCanonicalizer(content);
			} catch (IOException e) {
				throw new RuntimeException("Unable to parse JSON content, cause: " + e.getMessage(), e);
			}
			return jc.getEncodedString();
		}

		return content;
	}

	private String computeHash(String content) {
		byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
		long hash = hashFunction.hashBytes(bytes);
		return Long.toHexString(hash);
	}

	protected static DocumentWriteOperation addHashToMetadata(DocumentWriteOperation op, String fieldName, String hash) {
		DocumentMetadataHandle newMetadata = new DocumentMetadataHandle();
		if (op.getMetadata() != null) {
			DocumentMetadataHandle originalMetadata = (DocumentMetadataHandle) op.getMetadata();
			newMetadata.setPermissions(originalMetadata.getPermissions());
			newMetadata.setCollections(originalMetadata.getCollections());
			newMetadata.setQuality(originalMetadata.getQuality());
			newMetadata.setProperties(originalMetadata.getProperties());
			newMetadata.getMetadataValues().putAll(originalMetadata.getMetadataValues());
		}
		newMetadata.getMetadataValues().put(fieldName, hash);
		return new DocumentWriteOperationImpl(op.getUri(), newMetadata, op.getContent(), op.getTemporalDocumentURI());
	}
}
