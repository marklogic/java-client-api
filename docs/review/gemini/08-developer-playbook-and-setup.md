# MarkLogic Java Client API: Developer Playbook and Setup Guide

This document provides verified local development setup guides for IntelliJ IDEA and Visual Studio Code, a debugging playbook with exact breakpoint targets and logging techniques, a prioritized remediation roadmap, unanswered questions, and an evidence index linking all conclusions to repository source files.

---

## 1. Prerequisites and Environment Matrix

| Component | Minimum Version | Recommended Version | Verification Command | Notes |
| :--- | :--- | :--- | :--- | :--- |
| **Java Development Kit (JDK)** | 17 | Eclipse Temurin / Amazon Corretto 17 or 21 | `java -version` | CI tests against JDK 17.0.2 and JDK 21.0.1. |
| **Build Tool (Gradle)** | 8.x / 9.x | 9.3.1 (via `./gradlew`) | `./gradlew --version` | Wrapper is provided in repo root. |
| **MarkLogic Server** | 10.x | 11.x or 12.x | MarkLogic Admin Console (`:8001`) | Standalone installation or Docker container. |
| **Docker / Compose** | 20.10+ | Latest Docker Desktop / Colima | `docker compose version` | Required for automated container test environment. |

---

## 2. Local Setup Guide: IntelliJ IDEA

### 2.1 Project Import & SDK Configuration
1. **Open Project**: Launch IntelliJ IDEA and select **File -> Open...** -> select `/path/to/java-client-api`.
2. **Import as Gradle Project**: IntelliJ will automatically detect the root `settings.gradle`.
3. **Project SDK**:
   - Go to **File -> Project Structure -> Project**.
   - Set **SDK** to OpenJDK 17 or 21.
   - Set **Language Level** to `17 - Records, patterns, etc.`.
4. **Gradle JVM**:
   - Navigate to **Preferences (Settings) -> Build, Execution, Deployment -> Build Tools -> Gradle**.
   - Set **Gradle JVM** to the Project SDK (JDK 17 or 21).
   - Set **Build and run using** to `Gradle` (or `IntelliJ IDEA` for faster unit test cycles).

### 2.2 Annotation Processing & Source Roots
- In **Preferences -> Build, Execution, Deployment -> Compiler -> Annotation Processors**:
  - Check **Enable annotation processing**.
  - Store generated sources relative to: **Module content root**.
- Ensure the following directories are recognized as source roots:
  - `marklogic-client-api/src/main/java` (Sources)
  - `marklogic-client-api/src/main/resources` (Resources)
  - `marklogic-client-api/src/test/java` (Test Sources)
  - `marklogic-client-api/src/test/resources` (Test Resources)

### 2.3 IntelliJ Run / Debug Configuration: Standalone Smoke Test
Create a JUnit Run Configuration in IntelliJ:
- **Type**: `JUnit`
- **Name**: `Unit Tests Smoke Test`
- **Class**: `com.marklogic.client.impl.okhttp.RedactingHttpLoggerTest`
- **VM Options**: `-ea -Dmarklogic.okhttp.network.level=BASIC`
- **JRE**: JDK 17 / 21

### 2.4 IntelliJ Run / Debug Configuration: MarkLogic Integration Test
For tests communicating with a local MarkLogic instance:
- **Type**: `JUnit`
- **Name**: `Document Read/Write Test`
- **Class**: `com.marklogic.client.test.DocumentWriteTest`
- **VM Options**:
  ```bash
  -ea -DTEST_HOST=localhost -DTEST_PORT=8012 -DTEST_ADMIN_PASSWORD=admin -Dmarklogic.okhttp.network.level=HEADERS -Dmarklogic.okhttp.network.output=STDERR
  ```

---

## 3. Local Setup Guide: Visual Studio Code

### 3.1 Recommended Extensions
Install the following VS Code extensions:
- **Extension Pack for Java** (`vscjava.vscode-java-pack`)
- **Gradle for Java** (`vscjava.vscode-gradle`)
- **XML** (`redhat.vscode-xml`)

