package com.marklogic.client.test;

import static org.custommonkey.xmlunit.XMLAssert.assertXpathEvaluatesTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;

import com.marklogic.client.ExtensionMetadata;
import com.marklogic.client.Format;
import com.marklogic.client.TransformExtensionsManager;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.StringHandle;

public class TransformExtensionsTest {
	final static String XQUERY_NAME = "testxqy";
	final static String XSLT_NAME   = "testxsl";
	final static String XQUERY_FILE = XQUERY_NAME + ".xqy"; 
	final static String XSLT_FILE   = XSLT_NAME + ".xsl"; 

	static private String xqueryTransform;
	static private String xslTransform;

	@BeforeClass
	public static void beforeClass() throws IOException {
		Common.connectAdmin();
		xqueryTransform = Common.testFileToString(XQUERY_FILE);
		xslTransform    = Common.testFileToString(XSLT_FILE);
	}
	@AfterClass
	public static void afterClass() {
		Common.release();
		xqueryTransform = null;
		xslTransform    = null;
	}

	static ExtensionMetadata makeXQueryMetadata() {
		ExtensionMetadata metadata = new ExtensionMetadata();
		metadata.setTitle("Document XQuery Transform");
		metadata.setDescription("This plugin adds an attribute to the root element");
		metadata.setProvider("MarkLogic");
		metadata.setVersion("0.1");
		return metadata;
	}

	static ExtensionMetadata makeXSLTMetadata() {
		ExtensionMetadata metadata = new ExtensionMetadata();
		metadata.setTitle("Document XSLT Transform");
		metadata.setDescription("This plugin adds an attribute to the root element");
		metadata.setProvider("MarkLogic");
		metadata.setVersion("0.1");
		return metadata;
	}

	static Map<String,String> makeParameters() {
		Map<String,String> params = new HashMap<String,String>();
		params.put("value", "true");
		return params;
	}

	@Test
	public void testTransformExtensions() throws XpathException {
		TransformExtensionsManager extensionMgr =
			Common.client.newServerConfigManager().newTransformExtensionsManager();

		StringHandle handle = new StringHandle();
		handle.setFormat(Format.TEXT);

		writeXQueryTransform(extensionMgr);

		writeXSLTransform(extensionMgr);

		extensionMgr.readXQueryTransform(XQUERY_NAME, handle);
		assertEquals("Failed to retrieve XQuery transform", xqueryTransform, handle.get());

		Document result = extensionMgr.readXSLTransform(XSLT_NAME, new DOMHandle()).get();
		assertNotNull("Failed to retrieve XSLT transform", result);
		assertXpathEvaluatesTo("1", "count(/*[local-name(.) = 'stylesheet'])", result);

		result = extensionMgr.listTransforms(new DOMHandle()).get();
		assertNotNull("Failed to retrieve transforms list", result);
		assertXpathEvaluatesTo("2", "count(" +
				"/*[local-name(.) = 'transforms']/" +
				"*[local-name(.) = 'transform']/" +
				"*[local-name(.) = 'transform-parameters']/" +
				"*[local-name(.) = 'parameter']/" +
				"*[local-name(.) = 'parameter-name']" +
				")", result);

        extensionMgr.deleteTransform(XQUERY_NAME);
		extensionMgr.readXQueryTransform(XQUERY_NAME, handle);
		assertNull("Failed to delete XQuery transform", handle.get());

		extensionMgr.deleteTransform(XSLT_NAME);
		extensionMgr.readXSLTransform(XSLT_NAME, handle);
		assertNull("Failed to delete XSLT transform", handle.get());
	}
	public void writeXQueryTransform(TransformExtensionsManager extensionMgr) {
		extensionMgr.writeXQueryTransform(
				XQUERY_NAME,
				new StringHandle().withFormat(Format.TEXT).with(xqueryTransform),
				makeXQueryMetadata(),
				makeParameters()
				);		
	}
	public void writeXSLTransform(TransformExtensionsManager extensionMgr) {
		extensionMgr.writeXSLTransform(
				XSLT_NAME,
				new StringHandle().with(xslTransform),
				makeXSLTMetadata(),
				makeParameters()
				);
	}
}
