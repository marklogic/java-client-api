package com.marklogic.client.impl.okhttp;

import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.SecurityContext;
import com.marklogic.client.impl.HTTPKerberosAuthInterceptor;
import com.marklogic.client.impl.HTTPSamlAuthInterceptor;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;
import java.util.Map;

/**
 * Grand plan:
 * 1. Support a set of "marklogic.connection.*" properties to configure any part of any authentication type
 * 2. Provide a Factory class that takes a Properties object (or Map<String,String?) and spits out a DatabaseClient
 * 3. But also.... we need a Factory method that takes a Properties object and spits out an OkHttpClient so that
 * ml-app-deployer can reuse it
 * 4. ml-app-deployer can then collect properties - e.g. mlManage* - and adapt them to the "marklogic.connection.*"
 * properties in order to construct a DatabaseClient or an OkHttpClient.
 * <p>
 * It's probably okay for ml-gradle to depend on something in the impl package.
 * Generally, we're not trying to make a public interface for constructing an OkHttpClient, just a DatabaseClient.
 * But since we control both, we can for now allow this dependency.
 * So - OkHttpClient client = OkHttpUtil?.newClient(host, port, basePath, securityContext), right?
 * Where securityContext has the SSLContext and TrustManager and SSLHostnameVerifier?
 */
public abstract class OkHttpClientFactory {

	public static OkHttpClient.Builder newOkHttpClientBuilder(String host, int port, SecurityContext securityContext) {
		OkHttpClient.Builder clientBuilder = OkHttpUtil.newClientBuilder();
		AuthenticationConfigurer authenticationConfigurer = null;

		// As of 6.1.0, kerberos/saml/certificate are still coded within this class to avoid potential breaks from
		// refactoring. Once the tests for these auth methods are running properly, the code for each can be
		// safely refactored.
		if (securityContext instanceof DatabaseClientFactory.BasicAuthContext) {
			authenticationConfigurer = new BasicAuthenticationConfigurer();
		} else if (securityContext instanceof DatabaseClientFactory.DigestAuthContext) {
			authenticationConfigurer = new DigestAuthenticationConfigurer();
		} else if (securityContext instanceof DatabaseClientFactory.KerberosAuthContext) {
			configureKerberosAuth((DatabaseClientFactory.KerberosAuthContext) securityContext, host, clientBuilder);
		} else if (securityContext instanceof DatabaseClientFactory.CertificateAuthContext) {
		} else if (securityContext instanceof DatabaseClientFactory.SAMLAuthContext) {
			configureSAMLAuth((DatabaseClientFactory.SAMLAuthContext) securityContext, clientBuilder);
		} else if (securityContext instanceof DatabaseClientFactory.MarkLogicCloudAuthContext) {
			authenticationConfigurer = new MarkLogicCloudAuthenticationConfigurer(host, port);
		} else {
			throw new IllegalArgumentException("Unsupported security context: " + securityContext.getClass());
		}

		if (authenticationConfigurer != null) {
			authenticationConfigurer.configureAuthentication(clientBuilder, securityContext);
		}

		SSLContext sslContext = securityContext.getSSLContext();
		X509TrustManager trustManager = securityContext.getTrustManager();

		DatabaseClientFactory.SSLHostnameVerifier sslVerifier = null;
		if (sslContext != null || securityContext instanceof DatabaseClientFactory.CertificateAuthContext) {
			sslVerifier = securityContext.getSSLHostnameVerifier() != null ?
				securityContext.getSSLHostnameVerifier() :
				DatabaseClientFactory.SSLHostnameVerifier.COMMON;
		}

		OkHttpUtil.configureSocketFactory(clientBuilder, sslContext, trustManager);
		OkHttpUtil.configureHostnameVerifier(clientBuilder, sslVerifier);

		return clientBuilder;
	}

	private static void configureKerberosAuth(DatabaseClientFactory.KerberosAuthContext keberosAuthContext, String host, OkHttpClient.Builder clientBuilder) {
		Map<String, String> kerberosOptions = keberosAuthContext.getKrbOptions();
		Interceptor interceptor = new HTTPKerberosAuthInterceptor(host, kerberosOptions);
		clientBuilder.addInterceptor(interceptor);
	}

	private static void configureSAMLAuth(DatabaseClientFactory.SAMLAuthContext samlAuthContext, OkHttpClient.Builder clientBuilder) {
		Interceptor interceptor;
		String authorizationTokenValue = samlAuthContext.getToken();
		if (authorizationTokenValue != null && authorizationTokenValue.length() > 0) {
			interceptor = new HTTPSamlAuthInterceptor(authorizationTokenValue);
		} else if (samlAuthContext.getAuthorizer() != null) {
			interceptor = new HTTPSamlAuthInterceptor(samlAuthContext.getAuthorizer());
		} else if (samlAuthContext.getRenewer() != null) {
			interceptor = new HTTPSamlAuthInterceptor(samlAuthContext.getAuthorization(), samlAuthContext.getRenewer());
		} else
			throw new IllegalArgumentException("Either a call back or renewer expected.");
		clientBuilder.addInterceptor(interceptor);
	}
}
