/*
 * Copyright 2019 MarkLogic Corporation
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
package com.marklogic.client.test.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.test.Common;
import okhttp3.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DataServiceBootstrapper {
    private String contentDbName = "DBFUnitTest";
    private String modulesDbName = "DBFUnitTestModules";
    private String serverName    = "DBFUnitTest";
    private String host          = Common.HOST;
    private int    serverPort    = Common.DATA_SERVICE_PORT;

    public static void main(String... args) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();

        String operation = (args.length > 0) ? args[0] : "setup";
        switch(operation) {
            case "setup":
                new DataServiceBootstrapper().setupServer(writer);
                break;
            case "teardown":
                new DataServiceBootstrapper().teardownServer(writer);
                break;
            default:
                throw new IllegalArgumentException("unknown operation: "+operation);
        }
    }
    public void setupServer(ObjectWriter writer) throws IOException {
        DatabaseClient adminDBClient = Common.connectServerAdmin();
        OkHttpClient client = (OkHttpClient) adminDBClient.getClientImplementation();

        for (String dbName : new String[]{contentDbName, modulesDbName}) {
            Map<String, String> instancedef = new HashMap<>();
            instancedef.put("database-name", dbName);
            createEntity(
                    client, writer, "databases", "database", dbName, instancedef
            );

            String forestName = dbName+"-1";
            instancedef = new HashMap<>();
            instancedef.put("database", dbName);
            instancedef.put("forest-name", forestName);
            createEntity(
                    client, writer, "forests", "forest", forestName, instancedef
            );
        }

        Map attachdef = new HashMap<>();
        attachdef.put("server-name",      serverName);
        attachdef.put("root",             "/");
        attachdef.put("port",             serverPort);
        attachdef.put("content-database", contentDbName);
        attachdef.put("modules-database", modulesDbName);

        createEntity(client, writer, "servers?group-id=Default&server-type=http",
                "appserver", serverName, attachdef
        );
    }
    public void createEntity(OkHttpClient client, ObjectWriter writer, String address,
                             String entityType, String instanceName, Map<String,?> instancedef
    ) throws IOException {
        Response response = client.newCall(
                new Request.Builder()
                        .url("http://"+host+":8002/manage/v2/"+address)
                        .post(
                                RequestBody.create(MediaType.parse("application/json"), writer.writeValueAsString(instancedef))
                        ).build()
        ).execute();
        if (response.code() >= 400) {
            throw new RuntimeException("Could not create "+instanceName+" "+entityType+" "+response.code());
        }
    }
    public void teardownServer(ObjectWriter writer) throws IOException {
        DatabaseClient adminDBClient = Common.connectServerAdmin();
        OkHttpClient client = (OkHttpClient) adminDBClient.getClientImplementation();

        Map<String, Integer> detachdef = new HashMap<>();
        detachdef.put("content-database", 0);
        detachdef.put("modules-database", 0);

        Response response = client.newCall(
                new Request.Builder()
                        .url("http://"+host+":8002/manage/v2/servers/"+serverName+"/properties?group-id=Default")
                        .put(
                            RequestBody.create(MediaType.parse("application/json"), writer.writeValueAsString(detachdef)
                            )
                ).build()
        ).execute();
        if (response.code() >= 400) {
            throw new RuntimeException("Could not detach "+serverName+" appserver: "+response.code());
        }

        for (String dbName : new String[]{contentDbName, modulesDbName}) {
            deleteEntity(client, "databases/"+dbName+"?forest-delete=data", "database", dbName);
        }

        deleteEntity(client, "servers/${serverName}?group-id=Default", "appserver", serverName);
    }
    public void deleteEntity(OkHttpClient client, String address, String entityType, String instanceName) throws IOException {
        Response response = client.newCall(
                new Request.Builder()
                        .url("http://"+host+":8002/manage/v2/"+address)
                        .delete()
                        .build()
        ).execute();
        if (response.code() >= 400) {
            throw new RuntimeException("Could not delete "+instanceName+" "+entityType+" "+response.code());
        }
    }

}
