'use strict';
var endpointState; // jsonDocument?
var workUnit;      // jsonDocument?
var input;         // jsonDocument*
declareUpdate();

const state  = fn.head(xdmp.fromJSON(endpointState));
state.next = state.next + 1;

const work = fn.head(xdmp.fromJSON(workUnit));

const inputs =
    (input instanceof Sequence) ? input.toArray().map(item => fn.head(xdmp.fromJSON(item))) :
    (input instanceof Document) ? [fn.head(xdmp.fromJSON(input))] :
                                  [ {UNKNOWN: input} ];

xdmp.documentInsert(
    '/marklogic/ds/test/bulkInputCaller/'+state.next+'.json',
    {state:state, work:work, inputs:inputs},
    {permissions:[
            xdmp.permission('rest-reader', 'read'),
            xdmp.permission('rest-writer', 'update')
        ]})

const returnValue = (state.next < work.max && fn.count(input) > 0) ? state : null;

returnValue;
