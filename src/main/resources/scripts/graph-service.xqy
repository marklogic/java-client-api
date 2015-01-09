xquery version "1.0-ml";

(: Copyright 2011-2015 MarkLogic Corporation.  All Rights Reserved. :)

module namespace graph = "http://marklogic.com/rest-api/resource/graph";

(: WARNING: 
    The semantics model library may change in a future release without notice.
    Do not call the functions of the semantics model library in your own code.
    :)
import module namespace semmod = "http://marklogic.com/rest-api/models/semantics"
    at "/MarkLogic/rest-api/models/semantics-model.xqy";

declare namespace search = "http://marklogic.com/appservices/search";

declare namespace rapi = "http://marklogic.com/rest-api";

declare default function namespace "http://www.w3.org/2005/xpath-functions";
declare option xdmp:mapping "false";


declare private function graph:rdf-mime-type(
    $header-name as xs:string,
    $format      as xs:string
) as map:map
{
    let $headers := map:map()
    return (
        map:put($headers, $header-name, graph:format-mime-type($format)),
        $headers
        )
};

declare private function graph:format-mime-type(
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

declare function graph:get(
    $context as map:map,
    $params  as map:map
) as document-node()*
{
    let $mimetype := graph:format-mime-type(map:get($params, "format"))
    let $headers  := map:map()
    return (
        map:put($headers, "accept",       $mimetype),
        map:put($context, "output-types", $mimetype),

        document { 
    	   semmod:graph-read($headers, $params)
            }
        )
};

declare %rapi:transaction-mode("update")
function graph:post(
    $context as map:map,
    $params  as map:map,
    $input   as document-node()*
) as document-node()?
{
    let $input-format := map:get($params, "format")
    let $headers := graph:rdf-mime-type("content-type", $input-format)
    return semmod:graph-insert($headers, $params, $input, ())[false()]
};

declare function graph:put(
    $context as map:map,
    $params  as map:map,
    $input   as document-node()*
) as document-node()?
{
    let $input-format := map:get($params, "format")
    let $headers := graph:rdf-mime-type("content-type", $input-format)
    return semmod:graph-replace($headers, $params, $input, ())[false()]
};

declare function graph:delete(
    $context as map:map,
    $params  as map:map
) as document-node()?
{
    semmod:graph-delete(map:map(), $params, ())[false()]
};

declare private function graph:prepare-params(
    $params as map:map
) as empty-sequence()
{
    let $views      := map:get($params,"view")
    let $start      := map:get($params,"start")
    let $pageLength := map:get($params,"pageLength")
    return (
        if ($views = "results") then ()
        else map:put($params,"view",($views,"results")),

        if (empty($start))
        then map:put($params,"start",xs:unsignedLong(1))
        else map:put($params,"start",xs:unsignedLong($start)),

        if (empty($pageLength))
        then map:put($params,"pageLength",xs:unsignedLong(10))
        else map:put($params,"pageLength",xs:unsignedLong($pageLength))
        )
};


