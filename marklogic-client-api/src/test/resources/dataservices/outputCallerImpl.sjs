'use strict';
var endpointState; // jsonDocument?
var workUnit;      // jsonDocument?

const returnValue = Sequence.from([{
    endpointState: endpointState,
    workUnit:      workUnit
}, {docNum:1, docName:"alpha"}, {docNum:2, docName:"beta"}]);
returnValue;
