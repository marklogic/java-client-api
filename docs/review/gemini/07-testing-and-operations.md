# MarkLogic Java Client API: Testing and Operations

This document analyzes the testing architecture, CI/CD automation pipelines, operational characteristics, and troubleshooting procedures for the MarkLogic Java Client API (version 8.2.0).

---

## 1. Test Pyramid & Test Suite Organization

The test suite spans three distinct subprojects with varying execution requirements, performance profiles, and isolation levels.

```
                      ▲
                     / \
                    /   \     Functional & QA End-to-End Tests
                   / QA  \    (marklogic-client-api-functionaltests:
                  / Test  \    runFragileTests, runSlowFunctionalTests)
                 /─────────\
                /           \   Fast Functional & Dev Tools Tests
               / Integration \  (marklogic-client-api:test,
              /   & Fast QA   \  fastfunctest, ml-development-tools:test)
             /─────────────────\
            /    Unit Tests     \ Standalone Unit Tests (No ML Required)
           /─────────────────────\ (Token Auth, Redacting Logger, Handlers)
```

### 1.1 Test Suite Breakdown

| Module / Target | Test Count & Nature | Execution Speed | External Dependencies | Description |
| :--- | :--- | :--- | :--- | :--- |
| **`marklogic-client-api` (Unit)** | ~15 classes | < 5 seconds | None (Mocked/Local) | Token renewer, `RedactingHttpLogger`, `ClosingHandles`, property sources, query builders. |
| **`marklogic-client-api` (Integration)** | ~180 classes | 2 – 5 minutes | Running MarkLogic (port 8012 via `mlDeploy`) | Core CRUD, DMSDK batchers, Optic rows, bitemporal, transactions, XML/JSON handles. |
| **`ml-development-tools`** | ~20 classes | < 1 minute | Running MarkLogic | Code generation, Data Services schema compilation, DB function proxy generators. |
| **`functionaltests` (`fastfunctest`)** | ~40 classes | 1 – 3 minutes | Running MarkLogic (port 8012 via `mlDeploy`) | Modern fast functional QA tests sharing pre-deployed test application. |
| **`functionaltests` (`runFragileTests`)**| ~15 classes | 2 – 5 minutes | Running MarkLogic (custom forest directory) | Tests sensitive to filesystem layout, failovers, or known race conditions. |
| **`functionaltests` (`runSlowFunctionalTests`)** | ~120 classes | 20 – 45 minutes | Running MarkLogic (admin privileges) | Legacy QA suite; creates and tears down dedicated REST instances per test class. |
| **Undertow Reverse Proxy Tests** | Selected classes | < 1 minute | Embedded Undertow + MarkLogic | Emulates MarkLogic Cloud / reverse proxy URL base paths and basic auth. |

---

## 2. Environment Requirements & Test Fixtures

