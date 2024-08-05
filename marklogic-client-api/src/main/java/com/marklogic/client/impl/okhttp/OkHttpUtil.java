/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.impl.okhttp;

import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.impl.HTTPKerberosAuthInterceptor;
import com.marklogic.client.impl.HTTPSamlAuthInterceptor;
import com.marklogic.client.impl.SSLUtil;
import okhttp3.ConnectionPool;
import okhttp3.CookieJar;
import okhttp3.Dns;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

import javax.net.SocketFactory;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Contains convenience methods for constructing an OkHttpClient.Builder so that it can be used in places other than
 * only {@code OkHttpServices}. This code was moved here from OkHttpServices without any modification during the move
 * (other than the method {@code initializeSslContext} being extracted for readability purposes).
 *
 * @since 6.1.0
 */
public abstract class OkHttpUtil {

	final private static ConnectionPool connectionPool = new ConnectionPool();

	public static OkHttpClient.Builder newOkHttpClientBuilder(String host, DatabaseClientFactory.SecurityContext securityContext) {
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
			authenticationConfigurer = new MarkLogicCloudAuthenticationConfigurer(host);
		} else if (securityContext instanceof DatabaseClientFactory.OAuthContext) {
			authenticationConfigurer = new OAuthAuthenticationConfigurer();
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

	/**
	 * @return an OkHttpClient.Builder initialized with a sensible set of defaults that can then have authentication
	 * configured
	 */
	static OkHttpClient.Builder newClientBuilder() {
		return new OkHttpClient.Builder()
			.followRedirects(false)
			.followSslRedirects(false)
			// all clients share a single connection pool
			.connectionPool(connectionPool)
			// cookies are ignored (except when a Transaction is being used)
			.cookieJar(CookieJar.NO_COOKIES)
			// no timeouts since some of our clients' reads and writes can be massive
			.readTimeout(0, TimeUnit.SECONDS)
			.writeTimeout(0, TimeUnit.SECONDS)
			// prefer ipv4 to ipv6
			.dns(new DnsImpl());
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

	/**
	 * Configure the hostname verifier for the given OkHttpClient.Builder based on the given SSLHostnameVerifier.
	 *
	 * @param clientBuilder
	 * @param sslVerifier
	 */
	static void configureHostnameVerifier(OkHttpClient.Builder clientBuilder, DatabaseClientFactory.SSLHostnameVerifier sslVerifier) {
		HostnameVerifier hostnameVerifier = null;
		if (DatabaseClientFactory.SSLHostnameVerifier.ANY.equals(sslVerifier)) {
			hostnameVerifier = (hostname, session) -> true;
		} else if (DatabaseClientFactory.SSLHostnameVerifier.COMMON.equals(sslVerifier) ||
			DatabaseClientFactory.SSLHostnameVerifier.STRICT.equals(sslVerifier)) {
			hostnameVerifier = null;
		} else if (sslVerifier != null) {
			hostnameVerifier = new DatabaseClientFactory.SSLHostnameVerifier.HostnameVerifierAdapter(sslVerifier);
		}
		if (hostnameVerifier != null) {
			clientBuilder.hostnameVerifier(hostnameVerifier);
		}
	}

	/**
	 * Configure the socket factory used by the given OkHttpClient.Builder based on whether SSL is required or not.
	 *
	 * @param clientBuilder
	 * @param sslContext
	 * @param trustManager
	 */
	static void configureSocketFactory(OkHttpClient.Builder clientBuilder, SSLContext sslContext, X509TrustManager trustManager) {
		/**
		 * Per https://square.github.io/okhttp/3.x/okhttp/okhttp3/OkHttpClient.Builder.html#sslSocketFactory-javax.net.ssl.SSLSocketFactory- ,
		 * OkHttp requires a TrustManager to be specified so that it can build a clean certificate chain. If trustManager
		 * is not null, then the given sslContext is assumed to have been initialized already. If trustManager is
		 * null, then the given sslContext is assumed to not have been initialized, and an attempt is made to initialize
		 * it using the JVM's default TrustManager.
		 */
		if (sslContext == null) {
			clientBuilder.socketFactory(new SocketFactoryDelegator(SocketFactory.getDefault()));
		} else if (trustManager != null) {
			clientBuilder.sslSocketFactory(new SSLSocketFactoryDelegator(sslContext.getSocketFactory()), trustManager);
		} else {
			initializeSslContext(clientBuilder, sslContext);
		}
	}

	/**
	 * This would be for an uninitialized SSLContext that depends on the default TrustManagerFactory.
	 *
	 * @param clientBuilder
	 * @param sslContext
	 */
	private static void initializeSslContext(OkHttpClient.Builder clientBuilder, SSLContext sslContext) {
		TrustManager[] trustManagers = SSLUtil.getDefaultTrustManagers();
		try {
			// In a future release, we may want to check if getSocketFactory() works already, implying that the
			// SSLContext has already been initialized. However, if that's the case, then it's not guaranteed that
			// the default trust manager is the appropriate one to pass to OkHttp.
			sslContext.init(null, trustManagers, null);
		} catch (KeyManagementException e) {
			throw new RuntimeException("Unable to initialize SSLContext; cause: " + e.getMessage(), e);
		}
		clientBuilder.sslSocketFactory(new SSLSocketFactoryDelegator(sslContext.getSocketFactory()), (X509TrustManager) trustManagers[0]);
	}

	static class DnsImpl implements Dns {
		@Override
		public List<InetAddress> lookup(String hostname) throws UnknownHostException {
			List<InetAddress> rawAddresses = Dns.SYSTEM.lookup(hostname);
			List<InetAddress> ipv4Addresses = new ArrayList<>();
			for (InetAddress address : rawAddresses) {
				if (address instanceof Inet4Address) {
					ipv4Addresses.add(address);
				}
			}
			return ipv4Addresses.isEmpty() ? rawAddresses : ipv4Addresses;
		}
	}
}
