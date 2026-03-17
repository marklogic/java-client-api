# Build & Test Guide

## Prerequisites

| Requirement    | Notes                                                                                        |
| -------------- | -------------------------------------------------------------------------------------------- |
| JDK 17+        | JDK 21 also works; set `JAVA_HOME` or rely on Gradle toolchain auto-provisioning             |
| Docker         | Required for functional tests (MarkLogic container)                                          |
| Gradle wrapper | Use `./gradlew` (Linux/macOS) or `gradlew.bat` (Windows); do **not** install Gradle globally |

---

## Quick Build (no tests)

```bash
./gradlew clean build -x test
```

---

## Unit Tests — core library only

```bash
./gradlew :marklogic-client-api:test
```

Unit tests have **no external dependencies**; they run without MarkLogic.

---

## Developer-Tools Tests

```bash
./gradlew :ml-development-tools:test
```

---

## Functional / Integration Tests

Functional tests require a running MarkLogic instance. Start it with Docker Compose first:

```bash
docker compose up -d
```

Then deploy the test application and run the functional tests:

```bash
./gradlew :test-app:mlDeploy :test-app:mlReloadSchemas
./gradlew :marklogic-client-api-functionaltests:test
```

> **Tip:** The `Jenkinsfile` contains the authoritative CI test sequence if the local steps diverge.

---

## Running a Specific Test Class

```bash
./gradlew :marklogic-client-api:test --tests "com.marklogic.client.test.SomeTest"
```

---

## Gradle Properties

Key properties live in `gradle.properties` and `marklogic-client-api/gradle.properties`. Override on the command line with `-P<key>=<value>`:

```bash
./gradlew :marklogic-client-api:test -PmlHost=localhost -PmlPort=8000
```

---

## Build Artifacts

After a successful build the JAR is at:

```
marklogic-client-api/build/libs/marklogic-client-api-<version>.jar
```

---

## Linting / Static Analysis

No dedicated lint step is configured in the current Gradle build.

---

## Common Pitfalls

- Functional tests **will hang or fail** if Docker is not running or MarkLogic has not finished starting. Wait ~30 s after `docker compose up -d` before deploying.
- Java toolchain auto-provisioning requires internet access on first run. On air-gapped machines set `org.gradle.java.installations.paths` in `~/.gradle/gradle.properties`.
