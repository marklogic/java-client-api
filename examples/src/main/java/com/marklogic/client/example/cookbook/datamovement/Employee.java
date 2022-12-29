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

import java.util.Calendar;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Employee {
  private static Logger logger = LoggerFactory.getLogger(Employee.class);
  private int employeeId;
  private Calendar birthDate;
  private String firstName;
  private String lastName;
  private Gender gender;
  private Calendar hireDate;
  private Salary[] salaries;
  private Title[] titles;

  public enum Gender { MALE, FEMALE };

  public Employee() {}

  public int getEmployeeId() {
    return employeeId;
  }

  public void setEmployeeId(int employeeId) {
    this.employeeId = employeeId;
  }

  public Calendar getBirthDate() {
    return birthDate;
  }

  public void setBirthDate(Calendar birthDate) {
    this.birthDate = birthDate;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public Gender getGender() {
    return gender;
  }

  public void setGender(Gender gender) {
    this.gender = gender;
  }

  public Calendar getHireDate() {
    return hireDate;
  }

  public void setHireDate(Calendar hireDate) {
    this.hireDate = hireDate;
  }

  public Salary[] getSalaries() {
    return salaries;
  }

  public void setSalaries(Salary[] salaries) {
    this.salaries = salaries;
  }

  public Title[] getTitles() {
    return titles;
  }

  public void setTitles(Title[] titles) {
    this.titles = titles;
  }

  public boolean equals(Employee e) {
    if ( e == null ) return false;
    if ( e == this ) return true;
    return new EqualsBuilder()
      .append(getEmployeeId(), e.getEmployeeId())
      .append(getBirthDate(), e.getBirthDate())
      .append(getFirstName(), e.getFirstName())
      .append(getLastName(), e.getLastName())
      .append(getGender(), e.getGender())
      .append(getHireDate(), e.getHireDate())
      .append(getSalaries(), e.getSalaries())
      .append(getTitles(), e.getTitles())
      .isEquals();
  }

  public int hashCode() {
    return new HashCodeBuilder(23, 43)
      .append(getEmployeeId())
      .append(getBirthDate())
      .append(getFirstName())
      .append(getLastName())
      .append(getGender().toString())
      .append(getHireDate())
      .append(getSalaries())
      .append(getTitles())
      .toHashCode();
  }

  @JsonIgnoreProperties(ignoreUnknown=true)
  public static class Salary {
    private int salary;
    private Calendar fromDate;
    private Calendar toDate;

    public int getSalary() {
        return salary;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }

    public Calendar getFromDate() {
        return fromDate;
    }

    public void setFromDate(Calendar fromDate) {
        this.fromDate = fromDate;
    }

    public Calendar getToDate() {
        return toDate;
    }

    public void setToDate(Calendar toDate) {
        this.toDate = toDate;
    }

    public boolean equals(Object obj) {
      if ( obj == null ) return false;
      if ( obj == this ) return true;
      if ( ! (obj instanceof Salary) ) return false;
      Salary s = (Salary) obj;
      return new EqualsBuilder()
        .append(getSalary(), s.getSalary())
        .append(getFromDate(), s.getFromDate())
        .append(getToDate(), s.getToDate())
        .isEquals();
    }

    public int hashCode() {
      return new HashCodeBuilder(19, 41)
        .append(getSalary())
        .append(getFromDate())
        .append(getToDate())
        .toHashCode();
    }
  }

  @JsonIgnoreProperties(ignoreUnknown=true)
  public static class Title {
    private String title;
    private Calendar fromDate;
    private Calendar toDate;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Calendar getFromDate() {
        return fromDate;
    }

    public void setFromDate(Calendar fromDate) {
        this.fromDate = fromDate;
    }

    public Calendar getToDate() {
        return toDate;
    }

    public void setToDate(Calendar toDate) {
        this.toDate = toDate;
    }

    public boolean equals(Object obj) {
      if ( obj == null ) return false;
      if ( obj == this ) return true;
      if ( ! (obj instanceof Title) ) return false;
      Title t = (Title) obj;
      return new EqualsBuilder()
        .append(getTitle(), t.getTitle())
        .append(getFromDate(), t.getFromDate())
        .append(getToDate(), t.getToDate())
        .isEquals();
    }

    public int hashCode() {
      return new HashCodeBuilder(17, 37)
        .append(getTitle())
        .append(getFromDate())
        .append(getToDate())
        .toHashCode();
    }
  }
}
