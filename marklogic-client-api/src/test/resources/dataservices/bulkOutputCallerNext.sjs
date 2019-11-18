'use strict';

const state  = fn.head(xdmp.fromJSON(endpointState));
const work = fn.head(xdmp.fromJSON(workUnit));
const limit = fn.head(xdmp.fromJSON(workUnit));

const res = [];
const d = fn.subsequence(fn.collection("bulkOutputCallerNext"), state.next, limit.max);
res.push(state);
for (const x of d) {
res.push(x); };
state.next = state.next + work.max;
limit.max = limit.max + work.max;

const returnValue = Sequence.from(res);

returnValue;
