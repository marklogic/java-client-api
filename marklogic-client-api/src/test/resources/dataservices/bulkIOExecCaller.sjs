'use strict';
var endpointState; // jsonDocument?
var workUnit;      // jsonDocument?
declareUpdate();


const work = fn.head(xdmp.fromJSON(workUnit));

const state = fn.head(xdmp.fromJSON(endpointState));
state.next = state.next + 1;

state.workMax = work.max;

let returnValue = null;
if (state.next < work.max) {
    returnValue = state;
} else {
    xdmp.documentInsert(
        '/marklogic/ds/test/bulkIOExecCaller.json',
        {state:state, work:work},
        {permissions:[
            xdmp.permission('rest-reader', 'read'),
            xdmp.permission('rest-writer', 'update')
            ]});
}

returnValue;
