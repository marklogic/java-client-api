/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.FailedRequestException;
import com.marklogic.client.Transaction;
import com.marklogic.client.admin.ExtensionLibrariesManager;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.eval.EvalResult;
import com.marklogic.client.eval.EvalResultIterator;
import com.marklogic.client.eval.ServerEvaluationCall;
import com.marklogic.client.impl.HandleAccessor;
import com.marklogic.client.io.*;
import com.marklogic.client.query.DeleteQueryDefinition;
import com.marklogic.client.query.QueryManager;
import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.resource.appservers.ServerManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import jakarta.xml.bind.DatatypeConverter;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.*;


public class EvalTest {
  private static GregorianCalendar septFirst = new GregorianCalendar(TimeZone.getTimeZone("CET"));
  private static ExtensionLibrariesManager libMgr;
  private static DatabaseClient restAdminClient = Common.connectRestAdmin();

  @BeforeAll
  public static void beforeAll() {
    libMgr = restAdminClient.newServerConfigManager().newExtensionLibrariesManager();
    Common.connectEval();

    septFirst.set(2014, Calendar.SEPTEMBER, 1, 0, 0, 0);
    septFirst.set(Calendar.MILLISECOND, 0);

	  // In order to verify that X-Error-Accept is used to request back errors as JSON, the app server used by this test
	  // must first be modified to default to "compatible" which results in a block of HTML being sent back, which will
	  // cause an error to be returned when the client tries to process it as JSON.
	  ObjectNode payload = Common.newServerPayload().put("default-error-format", "compatible");
	  new ServerManager(Common.newManageClient()).save(payload.toString());
  }

  @AfterAll
  public static void afterAll() {
	  // Reset the app server back to the default of "json" as the error format
	  ObjectNode payload = Common.newServerPayload().put("default-error-format", "json");
	  new ServerManager(Common.newManageClient()).save(payload.toString());
  }

  @Test
  void invalidJavascript() {
	  FailedRequestException ex = assertThrows(FailedRequestException.class, () ->
		  Common.evalClient.newServerEval().javascript("console.log('This fails").evalAs(String.class));

	  String message = ex.getServerMessage();
	  assertTrue(message.contains("Invalid or unexpected token"), "The error message from the server is expected " +
		  "to contain the actual error, which in this case is due to bad syntax. In order for this to happen, the " +
		  "Java Client should send the X-Error-Accept header per the docs at " +
		  "https://docs.marklogic.com/guide/rest-dev/intro#id_34966; actual error: " + message);
  }

	@Test
	void invalidJavascriptModule() {
		FailedRequestException ex = assertThrows(FailedRequestException.class, () ->
			Common.evalClient.newServerEval().modulePath("/data/moduleDoesNotExist.sjs").eval()
		);
		String message = ex.getServerMessage();
		assertTrue(
			message.contains("Module /data/moduleDoesNotExist.sjs not found"),
			"The error message from the server is expected to contain the actual error, which in this case " +
				"is due to a missing module. In order for this to happen, the Java Client should send the " +
				"X-Error-Accept header per the docs at https://docs.marklogic.com/guide/rest-dev/intro#id_34966; " +
				"actual error: " + message);
	}

  @Test
  void invalidXQuery() {
	  FailedRequestException ex = assertThrows(FailedRequestException.class, () ->
		  Common.evalClient.newServerEval().xquery("let $var := this fails").evalAs(String.class));

	  String message = ex.getServerMessage();
	  assertTrue(message.contains("Unexpected token syntax error"), "The server error message should contain the " +
		  "actual error; actual message: " + message);
  }

  @Test
  public void evalHelloWorld() {
    // javascript hello world and response determined by implicit StringHandle which registered with String.class
    ServerEvaluationCall query = Common.evalClient.newServerEval().javascript("'hello world'");
    String response = query.evalAs(String.class);
    assertEquals( "hello world", response);

    // xquery hello world with a variable and response explicit set to StringHandle
    query = Common.evalClient.newServerEval()
      .xquery("declare variable $planet external;" +
        "'hello world from ' || $planet")
      .addVariable("planet", "Mars");
    StringHandle strResponse = query.eval(new StringHandle());
    assertEquals( "hello world from Mars", strResponse.get());
  }

