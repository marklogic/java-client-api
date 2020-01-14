'use strict';
var endpointState; // jsonDocument?
var workUnit;      // jsonDocument?
declareUpdate();

const callCounter = fn.head(xdmp.getSessionField('counter', 0));
xdmp.setSessionField('counter', callCounter + 1);

const work = fn.head(xdmp.fromJSON(workUnit));

const state = fn.head(xdmp.fromJSON(endpointState));
state.next = state.next + 1;
state.sessionCounter = callCounter;
state.workMax = work.max;

let returnValue = null;
if (state.next < work.max) {
    returnValue = state;
} else {
    xdmp.documentInsert(
        '/marklogic/ds/test/bulkExecCallerFinalState.json',
        state,
        {permissions:[
            xdmp.permission('rest-reader', 'read'),
            xdmp.permission('rest-writer', 'update')
            ]});
}
returnValue;
