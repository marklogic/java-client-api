/*
 * Copyright 2012-2017 MarkLogic Corporation
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
  private static DatabaseClient restAdminClient = null;

  private DatabaseClientSingleton() {}

  private static void registerHandlers() {
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
  }

  public static DatabaseClient get() {
    if(client == null) {
      registerHandlers();
      ExampleProperties properties = null;
      try {
        properties = Util.loadProperties();
      } catch (IOException e) {
        e.printStackTrace();
        throw new RuntimeException(e);
      }
      client = DatabaseClientFactory.newClient(properties.host, properties.port,
        new DigestAuthContext(properties.writerUser, properties.writerPassword));
    }
    return client;
  }

  public static DatabaseClient getRestAdmin() {
    if(restAdminClient == null) {
      ExampleProperties properties = null;
      try {
        properties = Util.loadProperties();
      } catch (IOException e) {
        e.printStackTrace();
        throw new RuntimeException(e);
      }
      restAdminClient = DatabaseClientFactory.newClient(properties.host, properties.port,
        new DigestAuthContext(properties.adminUser, properties.adminPassword));
    }
    return restAdminClient;
  }
}
