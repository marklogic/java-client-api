package com.marklogic.client.test.datamovement;

import com.marklogic.client.datamovement.ZipSplitter;
import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.client.io.BytesHandle;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.junit.Assert.*;

public class ZipSplitterTest {
    private static final String zipFile = "src/test/resources/data" + File.separator + "pathSplitter/files.zip";

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
    public void testSplitterWriteWithCustomUriMaker() throws Exception {
        String[] expectedURIs = {
                "/sytemPath/NewZipFile/file11_abcd.xml",
                "/sytemPath/NewZipFile/file22_abcd.json",
                "/sytemPath/NewZipFile/file33_abcd.txt"};

        ZipSplitter splitter = new ZipSplitter();
        ZipSplitter.UriMaker uriMaker = new UriMakerTest();
        uriMaker.setInputAfter("/sytemPath/");
        uriMaker.setInputName("NewZipFile");
        splitter.setUriMaker(uriMaker);
        Stream<DocumentWriteOperation> contentStream =
                splitter.splitWriteOperations(new ZipInputStream(new FileInputStream(zipFile)));
        assertNotNull(contentStream);

        ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFile));
        ZipEntry zipEntry = null;

        Iterator<DocumentWriteOperation> itr = contentStream.iterator();
        int i = 0;
        while (itr.hasNext() && ((zipEntry = zipInputStream.getNextEntry()) != null)) {
            DocumentWriteOperation docOp = itr.next();
            String uri = docOp.getUri();
            assertEquals(docOp.getUri(), expectedURIs[i]);

            assertNotNull(docOp.getContent());
            String docOpContent = docOp.getContent().toString();
            checkContent(zipInputStream, zipEntry, docOpContent);
            i++;
        }
    }

    private class UriMakerTest implements ZipSplitter.UriMaker {
        private String inputAfter;
        private String inputName;
        private Pattern extensionRegex = Pattern.compile("^(.+)\\.([^.]+)$");

        @Override
        public String getInputAfter() {
            return this.inputAfter;
        }

        @Override
        public void setInputAfter(String base) {
            this.inputAfter = base;
        }

        @Override
        public String getInputName() {
            return this.inputName;
        }

        @Override
        public void setInputName(String name) {
            this.inputName = name;
        }

        @Override
        public String makeUri(long num, String entryName, BytesHandle handle) {
            StringBuilder uri = new StringBuilder();
            String randomUUIDForTest = "abcd";

            Matcher matcher = extensionRegex.matcher(entryName);
            matcher.find();
            String name = matcher.group(1);
            String extension = matcher.group(2);

            if (getInputAfter() != null && getInputAfter().length() != 0) {
                uri.append(getInputAfter());
            }

            if (getInputName() != null && getInputName().length() != 0) {
                uri.append(getInputName());
            }

            uri.append("/").append(name);
            uri.append(num).append("_").append(randomUUIDForTest).append(".").append(extension);
            return uri.toString();
        }
    }

    @Test
    public void testSplitterWriteWithoutInputNameWithDefaultUriMaker() throws Exception {
        String[] names = {"/file11", "/file22", "/file33"};
        String[] extensions = {"xml", "json", "txt"};
        ZipSplitter splitter = new ZipSplitter();
        Stream<DocumentWriteOperation> contentStream =
                splitter.splitWriteOperations(new ZipInputStream(new FileInputStream(zipFile)));
        assertNotNull(contentStream);

        ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFile));
        ZipEntry zipEntry = null;

        Iterator<DocumentWriteOperation> itr = contentStream.iterator();
        int i = 0;
        while (itr.hasNext() && ((zipEntry = zipInputStream.getNextEntry()) != null)) {
            DocumentWriteOperation docOp = itr.next();
            String uri = docOp.getUri();
            assertNotNull(docOp.getUri());
            assertTrue(docOp.getUri().startsWith(names[i]));
            assertTrue(docOp.getUri().endsWith(extensions[i]));

            assertNotNull(docOp.getContent());
            String docOpContent = docOp.getContent().toString();
            checkContent(zipInputStream, zipEntry, docOpContent);
            i++;
        }
    }

    @Test
    public void testSplitterWriteWithInputNameWithDefaultUriMaker() throws Exception {
        String[] names = {"ZipFile/file11", "ZipFile/file22", "ZipFile/file33"};
        String[] extensions = {"xml", "json", "txt"};
        ZipSplitter splitter = new ZipSplitter();
        Stream<DocumentWriteOperation> contentStream =
                splitter.splitWriteOperations(new ZipInputStream(new FileInputStream(zipFile)), "ZipFile.zip");
        assertNotNull(contentStream);

        ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFile));
        ZipEntry zipEntry = null;

        Iterator<DocumentWriteOperation> itr = contentStream.iterator();
        int i = 0;
        while (itr.hasNext() && ((zipEntry = zipInputStream.getNextEntry()) != null)) {
            DocumentWriteOperation docOp = itr.next();
            String uri = docOp.getUri();
            assertNotNull(docOp.getUri());
            assertTrue(docOp.getUri().startsWith(names[i]));
            assertTrue(docOp.getUri().endsWith(extensions[i]));

            assertNotNull(docOp.getContent());
            String docOpContent = docOp.getContent().toString();
            checkContent(zipInputStream, zipEntry, docOpContent);
            i++;
        }
    }

    @Test
    public void testSplitterWriteWithUriTransformer() throws Exception {
        String[] expected = {"/Test/file1.xml", "/Test/file2.json", "/Test/file3.txt"};
        ZipSplitter splitter = new ZipSplitter();
        Function<String, String> uriTransformer = uri -> "/Test/" + uri;
        splitter.setUriTransformer(uriTransformer);
        Stream<DocumentWriteOperation> contentStream =
                splitter.splitWriteOperations(new ZipInputStream(new FileInputStream(zipFile)), "ZipFile.zip");
        assertNotNull(contentStream);

        ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFile));
        ZipEntry zipEntry = null;

        Iterator<DocumentWriteOperation> itr = contentStream.iterator();
        int i = 0;
        while (itr.hasNext() && ((zipEntry = zipInputStream.getNextEntry()) != null)) {
            DocumentWriteOperation docOp = itr.next();
            String uri = docOp.getUri();
            assertEquals(docOp.getUri(), expected[i]);

            assertNotNull(docOp.getContent());
            String docOpContent = docOp.getContent().toString();
            checkContent(zipInputStream, zipEntry, docOpContent);
            i++;
        }
        assertEquals(i, 3);
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
