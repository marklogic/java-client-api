/*
 * Copyright (c) 2022 MarkLogic Corporation
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

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.marklogic.client.datamovement.DataMovementManager;
import com.marklogic.client.datamovement.WriteBatcher;
import com.marklogic.client.document.DocumentManager;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.expression.PlanBuilder;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.io.marker.AbstractWriteHandle;
import com.marklogic.client.row.RowRecord;
import com.marklogic.client.test.Common;
import com.marklogic.client.type.PlanParamExpr;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.marklogic.client.io.Format.JSON;
import static com.marklogic.client.io.Format.XML;
import static org.junit.Assert.*;

public class ValidateDocTest extends AbstractOpticUpdateTest {

    private Set<String> expectedUris = new HashSet<>();
    private DataMovementManager dataMovementManager;

    @Before
    public void moreSetup(){
        dataMovementManager = Common.client.newDataMovementManager();
    }
// TODO : test with rest-writer user when https://bugtrack.marklogic.com/57978 is fixed
    @Test
    public void xmlSchema() {
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
        expectedUris.forEach(uri -> assertNotNull("URI was not written: " + uri, mgr.exists(uri)));

        List<RowRecord> rows = resultRows(plan);

        //TODO : uncomment lines 84 and 89 after https://bugtrack.marklogic.com/57987 is fixed
        List<String> persistedUris = rows.stream().map(row -> {
            String uri = row.getString("uri");
            if (StringUtils.isEmpty(uri)) {
                //fail("URI returned by resultRows is null: " + row);
            }
            return uri;
        }).collect(Collectors.toList());
        assertEquals(uriCountToWrite, persistedUris.size());
        //expectedUris.forEach(uri -> assertTrue("Did not find URI: " + uri, persistedUris.contains(uri)));
    }

    @Test
    public void testUsingFromParamAndSchematron() {

        PlanBuilder.Plan plan = op.fromParam("bindingParam", "", op.colTypes(
                op.colType("rowId", "integer"),
                op.colType("doc")
        ))
        .validateDoc(op.col("doc"),
                op.schemaDefinition("schematron").withSchemaUri("/validateDoc/schematron.sch")
        );

        final PlanParamExpr param = op.param("bindingParam");
        ArrayNode array = mapper.createArrayNode();
        array.addObject().put("rowId", 1).put("doc", "doc1.xml");
        array.addObject().put("rowId", 2).put("doc", "doc2.xml");
        array.addObject().put("rowId", 3).put("doc", "doc3.xml");
        Map<String, AbstractWriteHandle> attachments = new HashMap<>();
        attachments.put("doc1.xml", new StringHandle("<user id=\"001\">\n" +
                "  <name>Alan</name>\n" +
                "  <gender>Male</gender>\n" +
                "  <age>14</age>\n" +
                "  <score total=\"90\">\n" +
                "    <test-1>50</test-1>\n" +
                "    <test-2>40</test-2>\n" +
                "  </score>  \n" +
                "  <result>fail</result> \n" +
                "</user>").withFormat(Format.XML));
        attachments.put("doc2.xml", new StringHandle("<user id=\"002\">\n" +
                "  <name>John</name>\n" +
                "  <gender>Male</gender>\n" +
                "  <age>15</age>\n" +
                "  <score total=\"90\">\n" +
                "    <test-1>50</test-1>\n" +
                "    <test-2>40</test-2>\n" +
                "  </score>  \n" +
                "  <result>fail</result> \n" +
                "</user>").withFormat(Format.XML));
        attachments.put("doc3.xml", new StringHandle("<user id=\"003\">\n" +
                "  <name>George</name>\n" +
                "  <gender>Male</gender>\n" +
                "  <age>15</age>\n" +
                "  <score total=\"90\">\n" +
                "    <test-1>50</test-1>\n" +
                "    <test-2>40</test-2>\n" +
                "  </score>  \n" +
                "  <result>pass</result> \n" +
                "</user>").withFormat(Format.XML));
        plan = plan.bindParam(param, new JacksonHandle(array), Collections.singletonMap("doc", attachments));

        Iterator<RowRecord> rows = rowManager.resultRows(plan).iterator();
        while (rows.hasNext()){
            RowRecord rowRecord = rows.next();
            if(rowRecord.getString("rowId")!=null)
                expectedUris.add(rowRecord.getString("rowId"));
        }
        // TODO: uncomment the below after https://bugtrack.marklogic.com/58055 is fixed
        // assertTrue(expectedUris.size() == 1);
        // assertTrue(expectedUris.contains("3"));
    }

    @Test
    public void jsonSchema() {
        PlanBuilder.ModifyPlan plan = op
                .fromDocDescriptors(
                        op.docDescriptor(newWriteOp("/acme/doc1.json", mapper.createObjectNode().put("count", 1).put("total",2))),
                        op.docDescriptor(newWriteOp("/acme/doc2.json", mapper.createObjectNode().put("count", 2).put("total",3)))
                )
                .validateDoc(op.col("doc"),
                        op.schemaDefinition("jsonSchema").withSchemaUri("/validateDoc/jsonSchema.json")
                );

        // TODO : uncomment below block after https://bugtrack.marklogic.com/58025 is fixed
        /*verifyExportedPlanReturnsSameRowCount(plan);
        Iterator<RowRecord> rows = rowManager.resultRows(plan).iterator();
        while (rows.hasNext()){
            RowRecord str = rows.next();
            String uri = str.getString("uri");
            if(uri!=null){
                System.out.println(str.getString("doc"));
                expectedUris.add(uri);
            }
        }
        */

        // TODO: uncomment the below after https://bugtrack.marklogic.com/57987 is fixed
        /*
        rowManager.execute(plan.write());
        DocumentManager docMgr = Common.client.newDocumentManager();
        assertTrue(docMgr.exists("/acme/doc1.json")!=null);
        assertTrue(docMgr.exists("/acme/doc2.json")!=null);
        assertTrue(expectedUris.size()==2);
        assertTrue(expectedUris.contains("/acme/doc1.json"));
        assertTrue(expectedUris.contains("/acme/doc2.json"));*/
    }
}
