'use strict';
var endpointDirectory;
var functionName;
xdmp.invokeFunction(
    () => cts.doc(endpointDirectory+functionName+'.api'),
    {
        database: xdmp.modulesDatabase(),
        modules:  0,
        root:     '/'
    }
);
