# Using Cookbook Examples

The most important use of cookbook examples is reading the source code.  You
can do this on [github](https://github.com/marklogic/java-client-api) or on
your machine once you've cloned the code from github.

To run the examples, first edit the
[Example.properties](../../../../../../resources/Example.properties) file in the
distribution to specify the connection parameters for your server. Most
Cookbook examples have a main method, so they can be run from the command-line
like so:

    java -cp $CLASSPATH com.marklogic.client.example.cookbook.DocumentWrite

This, of course, requires that you have all necessary dependencies in the env
variable $CLASSPATH.  You can get the classpath for your machine by executing the the following gradle task

    ./gradlew printClasspath

# Testing Cookbook Examples

Most cookbook examples pass their unit test if they run without error.  First
edit the [Example.properties](../../../../../../resources/Example.properties) file
in the distribution to specify the connection parameters for your server. Then
run `./gradlew test` while specifying the unit test you want to run, for example:

    ./gradlew java-client-api:test -Dtest.single=DocumentWriteTest

The above command runs the DocumentWriteTest unit test in java-client-api sub project.

# Creating a Cookbook Example

We encourage community-contributed cookbook examples!  Make sure you follow
the guidelines in [CONTRIBUTING.md](../../../../../../../../CONTRIBUTING.md)
when you submit a pull request.  Each cookbook example should be runnable from
the command-line, so it should have a static `main` method.  The approach in
the code should come as close as possible to production code (code one would
reasonably expect to use in a production application), while remaining as
simple as possible to facilitate grokking for newbies to the Java Client API.
It should have helpful comments throughout, including javadocs since it will
show up in the published javadocs.  It should be added to
[AllCookbookExamples.java](https://github.com/marklogic/java-client-api/blob/master/marklogic-client-api/src/main/java/com/marklogic/client/example/cookbook/AllCookbookExamples.java)
in order of recommended examples for developers to review.

It should have a unit test added to
[this package](https://github.com/marklogic/java-client-api/tree/master/marklogic-client-api/src/test/java/com/marklogic/client/test/example/cookbook).
The unit test can test whatever is needed, however most cookbook unit tests
just run the class and consider it success if no errors are thrown.  Some
cookbook examples, such as SSLClientCreator and KerberosClientCreator cannot be
included in unit tests because the unit tests require a server configured with
digest authentication and those tests require a different authentication
scheme.  Any cookbook examples not included in unit tests run the risk of
breaking without anyone noticing--hence we have unit tests whenever possible.
