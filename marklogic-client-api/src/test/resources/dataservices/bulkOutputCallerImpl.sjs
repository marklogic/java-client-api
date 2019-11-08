'use strict';
var endpointState; // jsonDocument?
var workUnit;      // jsonDocument?
var input;         // jsonDocument*


const state  = fn.head(xdmp.fromJSON(endpointState));
const work = fn.head(xdmp.fromJSON(workUnit));

const res = [];
const d = fn.subsequence(fn.collection("bulkOutputTest"), 1);
for (const x of d) {
res.push(x); };

state.next = state.next + 1;
const returnValue = (state.next < work.max) ? Sequence.from(res) : null;

returnValue;
