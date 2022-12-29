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
import com.marklogic.client.datamovement.JobTicket;
import com.marklogic.client.datamovement.JobReport;
import com.marklogic.client.datamovement.WriteBatcher;
import com.marklogic.client.example.cookbook.Util.ExampleProperties;
import com.marklogic.client.example.cookbook.Util;
import com.marklogic.client.example.cookbook.datamovement.Employee.Salary;
import com.marklogic.client.example.cookbook.datamovement.Employee.Title;

import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.core.JdbcTemplate;


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

public class BulkLoadFromJdbcWithSimpleJoins {
  private static Logger logger = LoggerFactory.getLogger(BulkLoadFromJdbcWithSimpleJoins.class);
  public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

  private static int threadCount = 3;
  private static int batchSize = 3;
  private Employee lastEmployee;
  private List<Salary> salaries = new ArrayList<>();
  private List<Title> titles = new ArrayList<>();

  public static final DataMovementManager moveMgr =
    DatabaseClientSingleton.get().newDataMovementManager();

  public static void main(String[] args) throws IOException, SQLException {
    new BulkLoadFromJdbcWithSimpleJoins().run();
  }

  public Employee populateEmployee(ResultSet row, Employee e) throws SQLException {
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
    return e;
  }

  public Salary populateSalary(ResultSet row, Salary s) throws SQLException {
    s.setSalary(row.getInt("salary"));
    Calendar fromDate = Calendar.getInstance();
    fromDate.clear();
    fromDate.setTime(row.getDate("s_from_date"));
    s.setFromDate(fromDate);
    Calendar toDate = Calendar.getInstance();
    toDate.clear();
    toDate.setTime(row.getDate("s_to_date"));
    s.setToDate(toDate);
    return s;
  }

  public Title populateTitle(ResultSet row, Title t) throws SQLException {
    t.setTitle(row.getString("title"));
    Calendar fromDate = Calendar.getInstance();
    fromDate.clear();
    fromDate.setTime(row.getDate("t_from_date"));
    t.setFromDate(fromDate);
    Calendar toDate = Calendar.getInstance();
    toDate.clear();
    toDate.setTime(row.getDate("t_to_date"));
    t.setToDate(toDate);
    return t;
  }

  public void addRow(ResultSet row, WriteBatcher wb) throws SQLException {
    try {
      Employee newEmployee = populateEmployee(row, new Employee());
      if ( ! newEmployee.equals(lastEmployee) ) {
        processEmployee(wb, denormalize(newEmployee));
      }
      Salary salary = populateSalary(row, new Salary());
      //logger.debug("DEBUG: [BulkLoadFromJdbcWithSimpleJoins] salaries.contains(salary)=[" + salaries.contains(salary) + "]");
      if ( ! salaries.contains(salary) ) {
        salaries.add(salary);
      }
      Title title = populateTitle(row, new Title());
      //logger.debug("DEBUG: [BulkLoadFromJdbcWithSimpleJoins] titles.contains(title)=[" + titles.contains(title) + "]");
      if ( ! titles.contains(title) ) {
        titles.add(title);
      }
    } catch (Exception exception) {
      exception.printStackTrace();
      throw new RuntimeException(exception);
    }
  }

  public Employee denormalize(Employee newEmployee) {
    if ( lastEmployee != null ) {
      lastEmployee.setSalaries(salaries.toArray(new Salary[salaries.size()]));
      lastEmployee.setTitles(titles.toArray(new Title[titles.size()]));
    }
    lastEmployee = newEmployee;
    salaries.clear();
    titles.clear();
    return newEmployee;
  }

  public void processEmployee(WriteBatcher wb, Employee employee) {
    wb.addAs("/employees/" + employee.getEmployeeId() + ".json", employee);
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
      // perform the simplest possible join between three tables
      "SELECT e.*, s.salary, t.title, s.from_date s_from_date, s.to_date s_to_date, " +
      "    t.from_date t_from_date, t.to_date t_to_date " +
      "FROM employees e, salaries s, titles t " +
      "WHERE e.emp_no=s.emp_no " +
      "  AND e.emp_no=t.emp_no " +
      "ORDER BY e.emp_no, s.from_date, s.to_date, t.from_date, t.to_date",
      row -> {
        addRow(row, wb);
      }
    );
    denormalize(lastEmployee);
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
    return new DriverManagerDataSource(properties.jdbcUrl, properties.jdbcUser, properties.jdbcPassword);
  }
}

