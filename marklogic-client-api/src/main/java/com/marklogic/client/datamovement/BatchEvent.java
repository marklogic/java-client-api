/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.datamovement;

import com.marklogic.client.DatabaseClient;

import java.util.Calendar;

/** A completed action in a datamovement job.
 */
public interface BatchEvent {
    /** The client-side timestamp when this batch completed writing or reading.
     *
     * @return the client timestamp when this batch completed
     */
    Calendar getTimestamp();

    /** Within the context of the job, the numeric position of this batch.
     *
     * @return the batch number
     */
    long getJobBatchNumber();

    /** The ticket for this job.  This can be useful for getting a snapshot
     * {@link DataMovementManager#getJobReport getJobReport} or for calling
     * {@link DataMovementManager#stopJob(JobTicket) stopJob} if needed.
     *
     * @return the JobTicket for this job
     */
    JobTicket getJobTicket();

    /** The DatabaseClient used to send or retrieve this batch.  This is useful
     * for performing additional operations on the same host.
     *
     * @return the DatabaseClient for this batch
     */
    DatabaseClient getClient();
}
