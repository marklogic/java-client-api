'use strict';

// declareUpdate(); // Note: uncomment if changing the database state

var inputFiles; // instance of DocumentNode+
var uris; // instance of xs.string+
var searchItem; // instance of xs.string+

 var content;
 var uri;
 var doc;

// TODO:  produce the ArrayNode+ output from the input variables
inputFiles = xdmp.getRequestField("inputFiles");
uris = xdmp.getRequestField("uris");
searchItem = xdmp.getRequestField("searchItem");

for(let i=0; i<inputFiles.length; i++){
  
  content = {value: inputFiles[i]};
  uri = '/' + uris[i] + '.json';
  

  doc = "declareUpdate(); xdmp.documentInsert(uri, content)";
  
  xdmp.eval(doc, {content: content, uri: uri}, {database: xdmp.database('TestClientAPIOneDB')})
}

var results =  [];
var modRet; 

var query = `for (const hit of cts.search(cts.wordQuery(searchItem))) {
					results.push(fn.documentUri(hit));
				} 
				results;
				`;
modRet = xdmp.eval(query, {searchItem: searchItem, results: results}, {database: xdmp.database('TestClientAPIOneDB')});

console.log("Return from CLIENT API results value is :", results);
console.log("Return modRet from CLIENT API results value is : ", modRet);
modRet;