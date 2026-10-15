# MarkLogic Java Client API: Technical Architecture & Security Audit

This directory contains the comprehensive, multi-phase architectural reverse-engineering report, security vulnerability assessment, scalability analysis, operational review, and developer guide for the **MarkLogic Java Client API** (version 8.2.0).

---

## Documentation Index

The audit is structured across eight dedicated technical documents:

| Document | Focus Areas | Key Contents |
| :--- | :--- | :--- |
| **[01. Executive Summary & Inventory](01-executive-summary-and-inventory.md)** | Overview & Structure | Purpose, module boundaries (5 subprojects), source set mapping, technology stack, dependency direction diagram, public API vs internal SPI demarcation, domain glossary. |
| **[02. Architecture & Design Patterns](02-architecture-and-design.md)** | Architectural Blueprints | Architectural styles, design patterns (Facade, Adapter/Handle, Producer-Consumer), C4 System Context & Container diagrams, Component diagram, Initialization & Shutdown sequences, unwritten convention boundaries. |
| **[03. End-to-End Execution Flows](03-execution-flows.md)** | Runtime Call Tracing | Step-by-step call chains with Mermaid sequence diagrams: Document Read/Write, Transactions, DMSDK Batch Ingestion, DMSDK Parallel Forest Querying, Optic API Plans, Authentication, Failure & Retry, and Shutdown. |
| **[04. Heavy-Lifting Code Analysis](04-heavy-lifting-code-analysis.md)** | Core Components & Hotspots | In-depth audit of the 15+ most critical classes, god class analysis (`OkHttpServices` 6,389 lines, `DocumentManagerImpl`), DMSDK state machines, expression AST generation (`FnExpr`, `CtsExpr`, `PlanBuilderImpl`), and Jackson POJO reflection. |
| **[05. Configuration Reference](05-configuration-reference.md)** | Configuration & Transport | Client builders, property source maps (`marklogic.client.*`), connection strings, authentication modes, SSL/TLS options, JVM system properties, OkHttp transport defaults, and DMSDK settings. |
| **[06. Quality, Security & Scalability](06-quality-security-and-scalability.md)** | Risk Assessment & Findings | Grounded findings categorized by severity: SEC-1 (Jackson Polymorphic Deserialization), SEC-2 (Unhardened XML in `FailedRequest`), SEC-3 (Insecure Hostname Verifier `ANY`), SCL-1 (Shared static `ConnectionPool`), SCL-2 (Infinite Socket Timeouts), MNT-1 (God Class). |
| **[07. Testing & Operations](07-testing-and-operations.md)** | Test Suites & CI/CD | Test pyramid, test execution commands, `test-app` deployment, embedded Undertow reverse proxy, known flaky tests, Jenkins CI pipeline with dynamic AWS Terraform ARM64 fleet, and operational observability. |
| **[08. Developer Playbook & Setup Guide](08-developer-playbook-and-setup.md)** | Developer Guide & Roadmap | IDE setup for IntelliJ IDEA & VS Code, debugging playbook with high-value breakpoint locations and wire payload inspection, prioritized remediation roadmap (Immediate, Near-Term, Strategic), unknowns, and evidence index. |

---

## Audit Highlights & Key Findings

### 1. Technology & Runtime Profile
- **Primary Languages**: Java (compiled for Java 17+, tested on Java 17 and 21) and XQuery/Server-side JavaScript (MarkLogic test fixtures).
- **Build System**: Gradle 9.3.1 multi-project build (`settings.gradle` orchestrating 5 subprojects).
- **HTTP Transport**: Square OkHttp 4.12.0 backed by Jackson 2.18.2 for JSON and Jakarta Mail (Angus Mail) for MIME multipart payloads.
- **Server Compatibility**: MarkLogic Server 10.x, 11.x, and 12.x on x86_64 and ARM64.

### 2. Critical Security Findings
- **SEC-1 (High - CWE-502)**: `PojoRepositoryImpl.java:99` and `PojoPageImpl.java:43` configure Jackson `ObjectMapper` with `activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ...)` which permits arbitrary polymorphic subtype deserialization and exposes the host application to Remote Code Execution (RCE) via Jackson gadget chains.
- **SEC-2 (Medium-High - CWE-611)**: While `XmlFactories.java` disables DTDs and external entities across most XML operations, `FailedRequest.java:52-59` parses server XML error responses using a raw `DocumentBuilderFactory.newInstance()` with no external entity or DTD protections, risking local file disclosure and SSRF.
- **SEC-3 (Medium - CWE-295)**: `DatabaseClientFactory.SSLHostnameVerifier.ANY` disables TLS hostname verification entirely via `(hostname, session) -> true`, leaving connections vulnerable to Man-In-The-Middle attacks if enabled in production.

### 3. Scalability & Operational Findings
- **SCL-1 (Medium)**: All `DatabaseClient` instances share a single JVM-wide static `ConnectionPool` in `OkHttpUtil.java:34` capped at 5 idle connections, causing connection thrashing in multi-database applications.
- **SCL-2 (High)**: Default socket `readTimeout` and `writeTimeout` are set to `0` (infinite) in `OkHttpUtil.java:99-100`, risking permanent thread pool starvation if network routes or server nodes stall.
- **MNT-1 (High)**: `OkHttpServices.java` is a 6,389-line monolithic God Class coupling transport, retries, multipart parsing, transactions, and Optic query execution into a single component.

---

## Prioritized Action Plan

```
  Immediate (Sprint 1)         Near-Term (Sprints 2-3)          Strategic (Sprints 4+)
  ┌───────────────────────┐    ┌───────────────────────────┐    ┌───────────────────────────┐
  │ Fix Jackson Deser     │    │ Configurable ConnPool     │    │ Decompose OkHttpServices  │
  │ (SEC-1)               │    │ (SCL-1)                   │    │ (MNT-1)                   │
  │                       │    │                           │    │                           │
  │ Harden XML in         │    │ Deprecate Hostname ANY    │    │ HTTP/2 Multiplexing &     │
  │ FailedRequest (SEC-2) │    │ (SEC-3)                   │    │ Non-blocking Reactive API │
  │                       │    │                           │    │                           │
  │ Set Safe Non-Zero     │    │ Synchronize DocManager    │    │ Modernize QA Test Suite   │
  │ Timeouts (SCL-2)      │    │ State (MNT-2)             │    │ (Remove hardcoded creds)  │
  └───────────────────────┘    └───────────────────────────┘    └───────────────────────────┘
```

For complete details, please refer to the individual documents indexed above.
