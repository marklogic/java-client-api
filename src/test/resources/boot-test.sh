#!/bin/bash

DEFAULT_M2=$HOME/.m2/repository
M2_REPO=${1:-$DEFAULT_M2}

java -cp target/test-classes:target/classes:$M2_REPO/org/apache/httpcomponents/httpclient/4.1.1/httpclient-4.1.1.jar:$M2_REPO/org/apache/httpcomponents/httpcore/4.1/httpcore-4.1.jar:$M2_REPO/commons-logging/commons-logging/1.1.1/commons-logging-1.1.1.jar:$M2_REPO/org/slf4j/slf4j-api/1.7.4/slf4j-api-1.7.4.jar com.marklogic.client.test.util.TestServerBootstrapper
