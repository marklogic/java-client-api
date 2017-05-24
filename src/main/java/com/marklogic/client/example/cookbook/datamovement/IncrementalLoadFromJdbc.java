/*
 * Copyright 2012-2017 MarkLogic Corporation
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class IncrementalLoadFromJdbc extends BulkLoadFromJdbcWithSimpleJoins {
  private static int threadCount = 10;
  private static int batchSize   = 1000;

  private static Logger logger = LoggerFactory.getLogger(IncrementalLoadFromJdbc.class);
  private static DatabaseClient client = DatabaseClientSingleton.get();
  public static final DataMovementManager moveMgr =
    DatabaseClientSingleton.get().newDataMovementManager();
  public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
  private static final LinkedBlockingQueue<String> uriQueue = new LinkedBlockingQueue<>(batchSize * 3);
  private static final ConcurrentHashMap<String,Employee> sourceEmployees = new ConcurrentHashMap<>();

  public static void main(String[] args) throws IOException, SQLException {
    //System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.wire", "debug");
    new IncrementalLoadFromJdbc().run();
  }

  public void processEmployee(WriteBatcher wb, Employee employee) {
    String uri = "/employees/" + employee.getEmployeeId() + ".json";
    try {
      uriQueue.put(uri);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    };
    sourceEmployees.put(uri, employee);
  }

  public void run() throws IOException, SQLException {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(getDataSource());
    jdbcTemplate.setFetchSize(Integer.MIN_VALUE);

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

    // the iterator to step through employees uris provided by the source
    Iterator<String> uris = new Iterator<String>() {
      String nextUri;
      boolean finished = false;

      public boolean hasNext() {
        if ( finished == true ) return false;
        try {
          // block (wait) for up to 10 minutes for the next uri from the source
          nextUri = uriQueue.poll(600, TimeUnit.SECONDS);
          // this is the indicator that we hit the end
          if ( "\u0000".equals(nextUri) ) {
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
        return nextUri;
      }
    };
    // the batcher to step through employee uris
    QueryBatcher qb = moveMgr.newQueryBatcher(uris)
      .withJobName(String.valueOf(System.currentTimeMillis()))
      .withBatchSize(batchSize)
      .withThreadCount(threadCount)
      .onUrisReady(batch -> {
        logger.debug("processing batch=" + batch.getJobBatchNumber() + ", so far=" + batch.getJobResultsSoFar());
        // for each batch of employee uris from the source
        // download the most recent load details for the batch
        DocumentPage records = client.newDocumentManager().read(LoadDetail.makeUris(batch.getItems()));
        HashMap<String,LoadDetail> details = new HashMap<>();
        for ( DocumentRecord record : records ) {
          details.put(record.getUri(), record.getContentAs(LoadDetail.class));
        }
        for ( String uri : batch.getItems() ) {
          String ldUri = LoadDetail.makeUri(uri);
          LoadDetail detail = details.get(ldUri);
          Employee employee = sourceEmployees.get(uri);
          if ( detail != null && detail.getHashCode() == employee.hashCode() ) {
            // this employee hasn't changed, so just update the details record
            // with the current job name
            logger.trace("employee hasn't changed; uri=[" + uri + "]");
            detail.setJobName(batch.getBatcher().getJobName());
            docWb.addAs(ldUri, detail);
          } else {
            // this employee has changed, so let's overwrite them
            logger.trace("employee changed; uri=[" + uri + "]");
            docWb.addAs(uri, employee);
            docWb.addAs(ldUri, new LoadDetail(batch.getBatcher().getJobName(), employee.hashCode()));
          }
        }
      })
      .onQueryFailure(exception -> exception.printStackTrace());
    JobTicket qbTicket = moveMgr.startJob(qb);
    jdbcTemplate.query(
      // perform the simplest possible join between three tables
      "SELECT *, s.from_date s_from_date, s.to_date s_to_date, t.from_date t_from_date, t.to_date t_to_date " +
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
        uriQueue.put("\u0000");
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    };
    qb.awaitCompletion();
    moveMgr.stopJob(qb);
    JobReport report = moveMgr.getJobReport(qbTicket);
    if ( report.getFailureBatchesCount() > 0 ) {
      throw new IllegalStateException("Encountered " +
        report.getFailureBatchesCount() + " failed query batches");
    }
    docWb.flushAndWait();
    moveMgr.stopJob(docWb);
    report = moveMgr.getJobReport(docWbTicket);
    if ( report.getFailureBatchesCount() > 0 ) {
      throw new IllegalStateException("Encountered " +
        report.getFailureBatchesCount() + " failed write batches");
    }
  }

  private DataSource getDataSource() throws IOException {
    ExampleProperties properties = Util.loadProperties();
    return new DriverManagerDataSource(properties.jdbcUrl);
  }
}
