'use strict';
declareUpdate(); // Note: uncomment if changing the database state

var api_session; // instance of null
var uri; // instance of xs.string
var content; // instance of xs.string

// TODO:  produce the xs.boolean output from the input variables

var jsonNode = new NodeBuilder();
jsonNode.addText(content);
jsonNode = jsonNode.toNode();

xdmp.documentInsert(uri, jsonNode);

console.log("Return from CLIENT API uri value is :", uri);
console.log("Return from CLIENT API content value is :", content);
