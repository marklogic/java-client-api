/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.functionaltest;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.Transaction;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.document.GenericDocumentManager;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.eval.EvalResult;
import com.marklogic.client.eval.EvalResult.Type;
import com.marklogic.client.eval.EvalResultIterator;
import com.marklogic.client.eval.ServerEvaluationCall;
import com.marklogic.client.io.BytesHandle;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.InputStreamHandle;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/*
 * This test is intended for
 * looping eval query for more than 100 times
 * Eval with transactions
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
public class TestEvalwithRunTimeDBnTransactions extends BasicJavaClientREST {
  private static String dbName = "TestEvalXqueryWithTransDB";
  private static String[] fNames = { "TestEvalXqueryWithTransDB-1" };

  private DatabaseClient client;

  @BeforeAll
  public static void setUpBeforeClass() throws Exception {
    System.out.println("In setup");
    configureRESTServer(dbName, fNames);
    createUserRolesWithPrevilages("test-eval", "xdbc:eval", "xdbc:eval-in", "xdmp:eval-in", "any-uri", "xdbc:invoke");
    createRESTUser("eval-user", "x", "test-eval", "rest-admin", "rest-writer", "rest-reader");
  }

  @AfterAll
  public static void tearDownAfterClass() throws Exception {
    System.out.println("In tear down");
    cleanupRESTServer(dbName, fNames);
    deleteRESTUser("eval-user");
    deleteUserRole("test-eval");
  }

  @BeforeEach
  public void setUp() throws KeyManagementException, NoSuchAlgorithmException, Exception {
    client = getDatabaseClient("eval-user", "x", getConnType());
  }

  @AfterEach
  public void tearDown() throws Exception {
    clearDB();
    client.release();
  }

  // loop the eval query more than 150 times and should not stuck
  @Test
  public void test1MultipleEvalQueries() throws Exception {

    GenericDocumentManager docMgr = client.newDocumentManager();
    File file1 = null;
    FileInputStream fis = null;
    try {
      file1 = new File("src/test/java/com/marklogic/client/functionaltest/data/Sega-4MB.jpg");
      fis = new FileInputStream(file1);
      InputStreamHandle handle1 = new InputStreamHandle(fis);
      handle1.setFormat(Format.BINARY);
      docMgr.write("/binary4mbdoc", handle1);
      final long binaryLength = file1.length();
      String query = "declare variable $myInteger as xs:integer external;"
          + "(fn:doc()/binary(),$myInteger,xdmp:database-name(xdmp:database()))";
      // This was previously 330, with no explanation as to why; given that the comment above says "more than 150",
      // making this 160 instead so it's a bit faster
      for (int i = 0; i <= 160; i++) {
        ServerEvaluationCall evl = client.newServerEval().xquery(query);
        evl.addVariable("myInteger", i);
        EvalResultIterator evr = evl.eval();
        while (evr.hasNext()) {
          EvalResult er = evr.next();
          if (er.getType().equals(Type.INTEGER)) {
            assertEquals(i, er.getNumber().intValue());
          } else if (er.getType().equals(Type.BINARY)) {
            byte[] bytes = er.get(new BytesHandle()).get();
            assertEquals(binaryLength, bytes.length);
          } else if (er.getType().equals(Type.STRING)) {
            assertEquals("TestEvalXqueryWithTransDB", er.getString());
          } else {
            fail("Getting incorrect type");
          }
        }
      }
    } catch (Exception e) {
      throw e;
    } finally {
      fis.close();
    }
  }

  // issue 170 are blocking the test progress in here
  @Test
  public void test2XqueryEvalTransactions() throws KeyManagementException, NoSuchAlgorithmException, Exception {
    int count = 1;
    boolean tstatus = true;
    Transaction t1 = client.openTransaction();
    try {
      XMLDocumentManager docMgr = client.newXMLDocumentManager();
      Map<String, String> map = new HashMap<>();
      DocumentWriteSet writeset = docMgr.newWriteSet();
      for (int i = 0; i < 102; i++) {
        writeset.add("/sec" + i + ".xml", new DOMHandle(getDocumentContent("This is so sec" + i)));
        map.put("/sec" + i + ".xml", convertXMLDocumentToString(getDocumentContent("This is so sec" + i)));
        if (count % 100 == 0) {
          docMgr.write(writeset, t1);
          writeset = docMgr.newWriteSet();
        }
        count++;
      }
      if (count % 100 > 0) {
        docMgr.write(writeset, t1);
      }
      String query = "declare variable $myInteger as xs:integer external;"
          + "(fn:count(fn:doc()))";
      ServerEvaluationCall evl = client.newServerEval().xquery(query);
      EvalResultIterator evr = evl.eval();
      assertEquals(0, evr.next().getNumber().intValue());
      evl = client.newServerEval().xquery(query).transaction(t1);
      evr = evl.eval();
      assertEquals(102, evr.next().getNumber().intValue());
    } catch (Exception e) {
      System.out.println(e.getMessage());
      tstatus = true;
      throw e;
    } finally {
      if (tstatus) {
        t1.rollback();
      }
    }
  }

  // issue 171 are blocking the test progress in here
  @Test
  public void test3XqueryEvalTransactionsWithRunTimeDB() throws KeyManagementException, NoSuchAlgorithmException, Exception {
    int count = 1;
    boolean tstatus = true;

    String[] fNamesTmp = { "test3XqueryEvalDB-1" };
    String dbNameTmp = "test3XqueryEvalDB";

    configureRESTServer(dbNameTmp, fNamesTmp);

    DatabaseClient client2 = getDatabaseClient("eval-user", "x", getConnType());
    Transaction t1 = client2.openTransaction();
    try {
      XMLDocumentManager docMgr = client2.newXMLDocumentManager();
      Map<String, String> map = new HashMap<>();
      DocumentWriteSet writeset = docMgr.newWriteSet();
      for (int i = 0; i < 102; i++) {
        writeset.add("/sec" + i + ".xml", new DOMHandle(getDocumentContent("This is so sec" + i)));
        map.put("/sec" + i + ".xml", convertXMLDocumentToString(getDocumentContent("This is so sec" + i)));
        if (count % 100 == 0) {
          docMgr.write(writeset, t1);
          writeset = docMgr.newWriteSet();
        }
        count++;
      }
      if (count % 100 > 0) {
        docMgr.write(writeset, t1);
      }
      String query = "declare variable $myInteger as xs:integer external;"
          + "(fn:count(fn:doc()))";
      ServerEvaluationCall evl = client2.newServerEval().xquery(query);
      EvalResultIterator evr = evl.eval();
      assertEquals(0, evr.next().getNumber().intValue());
      evl = client2.newServerEval().xquery(query).transaction(t1);
      evr = evl.eval();
      assertEquals(102, evr.next().getNumber().intValue());
    } catch (Exception e) {
      System.out.println(e.getMessage());
      tstatus = true;
      throw e;
    } finally {
      if (tstatus) {
        t1.rollback();
        client2.release();
      }
      cleanupRESTServer(dbNameTmp, fNamesTmp);
      associateRESTServerWithDB(getRestServerName(), "Documents");
    }
  }
}
