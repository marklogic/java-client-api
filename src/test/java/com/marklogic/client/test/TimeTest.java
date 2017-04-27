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
package com.marklogic.client.test;

import java.util.Calendar;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import com.marklogic.client.pojo.annotation.Id;

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
