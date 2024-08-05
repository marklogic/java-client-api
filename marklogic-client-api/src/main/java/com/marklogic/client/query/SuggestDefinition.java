/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.query;

/**
 * Represents the criteria for a suggestion call, in which the beginning
 * of a term is supplied for possible suggested endings.
 */
public interface SuggestDefinition {

  /**
   * Returns the name of the query options used for this query.
   * @return The options name.
   */
  String getOptionsName();

  /**
   * Sets the name of the query options to be used for this query.
   *
   * If no query options node with the specified name exists, the search will fail.
   *
   * @param name The name of the saved query options node on the server.
   */
  void setOptionsName(String name);

  /**
   * Sets one or more criteria for the suggestion call.
   *
   * @param pqtext A string for input to suggestions.
   */
  void setStringCriteria(String pqtext);


  /**
   * Returns the text of the suggestion call.
   * @return The suggestion input.
   */
  String getStringCriteria();

  /**
   * Sets zero or more search strings to AND with the suggestion call.
   *
   * @param qtext Zero or more
   * string queries to qualify that input string.
   */
  void setQueryStrings(String... qtext);


  /**
   * Returns the array of strings set for this SuggestDefinition.
   * @return The query text strings and suggestion input.
   */
  String[] getQueryStrings();

  /**
   * Sets a limit for a suggest call.  Only this number of suggestions
   * will be returned by the server.  Server default is 10.
   *
   * @param limit The maximum number of suggestions to fetch.
   */
  void setLimit(Integer limit);

  /**
   * Returns the maximum number of suggestions to fetch.
   * @return The limit.
   */
  Integer getLimit();

  /**
   * Sets the cursor position to use in the suggest call.
   * @param cursorPosition The cursor position.
   */
  void setCursorPosition(Integer cursorPosition);

  /**
   * Returns the cursor position for the suggest call.
   * @return The cursor position
   */
  Integer getCursorPosition();


}
