/*
 * Copyright (c) 2010-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.datamovement.impl;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.datamovement.WriteBatch;
import com.marklogic.client.datamovement.WriteBatcher;
import com.marklogic.client.datamovement.WriteEvent;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.document.ServerTransform;

import java.util.function.Consumer;

/**
 * Mutable class that captures the documents to be written. Documents are added via calls to "getDocumentWriteSet()", where the
 * DocumentWriteSet is empty when this class is constructed.
 */
class BatchWriteSet {

	private final WriteBatcher batcher;
	private final DocumentWriteSet documentWriteSet;
	private final long batchNumber;
	private final DatabaseClient client;
	private final ServerTransform transform;
	private final String temporalCollection;

	private long itemsSoFar;
	private Runnable onSuccess;
	private Consumer<Throwable> onFailure;

	BatchWriteSet(WriteBatcher batcher, DatabaseClient hostClient, ServerTransform transform, String temporalCollection, long batchNumber) {
		this.batcher = batcher;
		this.documentWriteSet = hostClient.newDocumentManager().newWriteSet();
		this.client = hostClient;
		this.transform = transform;
		this.temporalCollection = temporalCollection;
		this.batchNumber = batchNumber;
	}

	public DocumentWriteSet getDocumentWriteSet() {
		return documentWriteSet;
	}

	public long getBatchNumber() {
		return batchNumber;
	}

	public void setItemsSoFar(long itemsSoFar) {
		this.itemsSoFar = itemsSoFar;
	}

	public DatabaseClient getClient() {
		return client;
	}

	public ServerTransform getTransform() {
		return transform;
	}

	public String getTemporalCollection() {
		return temporalCollection;
	}

	public Runnable getOnSuccess() {
		return onSuccess;
	}

	public void onSuccess(Runnable onSuccess) {
		this.onSuccess = onSuccess;
	}

	public Consumer<Throwable> getOnFailure() {
		return onFailure;
	}

	public void onFailure(Consumer<Throwable> onFailure) {
		this.onFailure = onFailure;
	}

	public WriteBatch getBatchOfWriteEvents() {
		WriteBatchImpl batch = new WriteBatchImpl()
			.withBatcher(batcher)
			.withClient(client)
			.withJobBatchNumber(batchNumber)
			.withJobWritesSoFar(itemsSoFar)
			.withJobTicket(batcher.getJobTicket());

		WriteEvent[] writeEvents = getDocumentWriteSet().stream()
			.map(writeOperation ->
				new WriteEventImpl()
					.withTargetUri(writeOperation.getUri())
					.withContent(writeOperation.getContent())
					.withMetadata(writeOperation.getMetadata())
			)
			.toArray(WriteEventImpl[]::new);

		batch.withItems(writeEvents);
		return batch;
	}
}
