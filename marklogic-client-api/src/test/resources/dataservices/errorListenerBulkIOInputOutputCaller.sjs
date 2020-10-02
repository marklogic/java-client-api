'use strict';
var endpointState; // jsonDocument?
var endpointConstants;      // jsonDocument?
var input;         // jsonDocument*

const inputCount = fn.count(input);

const work = fn.head(xdmp.fromJSON(endpointConstants));
const state = fn.head(xdmp.fromJSON(endpointState));

const errorMin = work.errorMin;
const errorMax = work.errorMax;
const inErrorRange = (0 < errorMin && 0 < errorMax && errorMin <= state.next && state.next < errorMax);

state.next = state.next + inputCount;

const inputs =
    (input instanceof Sequence) ? input.toArray().map(item => fn.head(xdmp.fromJSON(item))) :
    (input instanceof Document) ? [fn.head(xdmp.fromJSON(input))] :
                                  [ {UNKNOWN: input} ];

//console.log(work.collection + ": state.next = " + state.next);
var ranInt = inErrorRange ? 1 : Math.floor(Math.random() * Math.floor(2));
if (ranInt == 1 && work.collection == "bulkInputOutputTest_1") {
    //console.log(work.collection + ': ' + "state.next = " + state.next + ", ranInt = " + ranInt);
    fn.error(xs.QName("ERROR"), "Exception");
}

const returnValue = (inputCount > 0) ? Sequence.from([state].concat(inputs)) : null;
returnValue;
