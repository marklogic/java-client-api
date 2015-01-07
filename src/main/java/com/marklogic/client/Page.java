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
package com.marklogic.client;

import java.util.Iterator;

/** A generic interface for pagination through large sets of items of type &lt;T&gt;. */
public interface Page<T> extends Iterable<T> {
    /** The internal iterator of type T in this Page. This iterator is the same
     *  one used for {@link #hasNext()} and {@link #next()}.
     */
    public Iterator<T> iterator();

    /** Returns true if internal iterator has more elements.
     *  The internal iterator is separate from any new iterator created by calls to iterator().
     *  @return true if the internal iterator has more elements
     */
    public boolean hasNext();

    /** Returns the next element in the internal iterator, which is separate
     *  from any new iterator created by calls to iterator().
     *  @return the next element in the iteration
     */
    public T next();

    /** The start position of this page within all possible items.  For result sets
     * this is the position of the first result within the result set.
     * @return the start position
     */
    public long getStart();

    /** The page size which is the maximum number of items allowed in one page.
     * @return the page size */
    public long getPageSize();

    /** The total count (most likely an
     * <a href="http://docs.marklogic.com/xdmp:estimate">estimate</a>) of all
     * possible items in the set.  If this number is larger than getPageSize()
     * then hasNextPage() should be true and you most likely can retrieve
     * additional pages to get the remaining available items in the set.
     * For result sets this is the number of items within the result set.
     * For search result sets this is the estimated number of matching items.
     * That means you may see this number change as you paginate through a 
     * search result set and the server updates the estimate with something
     * more accurate.
     * @return the total count of possible items 
     * @see <a href="http://docs.marklogic.com/xdmp:estimate">xdmp:estimate</a>
     * @see <a href="http://docs.marklogic.com/search:estimate">search:estimate</a>
     */
    public long getTotalSize();

    /** The count of items in this page, which is always less than getPageSize().  
     * If ({@link #getTotalSize()} - {@link #getStart()}) &gt; {@link #getPageSize()}
     * then size() == getPageSize().
     * @return the count of items in this page
     */
    public long size();


    /** The number of pages covering all possible items. Since this is calculated
     * based on {@link #getTotalSize()}, it is often an
     * <a href="http://docs.marklogic.com/xdmp:estimate">estimate</a>
     * just like getTotalSize().
     * That means you may see this number change as you paginate through a search
     * result set and the server updates the estimate with something more accurate.
     * @return the number of pages.  In pseudo-code:
     * <pre>{@code if ( getPageSize() == 0 ) return 0;
     *Math.ceil( getTotalSize() /  getPageSize() ); 
     * }</pre>
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
     * @return the page number.  In pseudo-code:
     * <pre>{@code if ( getPageSize() == 0 ) return 0;
     *if ( getStart() % getPageSize() == 0 ) return getStart() / getPageSize();
     *else return Math.floor(getStart() / getPageSize()) + 1;
     * }</pre>
     */
    public long getPageNumber();

    /** @return true if {@code getPageSize()==0 or getPageNumber()==1 }
     */
    public boolean isFirstPage();

    /** @return true if {@code getPageSize()==0 or getPageNumber()==getTotalPages() }
     */
    public boolean isLastPage();
}
