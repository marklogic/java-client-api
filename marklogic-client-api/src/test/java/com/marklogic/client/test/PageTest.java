/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.test;

import com.marklogic.client.Page;
import com.marklogic.client.impl.BasicPage;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

/** Implements com.marklogic.client.Page to test the default methods.
 **/
public class PageTest {
  class TestPage extends BasicPage<Object> {
    TestPage(Iterator<Object> iterator, long start, long pageSize, long totalSize) {
      super(iterator, start, pageSize, totalSize);
    }
  }
  @Test
  public void testTestPage() {
    Iterator<Object> iterator = new HashSet<>().iterator();

    Page<Object> page = new TestPage(iterator, 1, 10, 100);
    assertEquals( 10, page.size());
    assertEquals( 10, page.getTotalPages());
    assertEquals( true, page.hasContent());
    assertEquals( true, page.hasNextPage());
    assertEquals( 1, page.getPageNumber());
    assertEquals( true, page.isFirstPage());
    assertEquals( false, page.isLastPage());

    page = new TestPage(iterator, 1, 1, 100);
    assertEquals( 1, page.size());
    assertEquals( 100, page.getTotalPages());
    assertEquals( true, page.hasContent());
    assertEquals( true, page.hasNextPage());
    assertEquals( 1, page.getPageNumber());
    assertEquals( true, page.isFirstPage());
    assertEquals( false, page.isLastPage());

    page = new TestPage(iterator, 10, 0, 101);
    assertEquals( 0, page.size());
    assertEquals( 101, page.getTotalSize());
    assertEquals( 0, page.getTotalPages());
    assertEquals( false, page.hasContent());
    assertEquals( false, page.hasNextPage());
    assertEquals( 0, page.getPageNumber());
    assertEquals( true, page.isFirstPage());
    assertEquals( true, page.isLastPage());

    page = new TestPage(iterator, 2, 10, 100);
    assertEquals( 10, page.size());
    assertEquals( 10, page.getTotalPages());
    assertEquals( true, page.hasContent());
    assertEquals( true, page.hasNextPage());
    assertEquals( 1, page.getPageNumber());
    assertEquals( true, page.isFirstPage());
    assertEquals( false, page.isLastPage());

    page = new TestPage(iterator, 10, 10, 100);
    assertEquals( 10, page.size());
    assertEquals( 10, page.getTotalPages());
    assertEquals( true, page.hasContent());
    assertEquals( true, page.hasNextPage());
    assertEquals( 1, page.getPageNumber());
    assertEquals( true, page.isFirstPage());
    assertEquals( false, page.isLastPage());

    page = new TestPage(iterator, 12, 10, 100);
    assertEquals( 10, page.size());
    assertEquals( 10, page.getTotalPages());
    assertEquals( true, page.hasContent());
    assertEquals( true, page.hasNextPage());
    assertEquals( 2, page.getPageNumber());
    assertEquals( false, page.isFirstPage());
    assertEquals( false, page.isLastPage());

    page = new TestPage(iterator, 20, 20, 100);
    assertEquals( 20, page.size());
    assertEquals( 5, page.getTotalPages());
    assertEquals( true, page.hasContent());
    assertEquals( true, page.hasNextPage());
    assertEquals( 1, page.getPageNumber());
    assertEquals( true, page.isFirstPage());
    assertEquals( false, page.isLastPage());

    page = new TestPage(iterator, 22, 20, 100);
    assertEquals( 20, page.size());
    assertEquals( 5, page.getTotalPages());
    assertEquals( true, page.hasContent());
    assertEquals( true, page.hasNextPage());
    assertEquals( 2, page.getPageNumber());
    assertEquals( false, page.isFirstPage());
    assertEquals( false, page.isLastPage());

    page = new TestPage(iterator, 18, 20, 20);
    assertEquals( 20, page.size());
    assertEquals( 1, page.getTotalPages());
    assertEquals( true, page.hasContent());
    assertEquals( false, page.hasNextPage());
    assertEquals( 1, page.getPageNumber());
    assertEquals( true, page.isFirstPage());
    assertEquals( true, page.isLastPage());

    page = new TestPage(iterator, 905, 100, 990);
    assertEquals( 90, page.size());
    assertEquals( 10, page.getTotalPages());
    assertEquals( true, page.hasContent());
    assertEquals( false, page.hasNextPage());
    assertEquals( 10, page.getPageNumber());
    assertEquals( false, page.isFirstPage());
    assertEquals( true, page.isLastPage());
  }
}
