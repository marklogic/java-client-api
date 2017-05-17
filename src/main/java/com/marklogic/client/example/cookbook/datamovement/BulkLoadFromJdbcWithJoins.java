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
import com.marklogic.client.datamovement.JobTicket;
import com.marklogic.client.datamovement.JobReport;
import com.marklogic.client.datamovement.WriteBatcher;
import com.marklogic.client.example.cookbook.Util.ExampleProperties;
import com.marklogic.client.example.cookbook.Util;

import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import javax.sql.DataSource;

public class BulkLoadFromJdbcWithJoins {
  private static Logger logger = LoggerFactory.getLogger(BulkLoadFromJdbcWithJoins.class);

  private static int threadCount = 10;
  private static int batchSize   = 10000;

  public static final DataMovementManager moveMgr =
    DatabaseClientSingleton.get().newDataMovementManager();

  public static void main(String[] args) throws IOException, SQLException {
    System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.wire", "debug");
    new BulkLoadFromJdbcWithJoins().run();
  }

  public void populate(ResultSet row, Employee e) throws SQLException {
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
    e.setSalary(row.getInt("salary"));
    e.setTitle(row.getString("title"));
  }

  public void run() throws IOException, SQLException {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(getDataSource());
    WriteBatcher wb = moveMgr.newWriteBatcher()
      .withBatchSize(batchSize)
      .withThreadCount(threadCount)
      .onBatchSuccess(batch -> System.out.println("Written: " + batch.getJobWritesSoFar()))
      .onBatchFailure((batch,exception) -> exception.printStackTrace());
    JobTicket ticket = moveMgr.startJob(wb);
    jdbcTemplate.query(
      "SELECT e.*, MAX(s.salary) AS salary, MAX(title) AS title " +
      "FROM employees e, salaries s, titles t " +
      "WHERE e.emp_no = s.emp_no " +
      "  AND e.emp_no = t.emp_no " +
      "GROUP BY e.emp_no " +
      "LIMIT 100",
      (RowCallbackHandler) row -> {
        Employee employee = new Employee();
        populate(row, employee);
        wb.addAs(employee.getEmployeeId() + ".json", employee);
      }
    );
    wb.flushAndWait();
    moveMgr.stopJob(wb);
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
