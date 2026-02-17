xquery version "1.0-ml";

module namespace transform = "http://marklogic.com/rest-api/transform/toBinary";

(:
Demonstrates how a transform can be used to store JSON or XML as a binary document. This is useful for a use case of
a document with a URI that ends in e.g. ".json" or ".xml" but the user wants to treat the content as binary data so
it is not indexed.
:)
declare function transform($context as map:map, $params as map:map, $content as document-node()) as document-node()
{
    let $node := $content/node()
    let $enc := xdmp:base64-encode(xdmp:quote($node))
    let $bin := xs:hexBinary(xs:base64Binary($enc))
    return document { binary { $bin } }
};
