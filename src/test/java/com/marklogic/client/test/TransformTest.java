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
}
