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

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.SimpleTimeZone;

import javax.xml.parsers.DocumentBuilderFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.marklogic.client.eval.JSONVariableSet;
import com.marklogic.client.eval.ServerEval;
import com.marklogic.client.eval.VariableSet;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.StringHandle;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class EvalTest {
    @BeforeClass
    public static void beforeClass() throws JAXBException {
        Common.connect();
        //System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.wire", "debug");
    }
    @AfterClass
    public static void afterClass() {
        Common.release();
    }

    @Test
    public void evalTest1 {
        // hello world and response determined by implicit StringHandle which registered with String.class
        String xquery = "'hello world'";
        ServerEval eval = Common.client.newServerEval();
        String response = eval.evalXQueryAs(String.class, xquery, null, null, null, TransactionMode.QUERY);
        assertEquals("Return should be 'hello world'", "hello world", response);

        // hello world with a variable and response explicit set to StringHandle
        xquery = "declare variable $planet external;" +
                        "'hello world from ' || $planet";
        VariableSet vars = eval.newXMLVariableSet().addAs("planet", "Mars");
        StringHandle strResponse = eval.evalXQuery(xquery, vars, new StringHandle(), null, null, TransactionMode.QUERY);
        assertEquals("Return should be 'hello world from Mars'", "hello world from Mars", strResponse.get());

        // accept and return each JSON variable type so use MultiPartResponsePage
        String xquery = "declare variable $myString as xs:string external;" +
                        "declare variable $myArray as json:array external;" +
                        "declare variable $myObject as json:object external;" +
                        "declare variable $myBool as xs:boolean external;" +
                        "declare variable $myInteger as xs:integer external;" +
                        "declare variable $myDouble as xs:double external;" +
                        "declare variable $myDate as xs:date external;" +
                        "$myString, $myArray, $myObject, $myBool, $myInteger, $myDouble, $myDate";
        Calendar septFirst = new GregorianCalendar(2014, Calendar.SEPTEMBER, 1);
        septFirst.setTimeZone(new SimpleTimeZone(0, "UTC"));
        JSONVariableSet jsonVars = eval.newJSONVariableSet()
          // String is mapped to implicitly use StringHandle
          .addAs("myString",  "Mars")
          // ArrayNode extends JSONNode which is mapped to implicitly use JacksonHandle
          .addAs("myArray",   new ObjectMapper().createArrayNode().add("item1").add("item2"))
          // ObjectNode extends JSONNode which is mapped to implicitly use JacksonHandle
          .addAs("myObject",  new ObjectMapper().createObjectNode().put("item1", "value1"))
          // the rest use built-in methods of JSONVariableSet
          .add  ("myBool",    true)
          .add  ("myInteger", 123)
          .add  ("myDouble",  1.1)
          .add  ("myDate",    septFirst);
        MultiPartResponsePage responses = eval.evalXQuery(xquery, jsonVars, null, null, TransactionMode.QUERY);
        assertEquals("myString should = 'Mars'", "Mars", responses.next().getAs(String.class));
        assertEquals("myArray should = [\"item1\",\"item2\"]", "[\"item1\",\"item2\"]", 
          responses.next().getAs(JsonNode.class));
        assertEquals("myObject should = {\"item1\"=\"value1\"}", "{\"item1\"=\"value1\"}", 
          responses.next().getAs(JsonNode.class));
        JacksonHandle myBool = responses.next().get(new JacksonHandle());
        assertEquals("myBool should = true", true, myBool.get().asBoolean());
        assertEquals("myInteger should = 123", 123, responses.next().get(new JacksonHandle()).get().asInt());
        assertEquals("myDouble should = 1.1", 1.1, responses.next().get(new JacksonHandle()).get().asDouble());
        // the format what we get from javax.xml.datatype.XMLGregorianCalendar.toString()
        assertEquals("myDate should = '2014-09-01T00:00:00'", "2014-09-01T00:00:00",
          responses.next().get(new JAXBDatatypeHandle()).get());


        // accept and return each XML variable type so use MultiPartResponsePage
        String xquery = "declare variable $myString as xs:string external;" +
                        "declare variable $myBinary as binary() external;" +
                        "declare variable $myComment as comment() external;" +
                        "declare variable $myDocument as document() external;" +
                        "declare variable $myDuration as xs:duration external;" +
                        "declare variable $myElement as element() external;" +
                        "declare variable $myNode as node() external;" +
                        "declare variable $myProcessingInstruction as processingInstruction() external;" +
                        "declare variable $myText as text() external;" +
                        "declare variable $myComment as comment() external;" +
                        "declare variable $myComment as comment() external;" +
                        "declare variable $myComment as comment() external;" +
                        "declare variable $myComment as comment() external;" +
                        "declare variable $myComment as comment() external;" +
                        "declare variable $myBool as xs:boolean external;" +
                        "declare variable $myInteger as xs:integer external;" +
                        "declare variable $myDouble as xs:double external;" +
                        "declare variable $myDate as xs:date external;" +
                        "$myString, $myArray, $myObject, $myBool, $myInteger, $myDouble, $myDate";
		Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        XMLVariableSet jsonVars = eval.newXMLVariableSet()
          // String is mapped to implicitly use StringHandle
          .addAs("myString",  "Mars")
          .addAs("myBinary",  byte[] { 1, 2, 3})
          .addAs("myComment", document.createComment("<!--test-->"))
          .addAs("myDocument", document)
          .add  ("myDuration", new JAXBDatatypeHandle("xs:dayTimeDuration", "PT0H"))
          .addAs("myElement", document.createElement("http://marklogic.com/appservices/search", "options"))
          // ArrayNode extends XMLNode which is mapped to implicitly use JacksonHandle
          .addAs("myArray",   new JacksonHandle().getMapper().createArrayNode().add("item1").add("item2"))
          // ObjectNode extends XMLNode which is mapped to implicitly use JacksonHandle
          .addAs("myObject",  new JacksonHandle().getMapper().createObjectNode().put("item1", "value1"))
          // the rest use built-in methods of XMLVariableSet
          .add  ("myBool",    true)
          .add  ("myInteger", 123)
          .add  ("myDouble",  1.1)
          .add  ("myDate",    new GregorianCalendar(2014, Calendar.SEPTEMBER, 1));
        MultiPartResponsePage responses = eval.evalXQuery(xquery, jsonVars, null, null, TransactionMode.QUERY);
        assertEquals("myString should = 'Mars'", "Mars", responses.next().getAs(String.class));
        assertEquals("myArray should = [\"item1\",\"item2\"]", "[\"item1\",\"item2\"]", 
          responses.next().getAs(JsonNode.class));
        assertEquals("myObject should = {\"item1\"=\"value1\"}", "{\"item1\"=\"value1\"}", 
          responses.next().getAs(JsonNode.class));
        JacksonHandle myBool = responses.next().get(new JacksonHandle());
        assertEquals("myBool should = true", true, myBool.get().asBoolean());
        assertEquals("myInteger should = 123", 123, responses.next().get(new JacksonHandle()).get().asInt());
        assertEquals("myDouble should = 1.1", 1.1, responses.next().get(new JacksonHandle()).get().asDouble());
        // the format what we get from java.util.Calendar.toString()
        assertEquals("myDate should = 'Mon Sep 01 00:00:00 UDT 2014'", "Mon Sep 01 00:00:00 UDT 2014",
          responses.next().get(new JAXBDatatypeHandle()).get());
    }

}
