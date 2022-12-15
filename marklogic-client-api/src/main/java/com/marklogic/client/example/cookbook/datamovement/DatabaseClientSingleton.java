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

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import javax.xml.bind.JAXBException;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.DigestAuthContext;
import com.marklogic.client.example.cookbook.Util;
import com.marklogic.client.example.cookbook.Util.ExampleProperties;
import com.marklogic.client.example.cookbook.datamovement.WriteandReadPOJOs.ProductDetails;
import com.marklogic.client.io.JAXBHandle;
import com.marklogic.client.io.JacksonDatabindHandle;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class DatabaseClientSingleton {
  private static DatabaseClient client = null;
  private static HashMap<String,DatabaseClient> dbSpecificClients = new HashMap<>();
  private static DatabaseClient restAdminClient = null;
  private static boolean handlesRegistered = false;
  private static ExampleProperties properties = getProperties();

  private DatabaseClientSingleton() {}

  private static void registerHandlers() {
    if ( handlesRegistered == true ) return;
    try {
    DatabaseClientFactory.getHandleRegistry().register(
      JAXBHandle.newFactory(ProductDetails.class));
    } catch (JAXBException e) {
      throw new IllegalStateException(e);
    }
    ObjectMapper mapper = new JacksonDatabindHandle(null).getMapper();
    // we do the next three lines so dates are written in xs:dateTime format
    // which makes them ready for range indexes in MarkLogic Server
    String ISO_8601_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
    mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    mapper.setDateFormat(new SimpleDateFormat(ISO_8601_FORMAT));
    DatabaseClientFactory.getHandleRegistry().register(
      JacksonDatabindHandle.newFactory(mapper, Employee.class));
    DatabaseClientFactory.getHandleRegistry().register(
      JacksonDatabindHandle.newFactory(mapper, LoadDetail.class));
    handlesRegistered = true;
  }

  public static DatabaseClient get() {
    if(client == null) {
      registerHandlers();
      client = DatabaseClientFactory.newClient(properties.host, properties.port,
          new DigestAuthContext(properties.writerUser, properties.writerPassword));
    }
    return client;
  }

  public static DatabaseClient getAdmin() {
    if (client == null) {
      registerHandlers();
      client = DatabaseClientFactory.newClient(properties.host, properties.port,
          new DigestAuthContext("admin", "admin"));
    }
    return client;
  }

  public static synchronized DatabaseClient getAdmin(String database) {
    if (dbSpecificClients.get("admin" + database) == null) {
      registerHandlers();
      dbSpecificClients.put("admin" + database, DatabaseClientFactory.newClient(properties.host, properties.port, database,
              new DigestAuthContext(properties.adminUser, properties.adminPassword)));
    }
    return dbSpecificClients.get("admin" + database);
  }

  public static synchronized DatabaseClient get(String database) {
    if(dbSpecificClients.get(database) == null) {
      registerHandlers();
      dbSpecificClients.put(database, DatabaseClientFactory.newClient(properties.host, properties.port,
        database, new DigestAuthContext(properties.writerUser, properties.writerPassword)));
    }
    return dbSpecificClients.get(database);
  }

  public static DatabaseClient getRestAdmin() {
    if(restAdminClient == null) {
      restAdminClient = DatabaseClientFactory.newClient(properties.host, properties.port,
        new DigestAuthContext(properties.adminUser, properties.adminPassword));
    }
    return restAdminClient;
  }

  private static ExampleProperties getProperties() {
    try {
      return Util.loadProperties();
    } catch (IOException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }
}
