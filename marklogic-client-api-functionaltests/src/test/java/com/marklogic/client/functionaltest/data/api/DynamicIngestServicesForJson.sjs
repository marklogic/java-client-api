'use strict';
var endpointState;      // jsonDocument?
var endpointConstants;  // jsonDocument?
var input;              // jsonDocument*
declareUpdate();

var arrayLen =   input.toArray().length;

const work = fn.head(xdmp.fromJSON(endpointConstants));
const state = fn.head(xdmp.fromJSON(endpointState));

var i = 0;

state.next = state.next + 1;
const inputs =
    (input instanceof Sequence) ? input.toArray().map(item => fn.head(xdmp.fromJSON(item))) :
    (input instanceof Document) ? [fn.head(xdmp.fromJSON(input))] :
                                  [ {UNKNOWN: input} ];

state.returnIndex = state.returnIndex + 1;

var uriCnt = state.next;
let returnValue = null;
if (inputs != null) {
if (state.next < work.max) {

for (var i=0; i<arrayLen; i++) {
// To Make sure that $bulk.inputBatchSize number of docs are inserted. Check on Java client.
console.log("Inserting : " + String(inputs[i]));
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
state.next = state.next + arrayLen -1;
    returnValue = state;
}
// Query in Java client for this URI to verify length
if (!fn.docAvailable('/api-default-bulk-size.json')) {
xdmp.documentInsert(
       '/api-default-bulk-size.json',  {state:state, work:work, length:arrayLen},
                                          {permissions:[
                                              xdmp.permission('rest-reader', 'read'),
                                              xdmp.permission('rest-writer', 'update')
                                              ],
                                              collections: 'Summary1'});
}
}
returnValue;
