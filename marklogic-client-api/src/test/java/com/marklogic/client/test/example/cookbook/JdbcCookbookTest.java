/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.test.example.cookbook;

import com.marklogic.client.example.cookbook.Util;
import com.marklogic.client.example.cookbook.Util.ExampleProperties;
import com.marklogic.client.example.cookbook.datamovement.BulkExportToJdbc;
import com.marklogic.client.example.cookbook.datamovement.BulkLoadFromJdbcRaw;
import com.marklogic.client.example.cookbook.datamovement.BulkLoadFromJdbcWithJoins;
import com.marklogic.client.example.cookbook.datamovement.BulkLoadFromJdbcWithSimpleJoins;
import com.marklogic.client.example.cookbook.datamovement.IncrementalLoadFromJdbc;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.io.IOException;

public class JdbcCookbookTest {

  class Database {
//    Server hsqlDBServer;
    JdbcTemplate jdbcTemplate;
    Database() throws IOException {
      setupHSQLDBServer();
      jdbcTemplate = new JdbcTemplate(getDataSource());
    }

    private void setupHSQLDBServer() {
//      hsqlDBServer = new Server();
//      hsqlDBServer.setDatabaseName(0, "employees");
//      hsqlDBServer.setDatabasePath(0, "mem:employees");
//      hsqlDBServer.setPort(9002);
//      hsqlDBServer.start();
    }

    private DataSource getDataSource() throws IOException {
      ExampleProperties properties = Util.loadProperties();
      return new DriverManagerDataSource(properties.jdbcUrl, properties.jdbcUser, properties.jdbcPassword);
    }

    public void execute(String query) {
      jdbcTemplate.execute(query);
    }

    public void tearDown() {
//      hsqlDBServer.stop();
    }
  }

	@Disabled("Disabled until the Java Client drops Java 8 support, which then allows for the most recent version " +
		"of org.hsqldb:hsqldb to be used, thus avoiding a High CVE.")
  @Test
  public void testMain() throws Exception {
    Database hsqlDB = new Database();
    populateDataset(hsqlDB);
    BulkLoadFromJdbcRaw.main(new String[0]);
    BulkLoadFromJdbcWithSimpleJoins.main(new String[0]);
    BulkLoadFromJdbcWithJoins.main(new String[0]);
    IncrementalLoadFromJdbc.main(new String[0]);
    BulkExportToJdbc.main(new String[0]);
    deleteDataset(hsqlDB);
    hsqlDB.tearDown();
  }

  private void deleteDataset(Database myDB) {
    myDB.execute("DROP SCHEMA PUBLIC CASCADE");
  }

