package com.marklogic.client.test.ssl;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.ForbiddenUserException;
import com.marklogic.client.MarkLogicIOException;
import com.marklogic.client.document.DocumentDescriptor;
import com.marklogic.client.eval.EvalResultIterator;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.test.Common;
import com.marklogic.client.test.junit5.RequireSSLExtension;
import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.resource.appservers.ServerManager;
import com.marklogic.rest.util.Fragment;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.util.FileCopyUtils;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.security.KeyStore;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(RequireSSLExtension.class)
public class TwoWaySSLTest {

	private final static String TEST_DOCUMENT_URI = "/optic/test/musician1.json";
	private final static String KEYSTORE_PASSWORD = "password";

	// Used for creating a temporary JKS (Java KeyStore) file.
	@TempDir
	static Path tempDir;

	private static DatabaseClient securityClient;
	private static ManageClient manageClient;
	private static File keyStoreFile;
	private static File p12File;


	@BeforeAll
	public static void setup() throws Exception {
		if (Common.USE_REVERSE_PROXY_SERVER) {
			return;
		}
		// Create a client using the java-unittest app server - which requires SSL via RequiresSSLExtension - and that
		// talks to the Security database.
		securityClient = Common.newClientBuilder()
			.withUsername(Common.SERVER_ADMIN_USER).withPassword(Common.SERVER_ADMIN_PASS)
			.withSSLProtocol("TLSv1.2")
			.withTrustManager(Common.TRUST_ALL_MANAGER)
			.withSSLHostnameVerifier(DatabaseClientFactory.SSLHostnameVerifier.ANY)
			.withDatabase("Security").build();
		manageClient = Common.newManageClient();

		final String certificateAuthorityId = createCertificateAuthority();
		ClientCertificate clientCertificate = createClientCertificate();
		makeAppServerRequireTwoWaySSL(certificateAuthorityId);

		writeClientCertificateFilesToTempDir(clientCertificate, tempDir);
		createPkcs12File(tempDir);
		createKeystoreFile(tempDir);
		keyStoreFile = new File(tempDir.toFile(), "client.jks");
		p12File = new File(tempDir.toFile(), "client.p12");
	}

	@AfterAll
	public static void teardown() {
		if (Common.USE_REVERSE_PROXY_SERVER) {
			return;
		}
		removeTwoWaySSLConfig();
		deleteCertificateAuthority();
	}

	/**
	 * After two-way SSL is configured on the java-unittest app server, verify that a DatabaseClient using a proper
	 * SSLContext can connect to the app server.
	 */
	@Test
	void digestAuthentication() throws Exception {
		if (Common.USE_REVERSE_PROXY_SERVER) {
			return;
		}

		// This client uses our Java KeyStore file with a client certificate in it, so it should work.
		DatabaseClient clientWithCert = Common.newClientBuilder()
			.withSSLHostnameVerifier(DatabaseClientFactory.SSLHostnameVerifier.ANY)
			.withSSLContext(createSSLContextWithClientCertificate(keyStoreFile))
			.withTrustManager(RequireSSLExtension.newTrustManager())
			.build();

		verifyTestDocumentCanBeRead(clientWithCert);

		// This client uses a new SSL context without the client certificate, so it should fail.
		DatabaseClient clientWithoutCert = Common.newClientBuilder()
			.withSSLHostnameVerifier(DatabaseClientFactory.SSLHostnameVerifier.ANY)
			.withSSLProtocol("TLSv1.2")
			.withTrustManager(RequireSSLExtension.newTrustManager())
			.build();

		// The type of SSL failure varies across Java versions, so not asserting on a particular error message.
		assertThrows(MarkLogicIOException.class,
			() -> clientWithoutCert.newJSONDocumentManager().exists(TEST_DOCUMENT_URI));

		// And now a client that doesn't even try to use SSL. It's not clear if a ForbiddenUserException is correct
		// here, but it's what the Java Client was throwing when this test was written.
		ForbiddenUserException userException = assertThrows(ForbiddenUserException.class,
			() -> Common.newClient().newJSONDocumentManager().exists(TEST_DOCUMENT_URI));
		assertTrue(userException.getMessage().contains("User is not allowed to check the existence of documents"),
			"Unexpected exception: " + userException.getMessage());
	}

