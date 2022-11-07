To run these tests, you should first deploy the test app in the `marklogic-client-api` module:

    ./gradlew -i mlDeploy

You can adjust the host that these tests connect to via the `src/test/resources/test.properties` file. However, while
an admin password is found in that file, many of these tests currently hardcode the password as "admin". Thus, 
adjusting the password in that file will not work yet. For now, you'll need to ensure that your admin user's password
is "admin". This will improved soon. 

Most of these tests are slow because they setup and teardown a complete ML application. An effort is underway to 
gradually refactor each test to depend on the test app that is deployed above. In general, this should result in each 
test running in a matter of seconds as opposed to minutes. One exception to this is the frequent usage of `sleep()` 
in these tests. An effort is also underway to gradually remove those calls or shorten them as much as possible.
