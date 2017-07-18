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
package com.marklogic.client.test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.security.auth.x500.X500Principal;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference
import org.junit.Test;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.DigestAuthContext;
import com.marklogic.client.DatabaseClientFactory.SSLHostnameVerifier;
import com.marklogic.client.document.TextDocumentManager;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.impl.OkHttpServices;

public class SSLTest {
  @Test
  public void testSSLAuth() throws NoSuchAlgorithmException, KeyManagementException {

    // create an SSL context
    SSLContext sslContext = SSLContext.getInstance("SSLv3");
    sslContext.init(null, new TrustManager[] { mock(X509TrustManager.class) }, null);

    // create the client
    DatabaseClient client = DatabaseClientFactory.newClient(Common.HOST, Common.PORT, new DigestAuthContext("MyFooUser", "x")
      .withSSLContext(sslContext)
      .withSSLHostnameVerifier(SSLHostnameVerifier.ANY));


    String expectedException = "com.marklogic.client.MarkLogicIOException: " +
      "javax.net.ssl.SSLException: Unrecognized SSL message, plaintext connection?";
    String exception = "";

    try {
      // make use of the client connection so we get an auth error
      TextDocumentManager docMgr = client.newTextDocumentManager();
      String docId = "/example/text.txt";
      StringHandle handle = new StringHandle();
      handle.set("A simple text document");
      docMgr.write(docId, handle);
      // the next line will only run if write doesn't throw an exception
      docMgr.delete(docId);
    }
    catch (Exception e) {
      exception = e.toString();
    }
    assertEquals(expectedException, exception);

  }

  private static class SSLTestServices extends OkHttpServices {
    static class SSLTestHostnameVerifierAdapter extends SSLTestServices.HostnameVerifierAdapter {
      SSLTestHostnameVerifierAdapter(SSLHostnameVerifier hostnameVerifier) {
        super(hostnameVerifier);
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
    SSLTestServices.SSLTestHostnameVerifierAdapter adapter = new SSLTestServices.SSLTestHostnameVerifierAdapter(verifier);

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
