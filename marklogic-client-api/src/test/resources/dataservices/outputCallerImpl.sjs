'use strict';
var endpointState; // jsonDocument?
var endpointConstants;      // jsonDocument?

const returnValue = Sequence.from([{
    endpointState: endpointState,
    endpointConstants:      endpointConstants
}, {docNum:1, docName:"alpha"}, {docNum:2, docName:"beta"}]);
returnValue;
