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
	This directory contains jar files for the MarkLogic Java API
	(client-api-java-1.0-SNAPSHOT.jar) as well as its library dependencies.

You are also encouraged to join the MarkLogic developer mailing list:

		http://community.marklogic.com/discuss/

Known Issues for the Early Access release:

*  Transform Extensions that do no follow the required conventions or have 
   syntax errors cannot be updated deleted.  To remove the transform, clear
   the Extensions database and restart the server.

*  Resource Extensions do not support updates.  You can create extensions only
   to read database resources.

===================================================================
MarkLogic Corp, http://marklogic.com
