/*
 * Copyright (c) 2010-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.datamovement.impl;

import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.Format;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.util.function.Consumer;

class BatchWriter implements Runnable {

	private static Logger logger = LoggerFactory.getLogger(WriteBatcherImpl.class);

	private final BatchWriteSet batchWriteSet;

	BatchWriter(BatchWriteSet batchWriteSet) {
		if (batchWriteSet.getDocumentWriteSet().size() == 0) {
			throw new IllegalStateException("Attempt to write an empty batch");
		}
		this.batchWriteSet = batchWriteSet;
	}

	@Override
	public void run() {
		try {
			logger.trace("begin write batch {} to forest on host \"{}\"", batchWriteSet.getBatchNumber(), batchWriteSet.getClient().getHost());
			if (batchWriteSet.getTemporalCollection() == null) {
				batchWriteSet.getClient().newDocumentManager().write(
					batchWriteSet.getDocumentWriteSet(), batchWriteSet.getTransform(), null
				);
			} else {
				// to get access to the TemporalDocumentManager write overload we need to instantiate
				// a JSONDocumentManager or XMLDocumentManager, but we don't want to make assumptions about content
				// format, so we'll set the default content format to unknown
				XMLDocumentManager docMgr = batchWriteSet.getClient().newXMLDocumentManager();
				docMgr.setContentFormat(Format.UNKNOWN);
				docMgr.write(
					batchWriteSet.getDocumentWriteSet(), batchWriteSet.getTransform(), null, batchWriteSet.getTemporalCollection()
				);
			}
			closeAllHandles();
			Runnable onSuccess = batchWriteSet.getOnSuccess();
			if (onSuccess != null) {
				onSuccess.run();
			}
		} catch (Throwable t) {
			logger.trace("failed batch sent to forest on host \"{}\"", batchWriteSet.getClient().getHost());
			Consumer<Throwable> onFailure = batchWriteSet.getOnFailure();
			if (onFailure != null) {
				onFailure.accept(t);
			}
		}
	}

	private void closeAllHandles() throws Throwable {
		Throwable lastThrowable = null;
		for (DocumentWriteOperation doc : batchWriteSet.getDocumentWriteSet()) {
			try {
				if (doc.getContent() instanceof Closeable) {
					((Closeable) doc.getContent()).close();
				}
				if (doc.getMetadata() instanceof Closeable) {
					((Closeable) doc.getMetadata()).close();
				}
			} catch (Throwable t) {
				logger.error("error calling close()", t);
				lastThrowable = t;
			}
		}
		if (lastThrowable != null) throw lastThrowable;
	}

	public BatchWriteSet getBatchWriteSet() {
		return batchWriteSet;
	}
}
