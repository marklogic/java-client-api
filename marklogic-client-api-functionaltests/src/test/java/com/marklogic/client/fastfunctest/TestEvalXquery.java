/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.fastfunctest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.FailedRequestException;
import com.marklogic.client.document.DocumentManager;
import com.marklogic.client.eval.EvalResult;
import com.marklogic.client.eval.EvalResult.Type;
import com.marklogic.client.eval.EvalResultIterator;
import com.marklogic.client.eval.ServerEvaluationCall;
import com.marklogic.client.io.*;
import com.marklogic.client.io.DocumentMetadataHandle.Capability;
import org.junit.jupiter.api.*;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.*;

/*
 * This test is meant for xquery to
 * verify the eval api can handle all the formats of documents
 * verify eval api can handle all the return types
 * Verify eval takes all kind of variable with name spaces
 */
public class TestEvalXquery extends AbstractFunctionalTest {

  @BeforeAll
  public static void setUpBeforeClass() throws Exception {
    TestEvalXquery.createUserRolesWithPrevilages("test-eval", "xdbc:eval", "any-uri", "xdbc:invoke");
    TestEvalXquery.createRESTUser("eval-user", "x", "test-eval");
  }

  @AfterAll
  public static void tearDownAfterClass() throws Exception {
    TestEvalXquery.deleteRESTUser("eval-user");
    TestEvalXquery.deleteUserRole("test-eval");
  }

  @BeforeEach
  public void setUp() throws KeyManagementException, NoSuchAlgorithmException, Exception {
    client = getDatabaseClient("eval-user", "x", getConnType());
  }

  @AfterEach
  public void tearDown() throws Exception {
    client.release();
  }

