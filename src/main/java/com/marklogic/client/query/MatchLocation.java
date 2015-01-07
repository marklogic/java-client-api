/*
 * Copyright 2012-2015 MarkLogic Corporation
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
package com.marklogic.client.query;

/**
 * A MatchLocation represents a location in a document matched by a search.
 */
public interface MatchLocation {
    /**
     * Returns the path to the matching location in the document.
     * @return The path.
     */
    public String getPath();

    /**
     * Returns the entire text only of the snippet, excluding any highlight tags.
     * @return The snippet text.
     */

    public String getAllSnippetText();

    /**
     * Returns the array of elements in the snippet.
     *
     * Some snippets are highlighted, others are not.
     *
     * @return The array of snippet elements.
     */
    public MatchSnippet[] getSnippets();
}
