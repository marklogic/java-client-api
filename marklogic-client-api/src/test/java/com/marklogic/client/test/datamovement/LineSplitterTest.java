package com.marklogic.client.test.datamovement;

import com.marklogic.client.datamovement.LineSplitter;
import com.marklogic.client.datamovement.Splitter;
import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.StringHandle;
import org.junit.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;

import static org.junit.Assert.*;

public class LineSplitterTest {
    static final private String jsonlFile = "src/test/resources/data" + File.separator + "pathSplitter/line-delimited.jsonl";
    static final private String jsonlGzipFile = "src/test/resources/data" + File.separator + "pathSplitter/line-delimited.jsonl.gz";
    static final private String xmlFile = "src/test/resources/data" + File.separator + "pathSplitter/line-delimited.txt";

    @Test
    public void testSplitter() throws Exception {
        LineSplitter splitter = new LineSplitter();
        Stream<StringHandle> contentStream = splitter.split(new FileInputStream(jsonlFile));
        assertNotNull(contentStream);

        String[] originalResult = Files.lines(Paths.get(jsonlFile))
                                    .toArray(size -> new String[size]);

        checkContent(contentStream, splitter.getFormat(), originalResult);
    }

    @Test
    public void testSplitterGzip() throws Exception {
        LineSplitter splitter = new LineSplitter();
        GZIPInputStream gzipStream = new GZIPInputStream(new FileInputStream(jsonlGzipFile));
        Stream<StringHandle> contentStream = splitter.split(gzipStream);
        assertNotNull(contentStream);

        gzipStream = new GZIPInputStream(new FileInputStream(jsonlGzipFile));
        String[] originalResult = new BufferedReader(new InputStreamReader(gzipStream))
                                    .lines()
                                    .toArray(size -> new String[size]);

        checkContent(contentStream, splitter.getFormat(), originalResult);
    }

    @Test
    public void testSplitterXML() throws Exception {
        LineSplitter splitter = new LineSplitter();
        splitter.setFormat(Format.XML);
        Stream<StringHandle> contentStream = splitter.split(new FileInputStream(xmlFile));
        assertNotNull(contentStream);

        String[] originalResult = Files.lines(Paths.get(xmlFile))
                                    .toArray(size -> new String[size]);

        checkContent(contentStream, splitter.getFormat(), originalResult);
    }

    private void checkContent(Stream<StringHandle> contentStream,
                                 Format format,
                                 String[] originalResult) {

        StringHandle[] result = contentStream.toArray(size -> new StringHandle[size]);
        assertNotNull(result);

        assertEquals(result.length, originalResult.length);
        assertEquals(result[0].getFormat(), format);
        for (int i = 0; i < result.length; i++) {
            assertNotNull(result[i].get());
            assertEquals(result[i].get(), originalResult[i]);
        }
    }

    @Test
    public void testSplitterDocumentWriteOperation() throws Exception {
        LineSplitter splitter = new LineSplitter();
        splitter.setFormat(Format.XML);
        Stream<DocumentWriteOperation> contentStream = splitter.splitWriteOperations(new FileInputStream(xmlFile), "line-delimited.txt");
        Splitter.UriMaker uriMaker = splitter.getUriMaker();
        uriMaker.setInputAfter("/test/");
        assertNotNull(contentStream);

        String[] originalResult = Files.lines(Paths.get(xmlFile))
                .toArray(size -> new String[size]);

        Iterator<DocumentWriteOperation> itr = contentStream.iterator();
        int i = 0;
        while (itr.hasNext()) {
            DocumentWriteOperation docOp = itr.next();
            String uri = docOp.getUri();
            assertNotNull(docOp.getUri());

            assertNotNull(docOp.getContent());
            String docOpContent = docOp.getContent().toString();
            assertEquals(docOpContent, originalResult[i]);
            i++;
        }

        assertEquals(4, splitter.getCount());
    }

}
