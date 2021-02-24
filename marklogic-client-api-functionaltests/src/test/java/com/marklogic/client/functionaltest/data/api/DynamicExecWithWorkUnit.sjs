'use strict';
var endpointState; // jsonDocument? 
var workUnit;      // jsonDocument?
declareUpdate();

const w = fn.head(xdmp.fromJSON(endpointConstants));
const endpt = fn.head(xdmp.fromJSON(endpointState));
const thisBatchStart = endpt.start;

const docs = fn.subsequence(fn.collection(w.collectionName), thisBatchStart, w.max);

for (const d of docs) {
	var docURI = fn.baseUri(d);
  
  	for (var x of fn.doc(docURI) ) {
  		// Build an array and insert
  		var n1 = new NodeBuilder();
  		var arrayNode = n1.addNode({"editions": ["2018", "2019"]}).toNode().xpath("./array-node('editions')");
  		xdmp.nodeInsertAfter(x.root.xpath("./state"), arrayNode);
  
  		// Insert a text node from transport
  		var n2 = new NodeBuilder();
    	var descNode = n2.addNode({desc:w.firstString}).toNode();
   		xdmp.nodeInsertAfter(x.root.xpath(w.path), descNode);
   		
   		// Insert a number node from transport
   		var n3 = new NodeBuilder();
    	var priceNode = n3.addNode({ "price": w.amount }).toNode();
    		
        xdmp.nodeInsertBefore(x.root.xpath(w.path), priceNode);
    	console.log("priceNode is ");
console.log(priceNode);
     }
  }