/*
 * Copyright 2012-2014 MarkLogic Corporation
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
package com.marklogic.client;

import java.util.Iterator;

/** A generic interface for pagination through large sets of items of type &lt;T&gt;. */
public interface Page<T> extends Iterable<T> {
    /** An iterator over the items in this page. */
    public Iterator<T> iterator();

    /** The start position of this page within all possible items.  For result sets
     * this is the position of the first result within the result set.
     * @return the start position
     */
    public long getStart();

    /** The page size which is the maximum number of items allowed in one page.
     * @return the page size */
    public long getPageSize();

    /** The total count (potentially an estimate) of all possible items in the set.
     *  For result sets this is the number of items within the result set.
     *  For search result sets this is the estimated number of matching items.
     * @return the total count of possible items */
    public long getTotalSize();

    /** The count of items in this page, which is always less than getPageSize().  
     * If ({@link #getTotalSize()} - {@link #getStart()}) &gt; {@link #getPageSize()}
     * then size() == getPageSize().
     * @return the count of items in this page
     */
    public long size();


    /** The number of pages covering all possible items. 
     * @return the number of pages.  Literally, 
     * <pre>{@code (long) Math.ceil((double) getTotalSize() / (double) getPageSize()); }</pre>
     */
    public long getTotalPages();

    /** Whether there are any items in this page.  
     * @return true if {@code size() > 0; }
     */
    public boolean hasContent();

    /** Whether there are any items in the next page.  
     * @return true if {@code getPageNumber() < getTotalPages(); }
     */
    public boolean hasNextPage();

    /** Whether there is a previous page.  
     * @return true if {@code getPageNumber() > 0 }
     */
    public boolean hasPreviousPage();

    /** The page number within the count of all possible pages.  
     * @return {@code (long) Math.floor((double) start / (double) getPageSize()) + 1; }
     */
    public long getPageNumber();

    /** @return true if {@code getPageNumber() == 1 }
     */
    public boolean isFirstPage();

    /** @return true if {@code getPageNumber() == getTotalPages() }
     */
    public boolean isLastPage();
}