### 3.2 Workspace Configuration (`.vscode/settings.json`)
Create or verify `.vscode/settings.json`:
```json
{
  "java.configuration.runtimes": [
    {
      "name": "JavaSE-17",
      "path": "/Library/Java/JavaVirtualMachines/temurin-17.jdk/Contents/Home",
      "default": true
    },
    {
      "name": "JavaSE-21",
      "path": "/Library/Java/JavaVirtualMachines/temurin-21.jdk/Contents/Home"
    }
  ],
  "java.compile.nullAnalysis.mode": "automatic",
  "java.test.defaultConfig": "default",
  "java.test.config": [
    {
      "name": "default",
      "vmArgs": [
        "-ea",
        "-Dmarklogic.okhttp.network.level=BASIC",
        "-Dmarklogic.okhttp.network.output=STDERR"
      ]
    }
  ]
}
```

### 3.3 VS Code Launch Configuration (`.vscode/launch.json`)
```json
{
  "version": "0.2.0",
  "configurations": [
    {
      "type": "java",
      "name": "Debug TokenAuthenticationInterceptorTest",
      "request": "launch",
      "mainClass": "com.marklogic.client.impl.okhttp.TokenAuthenticationInterceptorTest",
      "projectName": "marklogic-client-api"
    },
    {
      "type": "java",
      "name": "Remote Attach (JDWP)",
      "request": "attach",
      "hostName": "localhost",
      "port": 5005
    }
  ]
}
```

---

## 4. Build, Compilation, and Smoke Verification

From the terminal:
```bash
# 1. Clean and build all subproject bytecode (skipping integration tests)
./gradlew clean build -x test

# 2. Run standalone smoke tests (offline, no database required)
./gradlew marklogic-client-api:test --tests TokenAuthenticationInterceptorTest \
                                   --tests RedactingHttpLoggerTest \
                                   --tests ClosingHandlesTest
```
If the smoke tests pass with `BUILD SUCCESSFUL`, the compilation toolchain, annotation processor, and runtime dependencies are verified.

---

## 5. Debugging Playbook

### 5.1 High-Value Breakpoints