	/**
	 * Verifies certificate authentication when a user provides their own SSLContext.
	 */
	@Test
	void certificateAuthenticationWithSSLContext() throws Exception {
		if (Common.USE_REVERSE_PROXY_SERVER) {
			return;
		}

		setAuthenticationToCertificate();
		try {
			SSLContext sslContext = createSSLContextWithClientCertificate(keyStoreFile);
			DatabaseClient client = Common.newClientBuilder()
				.withCertificateAuth(sslContext, RequireSSLExtension.newTrustManager())
				.withSSLHostnameVerifier(DatabaseClientFactory.SSLHostnameVerifier.ANY)
				.build();

			verifyTestDocumentCanBeRead(client);
		} finally {
			setAuthenticationToDigestbasic();
		}
	}

	/**
	 * Verifies certificate authentication when a user provides a file and password, which must point to a PKC12
	 * keystore.
	 */
	@Test
	void certificateAuthenticationWithCertificateFileAndPassword() {
		if (Common.USE_REVERSE_PROXY_SERVER) {
			return;
		}

		setAuthenticationToCertificate();
		try {
			DatabaseClient client = Common.newClientBuilder()
				.withCertificateAuth(p12File.getAbsolutePath(), KEYSTORE_PASSWORD)
				.withTrustManager(RequireSSLExtension.newTrustManager())
				.withSSLHostnameVerifier(DatabaseClientFactory.SSLHostnameVerifier.ANY)
				.build();

			verifyTestDocumentCanBeRead(client);
		} finally {
			setAuthenticationToDigestbasic();
		}
	}

	private void setAuthenticationToCertificate() {
		new ServerManager(manageClient)
			.save(Common.newServerPayload().put("authentication", "certificate").toString());
	}

	private void setAuthenticationToDigestbasic() {
		new ServerManager(manageClient)
			.save(Common.newServerPayload().put("authentication", "digestbasic").toString());
	}

	private void verifyTestDocumentCanBeRead(DatabaseClient client) {
		DocumentDescriptor descriptor = client.newJSONDocumentManager().exists(TEST_DOCUMENT_URI);
		assertNotNull(descriptor);
		assertEquals(TEST_DOCUMENT_URI, descriptor.getUri());
	}

	private SSLContext createSSLContextWithClientCertificate(File keystoreFile) throws Exception {
		KeyStore keyStore = KeyStore.getInstance("JKS");
		keyStore.load(new FileInputStream(keystoreFile), KEYSTORE_PASSWORD.toCharArray());
		KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
		keyManagerFactory.init(keyStore, KEYSTORE_PASSWORD.toCharArray());
		SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
		sslContext.init(
			keyManagerFactory.getKeyManagers(),
			new X509TrustManager[]{RequireSSLExtension.newTrustManager()},
			null);
		return sslContext;
	}

	/**
	 * See https://docs.marklogic.com/pki:create-authority for more information. This results in both a new
	 * CA in MarkLogic and a new "secure credential".
	 */
	private static String createCertificateAuthority() {
		String xquery = "xquery version \"1.0-ml\";\n" +
			"import module namespace pki = \"http://marklogic.com/xdmp/pki\" at \"/MarkLogic/pki.xqy\";\n" +
			"declare namespace x509 = \"http://marklogic.com/xdmp/x509\";\n" +
			"\n" +
			"pki:create-authority(\n" +
			"  \"java-client-test\", \"Java Client Certificate Authority\",\n" +
			"  element x509:subject {\n" +
			"    element x509:countryName            {\"US\"},\n" +
			"    element x509:stateOrProvinceName    {\"California\"},\n" +
			"    element x509:localityName           {\"San Carlos\"},\n" +
			"    element x509:organizationName       {\"MarkLogicJavaClientTest\"},\n" +
			"    element x509:organizationalUnitName {\"Engineering\"},\n" +
			"    element x509:commonName             {\"JavaClientCA\"},\n" +
			"    element x509:emailAddress           {\"java-client@example.org\"}\n" +
			"  },\n" +
			"  fn:current-dateTime(),\n" +
			"  fn:current-dateTime() + xs:dayTimeDuration(\"P365D\"),\n" +
			"  (xdmp:permission(\"admin\",\"read\")))";

		return securityClient.newServerEval().xquery(xquery).evalAs(String.class);
	}

