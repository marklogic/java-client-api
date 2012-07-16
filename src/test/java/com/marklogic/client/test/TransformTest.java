/*
 * Copyright 2012 MarkLogic Corporation
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

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;

import com.marklogic.client.io.Format;
import com.marklogic.client.document.ServerTransform;
import com.marklogic.client.admin.TransformExtensionsManager;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.StringHandle;

public class TransformTest {
	final static public String TEST_NS =
		"http://marklogic.com/rest-api/test/transform";

	static private String xqueryTransform;
	static private String xslTransform;

	@BeforeClass
	public static void beforeClass() throws IOException {
		Common.connectAdmin();
		xqueryTransform = Common.testFileToString(TransformExtensionsTest.XQUERY_FILE);
		xslTransform    = Common.testFileToString(TransformExtensionsTest.XSLT_FILE);
	}
	@AfterClass
	public static void afterClass() {
		Common.release();
		xqueryTransform = null;
		xslTransform    = null;
	}

	@Test
	public void testXQueryTransform() {
		TransformExtensionsManager extensionMgr =
			Common.client.newServerConfigManager().newTransformExtensionsManager();

		extensionMgr.writeXQueryTransform(
				TransformExtensionsTest.XQUERY_NAME,
				new StringHandle().withFormat(Format.TEXT).with(xqueryTransform),
				TransformExtensionsTest.makeXQueryMetadata(),
				TransformExtensionsTest.makeParameters()
				);

		runTransform(TransformExtensionsTest.XQUERY_NAME);

		extensionMgr.deleteTransform(TransformExtensionsTest.XQUERY_NAME);
	}
	@Test
	public void testXSLTransform() {
		TransformExtensionsManager extensionMgr =
			Common.client.newServerConfigManager().newTransformExtensionsManager();

		extensionMgr.writeXSLTransform(
				TransformExtensionsTest.XSLT_NAME,
				new StringHandle().with(xslTransform),
				TransformExtensionsTest.makeXSLTMetadata(),
				TransformExtensionsTest.makeParameters()
				);

		runTransform(TransformExtensionsTest.XSLT_NAME);

		extensionMgr.deleteTransform(TransformExtensionsTest.XSLT_NAME);
	}
	private void runTransform(String transformName) {
		ServerTransform transform = new ServerTransform(transformName);
		transform.put("value", "true");

		String docId = "/test/testTransformable1.xml";

		XMLDocumentManager docMgr = Common.client.newXMLDocumentManager();
		docMgr.write(docId, new StringHandle().with("<document/>"));
		Document result = docMgr.read(docId, new DOMHandle(), transform).get();
		String value = result.getDocumentElement().getAttributeNS(TEST_NS, "transformed");
		assertEquals("Read transform failed",value,"true");

		docMgr.delete(docId);

		docId = "/test/testTransformable2.xml";
		docMgr.write(docId, new StringHandle().with("<document/>"), transform);
		result = docMgr.read(docId, new DOMHandle()).get();
		value = result.getDocumentElement().getAttributeNS(TEST_NS, "transformed");
		assertEquals("Write transform failed",value,"true");

		docMgr.delete(docId);
	}
}
