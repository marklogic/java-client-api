# MarkLogic Java Client API: Quality, Security, and Scalability Review

This document provides an in-depth audit of the MarkLogic Java Client API (version 8.2.0). Every finding is grounded in verified source code evidence, citing exact file paths and line numbers, with concrete attack or failure scenarios, severity ratings, existing mitigations, and actionable remediation steps.

---

## Severity Classification Matrix

| Finding ID | Classification | Severity | Impact Area | Description |
| :--- | :--- | :--- | :--- | :--- |
| **SEC-1** | Confirmed Defect | **High** | Security (CWE-502) | Unsafe Jackson Polymorphic Deserialization (`LaissezFaireSubTypeValidator`) |
| **SEC-2** | Confirmed Defect | **Medium-High** | Security (CWE-611) | Unhardened XML Parser in `FailedRequest` (XXE / SSRF) |
| **SEC-3** | Insecure Design | **Medium** | Security (CWE-295) | `SSLHostnameVerifier.ANY` disables TLS certificate verification |
| **SEC-4** | Information Leak | **Low** | Security (CWE-532) | Wire-level logging at `BODY` exposes unredacted document payloads |
| **SCL-1** | Scalability Bottleneck | **Medium** | Reliability / Perf | JVM-wide static `ConnectionPool` causes connection churn |
| **SCL-2** | Reliability Defect | **High** | Availability / Resil | Unbounded socket read/write timeouts (`timeout = 0`) cause thread starvation |
| **SCL-3** | Resource Risk | **Medium** | Reliability / Memory | Large batch buffer memory amplification under slow consumers |
| **MNT-1** | Technical Debt | **High** | Maintainability | 6,389-line monolithic God Class in `OkHttpServices` |
| **MNT-2** | Thread Safety Defect | **Medium** | Concurrency / Maint | Unsynchronized mutable state in `DocumentManagerImpl` |
| **MNT-3** | Technical Debt | **Low** | Architecture / Mod | Global system property mutation (`mail.mime.allowutf8`) |

---

## 1. Security Review

