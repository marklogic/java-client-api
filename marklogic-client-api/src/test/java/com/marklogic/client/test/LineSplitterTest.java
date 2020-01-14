package com.marklogic.client.test;

import com.marklogic.client.datamovement.LineSplitter;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.StringHandle;
import org.junit.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;

import static org.junit.Assert.*;

public class LineSplitterTest {
    static final private String jsonlFile = "src/test/resources/data" + File.separator + "line-delimited.jsonl";
    static final private String jsonlGzipFile = "src/test/resources/data" + File.separator + "line-delimited.jsonl.gz";
    static final private String xmlFile = "src/test/resources/data" + File.separator + "line-delimited.txt";

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

}
