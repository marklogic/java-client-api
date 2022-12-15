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

package com.marklogic.client.functionaltest;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.junit.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.CertificateAuthContext;
import com.marklogic.client.DatabaseClientFactory.SecurityContext;
import com.marklogic.client.document.TextDocumentManager;
import com.marklogic.client.io.StringHandle;

import junit.framework.Assert;

@Ignore("Ignored because it was previously ignored in build.gradle though without explanation")
public class TestDatabaseClientWithCertBasedAuth extends BasicJavaClientREST {

  public static String newLine = System.getProperty("line.separator");
  public static String temp = System.getProperty("java.io.tmpdir");
  public static String java_home = System.getProperty("java.home");
  public static String server = "CertServer";
  public static String setupServer = "App-Services";
  public static int port = 8071;
  public static int setupPort = 8000;
  public static String host = "localhost";
  public static DatabaseClient secClient;
  public static String localHostname = getBootStrapHostFromML();

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {

    createRESTServerWithDB(server, port);
    createRESTUser("portal", "seekrit", "admin", "rest-admin", "rest-writer", "rest-reader");
    associateRESTServerWithDB(setupServer, "Security");
    SecurityContext secContext = newSecurityContext("admin", "admin");
    secClient = DatabaseClientFactory.newClient(host, setupPort, secContext, getConnType());

    createCACert();
    createCertTemplate();
    createHostTemplate();
    createClientCert("portal");
    convertToHTTPS();
    generateP12("portal");

    createClientCert("blah");
    generateP12("blah");

    addCA();
    associateRESTServerWithDB(server, "Documents");
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {

    removeTrustedCert();
    convertToHTTP();
    removeCertTemplate();
    associateRESTServerWithDB(setupServer, "Documents");
  }

  @Before
  public void setUp() throws Exception {
    clearDB(port);
  }

  @After
  public void tearDown() throws Exception {
  }

  @SuppressWarnings("deprecation")
  @Test
  public void testUserPortal() throws Exception {
    final String query1 = "fn:count(fn:doc())";

    InetAddress addr = java.net.InetAddress.getLocalHost();
    System.out.println("Hostname is : " + addr.getHostName());

    DatabaseClient client = DatabaseClientFactory.newClient(localHostname, port, new CertificateAuthContext(temp + "portal.p12", "abc"));
    int count = client.newServerEval().xquery(query1).eval().next().getNumber().intValue();
    System.out.println(count);
    TextDocumentManager docMgr = client.newTextDocumentManager();
    String docId = "/example/texet.txt";
    StringHandle handle = new StringHandle();
    handle.set("A simple text document");
    docMgr.write(docId, handle);
    System.out.println(client.newServerEval().xquery(query1));
    System.out.println("Doc written");
    Assert.assertEquals(count + 1, client.newServerEval().xquery(query1).eval().next().getNumber().intValue());
    client.release();
  }

  @SuppressWarnings("deprecation")
  @Test
  public void testUserBlah() throws Exception {
    final String query1 = "fn:count(fn:doc())";

    DatabaseClient client = DatabaseClientFactory.newClient(localHostname, port, new CertificateAuthContext(temp + "blah.p12", "abc"));
    try {
      client.newServerEval().xquery(query1).eval().next().getNumber().intValue();
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("Exception is " + e.getClass().getName());
      Assert.assertTrue(e.getClass().getName().equals("com.marklogic.client.FailedRequestException"));
      Assert.assertTrue(e.getMessage().equals("Local message: failed to apply resource at eval: Unauthorized. Server Message: Unauthorized"));

    }
  }

  private static void runQuery(String query) throws Exception {
    secClient.newServerEval().xquery(query).eval();
  }

  public static void createCACert()
      throws Exception {
    StringBuilder q = new StringBuilder();
    q.append("xquery version \"1.0-ml\";");
    q.append("import module \"http://marklogic.com/xdmp/security\" at \"/MarkLogic/security.xqy\";");
    q.append("import module namespace pki = \"http://marklogic.com/xdmp/pki\" at \"/MarkLogic/pki.xqy\";");
    q.append("declare namespace x509 = \"http://marklogic.com/xdmp/x509\";");
    q.append("let $keys := xdmp:rsa-generate()");
    q.append("let $privkey := $keys[1]");
    q.append("let $pubkey := $keys[2]");
    q.append("let $subject :=");
    q.append(" element x509:subject {");
    q.append(" element x509:countryName{\"US\"},");
    q.append(" element x509:organizationName{\"Acme Corporation\"},");
    q.append(" element x509:commonName{\"Acme Corporation CA\"}");
    q.append(" }");
    q.append("let $x509 :=");
    q.append(" element x509:cert {");
    q.append(" element x509:version {2},");
    q.append(" element x509:serialNumber {pki:integer-to-hex(xdmp:random())},");
    q.append(" element x509:issuer {$subject/*},");
    q.append(" element x509:validity {");
    q.append(" element x509:notBefore {fn:current-dateTime()},");
    q.append(" element x509:notAfter  {fn:current-dateTime() + xs:dayTimeDuration(\"P365D\")}");
    q.append(" },");
    q.append("$subject,");
    q.append(" element x509:publicKey{$pubkey},");
    q.append(" element x509:v3ext{");
    q.append(" element x509:basicConstraints {");
    q.append(" attribute critical {\"false\"},");
    q.append(" \"CA:TRUE\"");
    q.append(" },");
    q.append(" element x509:keyUsage{");
    q.append(" attribute critical {\"false\"},");
    q.append(" \"Certificate Sign, CRL Sign\"");
    q.append(" },");
    q.append(" element x509:nsCertType {");
    q.append(" attribute critical {\"false\"},");
    q.append(" \"SSL Server\"");
    q.append(" },");
    q.append(" element x509:subjectKeyIdentifier {");
    q.append(" attribute critical {\"false\"},");
    q.append(" pki:integer-to-hex(xdmp:random())");
    q.append(" }");
    q.append(" }");
    q.append(" }");
    q.append(" let $certificate := xdmp:x509-certificate-generate($x509, $privkey)");
    q.append(" let $dum := xdmp:save(\"" + temp + "ca.cer\", text{$certificate})");
    q.append(" return");
    q.append(" (sec:create-credential(");
    q.append(" \"acme-ca\", \"Acme Certificate Authority\",");
    q.append(" (),(),$certificate, $privkey,");
    q.append("fn:true(), (), xdmp:permission(\"admin\", \"read\")),");
    q.append("pki:insert-trusted-certificates($certificate)");
    q.append(")");
    System.out.println("Creating CA credential");
    try {
      runQuery(q.toString());
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  public static void createCertTemplate()
      throws Exception {
    StringBuilder q = new StringBuilder();
    q.append("xquery version \"1.0-ml\";");
    q.append("import module \"http://marklogic.com/xdmp/security\" at \"/MarkLogic/security.xqy\";");
    q.append("import module namespace pki = \"http://marklogic.com/xdmp/pki\" at \"/MarkLogic/pki.xqy\";");
    q.append("declare namespace x509 = \"http://marklogic.com/xdmp/x509\";");
    q.append("pki:insert-template(");
    q.append("  pki:create-template(");
    q.append("	\"cert-template\", \"testing secure credentials\",");
    q.append("	(), (),");
    q.append("	<req xmlns=\"http://marklogic.com/xdmp/x509\">");
    q.append("	  <version>0</version>");
    q.append("	  <subject>");
    q.append("		<countryName>US</countryName>");
    q.append("		<stateOrProvinceName>CA</stateOrProvinceName>");
    q.append("		<localityName>San Carlos</localityName>");
    q.append("		<organizationName>Acme Corporation</organizationName>");
    q.append("	  </subject>");
    q.append("	  <v3ext>");
    q.append("		<nsCertType critical=\"false\">SSL Server</nsCertType>");
    q.append("		<subjectKeyIdentifier critical=\"false\">{pki:integer-to-hex(xdmp:random())}</subjectKeyIdentifier>");
    q.append("	  </v3ext>");
    q.append("	</req>))");
    System.out.println("Creating Certificate template");
    runQuery(q.toString());
  }

  public static void createHostTemplate()
      throws Exception {
    StringBuilder q = new StringBuilder();
    q.append("xquery version \"1.0-ml\";");
    q.append("import module \"http://marklogic.com/xdmp/security\" at \"/MarkLogic/security.xqy\";");
    q.append("import module namespace pki = \"http://marklogic.com/xdmp/pki\" at \"/MarkLogic/pki.xqy\";");
    q.append("declare namespace x509 = \"http://marklogic.com/xdmp/x509\";");
    q.append("let $csr-pem :=");
    q.append("  xdmp:invoke-function(");
    q.append("	function() {");
    q.append("	  pki:generate-certificate-request(");
    q.append("		pki:get-template-by-name(\"cert-template\")/pki:template-id,");
    q.append("		xdmp:host-name(), (), ())");
    q.append("	},");
    q.append("	<options xmlns=\"xdmp:eval\">");
    q.append("	  <transaction-mode>update-auto-commit</transaction-mode>");
    q.append("	  <isolation>different-transaction</isolation>");
    q.append("	</options>)");
    q.append("let $csr-xml := xdmp:x509-request-extract($csr-pem)");
    q.append("let $ca-xml :=");
    q.append("  xdmp:x509-certificate-extract(");
    q.append("	xdmp:credential(xdmp:credential-id(\"acme-ca\"))");
    q.append("	  /sec:credential-certificate)");
    q.append("let $cert-xml :=");
    q.append("  <cert xmlns=\"http://marklogic.com/xdmp/x509\">");
    q.append("	<version>2</version>");
    q.append("	<serialNumber>{pki:integer-to-hex(xdmp:random())}</serialNumber>");
    q.append("	{$ca-xml/x509:issuer}");
    q.append("	<validity>");
    q.append("	  <notBefore>{fn:current-dateTime()}</notBefore>");
    q.append("	  <notAfter>{fn:current-dateTime() + xs:dayTimeDuration(\"P365D\")}</notAfter>");
    q.append("	</validity>");
    q.append("	{$csr-xml/x509:subject}");
    q.append("	{$csr-xml/x509:publicKey}");
    q.append("	{$csr-xml/x509:v3ext}");
    q.append("  </cert>");
    q.append("let $cert-pem :=");
    q.append("  xdmp:x509-certificate-generate(");
    q.append("	$cert-xml, (),");
    q.append("	<options xmlns=\"ssl:options\">");
    q.append("	  <credential-id>{xdmp:credential-id(\"acme-ca\")}</credential-id>");
    q.append("	</options>)");
    q.append("return");
    q.append("  xdmp:invoke-function(");
    q.append("	function() {");
    q.append("	  pki:insert-signed-certificates($cert-pem)");
    q.append("	},");
    q.append("	<options xmlns=\"xdmp:eval\">");
    q.append("	  <transaction-mode>update-auto-commit</transaction-mode>");
    q.append("	  <isolation>different-transaction</isolation>");
    q.append("	</options>)");
    System.out.println("Creating Host template");
    runQuery(q.toString());
  }

  public static void createClientCert(String commonname)
      throws Exception {
    StringBuilder q = new StringBuilder();
    q.append("xquery version \"1.0-ml\";");
    q.append("import module namespace sec = \"http://marklogic.com/xdmp/security\" at \"/MarkLogic/security.xqy\";");
    q.append("import module namespace pki = \"http://marklogic.com/xdmp/pki\" at \"/MarkLogic/pki.xqy\";");
    q.append("declare namespace x509 = \"http://marklogic.com/xdmp/x509\";");
    q.append("let $validity := element x509:validity {");
    q.append("element x509:notBefore{fn:current-dateTime()},");
    q.append("element x509:notAfter{fn:current-dateTime() + xs:dayTimeDuration(\"P365D\")}}");
    q.append(" let $keys := xdmp:rsa-generate()");
    q.append(" let $privkey := $keys[1]");
    q.append(" let $privkeysave := xdmp:save(\"" + temp + commonname + "priv.pkey\", text{$privkey})");
    q.append(" let $pubkey := $keys[2]");
    q.append(" let $subject :=");
    q.append("element x509:subject {");
    q.append("element x509:countryName{\"US\"},");
    q.append("element x509:organizationName{\"Acme Corporation\"},");
    q.append("element x509:commonName{\"" + commonname + "\"}");
    q.append("}");
    q.append(" let $x509 :=");
    q.append("element x509:cert {");
    q.append("element x509:version {2},");
    q.append("element x509:serialNumber{pki:integer-to-hex(xdmp:random())},");
    q.append("$validity,");
    q.append("$subject,");
    q.append("element x509:publicKey{$pubkey},");
    q.append("element x509:v3ext {");
    q.append("element x509:subjectKeyIdentifier {");
    q.append("attribute critical {\"false\"},");
    q.append("pki:integer-to-hex(xdmp:random())");
    q.append("}");
    q.append("}");
    q.append("}");
    q.append(" let $certificate := xdmp:x509-certificate-generate($x509, $privkey, <options xmlns=\"ssl:options\"><credential-id>{xdmp:credential-id(\"acme-ca\")}</credential-id></options>)");
    q.append(" return xdmp:save(\"" + temp + commonname + ".cer\", text{$certificate})");

    try {
      System.out.println("Creating client credential: " + commonname);
      runQuery(q.toString());
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  public static void convertToHTTPS()
      throws Exception {
    StringBuilder q = new StringBuilder();
    q.append("xquery version \"1.0-ml\";");
    q.append("import module namespace admin = \"http://marklogic.com/xdmp/admin\" at \"/MarkLogic/admin.xqy\";");
    q.append("import module namespace pki = \"http://marklogic.com/xdmp/pki\" at \"/MarkLogic/pki.xqy\";");
    q.append("import module namespace sec= \"http://marklogic.com/xdmp/security\" at \"/MarkLogic/security.xqy\";");
    q.append("declare namespace x509 = \"http://marklogic.com/xdmp/x509\";");
    q.append(" let $cfg := admin:get-configuration()");
    q.append(" let $group-id := xdmp:group()");
    q.append(" let $app-server-id := admin:appserver-get-id($cfg, $group-id, \"" + server + "\")[1]");
    q.append(" let $template := pki:get-template-by-name(\"cert-template\")");
    q.append(" let $template-id := $template/pki:template-id");
    q.append(" let $client-ca :=");
    q.append("  pki:get-certificates(pki:get-trusted-certificate-ids())");
    q.append("	[pki:authority = fn:true()]");
    q.append("	[x509:cert/x509:subject/x509:commonName = \"Acme Corporation CA\"]");
    q.append("	/pki:certificate-id");
    q.append(" let $cfg := admin:appserver-set-authentication($cfg, $app-server-id, \"certificate\")");
    q.append(" let $cfg := admin:appserver-set-ssl-certificate-template($cfg, $app-server-id, $template-id)");
    q.append(" let $cfg := admin:appserver-set-ssl-client-certificate-authorities($cfg, $app-server-id, $client-ca)");
    q.append(" let $cfg := admin:appserver-set-ssl-require-client-certificate($cfg, $app-server-id, fn:true())");
    q.append("return admin:save-configuration($cfg)");
    runQuery(q.toString());
  }

  private static void addCA() throws IOException, InterruptedException {

    String seperator = File.separator;
    System.out.println(java_home + seperator + "lib" + seperator + "security" + seperator + "cacerts");
    System.out.println(temp + "ca.cer");

    Runtime rt = Runtime.getRuntime();
    Process pr = rt.exec(new String[] { "keytool", "-import", "-trustcacerts", "-noprompt", "-storepass", "changeit", "-file", temp + "ca.cer", "-alias", "Acme", "-keystore",
        "\"" + java_home + seperator + "lib" + seperator + "security" + seperator + "cacerts" + "\"" });
    System.out.println(pr.waitFor());
    InputStream is = pr.getInputStream();
    InputStreamReader isr = new InputStreamReader(is);
    BufferedReader buff = new BufferedReader(isr);

    String line;
    System.out.println("Adding CA to trusted certificate: STDOUT");
    while ((line = buff.readLine()) != null)
      System.out.println(line);

    InputStream is1 = pr.getErrorStream();
    InputStreamReader isr1 = new InputStreamReader(is1);
    BufferedReader buff1 = new BufferedReader(isr1);

    String line1;
    System.out.println("Adding CA to trusted certificate: ERR");
    while ((line1 = buff1.readLine()) != null)
      System.out.println(line1);
    Thread.currentThread().sleep(5000L);

  }

  private static void generateP12(String commonname) throws IOException, InterruptedException {
    Runtime rt = Runtime.getRuntime();
    Process pr = rt.exec(new String[] { "openssl", "pkcs12", "-export", "-in", temp + commonname + ".cer", "-inkey", temp + commonname + "priv.pkey", "-out",
        temp + commonname + ".p12", "-passout", "pass:abc" });
    System.out.println(pr.waitFor());
    InputStream is = pr.getInputStream();
    InputStreamReader isr = new InputStreamReader(is);
    BufferedReader buff = new BufferedReader(isr);

    String line;
    while ((line = buff.readLine()) != null)
      System.out.println(line);

    InputStream is1 = pr.getErrorStream();
    InputStreamReader isr1 = new InputStreamReader(is1);
    BufferedReader buff1 = new BufferedReader(isr1);

    String line1;
    while ((line1 = buff1.readLine()) != null)
      System.out.println(line1);
    Thread.currentThread().sleep(5000L);

  }

  public static void convertToHTTP()
      throws Exception {
    StringBuilder q = new StringBuilder();
    q.append("import module namespace sec= \"http://marklogic.com/xdmp/security\" at \"/MarkLogic/security.xqy\";");
    q.append("import module namespace admin= \"http://marklogic.com/xdmp/admin\" at \"/MarkLogic/admin.xqy\";");
    q.append("declare namespace x509 = \"http://marklogic.com/xdmp/x509\";");
    q.append("let $cfg := admin:get-configuration()");
    q.append("let $group-id := xdmp:group()");
    q.append("let $app-server-id := admin:appserver-get-id($cfg, $group-id, \"" + server + "\")[1]");
    q.append("let $cfg := admin:appserver-set-authentication($cfg, $app-server-id, \"digest\")");
    q.append("let $cfg := admin:appserver-set-ssl-certificate-template($cfg, $app-server-id, 0)");
    q.append("let $cfg := admin:appserver-set-ssl-client-certificate-authorities($cfg, $app-server-id, ())");
    q.append("return admin:save-configuration($cfg)");
    runQuery(q.toString());
  }

  public static void removeCertTemplate()
      throws Exception {
    StringBuilder q = new StringBuilder();
    q.append("xquery version \"1.0-ml\";");
    q.append("import module namespace pki = \"http://marklogic.com/xdmp/pki\" at \"/MarkLogic/pki.xqy\";");
    q.append("pki:delete-template(pki:get-template-by-name(\"cert-template\")/pki:template-id)");
    System.out.println("Removing Certificate Template");
    runQuery(q.toString());
  }

  public static void removeTrustedCert()
      throws Exception {
    String seperator = File.separator;
    Runtime rt = Runtime.getRuntime();
    Process pr = rt.exec(new String[] { "keytool", "-delete", "-noprompt", "-storepass", "changeit", "-alias", "Acme", "-keystore",
        "\"" + java_home + seperator + "lib" + seperator + "security" + seperator + "cacerts" + "\"" });
    System.out.println(pr.waitFor());

    StringBuilder q = new StringBuilder();
    q.append("xquery version \"1.0-ml\";");
    q.append("import module namespace admin = \"http://marklogic.com/xdmp/admin\" at \"/MarkLogic/admin.xqy\";");
    q.append("import module namespace pki = \"http://marklogic.com/xdmp/pki\" at \"/MarkLogic/pki.xqy\";");
    q.append("import module namespace sec= \"http://marklogic.com/xdmp/security\" at \"/MarkLogic/security.xqy\";");
    q.append("declare namespace x509 = \"http://marklogic.com/xdmp/x509\";");
    q.append("( \"acme-ca\"	) ! sec:remove-credential(.), ");
    q.append("pki:delete-certificate(pki:get-certificates(pki:get-trusted-certificate-ids())[pki:authority = fn:true()][x509:cert/x509:subject/x509:commonName = (\"Acme Corporation CA\")]/pki:certificate-id/text())");
    System.out.println("Removing from Trusted Certificates");
    runQuery(q.toString());
  }

  public static void removeCredentials()
      throws Exception {
    StringBuilder q = new StringBuilder();
    q.append("xquery version \"1.0-ml\";");
    q.append("import module \"http://marklogic.com/xdmp/security\" at \"/MarkLogic/security.xqy\";");
    q.append("xdmp:credentials()//sec:credential-name/text()! sec:remove-credential(.)");
    runQuery(q.toString());
  }

  public static void clearDB(int port) {
    DefaultHttpClient client = null;
    try {
      InputStream jsonstream = null;
      // In case of SSL use 8002 port to clear DB contents.
      client = new DefaultHttpClient();
      client.getCredentialsProvider().setCredentials(
          new AuthScope(host, 8002),
          new UsernamePasswordCredentials("admin", "admin"));
      HttpGet getrequest = new HttpGet("http://" + host + ":8002/manage/v2/servers/" + server + "/properties?group-id=Default&format=json");
      HttpResponse response1 = client.execute(getrequest);
      jsonstream = response1.getEntity().getContent();
      JsonNode jnode = new ObjectMapper().readTree(jsonstream);
      String dbName = jnode.get("content-database").asText();
      System.out.println("App Server's content database properties value from ClearDB is :" + dbName);

      ObjectMapper mapper = new ObjectMapper();
      ObjectNode mainNode = mapper.createObjectNode();

      mainNode.put("operation", "clear-database");

      HttpPost post = new HttpPost("http://" + host + ":8002" + "/manage/v2/databases/" + dbName);
      post.addHeader("Content-type", "application/json");
      post.setEntity(new StringEntity(mainNode.toString()));

      HttpResponse response = client.execute(post);
      HttpEntity respEntity = response.getEntity();
      if (response.getStatusLine().getStatusCode() == 400) {
        System.out.println("Database contents cleared");
      }
      else if (respEntity != null) {
        // EntityUtils to get the response content
        String content = EntityUtils.toString(respEntity);
        System.out.println(content);
      }
      else {
        System.out.println("No Proper Response from clearDB in SSL.");
      }

    } catch (Exception e) {
      // writing error to Log
      e.printStackTrace();
    } finally {
      client.getConnectionManager().shutdown();
    }
  }

}
