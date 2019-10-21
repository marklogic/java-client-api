'use strict';
var endpointState; // jsonDocument?
var workUnit;      // jsonDocument?
var input;         // jsonDocument*

const inputCount = fn.count(input);

const state = fn.head(xdmp.fromJSON(endpointState));
state.next = state.next + inputCount;

const work = fn.head(xdmp.fromJSON(workUnit));

// workaround for bug 53438
const inputs =
    (input instanceof Sequence) ? input.toArray().map(item => fn.head(xdmp.fromJSON(item))) :
    (input instanceof Document) ? [fn.head(xdmp.fromJSON(input))] :
                                  [ {UNKNOWN: input} ];

const returnValue = (inputCount > 0) ? Sequence.from([state].concat(inputs)) : null;

returnValue;
