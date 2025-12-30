/*
 * Copyright (c) 2010-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.datamovement.filter;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.datamovement.DataMovementManager;
import com.marklogic.client.datamovement.WriteBatcher;

import java.util.function.Consumer;

// Experimenting with a template that gets rid of some annoying DMSDK boilerplate.
record WriteBatcherTemplate(DatabaseClient databaseClient) {

	public void runWriteJob(Consumer<WriteBatcher> writeBatcherConfigurer, Consumer<WriteBatcher> writeBatcherUser) {
		try (DataMovementManager dmm = databaseClient.newDataMovementManager()) {
			WriteBatcher writeBatcher = dmm.newWriteBatcher();
			writeBatcherConfigurer.accept(writeBatcher);

			dmm.startJob(writeBatcher);
			writeBatcherUser.accept(writeBatcher);
			writeBatcher.flushAndWait();
			writeBatcher.awaitCompletion();
			dmm.stopJob(writeBatcher);
		}
	}
}
