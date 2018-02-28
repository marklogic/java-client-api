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
package com.marklogic.client.impl;

import java.util.Iterator;
import com.marklogic.client.Page;

public class BasicPage<T> implements Page<T> {
  private Iterator<T> iterator;
  private long start;
  private Long size = null;
  private long pageSize;
  private long totalSize;

  protected BasicPage(Class<T> type) {
  }

  public BasicPage(Iterator<T> iterator, long start, long pageSize, long totalSize) {
    this.iterator = iterator;
    this.start = start;
    this.pageSize = pageSize;
    this.totalSize = totalSize;
  }

  @Override
  public Iterator<T> iterator() {
    return iterator;
  }

  @Override
  public boolean hasNext() {
    return iterator.hasNext();
  }

  @Override
  public T next() {
    return iterator.next();
  }

  @Override
  public long getStart() {
    return start;
  }

  public BasicPage<T> setStart(long start) {
    this.start = start;
    return this;
  }

  @Override
  public long getPageSize() {
    return pageSize;
  }

  public BasicPage<T> setPageSize(long pageSize) {
    this.pageSize = pageSize;
    return this;
  }

  @Override
  public long getTotalSize() {
    return totalSize;
  }

  public BasicPage<T> setTotalSize(long totalSize) {
    this.totalSize = totalSize;
    return this;
  }

  public BasicPage<T> setSize(long size) {
    this.size = new Long(size);
    return this;
  }

  @Override
  public long size() {
    if ( size != null ) return size.longValue();
    if ( getPageSize() == 0 ) {
      return 0;
    } else if ( hasNextPage() ) {
      return getPageSize();
    } else if ((getTotalSize() % getPageSize()) == 0) {
      return getPageSize();
    } else {
      return getTotalSize() % getPageSize();
    }
  }

  @Override
  public long getTotalPages() {
    if ( getPageSize() == 0 ) return 0;
    return (long) Math.ceil((double) getTotalSize() / (double) getPageSize());
  }

  @Override
  public boolean hasContent() {
    return size() > 0;
  }

  @Override
  public boolean hasNextPage() {
    return getPageNumber() < getTotalPages();
  }

  @Override
  public boolean hasPreviousPage() {
    return getPageNumber() > 1;
  }

  @Override
  public long getPageNumber() {
    if ( getPageSize() == 0 ) return 0;
    double _start = (double) start;
    double _pageSize = (double) getPageSize();
    if ( _start % _pageSize == 0 ) return new Double(_start / _pageSize).longValue();
    else return (long) Math.floor(_start / _pageSize) + 1;
  }

  @Override
  public boolean isFirstPage() {
    if ( getPageSize() == 0 ) return true;
    return getPageNumber() == 1;
  }

  @Override
  public boolean isLastPage() {
    if ( getPageSize() == 0 ) return true;
    return getPageNumber() == getTotalPages();
  }
}
