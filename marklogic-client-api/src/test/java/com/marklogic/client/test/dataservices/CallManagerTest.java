/*
 * Copyright 2018 MarkLogic Corporation
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
package com.marklogic.client.test.dataservices;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.dataservices.CallManager;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.impl.NodeConverter;
import com.marklogic.client.io.*;
import com.marklogic.client.io.marker.*;
import com.marklogic.client.query.DeleteQueryDefinition;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.test.Common;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.bind.DatatypeConverter;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stax.StAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.time.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.Assert.*;

public class CallManagerTest {
    private final static String ENDPOINT_DIRECTORY = "/javaApi/test/callManager/";

    private static ObjectMapper objectMapper = new ObjectMapper();
    private static JsonFactory  jsonFactory  = new JsonFactory();

    private final static Map<String, Format> NODE_FORMATS = new HashMap<>();

    private DatabaseClient db      = Common.connect();
    private CallManager    callMgr = CallManager.on(db);

    private JacksonHandle serviceHandle;
    {
        ObjectNode servicedef = objectMapper.createObjectNode();
        servicedef.put("endpointDirectory", ENDPOINT_DIRECTORY);
        serviceHandle = new JacksonHandle(servicedef);
    }

    static Map<String, JsonNode> endpointdefs = new HashMap<>();

    @BeforeClass
    public static void setup() {
        NODE_FORMATS.put("array",          Format.JSON);
        NODE_FORMATS.put("binaryDocument", Format.BINARY);
        NODE_FORMATS.put("jsonDocument",   Format.JSON);
        NODE_FORMATS.put("object",         Format.JSON);
        NODE_FORMATS.put("textDocument",   Format.TEXT);
        NODE_FORMATS.put("xmlDocument",    Format.XML);

        DatabaseClient adminClient = Common.newServerAdminClient("java-unittest-modules");
        JSONDocumentManager docMgr = adminClient.newJSONDocumentManager();

        DocumentMetadataHandle docMeta = new DocumentMetadataHandle();

        docMeta.getPermissions().add("rest-reader", DocumentMetadataHandle.Capability.EXECUTE);

        EndpointSetup.setupEndpointMultipleRequired(docMgr, docMeta, "boolean");
        EndpointSetup.setupEndpointMultipleRequired(docMgr, docMeta, "date");
        EndpointSetup.setupEndpointMultipleRequired(docMgr, docMeta, "dateTime");
        EndpointSetup.setupEndpointMultipleRequired(docMgr, docMeta, "dayTimeDuration");
        EndpointSetup.setupEndpointMultipleRequired(docMgr, docMeta, "decimal");
        EndpointSetup.setupEndpointMultipleRequired(docMgr, docMeta, "double");
        EndpointSetup.setupEndpointMultipleRequired(docMgr, docMeta, "float");
        EndpointSetup.setupEndpointMultipleRequired(docMgr, docMeta, "int");
        EndpointSetup.setupEndpointMultipleRequired(docMgr, docMeta, "long");
        EndpointSetup.setupEndpointMultipleRequired(docMgr, docMeta, "string");
        EndpointSetup.setupEndpointMultipleRequired(docMgr, docMeta, "time");
        EndpointSetup.setupEndpointMultipleRequired(docMgr, docMeta, "unsignedInt");
        EndpointSetup.setupEndpointMultipleRequired(docMgr, docMeta, "unsignedLong");

        EndpointSetup.setupEndpointMultipleRequired(docMgr, docMeta, "array");
        EndpointSetup.setupEndpointMultipleRequired(docMgr, docMeta, "binaryDocument");
        EndpointSetup.setupEndpointMultipleRequired(docMgr, docMeta, "jsonDocument");
        EndpointSetup.setupEndpointMultipleRequired(docMgr, docMeta, "object");
        EndpointSetup.setupEndpointMultipleRequired(docMgr, docMeta, "textDocument");
        EndpointSetup.setupEndpointMultipleRequired(docMgr, docMeta, "xmlDocument");

        EndpointSetup.setupEndpointSingleRequired(docMgr, docMeta, "singleAtomic", "dateTime");
        EndpointSetup.setupEndpointSingleRequired(docMgr, docMeta, "singleNode", "object");

        EndpointSetup.setupEndpointSingleNulled(docMgr, docMeta, "nullAtomic", "decimal");
        EndpointSetup.setupEndpointSingleNulled(docMgr, docMeta, "nullNode", "xmlDocument");

        EndpointSetup.setupEndpointMultipleNulled(docMgr, docMeta, "multipleNullAtomic", "float");
        EndpointSetup.setupEndpointMultipleNulled(docMgr, docMeta, "multipleNullNode", "textDocument");

        EndpointSetup.setupTwoParamEndpoint(docMgr, docMeta, "twoAtomic", "date", "unsignedLong");
        EndpointSetup.setupTwoParamEndpoint(docMgr, docMeta, "twoNode", "array", "textDocument");
        EndpointSetup.setupTwoParamEndpoint(docMgr, docMeta, "twoMixed", "time", "textDocument");

        EndpointSetup.setupParamNoReturnEndpoint(docMgr, docMeta, "paramNoReturn", "double");
        EndpointSetup.setupNoParamReturnEndpoint(docMgr, docMeta, "noParamReturn", "double", "5.6");
        EndpointSetup.setupNoParamNoReturnEndpoint(docMgr, docMeta, "noParamNoReturn");

        adminClient.release();
    }
    @AfterClass
    public static void teardown() {
        DatabaseClient adminClient = Common.newServerAdminClient("java-unittest-modules");

        QueryManager queryMgr = adminClient.newQueryManager();
        DeleteQueryDefinition deletedef = queryMgr.newDeleteDefinition();
        deletedef.setDirectory(ENDPOINT_DIRECTORY);
        queryMgr.delete(deletedef);

        adminClient.release();
    }

    @Test
    public void testBadParamName() {
        String functionName = "boolean";

        CallManager.CallableEndpoint callableEndpoint = makeCallableEndpoint(functionName);

        CallManager.ManyCaller<Boolean> caller = makeManyCaller(callableEndpoint, Boolean.class);

        boolean threwException = false;
        try {
            caller.args().param("invalidParamName", new Boolean[]{true, false});
        } catch (IllegalArgumentException ex) {
            threwException = true;
        }
        assertTrue("no exception for parameter with bad name", threwException);
    }
    @Test
    public void testBadParamAtomicType() {
        String functionName = "int";

        CallManager.CallableEndpoint callableEndpoint = makeCallableEndpoint(functionName);

        CallManager.ManyCaller<Integer> caller = makeManyCaller(callableEndpoint, Integer.class);

        boolean threwException = false;
        try {
            caller.args().param("param2", Integer.valueOf(5));
        } catch (IllegalArgumentException ex) {
            threwException = true;
        }
        assertTrue("no exception for parameter with bad atomic data type", threwException);
    }
    @Test
    public void testBadParamNodeType() {
        String functionName = "jsonDocument";

        CallManager.CallableEndpoint callableEndpoint = makeCallableEndpoint(functionName );

        CallManager.ManyCaller<String> caller = makeManyCaller(callableEndpoint, String.class);

        boolean threwException = false;
        try {
            caller.args().param("param1",
                    CallManagerTest.document("<root><child>text1</child></root>")
            );
        } catch (IllegalArgumentException ex) {
            threwException = true;
        }
        assertTrue("no exception for parameter with bad node data type", threwException);
    }
    @Test
    public void testBadParamMultiplicity() {
        String functionName = "singleAtomic";

        CallManager.CallableEndpoint callableEndpoint = makeCallableEndpoint(functionName);

        CallManager.OneCaller<Date> caller = makeOneCaller(callableEndpoint, Date.class);

        boolean threwException = false;
        try {
            caller.args().param("param1", new Date[]{
                    DatatypeConverter.parseDateTime("2018-01-02T10:09:08").getTime(),
                    DatatypeConverter.parseDateTime("2018-01-02T11:10:09.867Z").getTime()
            });
        } catch (IllegalArgumentException ex) {
            threwException = true;
        }
        assertTrue("no exception for parameter with bad multiplicity", threwException);
    }
    @Test
    public void testBadParamOptionality() {
        String functionName = "boolean";

        CallManager.CallableEndpoint callableEndpoint = makeCallableEndpoint(functionName);

        CallManager.ManyCaller<Boolean> caller = makeManyCaller(callableEndpoint, Boolean.class);

        boolean threwException = false;
        try {
            caller.call();
        } catch (IllegalArgumentException ex) {
            threwException = true;
        }
        assertTrue("no exception for parameter with bad optionality", threwException);
    }
    @Test
    public void testBadParamSession() {
        String functionName = "boolean";

        CallManager.CallableEndpoint callableEndpoint = makeCallableEndpoint(functionName);

        CallManager.ManyCaller<Boolean> caller = makeManyCaller(callableEndpoint, Boolean.class);

        boolean threwException = false;
        try {
            caller.args(callMgr.newSessionState());
        } catch (IllegalArgumentException ex) {
            threwException = true;
        }
        assertTrue("no exception for bad session", threwException);
    }
    @Test
    public void testBadReturnAtomicType() {
        String functionName = "boolean";

        CallManager.CallableEndpoint callableEndpoint = makeCallableEndpoint(functionName);

        boolean threwException = false;
        try {
            makeManyCaller(callableEndpoint, Integer.class);
        } catch (IllegalArgumentException ex) {
            threwException = true;
        }
        assertTrue("no exception for return with bad atomic data type", threwException);
    }
    @Test
    public void testBadReturnNodeType() {
        String functionName = "jsonDocument";

        CallManager.CallableEndpoint callableEndpoint = makeCallableEndpoint(functionName);

        boolean threwException = false;
        try {
            makeManyCaller(callableEndpoint, Document.class);
        } catch (IllegalArgumentException ex) {
            threwException = true;
        }
        assertTrue("no exception for return with bad node data type", threwException);
    }
    @Test
    public void testBadReturnMultiplicity() {
        String functionName = "boolean";

        CallManager.CallableEndpoint callableEndpoint = makeCallableEndpoint(functionName);

        boolean threwException = false;
        try {
            makeOneCaller(callableEndpoint, Boolean.class);
        } catch (IllegalArgumentException ex) {
            threwException = true;
        }
        assertTrue("no exception for return with bad multiplicity", threwException);
    }
    @Test
    public void testBadReturnOptionality() {
        String functionName = "boolean";

        CallManager.CallableEndpoint callableEndpoint = makeCallableEndpoint(functionName);

        boolean threwException = false;
        try {
            callableEndpoint.returningNone();
        } catch (IllegalArgumentException ex) {
            threwException = true;
        }
        assertTrue("no exception for return with bad optionality", threwException);
    }


    @Test
    public void testBoolean() {
        String functionName = "boolean";

        CallManager.CallableEndpoint callableEndpoint = makeCallableEndpoint(functionName);

        Boolean[] values = new Boolean[]{true, false};
        CallManager.ManyCaller<Boolean> caller = makeManyCaller(callableEndpoint, Boolean.class);
        testCall(functionName, caller, caller.args().param("param1", values), values);

        testAtomicCall(functionName, callableEndpoint, stringify(values));
    }
    @Test
    public void testDate() {
        String functionName = "date";

        CallManager.CallableEndpoint callableEndpoint = makeCallableEndpoint(functionName);

        LocalDate[] values = new LocalDate[]{LocalDate.parse("2018-01-02"), LocalDate.parse("2018-02-03")};
        CallManager.ManyCaller<LocalDate> caller =
                makeManyCaller(callableEndpoint, LocalDate.class);
        testCall(functionName, caller, caller.args().param("param1", values), values);

        testAtomicCall(functionName, callableEndpoint, stringify(values));
    }
    @Test
    public void testTwoAtomic() {
        String functionName = "twoAtomic";

        CallManager.CallableEndpoint callableEndpoint = makeCallableEndpoint(functionName);

        LocalDate value = LocalDate.parse("2018-01-02");
        CallManager.OneCaller<LocalDate> caller = makeOneCaller(callableEndpoint, LocalDate.class);
        testCall(functionName, caller,
                caller.args()
                      .param("param1", value)
                      .param("param2", new Long[]{5l, 6l}),
                value);
    }
    @Test
    public void testDateTime() {
        String functionName = "dateTime";

        CallManager.CallableEndpoint callableEndpoint = makeCallableEndpoint(functionName);

        Date[] values = new Date[]{
                DatatypeConverter.parseDateTime("2018-01-02T10:09:08").getTime(),
                DatatypeConverter.parseDateTime("2018-01-02T11:10:09.867Z").getTime()
        };
        CallManager.ManyCaller<Date> caller = makeManyCaller(callableEndpoint, Date.class);
        testCall(functionName, caller, caller.args().param("param1", values), values);

        LocalDateTime[] values2 = new LocalDateTime[]{
                LocalDateTime.parse("2018-01-02T10:09:08"), LocalDateTime.parse("2018-01-02T11:10:09.867")
        };
        CallManager.ManyCaller<LocalDateTime> caller2 = makeManyCaller(callableEndpoint, LocalDateTime.class);
        testCall(functionName, caller2, caller2.args().param("param1", values2), values2);

        OffsetDateTime[] values3 = new OffsetDateTime[]{
                OffsetDateTime.parse("2018-01-02T10:09:08+07:00"), OffsetDateTime.parse("2018-01-02T11:10:09.867Z")
        };
        CallManager.ManyCaller<OffsetDateTime> caller3 = makeManyCaller(callableEndpoint, OffsetDateTime.class);
        testCall(functionName, caller3, caller3.args().param("param1", values3), values3);

        testAtomicCall(functionName, callableEndpoint, stringify(values3));
    }
    @Test
    public void testSingleAtomic() throws ParseException {
        String functionName = "singleAtomic";

        CallManager.CallableEndpoint callableEndpoint = makeCallableEndpoint(functionName);

        Date value = DatatypeConverter.parseDateTime("2018-01-02T10:09:08").getTime();
        CallManager.OneCaller<Date> caller = makeOneCaller(callableEndpoint, Date.class);
        testCall(functionName, caller, caller.args().param("param1", value), value);

        LocalDateTime value2 = LocalDateTime.parse("2018-01-02T10:09:08");
        CallManager.OneCaller<LocalDateTime> caller2 = makeOneCaller(callableEndpoint, LocalDateTime.class);
        testCall(functionName, caller2, caller2.args().param("param1", value2), value2);

        OffsetDateTime value3 = OffsetDateTime.parse("2018-01-02T10:09:08+07:00");
        CallManager.OneCaller<OffsetDateTime> caller3 = makeOneCaller(callableEndpoint, OffsetDateTime.class);
        testCall(functionName, caller3, caller3.args().param("param1", value3), value3);

        testAtomicCall(functionName, callableEndpoint, value3.toString());
    }
    @Test
    public void testDayTimeDuration() {
        String functionName = "dayTimeDuration";

        CallManager.CallableEndpoint callableEndpoint = makeCallableEndpoint(functionName);

        Duration[] values = new Duration[]{Duration.parse("P3DT4H5M6S"), Duration.parse("PT5H6M7S")};
        CallManager.ManyCaller<Duration> caller = makeManyCaller(callableEndpoint, Duration.class);
        testCall(functionName, caller, caller.args().param("param1", values), values);

        Stream<? extends String> output = callableEndpoint
                .returningMany(String.class)
                .call(callableEndpoint.args().param("param1", stringify(values)));
        Duration[] result = output.map(Duration::parse).toArray(size -> new Duration[size]);
        assertArrayEquals("string result not equal to string input for "+functionName, values, result);
    }
    @Test
    public void testDecimal() {
        String functionName = "decimal";

        CallManager.CallableEndpoint callableEndpoint = makeCallableEndpoint(functionName);

        BigDecimal[] values = new BigDecimal[]{new BigDecimal("1.2"), new BigDecimal("3.4")};
        CallManager.ManyCaller<BigDecimal> caller = makeManyCaller(callableEndpoint, BigDecimal.class);
        testCall(functionName, caller, caller.args().param("param1", values), values);

        testAtomicCall(functionName, callableEndpoint, stringify(values));
    }
    @Test
    public void testNullAtomic() {
        String functionName = "nullAtomic";

        CallManager.CallableEndpoint callableEndpoint = makeCallableEndpoint(functionName);

        testCall(functionName, makeOneCaller(callableEndpoint, BigDecimal.class));

        testAtomicCallOne(functionName, callableEndpoint);
    }
    @Test
    public void testDouble() {
        String functionName = "double";

        CallManager.CallableEndpoint callableEndpoint = makeCallableEndpoint(functionName);

        Double[] values = new Double[]{1.2, 3.4};
        CallManager.ManyCaller<Double> caller = makeManyCaller(callableEndpoint, Double.class);
        testCall(functionName, caller, caller.args().param("param1", values), values);

        testAtomicCall(functionName, callableEndpoint, stringify(values));
    }
    @Test
    public void testParamNoReturn() {
        String functionName = "paramNoReturn";

        CallManager.CallableEndpoint callableEndpoint = makeCallableEndpoint(functionName);

        callableEndpoint.returningNone().call(callableEndpoint.args().param("param1", 1.2));
    }
    @Test
    public void testNoParamReturn() {
        String functionName = "noParamReturn";

        CallManager.CallableEndpoint callableEndpoint = makeCallableEndpoint(functionName);

        double result = callableEndpoint.returningOne(Double.class).call();
        assertEquals("result mismatch with generated module for "+functionName, 5.6, result, 0.1);
    }
    @Test
    public void testNoParamNoReturn() {
        String functionName = "noParamNoReturn";

        CallManager.CallableEndpoint callableEndpoint = makeCallableEndpoint(functionName);

        callableEndpoint.returningNone().call();
    }
    @Test
    public void testFloat() {
        String functionName = "float";

        CallManager.CallableEndpoint callableEndpoint = makeCallableEndpoint(functionName);

        Float[] values = new Float[]{1.2f, 3.4f};
        CallManager.ManyCaller<Float> caller = makeManyCaller(callableEndpoint, Float.class);
        testCall(functionName, caller, caller.args().param("param1", values), values);

        testAtomicCall(functionName, callableEndpoint, stringify(values));
    }
    @Test
    public void testMultipleNullAtomic() {
        String functionName = "multipleNullAtomic";

        CallManager.CallableEndpoint callableEndpoint = makeCallableEndpoint(functionName);

        testCall(functionName, makeManyCaller(callableEndpoint, Float.class));

        testAtomicCallMany(functionName, callableEndpoint);
    }
    @Test
    public void testInteger() {
        String functionName = "int";

        CallManager.CallableEndpoint callableEndpoint = makeCallableEndpoint(functionName);

        Integer[] values = new Integer[]{5, 6};
        CallManager.ManyCaller<Integer> caller = makeManyCaller(callableEndpoint, Integer.class);
        testCall(functionName, caller, caller.args().param("param1", values), values);

        testAtomicCall(functionName, callableEndpoint, stringify(values));
    }
    @Test
    public void testLong() {
        String functionName = "long";

        CallManager.CallableEndpoint callableEndpoint = makeCallableEndpoint(functionName);

        Long[] values = new Long[]{5l, 6l};
        CallManager.ManyCaller<Long> caller = makeManyCaller(callableEndpoint, Long.class);
        testCall(functionName, caller, caller.args().param("param1", values), values);

        testAtomicCall(functionName, callableEndpoint, stringify(values));
    }
    @Test
    public void testString() {
        String functionName = "string";

        CallManager.CallableEndpoint callableEndpoint = makeCallableEndpoint(functionName);

        String[] values = new String[]{"abc", "def"};

        testAtomicCall(functionName, callableEndpoint, values);
    }
    @Test
    public void testTime() {
        String functionName = "time";

        CallManager.CallableEndpoint callableEndpoint = makeCallableEndpoint(functionName);

        LocalTime[] values = new LocalTime[]{LocalTime.parse("10:09:08"), LocalTime.parse("11:10:09.867")};
        CallManager.ManyCaller<LocalTime> caller = makeManyCaller(callableEndpoint, LocalTime.class);
        testCall(functionName, caller, caller.args().param("param1", values), values);

        OffsetTime[] values2 = new OffsetTime[]{OffsetTime.parse("10:09:08+07:00"), OffsetTime.parse("11:10:09.867Z")};
        CallManager.ManyCaller<OffsetTime> caller2 = makeManyCaller(callableEndpoint, OffsetTime.class);
        testCall(functionName, caller2, caller2.args().param("param1", values2), values2);

        testAtomicCall(functionName, callableEndpoint, stringify(values2));
    }
    @Test
    public void testTwoMixed() {
        String functionName = "twoMixed";

        CallManager.CallableEndpoint callableEndpoint = makeCallableEndpoint(functionName);

        LocalTime value = LocalTime.parse("10:09:08");
        CallManager.OneCaller<LocalTime> caller = makeOneCaller(callableEndpoint, LocalTime.class);
        testCall(functionName, caller,
                caller.args()
                      .param("param1", value)
                      .param("param2", convert(new String[]{"abc", "def"}, CallManagerTest::inputStream, InputStream.class)),
                value);
    }
    @Test
    public void testUnsignedInteger() {
        String functionName = "unsignedInt";

        CallManager.CallableEndpoint callableEndpoint = makeCallableEndpoint(functionName);

        Integer[] values = new Integer[]{5, 6};
        CallManager.ManyCaller<Integer> caller =makeManyCaller(callableEndpoint, Integer.class);
        testCall(functionName, caller, caller.args().param("param1", values), values);

        testAtomicCall(functionName, callableEndpoint, stringify(values));
    }
    @Test
    public void testUnsignedLong() {
        String functionName = "unsignedLong";

        CallManager.CallableEndpoint callableEndpoint = makeCallableEndpoint(functionName);

        Long[] values = new Long[]{5l, 6l};
        CallManager.ManyCaller<Long> caller = makeManyCaller(callableEndpoint, Long.class);
        testCall(functionName, caller, caller.args().param("param1", values), values);

        testAtomicCall(functionName, callableEndpoint, stringify(values));
    }

    @Test
    public void testBinaryDocument() {
        String functionName = "binaryDocument";

        CallManager.CallableEndpoint callableEndpoint = makeCallableEndpoint(functionName);

        String[] values = new String[]{
            "iVBORw0KGgoAAAANSUhEUgAAAA0AAAATCAYAAABLN4eXAAAAAXNSR0IArs4c6QAAAAZiS0dEAP8A/wD/oL2nkwAAAAlwSFlzAAALEwAACxMBAJqcGAAAAAd0SU1FB9oIEQEjMtAYogQAAAKvSURBVCjPlZLLbhxFAEVPVVdXVz/G8zCOn0CsKGyQkSIIKzas8xfsWbLkp/gJhCKheIlAJDaj2MYez6u7p7vrxQKUPVc6+yOdK77/4cfXQohJqlOVZdmBSpKY6jQKBM45oVMlgHvrvMuNWRljvlNKq69G2YyqLDg4mLE/2yPNYFRWlFXF/nTC2clRWbc7Fss1IcZzqTA8eWY5eu7p1Hv+WvyBVjnGZOQmI9UKISUqSXDO0bS7Tko0xfGSp18kjM7v+P3+NUMr8T5grWMYLCEErHM474khoCw1t78eU/8mEOpjXpxekJUORIZSCbkxSCnRWpPnBikTqbx31E1DjJHpeIzRhnW9xceI857H5Yr1Zku765jf3DIMtlUAIQRCiFhnabsOH1IEAmstAGWRY11ApykmM0oplTKZjNGZREpJoUueHI0ZFRV7exX7+1Nm0yn9YLm5u2fX96lUseLwxQ0vX8H04i2/XP9Et5H44OkHS920hBDo+56u77GDjcrHjvV1ya3TDO2M01mOUAEAhED+R5IkpKmCiFCOjoc/p+xuLbPpCc+P95HaEqIBIhHoB8t2W/PwsKBudl5FH7GxwUYYouJh5ci7nLbtWW02LBaPvLuef1AdrItKKolJpkivwGrG5QxTCsq8pCxLqqrk7PiIwTmW6y0xRCVTSg4vFnz+raM4+5ur1RtSUZHnOUWeMx5VVFWJTlOstfTWRuk96NIyOUgRRc188RZvgRg/3OffjoFESohxUMvmjqufP+X+MqDTU77+5EvMKKBUQpZpijxHSkluDHvjMW8uL79Rnz07bwSyzDLFqCzwDNw/PNI0O9bbhvVmQ7vb0bQdi+Wq327rl+rko8krodKnCHnofJju+r5oupBstg1KJT7Vuruev185O9zVm/WVUmouYoz83/0DxhRmafe2kasAAAAASUVORK5CYII=",
            "aGVsbG8sIHdvcmxk"
        };

        testNode(functionName, callableEndpoint, values);

        BytesHandle[] values2 = convert(values, CallManagerTest::BytesHandle, BytesHandle.class);
        CallManager.ManyCaller<BinaryReadHandle> caller2 = makeManyCaller(callableEndpoint, BinaryReadHandle.class);
        testConvertedCall(
                functionName, caller2, caller2.args().param("param1", values2), values, CallManagerTest::string
        );

/* TODO:
        File value3 = new File("src/test/resources/test.bin");
        CallManager.ManyCaller<File> caller3 = makeManyCaller(callableEndpoint, File.class);
        testConvertedCall(
                functionName, caller3, caller3.args().param("param1", value3), new String[]{CallManagerTest.string(value3)}, CallManagerTest::string
        );
        */
    }
    @Test
    public void testJsonDocument() {
        String functionName = "jsonDocument";

        CallManager.CallableEndpoint callableEndpoint = makeCallableEndpoint(functionName);

        String[] values = new String[]{"{\"root\":{\"child\":\"text1\"}}", "{\"root\":{\"child\":\"text2\"}}"};

        testCharacterNode(functionName, callableEndpoint, values);

        JsonNode[] values2 = convert(values, CallManagerTest::jsonNode, JsonNode.class);
        CallManager.ManyCaller<JsonNode> caller2 = makeManyCaller(callableEndpoint, JsonNode.class);
        testConvertedCall(
                functionName, caller2, caller2.args().param("param1", values2), values, CallManagerTest::string
        );

        JsonParser[] values3 = convert(values, CallManagerTest::jsonParser, JsonParser.class);
        CallManager.ManyCaller<JsonParser> caller3 = makeManyCaller(callableEndpoint, JsonParser.class);
        testConvertedCall(
                functionName, caller3, caller3.args().param("param1", values3), values, CallManagerTest::string
        );

        StringHandle[] values4 = convert(values, StringHandle::new, StringHandle.class);
        CallManager.ManyCaller<JSONReadHandle> caller4 = makeManyCaller(callableEndpoint, JSONReadHandle.class);
        testConvertedCall(
                functionName, caller4, caller4.args().param("param1", values4), values, CallManagerTest::string
        );

/* TODO:
        File value5 = new File("src/test/resources/basic1.json");
        CallManager.ManyCaller<File> caller5 = makeManyCaller(callableEndpoint, File.class);
        testConvertedCall(
                functionName, caller3, caller3.args().param("param1", value5),
                new String[]{CallManagerTest.string(value5)}, CallManagerTest::string
        );
 */
    }
    @Test
    public void testArray() {
        String functionName = "array";

        CallManager.CallableEndpoint callableEndpoint = makeCallableEndpoint(functionName);

        String[] valuesSpaced   = new String[]{"[\"text1\", 1]", "[\"text2\", 2]"};
        String[] valuesUnspaced = new String[]{"[\"text1\",1]", "[\"text2\",2]"};

        testCharacterNode(functionName, callableEndpoint, valuesSpaced);

        JsonNode[] values2 = convert(valuesUnspaced, CallManagerTest::jsonNode, JsonNode.class);
        CallManager.ManyCaller<JsonNode> caller2 = makeManyCaller(callableEndpoint, JsonNode.class);
        testConvertedCall(
                functionName, caller2, caller2.args().param("param1", values2), valuesUnspaced, CallManagerTest::string
        );

        JsonParser[] values3 = convert(valuesUnspaced, CallManagerTest::jsonParser, JsonParser.class);
        CallManager.ManyCaller<JsonParser> caller3 = makeManyCaller(callableEndpoint, JsonParser.class);
        testConvertedCall(
                functionName, caller3, caller3.args().param("param1", values3), valuesUnspaced, CallManagerTest::string
        );

        StringHandle[] values4 = convert(valuesSpaced, StringHandle::new, StringHandle.class);
        CallManager.ManyCaller<JSONReadHandle> caller4 = makeManyCaller(callableEndpoint, JSONReadHandle.class);
        testConvertedCall(
                functionName, caller4, caller4.args().param("param1", values4), valuesSpaced, CallManagerTest::string
        );
    }
    @Test
    public void testTwoNode() {
        String functionName = "twoNode";

        CallManager.CallableEndpoint callableEndpoint = makeCallableEndpoint(functionName);

        String value = "[\"text1\",1]";
        CallManager.OneCaller<JsonNode> caller = makeOneCaller(callableEndpoint, JsonNode.class);
        testConvertedCall(functionName, caller,
                caller.args()
                      .param("param1", jsonNode(value))
                      .param("param2", convert(new String[]{"abc", "def"}, StringReader::new, Reader.class)),
                value,
                CallManagerTest::string
        );
    }
    @Test
    public void testObject() {
        String functionName = "object";

        CallManager.CallableEndpoint callableEndpoint = makeCallableEndpoint(functionName);

        String[] values = new String[]{"{\"root\":{\"child\":\"text1\"}}", "{\"root\":{\"child\":\"text2\"}}"};

        testCharacterNode(functionName, callableEndpoint, values);

        JsonNode[] values2 = convert(values, CallManagerTest::jsonNode, JsonNode.class);
        CallManager.ManyCaller<JsonNode> caller2 = makeManyCaller(callableEndpoint, JsonNode.class);
        testConvertedCall(
                functionName, caller2, caller2.args().param("param1", values2), values, CallManagerTest::string
        );

        JsonParser[] values3 = convert(values, CallManagerTest::jsonParser, JsonParser.class);
        CallManager.ManyCaller<JsonParser> caller3 = makeManyCaller(callableEndpoint, JsonParser.class);
        testConvertedCall(
                functionName, caller3, caller3.args().param("param1", values3), values, CallManagerTest::string
        );

        StringHandle[] values4 = convert(values, StringHandle::new, StringHandle.class);
        CallManager.ManyCaller<JSONReadHandle> caller4 = makeManyCaller(callableEndpoint, JSONReadHandle.class);
        testConvertedCall(
                functionName, caller4, caller4.args().param("param1", values4), values, CallManagerTest::string
        );
    }
    @Test
    public void testSingleNode() {
        String functionName = "singleNode";

        CallManager.CallableEndpoint callableEndpoint = makeCallableEndpoint(functionName);

        String value = "{\"root\":{\"child\":\"text1\"}}";

        testCharacterNode(functionName, callableEndpoint, value);

        JsonNode value2 = jsonNode(value);
        CallManager.OneCaller<JsonNode> caller2 = makeOneCaller(callableEndpoint, JsonNode.class);
        testConvertedCall(
                functionName, caller2, caller2.args().param("param1", value2), value, CallManagerTest::string
        );

        JsonParser value3 = jsonParser(value);
        CallManager.OneCaller<JsonParser> caller3 = makeOneCaller(callableEndpoint, JsonParser.class);
        testConvertedCall(
                functionName, caller3, caller3.args().param("param1", value3), value, CallManagerTest::string
        );

        StringHandle value4 = new StringHandle(value);
        CallManager.OneCaller<JSONReadHandle> caller4 = makeOneCaller(callableEndpoint, JSONReadHandle.class);
        testConvertedCall(
                functionName, caller4, caller4.args().param("param1", value4), value, CallManagerTest::string
        );
    }
    @Test
    public void testTextDocument() {
        String functionName = "textDocument";

        CallManager.CallableEndpoint callableEndpoint = makeCallableEndpoint(functionName);

        String[] values = new String[]{"abc", "def"};

        testCharacterNode(functionName, callableEndpoint, values);

        StringHandle[] values2 = convert(values, StringHandle::new, StringHandle.class);
        CallManager.ManyCaller<TextReadHandle> caller2 = makeManyCaller(callableEndpoint, TextReadHandle.class);
        testConvertedCall(
                functionName, caller2, caller2.args().param("param1", values2), values, CallManagerTest::string
        );

/* TODO:
        File value3 = new File("src/test/resources/hola.txt");
        CallManager.ManyCaller<File> caller3 = makeManyCaller(callableEndpoint, File.class);
        testConvertedCall(
                functionName, caller3, caller3.args().param("param1", value3),
                new String[]{CallManagerTest.string(value3)}, CallManagerTest::string
        );
        */
    }
    @Test
    public void testMultipleNullNode() {
        String functionName = "multipleNullNode";

        CallManager.CallableEndpoint callableEndpoint = makeCallableEndpoint(functionName);

        testCharacterNodeMany(functionName, callableEndpoint);
    }
    @Test
    public void testXmlDocument() {
        String functionName = "xmlDocument";

        CallManager.CallableEndpoint callableEndpoint = makeCallableEndpoint(functionName );

        String[] values = new String[]{"<root><child>text1</child></root>", "<root><child>text2</child></root>"};

        // can't call testCharacterNode() because of the XML prolog

        byte[][] values2 = convert(values, CallManagerTest::bytes, byte[].class);
        CallManager.ManyCaller<byte[]> caller2 = makeManyCaller(callableEndpoint, byte[].class);
        testConvertedCall(
                functionName, caller2, caller2.args().param("param1", values2), values, CallManagerTest::xmlString
        );

        InputStream[] values3 = convert(values, CallManagerTest::inputStream, InputStream.class);
        CallManager.ManyCaller<InputStream> caller3 = makeManyCaller(callableEndpoint, InputStream.class);
        testConvertedCall(
                functionName, caller3, caller3.args().param("param1", values3), values, CallManagerTest::xmlString
        );

        Reader[] values4 = convert(values, StringReader::new, Reader.class);
        CallManager.ManyCaller<Reader> caller4 = makeManyCaller(callableEndpoint, Reader.class);
        testConvertedCall(
                functionName, caller4, caller4.args().param("param1", values4), values, CallManagerTest::xmlString
        );

        CallManager.ManyCaller<String> caller5 = makeManyCaller(callableEndpoint, String.class);
        testConvertedCall(
                functionName, caller5, caller5.args().param("param1", values), values, CallManagerTest::xmlString
        );

        Document[] values6 = convert(values, CallManagerTest::document, Document.class);
        CallManager.ManyCaller<Document> caller6 = makeManyCaller(callableEndpoint, Document.class);
        testConvertedCall(
                functionName, caller6, caller6.args().param("param1", values6), values, CallManagerTest::string
        );

        InputSource[] values7 = convert(values, CallManagerTest::inputSource, InputSource.class);
        CallManager.ManyCaller<InputSource> caller7 = makeManyCaller(callableEndpoint, InputSource.class);
        testConvertedCall(
                functionName, caller7, caller7.args().param("param1", values7), values, CallManagerTest::string
        );

        Source[] values8 = convert(values, CallManagerTest::source, Source.class);
        CallManager.ManyCaller<Source> caller8 = makeManyCaller(callableEndpoint, Source.class);
        testConvertedCall(
                functionName, caller8, caller8.args().param("param1", values8), values, CallManagerTest::string
        );

        XMLEventReader[] values9 = convert(values, CallManagerTest::xmlEventReader, XMLEventReader.class);
        CallManager.ManyCaller<XMLEventReader> caller9 = makeManyCaller(callableEndpoint, XMLEventReader.class);
        testConvertedCall(
                functionName, caller9, caller9.args().param("param1", values9), values, CallManagerTest::string
        );

        XMLStreamReader[] values10 = convert(values, CallManagerTest::xmlStreamReader, XMLStreamReader.class);
        CallManager.ManyCaller<XMLStreamReader> caller10 = makeManyCaller(callableEndpoint, XMLStreamReader.class);
        testConvertedCall(
                functionName, caller10, caller10.args().param("param1", values10), values, CallManagerTest::string
        );

        StringHandle[] values11 = convert(values, StringHandle::new, StringHandle.class);
        CallManager.ManyCaller<XMLReadHandle> caller11 = makeManyCaller(callableEndpoint, XMLReadHandle.class);
        testConvertedCall(
                functionName, caller11, caller11.args().param("param1", values11), values, CallManagerTest::xmlString
        );

/* TODO:
        File value12 = new File("src/test/resources/test.xml");
        CallManager.ManyCaller<File> caller12 = makeManyCaller(callableEndpoint, File.class);
        testConvertedCall(
                functionName, caller3, caller3.args().param("param1", value12),
                new String[]{CallManagerTest.string(value12)}, CallManagerTest::string
        );
        */
    }
    @Test
    public void testNullNode() {
        String functionName = "nullNode";

        CallManager.CallableEndpoint callableEndpoint = makeCallableEndpoint(functionName );

        testCharacterNodeOne(functionName, callableEndpoint);

        testCall(functionName, makeOneCaller(callableEndpoint, String.class));
        testCall(functionName, makeOneCaller(callableEndpoint, Document.class));
        testCall(functionName, makeOneCaller(callableEndpoint, InputSource.class));
        testCall(functionName, makeOneCaller(callableEndpoint, Source.class));
        testCall(functionName, makeOneCaller(callableEndpoint, XMLEventReader.class));
        testCall(functionName, makeOneCaller(callableEndpoint, XMLStreamReader.class));
        testCall(functionName, makeOneCaller(callableEndpoint, XMLReadHandle.class));
    }

    private void testNodeMany(String functionName, CallManager.CallableEndpoint callableEndpoint) {
        testCall(functionName, makeManyCaller(callableEndpoint, byte[].class));

        testCall(functionName, makeManyCaller(callableEndpoint, InputStream.class));
    }
    private void testNodeOne(String functionName, CallManager.CallableEndpoint callableEndpoint) {
        testCall(functionName, makeOneCaller(callableEndpoint, byte[].class));

        testCall(functionName, makeOneCaller(callableEndpoint, InputStream.class));
    }
    private void testNode(String functionName, CallManager.CallableEndpoint callableEndpoint, String value) {
        byte[] value2 = bytes(value);
        CallManager.OneCaller<byte[]> caller2 = makeOneCaller(callableEndpoint, byte[].class);
        testConvertedCall(
                functionName, caller2, caller2.args().param("param1", value2), value, CallManagerTest::string
        );

        InputStream value3 = inputStream(value);
        CallManager.OneCaller<InputStream> caller3 = makeOneCaller(callableEndpoint, InputStream.class);
        testConvertedCall(
                functionName, caller3, caller3.args().param("param1", value3), value, NodeConverter::InputStreamToString
        );
    }
    private void testNode(String functionName, CallManager.CallableEndpoint callableEndpoint, String[] values) {
        byte[][] values2 = convert(values, CallManagerTest::bytes, byte[].class);
        CallManager.ManyCaller<byte[]> caller2 = makeManyCaller(callableEndpoint, byte[].class);
        testConvertedCall(
                functionName, caller2, caller2.args().param("param1", values2), values, CallManagerTest::string
        );

        InputStream[] values3 = convert(values, CallManagerTest::inputStream, InputStream.class);
        CallManager.ManyCaller<InputStream> caller3 = makeManyCaller(callableEndpoint, InputStream.class);
        testConvertedCall(
                functionName, caller3, caller3.args().param("param1", values3), values, NodeConverter::InputStreamToString
        );
    }
    private void testCharacterNodeMany(String functionName, CallManager.CallableEndpoint callableEndpoint) {
        testNodeMany(functionName, callableEndpoint);

        testCall(functionName, makeManyCaller(callableEndpoint, Reader.class));

        testCall(functionName, makeManyCaller(callableEndpoint, String.class));
    }
    private void testCharacterNodeOne(String functionName, CallManager.CallableEndpoint callableEndpoint) {
        testNodeOne(functionName, callableEndpoint);

        testCall(functionName, makeOneCaller(callableEndpoint, Reader.class));

        testCall(functionName, makeOneCaller(callableEndpoint, String.class));
    }
    private void testCharacterNode(String functionName, CallManager.CallableEndpoint callableEndpoint, String value) {
        testNode(functionName, callableEndpoint, value);

        Reader value2 = new StringReader(value);
        CallManager.OneCaller<Reader> caller2 = makeOneCaller(callableEndpoint, Reader.class);
        testConvertedCall(
                functionName, caller2, caller2.args().param("param1", value2), value, NodeConverter::ReaderToString
        );

        CallManager.OneCaller<String> caller = makeOneCaller(callableEndpoint, String.class);
        testCall(functionName, caller, caller.args().param("param1", value), value);
    }
    private void testCharacterNode(String functionName, CallManager.CallableEndpoint callableEndpoint, String[] values) {
        testNode(functionName, callableEndpoint, values);

        Reader[] values2 = convert(values, StringReader::new, Reader.class);
        CallManager.ManyCaller<Reader> caller2 = makeManyCaller(callableEndpoint, Reader.class);
        testConvertedCall(
                functionName, caller2, caller2.args().param("param1", values2), values, NodeConverter::ReaderToString
        );

        CallManager.ManyCaller<String> caller = makeManyCaller(callableEndpoint, String.class);
        testCall(functionName, caller, caller.args().param("param1", values), values);
    }

    private static byte[] bytes(String value) {
        return value.getBytes(Charset.forName("UTF-8"));
    }
    private static BytesHandle BytesHandle(String value) {
        return new BytesHandle(bytes(value));
    }
    private static Document document(String value) {
        return NodeConverter.InputStreamToDocument(inputStream(value));
    }
    private static InputSource inputSource(String value) {
        return NodeConverter.ReaderToInputSource(new StringReader(value));
    }
    private static InputStream inputStream(String value) {
        return new ByteArrayInputStream(bytes(value));
    }
    private static JsonNode jsonNode(String value) {
        try {
            return objectMapper.readTree(value);
        } catch(IOException e) {
//          e.printStackTrace(System.out);
            throw new RuntimeException(e);
        }
    }
    private static JsonParser jsonParser(String value) {
        try {
            return jsonFactory.createParser(value);
        } catch (JsonParseException e) {
//          e.printStackTrace(System.out);
            throw new RuntimeException(e);
        } catch (IOException e) {
//          e.printStackTrace(System.out);
            throw new RuntimeException(e);
        }
    }
    private static Source source(String value) {
        return NodeConverter.ReaderToSource(new StringReader(value));
    }
    private static String string(BinaryReadHandle value) {
        return string((InputStreamHandle) value);
    }
    private static String string(byte[] value) {
        return new String(value, Charset.forName("UTF-8"));
    }
    private static String string(Document value) {
        return string(new DOMSource(value));
    }
    private static String string(File value) {
        try {
            return string(new FileInputStream(value));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    private static String string(InputStreamHandle value) {
        return string(value.get());
    }
    private static String string(InputStream value) {
        return NodeConverter.InputStreamToString(value);
    }
    private static String string(InputSource value) {
        return string(new SAXSource(value));
    }
    private static String string(JsonNode value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
//          e.printStackTrace(System.out);
            throw new RuntimeException(e);
        }
    }
    private static String string(JsonParser value) {
        try {
            StringWriter writer = new StringWriter();
            value.nextToken();
            JsonGenerator generator = jsonFactory.createGenerator(writer);
            generator.copyCurrentStructure(value);
            generator.flush();
            generator.close();
            return writer.toString();
        } catch (IOException e) {
//          e.printStackTrace(System.out);
            throw new RuntimeException(e);
        }
    }
    private static String string(JSONReadHandle value) {
        return string((ReaderHandle) value);
    }
    private static String string(ReaderHandle value) {
        return NodeConverter.ReaderToString(value.get());
    }
    private static String string(Source value) {
            StringWriter writer = new StringWriter();
            transform(value, new StreamResult(writer));
            return writer.toString();
    }
    private static String string(TextReadHandle value) {
        return string((ReaderHandle) value);
    }
    private static String string(XMLEventReader value) {
        try {
            return string(new StAXSource(value));
        } catch (XMLStreamException e) {
//          e.printStackTrace(System.out);
            throw new RuntimeException(e);
        }
    }
    private static String string(XMLStreamReader value) {
        return string(new StAXSource(value));
    }
    private static XMLEventReader xmlEventReader(String value) {
        return NodeConverter.ReaderToXMLEventReader(new StringReader(value));
    }
    private static XMLStreamReader xmlStreamReader(String value) {
        return NodeConverter.ReaderToXMLStreamReader(new StringReader(value));
    }
    private static String xmlString(byte[] value) {
        return string(new StreamSource(new ByteArrayInputStream(value)));
    }
    private static String xmlString(InputStream value) {
        return string(new StreamSource(value));
    }
    private static String xmlString(Reader value) {
        return string(new StreamSource(value));
    }
    private static String xmlString(String value) {
        return string(new StreamSource(new StringReader(value)));
    }
    private static String xmlString(XMLReadHandle value) {
        return xmlString(((ReaderHandle) value).get());
    }

// TODO: initialize once
    private static void transform(Source source, Result result) {
        try {
            transformer().transform(source, result);
        } catch (TransformerException e) {
//          e.printStackTrace(System.out);
            throw new RuntimeException(e);
        }
    }
    private static Transformer transformer() {
        try {
            Transformer transformer = transformerFactory().newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            return transformer;
        } catch (TransformerException e) {
//          e.printStackTrace(System.out);
            throw new RuntimeException(e);
        }
    }
    private static TransformerFactory transformerFactory() {
        TransformerFactory factory = TransformerFactory.newInstance();
        return factory;
    }

    private static <T> T[] convert(String[] values, Function<String, T> converter, Class<T> as) {
        T[] converted = (T[]) Array.newInstance(as, values.length);
        for (int i=0; i < values.length; i++)
            converted[i] = converter.apply(values[i]);
        return converted;
    }

    private CallManager.CallableEndpoint makeCallableEndpoint(String functionName) {
        JsonNode endpointdef = endpointdefs.get(functionName);
        assertNotNull("no endpoint definition found for "+functionName, endpointdef);
        return callMgr.endpoint(serviceHandle, new JacksonHandle(endpointdef), "sjs");
    }
    private <T> CallManager.ManyCaller<T> makeManyCaller(CallManager.CallableEndpoint callableEndpoint, Class<T> as) {
        return callableEndpoint.returningMany(as);
    }
    private <T> CallManager.OneCaller<T> makeOneCaller(CallManager.CallableEndpoint callableEndpoint, Class<T> as) {
        return callableEndpoint.returningOne(as);
    }
    private <T> void testCall(String functionName, CallManager.ManyCaller<T> caller) {
        try {
            Stream<? extends T> result = caller.call();
            assertEquals("result not empty for "+functionName, 0, result.count());
        } catch(Exception e) {
//          e.printStackTrace(System.out);
            fail(e.getClass().getSimpleName()+": "+e.getMessage());
        }
    }
    private <T> void testCall(String functionName, CallManager.OneCaller<T> caller) {
        try {
            T result = caller.call();
            assertNull("result not null for "+functionName, result);
        } catch(Exception e) {
//          e.printStackTrace(System.out);
            fail(e.getClass().getSimpleName()+": "+e.getMessage());
        }
    }
    private <T> void testCall(
            String functionName,
            CallManager.OneCaller<T> caller,
            CallManager.CallArgs args,
            T value
    ) {
        try {
            T result = caller.call(args);
            assertEquals("result not equal to input for "+functionName, value, result);
        } catch(Exception e) {
//          e.printStackTrace(System.out);
            fail(e.getClass().getSimpleName()+": "+e.getMessage());
        }
    }
    private <T> void testCall(
            String functionName,
            CallManager.ManyCaller<T> caller,
            CallManager.CallArgs args,
            T[] values
    ) {
        try {
            Stream<? extends T> output = caller.call(args);
            T[] result = output.toArray(size -> (T[]) Array.newInstance(values.getClass().getComponentType(), size));
            assertArrayEquals("result not equal to input for "+functionName, values, result);
        } catch(Exception e) {
//          e.printStackTrace(System.out);
            fail(e.getClass().getSimpleName()+": "+e.getMessage());
        }
    }
    private <T> void testConvertedCall(
            String functionName,
            CallManager.OneCaller<T> caller,
            CallManager.CallArgs args,
            String value,
            Function<T, String> converter
    ) {
        try {
            T output = caller.call(args);
            String result = converter.apply(output);
            assertEquals("result not equal to input for "+functionName, value, result);
        } catch(Exception e) {
//          e.printStackTrace(System.out);
            fail(e.getClass().getSimpleName()+": "+e.getMessage());
        }
    }
    private <T> void testConvertedCall(
            String functionName,
            CallManager.ManyCaller<T> caller,
            CallManager.CallArgs args,
            String[] values,
            Function<T, String> converter
    ) {
        try {
            Stream<? extends T> output = caller.call(args);
            String[] result = output.map(converter).toArray(size -> new String[size]);
            assertArrayEquals("result not equal to input for "+functionName, values, result);
        } catch(Exception e) {
//            e.printStackTrace(System.out);
            fail(e.getClass().getSimpleName()+": "+e.getMessage());
        }
    }
    private void testAtomicCallMany(
            String functionName,
            CallManager.CallableEndpoint callableEndpoint
    ) {
        try {
            Stream<? extends String> result = callableEndpoint
                    .returningMany(String.class)
                    .call();
            assertEquals("result not empty for "+functionName, 0, result.count());
        } catch(Exception e) {
//          e.printStackTrace(System.out);
            fail(e.getClass().getSimpleName()+": "+e.getMessage());
        }
    }
    private void testAtomicCallOne(
            String functionName,
            CallManager.CallableEndpoint callableEndpoint
    ) {
        try {
            String result = callableEndpoint
                    .returningOne(String.class)
                    .call();
            assertNull("result not null for "+functionName, result);
        } catch(Exception e) {
//          e.printStackTrace(System.out);
            fail(e.getClass().getSimpleName()+": "+e.getMessage());
        }
    }
    private void testAtomicCall(
            String functionName,
            CallManager.CallableEndpoint callableEndpoint,
            String value
    ) {
        try {
            String result = callableEndpoint
                    .returningOne(String.class)
                    .call(callableEndpoint.args().param("param1", value));
            assertEquals("string result not equal to string input for "+functionName, value, result);
        } catch(Exception e) {
//          e.printStackTrace(System.out);
            fail(e.getClass().getSimpleName()+": "+e.getMessage());
        }
    }
    private void testAtomicCall(
            String functionName,
            CallManager.CallableEndpoint callableEndpoint,
            String[] values
    ) {
        try {
            Stream<? extends String> output = callableEndpoint
                    .returningMany(String.class)
                    .call(callableEndpoint.args().param("param1", values));
            String[] result = output.toArray(size -> new String[size]);
            assertArrayEquals("string result not equal to string input for "+functionName, values, result);
        } catch(Exception e) {
//          e.printStackTrace(System.out);
            fail(e.getClass().getSimpleName()+": "+e.getMessage());
        }
    }
    private static String[] stringify(Object[] values) {
        if (values == null)
            return null;
        String[] converted = new String[values.length];
        for (int i=0; i < values.length; i++) {
            Object value = values[i];
            converted[i] = (value == null) ? null : value.toString();
        }
        return converted;
    }
    
    static class EndpointSetup {
    	
        static void setupParamNoReturnEndpoint(
                JSONDocumentManager docMgr, DocumentMetadataHandle docMeta, String functionName, String datatype
        ) {
            JsonNode endpointdef = getEndpointdef(functionName, datatype, null, null, false, false);
            String script = getScript(datatype, null, null, false, false);
            setupEndpoint(docMgr, docMeta, endpointdef, script);
        }
        static void setupNoParamReturnEndpoint(
                JSONDocumentManager docMgr, DocumentMetadataHandle docMeta, String functionName, String datatype, String returnVal
        ) {
            JsonNode endpointdef = getEndpointdef(functionName, null, null, datatype, false, false);
            String script = getScript(null, null, returnVal, false, false);
            setupEndpoint(docMgr, docMeta, endpointdef, script);
        }
        static void setupNoParamNoReturnEndpoint(
                JSONDocumentManager docMgr, DocumentMetadataHandle docMeta, String functionName
        ) {
            JsonNode endpointdef = getEndpointdef(functionName, null, null, null, false, false);
            String script = getScript(null, null, null, false, false);
            setupEndpoint(docMgr, docMeta, endpointdef, script);
        }
        static void setupTwoParamEndpoint(
                JSONDocumentManager docMgr, DocumentMetadataHandle docMeta, String functionName, String datatype, String paramType2
        ) {
            JsonNode endpointdef = getEndpointdef(functionName, datatype, paramType2, datatype, false, false);
            String script = getScript(datatype, paramType2, null, false, false);
            setupEndpoint(docMgr, docMeta, endpointdef, script);
        }
        static void setupEndpointSingleNulled(
                JSONDocumentManager docMgr, DocumentMetadataHandle docMeta, String functionName, String datatype
        ) {
            JsonNode endpointdef = getEndpointdef(functionName, datatype, false, true);
            String script = getScript(datatype, false, true);
            setupEndpoint(docMgr, docMeta, endpointdef, script);
        }
        static void setupEndpointSingleRequired(
                JSONDocumentManager docMgr, DocumentMetadataHandle docMeta, String functionName, String datatype
        ) {
            JsonNode endpointdef = getEndpointdef(functionName, datatype, false, false);
            String script = getScript(datatype, false, false);
            setupEndpoint(docMgr, docMeta, endpointdef, script);
        }
        static void setupEndpointMultipleNulled(
                JSONDocumentManager docMgr, DocumentMetadataHandle docMeta, String functionName, String datatype
        ) {
            JsonNode endpointdef = getEndpointdef(functionName, datatype, true, true);
            String script = getScript(datatype, true, true);
            setupEndpoint(docMgr, docMeta, endpointdef, script);
        }
        static void setupEndpointMultipleRequired(JSONDocumentManager docMgr, DocumentMetadataHandle docMeta, String datatype) {
            JsonNode endpointdef = getEndpointdef(datatype, datatype, true, false);
            String script = getScript(datatype, true, false);
            setupEndpoint(docMgr, docMeta, endpointdef, script);
        }
        static void setupEndpoint(JSONDocumentManager docMgr, DocumentMetadataHandle docMeta, JsonNode endpointdef, String script) {
            String functionName = endpointdef.get("functionName").asText();
            String baseUri      = ENDPOINT_DIRECTORY + functionName;
            docMgr.write(baseUri+".api", docMeta, new JacksonHandle(endpointdef));
            docMgr.write(baseUri+".sjs", docMeta, new StringHandle(script));

            endpointdefs.put(functionName, endpointdef);
        }

        private static JsonNode getEndpointdef(String functionName, String datatype, boolean isMultiple, boolean isNullable) {
            return getEndpointdef(functionName, datatype, null, datatype, isMultiple, isNullable);
        }
        private static JsonNode getEndpointdef(
                String functionName, String paramType1, String paramType2, String returnType, boolean isMultiple, boolean isNullable
        ) {
            ObjectNode endpointdef = objectMapper.createObjectNode();
            endpointdef.put("functionName", functionName);
            if (paramType1 != null) {
                ArrayNode paramdefs  = objectMapper.createArrayNode();
                ObjectNode paramdef = objectMapper.createObjectNode();
                paramdef.put("name", "param1");
                paramdef.put("datatype", paramType1);
                paramdef.put("multiple", isMultiple);
                paramdef.put("nullable", isNullable);
                paramdefs.add(paramdef);
                if (paramType2 != null) {
                    paramdef = objectMapper.createObjectNode();
                    paramdef.put("name", "param2");
                    paramdef.put("datatype", paramType2);
                    paramdef.put("multiple", !isMultiple);
                    paramdef.put("nullable", isNullable);
                    paramdefs.add(paramdef);
                }
                endpointdef.set("params", paramdefs);
            }
            if (returnType != null) {
                ObjectNode returndef = objectMapper.createObjectNode();
                returndef.put("datatype", returnType);
                returndef.put("multiple", isMultiple);
                returndef.put("nullable", isNullable);
                endpointdef.set("return", returndef);
            }
            return endpointdef;
        }
        
        static String getScript(String datatype, boolean isMultiple, boolean isNullable) {
            return getScript(datatype, null, null, isMultiple, isNullable);
        }
        
        static String getScript(
                String paramType1, String paramType2, String returnVal, boolean isMultiple, boolean isNullable
        ) {
            StringBuilder scriptBldr = new StringBuilder()
                    .append("'use strict';\n");
            if (paramType1 != null) {
                scriptBldr = scriptBldr
                        .append("var param1;\n");
                if (paramType2 != null) {
                    scriptBldr = scriptBldr
                            .append("var param2;\n");
                }
            }

            if (paramType1 != null) {
                if (isNullable) {
                    scriptBldr = scriptBldr
                            .append("if (fn.count(param1) != 0)\n")
                            .append("  fn.error(null, 'TEST_ERROR',\n")
                            .append("    'received ' + fn.count(param1) + ' instead of no values');\n");
                } else if (isMultiple) {
                    scriptBldr = scriptBldr
                            .append("if (fn.count(param1) < 2)\n")
                            .append("  fn.error(null, 'TEST_ERROR',\n")
                            .append("    'received ' + fn.count(param1) + ' instead of multiple values');\n")
                            .append("const value1 = fn.head(param1);\n");
                } else {
                    scriptBldr = scriptBldr
                            .append("const value1 = param1;\n");
                }
                if (paramType2 != null) {
                    if (!isMultiple) {
                        scriptBldr = scriptBldr
                                .append("if (fn.count(param2) < 2)\n")
                                .append("  fn.error(null, 'TEST_ERROR',\n")
                                .append("    'received ' + fn.count(param2) + ' instead of multiple values');\n")
                                .append("const value2 = fn.head(param2);\n");
                    } else {
                        scriptBldr = scriptBldr
                                .append("const value2 = param2;\n");
                    }
                }

                Format documentFormat = isNullable ? null : NODE_FORMATS.get(paramType1);
                if (isNullable) {
                    scriptBldr = scriptBldr
                            .append("const isValid = true;\n");
                } else if (documentFormat != null) {
                    scriptBldr = scriptBldr
                            .append("const isValid = ((value1 instanceof Document) ?\n")
                            .append("    value1.documentFormat == '").append(documentFormat.name()).append("' :\n")
                            .append("    xdmp.nodeKind(value1) == '").append(paramType1).append("'\n")
                            .append("    );\n");
                } else {
                    scriptBldr = scriptBldr
                            .append("const isValid = (\n")
                            .append("    fn.localNameFromQName(xdmp.type(value1)) == '").append(paramType1).append("' ||\n")
                            .append("    xdmp.castableAs('http://www.w3.org/2001/XMLSchema', '").append(paramType1).append("', value1)\n")
                            .append("    );\n");
                }
                if (paramType2 != null) {
                    Format documentFormat2 = isNullable ? null : NODE_FORMATS.get(paramType2);
                    if (documentFormat2 != null) {
                        scriptBldr = scriptBldr
                                .append("const isValid2 = ((value2 instanceof Document) ?\n")
                                .append("    value2.documentFormat == '").append(documentFormat2.name()).append("' :\n")
                                .append("    xdmp.nodeKind(value2) == '").append(paramType2).append("'\n")
                                .append("    );\n");
                    } else {
                        scriptBldr = scriptBldr
                                .append("const isValid2 = (\n")
                                .append("    fn.localNameFromQName(xdmp.type(value2)) == '").append(paramType2).append("' ||\n")
                                .append("    xdmp.castableAs('http://www.w3.org/2001/XMLSchema', '").append(paramType2).append("', value2)\n")
                                .append("    );\n");
                    }
                }

                scriptBldr = scriptBldr
                        .append("if (!isValid)\n")
                        .append("  fn.error(null, 'TEST_ERROR',\n")
                        .append("    'param1 set to ' + Object.prototype.toString.call(value1) +")
                        .append("    ' instead of ").append(paramType1).append(" value');\n");
                if (paramType2 != null) {
                    scriptBldr = scriptBldr
                            .append("if (!isValid2)\n")
                            .append("  fn.error(null, 'TEST_ERROR',\n")
                            .append("    'param2 set to ' + Object.prototype.toString.call(value2) +")
                            .append("    ' instead of ").append(paramType2).append(" value');\n");
                }

                scriptBldr = scriptBldr
                        .append("param1;");
            } else if (returnVal != null) {
                scriptBldr = scriptBldr
                        .append(returnVal)
                        .append(";");
            }

            return scriptBldr.toString();
        }
    }
}
