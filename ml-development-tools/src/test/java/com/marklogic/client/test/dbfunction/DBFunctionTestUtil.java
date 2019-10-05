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
   // used for test endpoints that inspect request and generate response
   public final static DatabaseClient db = makeTestClient();

   // used for bulk test endpoints that depend on DMSDK
   public final static DatabaseClient restDb = makeRestClient();

   // used for test endpoints that need an elevated privilege
   public final static DatabaseClient adminDb = makeAdminTestClient();

   private static DatabaseClient makeRestClient() {
      return makeRestClientImpl(
              new DatabaseClientFactory.DigestAuthContext("rest-reader", "x")
      );
   }
   private static DatabaseClient makeTestClient() {
      return makeTestClientImpl(
          new DatabaseClientFactory.DigestAuthContext("rest-reader", "x")
      );
   }
   private static DatabaseClient makeAdminTestClient() {
      return makeTestClientImpl(
          new DatabaseClientFactory.DigestAuthContext("admin", "admin")
      );
   }
   private static DatabaseClient makeRestClientImpl(DatabaseClientFactory.DigestAuthContext auth) {
      return makeClientImpl(auth, "8012", false);
   }
   private static DatabaseClient makeTestClientImpl(DatabaseClientFactory.DigestAuthContext auth) {
      return makeClientImpl(auth, "8016", true);
   }
   private static DatabaseClient makeClientImpl(
           DatabaseClientFactory.DigestAuthContext auth, String defaultPort, boolean withCheck
   ) {
      String host = System.getProperty("TEST_HOST", "localhost");
      int    port = Integer.parseInt(System.getProperty("TEST_PORT", defaultPort));

      DatabaseClient db = DatabaseClientFactory.newClient(host, port, auth);

      if (withCheck) {
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
            throw new RuntimeException(e);
         }
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