  @Test
  public void evalAndInvokeJavascript() throws DatatypeConfigurationException, JsonProcessingException, IOException {
    String javascript =
      "var myString;" +
      "var myArray;" +
      "var myObject;" +
      "var myBool;" +
      "var myInteger;" +
      "var myDouble;" +
      "var myDate;" +
      "var myNull;" +
      "xdmp.arrayValues([myString, myArray, myObject, myBool, myInteger, myDouble, myDate, myNull])";
    // first run it as ad-hoc eval
    runAndTestJavascript( Common.evalClient.newServerEval().javascript(javascript) );

    // run the same code, this time as a module we'll invoke
    StringHandle javascriptModule = new StringHandle( javascript );
    javascriptModule.setFormat(Format.TEXT);

    // libMgr is connected with  admin privileges (as rest-admin user)
    libMgr.write("/ext/test/evaltest.sjs", javascriptModule);

    // now module is installed, let's invoke it
    runAndTestJavascript( Common.evalClient.newServerEval().modulePath("/ext/test/evaltest.sjs") );

    // clean up module we no longer need
    libMgr.delete("/ext/test/evaltest.sjs");
  }

  @Test
  public void evalAndInvokeXQuery()
    throws ParserConfigurationException, DatatypeConfigurationException, JsonProcessingException, IOException, SAXException
  {
    InputStreamHandle xquery = new InputStreamHandle(
      this.getClass().getClassLoader().getResourceAsStream("evaltest.xqy"));
    // first read it locally and run it as ad-hoc eval
    runAndTestXQuery( Common.evalClient.newServerEval().xquery(xquery) );

    // run the same code, this time as a module we'll invoke
    xquery = new InputStreamHandle(
      this.getClass().getClassLoader().getResourceAsStream("evaltest.xqy"));
    xquery.setFormat(Format.TEXT);

    // libMgr is connected with  admin privileges (as rest-admin user)
    libMgr.write("/ext/test/evaltest.xqy", xquery);

    // now module is installed, let's invoke it
    runAndTestXQuery( Common.evalClient.newServerEval().modulePath("/ext/test/evaltest.xqy") );

    // clean up module we no longer need
    libMgr.delete("/ext/test/evaltest.xqy");
  }

  private void runAndTestJavascript(ServerEvaluationCall call)
    throws DatatypeConfigurationException, JsonProcessingException, IOException
  {
    call = call
      // String is directly supported in any EvalBuilder
      .addVariable("myString",  "Mars")
      // ArrayNode extends JSONNode which is mapped to implicitly use JacksonHandle
      .addVariableAs("myArray",   new ObjectMapper().createArrayNode().add("item1").add("item2"))
      // ObjectNode extends JSONNode which is mapped to implicitly use JacksonHandle
      .addVariableAs("myObject",  new ObjectMapper().createObjectNode().put("item1", "value1"))
      // the rest are auto-boxed by EvalBuilder.addVariable(String, Number)
      .addVariable("myBool",    true)
      .addVariable("myInteger", 123)
      .addVariable("myDouble",  1.1)
      .addVariable("myDate",
        DatatypeFactory.newInstance().newXMLGregorianCalendar(septFirst).toString())
      // we can pass a null node
      .addVariable("myNull", (String) null);

    try (EvalResultIterator results = call.eval()) {
      assertEquals( "Mars", results.next().getAs(String.class));
      assertEquals(
        new ObjectMapper().readTree("[\"item1\",\"item2\"]"),
        results.next().getAs(JsonNode.class));
      assertEquals(
        new ObjectMapper().readTree("{\"item1\":\"value1\"}"),
        results.next().getAs(JsonNode.class));
      assertEquals( true, results.next().getBoolean());
      assertEquals( 123,
        results.next().getNumber().intValue());
      assertEquals( 1.1,
        results.next().getNumber().doubleValue(), .001);
      // the same format we sent in (from javax.xml.datatype.XMLGregorianCalendar.toString())
      assertEquals( "2014-09-01T00:00:00.000+02:00",
        results.next().getString());
      assertEquals( null, results.next().getString());
    }

  }

  @Test
  public void getNullTests() throws DatatypeConfigurationException, JsonProcessingException, IOException {
    String javascript = "var myNull; myNull";
    ServerEvaluationCall call = Common.evalClient.newServerEval().javascript(javascript)
      .addVariable("myNull", (String) null);

    try (EvalResultIterator results = call.eval()) {
       assertEquals( null, results.next().getString());
    }

    try (EvalResultIterator results = call.eval()) {
      assertEquals( null, results.next().get(new StringHandle()).get());
    }

    try (EvalResultIterator results = call.eval()) {
      assertEquals( null, results.next().get(new BytesHandle()).get());
    }

    try (EvalResultIterator results = call.eval()) {
      NullNode jsonNullNode = new ObjectMapper().createObjectNode().nullNode();
      assertEquals( jsonNullNode, results.next().get(new JacksonHandle()).get());
    }

    try (EvalResultIterator results = call.eval()) {
      ReaderHandle valueReader = results.next().get(new ReaderHandle());
      String value = HandleAccessor.contentAsString(valueReader);
      assertEquals( "null", value);
    }
  }

