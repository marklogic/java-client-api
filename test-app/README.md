This is an ml-gradle project for deploying a test application to MarkLogic that the tests in ./marklogic-client-api and 
most of the tests in ./marklogic-client-api-functional-tests depends on. 

To deploy it, run this from the root directory of this repository:

    ./gradlew -i mlDeploy

## Running the reverse proxy

This project also includes a Gradle task for running a reverse proxy server using [Undertow](https://undertow.io/). 
This is intended to support testing the use of a "base path" parameter with the Java Client and to also do a reasonable
job of emulating how MarkLogic Cloud works. 

Note - the reverse proxy server only supports basic authentication, not digest authentication. Thus, you need to ensure
that any MarkLogic app server that you proxy requests to supports either "digestbasic" or "basic" for authentication. 
That includes your Admin, Manage, and App-Services app servers.

To run the server, run the following:

    ./gradlew runBlockingReverseProxyServer

By default, this will listen on port 8020 and proxy requests based on the mapping that it logs when the server is 
started up.

To emulate how MarkLogic Cloud works, run the following (you can use `runBlock` as an abbreviation, Gradle will figure 
out what you mean):

    sudo ./gradlew runBlock -PrpsHttpsPort=443

This will result in the server listening for HTTPS requests on port 443; sudo is required since this listens to a port
under 1024. 

You can also specify custom mappings via the Gradle task. For example, if you have a MarkLogic app server listening on 
port 8123 and you want to associate a path of "/my/custom/server" to it, you can do:

    ./gradlew runBlock -PrpsCustomMappings=/my/custom/server,8123
