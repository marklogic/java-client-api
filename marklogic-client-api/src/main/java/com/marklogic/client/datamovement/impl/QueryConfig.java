/*
 * Copyright (c) 2010-2026 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.datamovement.impl;

import com.marklogic.client.datamovement.ForestConfiguration;

record QueryConfig(
	String serializedCtsQuery,
	ForestConfiguration forestConfig,
	Boolean filtered,
	int maxDocToUriBatchRatio,
	int defaultDocBatchSize,
	int maxUriBatchSize
) {
}