| Operation / Flow | Class | Method | Target Line | What to Inspect |
| :--- | :--- | :--- | :--- | :--- |
| **Document Write** | [`OkHttpServices.java`](file:///Users/rsingh/Code/GitHub/java-client-api/marklogic-client-api/src/main/java/com/marklogic/client/impl/OkHttpServices.java) | `putValue` | `putValue(...)` | Request URL, headers, ETag versioning, and serialized document body. |
| **Document Read** | [`OkHttpServices.java`](file:///Users/rsingh/Code/GitHub/java-client-api/marklogic-client-api/src/main/java/com/marklogic/client/impl/OkHttpServices.java) | `getValue` | `getValue(...)` | Content-Type, stream handling, and handle adapter mapping. |
| **Transactions** | [`OkHttpServices.java`](file:///Users/rsingh/Code/GitHub/java-client-api/marklogic-client-api/src/main/java/com/marklogic/client/impl/OkHttpServices.java) | `openTransaction` | `connect(...)` | Extracted `HostId` cookie value; transaction ID string. |
| **Transient Retries** | [`OkHttpServices.java`](file:///Users/rsingh/Code/GitHub/java-client-api/marklogic-client-api/src/main/java/com/marklogic/client/impl/OkHttpServices.java) | `calculateDelay` | Retry loop | HTTP response status (502/503/504), retry attempt count, sleep duration. |
| **DMSDK Backpressure** | [`WriteBatcherImpl.java`](file:///Users/rsingh/Code/GitHub/java-client-api/marklogic-client-api/src/main/java/com/marklogic/client/datamovement/impl/WriteBatcherImpl.java) | `add` | `batchList.add(...)` | Buffer size; whether `CallerRunsPolicy` is triggered by executor. |
| **Cloud Token Renewal** | [`TokenAuthenticationInterceptor.java`](file:///Users/rsingh/Code/GitHub/java-client-api/marklogic-client-api/src/main/java/com/marklogic/client/impl/okhttp/TokenAuthenticationInterceptor.java) | `intercept` | `renewer.apply(...)` | Token expiration timestamp; whether 401 triggered an inline renewal request. |
| **Error XML Parsing** | [`FailedRequest.java`](file:///Users/rsingh/Code/GitHub/java-client-api/marklogic-client-api/src/main/java/com/marklogic/client/impl/FailedRequest.java) | `parseFailedRequest` | `builder.parse(is)` | Raw error XML from server; verify `DocumentBuilderFactory` attributes. |

### 5.2 Safely Inspecting Wire Payloads
Do not enable `BODY` logging in shared or production environments. For local debugging:
```bash
# Safely inspect HTTP method, URL, status code, and sanitized headers (tokens redacted):
-Dmarklogic.okhttp.network.level=HEADERS -Dmarklogic.okhttp.network.output=STDERR
```

### 5.3 Thread and Connection Pool Profiling
When investigating suspected thread stalls or socket leaks:
1. **Thread Dump**: Run `jcmd <PID> Thread.print` or use IntelliJ Profiler / VisualVM.
   - Look for threads stuck in `java.net.SocketInputStream.socketRead0` without timeouts (Finding SCL-2).
2. **Connection Pool State**: Breakpoint at `OkHttpUtil.java:34` and inspect `connectionPool.idleConnectionCount()` and `connectionPool.connectionCount()`.

---

## 6. Prioritized Remediation Roadmap

```
  ┌────────────────────────────────────────────────────────────────────────┐
  │                           REMEDIATION ROADMAP                          │
  ├────────────────────────────────────────────────────────────────────────┤
  │                                                                        │
  │  [IMMEDIATE: Sprint 1]                                                 │
  │  ├── SEC-1: Restrict Jackson subtype validator in PojoRepository       │
  │  ├── SEC-2: Use XmlFactories.getDocumentBuilderFactory in FailedRequest│
  │  └── SCL-2: Set non-zero default read/write timeouts (e.g. 60s)        │
  │                                                                        │
  │  [NEAR-TERM: Sprints 2 - 3]                                            │
  │  ├── SCL-1: Make ConnectionPool configurable per DatabaseClient        │
  │  ├── SEC-3: Deprecate SSLHostnameVerifier.ANY & log security warning   │
  │  ├── MNT-2: Synchronize metadataCategories in DocumentManagerImpl       │
  │  └── SCL-3: Expose memory threshold parameter in WriteBatcher          │
  │                                                                        │
  │  [STRATEGIC: Sprints 4+]                                               │
  │  ├── MNT-1: Decompose OkHttpServices into modular domain handlers      │
  │  ├── MNT-3: Eliminate global System.setProperty("mail.mime.allowutf8") │
  │  └── Modernize functional test suite to eliminate hardcoded passwords │
  └────────────────────────────────────────────────────────────────────────┘
```

### 6.1 Phase 1: Immediate Priority (Sprint 1)
- **SEC-1 (CWE-502)**: In `PojoRepositoryImpl.java:99` and `PojoPageImpl.java:43`, replace `LaissezFaireSubTypeValidator.instance` with `BasicPolymorphicTypeValidator.builder().allowIfBaseType(...)`.
- **SEC-2 (CWE-611)**: In `FailedRequest.java:52`, replace raw `DocumentBuilderFactory.newInstance()` with `XmlFactories.getDocumentBuilderFactory()`.
- **SCL-2**: In `OkHttpUtil.java:99-100`, replace `0` millisecond timeouts with 60 seconds default, configurable via builder methods.

### 6.2 Phase 2: Near-Term Priority (Sprints 2–3)
- **SCL-1**: In `OkHttpUtil.java:34`, remove the JVM-wide static `ConnectionPool` and permit per-client connection pool configuration.
- **SEC-3**: In `DatabaseClientFactory.java:52`, mark `SSLHostnameVerifier.ANY` as `@Deprecated` and emit a warning log when instantiated.
- **MNT-2**: In `DocumentManagerImpl.java:51-74`, replace unsynchronized `HashSet` and mutable `boolean` with thread-safe data structures.
- **SCL-3**: In `WriteBatcherImpl.java`, introduce `withMaxMemoryBuffered(long bytes)` to prevent heap exhaustion.

### 6.3 Phase 3: Strategic Architectural Improvements (Sprints 4+)
- **MNT-1**: Refactor the 6,389-line `OkHttpServices` class into discrete, decoupled domain service classes.
- **MNT-3**: Replace global `System.setProperty("mail.mime.allowutf8", "true")` with local MIME message configurations.
- **Test Modernization**: Refactor legacy functional tests to read credentials dynamically from `test.properties` and eliminate hardcoded `"admin"` credentials.

---

## 7. Unknowns and Follow-up Questions

| Unknown / Question | Why It Matters | Evidence Needed |
| :--- | :--- | :--- |
| **Jakarta / Angus Mail Performance** | The migration from Oracle JavaMail to Jakarta/Angus Mail required a static `mail.mime.allowutf8` system property. Does this cause performance regressions in large multi-part bulk writes? | Benchmark comparing throughput of 10,000 multi-part document writes under JavaMail 1.6 vs Angus Mail 2.0. |
| **Intermittent `JacksonStreamTest` Failures** | Functional test documentation notes intermittent stream closed errors in CI runners. | Network packet capture and Jackson token stream consumption logs on Linux CI agents. |
| **AWS Dynamic ARM64 Fleet Costs** | `Jenkinsfile` provisions dynamic EC2 instances in `us-west`. If Jenkins jobs are aborted prematurely, does `terraform destroy` always fire? | AWS CloudWatch audit logs for lingering `java-client-instance-*` EC2 instances. |

---

## 8. Evidence Index

This index maps key architectural conclusions and audit findings to exact files and line numbers in the repository.

| Topic / Finding | File Path | Line Range | Key Symbols |
| :--- | :--- | :--- | :--- |
| **Client Creation Facade** | `marklogic-client-api/.../DatabaseClientFactory.java` | 27–125 | `DatabaseClientFactory`, `newClient` |
| **Property Source Adapter**| `marklogic-client-api/.../DatabaseClientPropertySource.java` | 28–142 | `DatabaseClientPropertySource`, `newClientBean` |
| **Builder Pattern** | `marklogic-client-api/.../DatabaseClientBuilder.java` | 27–120 | `DatabaseClientBuilder`, `withConnectionString` |
| **Transport Implementation**| `marklogic-client-api/.../OkHttpServices.java` | 85–280 | `OkHttpServices`, `connect`, `release` |
| **Retry Delays & Backoff** | `marklogic-client-api/.../OkHttpServices.java` | 98–135 | `DELAY_FLOOR`, `DELAY_CEILING`, `calculateDelay` |
| **Shared Connection Pool** | `marklogic-client-api/.../okhttp/OkHttpUtil.java` | 34 | `connectionPool = new ConnectionPool(5, 5, ...)` |
| **Infinite Timeouts** | `marklogic-client-api/.../okhttp/OkHttpUtil.java` | 99–100 | `clientBuilder.readTimeout(0, ...)` |
| **Hostname Verifier ANY** | `marklogic-client-api/.../okhttp/OkHttpUtil.java` | 133–134 | `(hostname, session) -> true` |
| **Redacting Log Interceptor**| `marklogic-client-api/.../okhttp/RedactingHttpLogger.java` | 25–45 | `RedactingHttpLogger`, `log` |
| **SEC-1 (Jackson Typing)** | `marklogic-client-api/.../PojoRepositoryImpl.java` | 96–99 | `activateDefaultTyping`, `LaissezFaireSubTypeValidator` |
| **SEC-2 (Unhardened XML)** | `marklogic-client-api/.../FailedRequest.java` | 51–59 | `DocumentBuilderFactory.newInstance()`, `parse` |
| **Secure XML Factories** | `marklogic-client-api/.../XmlFactories.java` | 93–128 | `makeNewDocumentBuilderFactory`, `FEATURE_SECURE_PROCESSING` |
| **DMSDK Write Batcher** | `marklogic-client-api/.../datamovement/impl/WriteBatcherImpl.java` | 65–120 | `WriteBatcherImpl`, `add`, `flushAndWait` |
| **DMSDK Query Batcher** | `marklogic-client-api/.../datamovement/impl/QueryBatcherImpl.java` | 50–110 | `QueryBatcherImpl`, `withBatchSize` |
| **CI/CD Pipeline** | `Jenkinsfile` | 1–439 | `runTests`, `deployAWSInstance`, `terraform destroy` |
| **Test Application Config**| `test-app/README.md` | 1–42 | `./gradlew mlDeploy`, `Undertow` reverse proxy |
| **Functional Tests Notes** | `marklogic-client-api-functionaltests/README.md` | 1–32 | `fastfunctest`, flaky test culprits |
