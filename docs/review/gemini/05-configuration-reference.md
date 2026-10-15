# MarkLogic Java Client API: Configuration Reference

This document provides a complete technical reference for configuring the MarkLogic Java Client API (version 8.2.0). It covers all client creation mechanisms, connection properties, authentication options, SSL/TLS security settings, OkHttp transport configuration, JVM system properties, and Data Movement SDK (DMSDK) operational tuning.

---

## 1. Configuration Mechanisms & Precedence

The MarkLogic Java Client supports multiple configuration interfaces to accommodate programmatic, declarative, and framework-driven integrations.

```
                    ┌────────────────────────────────────────────────────────┐
                    │                      User Inputs                       │
                    └────────────────────────────────────────────────────────┘
                                 │                              │
                     DatabaseClientBuilder             Spring / External Map
                                 │                              │
                                 ▼                              ▼
                    ┌────────────────────────────────────────────────────────┐
                    │        DatabaseClientPropertySource (Function)        │
                    └────────────────────────────────────────────────────────┘
                                 │                              │
                                 ▼                              ▼
                    ┌─────────────────────────┐   ┌──────────────────────────┐
                    │ DatabaseClientFactory   │   │  DatabaseClientFactory   │
                    │         .Bean           │   │       .newClient()       │
                    └─────────────────────────┘   └──────────────────────────┘
                                 │                              │
                                 └──────────────┬───────────────┘
                                                ▼
                    ┌────────────────────────────────────────────────────────┐
                    │                   OkHttpServices                       │
                    │  (Applies System Properties & OkHttpClientConfigurators)│
                    └────────────────────────────────────────────────────────┘
```

