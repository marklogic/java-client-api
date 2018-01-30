/*
 * Copyright 2018 MarkLogic Corporation
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

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;

import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.datamovement.DataMovementManager;
import com.marklogic.client.datamovement.QueryBatcher;
import com.marklogic.client.example.cookbook.datamovement.Employee.Gender;
import com.marklogic.client.example.cookbook.datamovement.Employee.Salary;
import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.client.query.StructuredQueryDefinition;

public class ExtractViaTemplate {
  private static int threadCount = 3;
  private static int batchSize   = 3;
  private String templateName = "employees.tde";

  public static final JSONDocumentManager docMgr =
    DatabaseClientSingleton.get().newJSONDocumentManager();
  public static final DataMovementManager moveMgr =
    DatabaseClientSingleton.get().newDataMovementManager();
  private static final SimpleDateFormat df =
    new SimpleDateFormat("yyyy-MM-dd");

  public static void main(String[] args) throws ParseException, IOException {
    new ExtractViaTemplate().run();
  }

  public void run() throws ParseException, IOException {
    setup();
    StructuredQueryDefinition query = new StructuredQueryBuilder().directory(1, "/employees/");
    QueryBatcher qb = moveMgr.newQueryBatcher(query)
      .onUrisReady(
        new ExtractViaTemplateListener()
          .withTemplate(templateName)
          .onTypedRowReady( row -> {
            System.out.println("row:" + row);
          })
      );
    moveMgr.startJob(qb);
    qb.awaitCompletion();
    moveMgr.stopJob(qb);
  }

  public void setup() throws ParseException, IOException {
    // insert a sample MarkLogic template which can extract fields from the
    // sample data below
    ObjectMapper mapper = new ObjectMapper()
      .configure(Feature.ALLOW_SINGLE_QUOTES, true);
    docMgr.writeAs(templateName, mapper.readTree(
      "{ 'template':{ 'description':'test template', 'context':'/firstName', " +
      "    'rows':[ { 'schemaName':'employee', 'viewName':'employee'," +
      "      'columns':[ { 'name':'firstName', 'scalarType':'string', 'val':'.' }," +
      "                  { 'name':'salary', 'scalarType':'int', 'nullable':true," +
      "                    'val':'head(../salaries/salary)'}" +
      " ] } ] } }"));

    // insert some sample documents
    String[][] employees = new String[][] {
      {"1", "1990-10-04", "Alice", "Edward", "FEMALE", "2012-04-05", "70675"},
      {"2", "1992-12-23", "Bob", "Miller", "MALE", "2010-06-01", "98023"},
      {"3", "1985-11-30", "Gerard", "Steven", "MALE", "2011-07-29", "87455"},
      {"4", "1970-01-08", "Evelyn", "Erick", "FEMALE", "2012-08-24", "62444"},
      {"5", "1978-05-14", "Daniel", "Washington", "MALE", "2007-02-17", "68978,76543,86732"},
      {"6", "1989-07-19", "Eve", "Alfred", "FEMALE", "2009-08-14", "70987,79083"},
      {"7", "1990-09-29", "Rachel", "Fisher", "FEMALE", "2015-01-01", "50678"},
      {"8", "1987-10-26", "Bruce", "Wayne", "MALE", "2010-04-09", "99873,106742"},
      {"9", "1992-11-25", "Thomas", "Crook", "MALE", "2013-07-07", "79003"},
      {"10", "1994-12-04", "Diana", "Trevor", "FEMALE", "2016-09-23", "67893,67993"}
    };
    for ( String[] employee : employees ) {
      Employee newEmployee = new Employee();
      newEmployee.setEmployeeId(Integer.parseInt(employee[0]));
      GregorianCalendar bday = new GregorianCalendar();
      bday.setTime(df.parse(employee[1]));
      newEmployee.setBirthDate(bday);
      newEmployee.setFirstName(employee[2]);
      newEmployee.setLastName(employee[3]);
      newEmployee.setGender(Gender.valueOf(employee[4]));
      ArrayList<Salary> salaries = new ArrayList<>();
      for ( String salary : employee[6].split(",") ) {
        Salary newSalary = new Salary();
        newSalary.setSalary(Integer.parseInt(salary));
        salaries.add(newSalary);
      }
      newEmployee.setSalaries(salaries.toArray(new Salary[salaries.size()]));
      docMgr.writeAs("/employees/" + employee[0] + ".json", newEmployee);
    }
  }
}