  /*
   * This method is validating all the return values from xquery
   */
  void validateReturnTypes(EvalResultIterator evr) throws Exception {
    while (evr.hasNext())
    {
      EvalResult er = evr.next();
      if (er.getType().equals(Type.XML)) {
        DOMHandle dh = new DOMHandle();
        dh = er.get(dh);
        if (dh.get().getDocumentElement().hasChildNodes()) {
          // System.out.println("Type XML  :"+convertXMLDocumentToString(dh.get()));
          assertEquals( "<foo attr=\"attribute\"><?processing instruction?><!--comment-->test1</foo>", convertXMLDocumentToString(dh.get()));
        } else {
          assertEquals( "<test1/>", convertXMLDocumentToString(dh.get()));
        }
      }
      else if (er.getType().equals(Type.JSON)) {
        JacksonHandle jh = new JacksonHandle();
        jh = er.get(jh);
        // System.out.println("Type JSON :"+jh.get().toString());
        assertTrue( jh.get().has("test"));
      }
      else if (er.getType().equals(Type.TEXTNODE)) {
        assertTrue( er.getAs(String.class).equals("test1"));
        // System.out.println("type txt node :"+er.getAs(String.class));

      } else if (er.getType().equals(Type.BINARY)) {
        FileHandle fh = new FileHandle();
        fh = er.get(fh);
        // System.out.println("type binary :"+fh.get().length());
        assertEquals( 2, fh.get().length());
      } else if (er.getType().equals(Type.BOOLEAN)) {
        assertTrue( er.getBoolean());
        // System.out.println("type boolean:"+er.getBoolean());
      }
      else if (er.getType().equals(Type.INTEGER)) {
        // System.out.println("type Integer: "+er.getNumber().longValue());
        assertEquals( 31, er.getNumber().intValue());
      }
      else if (er.getType().equals(Type.STRING)) {
        // There is git issue 152
          String str = er.getString();
        assertEquals( "xml", str);
        System.out.println("type string: " + str);
      } else if (er.getType().equals(Type.NULL)) {
        // There is git issue 151
        // assertNull(er.getAs(String.class));
        System.out.println("Testing is empty sequence is NUll?" + er.getAs(String.class));
      } else if (er.getType().equals(Type.OTHER)) {
        // There is git issue 151
        // System.out.println("Testing is Others? "+er.getAs(String.class));
        assertTrue( er.getString().contains("PT0S"));

      } else if (er.getType().equals(Type.ANYURI)) {
        // System.out.println("Testing is AnyUri? "+er.getAs(String.class));
        assertEquals( "test1.xml", er.getAs(String.class));

      } else if (er.getType().equals(Type.DATE)) {
        // Adjusted this to ignore timezone
        String val = er.getAs(String.class);
        assertTrue(val.startsWith("2002-03-07"));
      } else if (er.getType().equals(Type.DATETIME)) {
        // Adjusted this to ignore timezone
        String val = er.getAs(String.class);
        assertTrue(val.startsWith("2010-01"), "Unexpected value: " + val);
        assertTrue(val.contains(":13:50.873"), "Unexpected value: " + val);
      } else if (er.getType().equals(Type.DECIMAL)) {
        // System.out.println("Testing is Decimal? "+er.getAs(String.class));
        assertEquals( "10.5", er.getAs(String.class));

      } else if (er.getType().equals(Type.DOUBLE)) {
        // System.out.println("Testing is Double? "+er.getAs(String.class));
        assertEquals(1.0471975511966, er.getNumber().doubleValue(), 0);

      } else if (er.getType().equals(Type.DURATION)) {
        System.out.println("Testing is Duration? " + er.getAs(String.class));
        // assertEquals(0.4903562,er.getNumber().floatValue());
      } else if (er.getType().equals(Type.FLOAT)) {
        // System.out.println("Testing is Float? "+er.getAs(String.class));
        assertEquals(20, er.getNumber().floatValue(), 0);
      } else if (er.getType().equals(Type.GDAY)) {
        // System.out.println("Testing is GDay? "+er.getAs(String.class));
        assertEquals( "---01", er.getAs(String.class));
      } else if (er.getType().equals(Type.GMONTH)) {
        // System.out.println("Testing is GMonth "+er.getAs(String.class));
        assertEquals( "--01", er.getAs(String.class));
      } else if (er.getType().equals(Type.GMONTHDAY)) {
        // System.out.println("Testing is GMonthDay? "+er.getAs(String.class));
        assertEquals( "--12-25-14:00", er.getAs(String.class));
      } else if (er.getType().equals(Type.GYEAR)) {
        // System.out.println("Testing is GYear? "+er.getAs(String.class));
        assertEquals( "2005-12:00", er.getAs(String.class));
      } else if (er.getType().equals(Type.GYEARMONTH)) {
        // System.out.println("Testing is GYearMonth?1976-02 "+er.getAs(String.class));
        assertEquals( "1976-02", er.getAs(String.class));
      } else if (er.getType().equals(Type.HEXBINARY)) {
        // System.out.println("Testing is HEXBINARY? "+er.getAs(String.class));
        assertEquals( "BEEF", er.getAs(String.class));
      } else if (er.getType().equals(Type.QNAME)) {
          String str = er.getString();
        // System.out.println("Testing is QNAME integer"+er.getAs(String.class));
        assertTrue( str.contains("integer") || str.contains("fn:empty"));
      } else if (er.getType().equals(Type.TIME)) {
        // System.out.println("Testing is TIME? "+er.getAs(String.class));
        assertEquals( "10:00:00", er.getAs(String.class));
      } else if (er.getType().equals(Type.ATTRIBUTE)) {
        // System.out.println("Testing is ATTRIBUTE? "+er.getAs(String.class));
        assertEquals( "attribute", er.getAs(String.class));

      } else if (er.getType().equals(Type.PROCESSINGINSTRUCTION)) {
        // System.out.println("Testing is ProcessingInstructions? "+er.getAs(String.class));
        assertEquals( "<?processing instruction?>", er.getAs(String.class));
      } else if (er.getType().equals(Type.COMMENT)) {
        // System.out.println("Testing is Comment node? "+er.getAs(String.class));
        assertEquals( "<!--comment-->", er.getAs(String.class));
      } else if (er.getType().equals(Type.BASE64BINARY)) {
        // System.out.println("Testing is Base64Binary  "+er.getAs(String.class));
        assertEquals( "DEADBEEF", er.getAs(String.class));
      } else {
        System.out.println("Got something which is not belongs to anytype we support " + er.getAs(String.class));
        fail("getting in else part, missing a type  ");
      }
    }
    evr.close();
  }

