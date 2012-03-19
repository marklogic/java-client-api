package com.marklogic.client.test;

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;
import static org.custommonkey.xmlunit.XMLAssert.assertXpathEvaluatesTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.marklogic.client.DocumentIdentifier;
import com.marklogic.client.Format;
import com.marklogic.client.TextDocumentManager;
import com.marklogic.client.XMLDocumentManager;
import com.marklogic.client.AbstractDocumentManager.Metadata;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.DocumentMetadataHandle.Capability;
import com.marklogic.client.io.DocumentMetadataHandle.DocumentCollections;
import com.marklogic.client.io.DocumentMetadataHandle.DocumentPermissions;
import com.marklogic.client.io.DocumentMetadataHandle.DocumentProperties;
import com.marklogic.client.io.StringHandle;

public class MetadataHandleTest {
	@BeforeClass
	public static void beforeClass() {
		Common.connect();
	}
	@AfterClass
	public static void afterClass() {
		Common.release();
	}

	@Test
	public void testReadWriteMetadata() throws SAXException, IOException, XpathException {
		String uri = "/test/testMetadataXML1.xml";
		String content = "<?xml version='1.0' encoding='UTF-8'?>\n"+
			"<root mode='mixed' xml:lang='en'>\n"+
			"<child mode='basic'>value</child>\n"+
			"A simple XML document\n"+
			"</root>\n";
		DocumentIdentifier docId = new DocumentIdentifier(uri);
		XMLDocumentManager docMgr = Common.client.newXMLDocumentManager();
		docMgr.write(docId, new StringHandle().on(content));

		String metadataText = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
		"<rapi:metadata uri=\"/test/testMetadataXML1.xml\" xsi:schemaLocation=\"http://marklogic.com/rest-api/database dbmeta.xsd\" xmlns:rapi=\"http://marklogic.com/rest-api\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n"+
		"  <rapi:collections>\n"+
		"    <rapi:collection>/document/collection1</rapi:collection>\n"+
		"    <rapi:collection>/document/collection2</rapi:collection>\n"+
		"  </rapi:collections>\n"+
		"  <rapi:permissions>\n"+
		"    <rapi:permission>\n"+
		"      <rapi:role-name>app-user</rapi:role-name>\n"+
		"      <rapi:capability>update</rapi:capability>\n"+
		"      <rapi:capability>read</rapi:capability>\n"+
		"    </rapi:permission>\n"+
		"  </rapi:permissions>\n"+
		"  <prop:properties xmlns:prop=\"http://marklogic.com/xdmp/property\">\n"+
// TODO:
//		"    <first>value one</first>\n"+
//		"    <second>2</second>\n"+
		"  </prop:properties>\n"+
		"  <rapi:quality>3</rapi:quality>\n"+
		"</rapi:metadata>\n";

		DocumentMetadataHandle metaHandle = new DocumentMetadataHandle();
		metaHandle.getCollections().addAll("/document/collection1", "/document/collection2");
		metaHandle.getPermissions().add("app-user", Capability.UPDATE, Capability.READ);
// TODO:
//		metaHandle.getProperties().put("first", "value one");
		metaHandle.getProperties().put("second", 2);
		metaHandle.setQuality(3);

		docMgr.setMetadataCategories(Metadata.ALL);

		docMgr.writeMetadata(docId, metaHandle);
		StringHandle xmlStringHandle = new StringHandle();
		String stringMetadata = docMgr.readMetadata(docId, xmlStringHandle).get();
		assertTrue("Could not get document metadata as an XML String", stringMetadata != null || stringMetadata.length() == 0);

		docMgr.writeMetadata(docId, new StringHandle().on(metadataText));
		metaHandle = docMgr.readMetadata(docId, new DocumentMetadataHandle());
		assertTrue("Could not get document metadata as a structure", metaHandle != null);
		DocumentCollections collections = metaHandle.getCollections();
		assertEquals("Collection with wrong size", 2, collections.size());
		assertTrue("Collection with wrong values", collections.contains("/document/collection1") && collections.contains("/document/collection2"));
		DocumentPermissions permissions = metaHandle.getPermissions();
		// rest-reader and rest-writer expected
		assertEquals("Permissions with wrong size", 3, permissions.size());
		assertTrue("Permissions without key", permissions.containsKey("app-user"));
		assertEquals("Permission key with wrong value size", 2, permissions.get("app-user").size());
		assertTrue("Permission key with wrong values", permissions.get("app-user").contains(Capability.READ) && permissions.get("app-user").contains(Capability.UPDATE));
// TODO: properties
		assertEquals("Wrong quality", 3, metaHandle.getQuality());

	}
}
