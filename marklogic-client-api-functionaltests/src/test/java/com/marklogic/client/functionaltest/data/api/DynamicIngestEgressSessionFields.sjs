'use strict';
var endpointState;      // jsonDocument?
var input;              // jsonDocument*
var endpointConstants;  // jsonDocument?
declareUpdate();

const w = fn.head(xdmp.fromJSON(endpointConstants));
const endpt = fn.head(xdmp.fromJSON(endpointState));
var thisBatchStart = endpt.returnIndex;

var res = [];
var docResults = null;

const inputs =
    (input instanceof Sequence) ? input.toArray().map(item => fn.head(xdmp.fromJSON(item))) :
    (input instanceof Document) ? [fn.head(xdmp.fromJSON(input))] :
                                  [ {UNKNOWN: input} ];

// Return document back to client
// Open a cache with session
xdmp.setSessionField("test1", "Java Client API");

if (!fn.docAvailable('/bulk-input-output-call.json')) {

var sessionField = xdmp.getSessionField("test1");
var retContent = 'This is the return doc content';
xdmp.documentInsert(
       '/bulk-input-output-call.json',  {session:sessionField, inputs:retContent},
                                          {permissions:[
                                              xdmp.permission('rest-reader', 'read'),
                                              xdmp.permission('rest-writer', 'update')
                                              ],
                                              collections: 'bulk-input-output-call'});
}

let ret = null;

if (endpt.returnIndex < w.max && inputs != null && inputs.length != 0) {

var sessionField = xdmp.getSessionField("test1");

// Write input(s) + some data from session cache
var uri = xdmp.random(2000000);
xdmp.documentInsert(
            '/marklogic/ds/test/bulkInputCaller/' +uri+'.json',
            {session:sessionField, inputs:inputs},
            {permissions:[
                xdmp.permission('rest-reader', 'read'),
                xdmp.permission('rest-writer', 'update')
                ],
                collections: w.collectionName});



// We need endpointstate to go back to client as the first in the return.
res.push(endpt);
if (docResults === null) {
docResults = fn.doc('/bulk-input-output-call.json');
res.push(docResults);
}

ret = Sequence.from(res);
}

console.log(ret);
ret;