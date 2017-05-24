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

import com.marklogic.client.datamovement.DataMovementManager;
import com.marklogic.client.datamovement.ExportListener;
import com.marklogic.client.datamovement.JobTicket;
import com.marklogic.client.datamovement.JobReport;
import com.marklogic.client.datamovement.QueryBatcher;
import com.marklogic.client.example.cookbook.Util.ExampleProperties;
import com.marklogic.client.example.cookbook.Util;
import com.marklogic.client.example.cookbook.datamovement.Employee.Salary;
import com.marklogic.client.example.cookbook.datamovement.Employee.Title;
import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.client.query.StructuredQueryDefinition;

import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;

import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.lang3.builder.ToStringBuilder;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import java.text.SimpleDateFormat;

public class BulkExportToJdbc {
  private static Logger logger = LoggerFactory.getLogger(BulkExportToJdbc.class);
  public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

  private static int threadCount = 10;
  private static int batchSize   = 1000;

  public static final DataMovementManager moveMgr =
    DatabaseClientSingleton.get().newDataMovementManager();

  public static void main(String[] args) throws IOException, SQLException {
    new BulkExportToJdbc().run();
  }

  public void run() throws IOException, SQLException {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(getDataSource());
    StructuredQueryDefinition query = new StructuredQueryBuilder().directory(true, "/employees/");
    QueryBatcher qb = moveMgr.newQueryBatcher(query)
      .withBatchSize(batchSize)
      .withThreadCount(threadCount)
      .onUrisReady(
        new ExportListener()
          .withConsistentSnapshot()
          .onDocumentReady(record -> {
            Employee employee = record.getContentAs(Employee.class);
            jdbcTemplate.update(
              "INSERT INTO employees (emp_no, hire_date, first_name, last_name, birth_date) " +
              "VALUES (?, ?, ?, ?, ?)",
              employee.getEmployeeId(), employee.getHireDate(), employee.getFirstName(),
              employee.getLastName(), employee.getBirthDate());
            if ( employee.getSalaries() != null ) {
              for ( Salary salary : employee.getSalaries() ) {
                jdbcTemplate.update(
                  "INSERT INTO salaries (emp_no, salary, from_date, to_date) " +
                  "VALUES(?, ?, ?, ?)",
                  employee.getEmployeeId(), salary.getSalary(), salary.getFromDate(), salary.getToDate());
              }
            }
            if ( employee.getTitles() != null ) {
              for ( Title title : employee.getTitles() ) {
                jdbcTemplate.update(
                  "INSERT INTO titles (emp_no, title, from_date, to_date) " +
                  "VALUES(?, ?, ?, ?)",
                  employee.getEmployeeId(), title.getTitle(), title.getFromDate(), title.getToDate());
              }
            }
          })
          .onBatchFailure((failedBatch,exception) -> exception.printStackTrace())
      )
      .onUrisReady(batch ->
        logger.debug("Batch exported {}, so far {}",
          batch.getJobBatchNumber(), batch.getJobResultsSoFar())
      )
      .onQueryFailure(exception -> exception.printStackTrace());
    JobTicket ticket = moveMgr.startJob(qb);
    qb.awaitCompletion();
    moveMgr.stopJob(qb);
    JobReport report = moveMgr.getJobReport(ticket);
    if ( report.getFailureBatchesCount() > 0 ) {
      throw new IllegalStateException("Encountered " +
        report.getFailureBatchesCount() + " failed batches");
    }
  }

  private DataSource getDataSource() throws IOException {
    ExampleProperties properties = Util.loadProperties();
    return new DriverManagerDataSource(properties.jdbcUrl);
  }
}
