xquery version "1.0-ml";

(: Copyright 2011-2013 MarkLogic Corporation.  All Rights Reserved. :)

module namespace graph = "http://marklogic.com/rest-api/resource/graph";

(: WARNING: 
    The semantics model library may change in a future release without notice.
    Do not call the functions of the semantics model library in your own code.
    :)
import module namespace semmod = "http://marklogic.com/rest-api/models/semantics"
    at "/MarkLogic/rest-api/models/semantics-model.xqy";

declare namespace search = "http://marklogic.com/appservices/search";

declare default function namespace "http://www.w3.org/2005/xpath-functions";
declare option xdmp:mapping "false";


declare private function graph:rdf-mime-type(
    $header-name as xs:string,
    $format as xs:string
) as map:map
{
    let $headers := map:map()
    let $_ := 
        if ($format eq "nquad")
        then map:put($headers, "accept", "text/nquad")
        else if ($format eq "html")
             then map:put($headers, "accept", "text/html")
        else if ($format eq "json")
            then map:put($headers, "accept", "application/json")
        else if ($format eq "turtle")
             then map:put($headers, "accept", "text/turtle")
        else if ($format eq "xml")
             then map:put($headers, "accept", "application/rdf+xml")
        else if ($format eq "ntriples")
             then map:put($headers, "accept", "text/plain")
        else map:put($headers, "accept", "text/plain")
    return $headers
};

declare function graph:get(
    $context as map:map,
    $params  as map:map
) as document-node()*
{
    let $output-format := map:get($params, "output-format")
    let $headers := graph:rdf-mime-type("accept", $output-format)
    let $mime := map:put($context, "output-types", $output-format)
    return document { 
    	semmod:graph-read($headers, $params)
    }
};

declare function graph:post(
    $context as map:map,
    $params  as map:map,
    $input   as document-node()*
) as document-node()?
{
    let $input-format := map:get($params, "input-format")
    let $headers := graph:rdf-mime-type("content-type", $input-format)
    let $insert := semmod:graph-insert($headers, $params, $input)
    let $mime := map:put($context, "output-types", "text/plain")
    return document { text { "OK" } }   (: hack to prevent java from not liking status code :)
};

declare function graph:put(
    $context as map:map,
    $params  as map:map,
    $input   as document-node()*
) as document-node()?
{
    let $input-format := map:get($params, "input-format")
    let $headers := graph:rdf-mime-type("content-type", $input-format)
    let $insert := semmod:graph-replace($headers, $params, $input)
    let $mime := map:put($context, "output-types", "text/plain")
    return document { text { "OK" } }   (: hack to prevent java from not liking status code :)
};

declare function graph:delete(
    $context as map:map,
    $params  as map:map
) as document-node()?
{
    let $insert := semmod:graph-delete(map:map(), $params)
    return ()
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


