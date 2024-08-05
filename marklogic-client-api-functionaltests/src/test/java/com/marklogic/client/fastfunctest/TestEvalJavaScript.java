/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.fastfunctest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.DocumentManager;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.document.TextDocumentManager;
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

import static org.junit.jupiter.api.Assertions.*;

/*
 * This test is meant for javascript to
 * verify the eval api can handle all the formats of documents
 * verify eval api can handle all the return types
 * Verify eval takes all kind of variables
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
public class TestEvalJavaScript extends AbstractFunctionalTest {
  private static String appServerHostname = null;
  private String dbName = "java-functest";

  private DatabaseClient client;

  @BeforeAll
  public static void setUpBeforeClass() throws Exception {
    TestEvalXquery.createUserRolesWithPrevilages("test-js-eval", "xdbc:eval", "xdbc:eval-in", "xdmp:eval-in", "xdmp:invoke-in", "xdmp:invoke", "xdbc:invoke-in", "any-uri",
        "xdbc:invoke");
    TestEvalXquery.createRESTUser("eval-userJS", "x", "test-js-eval");
    appServerHostname = getRestAppServerHostName();
  }

  @AfterAll
  public static void tearDownAfterClass() throws Exception {
    TestEvalXquery.deleteRESTUser("eval-userJS");
    TestEvalXquery.deleteUserRole("test-js-eval");
  }

  @BeforeEach
  public void setUp() throws Exception {
    client = newClientAsUser("eval-userJS", "x");
  }

  /*
   * This method is validating all the return values from java script, this
   * method has more types than we test them here
   */
  void validateReturnTypes(EvalResultIterator evr) throws Exception {

    while (evr.hasNext()) {
      EvalResult er = evr.next();
      if (er.getType().equals(Type.JSON)) {

        JacksonHandle jh = new JacksonHandle();
        jh = er.get(jh);

        if (jh.get().isArray()) {
          System.out.println("Type Array :" + jh.get().toString());
          assertEquals( 1, jh.get().get(0)
              .asInt());
          assertEquals( 2, jh.get().get(1)
              .asInt());
          assertEquals( 3, jh.get().get(2)
              .asInt());
        } else if (jh.get().isObject()) {
          System.out.println("Type Object :" + jh.get().toString());
          if (jh.get().has("foo")) {
            assertNull( jh.get()
                .get("testNull").textValue());
          } else if (jh.get().has("obj")) {
            assertEquals( "value", jh
                .get().get("obj").asText());
          } else {
            fail("getting a wrong object ");
          }

        } else if (jh.get().isNumber()) {
          System.out.println("Type Number :" + jh.get().toString());
          assertEquals( 1, jh.get().asInt());
        } else if (jh.get().isNull()) {
          System.out.println("Type Null :" + jh.get().toString());
          assertNull( jh.get().textValue());
        } else if (jh.get().isBoolean()) {
          System.out.println("Type boolean :" + jh.get().toString());
          assertTrue( jh.get()
              .asBoolean());
        } else {
          // System.out.println("Running into different types than expected");
          fail("Running into different types than expected");
        }

      } else if (er.getType().equals(Type.TEXTNODE)) {
        assertTrue(
            er.getAs(String.class).equals("test1"));
        // System.out.println("type txt node :"+er.getAs(String.class));

      } else if (er.getType().equals(Type.BINARY)) {
        FileHandle fh = new FileHandle();
        fh = er.get(fh);
        // System.out.println("type binary :"+fh.get().length());
        assertEquals( 2, fh.get().length());
      } else if (er.getType().equals(Type.BOOLEAN)) {
        assertTrue(er.getBoolean());
        // System.out.println("type boolean:"+er.getBoolean());
      } else if (er.getType().equals(Type.INTEGER)) {
        System.out.println("type Integer: "
            + er.getNumber().longValue());
        assertEquals( 31, er.getNumber()
            .intValue());
      } else if (er.getType().equals(Type.STRING)) {
          String str = er.getString();
        // There is git issue 152
        System.out.println("type string: " + str );
        assertTrue(str.contains("true") || str.contains("xml")
            || str.contains("31") || str.contains("1.0471975511966"));

      } else if (er.getType().equals(Type.NULL)) {
        // There is git issue 151
        // assertNull(er.getAs(String.class));
        System.out.println("Testing is empty sequence is NUll?"
            + er.getAs(String.class));
      } else if (er.getType().equals(Type.OTHER)) {
        // There is git issue 151
        System.out.println("Testing is Others? "
            + er.getAs(String.class));

      } else if (er.getType().equals(Type.ANYURI)) {
        // System.out.println("Testing is AnyUri? "+er.getAs(String.class));
        assertEquals( "test1.xml",
            er.getAs(String.class));

      } else if (er.getType().equals(Type.DATE)) {
        // System.out.println("Testing is DATE? "+er.getAs(String.class));
        assertEquals( "2002-03-07-07:00",
            er.getAs(String.class));
      } else if (er.getType().equals(Type.DATETIME)) {
        // System.out.println("Testing is DATETIME? "+er.getAs(String.class));
        assertEquals(
            "2010-01-06T18:13:50.874-07:00", er.getAs(String.class));

      } else if (er.getType().equals(Type.DECIMAL)) {
        // System.out.println("Testing is Decimal? "+er.getAs(String.class));
        assertEquals( "1.0471975511966",
            er.getAs(String.class));

      } else if (er.getType().equals(Type.DOUBLE)) {
        // System.out.println("Testing is Double? "+er.getAs(String.class));
        assertEquals(1.0471975511966, er.getNumber().doubleValue(), 0);

      } else if (er.getType().equals(Type.DURATION)) {
        System.out.println("Testing is Duration? "
            + er.getAs(String.class));
        // assertEquals(0.4903562,er.getNumber().floatValue());
      } else if (er.getType().equals(Type.FLOAT)) {
        // System.out.println("Testing is Float? "+er.getAs(String.class));
        assertEquals(20, er.getNumber().floatValue(), 0);
      } else if (er.getType().equals(Type.GDAY)) {
        // System.out.println("Testing is GDay? "+er.getAs(String.class));
        assertEquals( "---01",
            er.getAs(String.class));
      } else if (er.getType().equals(Type.GMONTH)) {
        // System.out.println("Testing is GMonth "+er.getAs(String.class));
        assertEquals( "--01",
            er.getAs(String.class));
      } else if (er.getType().equals(Type.GMONTHDAY)) {
        // System.out.println("Testing is GMonthDay? "+er.getAs(String.class));
        assertEquals( "--12-25-14:00",
            er.getAs(String.class));
      } else if (er.getType().equals(Type.GYEAR)) {
        // System.out.println("Testing is GYear? "+er.getAs(String.class));
        assertEquals( "2005-12:00",
            er.getAs(String.class));
      } else if (er.getType().equals(Type.GYEARMONTH)) {
        // System.out.println("Testing is GYearMonth?1976-02 "+er.getAs(String.class));
        assertEquals( "1976-02",
            er.getAs(String.class));
      } else if (er.getType().equals(Type.HEXBINARY)) {
        // System.out.println("Testing is HEXBINARY? "+er.getAs(String.class));
        assertEquals( "BEEF",
            er.getAs(String.class));
      } else if (er.getType().equals(Type.QNAME)) {
        // System.out.println("Testing is QNAME integer"+er.getAs(String.class));
        assertEquals( "integer",
            er.getAs(String.class));
      } else if (er.getType().equals(Type.TIME)) {
        // System.out.println("Testing is TIME? "+er.getAs(String.class));
        assertEquals( "10:00:00",
            er.getAs(String.class));
      } else if (er.getType().equals(Type.ATTRIBUTE)) {
        // System.out.println("Testing is ATTRIBUTE? "+er.getAs(String.class));
        assertEquals( "attribute",
            er.getAs(String.class));

      } else if (er.getType().equals(Type.PROCESSINGINSTRUCTION)) {
        // System.out.println("Testing is ProcessingInstructions? "+er.getAs(String.class));
        assertEquals(
            "<?processing instruction?>", er.getAs(String.class));
      } else if (er.getType().equals(Type.COMMENT)) {
        // System.out.println("Testing is Comment node? "+er.getAs(String.class));
        assertEquals( "<!--comment-->",
            er.getAs(String.class));
      } else if (er.getType().equals(Type.BASE64BINARY)) {
        // System.out.println("Testing is Base64Binary  "+er.getAs(String.class));
        assertEquals( "DEADBEEF",
            er.getAs(String.class));
      } else {
        System.out
            .println("Got something which is not belongs to anytype we support "
                + er.getAs(String.class));
        fail("getting in else part, missing a type  ");
      }
    }
  }

  // This test intended to verify a simple JS query for inserting and reading
  // documents of different formats and returning boolean,string number types

  @Test
  public void testJSReturningDifferentTypesOrder1() throws KeyManagementException, NoSuchAlgorithmException, Exception {
	  System.out.println("Running testJSReturningDifferentTypesOrder1");
	  String insertXML = "declareUpdate();" + "var x = new NodeBuilder();"
        + "x.startDocument();" + "x.startElement(\"foo\");"
        + "x.addText(\"test1\");" + "x.endElement();"
        + "x.endDocument();"
        + "xdmp.documentInsert(\"test1.xml\",x.toNode())";
    String insertJSON = "declareUpdate();xdmp.documentInsert(\"test2.json\", {\"test\":\"hello\"})";
    String insertTXT = "declareUpdate();var txt= new NodeBuilder();txt.addText(\"This is a text document.\");xdmp.documentInsert(\"/test3.txt\",txt.toNode() )";
    String insertBinary = "declareUpdate();var binary= new NodeBuilder();binary.addBinary(\"ABCD\");xdmp.documentInsert(\"test4.bin\",binary.toNode())";
    String query1 = "fn.exists(fn.doc())";
    String query2 = "fn.count(fn.doc())";
    String query3 = "xdmp.databaseName(xdmp.database())";
    String readDoc = "fn.doc()";
    System.out.println(insertJSON);
    System.out.println(insertXML);
    System.out.println(insertTXT);
    System.out.println(insertBinary);
    boolean response = client.newServerEval().javascript(insertXML).eval()
        .hasNext();

    assertFalse( response);
    response = client.newServerEval().javascript(insertJSON).eval()
        .hasNext();

    assertFalse( response);
    response = client.newServerEval().javascript(insertTXT).eval()
        .hasNext();

    assertFalse( response);
    response = client.newServerEval().javascript(insertBinary).eval()
        .hasNext();

    assertFalse( response);
    boolean response1 = client.newServerEval().javascript(query1).eval()
        .next().getBoolean();

    assertTrue(response1);
    int response2 = client.newServerEval().javascript(query2).eval().next()
        .getNumber().intValue();

    assertEquals( 4, response2);
    String response3 = client.newServerEval().javascript(query3)
        .evalAs(String.class);

    assertEquals( dbName, response3);
    ServerEvaluationCall evl = client.newServerEval();
    EvalResultIterator evr = evl.javascript(readDoc).eval();
    while (evr.hasNext()) {
      EvalResult er = evr.next();
      if (er.getType().equals(Type.XML)) {
        DOMHandle dh = new DOMHandle();
        dh = er.get(dh);
        assertEquals( "<foo>test1</foo>",
            convertXMLDocumentToString(dh.get()));
      } else if (er.getType().equals(Type.JSON)) {
        JacksonHandle jh = new JacksonHandle();
        jh = er.get(jh);
        assertTrue(jh.get().has("test"));
      } else if (er.getType().equals(Type.TEXTNODE)) {
        assertTrue(
            er.getAs(String.class).equals(
                "This is a text document."));

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
  // with javascript that retruns different types, formats
  @Test
  public void testJSReturningDifferentTypesOrder2() throws KeyManagementException, NoSuchAlgorithmException, Exception {
	System.out.println("Running testJSReturningDifferentTypesOrder2");
    InputStream inputStream = new FileInputStream(
        "src/test/java/com/marklogic/client/functionaltest/data/javascriptQueries.sjs");
    InputStreamHandle ish = new InputStreamHandle();
    ish.set(inputStream);

    try {
      EvalResultIterator evr = client.newServerEval().javascript(ish)
          .eval();
      while (evr.hasNext()) {
        EvalResult er = evr.next();
        if (er.getType().equals(Type.XML)) {
          DOMHandle dh = new DOMHandle();
          dh = er.get(dh);
          assertEquals( "<foo>test1</foo>",
              convertXMLDocumentToString(dh.get()));
        } else if (er.getType().equals(Type.JSON)) {
          JacksonHandle jh = new JacksonHandle();
          jh = er.get(jh);
          assertTrue(jh.get().has("test"));
        } else if (er.getType().equals(Type.TEXTNODE)) {
          assertTrue( er.getAs(String.class)
              .equals("This is a text document."));

        } else if (er.getType().equals(Type.BINARY)) {
          FileHandle fh = new FileHandle();
          fh = er.get(fh);
          assertEquals( 2, fh.get().length());

        } else {
          System.out.println("Something went wrong");
        }
      }
      evr.close();
    } catch (Exception e) {
      throw e;
    } finally {
      inputStream.close();
    }
  }

  /*
   * Test is intended to test different types of variable passed to Javascript
   * from java and check return types,data types, there is a bug log against
   * REST 30209
   *
   * Expected Result : JS variable myJsonNull in this test has JsonNode object
   * of Type NullNode and its value not null. Hence we expect an Exception from
   * JerseyServices. See Git issue # 317 for further explanation.
   */
	@Test
  public void testJSDifferentVariableTypes() throws Exception {
	System.out.println("Running testJSDifferentVariableTypes");
    DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    InputSource is = new InputSource();
    is.setCharacterStream(new StringReader("<foo attr=\"attribute\"><?processing instruction?><!--comment-->test1</foo>"));
    Document doc = db.parse(is);
    System.out.println(this.convertXMLDocumentToString(doc));
      String query1 = " var results = [];"
          + "var myString;"
          + "var myBool ;"
          + "var myInteger;"
          + "var myDecimal;"
          + "var myJsonObject;"
          + "var myNull;"
          + "var myJsonArray;"
          + "var myJsonNull;"
          + "results.push(myString,myBool,myInteger,myDecimal,myJsonObject,myJsonArray,myNull,myJsonNull);"
          + "xdmp.arrayValues(results)";

      ServerEvaluationCall evl = client.newServerEval().javascript(query1);
      evl.addVariable("myString", "xml")
          .addVariable("myBool", true)
          .addVariable("myInteger", (int) 31)
          .addVariable("myDecimal", (double) 1.0471975511966)
          .addVariableAs("myJsonObject", new ObjectMapper().createObjectNode().put("foo", "v1").putNull("testNull"))
          .addVariableAs("myNull", (String) null)
          .addVariableAs("myJsonArray", new ObjectMapper().createArrayNode().add(1).add(2).add(3))
          .addVariableAs("myJsonNull", new ObjectMapper().createObjectNode().nullNode());
      System.out.println(new ObjectMapper().createObjectNode().nullNode().toString());
      assertThrows(IllegalArgumentException.class, () -> evl.eval());
  }

  /*
   * Test is intended to test different types of variable passed to javascript
   * from java and check return types,data types, there is a bug log against
   * REST 30209
   *
   * Expected Result : JS variable myJsonNull in this test has value set to
   * null. See Git issue # 317 for further explanation.
   */
  @Test
  public void testJSDifferentVariableTypesWithNulls() throws KeyManagementException, NoSuchAlgorithmException, Exception {
	System.out.println("Running testJSDifferentVariableTypesWithNulls");
    DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    InputSource is = new InputSource();
    is.setCharacterStream(new StringReader("<foo attr=\"attribute\"><?processing instruction?><!--comment-->test1</foo>"));
    Document doc = db.parse(is);
    System.out.println(this.convertXMLDocumentToString(doc));
    try {
      String query1 = " var results = [];"
          + "var myString;"
          + "var myBool ;"
          + "var myInteger;"
          + "var myDecimal;"
          + "var myJsonObject;"
          + "var myNull;"
          + "var myJsonArray;"
          + "var myJsonNull;"
          + "results.push(myString,myBool,myInteger,myDecimal,myJsonObject,myJsonArray,myNull,myJsonNull);"
          + "xdmp.arrayValues(results)";

      ServerEvaluationCall evl = client.newServerEval().javascript(query1);
      evl.addVariable("myString", "xml")
          .addVariable("myBool", true)
          .addVariable("myInteger", (int) 31)
          .addVariable("myDecimal", (double) 1.0471975511966)
          .addVariableAs("myJsonObject", new ObjectMapper().createObjectNode().put("foo", "v1").putNull("testNull"))
          .addVariableAs("myNull", (String) null)
          .addVariableAs("myJsonArray", new ObjectMapper().createArrayNode().add(1).add(2).add(3))
          .addVariableAs("myJsonNull", null);
      System.out.println(new ObjectMapper().createObjectNode().nullNode().toString());
      EvalResultIterator evr = evl.eval();
      this.validateReturnTypes(evr);
      evr.close();
    } catch (Exception e) {
      throw e;
    }
  }

  /*
   * Test is intended to test different types of variable passed to javascript
   * from java and check return types,data types, there is a bug log against
   * REST 30209 This test does not have NullNode set in a variable
   */
  @Test
  public void testJSDifferentVariableTypesNoNullNodes() throws KeyManagementException, NoSuchAlgorithmException, Exception {

	System.out.println("Running testJSDifferentVariableTypesNoNullNodes");
    DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    InputSource is = new InputSource();
    is.setCharacterStream(new StringReader("<foo attr=\"attribute\"><?processing instruction?><!--comment-->test1</foo>"));
    Document doc = db.parse(is);
    System.out.println(this.convertXMLDocumentToString(doc));
    try {
      String query1 = " var results = [];"
          + "var myString;"
          + "var myBool ;"
          + "var myInteger;"
          + "var myDecimal;"
          + "var myJsonObject;"
          + "var myNull;"
          + "var myJsonArray;"
          + "results.push(myString,myBool,myInteger,myDecimal,myJsonObject,myJsonArray,myNull);"
          + "xdmp.arrayValues(results)";

      ServerEvaluationCall evl = client.newServerEval().javascript(query1);
      evl.addVariable("myString", "xml")
          .addVariable("myBool", true)
          .addVariable("myInteger", (int) 31)
          .addVariable("myDecimal", (double) 1.0471975511966)
          .addVariableAs("myJsonObject", new ObjectMapper().createObjectNode().put("foo", "v1").putNull("testNull"))
          .addVariableAs("myNull", (String) null)
          .addVariableAs("myJsonArray", new ObjectMapper().createArrayNode().add(1).add(2).add(3));
      System.out.println(new ObjectMapper().createObjectNode().nullNode().toString());
      EvalResultIterator evr = evl.eval();
      this.validateReturnTypes(evr);
      evr.close();

    } catch (Exception e) {
      throw e;
    }
  }

  @Test
  public void testMultipleJSfnOnServerEval() {
	System.out.println("Running testMultipleJSfnOnServerEval");
    String insertQuery = "xdmp.document-insert(\"test1.xml\",<foo>test1</foo>)";
    String query1 = "fn.exists(fn:doc())";
    assertThrows(IllegalStateException.class, () -> client.newServerEval().javascript(query1)
        .javascript(insertQuery).eval().next().getBoolean());
  }

  // Issue 30209 ,external variable passing is not working so I have test cases
  // where it test to see we can invoke a module
  @Test
  public void testJSReturningDifferentTypesOrder3fromModules() throws Exception {
	System.out.println("Running testJSReturningDifferentTypesOrder3fromModules");
    InputStream inputStream = null;
    DatabaseClient moduleClient = newAdminModulesClient();
    try {
      inputStream = new FileInputStream(
          "src/test/java/com/marklogic/client/functionaltest/data/javascriptQueries.sjs");
      InputStreamHandle ish = new InputStreamHandle();
      ish.set(inputStream);
      DocumentMetadataHandle metadataHandle = new DocumentMetadataHandle();
      metadataHandle.getPermissions().add("test-js-eval",
          Capability.UPDATE, Capability.READ, Capability.EXECUTE);
      DocumentManager dm = moduleClient.newDocumentManager();
      dm.write("/data/javascriptQueries.sjs", metadataHandle, ish);
      /*DocumentBuilder db = DocumentBuilderFactory.newInstance()
          .newDocumentBuilder();*/
      InputSource is = new InputSource();
      is.setCharacterStream(new StringReader(
          "<foo attr=\"attribute\"><?processing instruction?><!--comment-->test1</foo>"));

      ServerEvaluationCall evl = client.newServerEval().modulePath(
          "/data/javascriptQueries.sjs");

      EvalResultIterator evr = evl.eval();
      while (evr.hasNext()) {
        EvalResult er = evr.next();
        if (er.getType().equals(Type.XML)) {
          DOMHandle dh = new DOMHandle();
          dh = er.get(dh);
          assertEquals( "<foo>test1</foo>",
              convertXMLDocumentToString(dh.get()));
        } else if (er.getType().equals(Type.JSON)) {
          JacksonHandle jh = new JacksonHandle();
          jh = er.get(jh);
          assertTrue(jh.get().has("test"));
        } else if (er.getType().equals(Type.TEXTNODE)) {
          assertTrue( er.getAs(String.class)
              .equals("This is a text document."));

        } else if (er.getType().equals(Type.BINARY)) {
          FileHandle fh = new FileHandle();
          fh = er.get(fh);
          assertEquals( 2, fh.get().length());

        } else {
          System.out.println("Something went wrong");
        }
      }
      evr.close();
    } catch (Exception e) {
      throw e;
    } finally {
      if (inputStream != null) {
        inputStream.close();
      }
      moduleClient.release();
    }
  }

  /*
   * Test is intended to test ServerEvaluationCall.evalAs(Class<T>) closing underlying streams prematurely
   * Git Issue 725
   */
  @Test
  public void testStreamClosingWithEvalAs() throws KeyManagementException, NoSuchAlgorithmException, Exception {
	  System.out.println("Running testStreamClosingWithEvalAs");
	  try {
      String query1 =
           "var phrase = 'MarkLogic Corp';"
          + "var repeat = 10;"
          + "for (var i = 0; i < repeat; i++) {"
          + "phrase += phrase.concat(phrase);"
          + "}"
          + "phrase;";

      String query2 =
              "var phrase2 = 'Java Client API';"
             + "var repeat = 10;"
             + "for (var i = 0; i < repeat; i++) {"
             + "phrase2 += phrase2.concat(phrase2);"
             + "}"
             + "var jsStr = JSON.stringify(phrase2);"
             + "jsStr;";

      ServerEvaluationCall evl = client.newServerEval().javascript(query1);

      // Using evalAs(String.class)
      String evr = evl.evalAs(String.class);

      int nLen = evr.length();
      String firstFiftyChars = evr.substring(0, 50);
      String lastTenChars = evr.substring(evr.length() - 10);

      // Make sure we can access results that are greater than 17,400 chars. See Git Issue 725.
      System.out.println("String length is " + nLen);
      System.out.println("First Fifty String output is " + firstFiftyChars);
      System.out.println("Last Ten String output is " + lastTenChars);

      assertTrue( nLen==826686);
      assertTrue( firstFiftyChars.equalsIgnoreCase("MarkLogic CorpMarkLogic CorpMarkLogic CorpMarkLogi"));
      assertTrue( lastTenChars.equalsIgnoreCase("Logic Corp"));

      // Using evalAs(JsonNode.class)
      ServerEvaluationCall ev2 = client.newServerEval().javascript(query2);
      JsonNode jNode = ev2.evalAs(JsonNode.class);

      String jsonStr = jNode.asText();
      int nJLen = jsonStr.length();
      String firstFiftyCharsJson = jsonStr.substring(0, 50);
      String lastTenCharsJson = jsonStr.substring(jsonStr.length() - 10);

      // Make sure we can access results that are greater than 17,400 chars. See Git Issue 725.
      System.out.println("JSON String length is " + nJLen);
      System.out.println("First Fifty String JSON output is " + firstFiftyCharsJson);
      System.out.println("Last Ten String JSON output is " + lastTenCharsJson);

      assertTrue( nJLen==885735);
      assertTrue( firstFiftyCharsJson.equalsIgnoreCase("Java Client APIJava Client APIJava Client APIJava "));
      assertTrue( lastTenCharsJson.equalsIgnoreCase("Client API"));

    } catch (Exception e) {
      throw e;
    }
  }

  // Making sure that mjs modules can be used in eval.
  @Test
  public void testJavaScriptModules() {
	  System.out.println("Running testJavaScriptModules");
	  DatabaseClient moduleClient = null;
	  try {
		  String docId[] = { "/test/words/wd1.json", "/test/words/wd2.json", "/test/words/wd3.json",
				  "/test/words/wd4.xml", "/test/words/wd5.xml"};
		  moduleClient = newAdminModulesClient();
		  String mjsString = "import sr from '/MarkLogic/jsearch';" +
	              "var output =" +
	              " sr.documents()" +
	              "  .where(cts.directoryQuery('/test/words/'))" +
	              "  .orderBy('city')" +
	              "  .filter()" +
	              "  .slice(0, 10)" +
	              "  .result();" +
	              "output;";

		  DocumentManager dm = moduleClient.newDocumentManager();
		  DocumentMetadataHandle metadataHandle = new DocumentMetadataHandle();
		  metadataHandle.getPermissions().add("test-js-eval",
				  Capability.UPDATE, Capability.READ, Capability.EXECUTE);
		  dm.write("/mjs/JSearch.mjs", metadataHandle, new StringHandle(mjsString));

		  TextDocumentManager docMgr = client.newTextDocumentManager();
		  DocumentWriteSet writeset = docMgr.newWriteSet();

		  StringBuilder sb1 = new StringBuilder(
				  "{" +
						  "    \"city\": \"london\"," +
						  "    \"distance\": 50.4," +
						  "    \"srchDate\": \"2007-01-01\"," +
						  "    \"metro\": true," +
						  "    \"description\": \"Two recent discoveries indicate probable very early settlements near the Thames\"," +
						  "    \"mayor\": {" +
						  "       \"firstname\": \"John\"," +
						  "       \"middlename\": \"Mark\"," +
						  "       \"lastname\": \"Curry\"" +
						  "     }," +
						  "    \"location\": {" +
						  "      \"latLonPoint\": \"51.50, -0.12\"," +
						  "      \"latLonPair\": {" +
						  "        \"lat\": 51.50," +
						  "        \"long\": -0.12" +
						  "      }," +
						  "      \"latLonParent\": {" +
						  "        \"latLonChild\": \"51.50, -0.12\"" +
						  "      }" +
						  "    }" +
				  "  }");

		  StringBuilder sb2 = new StringBuilder(
				  "{ " +
						  "    \"city\": \"new york\"," +
						  "    \"distance\": 23.3," +
						  "    \"srchDate\": \"2006-06-23\"," +
						  "    \"metro\": true," +
						  "    \"description\": \"Henry Hudsons 1609 voyage marked the beginning of European involvement with the area\"," +
						  "    \"mayor\": {" +
						  "       \"firstname\": \"Phil\"," +
						  "       \"middlename\": \"Tim\"," +
						  "       \"lastname\": \"Dolan\"" +
						  "     }," +
						  "    \"location\": {" +
						  "      \"latLonPoint\": \"40.71, -74.01\"," +
						  "      \"latLonPair\": {" +
						  "        \"lat\": 40.71," +
						  "        \"long\": -74.01" +
						  "      }," +
						  "      \"latLonParent\": {" +
						  "        \"latLonChild\": \"40.71, -74.01\"" +
						  "      }" +
						  "    }" +
						  "  }"
				  );

		  StringBuilder sb3 = new StringBuilder(
				  "  {" +
						  "    \"city\": \"new jersey\"," +
						  "    \"distance\": 12.9," +
						  "    \"srchDate\": \"1971-12-23\"," +
						  "    \"metro\": false," +
						  "    \"description\": \"American forces under Washington met the forces under General Henry Clinton\"," +
						  "    \"mayor\": {" +
						  "       \"firstname\": \"Jason\"," +
						  "       \"middlename\": \"Gold\"," +
						  "       \"lastname\": \"Kidd\"" +
						  "     }," +
						  "    \"location\": {" +
						  "      \"latLonPoint\": \"40.72, -74.07\"," +
						  "      \"latLonPair\": {" +
						  "        \"lat\": 40.72," +
						  "        \"long\": -74.07" +
						  "      }," +
						  "      \"latLonParent\": {" +
						  "        \"latLonChild\": \"40.72, -74.07\"" +
						  "      }" +
						  "    }" +
						  "  }"
				  );
		  StringBuilder sb4 = new StringBuilder(
				  "<doc>" +
	              "    <city>beijing</city>" +
	              "    <distance direction=\"east\">134.5</distance>" +
	              "    <srchDate>1981-11-09</srchDate>" +
	              "    <metro rate=\"3\">true</metro>" +
	              "    <description>The Miyun Reservoir, on the upper reaches of the Chaobai River, is the largest reservoir within the municipality</description>" +
	              "    <mayor>" +
	              "      <firstname>Xu</firstname>" +
	              "      <middlename>Ying</middlename>" +
	              "      <lastname>Ma</lastname>" +
	              "    </mayor>" +
	              "    <location>" +
	              "      <latLonPoint>39.90,116.40</latLonPoint>" +
	              "      <latLonParent>" +
	              "        <latLonChild>39.90,116.40</latLonChild>" +
	              "      </latLonParent>" +
	              "      <latLonPair>" +
	              "        <lat>39.90</lat>" +
	              "        <long>116.40</long>" +
	              "      </latLonPair>" +
	              "      <latLonAttrPair lat=\"39.90\" long=\"116.40\"/>" +
	              "    </location>" +
	              "  </doc>"
				  );
		  StringBuilder sb5 = new StringBuilder(
				  "<doc>" +
	              "    <city>cape town</city>" +
	              "    <distance direction=\"south\">377.9</distance>" +
	              "    <srchDate>1999-04-22</srchDate>" +
	              "    <metro rate=\"2\">true</metro>" +
	              "    <description>The earliest known remnants in the region were found at Peers cave in Fish Hoek</description>" +
	              "    <mayor>" +
	              "      <firstname>Marco</firstname>" +
	              "      <middlename>Xu</middlename>" +
	              "      <lastname>Andrade</lastname>" +
	              "    </mayor>" +
	              "    <location>" +
	              "      <latLonPoint>-33.91,18.42</latLonPoint>" +
	              "      <latLonParent>" +
	              "        <latLonChild>-33.91,18.42</latLonChild>" +
	              "      </latLonParent>" +
	              "      <latLonPair>" +
	              "        <lat>-33.91</lat>" +
	              "        <long>18.42</long>" +
	              "      </latLonPair>" +
	              "      <latLonAttrPair lat=\"-33.91\" long=\"18.42\"/>" +
	              "    </location>" +
	              "  </doc>"
				  );

		  writeset.add(docId[0], new StringHandle().with(sb1.toString()));
		  writeset.add(docId[1], new StringHandle().with(sb2.toString()));
		  writeset.add(docId[2], new StringHandle().with(sb3.toString()));
		  writeset.add(docId[3], new StringHandle().with(sb4.toString()));
		  writeset.add(docId[4], new StringHandle().with(sb5.toString()));
		  docMgr.write(writeset);

		  ServerEvaluationCall evl = client.newServerEval().modulePath("/mjs/JSearch.mjs");

		  // Using evalAs(JsonNode.class)
		  JsonNode jNode = evl.evalAs(JsonNode.class);

		  assertTrue( jNode.get("results").get(0).get("uri").asText()
				                              .equalsIgnoreCase("/test/words/wd4.xml"));
		  assertTrue( jNode.get("results").get(1).get("uri").asText()
                  .equalsIgnoreCase("/test/words/wd5.xml"));
		  assertTrue( jNode.get("results").get(2).get("uri").asText()
                  .equalsIgnoreCase("/test/words/wd1.json"));
		  assertTrue( jNode.get("results").get(3).get("uri").asText()
                  .equalsIgnoreCase("/test/words/wd3.json"));
		  assertTrue( jNode.get("results").get(4).get("uri").asText()
                  .equalsIgnoreCase("/test/words/wd2.json"));

	  } catch (Exception e) {
		  throw e;
	  }
	  finally {
		  if (moduleClient != null)
		  moduleClient.release();
	  }
  }
}
