/*
 * Copyright (c) 2022 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.marklogic.client.example.cookbook.datamovement;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.datamovement.DataMovementManager;
import com.marklogic.client.datamovement.JobReport;
import com.marklogic.client.datamovement.JobTicket;
import com.marklogic.client.datamovement.QueryBatcher;
import com.marklogic.client.datamovement.WriteBatcher;
import com.marklogic.client.document.DocumentPage;
import com.marklogic.client.document.DocumentRecord;
import com.marklogic.client.example.cookbook.Util;
import com.marklogic.client.example.cookbook.Util.ExampleProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Demonstrates a way to load a massive volume of data updates from JDBC into
 * MarkLogic, assuming the source cannot identify changes and deletes. In this
 * example source data (accessed via JDBC) continues to grow and evolve, so
 * updates from the source must be regularly incporated into the target system
 * (MarkLogic Server).  These updates include new documents, updated documents,
 * and deleted documents.
 *
 * The source data is too large to ingest completely every time.  So this
 * example addresses the more difficult scenario where incremental loads are
 * required to include only the updates.
 *
 * Many source systems offer a document version or last updated time-stamp.
 * This pattern addresses the more difficult scenario where the source system
 * offers no such option.
 *
 * Additionally, this example addresses the more difficult scenario where the
 * source system can provide a list of all current document uris, but cannot
 * provide any information about modified or deleted documents.
 *
 * # Solution
 *
 * ## Step 1
 *
 * The process begins reading documents directly from JDBC, adding each to
 * uriQueue and sourceEmployees.  One batch at a time the uris from uriQueue
 * are used to retrieve hashcodes from the target.  For each source document
 * the hashcode is generated in memory and compared to the hashcode from the
 * target (if available).  Documents with no hashcode in the target are
 * considered new and written to the target.  Documents with a hashcode
 * different from the target are considered updated and written to the target.
 * In all cases a sidecar document including the current source hashcode is
 * written to the target.  This is all done in batches to reduce overhead on
 * the application, source, and target systems.  In addition, DMSDK processes
 * batches in multiple threads and against multiple MarkLogic hosts to fully
 * utilize the MarkLogic cluster.
 *
 * ## Step 2
 *
 * Any document written to MarkLogic Server also has written a "sidecar"
 * document containing metadata including the document uri, a hashcode and a
 * jobName.  The sidecar document has a collection representing the data
 * source.  The hascode is generated based on select portions of the source
 * document contents.  The hascode algorithm is consistent when the source
 * document hasn't changed and different any time the source document has
 * changed.  The jobName is any id or timestamp representing the last job which
 * checked the hashcode of the document, and should differ from previous job
 * runs.  This sidecar document is updated with each job run to reflect the
 * latest jobName.
 *
 * ## Step 3
 *
 * As the last step of a job run, a query returns all sidecar files with the
 * collection for this datasource but a jobName different than the current
 * jobName which indicates these documents are in MarkLogic but were missing
 * from this job run and are therefore not in the datasource.  After confirming
 * that these documents are legitimately not in the datasource, they are
 * archived in MarkLogic Server.  To archive documents we remove the collection
 * for this datasource and add an "archived" collection.  This effectively
 * removes the documents from queries that are looking for documents in the
 * collection for this datasource.  This is how we stay up-to-date with
 * deletes when the source system offers no way to track deleted documents.
 *
 * # Alternative Solutions
 *
 * ## Alternative Solution 1
 *
 * If your scenario allows you to load all the documents each time, do that
 * because it's simpler.  Simply delete in the target all data from that one
 * source then reload the latest data from that source.  This addresses new
 * documents, updated documents, and deleted documents.
 *
 * ## Alternative Solution 2
 *
 * Your scenario may be different if it requires a one-time data migration
 * rather than an ongoing load of updates from the source.  For example, a
 * one-time load for a production cut-over may have significant performance
 * requirements this solution cannot address.  Also, some one-time migrations
 * will not require comparison of hashcodes nor tracking of deletes.
 *
 * # Adjustments
 *
 * # Solution Adjustment 1
 *
 * If the source can provide you with last updated timestamps, compare those
 * instead of hashcodes.  This reduces the effort to select which portions of
 * the document to include in the hashcode.  This also reduces the processing
 * of calculating hashcodes each time.
 *
 * # Solution Adjustment 2
 *
 * The sidecar document can be written to a different MarkLogic database,
 * cluster, or non-MarkLogic system (including the file system).  This will
 * reduce the read load on the database with the actual document contents.
 * This also opens more options to write sidecar to a database with a different
 * configuration including forests on less expensive storage.
 *
 * # Solution Adjustment 3
 *
 * For systems that offer a way to track deleted documents, use that instead of
 * step 3.  Get the list of uris of source documents deleted since the last job
 * run.  Archive or delete those documents (and associated sidecar files) from
 * MarkLogic Server.
 *
 * # Solution Adjustment 4
 *
 * The source documents can be read from a staging area containing at least the
 * uri and the up-to-date hashcode for each document.  This will reduce the
 * read load on the source system to only documents found to be missing from
 * MarkLogic or updated from what is in MarkLogic.
 *
 * # Gotchas
 *
 * ## No Staging of Source Documents in Target
 *
 * We recommend loading documents to a staging area in MarkLogic without
 * transformations so we can see the documents in MarkLogic as they look in the
 * source system.  If we don't do that, and we transform the documents in
 * MarkLogic, it may be confusing how to calculate hashcodes.  Nevertheless,
 * this pattern can still be applied, it just requires more careful design and
 * documentation so it can reasonably be maintained.
 *
 * ## Documents are not 1-1 from Source to Target
 *
 * Not all documents (or records, or rows) from a source system map 1-1 to
 * final documents in a target system.  This may make it less obvious how to
 * apply this pattern.  Sometimes mapping source documents to target documents
 * occurs client-side.  Sometimes mapping source documents to target documents
 * happens server-side, as in the Data Hub Framwork.  One key to resolving this
 * is to generate hashcodes that help determine whether relevant source data
 * changed, so hashcodes should incorporate all relevant source data but not
 * data generated solely by transformations (or harmonization).
 *
 * When all relevant source data comes from multiple records, and no staging
 * documents match source documents, the source records must of course be
 * combined prior to calculating hashcodes, as we do in this example.  Here we
 * perform a join in the source relational database to combine all relevant
 * data into multiple rows.  Additionally, we combine multiple rows into a
 * single Employee object before we calculate the hashcodes.
 */
