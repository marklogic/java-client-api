#!/bin/bash

DEFAULT_M2=$USERPROFILE/.m2/repository
M2_REPO=${1:-$DEFAULT_M2}

java -cp "target/test-classes;target/classes;$M2_REPO/org/apache/httpcomponents/httpclient/4.5.3/httpclient-4.5.3.jar;$M2_REPO/org/apache/httpcomponents/httpcore/4.4.6/httpcore-4.4.6.jar;$M2_REPO/commons-logging/commons-logging/1.2/commons-logging-1.2.jar;$M2_REPO/org/slf4j/slf4j-api/1.7.25/slf4j-api-1.7.25.jar" com.marklogic.client.test.util.TestServerBootstrapper teardown

