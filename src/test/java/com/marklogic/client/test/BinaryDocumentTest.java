/*
 * Copyright 2012-2015 MarkLogic Corporation
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

import static org.custommonkey.xmlunit.XMLAssert.assertXpathEvaluatesTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import javax.xml.bind.DatatypeConverter;

import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;

import com.marklogic.client.document.DocumentDescriptor;
import com.marklogic.client.document.DocumentManager.Metadata;
import com.marklogic.client.document.BinaryDocumentManager;
import com.marklogic.client.document.BinaryDocumentManager.MetadataExtraction;
import com.marklogic.client.io.BytesHandle;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.InputStreamHandle;

public class BinaryDocumentTest {
	@BeforeClass
	public static void beforeClass() {
		Common.connect();
	}
	@AfterClass
	public static void afterClass() {
		Common.release();
	}

	// a simple base64-encoded binary
	final static public String ENCODED_BINARY =
		"iVBORw0KGgoAAAANSUhEUgAAAA0AAAATCAYAAABLN4eXAAAAAXNSR0IArs4c6QAAAAZiS0dEAP8A/wD/oL2nkwAAAAlwSFlzAAALEwAACxMBAJqcGAAAAAd0SU1FB9oIEQEjMtAYogQAAAKvSURBVCjPlZLLbhxFAEVPVVdXVz/G8zCOn0CsKGyQkSIIKzas8xfsWbLkp/gJhCKheIlAJDaj2MYez6u7p7vrxQKUPVc6+yOdK77/4cfXQohJqlOVZdmBSpKY6jQKBM45oVMlgHvrvMuNWRljvlNKq69G2YyqLDg4mLE/2yPNYFRWlFXF/nTC2clRWbc7Fss1IcZzqTA8eWY5eu7p1Hv+WvyBVjnGZOQmI9UKISUqSXDO0bS7Tko0xfGSp18kjM7v+P3+NUMr8T5grWMYLCEErHM474khoCw1t78eU/8mEOpjXpxekJUORIZSCbkxSCnRWpPnBikTqbx31E1DjJHpeIzRhnW9xceI857H5Yr1Zku765jf3DIMtlUAIQRCiFhnabsOH1IEAmstAGWRY11ApykmM0oplTKZjNGZREpJoUueHI0ZFRV7exX7+1Nm0yn9YLm5u2fX96lUseLwxQ0vX8H04i2/XP9Et5H44OkHS920hBDo+56u77GDjcrHjvV1ya3TDO2M01mOUAEAhED+R5IkpKmCiFCOjoc/p+xuLbPpCc+P95HaEqIBIhHoB8t2W/PwsKBudl5FH7GxwUYYouJh5ci7nLbtWW02LBaPvLuef1AdrItKKolJpkivwGrG5QxTCsq8pCxLqqrk7PiIwTmW6y0xRCVTSg4vFnz+raM4+5ur1RtSUZHnOUWeMx5VVFWJTlOstfTWRuk96NIyOUgRRc188RZvgRg/3OffjoFESohxUMvmjqufP+X+MqDTU77+5EvMKKBUQpZpijxHSkluDHvjMW8uL79Rnz07bwSyzDLFqCzwDNw/PNI0O9bbhvVmQ7vb0bQdi+Wq327rl+rko8krodKnCHnofJju+r5oupBstg1KJT7Vuruev185O9zVm/WVUmouYoz83/0DxhRmafe2kasAAAAASUVORK5CYII=";
	final static public byte[] BYTES_BINARY = DatatypeConverter.parseBase64Binary(ENCODED_BINARY);

	@Test
	public void testReadWrite() throws IOException, XpathException {
		String docId = "/test/binary-sample.png";
		String mimetype = "image/png";

		BinaryDocumentManager docMgr = Common.client.newBinaryDocumentManager();
		docMgr.setMetadataExtraction(MetadataExtraction.PROPERTIES);
		docMgr.write(docId, new BytesHandle().with(BYTES_BINARY).withMimetype(mimetype));

		DocumentDescriptor desc = docMgr.exists(docId);
		assertTrue("Binary exists did not get number of bytes",
				desc.getByteLength() != DocumentDescriptor.UNKNOWN_LENGTH);
		assertEquals("Binary exists got wrong number of bytes", BYTES_BINARY.length, desc.getByteLength());

		byte[] buf = docMgr.read(docId, new BytesHandle()).get();
		assertEquals("Binary document read wrong number of bytes", BYTES_BINARY.length, buf.length);

		buf = Common.streamToBytes(docMgr.read(docId, new InputStreamHandle()).get());
		assertTrue("Binary document read binary empty input stream",buf.length > 0);

		BytesHandle handle = new BytesHandle();
		buf = docMgr.read(docId, handle, 9, 10).get();
		assertEquals("Binary range read wrong number of bytes", 10, buf.length);
		assertEquals("Binary range did not set length in handle", 10, handle.getByteLength());

		docMgr.setMetadataCategories(Metadata.PROPERTIES);
		Document metadataDocument = docMgr.readMetadata(docId, new DOMHandle()).get();
		assertXpathEvaluatesTo("image/png","string(/*[local-name()='metadata']/*[local-name()='properties']/*[local-name()='content-type'])", metadataDocument);
		assertXpathEvaluatesTo("none","string(/*[local-name()='metadata']/*[local-name()='properties']/*[local-name()='filter-capabilities'])", metadataDocument);
		assertXpathEvaluatesTo("815","string(/*[local-name()='metadata']/*[local-name()='properties']/*[local-name()='size'])", metadataDocument);
	}
}
