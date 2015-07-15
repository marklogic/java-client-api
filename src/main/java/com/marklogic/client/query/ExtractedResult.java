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

import com.marklogic.client.io.Format;

/** Surfaces the data sent from the server-side XQuery search:search API. Note
 * that the most important function is to provide access to the list of ExtractedItem
 * objects.  For example:
 * <pre>{@code    String combinedSearch = 
 *      "<search:search xmlns:search=\"http://marklogic.com/appservices/search\">" +
 *        "<search:qtext>my search terms</search:qtext>" +
 *        "<search:options>" +
 *            "<search:extract-document-data>" +
 *                "<search:extract-path>/some/path</search:extract-path>" +
 *                "<search:extract-path>/another/path/*</search:extract-path>" +
 *            "</search:extract-document-data>" +
 *        "</search:options>" +
 *      "</search:search>";
 *    StringHandle queryHandle = new StringHandle(combinedSearch).withMimetype("application/xml");
 *    QueryDefinition query = queryMgr.newRawCombinedQueryDefinition(queryHandle);
 *    SearchHandle results = queryMgr.search(query, new SearchHandle());
 *    MatchDocumentSummary[] summaries = results.getMatchResults();
 *    for (MatchDocumentSummary summary : summaries) {
 *        ExtractedResult extracted = summary.getExtracted();
 *        if ( Format.XML == summary.getFormat() ) {
 *            for (ExtractedItem item : extracted) {
 *                Document extractItem = item.getAs(Document.class);
 *                ...
 *            }
 *        }
 *    }}</pre>
 *
 * Where you can, of course, use any appropriate StructureReadHandle or corresponding
 * class (provided by that handle's receiveAs() method) passed to the
 * {@link ExtractedItem#getAs} method.
 **/
public interface ExtractedResult extends Iterable<ExtractedItem> {

    /** The xquery type of the extracted data.  For XML it will be "element".  For
     * JSON it will be "object" or "array".  Not always available, returns null if
     * unavailable. **/
    public String getKind();

    /** Returns true if the underlying node is an "extracted-none" XML element or
     * JSON property. */
    public boolean isEmpty();

    /** The number of ExtractedItem objects in the Iterator. */
    public int size();

    /** Returns the next element in the internal iterator, which is separate
     *  from any new iterator created by calls to iterator().
     *  @return the next element in the iteration
     */
    public ExtractedItem next();

    /** Returns true if internal iterator has more elements.
     *  The internal iterator is separate from any new iterator created by calls to iterator().
     *  @return true if the internal iterator has more elements
     */
    public boolean hasNext();
}
