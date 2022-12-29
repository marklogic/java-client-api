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

import com.marklogic.client.datamovement.DataMovementManager;
import com.marklogic.client.datamovement.ExportListener;
import com.marklogic.client.datamovement.JobTicket;
import com.marklogic.client.datamovement.JobReport;
import com.marklogic.client.datamovement.QueryBatcher;
import com.marklogic.client.example.cookbook.Util.ExampleProperties;
import com.marklogic.client.example.cookbook.Util;
import com.marklogic.client.example.cookbook.datamovement.Employee.Gender;
import com.marklogic.client.example.cookbook.datamovement.Employee.Salary;
import com.marklogic.client.example.cookbook.datamovement.Employee.Title;
import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.client.query.StructuredQueryDefinition;

import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.core.JdbcTemplate;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import javax.sql.DataSource;

import java.text.SimpleDateFormat;

/** BulkExportToJdbc shows how simple it is to use Data Movement SDK to move
 * massive data sets from a source MarkLogic Server to a JDBC target server.
 * In this example all employees are exported using a query matching all docs
 * in directory /employees/.  Using the
 * [Shortcut Method](https://docs.marklogic.com/javadoc/client/overview-summary.html#ShortcutMethods)
 * `getContentAs` and the Employee POJO class (pre-registered with the handle
 * registry by DatabaseClientSingleton), we can easily serialize each document
 * to an Employee object.  From there it's straightforward to use Spring's
 * JdbcTemplate to write the employees, their salaries, and their titles via
 * JDBC.  Of course, Spring's JdbcTemplate is not required--you could choose
 * your favorite JDBC libraries to use with Data Movement SDK.  And of course
 * you don't need to deserialize to pojos--you could use any of the Java Client
 * API handles to deserialize the matching documents.
 */
public class BulkExportToJdbc {
  private static Logger logger = LoggerFactory.getLogger(BulkExportToJdbc.class);
  // this is the date format required by our relational database tables
  // which is thread local because DateFormat is not threadsafe
  public static final ThreadLocal<SimpleDateFormat> dateFormat =
          ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd"));

  // we're using a small thread count and batch size because the example
  // dataset is small, but with a larger dataset you'd use more threads and
  // larger batches
  private static int threadCount = 3;
  private static int batchSize   = 3;

  public static final DataMovementManager moveMgr =
    DatabaseClientSingleton.get().newDataMovementManager();

  public static void main(String[] args) throws IOException, SQLException {
    new BulkExportToJdbc().run();
  }

  public void run() throws IOException, SQLException {
    // connect to JDBC and initialize JdbcTemplate
    JdbcTemplate jdbcTemplate = new JdbcTemplate(getDataSource());
    // query for all employees in directory /employees/
    StructuredQueryDefinition query = new StructuredQueryBuilder().directory(true, "/employees/");
    // run the query on each forest in the cluster and asynchronously paginate
    // through matches, sending them to the onUrisReady listener ExportListener
    QueryBatcher qb = moveMgr.newQueryBatcher(query)
      .withBatchSize(batchSize)
      .withThreadCount(threadCount)

      // use withConsistentSnapshot so the set of matches doesn't change while this job
      // runs even though updates are still occurring concurrently in MarkLogic Server.
      // Requires a [merge timestamp](https://docs.marklogic.com/guide/app-dev/point_in_time#id_32468)
      // to be set on MarkLogic Server.
      .withConsistentSnapshot()

      .onUrisReady(
        // Since ExportListener meets our needs we'll use it instead of a
        // custom listener
        new ExportListener()

          // since the ExportListener uses a separate request from the QueryBatcher
          // we must also use withConsistentSnapshot on the ExportListener
          .withConsistentSnapshot()

          // this is our custom onDocumentReady listener
          .onDocumentReady(record -> {

            // Employee class is registered by DatabaseClientSingleton with the
            // handle registry so we can use the getContentAs shortcut method
            Employee employee = record.getContentAs(Employee.class);

            // using jdbcTemplate (which simplifies using jdbc) we can easily
            // write the employee to the target relational database server
            jdbcTemplate.update(
              "INSERT INTO employees_export (emp_no, hire_date, first_name, last_name, gender, birth_date) " +
              "VALUES (?, ?, ?, ?, ?, ?) ",
              employee.getEmployeeId(), dateFormat.get().format(employee.getHireDate().getTime()), employee.getFirstName(),
              employee.getLastName(), employee.getGender() == Gender.MALE ? "M" : "F",
              dateFormat.get().format(employee.getBirthDate().getTime()));
            if ( employee.getSalaries() != null ) {
              // each employee could have many salaries, and we need to write
              // each of those to its own row
              for ( Salary salary : employee.getSalaries() ) {
                jdbcTemplate.update(
                  "INSERT INTO salaries_export (emp_no, salary, from_date, to_date) " +
                  "VALUES(?, ?, ?, ?)",
                  employee.getEmployeeId(), salary.getSalary(), dateFormat.get().format(salary.getFromDate().getTime()),
                  dateFormat.get().format(salary.getToDate().getTime()));
              }
            }
            if ( employee.getTitles() != null ) {
              // each employee could have many titles, and we need to write
              // each of those to its own row
              for ( Title title : employee.getTitles() ) {
                jdbcTemplate.update(
                  "INSERT INTO titles_export (emp_no, title, from_date, to_date) " +
                  "VALUES(?, ?, ?, ?)",
                  employee.getEmployeeId(), title.getTitle(), dateFormat.get().format(title.getFromDate().getTime()),
                  dateFormat.get().format(title.getToDate().getTime()));
              }
            }
          })

          // in a production application we could have more elaborate error
          // handling here
          .onBatchFailure((failedBatch,exception) -> exception.printStackTrace())
      )

      // another onUrisReady listener, this one custom, and just for logging
      .onUrisReady(batch ->
        logger.debug("Batch exported {}, so far {}",
          batch.getJobBatchNumber(), batch.getJobResultsSoFar())
      )

      // in a production application we could have more elaborate error
      // handling here
      .onQueryFailure(exception -> exception.printStackTrace());

    // now that the job is configured, kick it off
    JobTicket ticket = moveMgr.startJob(qb);

    // wait for the job to fully complete all pagination and all listeners
    qb.awaitCompletion();

    // free up resources by stopping the job
    moveMgr.stopJob(qb);

    // double check that we didn't have any failed batches
    JobReport report = moveMgr.getJobReport(ticket);
    if ( report.getFailureBatchesCount() > 0 ) {
      throw new IllegalStateException("Encountered " +
        report.getFailureBatchesCount() + " failed batches");
    }
  }

  // get the jdbcUrl property from Example.properties with our jdbc connection info
  private DataSource getDataSource() throws IOException {
    ExampleProperties properties = Util.loadProperties();
    return new DriverManagerDataSource(properties.jdbcUrl, properties.jdbcUser, properties.jdbcPassword);
  }
}
