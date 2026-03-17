# System Overview — MarkLogic Java Client API

## Purpose

The **MarkLogic Java Client API** (`com.marklogic:marklogic-client-api`) is a Java library that exposes MarkLogic Server's REST API as a type-safe, fluent Java interface. It supports reading, writing, deleting, and querying JSON, XML, binary, and text documents, as well as ACID multi-statement transactions, semantic (SPARQL/RDF), Full-text search, alerting, Data Services, and Row Manager (Optic API).

## Repository Root

`marklogic-client-api-parent` (Gradle multi-project).

## Modules

| Module                                 | Description                                                                  |
| -------------------------------------- | ---------------------------------------------------------------------------- |
| `marklogic-client-api`                 | Core library — all production source code                                    |
| `marklogic-client-api-functionaltests` | Functional / integration tests requiring a live MarkLogic instance           |
| `ml-development-tools`                 | Kotlin-based developer tooling (code generation helpers)                     |
| `test-app`                             | MarkLogic application deployed to the test server (modules, schemas, config) |
| `examples`                             | Standalone usage examples                                                    |

## Runtime Requirements

- **Java 17** (minimum; Java 21 also supported and tested in CI)
- **MarkLogic Server** (for integration/functional tests) — started via `docker-compose.yaml`

## Technology Stack

- Build: **Gradle** (wrapper at `./gradlew`)
- Test framework: **JUnit 5** (unit) + MarkLogic functional test harness
- CI: **Jenkins** (`Jenkinsfile`) — Docker-based MarkLogic, parallel Java 17/21 builds
- Primary language: **Java**; developer tooling in **Kotlin**

## Key External Dependencies

- OkHttp (HTTP client transport)
- Jackson (JSON serialization)
- SLF4J / Logback (logging)

## Relationship to MarkLogic Server

All network communication travels over the **MarkLogic REST Management and Client APIs** (typically port 8000/8002). The library never connects directly to MarkLogic's internal ports; authentication is via HTTP Digest or certificate.
