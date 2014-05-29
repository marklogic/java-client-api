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
package com.marklogic.client.test;

import java.util.HashSet;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.marklogic.client.Page;
import com.marklogic.client.impl.BasicPage;

/** Implements com.marklogic.client.Page to test the default methods.
 **/
public class PageTest {
	class TestPage extends BasicPage<Object> {
		TestPage(Iterable<Object> iterable) {
			super(iterable);
		}
	}
    @Test
    public void testTestPage() {
		Iterable<Object> iterable = new HashSet<Object>();

		Page<Object> page = new TestPage(iterable).setStart(1).setPageSize(10).setTotalSize(100);
        assertEquals("Unexpected size", 10, page.size());
        assertEquals("Unexpected totalPages", 10, page.getTotalPages());
        assertEquals("Unexpected hasContent", true, page.hasContent());
        assertEquals("Unexpected hasNextPage", true, page.hasNextPage());
        assertEquals("Unexpected pageNumber", 1, page.getPageNumber());
        assertEquals("Unexpected isFirstPage", true, page.isFirstPage());
        assertEquals("Unexpected isLastPage", false, page.isLastPage());

		page = new TestPage(iterable).setStart(2).setPageSize(10).setTotalSize(100);
        assertEquals("Unexpected size", 10, page.size());
        assertEquals("Unexpected totalPages", 10, page.getTotalPages());
        assertEquals("Unexpected hasContent", true, page.hasContent());
        assertEquals("Unexpected hasNextPage", true, page.hasNextPage());
        assertEquals("Unexpected pageNumber", 1, page.getPageNumber());
        assertEquals("Unexpected isFirstPage", true, page.isFirstPage());
        assertEquals("Unexpected isLastPage", false, page.isLastPage());

		page = new TestPage(iterable).setStart(10).setPageSize(10).setTotalSize(100);
        assertEquals("Unexpected size", 10, page.size());
        assertEquals("Unexpected totalPages", 10, page.getTotalPages());
        assertEquals("Unexpected hasContent", true, page.hasContent());
        assertEquals("Unexpected hasNextPage", true, page.hasNextPage());
        assertEquals("Unexpected pageNumber", 2, page.getPageNumber());
        assertEquals("Unexpected isFirstPage", false, page.isFirstPage());
        assertEquals("Unexpected isLastPage", false, page.isLastPage());

		page = new TestPage(iterable).setStart(12).setPageSize(10).setTotalSize(100);
        assertEquals("Unexpected size", 10, page.size());
        assertEquals("Unexpected totalPages", 10, page.getTotalPages());
        assertEquals("Unexpected hasContent", true, page.hasContent());
        assertEquals("Unexpected hasNextPage", true, page.hasNextPage());
        assertEquals("Unexpected pageNumber", 2, page.getPageNumber());
        assertEquals("Unexpected isFirstPage", false, page.isFirstPage());
        assertEquals("Unexpected isLastPage", false, page.isLastPage());

		page = new TestPage(iterable).setStart(22).setPageSize(20).setTotalSize(100);
        assertEquals("Unexpected size", 20, page.size());
        assertEquals("Unexpected totalPages", 5, page.getTotalPages());
        assertEquals("Unexpected hasContent", true, page.hasContent());
        assertEquals("Unexpected hasNextPage", true, page.hasNextPage());
        assertEquals("Unexpected pageNumber", 2, page.getPageNumber());
        assertEquals("Unexpected isFirstPage", false, page.isFirstPage());
        assertEquals("Unexpected isLastPage", false, page.isLastPage());

		page = new TestPage(iterable).setStart(18).setPageSize(20).setTotalSize(20);
        assertEquals("Unexpected size", 20, page.size());
        assertEquals("Unexpected totalPages", 1, page.getTotalPages());
        assertEquals("Unexpected hasContent", true, page.hasContent());
        assertEquals("Unexpected hasNextPage", false, page.hasNextPage());
        assertEquals("Unexpected pageNumber", 1, page.getPageNumber());
        assertEquals("Unexpected isFirstPage", true, page.isFirstPage());
        assertEquals("Unexpected isLastPage", true, page.isLastPage());

		page = new TestPage(iterable).setStart(905).setPageSize(100).setTotalSize(990);
        assertEquals("Unexpected size", 90, page.size());
        assertEquals("Unexpected totalPages", 10, page.getTotalPages());
        assertEquals("Unexpected hasContent", true, page.hasContent());
        assertEquals("Unexpected hasNextPage", false, page.hasNextPage());
        assertEquals("Unexpected pageNumber", 10, page.getPageNumber());
        assertEquals("Unexpected isFirstPage", false, page.isFirstPage());
        assertEquals("Unexpected isLastPage", true, page.isLastPage());
	}
}
