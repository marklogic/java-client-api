/*
 * Copyright (c) 2010-2026 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.datamovement.impl;

import com.marklogic.client.datamovement.DocumentWriteSetFilter;
import com.marklogic.client.datamovement.filter.FilterException;
import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.impl.IoUtil;
import com.marklogic.client.io.Format;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.util.function.Consumer;

record BatchWriter(BatchWriteSet batchWriteSet, DocumentWriteSetFilter filter) implements Runnable {

	private static Logger logger = LoggerFactory.getLogger(WriteBatcherImpl.class);

	@Override
	public void run() {
		if (batchWriteSet.getDocumentWriteSet() == null || batchWriteSet.getDocumentWriteSet().isEmpty()) {
			logger.debug("Unexpected empty batch {}, skipping", batchWriteSet.getBatchNumber());
			return;
		}

		if (logger.isTraceEnabled()) {
			logger.trace("Begin write batch {} to forest on host '{}'", batchWriteSet.getBatchNumber(), batchWriteSet.getClient().getHost());
		}
		
		DocumentWriteSet documentWriteSet = batchWriteSet.getDocumentWriteSet();

		if (filter != null) {
			try {
				documentWriteSet = filter.apply(batchWriteSet);
			} catch (Exception e) {
				closeAllHandles();
				String message = String.format("Unable to apply filter to batch %d; cause: %s", batchWriteSet.getBatchNumber(), e.getMessage());
				onFailure(new FilterException(message, e));
				return;
			}
			if (documentWriteSet == null || documentWriteSet.isEmpty()) {
				closeAllHandles();
				logger.debug("Filter returned empty write set for batch {}, skipping write", batchWriteSet.getBatchNumber());
				return;
			}
			batchWriteSet.updateWithFilteredDocumentWriteSet(documentWriteSet);
		}

		try {
			writeDocuments(documentWriteSet);
			onSuccess();
		} catch (Throwable t) {
			onFailure(t);
		} finally {
			closeAllHandles();
		}
	}

	private void writeDocuments(DocumentWriteSet documentWriteSet) {
		if (batchWriteSet.getTemporalCollection() == null) {
			batchWriteSet.getClient().newDocumentManager().write(documentWriteSet, batchWriteSet.getTransform(), null);
		} else {
			// to get access to the TemporalDocumentManager write overload we need to instantiate
			// a JSONDocumentManager or XMLDocumentManager, but we don't want to make assumptions about content
			// format, so we'll set the default content format to unknown
			XMLDocumentManager docMgr = batchWriteSet.getClient().newXMLDocumentManager();
			docMgr.setContentFormat(Format.UNKNOWN);
			docMgr.write(documentWriteSet, batchWriteSet.getTransform(), null, batchWriteSet.getTemporalCollection());
		}
	}

	private void onSuccess() {
		Runnable onSuccess = batchWriteSet.getOnSuccess();
		if (onSuccess != null) {
			onSuccess.run();
		}
	}

	private void onFailure(Throwable t) {
		logger.trace("Failed batch sent to forest on host \"{}\"", batchWriteSet.getClient().getHost());
		Consumer<Throwable> onFailure = batchWriteSet.getOnFailure();
		if (onFailure != null) {
			onFailure.accept(t);
		}
	}

	/**
	 * This used to throw a Throwable... but it's not clear what a user would ever do with that if a content handle
	 * cannot be closed. Instead, this has been altered to use closeQuietly.
	 */
	private void closeAllHandles() {
		for (DocumentWriteOperation doc : batchWriteSet.getDocumentWriteSet()) {
			if (doc == null) {
				continue;
			}
			if (doc.getContent() instanceof Closeable closeable) {
				IoUtil.closeQuietly(closeable);
			}
			if (doc.getMetadata() instanceof Closeable closeable) {
				IoUtil.closeQuietly(closeable);
			}
		}
	}
}
