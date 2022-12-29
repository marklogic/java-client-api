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
package com.marklogic.client.example.cookbook;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.DigestAuthContext;
import com.marklogic.client.DatabaseClientFactory.SSLHostnameVerifier;
import com.marklogic.client.document.TextDocumentManager;
import com.marklogic.client.example.cookbook.Util.ExampleProperties;
import com.marklogic.client.io.StringHandle;

/**
 * SSLClientCreator illustrates the basic approach for creating a client using SSL for database access.
 *
 * Note:  to run this example, you must modify the REST server by specifying a SSL certificate template.
 */
public class SSLClientCreator {
  public static void main(String[] args) throws IOException, KeyManagementException, NoSuchAlgorithmException {
    run(Util.loadProperties());
  }

  public static void run(ExampleProperties props) throws NoSuchAlgorithmException, KeyManagementException {
    System.out.println("example: "+SSLClientCreator.class.getName());

    // create a trust manager
    // (note: a real application should verify certificates. This
    // naive trust manager which accepts all the certificates should be replaced
    // by a valid trust manager or get a system default trust manager
    // which would validate whether the remote authentication credentials
    // should be trusted or not.)
    TrustManager naiveTrustMgr[] = new X509TrustManager[] {
        new X509TrustManager() {
          @Override
          public void checkClientTrusted(X509Certificate[] chain, String authType) {
          }

          @Override
          public void checkServerTrusted(X509Certificate[] chain, String authType) {
          }

          @Override
          public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
          }
      }
    };

    // create an SSL context
    SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
    /*
     * Here, we use a naive TrustManager which would accept any certificate
     * which the server produces. But in a real application, there should be a
     * TrustManager which is initialized with a Keystore which would determine
     * whether the remote authentication credentials should be trusted or not.
     *
     * If we init the sslContext with null TrustManager, it would use the
     * <java-home>/lib/security/cacerts file for trusted root certificates, if
     * javax.net.ssl.trustStore system property is not set and
     * <java-home>/lib/security/jssecacerts is not present. See this link for
     * more information on TrustManagers -
     * http://docs.oracle.com/javase/7/docs/technotes/guides/security/jsse/
     * JSSERefGuide.html
     *
     * If self signed certificates, signed by CAs created internally are used,
     * then the internal CA's root certificate should be added to the keystore.
     * See this link -
     * https://docs.oracle.com/cd/E19226-01/821-0027/geygn/index.html for adding
     * a root certificate in the keystore.
     */
    sslContext.init(null, naiveTrustMgr, null);

    // create the client
    // (note: a real application should use a COMMON, STRICT, or implemented hostname verifier)
    DatabaseClient client = DatabaseClientFactory.newClient(
        props.host, props.port,
        new DigestAuthContext(props.writerUser, props.writerPassword)
            .withSSLContext(sslContext, (X509TrustManager) naiveTrustMgr[0])
            .withSSLHostnameVerifier(SSLHostnameVerifier.ANY));

    // make use of the client connection
    TextDocumentManager docMgr = client.newTextDocumentManager();
    String docId = "/example/text.txt";
    StringHandle handle = new StringHandle();
    handle.set("A simple text document");
    docMgr.write(docId, handle);

    System.out.println(
      "Connected by SSL to "+props.host+":"+props.port+" as "+props.writerUser);

    // clean up the written document
    docMgr.delete(docId);

    // release the client
    client.release();
  }
}
