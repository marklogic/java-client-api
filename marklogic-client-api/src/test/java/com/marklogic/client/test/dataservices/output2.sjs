'use strict';

/* TODO:
    example of per-forest search
    work unit with forest name, query, batch size, and optional max batch count
    endpoint state with uri of last result in page and batch count
    comment that session could cache the id of the registered query
  http://svn.marklogic.com/trac.engsvn/browser/xdmp/branches/b10_0/src/Modules/MarkLogic/rest-api/models/internal-uris-model.xqy
 */

var endpointState;
var workUnit;

// convert the JSON to JavaScript objects
const work  = fn.head(xdmp.fromJSON(workUnit));
const state = fn.head(xdmp.fromJSON(endpointState));

// required constant for the forest name scope
const forestName = work.forestName;

// optional constant for the query scope
const workQuery = work.query;

// the number of results in each batch defaulting to 100
const batchSize = fn.exists(work.batchSize) ? work.batchSize : 100;

// the maximum number of batches defaulting to the largest integer
const maxBatches = fn.exists(work.maxBatches) ? work.maxBatches : Number.MAX_SAFE_INTEGER;

// get the last URI in the previous batch defaulting to null
const lastUri = state.lastUri;

// get the number of the new batch defaulting to 0
const batchNum = fn.exists(state.batchNum) ? state.batchNum : 0;

let returnValue = null;
if (batchNum < maxBatches) {
  // precompute the constant query terms (if any)
  const registeredQuery = fn.exists(workQuery) ?
      cts.registeredQuery(cts.register(workQuery), 'unfiltered') : null;

  // uris after the last uri in the prior batch
  const uriQuery = fn.exists(lastUri) ?
      cts.rangeQuery(cts.uriReference(), '>', lastUri) : null;

  // combine the possible queries
  let batchQuery = null;
  if (fn.exists(uriQuery) && fn.exists(registeredQuery)) {
    batchQuery = cts.andQuery([registeredQuery, uriQuery]);
  } else if (fn.exists(registeredQuery)) {
    batchQuery = registeredQuery;
  } else if (fn.exists(lastUri)) {
    batchQuery = uriQuery;
  } else {
    batchQuery = cts.rangeQuery(cts.uriReference());
  }

  // get the result documentss for the queries
  const resultDocs = fn.subsequence(
      cts.search(
          batchQuery,
          ['score-zero', 'unfaceted', 'unfiltered', cts.indexOrder(cts.uriReference(), 'ascending')],
          0,
          xdmp.forest(forestName)
      ),
      1,
      batchSize
  );

  // the queries yielded a batch
  if (fn.exists(resultDocs)) {
    const batch = resultDocs.toArray();

    // set the last URI and batch number for the next request
    state.lastUri  = batch[batch.length];
    state.batchNum = batchNum + 1;

    // prepend the endpoint state to the batch
    const output = [state].concat(batch);

    // convert to a Sequence
    returnValue = Sequence.from(output);
  }
}
returnValue;