  private void runAndTestXQuery(ServerEvaluationCall call)
    throws JsonProcessingException, IOException, SAXException, ParserConfigurationException, DatatypeConfigurationException
  {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setNamespaceAware(true);
    factory.setValidating(false);
    Document document = factory.newDocumentBuilder()
      .parse(this.getClass().getClassLoader().getResourceAsStream("1-empty-1.0.xml"));
    call = call.addNamespace("myPrefix", "http://marklogic.com/test")
      .addVariable("myPrefix:myString",  "Mars")
      .addVariable("myArray",
        new JacksonHandle().with(new ObjectMapper().createArrayNode().add("item1").add("item2")))
      .addVariable("myObject",
        new JacksonHandle().with(new ObjectMapper().createObjectNode().put("item1", "value1")))
      .addVariable("myAnyUri",  "http://marklogic.com/a")
      .addVariable("myBinary",  DatatypeConverter.printHexBinary(",".getBytes()))
      .addVariable("myBase64Binary",  DatatypeConverter.printBase64Binary(new byte[] {1, 2, 3}))
      .addVariable("myHexBinary",  DatatypeConverter.printHexBinary(new byte[] {1, 2, 3}))
      .addVariable("myDuration", "P100D")
      .addVariable("myDocument", new DOMHandle(document))
      .addVariable("myQName", "myPrefix:a")
      //.addVariable("myAttribute", "<a a=\"a\"/>")
      .addVariable("myComment", "<!--a-->")
      .addVariable("myElement", "<a a=\"a\"/>")
      .addVariable("myProcessingInstruction", "<?a?>")
      .addVariable("myText", new StringHandle("a").withFormat(Format.TEXT))
      // the next three use built-in methods of ServerEvaluationCall
      .addVariable("myBool",    true)
      .addVariable("myInteger", 1234567890123456789l)
      .addVariable("myBigInteger", "123456789012345678901234567890")
      .addVariable("myDecimal",  "1111111111111111111.9999999999")
      .addVariable("myDouble",  11111111111111111111.7777777777)
      .addVariable("myFloat",  1.1)
      .addVariable("myGDay", "---01")
      .addVariable("myGMonth", "--01")
      .addVariable("myGMonthDay", "--01-01")
      .addVariable("myGYear", "1901")
      .addVariable("myGYearMonth", "1901-01")
      .addVariable("myDate", "2014-09-01")
      .addVariable("myDateTime",
        DatatypeFactory.newInstance().newXMLGregorianCalendar(septFirst).toString())
      .addVariable("myTime", "00:01:01");

    try (EvalResultIterator results = call.eval()) {
      EvalResult result = results.next();
      assertEquals("Mars", result.getAs(String.class));
      assertEquals( EvalResult.Type.STRING, result.getType());
      assertEquals( Format.TEXT, result.getFormat());
      result = results.next();
      assertEquals(
        new ObjectMapper().readTree("[\"item1\",\"item2\"]"),
        result.getAs(JsonNode.class));
      assertEquals( EvalResult.Type.JSON, result.getType());
      assertEquals( Format.JSON, result.getFormat());
      result = results.next();
      assertEquals(
        new ObjectMapper().readTree("{\"item1\":\"value1\"}"),
        result.getAs(JsonNode.class));
      assertEquals( EvalResult.Type.JSON, result.getType());
      assertEquals( Format.JSON, result.getFormat());
      result = results.next();
      assertEquals( "http://marklogic.com/a", result.getString());
      assertEquals( EvalResult.Type.ANYURI, result.getType());
      assertEquals( Format.TEXT, result.getFormat());
      result = results.next();
      assertEquals( ",", result.getString());
      assertEquals( EvalResult.Type.BINARY, result.getType());
      assertEquals( Format.BINARY, result.getFormat());
      result = results.next();
      assertArrayEquals(new byte[] {1, 2, 3},
        DatatypeConverter.parseBase64Binary(result.getString()));
      assertEquals( EvalResult.Type.BASE64BINARY, result.getType());
      assertEquals( Format.TEXT, result.getFormat());
      result = results.next();
      assertArrayEquals(new byte[] {1, 2, 3},
        DatatypeConverter.parseHexBinary(result.getString()));
      assertEquals( EvalResult.Type.HEXBINARY, result.getType());
      assertEquals( Format.TEXT, result.getFormat());
      result = results.next();
      assertEquals("P100D", result.getString());
      assertEquals( EvalResult.Type.DURATION, result.getType());
      assertEquals( Format.TEXT, result.getFormat());
      result = results.next();
      assertEquals( "myPrefix:a", result.getString());
      assertEquals( EvalResult.Type.QNAME, result.getType());
      assertEquals( Format.TEXT, result.getFormat());
      result = results.next();
      assertEquals(
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
          "<search:options xmlns:search=\"http://marklogic.com/appservices/search\"/>",
        result.getString());
      assertEquals( EvalResult.Type.XML, result.getType());
      assertEquals( Format.XML, result.getFormat());
      result = results.next();
      assertEquals( "a", result.getString());
      assertEquals( EvalResult.Type.ATTRIBUTE, result.getType());
      assertEquals( Format.TEXT, result.getFormat());
      result = results.next();
      assertEquals("<!--a-->", result.getString());
      assertEquals( EvalResult.Type.COMMENT, result.getType());
      assertEquals( Format.TEXT, result.getFormat());
      result = results.next();
      assertEquals( "<a a=\"a\"/>", result.getString());
      assertEquals( EvalResult.Type.XML, result.getType());
      assertEquals( Format.XML, result.getFormat());
      result = results.next();
      assertEquals("<?a?>", result.getString());
      assertEquals(
        EvalResult.Type.PROCESSINGINSTRUCTION, result.getType());
      assertEquals( Format.TEXT, result.getFormat());
      result = results.next();
      assertEquals("a", result.getString());
      assertEquals( EvalResult.Type.TEXTNODE, result.getType());
      assertEquals( Format.TEXT, result.getFormat());
      result = results.next();
      assertEquals(true, result.getBoolean());
      assertEquals( EvalResult.Type.BOOLEAN, result.getType());
      assertEquals( Format.TEXT, result.getFormat());
      result = results.next();
      assertEquals(1234567890123456789l,
        result.getNumber().longValue());
      assertEquals( EvalResult.Type.INTEGER, result.getType());
      assertEquals( Format.TEXT, result.getFormat());
      result = results.next();
      assertEquals( new BigInteger("123456789012345678901234567890"),
        new BigInteger(result.getString()));
      assertEquals( EvalResult.Type.STRING, result.getType());
      assertEquals( Format.TEXT, result.getFormat());
      result = results.next();
      assertEquals( 1111111111111111111.9,
        result.getNumber().doubleValue(), .001);
      assertEquals( EvalResult.Type.DECIMAL, result.getType());
      assertEquals( Format.TEXT, result.getFormat());
      result = results.next();
      assertEquals( 1.11111111111111E19,
        result.getNumber().doubleValue(), .001);
      assertEquals( EvalResult.Type.DOUBLE, result.getType());
      assertEquals( Format.TEXT, result.getFormat());
      result = results.next();
      assertEquals( 1.1, result.getNumber().floatValue(), .001);
      assertEquals( EvalResult.Type.FLOAT, result.getType());
      assertEquals( Format.TEXT, result.getFormat());
      result = results.next();
      assertEquals( "---01", result.getString());
      assertEquals( EvalResult.Type.GDAY, result.getType());
      assertEquals( Format.TEXT, result.getFormat());
      result = results.next();
      assertEquals( "--01", result.getString());
      assertEquals( EvalResult.Type.GMONTH, result.getType());
      assertEquals( Format.TEXT, result.getFormat());
      result = results.next();
      assertEquals( "--01-01", result.getString());
      assertEquals( EvalResult.Type.GMONTHDAY, result.getType());
      assertEquals( Format.TEXT, result.getFormat());
      result = results.next();
      assertEquals( "1901", result.getString());
      assertEquals( EvalResult.Type.GYEAR, result.getType());
      assertEquals( Format.TEXT, result.getFormat());
      result = results.next();
      assertEquals( "1901-01", result.getString());
      assertEquals( EvalResult.Type.GYEARMONTH, result.getType());
      assertEquals( Format.TEXT, result.getFormat());
      result = results.next();
      // the lexical format MarkLogic uses to serialize a date
      assertEquals("2014-09-01", result.getString());
      assertEquals( EvalResult.Type.DATE, result.getType());
      assertEquals( Format.TEXT, result.getFormat());
      result = results.next();
      // the lexical format MarkLogic uses to serialize a dateTime
      assertEquals("2014-09-01T00:00:00+02:00", result.getString());
      assertEquals( EvalResult.Type.DATETIME, result.getType());
      assertEquals( Format.TEXT, result.getFormat());
      result = results.next();
      assertEquals( "00:01:01", result.getString());
      assertEquals( EvalResult.Type.TIME, result.getType());
      assertEquals( Format.TEXT, result.getFormat());
      result = results.next();
      assertEquals( EvalResult.Type.OTHER, result.getType());
      assertEquals( Format.TEXT, result.getFormat());
      result = results.next();
      assertEquals( EvalResult.Type.OTHER, result.getType());
      assertEquals( Format.TEXT, result.getFormat());
    }
  }

