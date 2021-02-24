'use strict';
var endpointState; // jsonDocument?
    
var workUnit;      // jsonDocument?
var input;         // binaryDocument*
declareUpdate();

const work = fn.head(xdmp.fromJSON(endpointConstants));
const state = fn.head(xdmp.fromJSON(endpointState));
state.next = state.next + 1;

const inputs =
    (input instanceof Sequence) ? input.toArray().map(item => fn.head(item)) :
    (input instanceof Document) ? input :
                                   {UNKNOWN: input} ;
let returnValue = null;
if (state.next < work.max) {
if (Array.isArray(inputs)) {
var i;
for (i=0;i<inputs.length;i++){
    xdmp.documentInsert(
            '/marklogic/ds/test/bulkInputCaller/' +state.next+'.txt',
            inputs[i],
            {permissions:[
                xdmp.permission('rest-reader', 'read'),
                xdmp.permission('rest-writer', 'update')
                ]});
	state.next = state.next + 1;
}
}
else
{
        xdmp.documentInsert(
            '/marklogic/ds/test/bulkInputCaller/' +state.next+'.txt',
            inputs,
            {permissions:[
                xdmp.permission('rest-reader', 'read'),
                xdmp.permission('rest-writer', 'update')
                ]});
}
    returnValue = state;
}
returnValue;
