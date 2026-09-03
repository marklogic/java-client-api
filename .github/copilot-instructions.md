# Project Guidelines

## Architecture
- `marklogic-client-api`: core Java client library for interacting with the MarkLogic REST API.
- `ml-development-tools`: Gradle plugin and generators for Data Services endpoint proxies/tests.
- `marklogic-client-api-functionaltests`: functional/regression-style tests, split into fragile/fast/slow groups.
- `test-app`: ml-gradle deployment project that provisions test infrastructure in MarkLogic.
- `examples`: supporting code used by tests and usage examples.

## Build And Test
- Prefer Gradle from the repo root.
- Quick compile verification: `./gradlew clean build -x test`
- Core module tests: `./gradlew marklogic-client-api:test`
- Plugin tests (includes generated tests workflow): `./gradlew ml-development-tools:test`
- Functional tests must run in this order to reduce flakiness:
  1. `./gradlew marklogic-client-api-functionaltests:runFragileTests`
  2. `./gradlew marklogic-client-api-functionaltests:runFastFunctionalTests`
  3. `./gradlew marklogic-client-api-functionaltests:runSlowFunctionalTests`

## Test Environment
- Java 17+ is required for current releases.
- Most tests require a running MarkLogic instance and deployed test resources.
- Typical setup sequence:
  1. `docker compose up -d --build`
  2. `./gradlew -i mlWaitTillReady`
  3. `./gradlew -i mlDeploy`
  4. Run module tests
- Override local MarkLogic connection settings via `gradle-local.properties` (`mlHost`, `mlPassword`).

## Quality Controls
- Treat compile warnings as failures: project builds enforce `-Xlint:unchecked`, `-Xlint:deprecation`, and `-Werror`.
- Keep dependency security constraints intact (e.g. forced/excluded dependencies for CVE mitigation in Gradle files).
- When adding or changing first-party Java/Kotlin code, run security scanning steps used by this workspace workflow before finalizing changes.
- Do not relax quality gates (tests/compilation) to make a change pass; fix the underlying issue.

## Code Generation And Automation
- Data Services proxy generation is automated; use `generateEndpointProxies` instead of hand-writing proxy classes.
- `ml-development-tools` test automation uses `generateTests` and `fixMjsModulesForMarkLogic12` before `test`.
- Generated sources commonly include an "IMPORTANT: Do not edit" header. Regenerate from source declarations instead of editing generated output directly.
- For changes affecting generation logic, validate both generator behavior and generated artifact compilation/tests.

## Conventions For Changes
- Keep edits scoped to the target module; avoid cross-module churn unless required.
- Prefer existing patterns in nearby code over introducing new abstractions.
- For test-related fixes, document whether behavior changes impact unit tests, functional tests, or deployment setup.

## Docs To Link (Do Not Duplicate)
- `README.md`: product overview, dependency usage, Java compatibility.
- `CONTRIBUTING.md`: local build/test workflow and MarkLogic test setup.
- `ml-development-tools/src/test/example-project/README.md`: plugin-focused usage/testing notes.