  @Test
  public void test_171() throws Exception {
    DatabaseClient client = Common.newEvalClient("Documents");
    int count=1;
    boolean tstatus =true;
    String directory = "/test_eval_171/";
    Transaction t1 = client.openTransaction();
    try {
      QueryManager queryMgr = client.newQueryManager();
      DeleteQueryDefinition deleteQuery = queryMgr.newDeleteDefinition();
      deleteQuery.setDirectory(directory);
      queryMgr.delete(deleteQuery);

      XMLDocumentManager docMgr = client.newXMLDocumentManager();
      java.util.Map<String,String> map= new HashMap<>();
      DocumentWriteSet writeset =docMgr.newWriteSet();
      for (int i =0;i<2;i++) {
        String contents = "<xml>test" + i + "</xml>";
        String docId = directory + "sec"+i+".xml";
        writeset.add(docId, new StringHandle(contents).withFormat(Format.XML));
        map.put(docId, contents);
        if (count%100 == 0) {
          docMgr.write(writeset,t1);
          writeset = docMgr.newWriteSet();
        }
        count++;
      }
      if (count%100 > 0) {
        docMgr.write(writeset,t1);
      }

      String query = "(fn:count(xdmp:directory('" + directory + "')))";
      ServerEvaluationCall evl= client.newServerEval().xquery(query);
      try (EvalResultIterator evr = evl.eval()) {
        assertEquals( 0, evr.next().getNumber().intValue());
      }
      evl = client.newServerEval().xquery(query).transaction(t1);
      try (EvalResultIterator evr = evl.eval()) {
        assertEquals( 2, evr.next().getNumber().intValue());
      }
    } catch(Exception e) {
      System.out.println(e.getMessage());
      tstatus=true;
      throw e;
    } finally {
      if (tstatus) {
        t1.rollback();
      }
    }
  }