### 1.1 Configuration Precedence & Resolution
When using [`DatabaseClientPropertySource`](file:///Users/rsingh/Code/GitHub/java-client-api/marklogic-client-api/src/main/java/com/marklogic/client/impl/DatabaseClientPropertySource.java) or [`DatabaseClientBuilder`](file:///Users/rsingh/Code/GitHub/java-client-api/marklogic-client-api/src/main/java/com/marklogic/client/DatabaseClientBuilder.java):
1. **Connection String Base**: If `marklogic.client.connectionString` is provided, it seeds the initial `host`, `port`, `username`, `password`, and optionally `database`.
2. **Explicit Property Overrides**: Any discrete property (`marklogic.client.host`, `marklogic.client.port`, etc.) overrides the corresponding value parsed from the connection string.
3. **Explicit SecurityContext**: If an instance of `DatabaseClientFactory.SecurityContext` is provided directly via `marklogic.client.securityContext` or `.withSecurityContext()`, all auth-type and credential properties are ignored.
4. **Auth-Type Resolution**: If no explicit security context is provided, `marklogic.client.authType` dictates which credential properties are validated and constructed. If a connection string is supplied without an `authType`, `digest` is selected as the default.
5. **System Properties**: Runtime operational parameters (OkHttp network logging levels, retry maximum delays, retry minimum intervals) are read exclusively from `System.getProperties()` during `OkHttpServices` initialization.
6. **OkHttpClientConfigurator SPI**: Registered configurators (`DatabaseClientFactory.addConfigurator(...)`) execute last, mutating the final `OkHttpClient.Builder` before `.build()` is invoked.

---

## 2. Connection and Database Properties

All properties can be supplied to [`DatabaseClientBuilder`](file:///Users/rsingh/Code/GitHub/java-client-api/marklogic-client-api/src/main/java/com/marklogic/client/DatabaseClientBuilder.java) or passed in a property source map (`Function<String, Object>`) using the prefix `marklogic.client.`.

| Property Key | Builder Method | Type | Default | Description |
| :--- | :--- | :--- | :--- | :--- |
| `marklogic.client.connectionString` | `.withConnectionString(String)` | `String` | None | Connection URI in format: `[user:pass@]host:port[/database]` |
| `marklogic.client.host` | `.withHost(String)` | `String` | Required | MarkLogic server hostname or IP address |
| `marklogic.client.port` | `.withPort(int)` | `Integer` | Required | MarkLogic App Server REST port |
| `marklogic.client.database` | `.withDatabase(String)` | `String` | Server default | Target database name; defaults to App Server default content database |
| `marklogic.client.basePath` | `.withBasePath(String)` | `String` | `null` | URL path prefix for reverse-proxies or MarkLogic Cloud routes |
| `marklogic.client.connectionType` | `.withConnectionType(ConnectionType)` | `String` / Enum | `DIRECT` | `DIRECT` (direct to nodes) or `GATEWAY` (behind load balancer / cloud proxy) |
| `marklogic.client.disableGzippedResponses` | `.withGzippedResponsesDisabled()` | `Boolean` | `false` | When `true`, strips `Accept-Encoding: gzip` to avoid decompression CPU overhead |

---

## 3. Authentication & Security Properties

The client determines authentication handling from `marklogic.client.authType` (`basic`, `digest`, `cloud`, `kerberos`, `certificate`, `saml`, or `oauth`).

| Property Key | Builder Method | Type | Required For | Description |
| :--- | :--- | :--- | :--- | :--- |
| `marklogic.client.authType` | `.withAuthType(String)` | `String` | All | One of `basic`, `digest`, `cloud`, `kerberos`, `certificate`, `saml`, `oauth` |
| `marklogic.client.username` | `.withUsername(String)` | `String` | `basic`, `digest` | MarkLogic database user username |
| `marklogic.client.password` | `.withPassword(String)` | `String` | `basic`, `digest` | MarkLogic database user password (sensitive) |
| `marklogic.client.cloud.apiKey` | `.withCloudApiKey(String)` | `String` | `cloud` | MarkLogic Cloud / Progress Data Cloud API key |
| `marklogic.client.cloud.tokenDuration`| `.withCloudTokenDuration(String)` | `Integer` | `cloud` (optional)| Token lifetime in minutes before auto-renewal |
| `marklogic.client.certificate.file` | `.withCertificateFile(String)` | `String` | `certificate` | Path to client PKCS#12 certificate file (`.p12` / `.pfx`) |
| `marklogic.client.certificate.password`| `.withCertificatePassword(String)`| `String` | `certificate` | PKCS#12 certificate password |
| `marklogic.client.kerberos.principal` | `.withKerberosPrincipal(String)` | `String` | `kerberos` | Kerberos user principal name (requires JAAS config) |
| `marklogic.client.saml.token` | `.withSAMLToken(String)` | `String` | `saml` | Base64-encoded or raw SAML 2.0 assertion token |
| `marklogic.client.oauth.token` | `.withOAuthToken(String)` | `String` | `oauth` | OAuth2 Bearer Access Token |

---

## 4. SSL / TLS & Hostname Verification Properties

The client provides 5 distinct resolution approaches for SSL/TLS configuration within [`DatabaseClientPropertySource.java`](file:///Users/rsingh/Code/GitHub/java-client-api/marklogic-client-api/src/main/java/com/marklogic/client/impl/DatabaseClientPropertySource.java#L322-L350):

1. **Explicit Object**: `marklogic.client.sslContext` (`javax.net.ssl.SSLContext`) and optional `marklogic.client.trustManager` (`javax.net.ssl.X509TrustManager`).
2. **Mutual TLS via Keystore**: `ssl.keystore.path` and `ssl.keystore.password` (creates 2-way SSL context).
3. **Protocol `"default"`**: Uses the JVM default `SSLContext.getDefault()` and default trust manager.
4. **Protocol Named (e.g. `"TLSv1.2"`, `"TLSv1.3"`)**: Creates `SSLContext.getInstance(protocol)`.
5. **Truststore Path**: Loads custom trust certificates from JKS/PKCS12 truststore file.

| Property Key | Builder Method | Type | Default | Description |
| :--- | :--- | :--- | :--- | :--- |
| `marklogic.client.sslContext` | `.withSSLContext(SSLContext)` | `SSLContext` | `null` | Pre-initialized SSLContext instance |
| `marklogic.client.trustManager` | `.withTrustManager(X509TrustManager)`| `X509TrustManager` | `null` | Custom X509TrustManager implementation |
| `marklogic.client.sslProtocol` | `.withSSLProtocol(String)` | `String` | `TLSv1.2` | SSL/TLS protocol name (e.g. `TLSv1.2`, `TLSv1.3`, `default`) |
| `marklogic.client.sslHostnameVerifier` | `.withSSLHostnameVerifier(SSLHostnameVerifier)` | `String` / Enum | Strict default | `ANY`, `COMMON`, or `STRICT` (case-insensitive) |
| `marklogic.client.ssl.keystore.path` | `.withKeyStorePath(String)` | `String` | `null` | Filesystem path to client key store for 2-way TLS |
| `marklogic.client.ssl.keystore.password` | `.withKeyStorePassword(String)` | `String` | `null` | Key store protection password |
| `marklogic.client.ssl.keystore.type` | `.withKeyStoreType(String)` | `String` | `JKS` | Key store type (`JKS`, `PKCS12`) |
| `marklogic.client.ssl.keystore.algorithm` | `.withKeyStoreAlgorithm(String)`| `String` | `SunX509` | Key manager factory algorithm |
| `marklogic.client.ssl.truststore.path` | `.withTrustStorePath(String)` | `String` | `null` | Filesystem path to custom CA trust store |
| `marklogic.client.ssl.truststore.password`| `.withTrustStorePassword(String)`| `String` | `null` | Trust store protection password |
| `marklogic.client.ssl.truststore.type` | `.withTrustStoreType(String)` | `String` | `JKS` | Trust store type (`JKS`, `PKCS12`) |
| `marklogic.client.ssl.truststore.algorithm`| `.withTrustStoreAlgorithm(String)`| `String` | `SunX509` | Trust manager factory algorithm |

> [!WARNING]
> Setting `marklogic.client.sslHostnameVerifier` to `ANY` disables hostname verification entirely via `(hostname, session) -> true` ([`DatabaseClientFactory.java:311`](file:///Users/rsingh/Code/GitHub/java-client-api/marklogic-client-api/src/main/java/com/marklogic/client/DatabaseClientFactory.java#L311)). This leaves connections vulnerable to Man-In-The-Middle (MITM) attacks and should strictly be forbidden in non-test environments.

---

## 5. System Properties & Runtime Operational Flags

The client library monitors several JVM-wide system properties controlling network logging, transient HTTP retry logic, and MIME encoding.

| System Property | Default | Valid Values | Source Location | Description |
| :--- | :--- | :--- | :--- | :--- |
| `marklogic.okhttp.network.level` | Unset (off) | `BASIC`, `HEADERS`, `BODY`, `NONE` | [`LoggingUtil.java:57`](file:///Users/rsingh/Code/GitHub/java-client-api/marklogic-client-api/src/main/java/com/marklogic/client/util/LoggingUtil.java#L57) | Verbosity of OkHttp wire-level HTTP logging |
| `marklogic.okhttp.network.output` | `STDOUT` | `LOGGER`, `STDERR` | [`LoggingUtil.java:71`](file:///Users/rsingh/Code/GitHub/java-client-api/marklogic-client-api/src/main/java/com/marklogic/client/util/LoggingUtil.java#L71) | Target sink for network logs (`LOGGER` writes to SLF4J category `marklogic.okhttp.network` at INFO) |
| `com.marklogic.client.maximumRetryDelay` | `120` | Positive integer (seconds) | [`OkHttpServices.java:101`](file:///Users/rsingh/Code/GitHub/java-client-api/marklogic-client-api/src/main/java/com/marklogic/client/impl/OkHttpServices.java#L101) | Maximum total backoff ceiling in seconds for retryable 502/503/504 errors |
| `com.marklogic.client.minimumRetryInterval` | `8` | Positive integer (attempts) | [`OkHttpServices.java:102`](file:///Users/rsingh/Code/GitHub/java-client-api/marklogic-client-api/src/main/java/com/marklogic/client/impl/OkHttpServices.java#L102) | Minimum retry attempts before exhausting retries regardless of elapsed time |
| `mail.mime.allowutf8` | `true` | `true`, `false` | [`OkHttpServices.java:89`](file:///Users/rsingh/Code/GitHub/java-client-api/marklogic-client-api/src/main/java/com/marklogic/client/impl/OkHttpServices.java#L89) | Enables UTF-8 support in Jakarta Mail MIME multipart header decoding |

---

## 6. HTTP Transport Defaults & OkHttpClient Tuning

Transport parameters are established in [`OkHttpUtil.java`](file:///Users/rsingh/Code/GitHub/java-client-api/marklogic-client-api/src/main/java/com/marklogic/client/impl/OkHttpUtil.java#L34-L100):

| Parameter | Default Value | Hardcoded vs Configurable | Impact / Architectural Note |
| :--- | :--- | :--- | :--- |
| **Connection Pool** | Shared static instance (max 5 idle connections, 5 min keep-alive) | Hardcoded static in `OkHttpUtil.java:34` | All `DatabaseClient` instances share the same pool; cannot exceed 5 concurrent idle connections per route without custom configurator. |
| **Connect Timeout** | 10 seconds | Configurable via `OkHttpClientConfigurator` | Time allowed to complete TCP handshake and TLS negotiation. |
| **Read Timeout** | **`0` (Infinite)** | Configurable via `OkHttpClientConfigurator` | Default allows queries to hang indefinitely if network sockets stall or MarkLogic freezes. |
| **Write Timeout** | **`0` (Infinite)** | Configurable via `OkHttpClientConfigurator` | Default allows document upload sockets to hang indefinitely. |
| **Retry on Connection Failure** | `true` | Enabled in OkHttp builder | OkHttp automatically retries unrouteable IP addresses and stale TLS session tickets. |
| **Dispatcher Max Requests** | 64 concurrent (OkHttp default) | Configurable via configurator | Maximum total parallel asynchronous HTTP dispatches. |
| **Dispatcher Max Per Host** | 5 concurrent (OkHttp default) | Configurable via configurator | Maximum parallel requests per MarkLogic host. |

### 6.1 Recommended Production OkHttpClientConfigurator
To protect production applications against infinite thread starvation and connection pool exhaustion, apply an [`OkHttpClientConfigurator`](file:///Users/rsingh/Code/GitHub/java-client-api/marklogic-client-api/src/main/java/com/marklogic/client/extra/okhttpclient/OkHttpClientConfigurator.java):

```java
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.extra.okhttpclient.OkHttpClientConfigurator;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import java.util.concurrent.TimeUnit;

public class ProductionOkHttpConfigurator implements OkHttpClientConfigurator {
    @Override
    public void configure(OkHttpClient.Builder builder) {
        builder.connectTimeout(15, TimeUnit.SECONDS)
               .readTimeout(60, TimeUnit.SECONDS)
               .writeTimeout(60, TimeUnit.SECONDS)
               .connectionPool(new ConnectionPool(50, 5, TimeUnit.MINUTES));
    }
}

// Register globally before creating clients:
DatabaseClientFactory.addConfigurator(new ProductionOkHttpConfigurator());
```

---

## 7. Data Movement SDK (DMSDK) Configuration Reference

Data Movement batchers manage their own parallel thread pools, batch buffers, and failure listeners.

### 7.1 WriteBatcher Configuration
Constructed via `dataMovementManager.newWriteBatcher()`.

| Setting | Method | Default | Recommended Range | Description |
| :--- | :--- | :--- | :--- | :--- |
| **Batch Size** | `.withBatchSize(int)` | `100` | 100 – 1,000 | Number of documents sent in a single multipart POST payload |
| **Thread Count** | `.withThreadCount(int)` | `1` | 4 – 16 | Number of worker threads dispatched by `CompletableThreadPoolExecutor` |
| **Forest Configuration** | `.withForestConfig(ForestConfiguration)` | Auto-discovered | Auto-discovered | Target forest-to-host routing topology |
| **Job Name** | `.withJobName(String)` | `"unnamed"` | Descriptive name | Descriptive job identifier for logging and monitoring |
| **Success Listeners** | `.onBatchSuccess(WriteBatchListener)` | Default logging | Application specific | Callbacks fired after successful commit of a batch |
| **Failure Listeners** | `.onBatchFailure(WriteBatchListener)` | Default error log | Retry/DLQ handler | Callbacks executed upon batch insertion failure |

### 7.2 QueryBatcher Configuration
Constructed via `dataMovementManager.newQueryBatcher(StructuredQueryDefinition)` or raw query.

| Setting | Method | Default | Recommended Range | Description |
| :--- | :--- | :--- | :--- | :--- |
| **Batch Size** | `.withBatchSize(int docBatchSize)` | `1000` | 500 – 5,000 | Number of URIs per retrieved query batch |
| **Doc-to-URI Ratio** | `.withBatchSize(docBatch, ratio)` | `10` | 5 – 20 | Documents evaluated per internal forest query request |
| **Thread Count** | `.withThreadCount(int)` | `1` | 4 – 32 | Parallel forest query workers |
| **Consistent Snapshot** | `.withConsistentSnapshot()` | Off (`null`) | On for export | Pin query across all forests to single server timestamp |
| **Filtered Search** | `.withFilteredSearch()` | Fast unfiltered | Case-dependent | Force secondary filtering of query candidates on server |

---

## 8. Safe Configuration Profiles

### 8.1 MarkLogic Cloud (Progress Data Cloud)
```java
DatabaseClient client = DatabaseClientBuilder.newClient()
    .withHost("my-cloud-instance.marklogic.cloud")
    .withPort(443)
    .withBasePath("api")
    .withCloudApiKey(System.getenv("ML_CLOUD_API_KEY"))
    .withCloudTokenDuration(60) // 1 hour token renewal
    .withConnectionType(DatabaseClient.ConnectionType.GATEWAY)
    .build();
```

### 8.2 Production On-Premises Cluster with Load Balancer & Strict TLS
```java
DatabaseClient client = DatabaseClientBuilder.newClient()
    .withHost("lb-vip.corp.internal")
    .withPort(8000)
    .withDatabase("Documents")
    .withConnectionType(DatabaseClient.ConnectionType.GATEWAY)
    .withDigestAuth("app-user", System.getenv("ML_APP_PASSWORD"))
    .withSSLProtocol("TLSv1.3")
    .withTrustStorePath("/etc/ssl/certs/ml-truststore.p12")
    .withTrustStorePassword(System.getenv("ML_TRUSTSTORE_PASSWORD"))
    .withTrustStoreType("PKCS12")
    .withSSLHostnameVerifier(DatabaseClientFactory.SSLHostnameVerifier.STRICT)
    .build();
```

### 8.3 Two-Way (Mutual) TLS with Client Certificates
```java
DatabaseClient client = DatabaseClientBuilder.newClient()
    .withHost("node1.corp.internal")
    .withPort(8000)
    .withCertificateAuth("/etc/ssl/client-identity.p12", System.getenv("CERT_PASSWORD"))
    .withTrustStorePath("/etc/ssl/corporate-ca.jks")
    .withTrustStorePassword(System.getenv("CA_TRUSTSTORE_PASSWORD"))
    .withSSLHostnameVerifier(DatabaseClientFactory.SSLHostnameVerifier.STRICT)
    .build();
```
