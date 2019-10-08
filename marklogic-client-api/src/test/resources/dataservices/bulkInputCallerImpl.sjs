'use strict';
var endpointState; // jsonDocument?
var workUnit;      // jsonDocument?
declareUpdate();

const state = fn.head(xdmp.fromJSON(endpointState));
state.next = state.next + 1;

const work = fn.head(xdmp.fromJSON(workUnit));

let returnValue = null;
if (state.next < work.max) {
        xdmp.documentInsert(
            '/marklogic/ds/test/bulkInputCaller/' +state.next+'.json',
            state,
            {permissions:[
                xdmp.permission('rest-reader', 'read'),
                xdmp.permission('rest-writer', 'update')
                ]});
    returnValue = state;
}
returnValue;