### 2.1 MarkLogic Test Application (`test-app`)
Most tests in `marklogic-client-api` and `marklogic-client-api-functionaltests` do not create ad-hoc database configurations; they depend on a pre-configured database and REST server deployed via `ml-gradle` in [`test-app/`](file:///Users/rsingh/Code/GitHub/java-client-api/test-app/README.md).

- **Deployment Port**: `8012` (by default)
- **Content Database**: Configured with sample XML/JSON documents (e.g. musician documents for Optic joins loaded during setup).
- **Default Credentials**: Username `admin`, Password `admin`.
- **Deployment Command**:
  ```bash
  ./gradlew -i mlDeploy
  ```

### 2.2 Embedded Reverse Proxy (Undertow)
The repository includes an embedded Undertow HTTP proxy server used to test reverse-proxy traversal, base path routing (`marklogic.client.basePath`), and MarkLogic Cloud simulation:
- **Default Proxy Port**: `8020` (forwarding to `8012`)
- **HTTPS Port**: `443` (run with `-PrpsHttpsPort=443`, requires `sudo`)
- **Authentication Constraint**: Undertow reverse proxy only forwards HTTP Basic authentication; target MarkLogic app servers must allow `basic` or `digestbasic`.

---

## 3. Test Execution Procedures

### 3.1 Running Pure Unit Tests (Offline / No MarkLogic)
```bash
./gradlew test -x marklogic-client-api:test -x ml-development-tools:test
```
Or execute specific isolated unit test classes:
```bash
./gradlew marklogic-client-api:test --tests TokenAuthenticationInterceptorTest \
                                   --tests RedactingHttpLoggerTest \
                                   --tests ClosingHandlesTest
```

### 3.2 Deploying MarkLogic Test Application
Ensure a local or containerized MarkLogic instance is running on port 8000/8001/8002:
```bash
./gradlew -i mlDeploy mlReloadSchemas
```

### 3.3 Running Client Integration Tests
```bash
./gradlew marklogic-client-api:test
```

### 3.4 Running Reverse Proxy Integration Tests
Spins up Undertow in background, executes tests with `-PtestUseReverseProxyServer=true`, and tears down Undertow:
```bash
./gradlew -PtestUseReverseProxyServer=true runReverseProxyServer \
    marklogic-client-api:test --tests ReadDocumentPageTest
```

### 3.5 Running Functional QA Suites
```bash
# Fast functional suite (recommended for daily dev)
./gradlew marklogic-client-api-functionaltests:runFastFunctionalTests

# Fragile suite
./gradlew marklogic-client-api-functionaltests:runFragileTests

# Full legacy slow functional suite (long running)
./gradlew marklogic-client-api-functionaltests:runSlowFunctionalTests
```

---

## 4. Known Flaky Tests & Test Debt

Documented in [`marklogic-client-api-functionaltests/README.md`](file:///Users/rsingh/Code/GitHub/java-client-api/marklogic-client-api-functionaltests/README.md#L20-L30):
1. **`JacksonStreamTest`**: Intermittently fails in CI with `"stream closed"` error due to stream consumption timing differences.
2. **`BulkIOInputCallerTest`**: Fails intermittently under heavy concurrent load on CI runners.
3. **`TestSearchOnJSON` & `TestStructuredQuery`**: Suffer from cross-test state pollution when the full test suite runs without database resetting.
4. **`ApplyTransformJobTest` & `QueryBatcherJobReportTest`**: Suffer from an off-by-one race condition on job cancellation specifically under MarkLogic 11.
5. **Hardcoded Admin Password**: Multiple functional tests hardcode `"admin"` as the password, ignoring `test.properties`.

---

## 5. CI / CD Pipeline Analysis (Jenkinsfile)

The repository uses an enterprise-grade Jenkins declarative pipeline ([`Jenkinsfile`](file:///Users/rsingh/Code/GitHub/java-client-api/Jenkinsfile)) supporting multi-version matrices, containerized MarkLogic setups, and dynamic AWS ARM64 infrastructure.

```
                               ┌──────────────────────────┐
                               │   Jenkins Pipeline Init  │
                               └──────────────────────────┘
                                             │
                       ┌─────────────────────┴─────────────────────┐
                       │                                           │
                       ▼                                           ▼
             [Pull Request Flow]                          [Develop Branch Flow]
                       │                                           │
         Docker: ML 12 UBI (x86_64)                        Publish Artifacts
                       │                                   Javadoc Zip Archive
          marklogic-client-api:test                                │
          Reverse Proxy Validation                                 ▼
                       │                             Regression Matrix (x86_64)
                       ▼                             (ML 11 & ML 12 Containers)
                Pipeline Success                                   │
                                                                   ▼
                                                     AWS Dynamic ARM64 Provisioning
                                                     (Terraform: EC2 aarch64)
                                                                   │
                                                     Regression Matrix (ARM64)
                                                     (ML 11 & ML 12 UBI-9 ARM)
                                                                   │
                                                     Terraform Destroy (Auto-Approve)
```

### 5.1 Build Matrix & Supported Runtimes
- **Java Runtimes**: OpenJDK 17 (`jdk-17.0.2` / `amazon-corretto-17`) and OpenJDK 21 (`jdk-21.0.1` / `amazon-corretto-21`).
- **MarkLogic Platforms**:
  - `marklogic-server-ubi:latest-11` (MarkLogic 11 on UBI)
  - `marklogic-server-ubi:latest-12` (MarkLogic 12 on UBI)
  - `marklogic-server-ubi9-arm:latest-11` (MarkLogic 11 on ARM64)
  - `marklogic-server-ubi9-arm:latest-12` (MarkLogic 12 on ARM64)

### 5.2 Dynamic AWS Infrastructure via Terraform
In regression mode (`params.regressions = true`), the pipeline dynamically provisions dedicated ARM64 compute in AWS:
1. Deploys an AWS EC2 aarch64 instance using Terraform templates in `terraform-templates/java-client-api`.
2. Attaches testing EBS volumes, installs Amazon Corretto JDK 17/21, and registers the instance as a dynamic Jenkins agent.
3. Pulls ARM64 container images and runs all regression test suites.
4. The `post { always }` block executes `terraform destroy -auto-approve` to guarantee zero dangling AWS infrastructure costs.

### 5.3 Publishing & Release Flow
- On commits to the `develop` branch, the `publish` stage invokes `./gradlew publish` using Artifactory credentials (`DMC_USER` / `DMC_PASSWORD`).
- Generates Javadocs via `./gradlew javadoc`, compresses them into `javadoc.zip`, and archives them as pipeline build artifacts.

---

## 6. Observability, Logging, and Operational Diagnostics

### 6.1 Logging Categories & Configuration
The client uses SLF4J for internal diagnostics. Key logger categories:

| Logger Category | Default Level | Diagnostic Use Case |
| :--- | :--- | :--- |
| `marklogic.okhttp.network` | `INFO` | Wire-level HTTP traffic (activated via `marklogic.okhttp.network.level`). |
| `com.marklogic.client.impl.OkHttpServices` | `INFO` | Request dispatching, retry attempts, multipart parsing errors. |
| `com.marklogic.client.datamovement` | `INFO` | DMSDK batch status, worker thread lifecycle, forest failovers. |
| `com.marklogic.client.DatabaseClient` | `WARN` | Connection lifecycle and client release warnings. |

### 6.2 Enabling Wire-Level HTTP Logging
To log HTTP wire traffic without modifying code:
```bash
# Log basic method, URL, and status codes to SLF4J
-Dmarklogic.okhttp.network.level=BASIC -Dmarklogic.okhttp.network.output=LOGGER

# Log full request and response headers to standard error
-Dmarklogic.okhttp.network.level=HEADERS -Dmarklogic.okhttp.network.output=STDERR
```

### 6.3 Common Operational Failures & Resolution

| Error / Symptom | Root Cause | Diagnosis & Resolution |
| :--- | :--- | :--- |
| **`FailedRequestException: 403 Forbidden: You have attempted to access an HTTPS server using HTTP`** | Client attempted plain HTTP connection against an SSL-enabled MarkLogic App Server. | Enable SSL: `.withSSLProtocol("TLSv1.2")` or `.withTrustStorePath(...)`. |
| **`FailedRequestException: 401 Unauthorized`** | Incorrect username/password, wrong `authType` (e.g. Basic instead of Digest), or missing Cloud API key. | Verify credentials; check MarkLogic App Server authentication setting (`digest`, `basic`, or `digestbasic`). |
| **`SocketTimeoutException: connect timed out`** | Firewall blocking port, wrong IP address, or MarkLogic host down. | Check network routing and TCP port reachability via `nc -zv <host> <port>`. |
| **Application threads hanging indefinitely** | Socket read timeout is default `0` (infinite); MarkLogic node stuck in heavy query. | Implement `OkHttpClientConfigurator` setting `readTimeout(60, SECONDS)`. |
| **`FailedRequestException: 502/503/504 Retry exhausted`** | Backend MarkLogic cluster restart or failover exceeded `maximumRetryDelay` (120s). | Increase retry ceiling via `-Dcom.marklogic.client.maximumRetryDelay=300`. |
