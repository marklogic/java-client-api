xquery version "1.0-ml";

(: Copyright 2011-2015 MarkLogic Corporation.  All Rights Reserved. :)

module namespace sparql = "http://marklogic.com/rest-api/resource/sparql";

(: WARNING: 
    The semantics model library may change in a future release without notice.
    Do not call the functions of the semantics model library in your own code.
    :)
import module namespace semmod = "http://marklogic.com/rest-api/models/semantics"
    at "/MarkLogic/rest-api/models/semantics-model.xqy";

declare namespace search = "http://marklogic.com/appservices/search";

declare default function namespace "http://www.w3.org/2005/xpath-functions";
declare option xdmp:mapping "false";

declare private function sparql:rdf-mime-type(
    $header-name as xs:string,
    $format      as xs:string
) as map:map
{
    let $headers := map:map()
    return (
        map:put($headers, $header-name, sparql:format-mime-type($format)),
        $headers
        )
};

declare private function sparql:format-mime-type(
    $format as xs:string
) as xs:string
{
    switch ($format)
    case "nquad"     return "application/n-quads"
    case "html"      return "text/html"
    case "rdfjson"   return "application/rdf+json"
    case "turtle"    return "text/turtle"
    case "rdfxml"    return "application/rdf+xml"
    case "ntriple"   return "application/n-triples"
    case "triplexml" return "application/vnd.marklogic.triples+xml"
    case "n3"        return "text/n3"
    default          return "application/n-quads"
};

declare function sparql:post(
    $context as map:map,
    $params  as map:map,
    $input   as document-node()*
) as document-node()?
{
    let $format := map:get($params, "format")
    let $default-graph-uri := map:get($params, "default-graph-uri")
    let $nameed-graph-uri := map:get($params, "named-graph-uri")
    let $headers := sparql:rdf-mime-type("accept", $format)
    let $mime := map:put($context, "output-types", map:get($headers, "accept"))
    let $results := semmod:sparql-query($headers, $params, $input)
    return
    document {
        semmod:results-payload($headers,$params,$results)
    }
};




