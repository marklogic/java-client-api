package com.marklogic.client.fastfunctest;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.MarkLogicIOException;
import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.resource.appservers.ServerManager;
import com.marklogic.mgmt.resource.security.CertificateTemplateManager;
import org.junit.jupiter.api.*;

import javax.net.ssl.SSLContext;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Disabled
public class DatabaseClientFromPropertiesTest extends AbstractFunctionalTest {

	private final static String TEMPLATE_NAME = "func-test-template";
	private final static String TEMPLATE = "<certificate-template-properties xmlns=\"http://marklogic.com/manage\">\n" +
		"  <template-name>" + TEMPLATE_NAME + "</template-name>\n" +
		"  <template-description>Sample description</template-description>\n" +
		"  <key-type>rsa</key-type>\n" +
		"  <key-options />\n" +
		"  <req>\n" +
		"    <version>0</version>\n" +
		"    <subject>\n" +
		"      <countryName>US</countryName>\n" +
		"      <stateOrProvinceName>CA</stateOrProvinceName>\n" +
		"      <localityName>San Carlos</localityName>\n" +
		"      <organizationName>MarkLogic</organizationName>\n" +
		"      <organizationalUnitName>Engineering</organizationalUnitName>\n" +
		"      <emailAddress>nobody@marklogic.com</emailAddress>\n" +
		"    </subject>\n" +
		"  </req>\n" +
		"</certificate-template-properties>";

	private static ManageClient manageClient;

	private Map<String, Object> props;

	@BeforeAll
	public static void beforeClass() {
		manageClient = newManageClient();
		new CertificateTemplateManager(manageClient).save(TEMPLATE);
	}

	@AfterAll
	public static void afterClass() {
//		new CertificateTemplateManager(newManageClient()).delete(TEMPLATE);
	}

	@BeforeEach
	public void before() {
		props = new HashMap<>();
		props.put("marklogic.connection.host", getServer());
		props.put("marklogic.connection.port", getRestServerPort());
		props.put("marklogic.connection.securityContextType", "digest");
		props.put("marklogic.connection.username", getRestReaderUser());
		props.put("marklogic.connection.password", getRestReaderPassword());
//		props.put("marklogic.connection.sslContext", "TLSv1.2");
//		props.put("marklogic.connection.sslHostnameVerifier", "ANY");
//		// It seems reasonable for testing that we have a "simple" trustManager that can be easily used here.
//		// In the real world, the user will likely use "default" for their sslContext
//		props.put("marklogic.connection.trustManager", TRUST_ALL_MANAGER);
	}

	@Test
	public void test() throws Exception {
		ObjectNode payload = newServerPayload(getRestServerName());
		payload.put("ssl-certificate-template", TEMPLATE_NAME);
		new ServerManager(manageClient).save(payload.toString());

		try {
			Map<String, Object> props = new HashMap<>();
			props.put("marklogic.connection.host", getServer());
			props.put("marklogic.connection.port", getRestServerPort());
			props.put("marklogic.connection.securityContextType", "digest");
			props.put("marklogic.connection.username", getRestReaderUser());
			props.put("marklogic.connection.password", getRestReaderPassword());
			props.put("marklogic.connection.sslContext", "TLSv1.2");
			props.put("marklogic.connection.sslHostnameVerifier", "ANY");
			// It seems reasonable for testing that we have a "simple" trustManager that can be easily used here.
			// In the real world, the user will likely use "default" for their sslContext
//			props.put("marklogic.connection.trustManager", TRUST_ALL_MANAGER);

			DatabaseClient sslClient = DatabaseClientFactory.newClient(propName -> props.get(propName));

			DatabaseClient.ConnectionResult result = sslClient.checkConnection();
			System.out.println(result.getStatusCode() + ":" + result.getErrorMessage());

			props.put("marklogic.connection.sslContext", "default");
			sslClient = DatabaseClientFactory.newClient(propName -> props.get(propName));
			try {
				sslClient.checkConnection();
			} catch (MarkLogicIOException ex) {
				assertTrue(ex.getMessage().contains("PKIX path building failed"),
					"Client construction should have failed because the test certificate template isn't " +
						"associated with a CA that the default SSLContext recognizes");
			}

			props.put("marklogic.connection.sslContext", SSLContext.getDefault());
			sslClient = DatabaseClientFactory.newClient(propName -> props.get(propName));
			try {
				sslClient.checkConnection();
			} catch (MarkLogicIOException ex) {
				assertTrue(ex.getMessage().contains("PKIX path building failed"),
					"Client construction should have failed because the test certificate template isn't " +
						"associated with a CA that the default SSLContext recognizes");
			}
		} finally {
			payload.put("ssl-certificate-template", "");
			new ServerManager(manageClient).save(payload.toString());
		}
	}
}