	/**
	 * See https://docs.marklogic.com/pki:authority-create-client-certificate for more information.
	 * The commonName matches that of a known test user so that certificate authentication can be tested too.
	 */
	private static ClientCertificate createClientCertificate() {
		String xquery = "xquery version \"1.0-ml\";\n" +
			"import module namespace sec = \"http://marklogic.com/xdmp/security\" at \"/MarkLogic/security.xqy\"; \n" +
			"import module namespace pki = \"http://marklogic.com/xdmp/pki\" at \"/MarkLogic/pki.xqy\";\n" +
			"declare namespace x509 = \"http://marklogic.com/xdmp/x509\";\n" +
			"\n" +
			"pki:authority-create-client-certificate(\n" +
			"  xdmp:credential-id(\"java-client-test\"),\n" +
			"  element x509:subject {\n" +
			"    element x509:countryName            {\"US\"},\n" +
			"    element x509:stateOrProvinceName    {\"California\"},\n" +
			"    element x509:localityName           {\"San Carlos\"},\n" +
			"    element x509:organizationName       {\"ProgressMarkLogic\"},\n" +
			"    element x509:organizationalUnitName {\"Engineering\"},\n" +
			"    element x509:commonName             {\"JavaClientCertificateUser\"},\n" +
			"    element x509:emailAddress           {\"java.client@example.org\"}\n" +
			"  },\n" +
			"  fn:current-dateTime(),\n" +
			"  fn:current-dateTime() + xs:dayTimeDuration(\"P365D\"))\n";

		EvalResultIterator iter = securityClient.newServerEval().xquery(xquery).eval();
		String cert = null;
		String key = null;
		while (iter.hasNext()) {
			if (cert == null) {
				cert = iter.next().getString();
			} else {
				key = iter.next().getString();
			}
		}
		return new ClientCertificate(cert, key);
	}

	private static class ClientCertificate {
		final String pemEncodedCertificate;
		final String privateKey;

		public ClientCertificate(String pemEncodedCertificate, String privateKey) {
			this.pemEncodedCertificate = pemEncodedCertificate;
			this.privateKey = privateKey;
		}
	}

	/**
	 * Via the RequiresSSLExtension, the app server already requires a 1-way SSL connection. This configures the
	 * app server to both require a client certificate and have that client certificate associated with the
	 * CA that was created earlier in the test.
	 */
	private static void makeAppServerRequireTwoWaySSL(String certificateAuthorityId) {
		String certificateAuthorityCertificate = getCertificateAuthorityCertificate(certificateAuthorityId);
		ObjectNode payload = Common.newServerPayload()
			.put("ssl-require-client-certificate", true)
			.put("ssl-client-issuer-authority-verification", true);
		payload.putArray("ssl-client-certificate-pem").add(certificateAuthorityCertificate);
		new ServerManager(manageClient).save(payload.toString());
	}

	/**
	 * Couldn't find a Manage API endpoint that returns the CA certificate, so directly accessing the Security
	 * database and reading a known URI to get an XML document associated with the CA's secure credential, which has
	 * the certificate in it.
	 */
	private static String getCertificateAuthorityCertificate(String certificateAuthorityId) {
		String certificateUri = String.format("http://marklogic.com/xdmp/credentials/%s", certificateAuthorityId);
		String xml = securityClient.newXMLDocumentManager().read(certificateUri, new StringHandle()).get();
		return new Fragment(xml).getElementValue("/sec:credential/sec:credential-certificate");
	}

