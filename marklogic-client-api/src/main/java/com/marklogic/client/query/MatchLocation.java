/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.query;

/**
 * A MatchLocation represents a location in a document matched by a search.
 */
public interface MatchLocation {
  /**
   * Returns the path to the matching location in the document.
   * @return The path.
   */
  String getPath();

  /**
   * Returns the entire text only of the snippet, excluding any highlight tags.
   * @return The snippet text.
   */

  String getAllSnippetText();

  /**
   * Returns the array of elements in the snippet.
   *
   * Some snippets are highlighted, others are not.
   *
   * @return The array of snippet elements.
   */
  MatchSnippet[] getSnippets();
}
