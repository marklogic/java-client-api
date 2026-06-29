/*
 * Copyright (c) 2010-2026 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.example.cookbook;

import java.io.IOException;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.document.TextDocumentManager;
import com.marklogic.client.example.cookbook.Util.ExampleProperties;
import com.marklogic.client.io.StringHandle;

/**
 * SSLClientCreator illustrates the basic approach for creating a client using
 * SSL for database access.
 *
 * <p>
 * A JKS or PKCS12 truststore containing the server's CA certificate must be
 * configured via
 * {@code example.truststore.path} and {@code example.truststore.password} in
 * {@code Example.properties}
 * (or the equivalent system properties) before running this example.
 * </p>
 *
 * <p>
 * Note: to run this example, you must also modify the REST server by specifying
 * an SSL certificate template.
 * </p>
 */
public class SSLClientCreator {
  public static void main(String[] args) throws IOException {
    run(Util.loadProperties());
  }

  public static void run(ExampleProperties props) {
    System.out.println("example: " + SSLClientCreator.class.getName());

    // Configure example.truststore.path and example.truststore.password in
    // Example.properties.
    if (props.trustStorePath == null || props.trustStorePath.isEmpty()) {
      throw new IllegalStateException(
          "example.truststore.path is not configured. Set it in Example.properties to the path of a JKS or "
              + "PKCS12 truststore containing the server's CA certificate.");
    }
    if (props.trustStorePassword == null) {
      throw new IllegalStateException(
          "example.truststore.password is not configured. Set it in Example.properties.");
    }

    // Create the client using the property-source API. SSL is configured
    // declaratively via the
    // truststore path and password so that the client validates the server
    // certificate against
    // the trusted CAs in that store. STRICT hostname verification ensures the
    // server certificate
    // CN/SANs are checked against the connected host.
    try (DatabaseClient client = DatabaseClientFactory.newClient(propertyName -> switch (propertyName) {
      case "marklogic.client.host" -> props.host;
      case "marklogic.client.port" -> props.port;
      case "marklogic.client.authType" -> "digest";
      case "marklogic.client.username" -> props.writerUser;
      case "marklogic.client.password" -> props.writerPassword;
      case "marklogic.client.sslProtocol" -> "TLSv1.3";
      case "marklogic.client.ssl.truststore.path" -> props.trustStorePath;
      case "marklogic.client.ssl.truststore.password" -> props.trustStorePassword;
      case "marklogic.client.sslHostnameVerifier" -> DatabaseClientFactory.SSLHostnameVerifier.STRICT;
      default -> null;
    })) {

      // make use of the client connection
      TextDocumentManager docMgr = client.newTextDocumentManager();
      String docId = "/example/text.txt";
      StringHandle handle = new StringHandle();
      handle.set("A simple text document");
      docMgr.write(docId, handle);

      System.out.println(
          "Connected by SSL to " + props.host + ":" + props.port + " as " + props.writerUser);

      // clean up the written document
      docMgr.delete(docId);

    }
  }
}
