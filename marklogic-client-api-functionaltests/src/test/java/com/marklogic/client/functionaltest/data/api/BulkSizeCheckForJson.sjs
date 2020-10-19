'use strict';
var endpointState;      // jsonDocument?
var endpointConstants;  // jsonDocument?
var input;              // jsonDocument*
declareUpdate();

console.log("Length of input is ");
var arrayLen =   input.toArray().length;

console.log("=====");
console.log(arrayLen);
console.log("=====");

const work = fn.head(xdmp.fromJSON(endpointConstants));
const state = fn.head(xdmp.fromJSON(endpointState));

state.next = state.next + 1;
const inputs =
    (input instanceof Sequence) ? input.toArray().map(item => fn.head(xdmp.fromJSON(item))) :
    (input instanceof Document) ? [fn.head(xdmp.fromJSON(input))] :
                                  [ {UNKNOWN: input} ];

var uriCnt = state.next;
console.log("******");
console.log(uriCnt);
console.log("******");

let returnValue = null;
if (inputs != null) {
if (state.next <= work.max) {

for (var i=0; i<arrayLen; i++) {
// To Make sure that $bulk.inputBatchSize number of docs are inserted. Check on Java client.

        xdmp.documentInsert(
            '/marklogic/ds/test/bulkInputCaller/' +uriCnt+'.json',
            {state:state, work:work, inputs:inputs[i]},
            {permissions:[
                xdmp.permission('rest-reader', 'read'),
                xdmp.permission('rest-writer', 'update')
                ],
                collections: 'JsonIngressCollection'});
                uriCnt++;
}
state.next = state.next + arrayLen;
    returnValue = state;
}
// Query in Java client for this URI to verify length
if (!fn.docAvailable('/api-bulk-size.json')) {
xdmp.documentInsert(
       '/api-bulk-size.json',  {state:state, work:work, length:arrayLen},
                                          {permissions:[
                                              xdmp.permission('rest-reader', 'read'),
                                              xdmp.permission('rest-writer', 'update')
                                              ],
                                              collections: 'JsonIngressCollection'});
}
}
returnValue;