  // This test intended to verify a simple xquery for inserting and reading
  // documents of different formats and returning boolean,string number types
  @Test
  public void testXqueryReturningDifferentAutomicTypes() throws KeyManagementException, NoSuchAlgorithmException, Exception {
    String insertXML = "xdmp:document-insert(\"test1.xml\",<foo>test1</foo>)";
    String insertJSON = "xdmp:document-insert(\"test2.json\",object-node {\"test\":\"hello\"})";
    String insertTXT = "xdmp:document-insert(\"/test3.txt\",text { \"This is a text document.\" })";
    String insertBinary = "xdmp:document-insert(\"test4.bin\",binary {\"ABCD\"})";
    String query1 = "fn:exists(fn:doc())";
    String query2 = "fn:count(fn:doc())";
    String query3 = "xdmp:database-name(xdmp:database())";
    String readDoc = "fn:doc()";

    boolean response = client.newServerEval().xquery(insertXML).eval().hasNext();
    // System.out.printlnln(response);
    assertFalse( response);
    response = client.newServerEval().xquery(insertJSON).eval().hasNext();
    // System.out.printlnln(response);
    assertFalse( response);
    response = client.newServerEval().xquery(insertTXT).eval().hasNext();
    // System.out.printlnln(response);
    assertFalse( response);
    response = client.newServerEval().xquery(insertBinary).eval().hasNext();
    // System.out.printlnln(response);
    assertFalse( response);
    boolean response1 = client.newServerEval().xquery(query1).eval().next().getBoolean();
    // System.out.printlnln(response1);
    assertTrue( response1);
    int response2 = client.newServerEval().xquery(query2).eval().next().getNumber().intValue();
    // System.out.printlnln(response2);
    assertEquals( 4, response2);
    String response3 = client.newServerEval().xquery(query3).evalAs(String.class);
    // System.out.printlnln(response3);
    assertEquals( "java-functest", response3);
    ServerEvaluationCall evl = client.newServerEval();
    EvalResultIterator evr = evl.xquery(readDoc).eval();
    while (evr.hasNext())
    {
      EvalResult er = evr.next();
      if (er.getType().equals(Type.XML)) {
        DOMHandle dh = new DOMHandle();
        dh = er.get(dh);
        assertEquals( "<foo>test1</foo>", convertXMLDocumentToString(dh.get()));
      }
      else if (er.getType().equals(Type.JSON)) {
        JacksonHandle jh = new JacksonHandle();
        jh = er.get(jh);
        assertTrue( jh.get().has("test"));
      }
      else if (er.getType().equals(Type.TEXTNODE)) {
        assertTrue( er.getAs(String.class).equals("This is a text document."));

      } else if (er.getType().equals(Type.BINARY)) {
        FileHandle fh = new FileHandle();
        fh = er.get(fh);
        assertEquals( 2, fh.get().length());

      } else {
        System.out.println("Something went wrong");
      }
    }
    evr.close();
  }

  // This test is intended to test eval(T handle), passing input stream handle
  // with xqueries that retruns different types, formats
  @Test
  public void testXqueryReturningDifferentTypesAndFormatsWithHandle() throws KeyManagementException, NoSuchAlgorithmException, Exception {

    InputStream inputStream = new FileInputStream("src/test/java/com/marklogic/client/functionaltest/data/xqueries.txt");
    InputStreamHandle ish = new InputStreamHandle();
    ish.set(inputStream);
    EvalResultIterator evr = null;

    try {
      evr = client.newServerEval().xquery(ish).eval();
      this.validateReturnTypes(evr);
    } catch (Exception e) {
      throw e;
    } finally {
      inputStream.close();
    }
    evr.close();
  }

