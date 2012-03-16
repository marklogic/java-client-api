package com.marklogic.client.test;

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;
import static org.custommonkey.xmlunit.XMLAssert.assertXpathEvaluatesTo;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.marklogic.client.AbstractDocumentManager.Metadata;
import com.marklogic.client.DocumentIdentifier;
import com.marklogic.client.Format;
import com.marklogic.client.TextDocumentManager;
import com.marklogic.client.XMLDocumentManager;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.StringHandle;

public class GenericDocumentTest {
	@BeforeClass
	public static void beforeClass() {
		Common.connect();
	}
	@AfterClass
	public static void afterClass() {
		Common.release();
	}

	@Test
	public void testExists() {
		String uri = "/test/testExists1.txt";
		DocumentIdentifier docId = new DocumentIdentifier(uri);
		TextDocumentManager docMgr = Common.client.newTextDocumentManager();
		assertTrue("Non-existent document appears to exists", !docMgr.exists(docId));
		docMgr.write(docId,new StringHandle().on("A simple text document"));
		assertTrue("Existent document doesn't appear to exist", docMgr.exists(docId));
		docMgr.delete(docId);
	}

	@Test
	public void testDelete() {
		String uri = "/test/testDelete1.txt";
		DocumentIdentifier docId = new DocumentIdentifier(uri);
		TextDocumentManager docMgr = Common.client.newTextDocumentManager();
		docMgr.write(docId, new StringHandle().on("A simple text document"));
		String text = docMgr.read(docId, new StringHandle()).get();
		assertTrue("Could not create document for deletion", text != null && text.length() > 0);
		docMgr.delete(docId);
		text = null;
		boolean hadException = false;
		try {
			text = docMgr.read(docId, new StringHandle()).get();
		} catch (Exception ex) {
			hadException = true;
		}
		assertTrue("Could not delete document", text == null && hadException);
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

		String metadata = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
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
		"    <first>value one</first>\n"+
		"    <second>2</second>\n"+
		"  </prop:properties>\n"+
		"  <rapi:quality>3</rapi:quality>\n"+
		"</rapi:metadata>\n";

		docMgr.setMetadataCategories(Metadata.ALL);
		docMgr.writeMetadata(docId, new StringHandle().on(metadata));

		StringHandle xmlStringHandle = new StringHandle();
		String stringMetadata = docMgr.readMetadata(docId, xmlStringHandle).get();
		assertTrue("Could not get document metadata as an XML String", stringMetadata != null || stringMetadata.length() == 0);

		Document domMetadata = docMgr.readMetadata(docId, new DOMHandle()).get();
		assertTrue("Could not get document metadata as an XML document", domMetadata != null);

		StringHandle jsonStringHandle = new StringHandle();
		jsonStringHandle.setFormat(Format.JSON);
		stringMetadata = docMgr.readMetadata(docId, jsonStringHandle).get();
		assertTrue("Could not get document metadata as JSON", stringMetadata != null || stringMetadata.length() == 0);

		String docText = docMgr.read(docId, xmlStringHandle, new StringHandle()).get();
		stringMetadata = xmlStringHandle.get();
		assertXMLEqual("Failed to read document content in single request",content,docText);
		assertTrue("Could not read document metadata in a single request", stringMetadata != null);
		assertXpathEvaluatesTo("2","count(/*[local-name()='metadata']/*[local-name()='collections']/*[local-name()='collection'])",stringMetadata);
		assertXpathEvaluatesTo("1","count(/*[local-name()='metadata']/*[local-name()='permissions']/*[local-name()='permission']/*[local-name()='role-name' and string(.)='app-user'])",stringMetadata);
		assertXpathEvaluatesTo("2","count(/*[local-name()='metadata']/*[local-name()='properties']/*[local-name()='first' or local-name()='second'])",stringMetadata);
		assertXpathEvaluatesTo("1","count(/*[local-name()='metadata']/*[local-name()='quality' and string(.)='3'])",stringMetadata);

		// http://bugtrack.marklogic.com/16564
		boolean exhibitMultipartDecodeDefect = false;
		if (exhibitMultipartDecodeDefect) {
			String uri2 = "/test/testMetadataXML2.xml";
			docId.setUri(uri2);
			docMgr.write(docId, new StringHandle().on(content), new StringHandle().on(metadata));
			docText = docMgr.read(docId, xmlStringHandle, new StringHandle()).get();
			stringMetadata = xmlStringHandle.get();
			assertXMLEqual("Failed to write document content in single request",content,docText);
			assertXpathEvaluatesTo("2","count(/*[local-name()='metadata']/*[local-name()='collections']/*[local-name()='collection'])",stringMetadata);
			assertXpathEvaluatesTo("1","count(/*[local-name()='metadata']/*[local-name()='permissions']/*[local-name()='permission']/*[local-name()='role-name' and string(.)='app-user'])",stringMetadata);
			assertXpathEvaluatesTo("2","count(/*[local-name()='metadata']/*[local-name()='properties']/*[local-name()='first' or local-name()='second'])",stringMetadata);
			assertXpathEvaluatesTo("1","count(/*[local-name()='metadata']/*[local-name()='quality' and string(.)='3'])",stringMetadata);
		}

		docMgr.writeDefaultMetadata(docId);
		stringMetadata = docMgr.readMetadata(docId, xmlStringHandle).get();
		assertTrue("Could not read document metadata after write default", stringMetadata != null);
		assertXpathEvaluatesTo("0","count(/*[local-name()='metadata']/*[local-name()='collections']/*[local-name()='collection'])",stringMetadata);
		assertXpathEvaluatesTo("0","count(/*[local-name()='metadata']/*[local-name()='permissions']/*[local-name()='permission']/*[local-name()='role-name' and string(.)='app-user'])",stringMetadata);
		assertXpathEvaluatesTo("0","count(/*[local-name()='metadata']/*[local-name()='properties']/*[local-name()='first' or local-name()='second'])",stringMetadata);
		assertXpathEvaluatesTo("0","count(/*[local-name()='metadata']/*[local-name()='quality' and string(.)='3'])",stringMetadata);
	}

}
