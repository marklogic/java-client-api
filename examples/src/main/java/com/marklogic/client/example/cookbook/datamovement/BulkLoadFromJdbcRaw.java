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
import com.marklogic.client.admin.ServerConfigurationManager;
import com.marklogic.client.admin.TransformExtensionsManager;
import com.marklogic.client.datamovement.ApplyTransformListener;
import com.marklogic.client.datamovement.DataMovementManager;
import com.marklogic.client.datamovement.JobTicket;
import com.marklogic.client.datamovement.JobReport;
import com.marklogic.client.datamovement.QueryBatcher;
import com.marklogic.client.datamovement.WriteBatcher;
import com.marklogic.client.document.ServerTransform;
import com.marklogic.client.example.cookbook.Util.ExampleProperties;
import com.marklogic.client.example.cookbook.Util;
import com.marklogic.client.query.StructuredQueryDefinition;
import com.marklogic.client.query.StructuredQueryBuilder;

import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;
import javax.sql.DataSource;

/** BulkLoadFromJdbcRaw shows one way to load rows as-is from JDBC (the source
 * system) into MarkLogic (the target system), then transform (or harmonize)
 * after everything has been ingested.  We pull each row from three tables:
 * employees, salaries, and titles.  We convert employee rows to the Employee
 * POJO and the salaries and titles rows to Jackson ObjectNode (JSON).  Then we
 * write each row to MarkLogic Server (the target) as flat JSON.  Then we run a
 * transform on each employee record to pull in the salaries and titles for that
 * employee.  We call this "denormalization" and is a common way to translate
 * relational data into documents.
 *
 * This example assumes there are no employees, salaries, or titles records in
 * the target.  If this is run multiple times to capture changes, a step should
 * be added to first delete all employees, salaries, and titles in the target
 * system.  Otherwise old data (data updated or deleted from the source system)
 * might get mixed with new data.
 */
public class BulkLoadFromJdbcRaw {
  // during testing we run with a small data set and few threads but production
  // systems would use many more threads and much larger batch sizes
  private static int threadCount = 3;
  private static int batchSize = 3;

  // DataMovementManager helps orchestrate optimized writes across the
  // MarkLogic cluster
  public static final DataMovementManager moveMgr =
    DatabaseClientSingleton.get().newDataMovementManager();

  // ObjectMapper serializes objects to JSON for writing
  public static final ObjectMapper mapper = new ObjectMapper();

  // the ISO 8601 format is expected for dates in MarkLogic Server
  public static final String ISO_8601_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
  public static final SimpleDateFormat dateFormat = new SimpleDateFormat(ISO_8601_FORMAT);

  public static void main(String[] args) throws IOException, SQLException {
    new BulkLoadFromJdbcRaw().run();
  }

  public void run() throws IOException, SQLException {
    // first use a REST Admin user to install our transform
    installTransform(DatabaseClientSingleton.getRestAdmin());
    // then load all the data from the source system to the target system
    load();
    // then pull the salaries and titles into the employees
    transform();
  }

  // take data from a JDBC ResultSet (row) and populate an Employee POJO object
  public void populateEmployee(ResultSet row, Employee e) throws SQLException {
    e.setEmployeeId(row.getInt("emp_no"));
    e.setBirthDate(Calendar.getInstance());
    e.getBirthDate().setTime(row.getDate("birth_date"));
    e.setFirstName(row.getString("first_name"));
    e.setLastName(row.getString("last_name"));
    if ( "M".equals(row.getString("gender")) ) {
      e.setGender(Employee.Gender.MALE);
    } else if ( "F".equals(row.getString("gender")) ) {
      e.setGender(Employee.Gender.FEMALE);
    }
    e.setHireDate(Calendar.getInstance());
    e.getHireDate().setTime(row.getDate("hire_date"));
  }

  // take data from a JDBC ResultSet (row) and populate an ObjectNode (JSON) object
  public void populateSalary(ResultSet row, ObjectNode s) throws SQLException {
    s.put("employeeId", row.getInt("emp_no"));
    s.put("salary", row.getInt("salary"));
    Calendar fromDate = Calendar.getInstance();
    fromDate.setTime(row.getDate("from_date"));
    s.put("fromDate", dateFormat.format(fromDate.getTime()));
    Calendar toDate = Calendar.getInstance();
    toDate.setTime(row.getDate("to_date"));
    s.put("toDate", dateFormat.format(toDate.getTime()));
  }

  // take data from a JDBC ResultSet (row) and populate an ObjectNode (JSON) object
  public void populateTitle(ResultSet row, ObjectNode t) throws SQLException {
    t.put("employeeId", row.getInt("emp_no"));
    t.put("title", row.getString("title"));
    Calendar fromDate = Calendar.getInstance();
    fromDate.setTime(row.getDate("from_date"));
    t.put("fromDate", dateFormat.format(fromDate.getTime()));
    Calendar toDate = Calendar.getInstance();
    toDate.setTime(row.getDate("to_date"));
    t.put("toDate", dateFormat.format(toDate.getTime()));
  }

