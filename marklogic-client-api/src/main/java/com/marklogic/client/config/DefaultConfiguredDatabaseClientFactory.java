package com.marklogic.client.config;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;

/**
 * Default implementation for constructing a new instance of DatabaseClient based on the inputs in an instance of
 * DatabaseClientConfig.
 */
public class DefaultConfiguredDatabaseClientFactory implements ConfiguredDatabaseClientFactory {

	/**
	 * @param config
	 * @return
	 */
	@Override
	public DatabaseClient newDatabaseClient(DatabaseClientConfig config) {
		DatabaseClientFactory.SecurityContext securityContext = buildSecurityContext(config);
		securityContext = applySslConfig(config, securityContext);
		return createClient(config, securityContext);
	}

	/**
	 * @param config
	 * @param securityContext
	 * @return
	 */
	protected DatabaseClient createClient(DatabaseClientConfig config, DatabaseClientFactory.SecurityContext securityContext) {
		final String host = config.getHost();
		final int port = config.getPort();
		final String database = config.getDatabase();
		final DatabaseClient.ConnectionType connectionType = config.getConnectionType();

		if (connectionType == null) {
			if (securityContext == null) {
				if (database == null) {
					return DatabaseClientFactory.newClient(host, port);
				}
				return DatabaseClientFactory.newClient(host, port, database);
			}
			if (database == null) {
				return DatabaseClientFactory.newClient(host, port, securityContext);
			}
			return DatabaseClientFactory.newClient(host, port, database, securityContext);
		} else {
			if (securityContext == null) {
				if (database == null) {
					return DatabaseClientFactory.newClient(host, port, null, connectionType);
				}
				return DatabaseClientFactory.newClient(host, port, database, null, connectionType);
			}
			if (database == null) {
				return DatabaseClientFactory.newClient(host, port, securityContext, connectionType);
			}
			return DatabaseClientFactory.newClient(host, port, database, securityContext, connectionType);
		}
	}

	/**
	 * @param config
	 * @return
	 */
	protected DatabaseClientFactory.SecurityContext buildSecurityContext(DatabaseClientConfig config) {
		SecurityContextType securityContextType = config.getSecurityContextType();
		if (SecurityContextType.BASIC.equals(securityContextType)) {
			return new DatabaseClientFactory.BasicAuthContext(config.getUsername(), config.getPassword());
		} else if (SecurityContextType.CERTIFICATE.equals(securityContextType)) {
			return buildCertificateAuthContent(config);
		} else if (SecurityContextType.DIGEST.equals(securityContextType)) {
			return new DatabaseClientFactory.DigestAuthContext(config.getUsername(), config.getPassword());
		} else if (SecurityContextType.KERBEROS.equals(securityContextType)) {
			return new DatabaseClientFactory.KerberosAuthContext(config.getExternalName());
		} else if (SecurityContextType.NONE.equals(securityContextType)) {
			return null;
		} else {
			throw new IllegalArgumentException("Unsupported SecurityContextType: " + securityContextType);
		}
	}

	/**
	 * Uses the certificate-related properties in the config object to construct a new CertificateAuthContext.
	 *
	 * @param config
	 * @return
	 */
	protected DatabaseClientFactory.SecurityContext buildCertificateAuthContent(DatabaseClientConfig config) {
		X509TrustManager trustManager = config.getTrustManager();

		String certFile = config.getCertFile();
		if (certFile != null) {
			try {
				if (config.getCertPassword() != null) {
					return new DatabaseClientFactory.CertificateAuthContext(certFile, config.getCertPassword(), trustManager);
				}
				return new DatabaseClientFactory.CertificateAuthContext(certFile, trustManager);
			} catch (Exception ex) {
				throw new RuntimeException("Unable to build CertificateAuthContext: " + ex.getMessage(), ex);
			}
		}

		DatabaseClientFactory.SSLHostnameVerifier verifier = config.getSslHostnameVerifier();
		if (verifier != null) {
			return new DatabaseClientFactory.CertificateAuthContext(config.getSslContext(), verifier, trustManager);
		}
		return new DatabaseClientFactory.CertificateAuthContext(config.getSslContext(), trustManager);
	}

	/**
	 * Applies an SSLContext and SSLHostnameVerifier if they've been set on the config object.
	 *
	 * @param config
	 * @param securityContext
	 * @return
	 */
	protected DatabaseClientFactory.SecurityContext applySslConfig(DatabaseClientConfig config, DatabaseClientFactory.SecurityContext securityContext) {
		if (securityContext != null) {
			SSLContext sslContext = config.getSslContext();
			DatabaseClientFactory.SSLHostnameVerifier verifier = config.getSslHostnameVerifier();
			if (sslContext != null) {
				securityContext = securityContext.withSSLContext(sslContext, config.getTrustManager());
			}
			if (verifier != null) {
				securityContext = securityContext.withSSLHostnameVerifier(verifier);
			}
		}
		return securityContext;
	}
}