	/**
	 * Note that if this test fails and the CA somehow doesn't get deleted, you can delete it manually via the Admin
	 * UI - but you need to delete two things - the CA, and then there's a "java-client-test" Secure Credential that
	 * needs to be deleted as well.
	 */
	private static void deleteCertificateAuthority() {
		String xquery = "xquery version \"1.0-ml\";\n" +
			"import module namespace pki = \"http://marklogic.com/xdmp/pki\" at \"/MarkLogic/pki.xqy\";\n" +
			"\n" +
			"pki:delete-authority(\"java-client-test\")";

		securityClient.newServerEval().xquery(xquery).evalAs(String.class);
	}

	/**
	 * Restores the app server back to only requiring 1-way SSL.
	 */
	private static void removeTwoWaySSLConfig() {
		ObjectNode payload = Common.newServerPayload()
			.put("ssl-require-client-certificate", false)
			.put("ssl-client-issuer-authority-verification", false);
		payload.putArray("ssl-client-certificate-pem");
		new ServerManager(manageClient).save(payload.toString());
	}

	/**
	 * Writes the client certificate PEM and private keys to disk so that they can accessed by the openssl program.
	 *
	 * @param clientCertificate
	 * @param tempDir
	 * @throws IOException
	 */
	private static void writeClientCertificateFilesToTempDir(ClientCertificate clientCertificate, Path tempDir) throws IOException {
		File certFile = new File(tempDir.toFile(), "cert.pem");
		FileCopyUtils.copy(clientCertificate.pemEncodedCertificate.getBytes(), certFile);
		File keyFile = new File(tempDir.toFile(), "client.key");
		FileCopyUtils.copy(clientCertificate.privateKey.getBytes(), keyFile);
	}

	/**
	 * See https://stackoverflow.com/a/8224863/3306099 for where this approach was obtained from.
	 */
	private static void createPkcs12File(Path tempDir) throws Exception {
		ProcessBuilder builder = new ProcessBuilder();
		builder.directory(tempDir.toFile());
		builder.command("openssl", "pkcs12", "-export",
			"-in", "cert.pem", "-inkey", "client.key",
			"-out", "client.p12",
			"-name", "my-client",
			"-passout", "pass:" + KEYSTORE_PASSWORD);

		ExecutorService executorService = Executors.newSingleThreadExecutor();
		Process process = builder.start();
		executorService.submit(new StreamGobbler(process.getInputStream(), System.out::println));
		executorService.submit(new StreamGobbler(process.getErrorStream(), System.err::println));
		int exitCode = process.waitFor();
		assertEquals(0, exitCode, "Unable to create pkcs12 file using openssl");
	}

	private static void createKeystoreFile(Path tempDir) throws Exception {
		ProcessBuilder builder = new ProcessBuilder();
		builder.directory(tempDir.toFile());
		builder.command("keytool", "-importkeystore",
			"-deststorepass", KEYSTORE_PASSWORD,
			"-destkeypass", KEYSTORE_PASSWORD,
			"-destkeystore", "client.jks",
			"-srckeystore", "client.p12",
			"-srcstoretype", "PKCS12",
			"-srcstorepass", KEYSTORE_PASSWORD,
			"-alias", "my-client");

		Process process = builder.start();
		ExecutorService executorService = Executors.newSingleThreadExecutor();
		executorService.submit(new StreamGobbler(process.getInputStream(), System.out::println));
		executorService.submit(new StreamGobbler(process.getErrorStream(), System.err::println));
		int exitCode = process.waitFor();
		assertEquals(0, exitCode, "Unable to create keystore using keytool");
	}

	/**
	 * Copied from https://www.baeldung.com/run-shell-command-in-java .
	 */
	private static class StreamGobbler implements Runnable {
		private InputStream inputStream;
		private Consumer<String> consumer;

		public StreamGobbler(InputStream inputStream, Consumer<String> consumer) {
			this.inputStream = inputStream;
			this.consumer = consumer;
		}

		@Override
		public void run() {
			new BufferedReader(new InputStreamReader(inputStream)).lines()
				.forEach(consumer);
		}
	}
}
