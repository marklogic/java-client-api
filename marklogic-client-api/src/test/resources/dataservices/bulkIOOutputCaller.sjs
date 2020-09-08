'use strict';

const state  = fn.head(xdmp.fromJSON(endpointState));
const work = fn.head(xdmp.fromJSON(endpointConstants));

const res = [];
const d = fn.subsequence(fn.collection(work.collection), state.next, work.limit);

state.next = state.next + work.limit;

if(!fn.empty(d)) {
res.push(state);
for (const x of d) {
res.push(x);
};
}

const returnValue = Sequence.from(res);

returnValue;