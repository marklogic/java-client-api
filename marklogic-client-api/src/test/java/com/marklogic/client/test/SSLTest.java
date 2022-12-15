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
package com.marklogic.client.test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.net.ssl.*;
import javax.security.auth.x500.X500Principal;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import com.marklogic.client.MarkLogicIOException;
import org.junit.Test;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.DigestAuthContext;
import com.marklogic.client.DatabaseClientFactory.SSLHostnameVerifier;
import com.marklogic.client.document.TextDocumentManager;
import com.marklogic.client.io.StringHandle;

public class SSLTest {
  @Test
  public void testSSLAuth() throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException {
    TrustManagerFactory trustMgrFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
    trustMgrFactory.init(KeyStore.getInstance(KeyStore.getDefaultType()));

    TrustManager[] trustMgrs = trustMgrFactory.getTrustManagers();
    assertNotNull(trustMgrs);
    assertTrue(trustMgrs.length > 0);

    X509TrustManager x509trustMgr = null;
    for (TrustManager trustMgr: trustMgrs) {
      if (trustMgr instanceof X509TrustManager) {
        x509trustMgr = (X509TrustManager) trustMgr;
        break;
      }
    }
    assertNotNull(x509trustMgr);

    // create an SSL context
    SSLContext sslContext = SSLContext.getInstance("SSLv3");
    sslContext.init(null, trustMgrs, null);

    // create the client
    DatabaseClient client = Common.makeNewClient(Common.HOST, Common.PORT, Common.newSecurityContext("rest-writer", "x")
      .withSSLContext(sslContext, x509trustMgr)
      .withSSLHostnameVerifier(SSLHostnameVerifier.ANY));

    try {
      // make use of the client connection so we get an auth exception if it
      // is a non SSL connection and a successful write if it is an SSL connection
      TextDocumentManager docMgr = client.newTextDocumentManager();
      String docId = "/example/text.txt";
      StringHandle handle = new StringHandle();
      handle.set("A simple text document by SSL connection");
      docMgr.write(docId, handle);
      // subsequent lines will only run if write doesn't throw an exception
      handle = new StringHandle();
      docMgr.read(docId, handle);
      assertEquals(handle.get(), "A simple text document by SSL connection");
      docMgr.delete(docId);
    } catch (MarkLogicIOException e) {
      String exception = e.toString();
      String message = exception.toLowerCase();

      boolean foundExpected = false;

      String[] expectedClasses  = {"javax.net.ssl.SSLException", "java.net.UnknownServiceException"};
      String[] expectedMessages = {"unrecognized ssl message",   "unable to find acceptable protocols"};
      for (int i=0; i < expectedClasses.length; i++) {
        String expectedException = "com.marklogic.client.MarkLogicIOException: " + expectedClasses[i] +": ";
        if (exception.startsWith(expectedException) && message.contains(expectedMessages[i])) {
          foundExpected = true;
          break;
        }
      }
      if (!foundExpected) {
        fail("unexpected exception for SSL over HTTPS or HTTP connection:\n"+exception);
      }
    }
  }

  @Test
  public void testHostnameVerifier() throws SSLException, CertificateParsingException {
    // three things our SSLHostnameVerifier will capture
    AtomicReference<String> capturedHost = new AtomicReference<>();
    AtomicReference<String[]> capturedCNs = new AtomicReference<>();
    AtomicReference<String[]> capturedSAs = new AtomicReference<>();

    // this adapter is an SSLHostnameVerifier we'd normally pass to withSSLHostnameVerifier
    SSLHostnameVerifier verifier = (host, cns, alts) -> {
      capturedHost.set(host);
      capturedCNs.set(cns);
      capturedSAs.set(alts);
    };
    // rather than attempt a real SSL connection, let's just test the implementation
    // with some mocks
    SSLHostnameVerifier.HostnameVerifierAdapter adapter = new SSLHostnameVerifier.HostnameVerifierAdapter(verifier);

    // three things we'll pass and expect we can capture when verify is called
    String passedHost = "somehost";
    String[] passedCns = new String[] {"\u82b1\u5b50.co.jp", "bar.com", "foo.com"};
    String[] passedSas = new String[] {"a.foo.com", "104.198.163.83", "a.bar.com", "\u82b1\u5b50.co.jp"};
    // throw some extra information in like a real SSL cert would have
    // but what we're really wanting here are the CNs (common names)
    X500Principal principal = new X500Principal("C=US, ST=California, L=San Carlos, O=API Team, OU=test certificates, " +
      "CN=" + passedCns[2] + ", CN=" + passedCns[1] + ", CN=" + passedCns[0]);
    X509Certificate cert = mock(X509Certificate.class);
    when(cert.getSubjectX500Principal()).thenReturn(principal);

    int type_dnsName = 2;
    int type_ipAddress = 7;
    // subject alts come out as a Collection of 2-entry lists where the first entry
    // is the Integer type and the second entry is the value
    // if the entry is 2 it's a DNS or 7 then it's an IP address
    // according to https://docs.oracle.com/javase/8/docs/api/java/security/cert/X509Certificate.html#getSubjectAlternativeNames--
    Collection<List<?>> listSas = new ArrayList<>();
    listSas.add(Arrays.asList(new Object[] {new Integer(type_dnsName), passedSas[0]}));
    listSas.add(Arrays.asList(new Object[] {new Integer(type_ipAddress), passedSas[1]}));
    listSas.add(Arrays.asList(new Object[] {new Integer(type_dnsName), passedSas[2]}));
    listSas.add(Arrays.asList(new Object[] {new Integer(type_dnsName), passedSas[3]}));
    when(cert.getSubjectAlternativeNames()).thenReturn(listSas);

    // now that we have the cert all mocked with common names and subject alts, call the
    // implementation method HostnameVerifierAdapter.verify to make sure it calls
    // SSLHostnameVerifier.verify(String, String[], String[]) with the expected hostname, cns, and subjectAlts
    adapter.verify(passedHost, cert);

    assertEquals(passedHost, capturedHost.get());
    assertArrayEquals(passedCns, capturedCNs.get());
    assertArrayEquals(passedSas, capturedSAs.get());
  }
}
