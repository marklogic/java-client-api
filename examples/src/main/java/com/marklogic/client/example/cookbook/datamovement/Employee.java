/*
 * Copyright © 2025 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.example.cookbook.datamovement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Calendar;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Employee {
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
  }
}
