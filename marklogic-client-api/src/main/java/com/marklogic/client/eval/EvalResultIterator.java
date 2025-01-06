/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.eval;

import java.io.Closeable;
import java.util.Iterator;

/**
 * An Iterator to walk through all results returned from calls to
 * {@link ServerEvaluationCall#eval()}.
 */
public interface EvalResultIterator extends Iterable<EvalResult>, Iterator<EvalResult>, Closeable {
	@Override
	Iterator<EvalResult> iterator();

	@Override
	boolean hasNext();

	@Override
	EvalResult next();

	/**
	 * As of 7.1.0, this must be called to ensure that the response is closed, as results are now
	 * streamed from MarkLogic instead of being read entirely into memory first.
	 */
	void close();
}
