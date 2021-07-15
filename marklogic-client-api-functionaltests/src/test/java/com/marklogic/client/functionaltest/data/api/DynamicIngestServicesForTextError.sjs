'use strict';
var endpointState; // jsonDocument?

var workUnit;      // jsonDocument?
var input;         // binaryDocument*
declareUpdate();

const work = fn.head(xdmp.fromJSON(endpointConstants));
const state = fn.head(xdmp.fromJSON(endpointState));
state.next = state.next + 1;

const inputs =
    (input instanceof Sequence) ? input.toArray().map(item => fn.head(item)) :
    (input instanceof Document) ? input :
                                   {UNKNOWN: input} ;
let returnValue = null;
if (state.next < work.max) {
if (Array.isArray(inputs)) {
var i;
fn.error(xs.QName("XDMP-INVALDATE"), "QA Generated doc write error");
}
else
{
  fn.error(xs.QName("XDMP-INVALDATE"), "QA Generated doc write error");
}
    returnValue = state;
}
returnValue;