  @Test
  public void test_582_need_privilege() throws Exception{
    try {
      assertEquals("hello", restAdminClient.newServerEval()
        .xquery("'hello'").eval().next().getString());
      fail("a FailedRequestException should have been thrown since rest_admin doesn't have eval privileges");
    } catch (FailedRequestException fre) {
      assertTrue(fre.getMessage().contains("SEC-PRIV: Need privilege: http://marklogic.com/xdmp/privileges/xdbc-eval"));
    }
  }

  @Test
  public void test_issue725() throws IOException {
    String inputText = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor " +
      "incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation";
    String javascript =
      "var inputText;" +
      "var output = new String();" +
      "for ( i=0; i < 10000; i++ ) {" +
      "  output = output + inputText;" +
      "}" +
      "output;";
    ServerEvaluationCall query = Common.evalClient.newServerEval()
      .javascript(javascript)
      .addVariable("inputText", inputText);
    Reader response = query.evalAs(Reader.class);
    String strVal = new BufferedReader(response).readLine();
    StringBuilder expectedOutput = new StringBuilder();
    for ( int i=0; i < 10000; i++ ) {
      expectedOutput.append(inputText);
    }
    assertEquals(expectedOutput.toString(), strVal);
  }

	@Test
	public void test_issue874() {
		assertThrows(RuntimeException.class, () -> {
			try (EvalResultIterator iterator = new EvalResultIterator() {
				@Override
				public void close() throws RuntimeException {
					throw new RuntimeException("test close() from try-with-resources");
				}

				public java.util.Iterator<com.marklogic.client.eval.EvalResult> iterator() {
					return this;
				}

				@Override
				public boolean hasNext() {
					return false;
				}

				@Override
				public EvalResult next() {
					throw new UnsupportedOperationException();
				}
			}) {
				iterator.hasNext();
			}
		});
	}

}
