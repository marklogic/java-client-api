'use strict';
// This is used to test Open API. Ignore rest of these code.
// declareUpdate(); // Note: uncomment if changing the database state


function OpenApidocOut(docId) {

return cts.doc(docId);
}

var t = OpenApidocOut(xdmp.getRequestField("uri"));
t;
