Copyright 2012 MarkLogic Corporation.  All Rights Reserved.

MarkLogic Client API for Java version ${project.version}

This release constitutes an early-access release subject to the terms of your
agreement.

This distribution contains code and basic documentation for Client API for Java

The directory structure is organized as follows:

docs/
	This directory contains the API documentation (Javadoc).

example/
    This directory contains cookbook examples and supporting data as well as
    examples of content representation extensions.  Please see the README.txt
    file in the directory for more detail.

lib/
	This directory contains two jar files.

	*  client-api-java-1.0-SNAPSHOT-jar-with-dependencies.jar contains the classes
	   for the API as well as classes from library dependencies.

	*  client-api-java-1.0-SNAPSHOT.jar contains only the classes for the API.

You are also encouraged to join the MarkLogic developer mailing list:

		http://community.marklogic.com/discuss/

Notes:

At this time, the QueryOptions class provides a representation for query options
read from the database but cannot be used to write query options to the database.
Instead, create query options as an XML or JSON structure.  Please see the 
QueryOptions and StringOptionsSearch cookbook examples as well as the
query-options-template.xml template file.

===================================================================
MarkLogic Corp, http://marklogic.com
