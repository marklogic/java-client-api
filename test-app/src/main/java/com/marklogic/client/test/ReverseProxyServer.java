package com.marklogic.client.test;

import com.marklogic.client.ext.modulesloader.ssl.SimpleX509TrustManager;
import io.undertow.Undertow;
import io.undertow.client.ClientCallback;
import io.undertow.client.ClientConnection;
import io.undertow.client.UndertowClient;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.ServerConnection;
import io.undertow.server.handlers.proxy.ProxyCallback;
import io.undertow.server.handlers.proxy.ProxyClient;
import io.undertow.server.handlers.proxy.ProxyConnection;
import io.undertow.server.handlers.proxy.ProxyHandler;
import org.springframework.core.io.ClassPathResource;
import org.xnio.IoUtils;
import org.xnio.OptionMap;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.io.IOException;
import java.net.URI;
import java.security.KeyStore;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Credit to https://stackoverflow.com/a/39531382/3306099 for this, though it's been tweaked a bit.
 * <p>
 * Undertow has an example of a reverse proxy server as well -
 * https://github.com/undertow-io/undertow/tree/master/examples/src/main/java/io/undertow/examples/reverseproxy
 * <p>
 * Note that this does not yet support digest authentication, which seems to be common with reverse proxy servers.
 * That's fine for testing the Java Client, as verifying that the basePath works is not related to what kind of
 * authentication is required by MarkLogic.
 */
public class ReverseProxyServer {

	/**
	 * Accepts up to 3 args: 1) the MarkLogic hostname to proxy to; 2) the hostname for this server;
	 * 3) the port for this server. For current use cases though, including Jenkins, localhost should suffice for both
	 * hostnames and 8020 should suffice as the port.
	 *
	 * @param args
	 * @throws Exception
	 */
	public static void main(final String[] args) throws Exception {
		String markLogicHost = "localhost";
		String serverHost = "localhost";
		int serverPort = 8020;
		int secureServerPort = 8021;

		if (args.length > 0) {
			markLogicHost = args[0];
			if (args.length > 1) {
				serverHost = args[1];
				if (args.length > 2) {
					serverPort = Integer.parseInt(args[2]);
				}
			}
		}

		// Set up the mapping of paths to MarkLogic ports. Paths with and without forward slashes are used to ensure
		// both work properly.
		Map<String, URI> mapping = new HashMap<>();
		mapping.put("/test/marklogic/unit", new URI("http://" + markLogicHost + ":8012"));
		// 8014 is for fast functional tests. Slow tests are not yet proxied, which currently will be difficult as they
		// use a variety of ports. Best approach will be to convert them over to fast tests that all use 8014.
		mapping.put("/testFunctional", new URI("http://" + markLogicHost + ":8014"));
		System.out.println("Proxy mapping: " + mapping);

		Undertow.builder()
			.addHttpListener(serverPort, serverHost)
			.addHttpsListener(secureServerPort, serverHost, buildSSLContext())
			.setIoThreads(4)
			.setHandler(ProxyHandler.builder()
				.setProxyClient(new ReverseProxyClient(mapping))
				.setMaxRequestTime(30000)
				.build()
			)
			.build()
			.start();
	}

	/**
	 * Tried to enable SSL so that CheckSSLConnectionTest would succeed, but no luck so far. This is using a test
	 * keystore (copied from the marklogic-nifi project). Undertow seems to accept the request and then forward it
	 * along, but the test gets a 403. It seems that the basic authentication is not being sent correctly, as testing
	 * via a web browser results in the authentication being rejected.
	 *
	 * @return
	 * @throws Exception
	 */
	private static SSLContext buildSSLContext() throws Exception {
		final String keyStorePassword = "passwordpassword";
		KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
		keyStore.load(new ClassPathResource("keystore.jks").getInputStream(), keyStorePassword.toCharArray());
		kmf.init(keyStore, keyStorePassword.toCharArray());

		SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
		sslContext.init(kmf.getKeyManagers(), new TrustManager[]{new SimpleX509TrustManager()}, null);
		return sslContext;
	}

	private static class ReverseProxyClient implements ProxyClient {
		private static final ProxyTarget TARGET = new ProxyTarget() {
		};

		private final UndertowClient client;
		private final Map<String, URI> mapping;

		public ReverseProxyClient(Map<String, URI> mapping) {
			this.client = UndertowClient.getInstance();
			this.mapping = mapping;
		}

		@Override
		public ProxyTarget findTarget(HttpServerExchange exchange) {
			return TARGET;
		}

		@Override
		public void getConnection(ProxyTarget target, HttpServerExchange exchange, ProxyCallback<ProxyConnection> callback, long timeout, TimeUnit timeUnit) {
			final String requestURI = exchange.getRequestURI();
			System.out.println("Received request: " + new Date() + "; " + requestURI);
			URI targetUri = null;

			for (String path : mapping.keySet()) {
				if (requestURI.startsWith(path)) {
					targetUri = mapping.get(path);
					exchange.setRequestURI(requestURI.substring(path.length()));
					break;
				}
			}

			if (targetUri == null) {
				throw new IllegalArgumentException("Unsupported request URI: " + exchange.getRequestURI());
			}

			System.out.println("Proxying to: " + targetUri + exchange.getRequestURI());
			client.connect(
				new ConnectNotifier(callback, exchange),
				targetUri,
				exchange.getIoThread(),
				exchange.getConnection().getByteBufferPool(),
				OptionMap.EMPTY);
		}

		private final class ConnectNotifier implements ClientCallback<ClientConnection> {
			private final ProxyCallback<ProxyConnection> callback;
			private final HttpServerExchange exchange;

			private ConnectNotifier(ProxyCallback<ProxyConnection> callback, HttpServerExchange exchange) {
				this.callback = callback;
				this.exchange = exchange;
			}

			@Override
			public void completed(final ClientConnection connection) {
				final ServerConnection serverConnection = exchange.getConnection();
				serverConnection.addCloseListener(serverConnection1 -> IoUtils.safeClose(connection));
				callback.completed(exchange, new ProxyConnection(connection, "/"));
			}

			@Override
			public void failed(IOException e) {
				callback.failed(exchange);
			}
		}
	}
}
