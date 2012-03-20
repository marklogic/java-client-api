package com.marklogic.client.test;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;

import javax.xml.bind.DatatypeConverter;

import com.marklogic.client.BinaryDocumentManager;
import com.marklogic.client.AbstractDocumentManager.Metadata;
import com.marklogic.client.BinaryDocumentManager.MetadataExtraction;
import com.marklogic.client.DocumentIdentifier;
import com.marklogic.client.io.BytesHandle;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.client.io.StringHandle;

public class BinaryDocumentTest {
	@BeforeClass
	public static void beforeClass() {
		Common.connect();
	}
	@AfterClass
	public static void afterClass() {
		Common.release();
	}

	// TODO: extraction parameter
	// TODO: range request

	// a simple base64-encoded binary
	final static String ENCODED_BINARY =
"iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAABHNCSVQICAgIfAhkiAAAAAlwSFlzAAAN1wAADdcBQiibeAAAABl0RVh0U29mdHdhcmUAd3d3Lmlua3NjYXBlLm9yZ5vuPBoAAAAadEVYdFRpdGxlAEJpbmFyeSB3aXRoIG1ldGFkYXRhlZB6ogAAAA50RVh0QXV0aG9yAEVIZW5udW0vx5F5AAAAKnRFWHREZXNjcmlwdGlvbgBBIHNpbXBsZSwgc21hbGwgYXNzZXQgZm9yIHRlc3S+p8YwAAAAGHRFWHRDcmVhdGlvbiBUaW1lADIwMTItMDItMjImuR4cAAAAGHRFWHRTb3VyY2UAYmluYXJ5LXNvdXJjZS5zdmeAUrN6AAAA6UlEQVQ4jWP8//8/AyWAiSLd1DCABZmTljZ7qoCAcNqPHz8Z+fj4PouKCnOzsbH8kJMT/yogwPWVn5/nCw8Pe76CAsNBrAYwMzMLCgoKwcQEvn37wSAnJ8OqrCzLC1Pz7x+DIQMDwoCBDwPqGsDOzvERXcH//wx/iTZgwoSobF5erp3IYl++/PxHtAEMDAz/cnLMPAQF+fOZmZn/QVzwnxVFwT+G3/gMYGBgYGDIyDCcJCzMqc/JyfYeJvb9+4/XHz58LFdRYZiKrJYRX15YvfoaGwsLx3QJCZHVlpZ8O7CpwWsAMWDg0wEAcQxBn+bNom8AAAAASUVORK5CYII=";
	final static byte[] BYTES_BINARY = DatatypeConverter.parseBase64Binary(ENCODED_BINARY);

	@Test
	public void testReadWrite() throws IOException {
		String uri = "/test/binary-sample.png";
		DocumentIdentifier docId = new DocumentIdentifier(uri);
		BinaryDocumentManager docMgr = Common.client.newBinaryDocumentManager();
		docMgr.write(docId, new BytesHandle().on(BYTES_BINARY));
		byte[] buf = docMgr.read(docId, new BytesHandle()).get();
		assertTrue("Binary document read 0 bytes", buf.length > 0);
		buf = Common.streamToBytes(docMgr.read(docId, new InputStreamHandle()).get());
		assertTrue("Binary document read binary empty input stream",buf.length > 0);

// TODO: use a binary with metadata and verify property
//		docMgr.setMetadataExtraction(MetadataExtraction.PROPERTIES);
//		docMgr.setMetadataCategories(Metadata.PROPERTIES);
//		String metadataString = docMgr.readMetadata(docId, new StringHandle()).get();
//		Document metadataDocument = docMgr.readMetadata(docId, new DOMHandle()).get();
	}
}
