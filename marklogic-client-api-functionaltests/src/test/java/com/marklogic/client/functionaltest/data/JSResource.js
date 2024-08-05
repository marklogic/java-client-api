/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

// module that exports get, post, put and delete
function get(context, params) {
 context.outputTypes = ["application/json"];
 var arg1 = params.arg1;
 var arg2 = params.arg2;
 var x = arg1.toString();


return {
	"argument1": x,
	"argument2": arg2,
	"database-name":xdmp.databaseName(xdmp.database()),
	"document-count":fn.count(fn.doc()),
    "content": "This is a JSON document",
    "document-content": fn.doc(),
    "response": xdmp.getResponseCode(),
	"outputTypes": context.outputTypes,

  }
};
function post(context, params, input) {

   var argUrl = params.uri;

    xdmp.eval(" \
    declareUpdate(); \
    var argUrl; \
    var sibling=cts.doc(argUrl).root.content; \
    var inputObject; \
    var newNode = new NodeBuilder(); \
	newNode.addNode(inputObject); \
	var named = newNode.toNode().xpath('.//array-node()'); xdmp.nodeInsertAfter(sibling,named);\
 ",{"inputObject":input,"argUrl":argUrl},{"isolation":"different-transaction"});
   return ({"response": xdmp.getResponseCode()})

 };

// Function responding to PUT method - must use local name 'put'.
function put(context, params, input) {
    var argUrl = params.uri;
    var inputObject = input;
   //xdmp.documentInsert(argUrl,input);
  xdmp.eval("declareUpdate(); var argUrl; var input;xdmp.documentInsert(argUrl,input)",{"argUrl":argUrl,"input":inputObject},{"isolation":"different-transaction"});
  var count = xdmp.eval("fn.count(fn.doc())");
  xdmp.log(count);
   return ({"response": xdmp.getResponseCode()})
};

// Function responding to DELETE method - must use local name 'delete'.
function deleteFunction(context, params) {
    var docuri = params.uri;
    xdmp.documentDelete(docuri);
	return({"response": xdmp.getResponseCode()})
};
exports.GET = get;
exports.POST = post;
exports.PUT = put;
exports.DELETE = deleteFunction;