  // Test is intended to test different types of variable passed to xquery from
  // java and check return node types,data types
  @Test
  public void testXqueryDifferentVariableTypes() throws KeyManagementException, NoSuchAlgorithmException, Exception {

    DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    InputSource is = new InputSource();
    is.setCharacterStream(new StringReader("<foo attr=\"attribute\"><?processing instruction?><!--comment-->test1</foo>"));
    Document doc = db.parse(is);
    System.out.println(this.convertXMLDocumentToString(doc));
    try {
      String query1 = "declare namespace test=\"http://marklogic.com/test\";"
          + "declare variable $test:myString as xs:string external;"
          + "declare variable $myBool as xs:boolean external;"
          + "declare variable $myInteger as xs:integer external;"
          + "declare variable $myDecimal as xs:decimal external;"
          + "declare variable $myDouble as xs:double external;"
          + "declare variable $myFloat as xs:float external;"
          + "declare variable $myXmlNode as document-node() external;"
          + "declare variable $myNull  external;"
          + "( $test:myString, $myBool,$myInteger,$myDecimal,$myDouble,$myFloat, document{ ($myXmlNode) },"
          + "($myXmlNode)//comment(),($myXmlNode)//text(),($myXmlNode)//*,"
          + "($myXmlNode)/@attr, ($myXmlNode)//processing-instruction())";

      ServerEvaluationCall evl = client.newServerEval().xquery(query1);
      evl.addNamespace("test", "http://marklogic.com/test")
          .addVariable("test:myString", "xml")
          .addVariable("myBool", true).addVariable("myInteger", (int) 31)
          .addVariable("myDecimal", 10.5).addVariable("myDouble", 1.0471975511966)
          .addVariable("myFloat", 20).addVariableAs("myXmlNode", new DOMHandle(doc))
          .addVariableAs("myNull", (String) null);
      EvalResultIterator evr = evl.eval();
      this.validateReturnTypes(evr);
      String query2 = "declare namespace test=\"http://marklogic.com/test\";" + "declare variable $myArray as json:array external;"
          + "declare variable $myObject as json:object external;"
          // +"declare variable $myJsonNode as xs:string external;"
          // +"($myArray,$myObject,xdmp:unquote($myJsonNode)/a,xdmp:unquote($myJsonNode)/b,xdmp:unquote($myJsonNode)/c1,"
          + "($myArray,$myObject)";
      // +"xdmp:unquote($myJsonNode)/d,xdmp:unquote($myJsonNode)/f,xdmp:unquote($myJsonNode)/g )";
      System.out.println(query2);
      evl = client.newServerEval().xquery(query2);
      evl.addVariableAs("myArray", new ObjectMapper().createArrayNode().add(1).add(2).add(3))
          .addVariableAs("myObject", new ObjectMapper().createObjectNode().put("foo", "v1").putNull("testNull"))
          .addVariableAs("myXmlNode", new DOMHandle(doc)).addVariableAs("myNull", null);
      // .addVariableAs("myJsonNode", new
      // StringHandle(jsonNode).withFormat(Format.JSON));
      evr = evl.eval();
      while (evr.hasNext())
      {
        EvalResult er = evr.next();
        if (er.getType().equals(Type.JSON)) {
          JacksonHandle jh = new JacksonHandle();
          jh = er.get(jh);

          if (jh.get().isArray()) {
            // System.out.println("Type Array :"+jh.get().toString());
            assertEquals( 1, jh.get().get(0).asInt());
            assertEquals( 2, jh.get().get(1).asInt());
            assertEquals( 3, jh.get().get(2).asInt());
          }
          else if (jh.get().isObject()) {
            System.out.println("Type Object :" + jh.get().toString());
            if (jh.get().has("foo")) {
              assertNull( jh.get().get("testNull").textValue());
            } else if (jh.get().has("obj"))
            {
              assertEquals( "value", jh.get().get("obj").asText());
            } else {
              fail("getting a wrong object ");
            }

          }
          else if (jh.get().isNumber()) {
            // System.out.println("Type Number :"+jh.get().toString());
            assertEquals( 1, jh.get().asInt());
          }
          else if (jh.get().isNull()) {
            // System.out.println("Type Null :"+jh.get().toString());
            assertNull( jh.get().textValue());
          }
          else if (jh.get().isBoolean()) {
            // System.out.println("Type boolean :"+jh.get().toString());
            assertTrue( jh.get().asBoolean());
          }
          else {
            // System.out.println("Running into different types than expected");
            fail("Running into different types than expected");
          }

        }
        else if (er.getType().equals(Type.TEXTNODE)) {
          assertTrue( er.getAs(String.class).contains("s"));
          // System.out.println("type txt node :"+er.getAs(String.class));
        } else {
          System.out.println("No corresponding type found for :" + er.getType());
        }
      }
      evr.close();

    } catch (Exception e) {
      throw e;
    }
  }

