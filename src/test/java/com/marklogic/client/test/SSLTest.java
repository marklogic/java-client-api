/*
 * Copyright 2012-2015 MarkLogic Corporation
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

import static org.junit.Assert.assertEquals;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import org.junit.Test;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.DatabaseClientFactory.SSLHostnameVerifier;
import com.marklogic.client.document.TextDocumentManager;
import com.marklogic.client.io.StringHandle;

public class SSLTest {
    @Test
    public void testSSLAuth() throws NoSuchAlgorithmException, KeyManagementException {

        // create a trust manager
        // (note: a real application should verify certificates)
        TrustManager naiveTrustMgr = new X509TrustManager() {
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
        };

        // create an SSL context
        SSLContext sslContext = SSLContext.getInstance("SSLv3");
        sslContext.init(null, new TrustManager[] { naiveTrustMgr }, null);

        // create the client
        DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8012, "MyFooUser", "x", Authentication.DIGEST, sslContext, SSLHostnameVerifier.ANY);


        String expectedException = "com.sun.jersey.api.client.ClientHandlerException: javax.net.ssl.SSLPeerUnverifiedException: peer not authenticated";
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
}
