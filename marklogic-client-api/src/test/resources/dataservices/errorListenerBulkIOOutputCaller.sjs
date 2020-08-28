'use strict';

const state  = fn.head(xdmp.fromJSON(endpointState));
const work = fn.head(xdmp.fromJSON(workUnit));

const res = [];
const d = fn.subsequence(fn.collection(work.collection), state.next, work.limit);

state.next = state.next + work.limit;

if(!fn.empty(d)) {
    res.push(state);
    for (const x of d) {
        res.push(x);
    };
}

//console.log(work.collection + ': ' + "state.next = " + state.next);
var ranInt = Math.floor(Math.random() * Math.floor(2));
if (ranInt == 1 && work.collection == "bulkOutputTest_1") {
    //console.log(work.collection + ': ' + "state.next = " + state.next + ", ranInt = " + ranInt);
    fn.error(xs.QName("ERROR"), "Exception");
}

const returnValue = Sequence.from(res);

returnValue;