	@Test
  public void testMultipleXqueryfnOnServerEval() {
    String insertQuery = "xdmp:document-insert(\"test1.xml\",<foo>test1</foo>)";
    String query1 = "fn:exists(fn:doc())";
    assertThrows(IllegalStateException.class, () ->
		client.newServerEval().xquery(query1).xquery(insertQuery).eval().next().getBoolean());
  }

  // Issue 156 exist for this, have test cases where you can pass, element node,
  // text node, binary node as an external variable
	@Test
  public void testXqueryWithExtVarAsNode() throws Exception {
    DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    InputSource is = new InputSource();
    is.setCharacterStream(new StringReader("<foo attr=\"attribute\"><?processing instruction?> <!--comment-->test1</foo>"));
    Document doc = db.parse(is);
      String query1 = "declare variable $myXmlNode as node() external;"
          + "document{ xdmp:unquote($myXmlNode) }";
      ServerEvaluationCall evl = client.newServerEval().xquery(query1);
      evl.addVariableAs("myXmlNode", new DOMHandle(doc));
      assertThrows(FailedRequestException.class, () -> evl.eval());
  }

  // Issue 156 , have test cases where you can pass, element node, text node,
  // binary node, json object, json array as an external variable
  @Test
  public void testXqueryInvokeModuleRetDiffTypes() throws Exception {

    InputStream inputStream = null;

    DatabaseClient moduleClient = newAdminModulesClient();
    try {
      inputStream = new FileInputStream("src/test/java/com/marklogic/client/functionaltest/data/xquery-modules-with-diff-variable-types.xqy");
      InputStreamHandle ish = new InputStreamHandle();
      ish.set(inputStream);
      DocumentMetadataHandle metadataHandle = new DocumentMetadataHandle();
      metadataHandle.getPermissions().add("test-eval", Capability.UPDATE, Capability.READ, Capability.EXECUTE);
      DocumentManager dm = moduleClient.newDocumentManager();
      dm.write("/data/xquery-modules-with-diff-variable-types.xqy", metadataHandle, ish);
      DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
      InputSource is = new InputSource();
      is.setCharacterStream(new StringReader("<foo attr=\"attribute\"><?processing instruction?><!--comment-->test1</foo>"));
      Document doc = db.parse(is);
      ServerEvaluationCall evl = client.newServerEval().modulePath("/data/xquery-modules-with-diff-variable-types.xqy");
      evl.addNamespace("test", "http://marklogic.com/test")
          .addVariable("test:myString", "xml")
          .addVariable("myBool", true).addVariable("myInteger", (int) 31)
          .addVariable("myDecimal", 10.5).addVariable("myDouble", 1.0471975511966)
          .addVariable("myFloat", 20).addVariableAs("myXmlNode", new DOMHandle(doc))
          .addVariableAs("myNull", (String) null);
      EvalResultIterator evr = evl.eval();
      this.validateReturnTypes(evr);
      evr.close();
    } finally {
      if (inputStream != null) {
        inputStream.close();
      }
      moduleClient.release();
    }

  }
}