public class IncrementalLoadFromJdbc extends BulkLoadFromJdbcWithSimpleJoins {
  // threadCount and batchSize are only small because this recipe ships with a
  // very small dataset to reduce download size.  In a large production app
  // these numbers would be much higher
  private static int threadCount = 3;
  private static int batchSize = 3;

  private static Logger logger = LoggerFactory.getLogger(IncrementalLoadFromJdbc.class);
  private static DatabaseClient client = DatabaseClientSingleton.get();
  public static final DataMovementManager moveMgr =
    DatabaseClientSingleton.get().newDataMovementManager();
  public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

  // uriQueue is where we queue up uris from the source which we still need to
  // compare against the target.  This is the basis for the uris Iterator below.
  private static final LinkedBlockingQueue<String> uriQueue = new LinkedBlockingQueue<>(batchSize * 3);

  // sourceEmployees is where we cache Employees that have been retrieved from
  // the source but not yet compared against what's in the target.  As soon as
  // they are compared and written to MarkLogic if needed, they are deleted
  // from sourceEmployees to reduce memory usage if this is run against a very
  // large dataset
  private static final ConcurrentHashMap<String,Employee> sourceEmployees = new ConcurrentHashMap<>();

  public static void main(String[] args) throws IOException, SQLException {
    new IncrementalLoadFromJdbc().run();
  }

  // processEmployee is called by BulkLoadFromJdbcWithSimpleJoins.addRow method
  // for each Employee record retrieved from the source
  public void processEmployee(WriteBatcher wb, Employee employee) {
    String uri = "/employees/" + employee.getEmployeeId() + ".json";
    try {
      // queue this uri to see if it's in the server and unchanged
      uriQueue.put(uri);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    };
    // queue this employee to generate a local hashcode to compare against the
    // server and write this employee to the target if needed
    sourceEmployees.put(uri, employee);
  }

