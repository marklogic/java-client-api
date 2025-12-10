/*
 * Copyright (c) 2010-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.datamovement;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.DocumentWriteSet;

import java.util.function.Function;

/**
 * A filter that can modify a DocumentWriteSet before it is written to the database.
 *
 * @since 8.1.0
 */
public interface DocumentWriteSetFilter extends Function<DocumentWriteSetFilter.Context, DocumentWriteSet> {

	interface Context {
		/**
		 * @return the DocumentWriteSet to be written
		 */
		DocumentWriteSet getDocumentWriteSet();

		/**
		 * @return the batch number
		 */
		long getBatchNumber();

		/**
		 * @return the DatabaseClient being used for this batch
		 */
		DatabaseClient getDatabaseClient();

		/**
		 * @return the temporal collection name, or null if not writing to a temporal collection
		 */
		String getTemporalCollection();
	}
}
