/*
 * Copyright 2014-2019 MarkLogic Corporation
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

package com.marklogic.client.functionaltest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.TextDocumentManager;

public class TestMultithreading extends BasicJavaClientREST {

  private static String dbName = "TestMultithreadingDB";
  private static String[] fNames = { "TestMultithreadingDBDB-1" };

  @BeforeClass
  public static void setUp() throws Exception {
    System.out.println("In setup");
    configureRESTServer(dbName, fNames);
  }

  @After
  public void testCleanUp() throws Exception {
    clearDB();
    System.out.println("Running clear script");
  }

  @Test
  public void testMultithreading() throws KeyManagementException, NoSuchAlgorithmException, InterruptedException, IOException
  {
    ThreadClass dt1 = new ThreadClass("Thread A");
    ThreadClass dt2 = new ThreadClass("Thread B");

    Thread t1 = new Thread(dt1);
    t1.start(); // this will start thread of object 1
    Thread t2 = new Thread(dt2);
    t2.start(); // this will start thread of object 2
    t2.join();

    DatabaseClient client = getDatabaseClient("rest-reader", "x", getConnType());
    TextDocumentManager docMgr = client.newTextDocumentManager();

    for (int i = 1; i <= 5; i++) {
      String expectedUri = "/multithread-content-A/filename" + i + ".txt";
      String docUri = docMgr.exists("/multithread-content-A/filename" + i + ".txt").getUri();
      assertEquals("URI is not found", expectedUri, docUri);
    }

    for (int i = 1; i <= 5; i++) {
      String expectedUri = "/multithread-content-B/filename" + i + ".txt";
      String docUri = docMgr.exists("/multithread-content-B/filename" + i + ".txt").getUri();
      assertEquals("URI is not found", expectedUri, docUri);
    }

    // release client
    client.release();
  }

  @Test
  public void testMultithreadingMultipleSearch() throws KeyManagementException, NoSuchAlgorithmException, InterruptedException
  {
    System.out.println("testMultithreadingMultipleSearch");

    ThreadWrite tw1 = new ThreadWrite("Write Thread");
    Thread w1 = new Thread(tw1);
    w1.start();
    w1.join();

    ThreadSearch ts1 = new ThreadSearch("Search Thread 1");
    ThreadSearch ts2 = new ThreadSearch("Search Thread 2");
    ThreadSearch ts3 = new ThreadSearch("Search Thread 3");
    ThreadSearch ts4 = new ThreadSearch("Search Thread 4");
    ThreadSearch ts5 = new ThreadSearch("Search Thread 5");

    Thread t1 = new Thread(ts1);
    t1.start();
    Thread t2 = new Thread(ts2);
    t2.start();
    Thread t3 = new Thread(ts3);
    t3.start();
    Thread t4 = new Thread(ts4);
    t4.start();
    Thread t5 = new Thread(ts5);
    t5.start();

    t1.join();
    t2.join();
    t3.join();
    t4.join();
    t5.join();

    long totalAllDocumentsReturned = ts1.totalAllResults + ts2.totalAllResults + ts3.totalAllResults + ts4.totalAllResults + ts5.totalAllResults;
    assertTrue("Documents count is incorrect", totalAllDocumentsReturned == 750);
  }

  @AfterClass
  public static void tearDown() throws Exception {
    System.out.println("In tear down");
    cleanupRESTServer(dbName, fNames);
  }
}
