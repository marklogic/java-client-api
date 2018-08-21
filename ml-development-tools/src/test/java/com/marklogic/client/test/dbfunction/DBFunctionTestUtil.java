package com.marklogic.client.test.dbfunction;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class DBFunctionTestUtil {
   // TODO - parameterizable security context
   public final static DatabaseClient db = makeTestClient();
   private static DatabaseClient makeTestClient() {

      String host = System.getProperty("TEST_HOST", "localhost");
      int    port = Integer.parseInt(System.getProperty("TEST_PORT", "8016"));;

      DatabaseClient db = DatabaseClientFactory.newClient(
        host,
        port,
// TODO: use rest-reader role after amping to add header in test inspector
//      new DatabaseClientFactory.DigestAuthContext("rest-reader", "x")
        new DatabaseClientFactory.DigestAuthContext("admin", "admin")
        );

      try {
         OkHttpClient client = (OkHttpClient) db.getClientImplementation();
// TODO: better alternative to ping for non-REST server
         Response response = client.newCall(new Request.Builder().url(
            new HttpUrl.Builder()
               .scheme("http")
               .host(host)
               .port(port)
               .encodedPath("/")
               .build()
            ).build()
            ).execute();
         int statusCode = response.code();
         if (statusCode >= 300 && statusCode != 404) {
            throw new RuntimeException(statusCode+" "+response.message());
         }
      } catch (IOException e) {
// TODO: library error
         throw new RuntimeException(e);
      }
      return db;
   }
   public static URL getResource(String name) {
      return DBFunctionTestUtil.class.getClassLoader().getResource(name);
   }
   public static InputStream getResourceAsStream(String name) {
      return DBFunctionTestUtil.class.getClassLoader().getResourceAsStream(name);
   }
}
