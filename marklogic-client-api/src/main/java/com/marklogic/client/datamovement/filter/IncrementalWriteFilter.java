/*
 * Copyright (c) 2010-2026 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.datamovement.filter;

import com.fasterxml.jackson.core.JsonPointer;
import com.marklogic.client.datamovement.DocumentWriteSetFilter;
import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.impl.DocumentWriteOperationImpl;
import com.marklogic.client.impl.HandleAccessor;
import com.marklogic.client.impl.XmlFactories;
import com.marklogic.client.io.BaseHandle;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.Format;
import net.openhft.hashing.LongHashFunction;
import org.erdtman.jcs.JsonCanonicalizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
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

		private String hashKeyName = "incrementalWriteHash";
		private String timestampKeyName = "incrementalWriteTimestamp";
		private boolean canonicalizeJson = true;
		private boolean useEvalQuery = false;
		private Consumer<DocumentWriteOperation[]> skippedDocumentsConsumer;
		private String[] jsonExclusions;
		private String[] xmlExclusions;

		/**
		 * @param keyName the name of the MarkLogic metadata key that will hold the hash value; defaults to "incrementalWriteHash".
		 */
		public Builder hashKeyName(String keyName) {
			// Don't let user shoot themselves in the foot with an empty key name.
			if (keyName != null && !keyName.trim().isEmpty()) {
				this.hashKeyName = keyName;
			}
			return this;
		}

		/**
		 * @param keyName the name of the MarkLogic metadata key that will hold the timestamp value; defaults to "incrementalWriteTimestamp".
		 */
		public Builder timestampKeyName(String keyName) {
			// Don't let user shoot themselves in the foot with an empty key name.
			if (keyName != null && !keyName.trim().isEmpty()) {
				this.timestampKeyName = keyName;
			}
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

		/**
		 * @param jsonPointers JSON Pointer expressions (RFC 6901) identifying JSON properties to exclude from hash calculation.
		 *                     For example, "/metadata/timestamp" or "/user/lastModified".
		 */
		public Builder jsonExclusions(String... jsonPointers) {
			this.jsonExclusions = jsonPointers;
			return this;
		}

		/**
		 * @param xpathExpressions XPath expressions identifying XML elements to exclude from hash calculation.
		 *                         For example, "//timestamp" or "//metadata/lastModified".
		 */
		public Builder xmlExclusions(String... xpathExpressions) {
			this.xmlExclusions = xpathExpressions;
			return this;
		}

		public IncrementalWriteFilter build() {
			validateJsonExclusions();
			validateXmlExclusions();
			if (useEvalQuery) {
				return new IncrementalWriteEvalFilter(hashKeyName, timestampKeyName, canonicalizeJson, skippedDocumentsConsumer, jsonExclusions, xmlExclusions);
			}
			return new IncrementalWriteOpticFilter(hashKeyName, timestampKeyName, canonicalizeJson, skippedDocumentsConsumer, jsonExclusions, xmlExclusions);
		}

		private void validateJsonExclusions() {
			if (jsonExclusions == null) {
				return;
			}
			for (String jsonPointer : jsonExclusions) {
				if (jsonPointer == null || jsonPointer.trim().isEmpty()) {
					throw new IllegalArgumentException(
						"Empty JSON Pointer expression is not valid for excluding content from incremental write hash calculation; " +
							"it would exclude the entire document. JSON Pointer expressions must start with '/'.");
				}
				try {
					JsonPointer.compile(jsonPointer);
				} catch (IllegalArgumentException e) {
					throw new IllegalArgumentException(
						String.format("Invalid JSON Pointer expression '%s' for excluding content from incremental write hash calculation. " +
							"JSON Pointer expressions must start with '/'; cause: %s", jsonPointer, e.getMessage()), e);
				}
			}
		}

		private void validateXmlExclusions() {
			if (xmlExclusions == null) {
				return;
			}
			XPath xpath = XmlFactories.getXPathFactory().newXPath();
			for (String xpathExpression : xmlExclusions) {
				if (xpathExpression == null || xpathExpression.trim().isEmpty()) {
					throw new IllegalArgumentException(
						"Empty XPath expression is not valid for excluding content from incremental write hash calculation.");
				}
				try {
					xpath.compile(xpathExpression);
				} catch (XPathExpressionException e) {
					throw new IllegalArgumentException(
						String.format("Invalid XPath expression '%s' for excluding content from incremental write hash calculation; cause: %s",
							xpathExpression, e.getMessage()), e);
				}
			}
		}
	}

	protected final String hashKeyName;
	private final String timestampKeyName;
	private final boolean canonicalizeJson;
	private final Consumer<DocumentWriteOperation[]> skippedDocumentsConsumer;
	private final String[] jsonExclusions;
	private final String[] xmlExclusions;

	// Hardcoding this for now, with a good general purpose hashing function.
	// See https://xxhash.com for benchmarks.
	private final LongHashFunction hashFunction = LongHashFunction.xx3();

	public IncrementalWriteFilter(String hashKeyName, String timestampKeyName, boolean canonicalizeJson, Consumer<DocumentWriteOperation[]> skippedDocumentsConsumer, String[] jsonExclusions, String[] xmlExclusions) {
		this.hashKeyName = hashKeyName;
		this.timestampKeyName = timestampKeyName;
		this.canonicalizeJson = canonicalizeJson;
		this.skippedDocumentsConsumer = skippedDocumentsConsumer;
		this.jsonExclusions = jsonExclusions;
		this.xmlExclusions = xmlExclusions;
	}

	protected final DocumentWriteSet filterDocuments(Context context, Function<String, String> hashRetriever) {
		final DocumentWriteSet newWriteSet = context.getDatabaseClient().newDocumentManager().newWriteSet();
		final List<DocumentWriteOperation> skippedDocuments = new ArrayList<>();
		final String timestamp = Instant.now().toString();

		for (DocumentWriteOperation doc : context.getDocumentWriteSet()) {
			if (!DocumentWriteOperation.OperationType.DOCUMENT_WRITE.equals(doc.getOperationType())) {
				newWriteSet.add(doc);
				continue;
			}

			final String serializedContent = serializeContent(doc);
			if (serializedContent == null) {
				// Not sure if the doc can have null content - possibly for a naked properties document? - but if it
				// does, just include it in the write set.
				newWriteSet.add(doc);
				continue;
			}

			final String contentHash = computeHash(serializedContent);
			final String existingHash = hashRetriever.apply(doc.getUri());
			if (logger.isTraceEnabled()) {
				logger.trace("URI: {}, existing Hash: {}, new Hash: {}", doc.getUri(), existingHash, contentHash);
			}

			if (existingHash != null) {
				if (!existingHash.equals(contentHash)) {
					newWriteSet.add(addHashToMetadata(doc, hashKeyName, contentHash, timestampKeyName, timestamp));
				} else if (skippedDocumentsConsumer != null) {
					skippedDocuments.add(doc);
				} else {
					// No consumer, so skip the document silently.
				}
			} else {
				newWriteSet.add(addHashToMetadata(doc, hashKeyName, contentHash, timestampKeyName, timestamp));
			}
		}

		if (!skippedDocuments.isEmpty() && skippedDocumentsConsumer != null) {
			skippedDocumentsConsumer.accept(skippedDocuments.toArray(new DocumentWriteOperation[0]));
		}

		return newWriteSet;
	}

	private String serializeContent(DocumentWriteOperation doc) {
		String content = HandleAccessor.contentAsString(doc.getContent());
		if (content == null) {
			return null;
		}

		Format format = null;
		if (doc.getContent() instanceof BaseHandle<?, ?> baseHandle) {
			format = baseHandle.getFormat();
		}

		if (canonicalizeJson && (Format.JSON.equals(format) || isPossiblyJsonContent(content))) {
			JsonCanonicalizer jc;
			try {
				if (jsonExclusions != null && jsonExclusions.length > 0) {
					content = ContentExclusionUtil.applyJsonExclusions(doc.getUri(), content, jsonExclusions);
				}
				jc = new JsonCanonicalizer(content);
				return jc.getEncodedString();
			} catch (IOException e) {
				// If the Format is actually JSON, then the write to MarkLogic should ultimately fail, which is the
				// error message the user would want to see via a batch failure listener. So in all cases, if we cannot
				// canonicalize something that appears to be JSON, we log a warning and return the original content for hashing.
				logger.warn("Unable to canonicalize JSON content for URI {}, using original content for hashing; cause: {}",
					doc.getUri(), e.getMessage());
			}
		} else if (xmlExclusions != null && xmlExclusions.length > 0) {
			try {
				content = ContentExclusionUtil.applyXmlExclusions(doc.getUri(), content, xmlExclusions);
			} catch (Exception e) {
				logger.warn("Unable to apply XML exclusions for URI {}, using original content for hashing; cause: {}",
					doc.getUri(), e.getMessage());
			}
		}

		return content;
	}

	private boolean isPossiblyJsonContent(String content) {
		// This isn't 100% reliable, as the content could be text that just happens to start with { or [, and so
		// we'll still need to catch an exception if we try to canonicalize non-JSON content.
		String trimmed = content.trim();
		return trimmed.startsWith("{") || trimmed.startsWith("[");
	}

	private String computeHash(String content) {
		byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
		long hash = hashFunction.hashBytes(bytes);
		return Long.toHexString(hash);
	}

	protected static DocumentWriteOperation addHashToMetadata(DocumentWriteOperation op, String hashKeyName, String hash,
															  String timestampKeyName, String timestamp) {
		DocumentMetadataHandle newMetadata = new DocumentMetadataHandle();
		if (op.getMetadata() != null) {
			DocumentMetadataHandle originalMetadata = (DocumentMetadataHandle) op.getMetadata();
			newMetadata.setPermissions(originalMetadata.getPermissions());
			newMetadata.setCollections(originalMetadata.getCollections());
			newMetadata.setQuality(originalMetadata.getQuality());
			newMetadata.setProperties(originalMetadata.getProperties());
			newMetadata.getMetadataValues().putAll(originalMetadata.getMetadataValues());
		}

		newMetadata.getMetadataValues().put(hashKeyName, hash);
		newMetadata.getMetadataValues().put(timestampKeyName, timestamp);

		return new DocumentWriteOperationImpl(op.getUri(), newMetadata, op.getContent(), op.getTemporalDocumentURI());
	}
}
