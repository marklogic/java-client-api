'use strict';
var endpointState; // jsonDocument?
var endpointConstants;      // jsonDocument?
var input;         // jsonDocument*

const returnValue = Sequence.from([{
    endpointState: endpointState,
    endpointConstants:      endpointConstants,
    input:         input
}].concat(input.toArray()));
returnValue;
