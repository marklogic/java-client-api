/*
 * Copyright (c) 2010-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.datamovement.impl;

import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.Format;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.util.function.Consumer;

record BatchWriter(BatchWriteSet batchWriteSet) implements Runnable {

	private static Logger logger = LoggerFactory.getLogger(WriteBatcherImpl.class);

	@Override
	public void run() {
		if (batchWriteSet.getDocumentWriteSet() == null || batchWriteSet.getDocumentWriteSet().isEmpty()) {
			logger.debug("Unexpected empty batch {}, skipping", batchWriteSet.getBatchNumber());
			return;
		}

		try {
			logger.trace("Begin write batch {} to forest on host '{}'", batchWriteSet.getBatchNumber(), batchWriteSet.getClient().getHost());

			DocumentWriteSet documentWriteSet = batchWriteSet.getDocumentWriteSet();
			writeDocuments(documentWriteSet);

			// This seems like it should be part of a finally block - but it's able to throw an exception. Which implies
			// that onFailure() should occur when this fails, which seems odd???
			closeAllHandles();

			onSuccess();
		} catch (Throwable t) {
			onFailure(t);
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

	private void closeAllHandles() throws Throwable {
		Throwable lastThrowable = null;
		for (DocumentWriteOperation doc : batchWriteSet.getDocumentWriteSet()) {
			try {
				if (doc.getContent() instanceof Closeable closeable) {
					closeable.close();
				}
				if (doc.getMetadata() instanceof Closeable closeable) {
					closeable.close();
				}
			} catch (Throwable t) {
				logger.error("Error closing all handles in BatchWriter", t);
				lastThrowable = t;
			}
		}
		if (lastThrowable != null) throw lastThrowable;
	}
}
