package com.marklogic.client.functionaltest;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.CertificateAuthContext;
import com.marklogic.client.document.TextDocumentManager;
import com.marklogic.client.io.StringHandle;
import com.marklogic.xcc.AdhocQuery;
import com.marklogic.xcc.ContentSource;
import com.marklogic.xcc.ContentSourceFactory;
import com.marklogic.xcc.RequestOptions;
import com.marklogic.xcc.ResultSequence;
import com.marklogic.xcc.Session;
import com.marklogic.xcc.exceptions.RequestException;
import com.marklogic.xcc.exceptions.XccConfigException;

import junit.framework.Assert;

public class TestDatabaseClientWithCertBasedAuth extends BasicJavaClientREST{
	
	private static HashMap<String, ContentSource> csMap = new HashMap<String, ContentSource>();
    private static Session session;
    public static String newLine = System.getProperty("line.separator");
    public static  ContentSource contentSource;
    public static String XCC_URI = "xcc://admin:admin@localhost:8000";
    public static String temp = System.getProperty("java.io.tmpdir");
    public static String java_home = System.getProperty("java.home");
    public static String server = "CertServer";
    public static int port = 8071;
    public static String host = "localhost";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		createRESTServerWithDB(server, port);
		createRESTUser("portal", "seekrit", "admin","rest-admin","rest-writer","rest-reader" );
		associateRESTServerWithDB(server,"Security");
		createCACert();
		
		createCertTemplate();
		createHostTemplate();
		createClientCert("portal");
		convertToHTTPS();
		generateP12("portal");
		
		createClientCert("blah");
		generateP12("blah");
		
