package com.marklogic.client.test;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

import javax.xml.bind.DatatypeConverter;

import com.marklogic.client.BinaryDocument;
import com.marklogic.client.io.BytesHandle;
import com.marklogic.client.io.InputStreamHandle;

public class BinaryDocumentTest {
	// a simple base64-encoded binary
	final static String ENCODED_BINARY =
"iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAABHNCSVQICAgIfAhkiAAAAAlwSFlzAAAN1wAADdcBQiibeAAAABl0RVh0U29mdHdhcmUAd3d3Lmlua3NjYXBlLm9yZ5vuPBoAAAAadEVYdFRpdGxlAEJpbmFyeSB3aXRoIG1ldGFkYXRhlZB6ogAAAA50RVh0QXV0aG9yAEVIZW5udW0vx5F5AAAAKnRFWHREZXNjcmlwdGlvbgBBIHNpbXBsZSwgc21hbGwgYXNzZXQgZm9yIHRlc3S+p8YwAAAAGHRFWHRDcmVhdGlvbiBUaW1lADIwMTItMDItMjImuR4cAAAAGHRFWHRTb3VyY2UAYmluYXJ5LXNvdXJjZS5zdmeAUrN6AAAA6UlEQVQ4jWP8//8/AyWAiSLd1DCABZmTljZ7qoCAcNqPHz8Z+fj4PouKCnOzsbH8kJMT/yogwPWVn5/nCw8Pe76CAsNBrAYwMzMLCgoKwcQEvn37wSAnJ8OqrCzLC1Pz7x+DIQMDwoCBDwPqGsDOzvERXcH//wx/iTZgwoSobF5erp3IYl++/PxHtAEMDAz/cnLMPAQF+fOZmZn/QVzwnxVFwT+G3/gMYGBgYGDIyDCcJCzMqc/JyfYeJvb9+4/XHz58LFdRYZiKrJYRX15YvfoaGwsLx3QJCZHVlpZ8O7CpwWsAMWDg0wEAcQxBn+bNom8AAAAASUVORK5CYII=";
	final static byte[] BYTES_BINARY = DatatypeConverter.parseBase64Binary(ENCODED_BINARY);

	@Test
	public void testReadWrite() throws IOException {
		String uri = "/test/binary-sample.png";
		BinaryDocument doc = Common.client.newBinaryDocument(uri);
		doc.write(new BytesHandle().on(BYTES_BINARY));
		byte[] buf = doc.read(new BytesHandle()).get();
		assertTrue("Binary document read 0 bytes", buf.length > 0);
		buf = Common.streamToBytes(doc.read(new InputStreamHandle()).get());
		assertTrue("Binary document read binary empty input stream",buf.length > 0);
	}
}
