'use strict';
var endpointState; // jsonDocument?
var workUnit;      // jsonDocument?
var input;         // jsonDocument*

const returnValue = Sequence.from([{
    endpointState: endpointState,
    workUnit:      workUnit,
    input:         input
}].concat(input.toArray()));
returnValue;