		addCA();
		associateRESTServerWithDB(server,"Documents");
	}


	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		associateRESTServerWithDB(server,"Security");
		removeTrustedCert();
		convertToHTTP();
		removeCertTemplate();
		associateRESTServerWithDB(server,"Documents");
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
	public void testUserPortal() throws Exception{
		final String query1 = "fn:count(fn:doc())";
		
		InetAddress addr = java.net.InetAddress.getLocalHost();    
        String hostname = addr.getCanonicalHostName(); 
		
		DatabaseClient client = DatabaseClientFactory.newClient(hostname, port, new CertificateAuthContext(temp+"portal.p12","abc"));
		int count = client.newServerEval().xquery(query1).eval().next().getNumber().intValue();
		System.out.println(count);
		TextDocumentManager docMgr = client.newTextDocumentManager();
	    String docId = "/example/texet.txt";
	    StringHandle handle = new StringHandle();
	    handle.set("A simple text document");
	    docMgr.write(docId, handle);
	    System.out.println(client.newServerEval().xquery(query1));
	    System.out.println("Doc written");
	    Assert.assertEquals(count+1, client.newServerEval().xquery(query1).eval().next().getNumber().intValue());
	    client.release();
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testUserBlah() throws Exception{
		final String query1 = "fn:count(fn:doc())";
		
		InetAddress addr = java.net.InetAddress.getLocalHost();    
        String hostname = addr.getCanonicalHostName(); 
		
		DatabaseClient client = DatabaseClientFactory.newClient(hostname, port, new CertificateAuthContext(temp+"blah.p12","abc"));
		try{
			client.newServerEval().xquery(query1).eval().next().getNumber().intValue();
		}
		catch(Exception e){
			e.printStackTrace();
			System.out.println("Exception is "+e.getClass().getName());
			 Assert.assertTrue(e.getClass().getName().equals("com.marklogic.client.FailedRequestException"));
				
		}
	}
	
	
	private static ResultSequence runQuery(String query) throws XccConfigException, URISyntaxException, RequestException {
	        session = getSession();
	        AdhocQuery aquery = session.newAdhocQuery(query);

	        RequestOptions options = new RequestOptions();
	        options.setCacheResult(false);
	        options.setDefaultXQueryVersion("1.0-ml");
	        aquery.setOptions(options);
	        return session.submitRequest(aquery);
	}
	
    private static Session getSession() throws XccConfigException, URISyntaxException{
	   	if (contentSource == null ) {
	   		contentSource = ContentSourceFactory.newContentSource(new URI(XCC_URI));
	    	csMap.put(XCC_URI, contentSource);
	    }
	   	return csMap.get(XCC_URI).newSession();
    }
    
    
    public static void createCACert()
            throws XccConfigException, RequestException, URISyntaxException {
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
            q.append(" element x509:countryName      {\"US\"},");
            q.append(" element x509:organizationName {\"Acme Corporation\"},");
            q.append(" element x509:commonName       {\"Acme Corporation CA\"}");
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
            q.append(" element x509:publicKey {$pubkey},");
            q.append(" element x509:v3ext {");
            q.append(" element x509:basicConstraints {");
            q.append(" attribute critical {\"false\"},");
            q.append(" \"CA:TRUE\"");
            q.append(" },");
            q.append(" element x509:keyUsage {");
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
            q.append("let $certificate := xdmp:x509-certificate-generate($x509, $privkey)");
            q.append("let $dum := xdmp:save(\""+temp+"ca.cer\", text{$certificate})");
            q.append(" return");
            q.append(" ( sec:create-credential(");
            q.append(" \"acme-ca\", \"Acme Certificate Authority\",");
            q.append("	  (), (), $certificate, $privkey,");
            q.append("fn:true(), (), xdmp:permission(\"admin\", \"read\")),");
            q.append("pki:insert-trusted-certificates($certificate)");
            q.append(")");
            try{
            	runQuery(q.toString());
            }
            catch (Exception e){
            	e.printStackTrace();
            }
            
        }
    
    public static void createCertTemplate()
            throws XccConfigException, RequestException, URISyntaxException {
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
            runQuery(q.toString());
        }
    
    public static void createHostTemplate()
            throws XccConfigException, RequestException, URISyntaxException {
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

            runQuery(q.toString());
        }
    
    public static void createClientCert(String commonname)
            throws XccConfigException, RequestException, URISyntaxException {
            StringBuilder q = new StringBuilder();
            q.append("xquery version \"1.0-ml\";");
            q.append("import module namespace sec = \"http://marklogic.com/xdmp/security\" at \"/MarkLogic/security.xqy\";");
            q.append("import module namespace pki = \"http://marklogic.com/xdmp/pki\" at \"/MarkLogic/pki.xqy\";");
            q.append("declare namespace x509 = \"http://marklogic.com/xdmp/x509\";");
            q.append("let $validity :=   element x509:validity {");
            q.append("element x509:notBefore {fn:current-dateTime()},");
            q.append("element x509:notAfter  {fn:current-dateTime() + xs:dayTimeDuration(\"P365D\")}}");
            q.append(" let $keys := xdmp:rsa-generate()");
            q.append(" let $privkey := $keys[1]");
            q.append(" let $privkeysave := xdmp:save(\""+temp+commonname+"priv.pkey\", text{$privkey})");
            q.append(" let $pubkey := $keys[2]");
            q.append(" let $subject :=");
            q.append("element x509:subject {");
            q.append("  element x509:countryName{\"US\"},");
            q.append("  element x509:organizationName{\"Acme Corporation\"},");
            q.append("  element x509:commonName{\""+commonname+"\"}");
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
            q.append("return xdmp:save(\""+temp+commonname+".cer\", text{$certificate})");
            
            try{
            	 runQuery(q.toString());
            }
            catch(Exception e){
            	e.printStackTrace();
            }
           
        }
    
    public static void convertToHTTPS()
            throws XccConfigException, RequestException, URISyntaxException {
            StringBuilder q = new StringBuilder();
            q.append("xquery version \"1.0-ml\";");
            q.append("import module namespace admin = \"http://marklogic.com/xdmp/admin\" at \"/MarkLogic/admin.xqy\";");
            q.append("import module namespace pki = \"http://marklogic.com/xdmp/pki\" at \"/MarkLogic/pki.xqy\";");
            q.append("import module namespace sec= \"http://marklogic.com/xdmp/security\" at \"/MarkLogic/security.xqy\";");
            q.append("declare namespace x509 = \"http://marklogic.com/xdmp/x509\";");
            q.append(" let $cfg := admin:get-configuration()");
            q.append(" let $group-id := xdmp:group()");
            q.append(" let $app-server-id := admin:appserver-get-id($cfg, $group-id, \""+server+"\")[1]");
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
		System.out.println(java_home+seperator+"lib"+seperator+"security"+seperator+"cacerts");
		System.out.println(temp+"ca.cer");
		
		Runtime rt = Runtime.getRuntime();
		//Process pr =    rt.exec("keytool -import -trustcacerts -noprompt  -storepass changeit -file "+ temp+"ca.cer -alias Acme");
		Process pr =    rt.exec(new String[]{"keytool", "-import", "-trustcacerts", "-noprompt","-storepass","changeit", "-file", temp+"ca.cer", "-alias", "Acme", "-keystore", "\""+ java_home+seperator+"lib"+seperator+"security"+seperator+"cacerts"+"\""});
		System.out.println(pr.waitFor());
		InputStream is = pr.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader buff = new BufferedReader (isr);

		String line;
		while((line = buff.readLine()) != null)
		    System.out.println(line);

		InputStream is1 = pr.getErrorStream();
		InputStreamReader isr1 = new InputStreamReader(is1);
		BufferedReader buff1 = new BufferedReader (isr1);

		String line1;
		while((line1 = buff1.readLine()) != null)
		    System.out.println(line1);
		Thread.currentThread().sleep(5000L);
		
	}

	private static void generateP12(String commonname) throws IOException, InterruptedException {
		Runtime rt = Runtime.getRuntime();
		Process pr =    rt.exec(new String[]{"openssl","pkcs12","-export","-in",temp+commonname+".cer","-inkey", temp+commonname+"priv.pkey", "-out", temp+commonname+".p12","-passout", "pass:abc"});
		System.out.println(pr.waitFor());
		InputStream is = pr.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader buff = new BufferedReader (isr);

		String line;
		while((line = buff.readLine()) != null)
		    System.out.println(line);

		InputStream is1 = pr.getErrorStream();
		InputStreamReader isr1 = new InputStreamReader(is1);
		BufferedReader buff1 = new BufferedReader (isr1);

		String line1;
		while((line1 = buff1.readLine()) != null)
		    System.out.println(line1);
		Thread.currentThread().sleep(5000L);
		
	}
    
    public static void convertToHTTP()
            throws XccConfigException, RequestException, URISyntaxException {
            StringBuilder q = new StringBuilder();
            q.append("import module namespace sec= \"http://marklogic.com/xdmp/security\" at \"/MarkLogic/security.xqy\";");
            q.append("import module namespace admin= \"http://marklogic.com/xdmp/admin\" at \"/MarkLogic/admin.xqy\";");
            q.append("declare namespace x509 = \"http://marklogic.com/xdmp/x509\";");
            q.append("let $cfg := admin:get-configuration()");
            q.append("let $group-id := xdmp:group()");
            q.append("let $app-server-id := admin:appserver-get-id($cfg, $group-id, \""+server+"\")[1]");
            q.append("let $cfg := admin:appserver-set-authentication($cfg, $app-server-id, \"basic\")");
            q.append("let $cfg := admin:appserver-set-ssl-certificate-template($cfg, $app-server-id, 0)");
            q.append("let $cfg := admin:appserver-set-ssl-client-certificate-authorities($cfg, $app-server-id, ())");
            q.append("return admin:save-configuration($cfg)");
            runQuery(q.toString());
    }
    
    public static void removeCertTemplate()
            throws XccConfigException, RequestException, URISyntaxException {
            StringBuilder q = new StringBuilder();
            q.append("xquery version \"1.0-ml\";");
            q.append("import module namespace pki = \"http://marklogic.com/xdmp/pki\" at \"/MarkLogic/pki.xqy\";");
            q.append("pki:delete-template(pki:get-template-by-name(\"cert-template\")/pki:template-id)");
            runQuery(q.toString());
    }
    
    public static void removeTrustedCert()
            throws Exception {
    		String seperator = File.separator;
			Runtime rt = Runtime.getRuntime();
			Process pr =    rt.exec(new String[]{"keytool", "-delete", "-noprompt","-storepass","changeit", "-alias", "Acme", "-keystore", "\""+ java_home+seperator+"lib"+seperator+"security"+seperator+"cacerts"+"\""});
			System.out.println(pr.waitFor());
		
	        StringBuilder q = new StringBuilder();
            q.append("xquery version \"1.0-ml\";");
            q.append("import module namespace admin = \"http://marklogic.com/xdmp/admin\" at \"/MarkLogic/admin.xqy\";");
            q.append("import module namespace pki = \"http://marklogic.com/xdmp/pki\" at \"/MarkLogic/pki.xqy\";");
            q.append("import module namespace sec= \"http://marklogic.com/xdmp/security\" at \"/MarkLogic/security.xqy\";");
            q.append("declare namespace x509 = \"http://marklogic.com/xdmp/x509\";");
            q.append("( \"acme-ca\"	) ! sec:remove-credential(.), ");
            q.append("pki:delete-certificate(pki:get-certificates(pki:get-trusted-certificate-ids())[pki:authority = fn:true()][x509:cert/x509:subject/x509:commonName = (\"Acme Corporation CA\")]/pki:certificate-id/text())");
            runQuery(q.toString());
    }
    
    public static void removeCredentials()
            throws XccConfigException, RequestException, URISyntaxException {
            StringBuilder q = new StringBuilder();
            q.append("xquery version \"1.0-ml\";");
            q.append("import module \"http://marklogic.com/xdmp/security\" at \"/MarkLogic/security.xqy\";");
            q.append("xdmp:credentials()//sec:credential-name/text()! sec:remove-credential(.)");
            runQuery(q.toString());
    }
    
    public static void clearDB(int port) {
		DefaultHttpClient client = null;
		try {
			InputStream jsonstream=null;
			 // In case of SSL use 8002 port to clear DB contents.
			client = new DefaultHttpClient();
			client.getCredentialsProvider().setCredentials(
					new AuthScope(host, 8002),
					new UsernamePasswordCredentials("admin", "admin"));
            HttpGet getrequest = new HttpGet("http://localhost:8002/manage/v2/servers/"+server+"/properties?group-id=Default&format=json");
			HttpResponse response1 = client.execute(getrequest);
			jsonstream = response1.getEntity().getContent();
			JsonNode jnode= new ObjectMapper().readTree(jsonstream);
			String dbName = jnode.get("content-database").asText();
			System.out.println("App Server's content database properties value from ClearDB is :"+ dbName);
			
			ObjectMapper mapper = new ObjectMapper();
			ObjectNode mainNode = mapper.createObjectNode();
			
			mainNode.put("operation", "clear-database");
			
			HttpPost post = new HttpPost("http://localhost:8002"+ "/manage/v2/databases/" + dbName);
			post.addHeader("Content-type", "application/json");
			post.setEntity(new StringEntity(mainNode.toString()));

			HttpResponse response = client.execute(post);
			HttpEntity respEntity = response.getEntity();
			if (response.getStatusLine().getStatusCode() == 400) {
				System.out.println("Database contents cleared");
			}
			else if (respEntity != null) {
				// EntityUtils to get the response content
				String content =  EntityUtils.toString(respEntity);
				System.out.println(content);
			}
			else {
				System.out.println("No Proper Response from clearDB in SSL.");
			}
		
	
		} catch (Exception e) {
			// writing error to Log
			e.printStackTrace();
		}
		finally {
			client.getConnectionManager().shutdown();
		}
	}
	
   
}
