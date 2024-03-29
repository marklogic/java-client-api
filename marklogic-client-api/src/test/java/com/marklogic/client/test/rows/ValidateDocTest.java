/*
 * Copyright (c) 2023 MarkLogic Corporation
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
package com.marklogic.client.test.rows;

import com.marklogic.client.datamovement.DataMovementManager;
import com.marklogic.client.datamovement.WriteBatcher;
import com.marklogic.client.document.DocumentManager;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.expression.PlanBuilder;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.row.RowRecord;
import com.marklogic.client.test.Common;
import com.marklogic.client.test.junit5.RequiresML11;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.*;
import java.util.stream.Collectors;

import static com.marklogic.client.io.Format.XML;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(RequiresML11.class)
public class ValidateDocTest extends AbstractOpticUpdateTest {

    private Set<String> expectedUris = new HashSet<>();
    private DataMovementManager dataMovementManager;

    @BeforeEach
    public void moreSetup(){
        dataMovementManager = Common.client.newDataMovementManager();
    }

    @Test
    public void testXmlSchemaWithLaxMode() {
        WriteBatcher writeBatcher = dataMovementManager.newWriteBatcher();
        DocumentMetadataHandle meta = newDefaultMetadata();
        dataMovementManager.startJob(writeBatcher);
        final int uriCountToWrite = 10;
        for (int i = 0; i < uriCountToWrite; i++) {
            String uri = "/acme/" + i + ".xml";
            writeBatcher.addAs(uri, meta, new StringHandle("<Doc><key>" + i + "</key><Value>value" + i + "</Value></Doc>").withFormat(XML));
            expectedUris.add(uri);
        }
        writeBatcher.flushAndWait();
        dataMovementManager.stopJob(writeBatcher);

        PlanBuilder.Plan plan = op
            .fromDocUris(op.cts.directoryQuery("/acme/"))
            .joinDoc(op.col("doc"), op.col("uri"))
            .validateDoc(op.col("doc"),
                op.schemaDefinition("xmlSchema").withMode("lax")
            );
        XMLDocumentManager mgr = Common.client.newXMLDocumentManager();
        expectedUris.forEach(uri -> assertNotNull(mgr.exists(uri), "URI was not written: " + uri));

        List<RowRecord> rows = resultRows(plan);

        List<String> persistedUris = rows.stream().map(row -> {
            String uri = row.getString("uri");
            return uri;
        }).collect(Collectors.toList());
        assertEquals(uriCountToWrite, persistedUris.size());
        expectedUris.forEach(uri -> assertTrue(persistedUris.contains(uri), "persistedUris does not contain "+uri));
    }

    @Test
    public void testXmlSchemaWithStrictMode() {
        String[][] triples = new String[][]{
                new String[]{"http://example.org/rowgraph/s1", "http://example.org/rowgraph/p1", "http://example.org/rowgraph/o1"},
                new String[]{"http://example.org/rowgraph/s1", "http://example.org/rowgraph/p2", "http://example.org/rowgraph/o2"},
                new String[]{"http://example.org/rowgraph/s2", "http://example.org/rowgraph/p1", "http://example.org/rowgraph/o3"}
        };
        XMLDocumentManager docMgr = Common.client.newXMLDocumentManager();

        String triplesXML =
                "<sem:triples xmlns:sem=\"http://marklogic.com/semantics\">\n"+
                        String.join("\n", (String[]) Arrays
                                .stream(triples)
                                .map(triple ->
                                        "<sem:triple>"+
                                                "<sem:subject>"+triple[0]+"</sem:subject>"+
                                                "<sem:predicate>"+triple[1]+"</sem:predicate>"+
                                                "<sem:object>"+triple[2]+"</sem:object>"+
                                                "</sem:triple>"
                                )
                                .toArray(size -> new String[size]))+
                        "</sem:triples>";
        DocumentWriteSet writeSet = Common.client.newDocumentManager().newWriteSet();
        DocumentMetadataHandle metadata = newDefaultMetadata();
        for(int i=0; i<4; i++){
            writeSet.add("/acme/"+i+".xml",metadata, new StringHandle(triplesXML).withFormat(Format.XML));
        }
        docMgr.write(writeSet);

        PlanBuilder.Plan plan = op
                .fromDocUris(op.cts.directoryQuery("/acme/"))
                .joinDoc(op.col("doc"), op.col("uri"))
                .validateDoc(op.col("doc"),
                        op.schemaDefinition("xmlSchema").withMode("strict")
                );
        Iterator<RowRecord> rows = rowManager.resultRows(plan).iterator();
        Set<String> uris = new HashSet<>();
        while (rows.hasNext()){
            uris.add(rows.next().getString("uri"));
        }
         assertTrue(uris.size() == 4);
        for(int i=0;i<4;i++){
            assertTrue(uris.contains("/acme/"+i+".xml"), "uris does not contain /acme/"+i+".xml");
        }
    }

    @Test
    public void testWriteWithXmlSchemaWithStrictMode() {
        String[][] triples = new String[][]{
                new String[]{"http://example.org/rowgraph/s1", "http://example.org/rowgraph/p1", "http://example.org/rowgraph/o1"},
                new String[]{"http://example.org/rowgraph/s1", "http://example.org/rowgraph/p2", "http://example.org/rowgraph/o2"},
                new String[]{"http://example.org/rowgraph/s2", "http://example.org/rowgraph/p1", "http://example.org/rowgraph/o3"}
        };

        String triplesXML =
                "<sem:triples xmlns:sem=\"http://marklogic.com/semantics\">\n"+
                        String.join("\n", (String[]) Arrays
                                .stream(triples)
                                .map(triple ->
                                        "<sem:triple>"+
                                                "<sem:subject>"+triple[0]+"</sem:subject>"+
                                                "<sem:predicate>"+triple[1]+"</sem:predicate>"+
                                                "<sem:object>"+triple[2]+"</sem:object>"+
                                                "</sem:triple>"
                                )
                                .toArray(size -> new String[size]))+
                        "</sem:triples>";
        DocumentWriteSet writeSet = Common.client.newDocumentManager().newWriteSet();
        DocumentMetadataHandle metadata = newDefaultMetadata();
        for(int i=0; i<4; i++){
            writeSet.add("/acme/"+i+".xml", metadata, new StringHandle(triplesXML).withFormat(Format.XML));
        }

        PlanBuilder.Plan plan = op.fromParam("myDocs", "", op.docColTypes())
                .validateDoc(op.col("doc"),
                        op.schemaDefinition("xmlSchema").withMode("strict"))
                .write();
        plan = plan.bindParam("myDocs", writeSet);

        List<RowRecord> rows = resultRows(plan);
        assertEquals(4, rows.size());
    }

    @Test
    public void testUsingFromParamAndSchematron() {
        DocumentWriteSet writeSet = Common.client.newDocumentManager().newWriteSet();
        DocumentMetadataHandle metadata = newDefaultMetadata();

        writeSet.add("/acme/doc1.xml", metadata, new StringHandle("<user id=\"001\">\n" +
                "  <name>Alan</name>\n" +
                "  <gender>Male</gender>\n" +
                "  <age>14</age>\n" +
                "  <score total=\"90\">\n" +
                "    <test-1>50</test-1>\n" +
                "    <test-2>40</test-2>\n" +
                "  </score>  \n" +
                "  <result>fail</result> \n" +
                "</user>").withFormat(Format.XML));
        writeSet.add("/acme/doc2.xml", metadata, new StringHandle("<user id=\"002\">\n" +
                "  <name>John</name>\n" +
                "  <gender>Male</gender>\n" +
                "  <age>15</age>\n" +
                "  <score total=\"90\">\n" +
                "    <test-1>50</test-1>\n" +
                "    <test-2>40</test-2>\n" +
                "  </score>  \n" +
                "  <result>fail</result> \n" +
                "</user>").withFormat(Format.XML));

        writeSet.add("/acme/doc3.xml", metadata, new StringHandle("<user id=\"003\">\n" +
                "  <name>George</name>\n" +
                "  <gender>Male</gender>\n" +
                "  <age>15</age>\n" +
                "  <score total=\"90\">\n" +
                "    <test-1>50</test-1>\n" +
                "    <test-2>40</test-2>\n" +
                "  </score>  \n" +
                "  <result>pass</result> \n" +
                "</user>").withFormat(Format.XML));
        PlanBuilder.Plan plan = op.fromParam("myDocs", "", op.docColTypes())
                .validateDoc(op.col("doc"),
                        op.schemaDefinition("schematron").withSchemaUri("/validateDoc/schematron.sch")
                ).write();
        plan = plan.bindParam("myDocs", writeSet);

        List<RowRecord> rows = resultRows(plan);
        assertEquals(1, rows.size());
        DocumentManager docMgr = Common.client.newDocumentManager();
        assertTrue(docMgr.exists("/acme/doc1.xml") == null);
        assertTrue(docMgr.exists("/acme/doc2.xml") == null);
        assertTrue(docMgr.exists("/acme/doc3.xml") != null);
    }

    @Test
    public void testWithJsonSchema() {
        PlanBuilder.ModifyPlan plan = op
                .fromDocDescriptors(
                        op.docDescriptor(newWriteOp("/acme/doc1.json", mapper.createObjectNode().put("count", 1).put("total",2))),
                        op.docDescriptor(newWriteOp("/acme/doc2.json", mapper.createObjectNode().put("count", 2).put("total",3)))
                )
                .validateDoc(op.col("doc"),
                        op.schemaDefinition("jsonSchema").withSchemaUri("/validateDoc/jsonSchema.json"));

        verifyExportedPlanReturnsSameRowCount(plan);
        Iterator<RowRecord> rows = rowManager.resultRows(plan).iterator();
        while (rows.hasNext()){
            RowRecord str = rows.next();
            String uri = str.getString("uri");
                expectedUris.add(uri);
        }
        assertTrue(expectedUris.size()==2);
        rowManager.execute(plan.write());
        DocumentManager docMgr = Common.client.newDocumentManager();
        assertTrue(docMgr.exists("/acme/doc1.json")!=null);
        assertTrue(docMgr.exists("/acme/doc2.json")!=null);
        assertTrue(
                docMgr.read("/acme/doc1.json").next().getContent(new StringHandle()).toString().equals("{\"count\":1, \"total\":2}"));
        assertTrue(
                docMgr.read("/acme/doc2.json").next().getContent(new StringHandle()).toString().equals("{\"count\":2, \"total\":3}"));

        assertTrue(expectedUris.contains("/acme/doc1.json"));
        assertTrue(expectedUris.contains("/acme/doc2.json"));
    }

    @Test
    public void testWithJsonSchemaAndInvalidJsons() {
        PlanBuilder.ModifyPlan plan = op
                .fromDocDescriptors(
                        op.docDescriptor(newWriteOp("/acme/doc1.json", mapper.createObjectNode().put("count", 1).put("total",2))),
                        op.docDescriptor(newWriteOp("/acme/doc2.json", mapper.createObjectNode().put("count", -1).put("total",3))),
                        op.docDescriptor(newWriteOp("/acme/doc3.json", mapper.createObjectNode().put("count", 2).put("total",13))),
                        op.docDescriptor(newWriteOp("/acme/doc4.json", mapper.createObjectNode().put("count", -2).put("total",23))),
                        op.docDescriptor(newWriteOp("/acme/doc5.json", mapper.createObjectNode().put("count", 3).put("total",33))),
                        op.docDescriptor(newWriteOp("/acme/doc6.json", mapper.createObjectNode().put("count", 4).put("total",34))),
                        op.docDescriptor(newWriteOp("/acme/doc7.json", mapper.createObjectNode().put("count", -5).put("total",38)))
                )
                .validateDoc(op.col("doc"),
                        op.schemaDefinition("jsonSchema").withSchemaUri("/validateDoc/jsonSchema.json"));

        verifyExportedPlanReturnsSameRowCount(plan);
        Iterator<RowRecord> rows = rowManager.resultRows(plan).iterator();
        while (rows.hasNext()){
            RowRecord str = rows.next();
            String uri = str.getString("uri");
                expectedUris.add(uri);
        }

        rowManager.execute(plan.write());
        DocumentManager docMgr = Common.client.newDocumentManager();
        assertTrue(docMgr.exists("/acme/doc1.json")!=null);
        assertTrue(
                docMgr.read("/acme/doc1.json").next().getContent(new StringHandle()).toString().equals("{\"count\":1, \"total\":2}"));
        assertTrue(docMgr.exists("/acme/doc2.json") == null);
        assertTrue(docMgr.exists("/acme/doc3.json") != null);
        assertTrue(
                docMgr.read("/acme/doc3.json").next().getContent(new StringHandle()).toString().equals("{\"count\":2, \"total\":13}"));
        assertTrue(docMgr.exists("/acme/doc4.json") == null);

        assertTrue(docMgr.exists("/acme/doc5.json") != null);
        assertTrue(
                docMgr.read("/acme/doc5.json").next().getContent(new StringHandle()).toString().equals("{\"count\":3, \"total\":33}"));

        assertTrue(docMgr.exists("/acme/doc6.json") != null);
        assertTrue(
                docMgr.read("/acme/doc6.json").next().getContent(new StringHandle()).toString().equals("{\"count\":4, \"total\":34}"));

        assertTrue(docMgr.exists("/acme/doc7.json") == null);
        assertTrue(expectedUris.size()==4);
         assertTrue( expectedUris.contains("/acme/doc1.json"));
         assertTrue(expectedUris.contains("/acme/doc3.json"));
         assertTrue(expectedUris.contains("/acme/doc5.json"));
         assertTrue(expectedUris.contains("/acme/doc6.json"));
    }
	@Test
	public void testWithNonExistingSchema() {
		PlanBuilder.ModifyPlan plan = op
			.fromDocDescriptors(
				op.docDescriptor(newWriteOp("/acme/doc1.json", mapper.createObjectNode().put("count", 1).put("total",2)))
			).validateDoc(op.col("doc"),
				op.schemaDefinition("jsonSchema").withSchemaUri("/validateDoc/Non-Existing.json"));
		rowManager.execute(plan.write());
		DocumentManager docMgr = Common.client.newDocumentManager();
		assertNull(docMgr.exists("/acme/doc1.json"), "When a schema is used that doesn't exist, " +
			"the validation by the server is expected to silently fail, and thus the document is not written");
	}
}
