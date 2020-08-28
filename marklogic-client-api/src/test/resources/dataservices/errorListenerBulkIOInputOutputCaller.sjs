'use strict';
var endpointState; // jsonDocument?
var workUnit;      // jsonDocument?
var input;         // jsonDocument*

const inputCount = fn.count(input);

const work = fn.head(xdmp.fromJSON(workUnit));

const state = fn.head(xdmp.fromJSON(endpointState));
state.next = state.next + inputCount;

state.workMax = work.max;

const inputs =
    (input instanceof Sequence) ? input.toArray().map(item => fn.head(xdmp.fromJSON(item))) :
    (input instanceof Document) ? [fn.head(xdmp.fromJSON(input))] :
                                  [ {UNKNOWN: input} ];

//console.log(work.collection + ": state.next = " + state.next);
var ranInt = Math.floor(Math.random() * Math.floor(2));
if (ranInt == 1 && work.collection == "bulkInputOutputTest_1") {
    //console.log(work.collection + ': ' + "state.next = " + state.next + ", ranInt = " + ranInt);
    fn.error(xs.QName("ERROR"), "Exception");
}

const returnValue = (inputCount > 0) ? Sequence.from([state].concat(inputs)) : null;
returnValue;
