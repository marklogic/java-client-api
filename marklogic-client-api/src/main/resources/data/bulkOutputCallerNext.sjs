'use strict';

const state  = fn.head(xdmp.fromJSON(endpointState));
const work = fn.head(xdmp.fromJSON(workUnit));
const limit = work.limit;
const forestName = work.forestName;

const res = [];
const query = cts.collectionQuery("bulkOutputTest");
const docs = cts.search(query,["unfiltered","score-zero"],null,xdmp.forest(forestName));
const d = fn.subsequence(docs, state.next, limit);

res.push(state);
for (const x of d) {
res.push(x); };
state.next = state.next + limit;

const returnValue = Sequence.from(res);

returnValue;
