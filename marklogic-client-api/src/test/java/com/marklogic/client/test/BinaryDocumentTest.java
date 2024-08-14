/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.test;

import com.marklogic.client.document.*;
import com.marklogic.client.document.BinaryDocumentManager.MetadataExtraction;
import com.marklogic.client.document.DocumentManager.Metadata;
import com.marklogic.client.io.*;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;

import jakarta.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import static org.custommonkey.xmlunit.XMLAssert.assertXpathEvaluatesTo;
import static org.junit.jupiter.api.Assertions.*;

public class BinaryDocumentTest {
  @BeforeAll
  public static void beforeClass() {
    Common.connect();
  }
  @AfterAll
  public static void afterClass() {
  }

  // a simple base64-encoded binary
  final static public String ENCODED_BINARY =
    "iVBORw0KGgoAAAANSUhEUgAAAA0AAAATCAYAAABLN4eXAAAAAXNSR0IArs4c6QAAAAZiS0dEAP8A/wD/oL2nkwAAAAlwSFlzAAALEwAACxMBAJqcGAAAAAd0SU1FB9oIEQEjMtAYogQAAAKvSURBVCjPlZLLbhxFAEVPVVdXVz/G8zCOn0CsKGyQkSIIKzas8xfsWbLkp/gJhCKheIlAJDaj2MYez6u7p7vrxQKUPVc6+yOdK77/4cfXQohJqlOVZdmBSpKY6jQKBM45oVMlgHvrvMuNWRljvlNKq69G2YyqLDg4mLE/2yPNYFRWlFXF/nTC2clRWbc7Fss1IcZzqTA8eWY5eu7p1Hv+WvyBVjnGZOQmI9UKISUqSXDO0bS7Tko0xfGSp18kjM7v+P3+NUMr8T5grWMYLCEErHM474khoCw1t78eU/8mEOpjXpxekJUORIZSCbkxSCnRWpPnBikTqbx31E1DjJHpeIzRhnW9xceI857H5Yr1Zku765jf3DIMtlUAIQRCiFhnabsOH1IEAmstAGWRY11ApykmM0oplTKZjNGZREpJoUueHI0ZFRV7exX7+1Nm0yn9YLm5u2fX96lUseLwxQ0vX8H04i2/XP9Et5H44OkHS920hBDo+56u77GDjcrHjvV1ya3TDO2M01mOUAEAhED+R5IkpKmCiFCOjoc/p+xuLbPpCc+P95HaEqIBIhHoB8t2W/PwsKBudl5FH7GxwUYYouJh5ci7nLbtWW02LBaPvLuef1AdrItKKolJpkivwGrG5QxTCsq8pCxLqqrk7PiIwTmW6y0xRCVTSg4vFnz+raM4+5ur1RtSUZHnOUWeMx5VVFWJTlOstfTWRuk96NIyOUgRRc188RZvgRg/3OffjoFESohxUMvmjqufP+X+MqDTU77+5EvMKKBUQpZpijxHSkluDHvjMW8uL79Rnz07bwSyzDLFqCzwDNw/PNI0O9bbhvVmQ7vb0bQdi+Wq327rl+rko8krodKnCHnofJju+r5oupBstg1KJT7Vuruev185O9zVm/WVUmouYoz83/0DxhRmafe2kasAAAAASUVORK5CYII=";
  final static public byte[] BYTES_BINARY = DatatypeConverter.parseBase64Binary(ENCODED_BINARY);

  @Test
  public void testReadWrite() throws IOException, XpathException {
    String docId = "/test/binary-sample.png";
    String mimetype = "image/png";

    for (int i: new int[]{0, 1}) {
        BinaryDocumentManager docMgr = Common.client.newBinaryDocumentManager();
        docMgr.setMetadataExtraction(MetadataExtraction.PROPERTIES);

        BytesHandle handle = new BytesHandle().with(BYTES_BINARY).withMimetype(mimetype);
        switch (i) {
            case 0:
                docMgr.write(docId, handle);
                break;
            case 1:
                docMgr.write(docMgr.newWriteSet().add(docId, handle));
                break;
            default:
                fail("unknown case: "+i);
        }

        DocumentDescriptor desc = docMgr.exists(docId);
        assertTrue(desc.getByteLength() != DocumentDescriptor.UNKNOWN_LENGTH);
        assertEquals(BYTES_BINARY.length, desc.getByteLength());

        byte[] buf = docMgr.read(docId, new BytesHandle()).get();
        assertEquals(BYTES_BINARY.length, buf.length);

        buf = Common.streamToBytes(docMgr.read(docId, new InputStreamHandle()).get());
        assertTrue(buf.length > 0);

        switch (i) {
            case 0:
                handle = new BytesHandle();
                buf = docMgr.read(docId, handle, 9, 10).get();
                assertEquals(10, buf.length);
                assertEquals(10, handle.getByteLength());
                break;
            case 1:
                break;
            default:
                fail("unknown case: "+i);
        }

        docMgr.setMetadataCategories(Metadata.PROPERTIES);
        Document metadataDocument = docMgr.readMetadata(docId, new DOMHandle()).get();
        assertXpathEvaluatesTo("image/png","string(/*[local-name()='metadata']/*[local-name()='properties']/*[local-name()='content-type'])", metadataDocument);
        assertXpathEvaluatesTo("text HD-HTML","string(/*[local-name()='metadata']/*[local-name()='properties']/*[local-name()='filter-capabilities'])", metadataDocument);
        assertXpathEvaluatesTo("815","string(/*[local-name()='metadata']/*[local-name()='properties']/*[local-name()='size'])", metadataDocument);

        docMgr.delete(docId);
    }
  }

  @Test
  public void test_issue_758() {
   BinaryDocumentManager docMgr = Common.client.newBinaryDocumentManager();
   DocumentWriteSet writeset = docMgr.newWriteSet();
   FileHandle h1 = new FileHandle(new File(
     "../marklogic-client-api-functionaltests/src/test/java/com/marklogic" +
       "/client" +
       "/functionaltest/data" +
       "/Sega-4MB.jpg"));
   String uri = "BinaryDocumentTest_" + new Random().nextInt(10000) + "/" + "Sega-4MB.jpg";
   writeset.add(uri, h1);
   docMgr.write(writeset);
   DocumentPage page = docMgr.read(uri);
   DocumentRecord rec = page.next();
   assertNotNull(rec);
   assertEquals(rec.getFormat(),Format.BINARY);
  }
}
