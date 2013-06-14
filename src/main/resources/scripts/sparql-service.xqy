xquery version "1.0-ml";

(: Copyright 2011-2013 MarkLogic Corporation.  All Rights Reserved. :)

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




