/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.impl;

import com.marklogic.client.query.SuggestDefinition;

public class SuggestDefinitionImpl implements SuggestDefinition {

  private String options;
  private String stringCriteria;
  private String[] queries;
  private Integer limit;
  private Integer cursorPosition;


  @Override
  public String getOptionsName() {
    return options;
  }

  @Override
  public void setOptionsName(String optionsName) {
    this.options = optionsName;
  }

  @Override
  public void setStringCriteria(String qtext) {
    this.stringCriteria = qtext;
  }

  @Override
  public String getStringCriteria() {
    return stringCriteria;
  }

  @Override
  public void setLimit(Integer limit) {
    this.limit = limit;
  }

  @Override
  public Integer getLimit() {
    return limit;
  }

  @Override
  public void setCursorPosition(Integer cursorPosition) {
    this.cursorPosition = cursorPosition;
  }

  @Override
  public Integer getCursorPosition() {
    return cursorPosition;
  }

  @Override
  public void setQueryStrings(String... qtext) {
    this.queries = qtext;
  }

  @Override
  public String[] getQueryStrings() {
    return queries;
  }

}
