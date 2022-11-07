package com.marklogic.client.example.cookbook;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.expression.PlanBuilder;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.row.RowManager;
import com.marklogic.client.row.RowRecord;
import com.marklogic.client.row.RowSet;

public class OpticUpdateExample {

    public static void main(String[] args) throws Exception {
        System.out.println("example: " + OpticUpdateExample.class.getName());
        Util.ExampleProperties props = Util.loadProperties();
        DatabaseClient client = DatabaseClientFactory.newClient(props.host, props.port,
            new DatabaseClientFactory.DigestAuthContext(props.writerUser, props.writerPassword));

        // Load a TDE template that establishes a tabular view of zip codes
        loadTdeTemplate(props);

        // Load a couple JSON documents that will be projected into the zip code view
        loadZipCodes(client);

        // Run an Optic Update plan for writing documents that contain data joined from the zip code view
        RowSet<RowRecord> rows = runPlanToWriteDocuments(client.newRowManager());

        // Print the results from the returned set of rows. Each row corresponds to a document that was written to
        // the database associated with the DatabaseClient.
        rows.forEach(row -> {
            String uri = row.getString("uri");
            JsonNode doc = row.getContainer("doc");
            System.out.println("Wrote " + uri + ": " + doc);
        });

        client.release();
    }

    /**
     * For more information on TDE templates, see https://docs.marklogic.com/guide/app-dev/TDE .
     * In an ml-gradle project, these will typically be loaded from files in a project instead of via code, as
     * described at https://github.com/marklogic-community/ml-gradle/wiki/Loading-schemas .
     *
     * @param props
     */
    private static void loadTdeTemplate(Util.ExampleProperties props) {
        DatabaseClient schemasClient = DatabaseClientFactory.newClient(props.host, props.port, "Schemas",
            new DatabaseClientFactory.DigestAuthContext(props.adminUser, props.adminPassword));
        schemasClient.newXMLDocumentManager().write("/tde/zipcodes.xml",
            new DocumentMetadataHandle()
                .withCollections("http://marklogic.com/xdmp/tde")
                .withPermission("rest-reader", DocumentMetadataHandle.Capability.READ)
                .withPermission("rest-writer", DocumentMetadataHandle.Capability.UPDATE),
            new StringHandle("<template xmlns=\"http://marklogic.com/xdmp/tde\">\n" +
                "    <context>/zipcode</context>\n" +
                "    <rows>\n" +
                "        <row>\n" +
                "            <schema-name>cookbook</schema-name>\n" +
                "            <view-name>zipcode</view-name>\n" +
                "            <columns>\n" +
                "                <column>\n" +
                "                    <name>zip</name>\n" +
                "                    <scalar-type>int</scalar-type>\n" +
                "                    <val>zip</val>\n" +
                "                </column>\n" +
                "                <column>\n" +
                "                    <name>city</name>\n" +
                "                    <scalar-type>string</scalar-type>\n" +
                "                    <val>city</val>\n" +
                "                </column>\n" +
                "            </columns>\n" +
                "        </row>\n" +
                "    </rows>\n" +
                "</template>").withFormat(Format.XML));
        schemasClient.release();
    }

    /**
     * For more information on loading data via the REST API, see https://docs.marklogic.com/guide/rest-dev/documents#id_11953 .
     * In an ml-gradle project, reference data like this will typically be loaded from files in a project instead of
     * via code as described by https://docs.marklogic.com/guide/rest-dev/documents#id_11953 .
     *
     * @param client
     */
    private static void loadZipCodes(DatabaseClient client) {
        JSONDocumentManager mgr = client.newJSONDocumentManager();
        DocumentWriteSet writeSet = mgr.newWriteSet();
        DocumentMetadataHandle metadata = new DocumentMetadataHandle()
            .withPermission("rest-reader", DocumentMetadataHandle.Capability.READ)
            .withPermission("rest-writer", DocumentMetadataHandle.Capability.UPDATE);
        writeSet.add("/zipcode/22201.json", metadata,
            new StringHandle("{\"zipcode\": {\"zip\": 22201, \"city\": \"Arlington\"}}").withFormat(Format.JSON));
        writeSet.add("/zipcode/22042.json", metadata,
            new StringHandle("{\"zipcode\": {\"zip\": 22042, \"city\": \"Falls Church\"}}").withFormat(Format.JSON));
        mgr.write(writeSet);
    }

    /**
     * Define an Optic Update plan and rows for it to process, which will result in new documents being written to
     * MarkLogic that include data joined in from the zip code view.
     *
     * @param rowManager
     * @return
     */
    public static RowSet<RowRecord> runPlanToWriteDocuments(RowManager rowManager) {
        PlanBuilder op = rowManager.newPlanBuilder();

        // Define some incoming rows from which to create new documents. These rows could come from any source, such as
        // a CSV or a relational database. The row with a zip code is expected to have a city joined into it via the
        // plan below.
        ArrayNode incomingData = new ObjectMapper().createArrayNode();
        incomingData.addObject().put("lastName", "Smith").put("firstName", "Jane").put("zipCode", 22201);
        incomingData.addObject().put("lastName", "Jones").put("firstName", "John");

        PlanBuilder.ModifyPlan plan = op
            // Define the columns of the incoming data that the pipeline will process
            .fromParam("incomingData", "input", op.colTypes(
                op.colType("lastName", "string"),
                op.colType("firstName", "string"),
                op.colType("zipCode", "integer", true)
            ))
            // Join in rows from the zipcode view that was created via the TDE
            .joinLeftOuter(
                op.fromView("cookbook", "zipcode"),
                op.on(
                    op.viewCol("input", "zipCode"),
                    op.viewCol("zipcode", "zip")
                )
            )
            // Define the URI, doc, and permissions to use for writing new documents. The document will contain data
            // from both the incoming data and the joined zipcode row, if a matching one was found.
            .select(
                op.as("uri", op.fn.concat(
                    op.xs.string("/acme/person/"),
                    op.viewCol("input", "lastName"),
                    op.xs.string(".json")
                )),
                op.as("doc", op.jsonObject(
                    op.prop("lastName", op.viewCol("input", "lastName")),
                    op.prop("firstName", op.viewCol("input", "firstName")),
                    op.prop("zipCode", op.viewCol("input", "zipCode")),
                    op.prop("cityOfResidence", op.viewCol("zipcode", "city"))
                )),
                op.as("permissions", op.jsonArray(
                    op.permission("rest-reader", "read"),
                    op.permission("rest-reader", "update")
                ))
            ).write();

        // Run the plan, binding the incoming data to the "fromParam" accessor
        return rowManager.resultRows(plan.bindParam("incomingData", new JacksonHandle(incomingData)));
    }
}
