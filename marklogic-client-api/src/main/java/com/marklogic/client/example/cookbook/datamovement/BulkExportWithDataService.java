/*
 * Copyright 2020 MarkLogic Corporation
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
import com.marklogic.client.datamovement.DataMovementManager;
import com.marklogic.client.datamovement.QueryBatch;
import com.marklogic.client.datamovement.QueryBatchListener;
import com.marklogic.client.datamovement.QueryBatcher;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.document.TextDocumentManager;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.example.cookbook.Util;
import com.marklogic.client.query.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;


public class BulkExportWithDataService implements QueryBatchListener {

    private DatabaseClient dbClient = DatabaseClientSingleton.getAdmin("java-unittest");
    private DatabaseClient dbModulesClient = DatabaseClientSingleton.getAdmin("java-unittest-modules");
    private DataMovementManager moveMgr = dbClient.newDataMovementManager();
    private List<String> urisList = new ArrayList<>();

    @Override
    public void processEvent(QueryBatch batch) {
    }

    public static void main(String args[]) throws IOException {
        new BulkExportWithDataService().run();
    }

    private void run() throws IOException {
        setup();
        exportJson();
        tearDown();
    }

    private void setup() throws IOException {

        writeDocuments(100,"BulkExportWithDataService");
        writeScriptFile("readJsonDocs.api");
        writeScriptFile("readJsonDocs.sjs");
    }

    private  void tearDown(){

        QueryManager queryMgr = dbClient.newQueryManager();
        QueryManager queryMgrModules = dbModulesClient.newQueryManager();
        DeleteQueryDefinition deletedef = queryMgr.newDeleteDefinition();
        deletedef.setCollections("BulkExportWithDataService");
        queryMgr.delete(deletedef);
        queryMgrModules.delete(deletedef);
    }


    private void exportJson(){
        BulkExportServices bulkExportServices = uris -> {
            List<Object> list = Arrays.asList(uris.toArray());
            for(Object i:list) {
                if(!(urisList.contains(i)))
                    throw new InternalError("urisList does not contain "+i.toString());
                urisList.remove(i);
            }
            return null;
        };

        StructuredQueryBuilder structuredQueryBuilder = new StructuredQueryBuilder();
        structuredQueryBuilder.directory(1,"/example/cookbook/bulkExport/");
        QueryBatcher queryBatcher = moveMgr.newQueryBatcher(structuredQueryBuilder.collection("BulkExportWithDataService"))
                .withBatchSize(3)
                .withThreadCount(3)
                .onQueryFailure(batch -> new InternalError("An exception occured in queryBatcher"))
                .onUrisReady(batch -> bulkExportServices.readJsonDocs(Stream.of(batch.getItems())));
        moveMgr.startJob(queryBatcher);
        queryBatcher.awaitCompletion();
        moveMgr.stopJob(queryBatcher);

    }

    private void writeDocuments(int count, String collection) {
        JSONDocumentManager manager = dbClient.newJSONDocumentManager();
        DocumentMetadataHandle metadata = new DocumentMetadataHandle().withCollections(collection);

        for(int i=1;i<=count;i++) {
            StringHandle data = new StringHandle("{\"docNum\":"+i+", \"docName\":\"doc"+i+"\"}");
            String docId = "/example/cookbook/bulkExport/"+i+".json";
            manager.write(docId, metadata, data);
            urisList.add(docId);
        }
    }

    private void writeScriptFile(String fileName) throws IOException {
        TextDocumentManager modMgr     = dbModulesClient.newTextDocumentManager();
        DocumentWriteSet writeSet = modMgr.newWriteSet();
        DocumentMetadataHandle metadata = new DocumentMetadataHandle().withCollections("BulkExportWithDataService");
        metadata.getPermissions().add("rest-writer", DocumentMetadataHandle.Capability.UPDATE, DocumentMetadataHandle.Capability.READ);
        metadata.getPermissions().add("rest-reader", DocumentMetadataHandle.Capability.READ);

        InputStream in = (Util.openStream("scripts"+ File.separator+"bulkExport"+File.separator+fileName));
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder out = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            out.append(line);
        }
        reader.close();

        writeSet.add("/example/cookbook/bulkExport/"+fileName, metadata,
                (new StringHandle(out.toString())));
        modMgr.write(writeSet);
    }
}
