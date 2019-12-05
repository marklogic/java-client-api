'use strict';

const state  = fn.head(xdmp.fromJSON(endpointState));
const work = fn.head(xdmp.fromJSON(workUnit));

const res = [];
const d = fn.subsequence(fn.collection("bulkOutputCallerNext"), state.next, work.limit);
res.push(state);
for (const x of d) {
res.push(x); };
state.next = state.next + work.limit;

const returnValue = Sequence.from(res);

returnValue;
