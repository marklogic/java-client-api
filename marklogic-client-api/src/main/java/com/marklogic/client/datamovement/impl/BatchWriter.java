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

	private final BatchWriteSet writeSet;

	BatchWriter(BatchWriteSet writeSet) {
		if (writeSet.getWriteSet().size() == 0) {
			throw new IllegalStateException("Attempt to write an empty batch");
		}
		this.writeSet = writeSet;
	}

	@Override
	public void run() {
		try {
			logger.trace("begin write batch {} to forest on host \"{}\"", writeSet.getBatchNumber(), writeSet.getClient().getHost());
			if (writeSet.getTemporalCollection() == null) {
				writeSet.getClient().newDocumentManager().write(
					writeSet.getWriteSet(), writeSet.getTransform(), null
				);
			} else {
				// to get access to the TemporalDocumentManager write overload we need to instantiate
				// a JSONDocumentManager or XMLDocumentManager, but we don't want to make assumptions about content
				// format, so we'll set the default content format to unknown
				XMLDocumentManager docMgr = writeSet.getClient().newXMLDocumentManager();
				docMgr.setContentFormat(Format.UNKNOWN);
				docMgr.write(
					writeSet.getWriteSet(), writeSet.getTransform(), null, writeSet.getTemporalCollection()
				);
			}
			closeAllHandles();
			Runnable onSuccess = writeSet.getOnSuccess();
			if (onSuccess != null) {
				onSuccess.run();
			}
		} catch (Throwable t) {
			logger.trace("failed batch sent to forest on host \"{}\"", writeSet.getClient().getHost());
			Consumer<Throwable> onFailure = writeSet.getOnFailure();
			if (onFailure != null) {
				onFailure.accept(t);
			}
		}
	}

	private void closeAllHandles() throws Throwable {
		Throwable lastThrowable = null;
		for (DocumentWriteOperation doc : writeSet.getWriteSet()) {
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

	public BatchWriteSet getWriteSet() {
		return writeSet;
	}
}
