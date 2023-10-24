package com.marklogic.client.test.junit5;

import com.marklogic.client.ext.helper.LoggingObject;
import com.marklogic.client.test.Common;
import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.resource.appservers.ServerManager;
import com.marklogic.mgmt.resource.security.CertificateTemplateManager;
import com.marklogic.rest.util.Fragment;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import javax.net.ssl.X509TrustManager;
import java.io.ByteArrayInputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

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

	/**
	 * @return a trust manager that accepts the public certificate associated with the certificate template created
	 * by this class. "secure" is meant to imply that this provides some level of security by only accepting the
	 * one issuer, as opposed to a "trust everything" approach.
	 */
	public static X509TrustManager newSecureTrustManager() {
		return new X509TrustManager() {
			@Override
			public void checkClientTrusted(X509Certificate[] chain, String authType) {
			}

			@Override
			public void checkServerTrusted(X509Certificate[] chain, String authType) {
			}

			@Override
			public X509Certificate[] getAcceptedIssuers() {
				return new X509Certificate[]{getCertificate()};
			}
		};
	}

	private static X509Certificate getCertificate() {
		CertificateTemplateManager mgr = new CertificateTemplateManager(Common.newManageClient());

		Fragment response = mgr.getCertificatesForTemplate(TEMPLATE_NAME);
		String cert = response.getElementValue("/msec:certificate-list/msec:certificate/msec:pem");
		try {
			return (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(cert.getBytes()));
		} catch (CertificateException e) {
			throw new RuntimeException("Unable to generate X509Certificate: " + e.getMessage(), e);
		}
	}

	private void setSslCertificateTemplate(String templateName) {
		new ServerManager(manageClient).save(
			Common.newServerPayload()
				.put("ssl-certificate-template", templateName)
				.toString()
		);
	}
}
