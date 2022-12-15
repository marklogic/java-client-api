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
package com.marklogic.client.example.cookbook.datamovement;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.datamovement.DataMovementManager;
import com.marklogic.client.datamovement.QueryBatcher;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.document.TextDocumentManager;
import com.marklogic.client.example.cookbook.Util;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.query.DeleteQueryDefinition;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StructuredQueryBuilder;

import java.io.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

/*
 * For custom task approach, the Gradle build file should include the below plugin and task -
 *
 * plugins {
 *     id 'com.marklogic.ml-development-tools' version '5.1.0'
 * }
 * task generateBulkExportServices(type: com.marklogic.client.tools.gradle.EndpointProxiesGenTask) {
 *     serviceDeclarationFile = 'marklogic-client-api/src/main/resources/scripts/bulkExport/service.json'
 * }
 *
 * URL of the product documentation at http://docs.marklogic.com/guide/java/DataServices#id_44346
 *
 * */

public class BulkExportWithDataService {

    private final static String COLLECTION = "BulkExportWithDataService";

    private static Util.ExampleProperties properties = getProperties();
    private DatabaseClient dbClient;
    private DatabaseClient dbModulesClient;
    private DataMovementManager moveMgr;
    private int count = 100;

    public static void main(String args[]) throws IOException {
        DatabaseClient dbClient = DatabaseClientFactory.newClient(properties.host, 8012,
            new DatabaseClientFactory.DigestAuthContext(properties.writerUser, properties.writerPassword));
        DatabaseClient dbModulesClient = DatabaseClientSingleton.getAdmin("java-unittest-modules");
        new BulkExportWithDataService(dbClient, dbModulesClient).run();
    }

    public BulkExportWithDataService(DatabaseClient dbClient, DatabaseClient modulesClient) {
        this.dbClient = dbClient;
        this.dbModulesClient = modulesClient;
        this.moveMgr = dbClient.newDataMovementManager();
    }

    public void run() throws IOException {
        setup();
        exportJson();
        tearDown();
    }

    private void setup() throws IOException {
        writeDocuments(count, COLLECTION);
        writeScriptFile("readJsonDocs.api");
        writeScriptFile("readJsonDocs.sjs");
    }

    private void tearDown() {
        QueryManager queryMgr = dbClient.newQueryManager();
        QueryManager queryMgrModules = dbModulesClient.newQueryManager();
        DeleteQueryDefinition deletedef = queryMgr.newDeleteDefinition();
        deletedef.setCollections(COLLECTION);
        queryMgr.delete(deletedef);
        queryMgrModules.delete(deletedef);
    }

    private void exportJson() {
        BulkExportServices bulkExportServices = BulkExportServices.on(dbClient);

        StructuredQueryBuilder structuredQueryBuilder = new StructuredQueryBuilder();
        AtomicInteger docCount = new AtomicInteger();
        QueryBatcher queryBatcher = moveMgr
            .newQueryBatcher(structuredQueryBuilder.collection(COLLECTION))
            .withBatchSize(10)
            .withThreadCount(3)
            .onUrisReady(batch -> {
                try {
                    Stream<Reader> docStream = bulkExportServices.readJsonDocs(Stream.of(batch.getItems()));
                    docStream.forEach(doc -> docCount.getAndIncrement());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });

        moveMgr.startJob(queryBatcher);
        queryBatcher.awaitCompletion();
        moveMgr.stopJob(queryBatcher);
        if (docCount.get() != count) {
            throw new RuntimeException("Expected " + count + " documents; found: " + docCount.get());
        }
    }

    private void writeDocuments(int count, String collection) {
        JSONDocumentManager manager = dbClient.newJSONDocumentManager();
        DocumentMetadataHandle metadata = new DocumentMetadataHandle()
            .withCollections(collection)
            .withPermission("rest-reader", DocumentMetadataHandle.Capability.READ, DocumentMetadataHandle.Capability.UPDATE);
        DocumentWriteSet writeSet = manager.newWriteSet();
        for (int i = 1; i <= count; i++) {
            StringHandle data = new StringHandle("{\"docNum\":" + i + ", \"docName\":\"doc" + i + "\"}");
            String docId = "/example/cookbook/bulkExport/" + i + ".json";
            writeSet.add(docId, metadata, data);
        }
        manager.write(writeSet);
    }

    private void writeScriptFile(String fileName) throws IOException {
        try (InputStream in = (Util.openStream("scripts" + File.separator + "bulkExport" + File.separator + fileName))) {
            final TextDocumentManager modMgr = dbModulesClient.newTextDocumentManager();

            final DocumentWriteSet writeSet = modMgr.newWriteSet();
            final DocumentMetadataHandle metadata = new DocumentMetadataHandle().withCollections("BulkExportWithDataService");
            metadata.getPermissions().add("rest-writer", DocumentMetadataHandle.Capability.UPDATE);
            metadata.getPermissions().add("rest-reader", DocumentMetadataHandle.Capability.READ, DocumentMetadataHandle.Capability.EXECUTE);

            final StringBuilder out = new StringBuilder();

            final BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null) {
                out.append(line);
            }

            writeSet.add("/example/cookbook/bulkExport/" + fileName, metadata,
                new StringHandle(out.toString()));

            modMgr.write(writeSet);
        }
    }

    private static Util.ExampleProperties getProperties() {
        try {
            return Util.loadProperties();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
