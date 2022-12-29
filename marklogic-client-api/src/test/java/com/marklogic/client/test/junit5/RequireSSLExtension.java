package com.marklogic.client.test.junit5;

import com.marklogic.client.ext.helper.LoggingObject;
import com.marklogic.client.test.Common;
import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.resource.appservers.ServerManager;
import com.marklogic.mgmt.resource.security.CertificateTemplateManager;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * Use this on tests that require an app server to require SSL connections. The app server will be modified to require
 * SSL connections before any test runs and will then be restored back to normal after all tests in the test class run.
 */
public class RequireSSLExtension extends LoggingObject implements BeforeAllCallback, AfterAllCallback {

	private ManageClient manageClient;
	private final static String TEMPLATE_NAME = "java-unittest-template";
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
		"      <emailAddress>java-unittest@marklogic.com</emailAddress>\n" +
		"    </subject>\n" +
		"  </req>\n" +
		"</certificate-template-properties>";

	@Override
	public void beforeAll(ExtensionContext context) {
		manageClient = Common.newManageClient();
		CertificateTemplateManager mgr = new CertificateTemplateManager(manageClient);
		mgr.save(TEMPLATE);
		mgr.generateTemporaryCertificate(TEMPLATE_NAME, Common.HOST);
		logger.info("Requiring SSL on app server: " + Common.SERVER_NAME);
		setSslCertificateTemplate(TEMPLATE_NAME);
	}

	@Override
	public void afterAll(ExtensionContext context) {
		setSslCertificateTemplate("");
		logger.info("Removing requirement for SSL on app server: " + Common.SERVER_NAME);
		new CertificateTemplateManager(manageClient).delete(TEMPLATE);
	}

	private void setSslCertificateTemplate(String templateName) {
		new ServerManager(manageClient).save(
			Common.newServerPayload()
				.put("ssl-certificate-template", templateName)
				.toString()
		);
	}
}
