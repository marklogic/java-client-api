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

import javax.xml.bind.JAXBException;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.DigestAuthContext;
import com.marklogic.client.example.cookbook.Util;
import com.marklogic.client.example.cookbook.Util.ExampleProperties;
import com.marklogic.client.io.JAXBHandle;

public class DatabaseClientSingleton {
  private static DatabaseClient client = null;

  private DatabaseClientSingleton() {}

  public static void register(Class<?>...pojoClasses) throws JAXBException {
    if(client != null) {
      throw new IllegalStateException("Cannot register classes after client instance is created!");
    }
    DatabaseClientFactory.getHandleRegistry().register(JAXBHandle.newFactory(pojoClasses));
  }

  public static DatabaseClient get() {
    if(client == null) {
      ExampleProperties properties = null;
      try {
        properties = Util.loadProperties();
      } catch (IOException e) {
        e.printStackTrace();
      }
      client = DatabaseClientFactory.newClient(properties.host, properties.port,
        new DigestAuthContext(properties.writerUser, properties.writerPassword));
    }
    return client;
  }
}