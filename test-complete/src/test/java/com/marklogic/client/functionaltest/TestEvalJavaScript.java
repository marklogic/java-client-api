/*
 * Copyright 2014-2015 MarkLogic Corporation
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.document.DocumentManager;
import com.marklogic.client.eval.EvalResult;
import com.marklogic.client.eval.EvalResult.Type;
import com.marklogic.client.eval.EvalResultIterator;
import com.marklogic.client.eval.ServerEvaluationCall;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.DocumentMetadataHandle.Capability;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.client.io.JacksonHandle;

/*
 * This test is meant for javascript to 
 * verify the eval api can handle all the formats of documents
 * verify eval api can handle all the return types
 * Verify eval takes all kind of variables
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestEvalJavaScript  extends BasicJavaClientREST {
	private static String dbName = "TestEvalJavaScriptDB";
	private static String [] fNames = {"TestEvalJavaScriptDB-1"};
	private static String restServerName = "REST-Java-Client-API-Server";
	private static int restPort = 8011;
	private  DatabaseClient client ;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		 System.out.println("In setup");
 	     setupJavaRESTServer(dbName, fNames[0], restServerName,restPort,false);
 	     TestEvalXquery.createUserRolesWithPrevilages("test-js-eval", "xdbc:eval", "xdbc:eval-in","xdmp:eval-in","xdmp:invoke-in","xdmp:invoke","xdbc:invoke-in","any-uri","xdbc:invoke");
 	     TestEvalXquery.createRESTUser("eval-user", "x", "test-js-eval");
		 System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.wire", "debug");
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		System.out.println("In tear down" );
		tearDownJavaRESTServer(dbName, fNames, restServerName);
		TestEvalXquery.deleteRESTUser("eval-user");
		TestEvalXquery.deleteUserRole("test-js-eval");
	}

	@Before
	public void setUp() throws Exception {
		client = DatabaseClientFactory.newClient("localhost", restPort,dbName,"eval-user", "x", Authentication.DIGEST);
	}

	@After
	public void tearDown() throws Exception {
		System.out.println("Running CleanUp script");	
    	// release client
    	client.release();
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
					assertEquals("array value at index 0 ", 1, jh.get().get(0)
							.asInt());
					assertEquals("array value at index 1 ", 2, jh.get().get(1)
							.asInt());
					assertEquals("array value at index 2 ", 3, jh.get().get(2)
							.asInt());
				} else if (jh.get().isObject()) {
					System.out.println("Type Object :" + jh.get().toString());
					if (jh.get().has("foo")) {
						assertNull("this object also has null node", jh.get()
								.get("testNull").textValue());
					} else if (jh.get().has("obj")) {
						assertEquals("Value of the object is ", "value", jh
								.get().get("obj").asText());
					} else {
						assertFalse("getting a wrong object ", true);
					}

				} else if (jh.get().isNumber()) {
					System.out.println("Type Number :" + jh.get().toString());
					assertEquals("Number value", 1, jh.get().asInt());
				} else if (jh.get().isNull()) {
					System.out.println("Type Null :" + jh.get().toString());
					assertNull("Returned Null", jh.get().textValue());
				} else if (jh.get().isBoolean()) {
					System.out.println("Type boolean :" + jh.get().toString());
					assertTrue("Boolean value returned false", jh.get()
							.asBoolean());
				} else {
					// System.out.println("Running into different types than expected");
					assertFalse("Running into different types than expected",
							true);
				}

			} else if (er.getType().equals(Type.TEXTNODE)) {
				assertTrue("document contains",
						er.getAs(String.class).equals("test1"));
				// System.out.println("type txt node :"+er.getAs(String.class));

			} else if (er.getType().equals(Type.BINARY)) {
				FileHandle fh = new FileHandle();
				fh = er.get(fh);
				// System.out.println("type binary :"+fh.get().length());
				assertEquals("files size", 2, fh.get().length());
			} else if (er.getType().equals(Type.BOOLEAN)) {
				assertTrue("Documents exist?", er.getBoolean());
				// System.out.println("type boolean:"+er.getBoolean());
			} else if (er.getType().equals(Type.INTEGER)) {
				System.out.println("type Integer: "
						+ er.getNumber().longValue());
				assertEquals("count of documents ", 31, er.getNumber()
						.intValue());
			} else if (er.getType().equals(Type.STRING)) {
				// There is git issue 152
				System.out.println("type string: " + er.getString());
				assertTrue("String?",er.getString().contains("true")||er.getString().contains("xml")
						||er.getString().contains("31") ||er.getString().contains("1.0471975511966"));

			} else if (er.getType().equals(Type.NULL)) {
				// There is git issue 151
				// assertNull(er.getAs(String.class));
				System.out.println("Testing is empty sequence is NUll?"
						+ er.getAs(String.class));
			} else if (er.getType().equals(Type.OTHER)) {
				// There is git issue 151
				System.out.println("Testing is Others? "
						+ er.getAs(String.class));
				// assertEquals("Returns OTHERs","xdmp:forest-restart#1",er.getString());

			} else if (er.getType().equals(Type.ANYURI)) {
				// System.out.println("Testing is AnyUri? "+er.getAs(String.class));
				assertEquals("Returns me a uri :", "test1.xml",
						er.getAs(String.class));

			} else if (er.getType().equals(Type.DATE)) {
				// System.out.println("Testing is DATE? "+er.getAs(String.class));
				assertEquals("Returns me a date :", "2002-03-07-07:00",
						er.getAs(String.class));
			} else if (er.getType().equals(Type.DATETIME)) {
				// System.out.println("Testing is DATETIME? "+er.getAs(String.class));
				assertEquals("Returns me a dateTime :",
						"2010-01-06T18:13:50.874-07:00", er.getAs(String.class));

			} else if (er.getType().equals(Type.DECIMAL)) {
				// System.out.println("Testing is Decimal? "+er.getAs(String.class));
				assertEquals("Returns me a Decimal :", "1.0471975511966",
						er.getAs(String.class));

			} else if (er.getType().equals(Type.DOUBLE)) {
				// System.out.println("Testing is Double? "+er.getAs(String.class));
				assertEquals(1.0471975511966, er.getNumber().doubleValue(), 0);

			} else if (er.getType().equals(Type.DURATION)) {
				System.out.println("Testing is Duration? "
						+ er.getAs(String.class));
				// assertEquals("Returns me a Duration :",0.4903562,er.getNumber().floatValue());
			} else if (er.getType().equals(Type.FLOAT)) {
				// System.out.println("Testing is Float? "+er.getAs(String.class));
				assertEquals(20, er.getNumber().floatValue(), 0);
			} else if (er.getType().equals(Type.GDAY)) {
				// System.out.println("Testing is GDay? "+er.getAs(String.class));
				assertEquals("Returns me a GDAY :", "---01",
						er.getAs(String.class));
			} else if (er.getType().equals(Type.GMONTH)) {
				// System.out.println("Testing is GMonth "+er.getAs(String.class));
				assertEquals("Returns me a GMONTH :", "--01",
						er.getAs(String.class));
			} else if (er.getType().equals(Type.GMONTHDAY)) {
				// System.out.println("Testing is GMonthDay? "+er.getAs(String.class));
				assertEquals("Returns me a GMONTHDAY :", "--12-25-14:00",
						er.getAs(String.class));
			} else if (er.getType().equals(Type.GYEAR)) {
				// System.out.println("Testing is GYear? "+er.getAs(String.class));
				assertEquals("Returns me a GYEAR :", "2005-12:00",
						er.getAs(String.class));
			} else if (er.getType().equals(Type.GYEARMONTH)) {
				// System.out.println("Testing is GYearMonth?1976-02 "+er.getAs(String.class));
				assertEquals("Returns me a GYEARMONTH :", "1976-02",
						er.getAs(String.class));
			} else if (er.getType().equals(Type.HEXBINARY)) {
				// System.out.println("Testing is HEXBINARY? "+er.getAs(String.class));
				assertEquals("Returns me a HEXBINARY :", "BEEF",
						er.getAs(String.class));
			} else if (er.getType().equals(Type.QNAME)) {
				// System.out.println("Testing is QNAME integer"+er.getAs(String.class));
				assertEquals("Returns me a QNAME :", "integer",
						er.getAs(String.class));
			} else if (er.getType().equals(Type.TIME)) {
				// System.out.println("Testing is TIME? "+er.getAs(String.class));
				assertEquals("Returns me a TIME :", "10:00:00",
						er.getAs(String.class));
			} else if (er.getType().equals(Type.ATTRIBUTE)) {
				// System.out.println("Testing is ATTRIBUTE? "+er.getAs(String.class));
				assertEquals("Returns me a ATTRIBUTE :", "attribute",
						er.getAs(String.class));

			} else if (er.getType().equals(Type.PROCESSINGINSTRUCTION)) {
				// System.out.println("Testing is ProcessingInstructions? "+er.getAs(String.class));
				assertEquals("Returns me a PROCESSINGINSTRUCTION :",
						"<?processing instruction?>", er.getAs(String.class));
			} else if (er.getType().equals(Type.COMMENT)) {
				// System.out.println("Testing is Comment node? "+er.getAs(String.class));
				assertEquals("Returns me a COMMENT :", "<!--comment-->",
						er.getAs(String.class));
			} else if (er.getType().equals(Type.BASE64BINARY)) {
				// System.out.println("Testing is Base64Binary  "+er.getAs(String.class));
				assertEquals("Returns me a BASE64BINARY :", "DEADBEEF",
						er.getAs(String.class));
			} else {
				System.out
						.println("Got something which is not belongs to anytype we support "
								+ er.getAs(String.class));
				assertFalse("getting in else part, missing a type  ", true);
			}
		}
	}
	
	//This test intended to verify a simple JS query for inserting and reading  documents of different formats and returning boolean,string number types
	
	@Test
	public void testJSReturningDifferentTypesOrder1() throws Exception {
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

		assertFalse("Insert query return empty sequence", response);
		response = client.newServerEval().javascript(insertJSON).eval()
				.hasNext();

		assertFalse("Insert query return empty sequence", response);
		response = client.newServerEval().javascript(insertTXT).eval()
				.hasNext();

		assertFalse("Insert query return empty sequence", response);
		response = client.newServerEval().javascript(insertBinary).eval()
				.hasNext();

		assertFalse("Insert query return empty sequence", response);
		boolean response1 = client.newServerEval().javascript(query1).eval()
				.next().getBoolean();

		assertTrue("Documents exist?", response1);
		int response2 = client.newServerEval().javascript(query2).eval().next()
				.getNumber().intValue();

		assertEquals("count of documents ", 4, response2);
		String response3 = client.newServerEval().javascript(query3)
				.evalAs(String.class);

		assertEquals("Content database?", dbName, response3);
		ServerEvaluationCall evl = client.newServerEval();
		EvalResultIterator evr = evl.javascript(readDoc).eval();
		while (evr.hasNext()) {
			EvalResult er = evr.next();
			if (er.getType().equals(Type.XML)) {
				DOMHandle dh = new DOMHandle();
				dh = er.get(dh);
				assertEquals("document has content", "<foo>test1</foo>",
						convertXMLDocumentToString(dh.get()));
			} else if (er.getType().equals(Type.JSON)) {
				JacksonHandle jh = new JacksonHandle();
				jh = er.get(jh);
				assertTrue("document has object?", jh.get().has("test"));
			} else if (er.getType().equals(Type.TEXTNODE)) {
				assertTrue(
						"document contains",
						er.getAs(String.class).equals(
								"This is a text document."));

			} else if (er.getType().equals(Type.BINARY)) {
				FileHandle fh = new FileHandle();
				fh = er.get(fh);
				assertEquals("files size", 2, fh.get().length());

			} else {
				System.out.println("Something went wrong");
			}
		}
	}
	
	//This test is intended to test eval(T handle), passing input stream handle with javascript that retruns different types, formats
	@Test
	public void testJSReturningDifferentTypesOrder2() throws Exception {

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
					assertEquals("document has content", "<foo>test1</foo>",
							convertXMLDocumentToString(dh.get()));
				} else if (er.getType().equals(Type.JSON)) {
					JacksonHandle jh = new JacksonHandle();
					jh = er.get(jh);
					assertTrue("document has object?", jh.get().has("test"));
				} else if (er.getType().equals(Type.TEXTNODE)) {
					assertTrue("document contains", er.getAs(String.class)
							.equals("This is a text document."));

				} else if (er.getType().equals(Type.BINARY)) {
					FileHandle fh = new FileHandle();
					fh = er.get(fh);
					assertEquals("files size", 2, fh.get().length());

				} else {
					System.out.println("Something went wrong");
				}
			}
		} catch (Exception e) {
			throw e;
		} finally {
			inputStream.close();
		}
	}
	
	/* Test is intended to test different types of variable passed to javascript from java and check return types,data types, there is a bug log against REST 30209
	 * 
	 * Expected Result : JS variable myJsonNull in this test has JsonNode object of Type NullNode and its value not null. Hence we expect an Exception from JerseyServices.
	 * See Git issue # 317 for further explanation.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testJSDifferentVariableTypes() throws Exception {
	
		DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader("<foo attr=\"attribute\"><?processing instruction?><!--comment-->test1</foo>"));
		Document doc = db.parse(is);
		System.out.println(this.convertXMLDocumentToString(doc));
	 	try {
		String query1 = " var results = [];"
						+"var myString;"
						+"var myBool ;"
						+"var myInteger;"
						+"var myDecimal;"
						+"var myJsonObject;"
						+"var myNull;"
						+ "var myJsonArray;"
						+ "var myJsonNull;"
						+ "results.push(myString,myBool,myInteger,myDecimal,myJsonObject,myJsonArray,myNull,myJsonNull);"
						+"xdmp.arrayValues(results)";
		
		ServerEvaluationCall evl= client.newServerEval().javascript(query1);
		evl.addVariable("myString", "xml")
		.addVariable("myBool", true)
		.addVariable("myInteger", (int)31)
		.addVariable("myDecimal", (double)1.0471975511966)
		.addVariableAs("myJsonObject", new ObjectMapper().createObjectNode().put("foo", "v1").putNull("testNull"))
		.addVariableAs("myNull", (String) null)
		.addVariableAs("myJsonArray", new ObjectMapper().createArrayNode().add(1).add(2).add(3))
		.addVariableAs("myJsonNull", new ObjectMapper().createObjectNode().nullNode() )
		;
		System.out.println(new ObjectMapper().createObjectNode().nullNode().toString());
		EvalResultIterator evr = evl.eval();
		this.validateReturnTypes(evr);

		} catch (Exception e) {
			throw e;
		}
	}
	
	/* Test is intended to test different types of variable passed to javascript from java and check return types,data types, there is a bug log against REST 30209
	 * 
	 * Expected Result : JS variable myJsonNull in this test has value set to null.
	 * See Git issue # 317 for further explanation.
	 */
	@Test
	public void testJSDifferentVariableTypesWithNulls() throws Exception {
	
		DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader("<foo attr=\"attribute\"><?processing instruction?><!--comment-->test1</foo>"));
		Document doc = db.parse(is);
		System.out.println(this.convertXMLDocumentToString(doc));
	 	try {
		String query1 = " var results = [];"
						+"var myString;"
						+"var myBool ;"
						+"var myInteger;"
						+"var myDecimal;"
						+"var myJsonObject;"
						+"var myNull;"
						+ "var myJsonArray;"
						+ "var myJsonNull;"
						+ "results.push(myString,myBool,myInteger,myDecimal,myJsonObject,myJsonArray,myNull,myJsonNull);"
						+"xdmp.arrayValues(results)";
		
		ServerEvaluationCall evl= client.newServerEval().javascript(query1);
		evl.addVariable("myString", "xml")
		.addVariable("myBool", true)
		.addVariable("myInteger", (int)31)
		.addVariable("myDecimal", (double)1.0471975511966)
		.addVariableAs("myJsonObject", new ObjectMapper().createObjectNode().put("foo", "v1").putNull("testNull"))
		.addVariableAs("myNull", (String) null)
		.addVariableAs("myJsonArray", new ObjectMapper().createArrayNode().add(1).add(2).add(3))
		.addVariableAs("myJsonNull", null )
		;
		System.out.println(new ObjectMapper().createObjectNode().nullNode().toString());
		EvalResultIterator evr = evl.eval();
		this.validateReturnTypes(evr);

		} catch (Exception e) {
			throw e;
		}
	}
		
	/* Test is intended to test different types of variable passed to javascript from java and check return types,data types, there is a bug log against REST 30209
	 * This test does not have NullNode set in a variable	 
	 */	
	@Test
	public void testJSDifferentVariableTypesNoNullNodes() throws Exception {
	
		DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader("<foo attr=\"attribute\"><?processing instruction?><!--comment-->test1</foo>"));
		Document doc = db.parse(is);
		System.out.println(this.convertXMLDocumentToString(doc));
	 	try {
		String query1 = " var results = [];"
						+"var myString;"
						+"var myBool ;"
						+"var myInteger;"
						+"var myDecimal;"
						+"var myJsonObject;"
						+"var myNull;"
						+ "var myJsonArray;"						
						+ "results.push(myString,myBool,myInteger,myDecimal,myJsonObject,myJsonArray,myNull);"
						+"xdmp.arrayValues(results)";
		
		ServerEvaluationCall evl= client.newServerEval().javascript(query1);
		evl.addVariable("myString", "xml")
		.addVariable("myBool", true)
		.addVariable("myInteger", (int)31)
		.addVariable("myDecimal", (double)1.0471975511966)
		.addVariableAs("myJsonObject", new ObjectMapper().createObjectNode().put("foo", "v1").putNull("testNull"))
		.addVariableAs("myNull", (String) null)
		.addVariableAs("myJsonArray", new ObjectMapper().createArrayNode().add(1).add(2).add(3))
		;
		System.out.println(new ObjectMapper().createObjectNode().nullNode().toString());
		EvalResultIterator evr = evl.eval();
		this.validateReturnTypes(evr);
		
		} catch (Exception e) {
			throw e;
		}
	}
	
	@Test(expected = java.lang.IllegalStateException.class)
	public void testMultipleJSfnOnServerEval() {
		String insertQuery = "xdmp.document-insert(\"test1.xml\",<foo>test1</foo>)";
		String query1 = "fn.exists(fn:doc())";
		String query2 = "fn.count(fn:doc())";
		String query3 = "xdmp.database-name(xdmp.database())";

		boolean response1 = client.newServerEval().javascript(query1)
				.javascript(insertQuery).eval().next().getBoolean();
		System.out.println(response1);
		int response2 = client.newServerEval().xquery(query2).eval().next()
				.getNumber().intValue();
		System.out.println(response2);
		String response3 = client.newServerEval().xquery(query3)
				.evalAs(String.class);
		System.out.println(response3);
	}
	
	//Issue 30209 ,external variable passing is not working so I have test cases where it test to see we can invoke a module 
	@Test
	public void testJSReturningDifferentTypesOrder3fromModules() throws Exception {

		InputStream inputStream = null;
		DatabaseClient moduleClient = DatabaseClientFactory.newClient(
				"localhost", restPort, (restServerName + "-modules"), "admin",
				"admin", Authentication.DIGEST);
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
			DocumentBuilder db = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(
					"<foo attr=\"attribute\"><?processing instruction?><!--comment-->test1</foo>"));
			Document doc = db.parse(is);
			ServerEvaluationCall evl = client.newServerEval().modulePath(
					"/data/javascriptQueries.sjs");
			
			EvalResultIterator evr = evl.eval();
			while (evr.hasNext()) {
				EvalResult er = evr.next();
				if (er.getType().equals(Type.XML)) {
					DOMHandle dh = new DOMHandle();
					dh = er.get(dh);
					assertEquals("document has content", "<foo>test1</foo>",
							convertXMLDocumentToString(dh.get()));
				} else if (er.getType().equals(Type.JSON)) {
					JacksonHandle jh = new JacksonHandle();
					jh = er.get(jh);
					assertTrue("document has object?", jh.get().has("test"));
				} else if (er.getType().equals(Type.TEXTNODE)) {
					assertTrue("document contains", er.getAs(String.class)
							.equals("This is a text document."));

				} else if (er.getType().equals(Type.BINARY)) {
					FileHandle fh = new FileHandle();
					fh = er.get(fh);
					assertEquals("files size", 2, fh.get().length());

				} else {
					System.out.println("Something went wrong");
				}
			}
		} catch (Exception e) {
			throw e;
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
			moduleClient.release();
		}
	}
}