  public void load() throws IOException, SQLException {
    // the JdbcTemplate is an easy way to run JDBC queries
    JdbcTemplate jdbcTemplate = new JdbcTemplate(getDataSource());

    // the WriteBatcher is used to queue writes, batch them, and distribute
    // them to all appropriate nodes in the MarkLogic cluster
    WriteBatcher wb = moveMgr.newWriteBatcher()
      .withBatchSize(batchSize)
      .withThreadCount(threadCount)
      .onBatchSuccess(batch -> System.out.println("Written: " + batch.getJobWritesSoFar()))
      .onBatchFailure((batch,exception) -> exception.printStackTrace());
    JobTicket ticket = moveMgr.startJob(wb);

    // run a JDBC query and for each row returned, populate an Employee object
    // then add it the the WriteBatcher with a uri in the /employees/ directory
    jdbcTemplate.query("SELECT * FROM employees",
      (RowCallbackHandler) row -> {
        Employee employee = new Employee();
        populateEmployee(row, employee);
        wb.addAs("/employees/" + employee.getEmployeeId() + ".json", employee);
      }
    );

    // run a JDBC query and for each salary returned, populate an ObjectNode
    // (JSON) object then add it the the WriteBatcher with a uri in the
    // /salaries/ directory
    jdbcTemplate.query("SELECT * FROM salaries",
      (RowCallbackHandler) row -> {
        ObjectNode salary = mapper.createObjectNode();
        populateSalary(row, salary);
        wb.addAs("/salaries/" + UUID.randomUUID().toString() + ".json", salary);
      }
    );

    // run a JDBC query and for each title returned, populate an ObjectNode
    // (JSON) object then add it the the WriteBatcher with a uri in the
    // /titles/ directory
    jdbcTemplate.query("SELECT * FROM titles",
      (RowCallbackHandler) row -> {
        ObjectNode title = mapper.createObjectNode();
        populateTitle(row, title);
        wb.addAs("/titles/" + UUID.randomUUID().toString() + ".json", title);
      }
    );

    // finish all writes before proceeding
    wb.flushAndWait();

    // free any resources used by the WriteBatcher
    moveMgr.stopJob(wb);

    // double-check that the WriteBatcher job had no failures
    JobReport report = moveMgr.getJobReport(ticket);
    if ( report.getFailureBatchesCount() > 0 ) {
      throw new IllegalStateException("Encountered " +
        report.getFailureBatchesCount() + " failed write batches");
    }
  }

  public void transform() throws IOException, SQLException {
    // search for all records in the /employees/ directory
    StructuredQueryDefinition query = new StructuredQueryBuilder().directory(1, "/employees/");

    // the QueryBatcher efficiently paginates through matching batches from all
    // appropriate nodes in the cluster then applies the transform on each batch
    QueryBatcher qb = moveMgr.newQueryBatcher(query)
      .withThreadCount(threadCount)
      .withBatchSize(batchSize)
      .onQueryFailure(throwable -> throwable.printStackTrace());

    // the ApplyTransformListener performs the transform on each batch and
    // overwrites the employee document with the results of the transform
    ApplyTransformListener transformListener = new ApplyTransformListener()
      .withTransform(new ServerTransform("BulkLoadFromJdbcRaw"))
      .withApplyResult(ApplyTransformListener.ApplyResult.REPLACE)
      .onBatchFailure((batch, throwable) -> throwable.printStackTrace());

    // add the ApplyTransformListener to the QueryBatcher
    qb.onUrisReady(transformListener);

    // start the job (across threadCount threads) and wait for it to finish
    JobTicket ticket = moveMgr.startJob(qb);
    qb.awaitCompletion();
    moveMgr.stopJob(ticket);

    // double-check that the QueryBatcher job had no failures
    JobReport report = moveMgr.getJobReport(ticket);
    if ( report.getFailureBatchesCount() > 0 ) {
      throw new IllegalStateException("Encountered " +
        report.getFailureBatchesCount() + " failed query batches");
    }
  }

  private DataSource getDataSource() throws IOException {
    ExampleProperties properties = Util.loadProperties();
    return new DriverManagerDataSource(properties.jdbcUrl, properties.jdbcUser, properties.jdbcPassword);
  }

  public void installTransform(DatabaseClient client) {
    // this transform seeks salaries and titles associated with a single
    // employee record and injects them into the employee record
    String script =
      "function transform_function(context, params, content) { " +
      "  var uri = context.uri; " +
      "  var employee = content.toObject(); " +
      "  var salaries = cts.search(cts.andQuery([" +
      "    cts.directoryQuery('/salaries/'), " +
      "    cts.jsonPropertyValueQuery('employeeId', employee.employeeId)" +
      "  ])); " +
      "  if ( fn.count(salaries) > 0 ) { " +
      "    employee.salaries = new Array(); " +
      "    for (let salary of salaries) { " +
      "      var employeeSalary = salary.toObject(); " +
      "      delete employeeSalary.employeeId; " +
      "      employee.salaries.push(employeeSalary); " +
      "    } " +
      "  } " +
      "  var titles = cts.search(cts.andQuery([" +
      "    cts.directoryQuery('/titles/'), " +
      "    cts.jsonPropertyValueQuery('employeeId', employee.employeeId)" +
      "  ])); " +
      "  if ( fn.count(titles) > 0 ) { " +
      "    employee.titles = new Array(); " +
      "    for (let title of titles) { " +
      "      var employeeTitle = title.toObject(); " +
      "      delete employeeTitle.employeeId; " +
      "      employee.titles.push(employeeTitle); " +
      "    } " +
      "  } " +
      "  return employee; " +
      "}; " +
      "exports.transform = transform_function";
    ServerConfigurationManager confMgr = client.newServerConfigManager();
    TransformExtensionsManager transformMgr = confMgr.newTransformExtensionsManager();
    transformMgr.writeJavascriptTransformAs("BulkLoadFromJdbcRaw", script);
  }
}
