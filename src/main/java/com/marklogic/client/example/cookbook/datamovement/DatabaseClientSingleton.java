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