'use strict';
var endpointState;      // jsonDocument?
var endpointConstants;  // jsonDocument?
var input;              // anyDocument*
declareUpdate();

var arrayLen =   input.toArray().length;

const work = fn.head(xdmp.fromJSON(endpointConstants));
const state = fn.head(xdmp.fromJSON(endpointState));

var i = 0;

state.next = state.next + 1;

const inputs =
    (input instanceof Sequence) ? input.toArray().map(item => fn.head(item)) :
    (input instanceof Document) ? input :
                                   {UNKNOWN: input} ;

state.returnIndex = state.returnIndex + 1;

var uriCnt = state.next;
let returnValue = null;
if (inputs != null) {
if (state.next < work.max) {

for (var i=0; i<arrayLen; i++) {
var docCont;
var binFlag = false;

// To Make sure that $bulk.inputBatchSize number of docs are inserted. Check on Java client.

//"Current documentFormat is : " + inputs[i].documentFormat;  <= For all it is BINARY
  try {
     docCont = String(inputs[i]);
    } catch (error) {
    // This should be binary file.
    console.log("++++++++++++++++++++ CAUGHT AN EXCEPTION +++++++++++++" + i );
    binFlag = true;
  }
  if (binFlag == false) {
  if (docCont.startsWith("{") || docCont.startsWith("[")) {
        console.log("Inserting : JSON " + i );
        console.log("Inserting : " + docCont);
        xdmp.documentInsert(
            '/marklogic/ds/test/anyDocument/' +uriCnt+'.json',
            inputs[i],
            {permissions:[
                xdmp.permission('rest-reader', 'read'),
                xdmp.permission('rest-writer', 'update')
                ],
            collections: 'AnyDocumentJSONCollection'});
        uriCnt++;
   } else if (docCont.startsWith("<")) {
        console.log("Inserting : XML " + i );
        console.log("Inserting : " + docCont);
        xdmp.documentInsert(
              '/marklogic/ds/test/anyDocument/' +uriCnt+'.xml',
              inputs[i],
              {permissions:[
                  xdmp.permission('rest-reader', 'read'),
                  xdmp.permission('rest-writer', 'update')
                  ],
              collections: 'AnyDocumentXMLCollection'});
        uriCnt++;
   } else if (typeof docCont === 'string') {
        console.log("Inserting : TXT " + i );
        console.log("Inserting : " + docCont);
        if (docCont.includes("digitalCopies") return fn.error("Invalid docs received");
        xdmp.documentInsert(
               '/marklogic/ds/test/anyDocument/' +uriCnt+'.txt',
               inputs[i],
               {permissions:[
                   xdmp.permission('rest-reader', 'read'),
                   xdmp.permission('rest-writer', 'update')
                   ],
               collections: 'AnyDocumentTEXTCollection'});
        uriCnt++;
   }
   } else if (binFlag == true) {
   console.log("Inserting : BINARY " + i );
   xdmp.documentInsert(
               '/marklogic/ds/test/anyDocument/' +uriCnt+'.jpg',
               inputs[i],
               {permissions:[
                    xdmp.permission('rest-reader', 'read'),
                    xdmp.permission('rest-writer', 'update')
                    ],
               collections: 'AnyDocumentBINARYCollection'});
   uriCnt++;
   binFlag = false;
   }
  }
  state.next = state.next + arrayLen -1;
  returnValue = state;
}
// Query in Java client for this URI to verify length
if (!fn.docAvailable('/api-default-anyDocument-size.json')) {
    xdmp.documentInsert(
       '/api-default-anyDocument-size.json',
       {state:state, work:work, length:arrayLen},
       {permissions:[
                      xdmp.permission('rest-reader', 'read'),
                      xdmp.permission('rest-writer', 'update')
                    ],
       collections: 'SummaryAnyDocuments'});
 }
}
returnValue;
