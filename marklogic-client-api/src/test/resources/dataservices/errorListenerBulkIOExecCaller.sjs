'use strict';
var endpointState; // jsonDocument?
var endpointConstants;      // jsonDocument?
declareUpdate();


const work = fn.head(xdmp.fromJSON(endpointConstants));

const state = fn.head(xdmp.fromJSON(endpointState));
state.next = state.next + 1;

state.workMax = work.max;

let returnValue = null;
if (state.next <= work.max) {
    returnValue = state;
    xdmp.documentInsert(
        '/marklogic/ds/test/errorListenerbulkIOExecCaller' + work.collection + '.json',
        {state:state, work:work},
        {permissions:[
            xdmp.permission('rest-reader', 'read'),
            xdmp.permission('rest-writer', 'update')
            ]});
}

//console.log(work.collection + ": state.next = " + state.next);

var ranInt = Math.floor(Math.random() * Math.floor(2));
if (ranInt == 1 && work.collection == "bulkExecTest_1" && state.next > 10) {
    //console.log(work.collection + ': ' + "state.next = " + state.next + ", ranInt = " + ranInt);
    fn.error(xs.QName("ERROR"), "Exception");
}

returnValue;
