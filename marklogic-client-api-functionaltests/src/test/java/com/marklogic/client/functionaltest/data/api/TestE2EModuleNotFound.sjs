'use strict';

// declareUpdate(); // Note: uncomment if changing the database state

var inputFiles; // instance of DocumentNode+
var uris; // instance of xs.string+
var searchItem; // instance of xs.string+

// TODO:  produce the ArrayNode+ output from the input variables

for(let i=0; i<inputFiles.length; i++){
  const content = {value: inputFiles[i]}
  const uri = '/' + uris[i] + '.json'
  const doc = "declareUpdate(); xdmp.documentInsert(uri, content)"
  xdmp.eval(doc, {body: content, uri: uri}, {database: xdmp.database('TestClientAPIOneDB')})
}

const results = [];


for (const hit of cts.search(cts.wordQuery('Audi'))) {
  results.push(fn.documentUri(hit));
}

results;