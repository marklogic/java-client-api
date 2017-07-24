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
import com.fasterxml.jackson.databind.SerializationFeature;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;
import javax.sql.DataSource;

public class BulkLoadFromJdbcRaw {
  private static Logger logger = LoggerFactory.getLogger(BulkLoadFromJdbcRaw.class);

  private static int threadCount = 10;
  private static int batchSize   = 10000;

  public static final DataMovementManager moveMgr =
    DatabaseClientSingleton.get().newDataMovementManager();
  public static final ObjectMapper mapper = new ObjectMapper();
  public static final String ISO_8601_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
  public static final SimpleDateFormat dateFormat = new SimpleDateFormat(ISO_8601_FORMAT);

  public static void main(String[] args) throws IOException, SQLException {
    new BulkLoadFromJdbcRaw().run();
  }

  public void run() throws IOException, SQLException {
    installTransform(DatabaseClientSingleton.getRestAdmin());
    load();
    transform();
  }

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
    JdbcTemplate jdbcTemplate = new JdbcTemplate(getDataSource());
    WriteBatcher wb = moveMgr.newWriteBatcher()
      .withBatchSize(batchSize)
      .withThreadCount(threadCount)
      .onBatchSuccess(batch -> System.out.println("Written: " + batch.getJobWritesSoFar()))
      .onBatchFailure((batch,exception) -> exception.printStackTrace());
    JobTicket ticket = moveMgr.startJob(wb);
    jdbcTemplate.query("SELECT * FROM employees LIMIT 100",
      (RowCallbackHandler) row -> {
        Employee employee = new Employee();
        populateEmployee(row, employee);
        wb.addAs("/employees/" + employee.getEmployeeId() + ".json", employee);
      }
    );
    jdbcTemplate.query("SELECT * FROM salaries LIMIT 100",
      (RowCallbackHandler) row -> {
        ObjectNode salary = mapper.createObjectNode();
        populateSalary(row, salary);
        wb.addAs("/salaries/" + UUID.randomUUID().toString() + ".json", salary);
      }
    );
    jdbcTemplate.query("SELECT * FROM titles LIMIT 100",
      (RowCallbackHandler) row -> {
        ObjectNode title = mapper.createObjectNode();
        populateTitle(row, title);
        wb.addAs("/titles/" + UUID.randomUUID().toString() + ".json", title);
      }
    );
    wb.flushAndWait();
    moveMgr.stopJob(wb);
    JobReport report = moveMgr.getJobReport(ticket);
    if ( report.getFailureBatchesCount() > 0 ) {
      throw new IllegalStateException("Encountered " +
        report.getFailureBatchesCount() + " failed write batches");
    }
  }

  public void transform() throws IOException, SQLException {
    StructuredQueryDefinition query = new StructuredQueryBuilder().directory(1, "/employees/");
    QueryBatcher qb = moveMgr.newQueryBatcher(query)
      .withThreadCount(threadCount)
      .withBatchSize(batchSize)
      .onQueryFailure(throwable -> throwable.printStackTrace());

    ApplyTransformListener transformListener = new ApplyTransformListener()
      .withTransform(new ServerTransform("BulkLoadFromJdbcRaw"))
      .withApplyResult(ApplyTransformListener.ApplyResult.REPLACE)
      .onBatchFailure((batch, throwable) -> throwable.printStackTrace());
    qb.onUrisReady(transformListener);

    JobTicket ticket = moveMgr.startJob(qb);
    qb.awaitCompletion();
    moveMgr.stopJob(ticket);
    JobReport report = moveMgr.getJobReport(ticket);
    if ( report.getFailureBatchesCount() > 0 ) {
      throw new IllegalStateException("Encountered " +
        report.getFailureBatchesCount() + " failed query batches");
    }
  }

  private DataSource getDataSource() throws IOException {
    ExampleProperties properties = Util.loadProperties();
    return new DriverManagerDataSource(properties.jdbcUrl);
  }

  public void installTransform(DatabaseClient client) {
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
      "      employee.salaries = employeeSalary; " +
      "    } " +
      "    for ( var i=1; i <= fn.count(salaries); i++ ) { " +
      "      xdmp.documentDelete(fn.baseUri(fn.subsequence(salaries, i, 1))) " +
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
