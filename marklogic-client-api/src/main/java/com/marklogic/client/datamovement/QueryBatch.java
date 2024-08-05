/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.datamovement;

import java.util.Calendar;

/** A group of uris retrieved from the Iterator or matches to the
 * QueryDefinition for this QueryBatcher job.
 */
public interface QueryBatch extends QueryEvent, Batch<String> {
  /** The server timestamp at which this query was run (if this job is running
   * withConsistentSnapshot()).
   *
   * @return the numeric timestamp at which this query was run
   */
  long getServerTimestamp();
}