  public void run() throws IOException, SQLException {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(getDataSource());

    // the batcher to write employees and loadDetail side-car docs
    WriteBatcher docWb = moveMgr.newWriteBatcher()
      .withBatchSize(batchSize)
      .withThreadCount(threadCount)
      .onBatchSuccess(batch -> {
        logger.debug("wrote batch " + batch.getJobBatchNumber() +
          ", so far: " + batch.getJobWritesSoFar());
      })
      .onBatchFailure((batch,exception) -> exception.printStackTrace());
    JobTicket docWbTicket = moveMgr.startJob(docWb);

    // the iterator to step through employees uris provided by the source and
    // queued in uriQueue
    Iterator<String> uris = new Iterator<String>() {
      String nextUri;
      boolean finished = false;

      public boolean hasNext() {
        if ( nextUri != null ) return true;
        if ( finished == true ) return false;
        try {
          // block (wait) for up to 10 minutes for the next uri from the source
          nextUri = uriQueue.poll(600, TimeUnit.SECONDS);
          // this is the indicator that we hit the end
          if ( "\u0000".equals(nextUri) ) {
            nextUri = null;
            finished = true;
            return false;
          }
          return true;
        } catch (InterruptedException e) {
          logger.error("INTERRUPTED!");
          return false;
        }
      }
      public String next() {
        String next = nextUri;
        nextUri = null;
        return next;
      }
    };

    // changedEmployees and unchangedEmployees are just used for debugging
    AtomicInteger changedEmployees = new AtomicInteger(0);
    AtomicInteger unchangedEmployees = new AtomicInteger(0);

    // the batcher to step through source employee uris (queued in uriQueue)
    QueryBatcher qb = moveMgr.newQueryBatcher(uris)
      .withJobName(String.valueOf(System.currentTimeMillis()))
      .withBatchSize(batchSize)
      .withThreadCount(threadCount)
      .onUrisReady(batch -> {
        logger.debug("processing batch=" + batch.getJobBatchNumber() + ", so far=" + batch.getJobResultsSoFar());
        // for each batch of employee uris from the source download the most
        // recent sidecar (LoadDetail) documents for the batch
        DocumentPage records = client.newDocumentManager().read(LoadDetail.makeUris(batch.getItems()));
        // "details" allows us to find sidecar (LoadDetail) documents by uri
        HashMap<String,LoadDetail> details = new HashMap<>();
        for ( DocumentRecord record : records ) {
          details.put(record.getUri(), record.getContentAs(LoadDetail.class));
        }
        // loop through the source uris (not the target since it doesn't yet
        // contain new documents from the source)
        for ( String uri : batch.getItems() ) {
          String ldUri = LoadDetail.makeUri(uri);
          LoadDetail detail = details.get(ldUri);
          Employee employee = sourceEmployees.get(uri);
          sourceEmployees.remove(uri);
          if ( detail != null && detail.getHashCode() == employee.hashCode() ) {
            logger.trace("employee hasn't changed; uri=[" + uri + "]");
            unchangedEmployees.incrementAndGet();
            // this employee hasn't changed, so just update the sidecar document
            // with the current job name
            detail.setJobName(batch.getBatcher().getJobName());
            docWb.addAs(ldUri, detail);
          } else {
            // this employee is new or has changed, so let's write it
            changedEmployees.incrementAndGet();
            logger.trace("employee changed; uri=[" + uri + "]");
            docWb.addAs(uri, employee);
            // plus write an updated sidecar document
            docWb.addAs(ldUri, new LoadDetail(batch.getBatcher().getJobName(), employee.hashCode()));
          }
        }
      })
      .onQueryFailure(exception -> exception.printStackTrace());
    JobTicket qbTicket = moveMgr.startJob(qb);

    // query the source for a joined dataset representing Employees, their
    // salaries, and their titles.  Each employee will have one row per
    // salary/title combination.  BulkLoadFromJdbcWithSimpleJoins.addRow does
    // the magic of combining multiple rows down into one Employee as needed.
    jdbcTemplate.query(
      // perform the simplest possible join between three tables
      "SELECT e.*, s.salary, t.title, s.from_date s_from_date, s.to_date s_to_date, t.from_date t_from_date, t.to_date t_to_date " +
      "FROM employees e, salaries s, titles t " +
      "WHERE e.emp_no=s.emp_no " +
      "  AND e.emp_no=t.emp_no " +
      "ORDER BY e.emp_no, s.from_date, s.to_date, t.from_date, t.to_date",
      (RowCallbackHandler) row -> {
        addRow(row, docWb);
      }
    );
    // we're done loading
    logger.debug("Loading finished");
    try {
        // send our magic uri through the queue so the Iterator knows it's now done
        // and can return false from hasNext()
        uriQueue.put("\u0000");
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    };
    qb.awaitCompletion();
    moveMgr.stopJob(qb);

    // some debugging output about the QueryBatcher job that processed the
    // source uris
    JobReport report = moveMgr.getJobReport(qbTicket);
    logger.debug("Compared " + report.getSuccessEventsCount() + " employees in " + report.getSuccessBatchesCount() + " batches");
    if ( report.getFailureBatchesCount() > 0 ) {
      throw new IllegalStateException("Encountered " +
        report.getFailureBatchesCount() + " failed query batches");
    }

    // wait for any writes to finish
    docWb.flushAndWait();
    moveMgr.stopJob(docWb);

    // more debugging output about the WriteBatcher job that wrote to the target
    logger.debug("Wrote " + report.getSuccessEventsCount() + " docs in " + report.getSuccessBatchesCount() + " batches");
    logger.debug(changedEmployees.get() + " employees were changed");
    logger.debug(unchangedEmployees.get() + " employees were unchanged");
    report = moveMgr.getJobReport(docWbTicket);
    if ( report.getFailureBatchesCount() > 0 ) {
      throw new IllegalStateException("Encountered " +
        report.getFailureBatchesCount() + " failed write batches");
    }
  }

  private DataSource getDataSource() throws IOException {
    ExampleProperties properties = Util.loadProperties();
    return new DriverManagerDataSource(properties.jdbcUrl, properties.jdbcUser, properties.jdbcPassword);
  }
}
