package com.marklogic.client.test;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.datamovement.LineSplitter;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.StringHandle;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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
    private DatabaseClient client;

    @Before
    public void setUp() {
        client = DatabaseClientFactory.newClient("localhost", 8012,
                new DatabaseClientFactory.DigestAuthContext("rest-admin", "x"));
    }

    @Test
    public void testSplitter() throws Exception {
        LineSplitter splitter = new LineSplitter();
        Stream<StringHandle> contentStream = splitter.split(new FileInputStream(jsonlFile));
        assertNotNull(contentStream);

        StringHandle[] result = contentStream.toArray(size -> new StringHandle[size]);
        assertNotNull(result);
        assertEquals(result.length, Files.lines(Paths.get(jsonlFile)).count());

        String[] originalResult = Files.lines(Paths.get(jsonlFile))
                                    .toArray(size -> new String[size]);

        for (int i = 0; i < result.length && i < originalResult.length; i++) {
            assertNotNull(result[i].get());
            assertEquals(result[i].get(), originalResult[i]);
        }
    }

    @Test
    public void testSplitterGzip() throws Exception {
        LineSplitter splitter = new LineSplitter();
        GZIPInputStream gzipStream = new GZIPInputStream(new FileInputStream(jsonlGzipFile));
        Stream<StringHandle> contentStream = splitter.split(gzipStream);
        assertNotNull(contentStream);

        StringHandle[] result = contentStream.toArray(size -> new StringHandle[size]);
        assertNotNull(result);

        gzipStream = new GZIPInputStream(new FileInputStream(jsonlGzipFile));
        String[] originalResult = new BufferedReader(new InputStreamReader(gzipStream))
                                    .lines()
                                    .toArray(size -> new String[size]);
        assertEquals(result.length, originalResult.length);

        for (int i = 0; i < result.length && i < originalResult.length; i++) {
            assertNotNull(result[i].get());
            assertEquals(result[i].get(), originalResult[i]);
        }
    }

    @Test
    public void testSplitterXML() throws Exception {
        LineSplitter splitter = new LineSplitter();
        splitter.setFormat(Format.XML);
        Stream<StringHandle> contentStream = splitter.split(new FileInputStream(xmlFile));
        assertNotNull(contentStream);

        StringHandle[] result = contentStream.toArray(size -> new StringHandle[size]);
        assertNotNull(result);
        assertEquals(result.length, Files.lines(Paths.get(xmlFile)).count());

        String[] originalResult = Files.lines(Paths.get(xmlFile))
                                    .toArray(size -> new String[size]);
        assertEquals(result[0].getFormat(), Format.XML);
        for (int i = 0; i < result.length && i < originalResult.length; i++) {
            assertNotNull(result[i].get());
            assertEquals(result[i].get(), originalResult[i]);
        }
    }

    @After
    public void closeSetUp() {
        client.release();
    }
}
