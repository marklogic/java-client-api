To run these tests, you should first deploy the test app in the `marklogic-client-api` module:

    ./gradlew -i mlDeploy

You can adjust the host that these tests connect to via the `src/test/resources/test.properties` file. However, while
an admin password is found in that file, many of these tests currently hardcode the password as "admin". Thus, 
adjusting the password in that file will not work yet. For now, you'll need to ensure that your admin user's password
is "admin". This will improved soon. 

Many of the tests have been refactored into a `fastfunctest` package. These tests typically run in seconds or less, 
as they depend on the app deployed by `mlDeploy` above. Tests not in this package are much slower and can take minutes
each, as they setup and teardown their own app instance. These tests will hopefully be refactored in the near future 
to use the deployed app, such that all of these tests can run in seconds each. 

For future development, it should be possible to sufficiently test every feature in `marklogic-client-api` via tests
within that same project. The only real difference with the tests in this project is that they took a lot longer to run
due to the setup/teardown in every test class. Now that that is no longer necessary, there is no real difference between
the nature of the tests in the two projects. Thus, it should be rare to add any more tests to this project. 

Some of these tests unfortunately fail intermittently when run by Jenkins (and when run locally). Notes on the main
culprits, as of 2022-11-11:

- `JacksonStreamTest` fails in both the unit and regression runs with a "stream closed" error; does not fail locally
- `BulkIOInputCallerTest` has tests that will fail often, but rarely locally
- `TestSearchOnJSON` and `TestStructuredQuery` both have failures that occur when the entire suite is run, but they both
pass locally consistently. This is usually due to a test earlier in the suite making a change to the test app deployed
to ML. These will hopefully be fixed soon in the future.
- `ApplyTransformJobTest` and `QueryBatcherJobReportTest` both have a "stop job" test that fairly consistently fails 
on ML 11 (not on ML 10) due to an off-by-one error. This is being actively investigated.