### SEC-1: Unsafe Jackson Polymorphic Deserialization via LaissezFaireSubTypeValidator
- **Severity**: **High** (CVSS: 7.5 - `CVSS:3.1/AV:N/AC:L/PR:N/UI:N/S:U/C:H/I:N/A:N`)
- **CWE**: [CWE-502: Deserialization of Untrusted Data](https://cwe.mitre.org/data/definitions/502.html)
- **Evidence**:
  - [`PojoRepositoryImpl.java:96-99`](file:///Users/rsingh/Code/GitHub/java-client-api/marklogic-client-api/src/main/java/com/marklogic/client/impl/PojoRepositoryImpl.java#L96-L99):
    ```java
    // This previously used enableDefaultTyping, but Jackson deprecated that several years ago...
    // Polaris also flagged it as a high vulnerability related to https://cwe.mitre.org/data/definitions/502.html .
    // The below is from https://stackoverflow.com/questions/69705476/...
    .activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.WRAPPER_OBJECT);
    ```
  - [`PojoPageImpl.java:43`](file:///Users/rsingh/Code/GitHub/java-client-api/marklogic-client-api/src/main/java/com/marklogic/client/impl/PojoPageImpl.java#L43):
    ```java
    JacksonDatabindHandle<T> handle = new JacksonDatabindHandle<>(entityClass);
    handle.getMapper().activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.WRAPPER_OBJECT);
    return docPage.nextContent(handle).get();
    ```
- **Trigger / Scenario**:
  An application stores Java domain POJOs in MarkLogic using `PojoRepository.write(pojo)`. If an attacker or untrusted integration inserts or modifies a document in that collection containing a polymorphic `@class` JSON wrapper object referencing a known Jackson deserialization gadget class (e.g., Spring `ClassPathXmlApplicationContext`, Logback `JNDI`, etc.), calling `pojoRepository.read(id)` or `pojoPage.next()` instantiates the gadget on the client JVM.
- **Impact**:
  Remote Code Execution (RCE) or Server-Side Request Forgery (SSRF) on the application server running the Java Client. While `JsonTypeInfo.As.WRAPPER_OBJECT` requires wrapper object syntax, `LaissezFaireSubTypeValidator` applies zero validation against gadget type instantiation.
- **Existing Mitigation**:
  None in the client library. The codebase attempted to resolve a Polaris security scan finding by migrating from deprecated `enableDefaultTyping` to `activateDefaultTyping`, but chose `LaissezFaireSubTypeValidator.instance`, which officially performs no subtype verification.
- **Recommended Remediation**:
  Replace `LaissezFaireSubTypeValidator` with a restricted `BasicPolymorphicTypeValidator` scoped strictly to trusted base packages:
  ```java
  PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder()
      .allowIfBaseType(entityClass.getPackageName())
      .build();
  objectMapper.activateDefaultTyping(ptv, ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.WRAPPER_OBJECT);
  ```
- **Estimated Effort**: 1–2 days.
- **Suggested Regression Test**:
  Create `PojoRepositoryDeserializationSecurityTest` attempting to read a JSON document containing an unauthorized wrapper type (e.g. `java.net.URL` or gadget surrogate). Verify Jackson throws `InvalidTypeIdException`.

---

### SEC-2: Unhardened XML Parser in FailedRequest Error Handling
- **Severity**: **Medium-High** (CVSS: 6.5 - `CVSS:3.1/AV:N/AC:L/PR:N/UI:N/S:U/C:L/I:N/A:L`)
- **CWE**: [CWE-611: Improper Restriction of XML External Entity Reference](https://cwe.mitre.org/data/definitions/611.html)
- **Evidence**:
  - [`FailedRequest.java:51-59`](file:///Users/rsingh/Code/GitHub/java-client-api/marklogic-client-api/src/main/java/com/marklogic/client/impl/FailedRequest.java#L51-L59):
    ```java
    FailedRequest failure = new FailedRequest();
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setNamespaceAware(true);
    factory.setValidating(false);
    DocumentBuilder builder;
    try {
        builder = factory.newDocumentBuilder();
        Document doc = builder.parse(is);
    ```
  - In contrast, [`XmlFactories.java:93-128`](file:///Users/rsingh/Code/GitHub/java-client-api/marklogic-client-api/src/main/java/com/marklogic/client/impl/XmlFactories.java#L93-L128) carefully configures `FEATURE_SECURE_PROCESSING`, `disallow-doctype-decl`, and disables `external-general-entities` and `external-parameter-entities`.
- **Trigger / Scenario**:
  When any MarkLogic request fails with an error status (4xx or 5xx) and `Content-Type: application/xml`, `OkHttpServices` forwards the response body stream to `FailedRequest.getFailedRequest(...)`. If a rogue reverse proxy, corrupted gateway, compromised cluster node, or man-in-the-middle responds with an XML error containing external DOCTYPE declarations:
  ```xml
  <!DOCTYPE error [ <!ENTITY xxe SYSTEM "file:///etc/passwd"> ]>
  <error:error xmlns:error="http://marklogic.com/xdmp/error">
      <error:message>&xxe;</error:message>
  </error:error>
  ```
  `builder.parse(is)` resolves the external entity on the client JVM.
- **Impact**:
  Local file disclosure or SSRF initiated from the application server executing the client library.
- **Existing Mitigation**:
  Direct connections to authentic MarkLogic servers typically produce valid internal error XML. However, gateway setups (`ConnectionType.GATEWAY`) and proxy routes are vulnerable.
- **Recommended Remediation**:
  Refactor `FailedRequest.java` to reuse `XmlFactories.getDocumentBuilderFactory()`:
  ```java
  DocumentBuilderFactory factory = XmlFactories.getDocumentBuilderFactory();
  DocumentBuilder builder = factory.newDocumentBuilder();
  Document doc = builder.parse(is);
  ```
- **Estimated Effort**: 0.5 day.
- **Suggested Regression Test**:
  Add `FailedRequestXXETest` feeding an XML payload with `<!DOCTYPE>` and `<!ENTITY>` into `FailedRequest.getFailedRequest(...)` and asserting that parsing fails with a security/SAX exception without resolving external resources.

---

### SEC-3: Insecure Built-In SSLHostnameVerifier.ANY Option
- **Severity**: **Medium** (CVSS: 5.9 - `CVSS:3.1/AV:A/AC:H/PR:N/UI:N/S:U/C:H/I:H/A:N`)
- **CWE**: [CWE-295: Improper Certificate Validation](https://cwe.mitre.org/data/definitions/295.html)
- **Evidence**:
  - [`OkHttpUtil.java:133-134`](file:///Users/rsingh/Code/GitHub/java-client-api/marklogic-client-api/src/main/java/com/marklogic/client/impl/okhttp/OkHttpUtil.java#L133-L134):
    ```java
    if (DatabaseClientFactory.SSLHostnameVerifier.ANY.equals(sslVerifier)) {
        hostnameVerifier = (hostname, session) -> true;
    }
    ```
  - [`DatabaseClientFactory.java:52`](file:///Users/rsingh/Code/GitHub/java-client-api/marklogic-client-api/src/main/java/com/marklogic/client/DatabaseClientFactory.java#L52):
    ```java
    final static public Builtin ANY = new Builtin("ANY");
    ```
- **Trigger / Scenario**:
  Developers frequently enable `.withSSLHostnameVerifier(SSLHostnameVerifier.ANY)` to bypass certificate errors during local prototyping and accidentally push this setting to production environments or container images.
- **Impact**:
  Renders the TLS channel completely vulnerable to DNS spoofing, ARP cache poisoning, and Man-In-The-Middle (MITM) proxy attacks. The attacker can present any certificate signed by any trusted CA (or untrusted if trust managers are also relaxed) and intercept plain-text Basic/Digest passwords and query data.
- **Existing Mitigation**:
  Javadoc warns that `ANY` is not recommended for production.
- **Recommended Remediation**:
  1. Deprecate `SSLHostnameVerifier.ANY`.
  2. If `ANY` is supplied at runtime, emit a high-priority `WARN` log event:
     `"INSECURE TLS CONFIGURATION: SSLHostnameVerifier.ANY is active. Hostname verification is completely disabled."`
- **Estimated Effort**: 0.5 day.

---

### SEC-4: Unredacted Document Payload Logging at Level BODY
- **Severity**: **Low** (CVSS: 3.3 - `CVSS:3.1/AV:L/AC:L/PR:L/UI:N/S:U/C:L/I:N/A:N`)
- **CWE**: [CWE-532: Insertion of Sensitive Information into Log File](https://cwe.mitre.org/data/definitions/532.html)
- **Evidence**:
  - [`OkHttpServices.java:233-246`](file:///Users/rsingh/Code/GitHub/java-client-api/marklogic-client-api/src/main/java/com/marklogic/client/impl/OkHttpServices.java#L233-L246):
    ```java
    if ("BODY".equalsIgnoreCase(props.getProperty(LoggingUtil.OKHTTP_NETWORK_LEVEL))) {
        networkInterceptor = networkInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
    }
    ```
  - [`RedactingHttpLogger.java:25-32`](file:///Users/rsingh/Code/GitHub/java-client-api/marklogic-client-api/src/main/java/com/marklogic/client/impl/okhttp/RedactingHttpLogger.java#L25-L32): Redacts `Authorization: [REDACTED]` and `x-auth-token: [REDACTED]` headers, but streams request and response bodies unredacted.
- **Trigger / Scenario**:
  Operations enables `-Dmarklogic.okhttp.network.level=BODY` to debug a production payload issue.
- **Impact**:
  PII, PCI, HIPAA, and proprietary data embedded within documents and Optic rows are written directly to application logs or stdout/stderr and ingested into log collectors.
- **Existing Mitigation**:
  Header authentication tokens are redacted; Javadoc contains an explicit security warning.
- **Recommended Remediation**:
  Require an explicit override system property `marklogic.okhttp.network.allowBodyInProduction=true` before activating `Level.BODY`, or truncate and mask body output above 512 bytes.
- **Estimated Effort**: 1 day.

---

## 2. Scalability and Reliability Review

### SCL-1: JVM-Wide Static ConnectionPool Contention
- **Severity**: **Medium**
- **Evidence**:
  - [`OkHttpUtil.java:34`](file:///Users/rsingh/Code/GitHub/java-client-api/marklogic-client-api/src/main/java/com/marklogic/client/impl/okhttp/OkHttpUtil.java#L34):
    ```java
    private static final ConnectionPool connectionPool = new ConnectionPool(5, 5, TimeUnit.MINUTES);
    ```
- **Trigger / Scenario**:
  A multi-tenant or multi-database microservice constructs multiple `DatabaseClient` instances (e.g., connecting to `Documents`, `Modules`, and analytical database endpoints across multiple hosts). All clients share the exact same `connectionPool` restricted to a maximum of **5 idle connections total across all hosts**.
- **Impact**:
  Idle connections are continuously evicted as soon as a 6th route or host connection completes. Under bursty traffic, this triggers severe TCP connection churn, TLS handshake overhead, and thread contention on the shared synchronized pool queue.
- **Recommended Remediation**:
  Allow callers to configure connection pool sizing per client via `DatabaseClientBuilder.withConnectionPool(maxIdle, keepAlive)` and stop sharing a hardcoded static 5-connection pool across independent clients.
- **Estimated Effort**: 1–2 days.

---

### SCL-2: Unbounded Default Sockets (Infinite Read and Write Timeouts)
- **Severity**: **High**
- **Evidence**:
  - [`OkHttpUtil.java:99-100`](file:///Users/rsingh/Code/GitHub/java-client-api/marklogic-client-api/src/main/java/com/marklogic/client/impl/okhttp/OkHttpUtil.java#L99-L100):
    ```java
    clientBuilder.readTimeout(0, TimeUnit.MILLISECONDS);
    clientBuilder.writeTimeout(0, TimeUnit.MILLISECONDS);
    ```
- **Trigger / Scenario**:
  1. A backend MarkLogic host freezes due to heavy disk I/O or an unindexed nested FLWOR query.
  2. A stateful intermediate firewall or cloud NAT gateway drops an idle TCP connection without sending TCP RST.
  3. Client threads calling `documentManager.read(...)` or `queryManager.search(...)` enter socket read wait.
- **Impact**:
  Because timeout is set to `0` (interpreted by OkHttp and Java Sockets as **infinite timeout**), application worker threads block indefinitely. Incoming HTTP requests queue up, leading to complete thread starvation of Tomcat/Spring web containers and container health check failures.
- **Existing Mitigation**:
  Users can implement an `OkHttpClientConfigurator` to override the timeouts. However, library defaults should be safe out of the box.
- **Recommended Remediation**:
  Provide sensible production defaults (e.g. readTimeout: 60s, writeTimeout: 60s) or expose `marklogic.client.readTimeout` / `marklogic.client.writeTimeout` in `DatabaseClientBuilder`.
- **Estimated Effort**: 1 day.

---

### SCL-3: Memory Amplification in DMSDK Batch Buffering Under Slow Consumers
- **Severity**: **Medium**
- **Evidence**:
  - [`WriteBatcherImpl.java`](file:///Users/rsingh/Code/GitHub/java-client-api/marklogic-client-api/src/main/java/com/marklogic/client/datamovement/impl/WriteBatcherImpl.java) buffers incoming documents into batch arrays before submission to `CompletableThreadPoolExecutor`.
- **Trigger / Scenario**:
  An application producer pushes hundreds of thousands of large documents (e.g. 5 MB PDF/XML files) into `WriteBatcher.add(uri, handle)`. The MarkLogic cluster experiences write latency, slowing down batch writes.
- **Impact**:
  Although `CallerRunsPolicy` provides backpressure when thread pool task queues fill, uncommitted document handles queued in memory hold large byte arrays and DOM trees, causing massive heap amplification, GC churn, and potential `OutOfMemoryError`.
- **Existing Mitigation**:
  DMSDK implements `CallerRunsPolicy` to slow down the producer thread once the executor queue is saturated.
- **Recommended Remediation**:
  Add an optional memory-based queue threshold (e.g., `withMaxMemoryBuffered(long bytes)`) alongside document count thresholds.
- **Estimated Effort**: 3–5 days.

---

## 3. Architecture and Maintainability Review

### MNT-1: Monolithic God Class in OkHttpServices
- **Severity**: **High**
- **Evidence**:
  - [`OkHttpServices.java`](file:///Users/rsingh/Code/GitHub/java-client-api/marklogic-client-api/src/main/java/com/marklogic/client/impl/OkHttpServices.java) spans **6,389 lines** and contains hundreds of methods covering:
    - HTTP client lifecycle and OkHttp builder setup
    - Transient status code retry loops and delay backoff calculations
    - Multipart MIME request generation and response parsing
    - XML, JSON, and binary entity stream handling
    - Transaction creation, cookies, and commit/rollback tracking
    - Bitemporal document updates
    - Optic plan execution and row bundle dispatch
- **Impact**:
  Extreme cognitive overhead. High coupling makes it nearly impossible to introduce architectural enhancements (such as HTTP/2 multiplexing, non-blocking asynchronous reactive flows, or alternative HTTP backends) without risking regressions across the entire client surface.
- **Recommended Remediation**:
  Decompose `OkHttpServices` into specialized domain coordinators:
  1. `HttpTransportManager`: Connection lifecycle, dispatcher, and pool.
  2. `HttpRetryCoordinator`: Retry backoff state and status code inspection.
  3. `MultipartPayloadHandler`: Jakarta Mail MIME body parsing.
  4. `TransactionTransportCoordinator`: `HostId` cookie pinning and transaction endpoints.
- **Estimated Effort**: 2–3 sprints.

---

### MNT-2: Unsynchronized Mutable State in DocumentManagerImpl
- **Severity**: **Medium**
- **Evidence**:
  - [`DocumentManagerImpl.java:51-74`](file:///Users/rsingh/Code/GitHub/java-client-api/marklogic-client-api/src/main/java/com/marklogic/client/impl/DocumentManagerImpl.java#L51-L74):
    ```java
    private boolean isMetadataCategoriesModified;
    final private Set<Metadata> metadataCategories = new HashSet<>() {
        @Override
        public boolean add(Metadata e) {
            isMetadataCategoriesModified = true;
            return super.add(e);
        }
    ...
    ```
- **Trigger / Scenario**:
  A developer instantiates a single `JSONDocumentManager` and shares it across multithreaded worker tasks, concurrently invoking `setMetadataCategories(...)` or reading documents with varying metadata categories.
- **Impact**:
  Neither `metadataCategories` (`HashSet`) nor `isMetadataCategoriesModified` (`boolean`) is synchronized or thread-safe. Concurrent modifications can lead to corrupted internal hash table buckets, `ConcurrentModificationException`, or dirty reads of metadata flags.
- **Recommended Remediation**:
  Make `DocumentManager` implementations strictly thread-safe:
  1. Guard access with `synchronized` blocks or an internal `ReentrantReadWriteLock`.
  2. Or, preferably, make managers immutable and return new configured copies via a fluent builder pattern.
- **Estimated Effort**: 1 day.

---

### MNT-3: Global System Property Mutation (mail.mime.allowutf8)
- **Severity**: **Low**
- **Evidence**:
  - [`OkHttpServices.java:87-92`](file:///Users/rsingh/Code/GitHub/java-client-api/marklogic-client-api/src/main/java/com/marklogic/client/impl/OkHttpServices.java#L87-L92):
    ```java
    static {
        if (System.getProperty("mail.mime.allowutf8") == null) {
            System.setProperty("mail.mime.allowutf8", "true");
        }
    }
    ```
- **Impact**:
  Mutating JVM global system properties within a client library static initializer violates clean library design principles. In shared enterprise application containers or modular OSGi runtimes, modifying `mail.mime.allowutf8` can trigger unexpected behavioral changes in other collocated mail components.
- **Recommended Remediation**:
  Pass UTF-8 decode parameters directly to Jakarta/Angus Mail session/mime message instances rather than setting global system properties.
- **Estimated Effort**: 0.5 day.
