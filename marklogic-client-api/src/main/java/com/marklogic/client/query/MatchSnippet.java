/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.query;

/**
 * A MatchSnippet is a Java object representation of a part of a snippet.
 */
public interface MatchSnippet {
  /**
   * Identifies whether this run of text in the snippet matches the search criteria or not.
   *
   * If the text matches the criteria, it will be highlighted.
   *
   * @return The hightlight value.
   */
  boolean isHighlighted();

  /**
   * Returns the string value of the run of text in this part of the snippet.
   * @return The text string.
   */
  String getText();
}