  private void populateDataset(Database hsqlDB) {
    hsqlDB.execute("CREATE TABLE employees (" +
      "emp_no      INTEGER         NOT NULL," +
      "birth_date  DATE            NOT NULL," +
      "first_name  VARCHAR(14)     NOT NULL," +
      "last_name   VARCHAR(16)     NOT NULL," +
      "gender      VARCHAR(2)      NOT NULL," +
      "hire_date   DATE            NOT NULL," +
      "PRIMARY KEY (emp_no));");

    hsqlDB.execute("CREATE TABLE titles (" +
      "emp_no      INTEGER         NOT NULL," +
      "title       VARCHAR(50)     NOT NULL," +
      "from_date   DATE            NOT NULL," +
      "to_date     DATE," +
      "FOREIGN KEY (emp_no) REFERENCES employees (emp_no) ON DELETE CASCADE," +
      "PRIMARY KEY (emp_no,title, from_date));");

    hsqlDB.execute("CREATE TABLE salaries (" +
    "emp_no      INTEGER         NOT NULL," +
    "salary      INTEGER         NOT NULL," +
    "from_date   DATE            NOT NULL," +
    "to_date     DATE            NOT NULL," +
    "FOREIGN KEY (emp_no) REFERENCES employees (emp_no) ON DELETE CASCADE," +
    "PRIMARY KEY (emp_no, from_date));");

    hsqlDB.execute("CREATE TABLE employees_export (" +
        "emp_no      INTEGER         NOT NULL," +
        "birth_date  DATE            NOT NULL," +
        "first_name  VARCHAR(14)     NOT NULL," +
        "last_name   VARCHAR(16)     NOT NULL," +
        "gender      VARCHAR(2)      NOT NULL," +
        "hire_date   DATE            NOT NULL," +
        "PRIMARY KEY (emp_no));");

      hsqlDB.execute("CREATE TABLE titles_export (" +
        "emp_no      INTEGER         NOT NULL," +
        "title       VARCHAR(50)     NOT NULL," +
        "from_date   DATE            NOT NULL," +
        "to_date     DATE," +
        "FOREIGN KEY (emp_no) REFERENCES employees_export (emp_no) ON DELETE CASCADE," +
        "PRIMARY KEY (emp_no,title, from_date));");

      hsqlDB.execute("CREATE TABLE salaries_export (" +
      "emp_no      INTEGER         NOT NULL," +
      "salary      INTEGER         NOT NULL," +
      "from_date   DATE            NOT NULL," +
      "to_date     DATE            NOT NULL," +
      "FOREIGN KEY (emp_no) REFERENCES employees_export (emp_no) ON DELETE CASCADE," +
      "PRIMARY KEY (emp_no, from_date));");

    hsqlDB.execute("INSERT INTO employees VALUES (1, '1990-10-04', 'Alice', 'Edward', 'F', '2012-04-05');");
    hsqlDB.execute("INSERT INTO employees VALUES (2, '1992-12-23', 'Bob', 'Miller', 'M', '2010-06-01');");
    hsqlDB.execute("INSERT INTO employees VALUES (3, '1985-11-30', 'Gerard', 'Steven', 'M', '2011-07-29');");
    hsqlDB.execute("INSERT INTO employees VALUES (4, '1970-01-08', 'Evelyn', 'Erick', 'F', '2012-08-24');");
    hsqlDB.execute("INSERT INTO employees VALUES (5, '1978-05-14', 'Daniel', 'Washington', 'M', '2007-02-17');");
    hsqlDB.execute("INSERT INTO employees VALUES (6, '1989-07-19', 'Eve', 'Alfred', 'F', '2009-08-14');");
    hsqlDB.execute("INSERT INTO employees VALUES (7, '1990-09-29', 'Rachel', 'Fisher', 'F', '2015-01-01');");
    hsqlDB.execute("INSERT INTO employees VALUES (8, '1987-10-26', 'Bruce', 'Wayne', 'M', '2010-04-09');");
    hsqlDB.execute("INSERT INTO employees VALUES (9, '1992-11-25', 'Thomas', 'Crook', 'M', '2013-07-07');");
    hsqlDB.execute("INSERT INTO employees VALUES (10, '1994-12-04', 'Diana', 'Trevor', 'F', '2016-09-23');");

    hsqlDB.execute("INSERT INTO titles VALUES (1, 'Engineer', '2012-04-05', '9999-01-01');");
    hsqlDB.execute("INSERT INTO titles VALUES (2, 'Staff Engineer', '2010-06-01', '9999-01-01');");
    hsqlDB.execute("INSERT INTO titles VALUES (3, 'Lead Engineer', '2011-07-29', '9999-01-01');");
    hsqlDB.execute("INSERT INTO titles VALUES (4, 'Engineer', '2012-08-24', '9999-01-01');");
    hsqlDB.execute("INSERT INTO titles VALUES (5, 'Engineer', '2007-02-17', '2009-05-09');");
    hsqlDB.execute("INSERT INTO titles VALUES (5, 'Senior Engineer', '2009-05-09', '2013-09-09');");
    hsqlDB.execute("INSERT INTO titles VALUES (5, 'Staff Engineer', '2013-09-09', '9999-01-01');");
    hsqlDB.execute("INSERT INTO titles VALUES (6, 'Engineer', '2009-08-14', '2013-09-12');");
    hsqlDB.execute("INSERT INTO titles VALUES (6, 'Senior Engineer', '2013-09-12', '9999-01-01');");
    hsqlDB.execute("INSERT INTO titles VALUES (7, 'Assistant Engineer', '2015-01-01', '9999-01-01');");
    hsqlDB.execute("INSERT INTO titles VALUES (8, 'Staff Engineer', '2010-04-09', '2014-04-09');");
    hsqlDB.execute("INSERT INTO titles VALUES (8, 'Principal Engineer', '2014-04-09', '9999-01-01');");
    hsqlDB.execute("INSERT INTO titles VALUES (9, 'Senior Engineer', '2013-07-07', '9999-01-01');");
    hsqlDB.execute("INSERT INTO titles VALUES (10, 'Engineer', '2016-09-23', '9999-01-01');");

    hsqlDB.execute("INSERT INTO salaries VALUES (1, 70675, '2012-04-05', '9999-01-01');");
    hsqlDB.execute("INSERT INTO salaries VALUES (2, 98023, '2010-06-01', '9999-01-01');");
    hsqlDB.execute("INSERT INTO salaries VALUES (3, 87455, '2011-07-29', '9999-01-01');");
    hsqlDB.execute("INSERT INTO salaries VALUES (4, 62444, '2012-08-24', '9999-01-01');");
    hsqlDB.execute("INSERT INTO salaries VALUES (5, 68978, '2007-02-17', '2009-05-09');");
    hsqlDB.execute("INSERT INTO salaries VALUES (5, 76543, '2009-05-09', '2013-09-09');");
    hsqlDB.execute("INSERT INTO salaries VALUES (5, 86732, '2013-09-09', '9999-01-01');");
    hsqlDB.execute("INSERT INTO salaries VALUES (6, 70987, '2009-08-14', '2013-09-12');");
    hsqlDB.execute("INSERT INTO salaries VALUES (6, 79083, '2013-09-12', '9999-01-01');");
    hsqlDB.execute("INSERT INTO salaries VALUES (7, 50678, '2015-01-01', '9999-01-01');");
    hsqlDB.execute("INSERT INTO salaries VALUES (8, 99873, '2010-04-09', '2014-04-09');");
    hsqlDB.execute("INSERT INTO salaries VALUES (8, 106742, '2014-04-09', '9999-01-01');");
    hsqlDB.execute("INSERT INTO salaries VALUES (9, 79003, '2013-07-07', '9999-01-01');");
    hsqlDB.execute("INSERT INTO salaries VALUES (10, 67893, '2016-09-23', '2016-12-01');");
    hsqlDB.execute("INSERT INTO salaries VALUES (10, 67993, '2016-12-01', '9999-01-01');");
  }
}
