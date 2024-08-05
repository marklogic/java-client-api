/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.eval;

import java.io.Closeable;
import java.util.Iterator;

/** An Iterator to walk through all results returned from calls to
 * {@link ServerEvaluationCall#eval()}.
 */
public interface EvalResultIterator extends Iterable<EvalResult>, Iterator<EvalResult>, Closeable {
  @Override
  Iterator<EvalResult> iterator();
  @Override
  boolean hasNext();
  @Override
  EvalResult next();
  void close();
}
