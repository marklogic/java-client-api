'use strict';

var endpointState;          // jsonDocument?
var endpointConstants;      // jsonDocument?
declareUpdate();

const w = fn.head(xdmp.fromJSON(endpointConstants));
const endpt = fn.head(xdmp.fromJSON(endpointState));

const thisBatchStart = endpt.returnIndex;

endpt.returnIndex = endpt.returnIndex + w.max - 1;
fn.error(xs.QName("XDMP-INVALDATE"), "greater than 50");
const res = [];

if (endpt.returnIndex <= w.max) {
console.log("Yes");
//const res = [];

// We need endpointstate to go back to client as the first in the return.
res.push(endpt);
}

let ret = Sequence.from(res)
console.log(ret);
ret;