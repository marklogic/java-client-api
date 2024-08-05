/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.test;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.marklogic.client.pojo.annotation.Id;

import java.util.Calendar;
import java.util.Date;

public class TimeTest {
  @Id public String id;

  @JsonTypeInfo(use=JsonTypeInfo.Id.NONE, include=JsonTypeInfo.As.EXTERNAL_PROPERTY)
  public Calendar calendarTest;

  /* The timezone below works for serializing but not for deserializing */
  @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone="CET")
  @JsonTypeInfo(use=JsonTypeInfo.Id.NONE, include=JsonTypeInfo.As.EXTERNAL_PROPERTY)
  public Calendar calendarTestCet;

  @JsonTypeInfo(use=JsonTypeInfo.Id.NONE, include=JsonTypeInfo.As.EXTERNAL_PROPERTY)
  public Date dateTest;

  public TimeTest() {}
  public TimeTest(String id, Calendar timestamp) {
    this.id = id;
    this.calendarTest = timestamp;
    this.calendarTestCet = timestamp;
    this.dateTest = timestamp.getTime();
  }
}
