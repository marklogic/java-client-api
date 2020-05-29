package com.marklogic.client.test.datamovement;

import com.marklogic.client.datamovement.Splitter;
import com.marklogic.client.datamovement.ZipSplitter;
import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.client.io.BytesHandle;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.junit.Assert.*;

public class ZipSplitterTest {
    static final private String zipFile = "src/test/resources/data" + File.separator + "pathSplitter/files.zip";

    @Test
    public void testSplitter() throws Exception {

        ZipSplitter splitter = new ZipSplitter();
        splitter.setEntryFilter(x -> x.getSize() > 50 ? true : false );
        Stream<BytesHandle> contentStream = splitter.split(new ZipInputStream(new FileInputStream(zipFile)));
        assertNotNull(contentStream);

        BytesHandle[] bytesResult = contentStream.toArray(size -> new BytesHandle[size]);
        assertNotNull(bytesResult);
        assertEquals(bytesResult.length, 2);

        ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFile));
        ZipEntry zipEntry = null;

        for (int i = 0; (zipEntry = zipInputStream.getNextEntry()) != null && i < bytesResult.length; i++) {
            assertNotNull(bytesResult[i].get());
            checkContent(zipInputStream, zipEntry, new String(bytesResult[i].get()));
        }
    }

    @Test
    public void testSplitterWrite() throws Exception {

        ZipSplitter splitter = new ZipSplitter();
        Stream<DocumentWriteOperation> contentStream =
                splitter.splitWriteOperations(new ZipInputStream(new FileInputStream(zipFile)));
        assertNotNull(contentStream);

        ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFile));
        ZipEntry zipEntry = null;

        Iterator<DocumentWriteOperation> itr = contentStream.iterator();
        while (itr.hasNext() && ((zipEntry = zipInputStream.getNextEntry()) != null)) {
            DocumentWriteOperation docOp = itr.next();
            assertNotNull(docOp.getUri());
            String uri1 = docOp.getUri();
            String uri2 = zipEntry.getName();

            assertNotNull(docOp.getContent());
            String docOpContent = docOp.getContent().toString();
            checkContent(zipInputStream, zipEntry, docOpContent);
        }
    }

    @Test
    public void testSplitterWriteWithInputName() throws Exception {

        ZipSplitter splitter = new ZipSplitter();
        Stream<DocumentWriteOperation> contentStream =
                splitter.splitWriteOperations(new ZipInputStream(new FileInputStream(zipFile)), "ZipFile.zip");
        Splitter.UriMaker uriMaker = splitter.getUriMaker();
        uriMaker.setInputAfter("/sytemPath/");
        uriMaker.setInputName("NewZipFile.zip");
        assertNotNull(contentStream);

        ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFile));
        ZipEntry zipEntry = null;

        Iterator<DocumentWriteOperation> itr = contentStream.iterator();
        while (itr.hasNext() && ((zipEntry = zipInputStream.getNextEntry()) != null)) {
            DocumentWriteOperation docOp = itr.next();
            assertNotNull(docOp.getUri());

            assertNotNull(docOp.getContent());
            String docOpContent = docOp.getContent().toString();
            checkContent(zipInputStream, zipEntry, docOpContent);
        }
    }

    private void checkContent(ZipInputStream zipInputStream,
                              ZipEntry zipEntry,
                              String unzippedContent) throws Exception {

        byte[] originalFileBytes = new byte[(int) zipEntry.getSize()];
        zipInputStream.read(originalFileBytes, 0, originalFileBytes.length);
        String originalFileContent = new String(originalFileBytes);
        assertEquals(unzippedContent, originalFileContent);
    }

}
