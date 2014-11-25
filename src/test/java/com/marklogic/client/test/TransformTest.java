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

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import javax.xml.namespace.QName;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;

import com.marklogic.client.FailedRequestException;
import com.marklogic.client.ForbiddenUserException;
import com.marklogic.client.ResourceNotFoundException;
import com.marklogic.client.ResourceNotResendableException;
import com.marklogic.client.admin.ServerConfigurationManager;
import com.marklogic.client.admin.TransformExtensionsManager;
import com.marklogic.client.document.ServerTransform;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.query.KeyValueQueryDefinition;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StringQueryDefinition;
import com.marklogic.client.query.ValuesDefinition;

public class TransformTest {
	final static public String TEST_NS =
		"http://marklogic.com/rest-api/test/transform";

	static private String xqueryTransform;
	static private String xslTransform;
	static private String optionsName;

	@BeforeClass
	public static void beforeClass()
	throws IOException, FailedRequestException, ForbiddenUserException, ResourceNotFoundException, ResourceNotResendableException {
		Common.connectAdmin();
		xqueryTransform = Common.testFileToString(TransformExtensionsTest.XQUERY_FILE);
		xslTransform    = Common.testFileToString(TransformExtensionsTest.XSLT_FILE);
    	optionsName = ValuesHandleTest.makeValuesOptions();
	}
	@AfterClass
	public static void afterClass()
	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
        Common.client.newServerConfigManager().newQueryOptionsManager().deleteOptions(optionsName);
		Common.release();
		xqueryTransform = null;
		xslTransform    = null;
	}

	@Test
	public void testXQueryTransform()
	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException, ResourceNotResendableException {
		ServerConfigurationManager confMgr =
			Common.client.newServerConfigManager();

		TransformExtensionsManager extensionMgr =
			confMgr.newTransformExtensionsManager();

		extensionMgr.writeXQueryTransform(
				TransformExtensionsTest.XQUERY_NAME,
				new StringHandle().withFormat(Format.TEXT).with(xqueryTransform),
				TransformExtensionsTest.makeXQueryMetadata()
				);

		runTransform(TransformExtensionsTest.XQUERY_NAME);

		extensionMgr.deleteTransform(TransformExtensionsTest.XQUERY_NAME);
	}
	@Test
	public void testXSLTransform()
	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException, ResourceNotResendableException {
		TransformExtensionsManager extensionMgr =
			Common.client.newServerConfigManager().newTransformExtensionsManager();

		extensionMgr.writeXSLTransform(
				TransformExtensionsTest.XSLT_NAME,
				new StringHandle().with(xslTransform),
				TransformExtensionsTest.makeXSLTMetadata()
				);

		runTransform(TransformExtensionsTest.XSLT_NAME);

		extensionMgr.deleteTransform(TransformExtensionsTest.XSLT_NAME);
	}
	private void runTransform(String transformName)
	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
		ServerTransform transform = new ServerTransform(transformName);
		transform.put("value", "true");

		String docId = "/test/testTransformable1.xml";

		XMLDocumentManager docMgr = Common.client.newXMLDocumentManager();
		docMgr.write(docId, new StringHandle().with("<document/>"));
		Document result = docMgr.read(docId, new DOMHandle(), transform).get();
		String value = result.getDocumentElement().getAttributeNS(TEST_NS, "transformed");
		assertEquals("Document read transform failed",value,"true");

		docMgr.delete(docId);

		docId = "/test/testTransformable2.xml";
		docMgr.write(docId, new StringHandle().with("<document/>"), transform);
		result = docMgr.read(docId, new DOMHandle()).get();
		value = result.getDocumentElement().getAttributeNS(TEST_NS, "transformed");
		assertEquals("Document write transform failed",value,"true");

		docMgr.delete(docId);

        QueryManager queryMgr = Common.client.newQueryManager();

        StringQueryDefinition stringQuery = queryMgr.newStringDefinition();
        stringQuery.setCriteria("grandchild1 OR grandchild4");
        stringQuery.setResponseTransform(transform);

		result = queryMgr.search(stringQuery, new DOMHandle()).get();
		value = result.getDocumentElement().getAttributeNS(TEST_NS, "transformed");
		assertEquals("String query read transform failed",value,"true");

		KeyValueQueryDefinition keyValueQuery = queryMgr.newKeyValueDefinition();
		keyValueQuery.put(queryMgr.newElementLocator(new QName("leaf")), "leaf3");
		keyValueQuery.setResponseTransform(transform);

		result = queryMgr.search(keyValueQuery, new DOMHandle()).get();
		value = result.getDocumentElement().getAttributeNS(TEST_NS, "transformed");
		assertEquals("Key-value query read transform failed",value,"true");

    	ValuesDefinition vdef =
    		queryMgr.newValuesDefinition("double", optionsName);
		stringQuery = queryMgr.newStringDefinition();
		stringQuery.setCriteria("10");
        stringQuery.setResponseTransform(transform);

        vdef.setQueryDefinition(stringQuery);
		result = queryMgr.values(vdef, new DOMHandle()).get();
		value = result.getDocumentElement().getAttributeNS(TEST_NS, "transformed");
		assertEquals("Values query read transform failed",value,"true");

// TODO: QBE tests with XQuery and XSLT
	}

	@Test
	public void test118() {
		String naiveTransform = "xquery version \"1.0-ml\";\n" + 

			"module namespace ex = \"http://marklogic.com/rest-api/transform/test118\";\n" + 

			"declare function ex:transform(\n" + 
			"  $context as map:map,\n" + 
			"  $params as map:map,\n" + 
			"  $content as document-node())\n" + 
			"as document-node() {\n" + 
			" document{\n" + 

			"<search:response snippet-format=\"highlight\" total=\"1\" start=\"1\" page-length=\"1\" " + 
			"  xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns=\"\" " + 
			"  xmlns:search=\"http://marklogic.com/appservices/search\">\n" + 
			"  <search:result index=\"1\" uri=\"/doc/2.xml\" path=\"fn:doc('/doc/2.xml')\" score=\"92160\" " + 
			"    confidence=\"0.674626\" fitness=\"0.674626\">\n" + 
			"    <search:snippet>\n" + 
			"      <headline>Q1 <match>outlook</match></headline>\n" + 
			"    </search:snippet>\n" + 
			"    <search:metadata>\n" + 
			"       <id>a</id>\n" + 
			"    </search:metadata>\n" + 
			"  </search:result>\n" + 
			"  <search:qtext>outlook snippet:highlight</search:qtext>\n" + 
			"  <search:metrics>\n" + 
			"    <search:query-resolution-time>PT0.008042S</search:query-resolution-time>\n" + 
			"    <search:facet-resolution-time>PT0.000323S</search:facet-resolution-time>\n" + 
			"    <search:snippet-resolution-time>PT0.018339S</search:snippet-resolution-time>\n" + 
			"    <search:total-time>PT0.027161S</search:total-time>\n" + 
			"  </search:metrics>\n" + 
			"</search:response>}\n" + 
			"};";
		TransformExtensionsManager extensionMgr =
			Common.client.newServerConfigManager().newTransformExtensionsManager();

		extensionMgr.writeXQueryTransform( "test118", new StringHandle().with(naiveTransform));
		QueryManager q = Common.client.newQueryManager();
		StringQueryDefinition s = q.newStringDefinition("");
		s.setCriteria("a");
		s.setResponseTransform(new ServerTransform("test118"));
		q.search(s, new SearchHandle());
		// if the previous line throws no exception, then 118 is resolved 
	}
}
