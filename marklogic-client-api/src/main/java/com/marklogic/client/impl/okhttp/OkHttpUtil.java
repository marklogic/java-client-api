package com.marklogic.client.impl.okhttp;

import com.marklogic.client.DatabaseClientFactory;
import okhttp3.ConnectionPool;
import okhttp3.CookieJar;
import okhttp3.Dns;
import okhttp3.OkHttpClient;

import javax.net.SocketFactory;
import javax.net.ssl.*;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Contains convenience methods for constructing an OkHttpClient.Builder so that it can be used in places other than
 * only {@code OkHttpServices}. This code was moved here from OkHttpServices without any modification during the move
 * (other than the method {@code initializeSslContext} being extracted for readability purposes).
 */
public abstract class OkHttpUtil {

	final private static ConnectionPool connectionPool = new ConnectionPool();

	/**
	 * @return an OkHttpClient.Builder initialized with a sensible set of defaults that can then have authentication
	 * configured
	 */
	public static OkHttpClient.Builder newClientBuilder() {
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

	/**
	 * Configure the hostname verifier for the given OkHttpClient.Builder based on the given SSLHostnameVerifier.
	 *
	 * @param clientBuilder
	 * @param sslVerifier
	 */
	public static void configureHostnameVerifier(OkHttpClient.Builder clientBuilder, DatabaseClientFactory.SSLHostnameVerifier sslVerifier) {
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
	public static void configureSocketFactory(OkHttpClient.Builder clientBuilder, SSLContext sslContext, X509TrustManager trustManager) {
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
		try {
			TrustManagerFactory trustMgrFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			trustMgrFactory.init((KeyStore) null);
			TrustManager[] trustMgrs = trustMgrFactory.getTrustManagers();
			if (trustMgrs == null || trustMgrs.length == 0) {
				throw new IllegalArgumentException("no trust manager and could not get default trust manager");
			}
			if (!(trustMgrs[0] instanceof X509TrustManager)) {
				throw new IllegalArgumentException("no trust manager and default is not an X509TrustManager");
			}
			sslContext.init(null, trustMgrs, null);
			clientBuilder.sslSocketFactory(new SSLSocketFactoryDelegator(sslContext.getSocketFactory()), (X509TrustManager) trustMgrs[0]);
		} catch (KeyStoreException e) {
			throw new IllegalArgumentException("no trust manager and cannot initialize factory for default", e);
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalArgumentException("no trust manager and no algorithm for default manager", e);
		} catch (KeyManagementException e) {
			throw new IllegalArgumentException("no trust manager and cannot initialize context with default", e);
		}
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
