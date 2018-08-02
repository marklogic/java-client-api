'use strict';
var uri;
var text;

declareUpdate({explicitCommit: true});

// console.log('beginning transaction for uri='+uri+', text='+text);

xdmp.documentInsert(uri, new NodeBuilder().addText(text).toNode());
