package com.marklogic.client.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.io.Reader;

import javax.xml.XMLConstants;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;

import com.marklogic.client.DocumentIdentifier;
import com.marklogic.client.JSONDocumentManager;
import com.marklogic.client.XMLDocumentManager;
import com.marklogic.client.io.BytesHandle;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.client.io.ReaderHandle;
import com.marklogic.client.io.StringHandle;

public class JSONDocumentTest {
	@BeforeClass
	public static void beforeClass() {
		Common.connect();
	}
	@AfterClass
	public static void afterClass() {
		Common.release();
	}

	@Test
	public void testReadWrite() throws IOException {
		String uri = "/test/testWrite1.json";

		DocumentIdentifier docId = Common.client.newDocId(uri);

		String content = "{\n"+
		"\"stringKey\":\"string value\",\n"+
		"\"numberKey\":7,\n"+
		"\"objectKey\":{\"childObjectKey\":\"child object value\"},\n"+
		"\"arrayKey\":[\"item value\",3,{\"itemObjectKey\":\"item object value\"}]\n"+
		"}\n";

		JSONDocumentManager docMgr = Common.client.newJSONDocumentManager();
		docMgr.write(docId, new StringHandle().with(content));

		String testText = content.replace("\n", "");
		String docText = docMgr.read(docId, new StringHandle()).get();
		assertNotNull("Read null string for JSON content",docText);
		assertEquals("Failed to read JSON document as String", testText, docText);

		BytesHandle bytesHandle = new BytesHandle();
		docMgr.read(docId, bytesHandle);
		assertEquals("JSON document mismatch reading bytes", bytesHandle.get().length,testText.length());

		InputStreamHandle inputStreamHandle = new InputStreamHandle();
		docMgr.read(docId, inputStreamHandle);
		byte[] b = Common.streamToBytes(inputStreamHandle.get());
		assertEquals("JSON document mismatch reading input stream",new String(b),testText);

		Reader reader = docMgr.read(docId, new ReaderHandle()).get();
		String s = Common.readerToString(reader);
		assertEquals("JSON document mismatch with reader",s,testText);

		File file = docMgr.read(docId, new FileHandle()).get();
		assertEquals("JSON document mismatch with file",testText.length(),file.length());

		String lang = "fr-CA";
		docMgr.setLanguage(lang);
		docMgr.write(docId, new StringHandle().with(content));

		XMLDocumentManager xmlMgr = Common.client.newXMLDocumentManager();
		docId.setMimetype(null);  // set to application/json by read
		Document document = xmlMgr.read(docId, new DOMHandle()).get();
		assertEquals("Failed to set language attribute on JSON", lang,
				document.getDocumentElement().getAttributeNS(XMLConstants.XML_NS_URI, "lang"));
	}
}
