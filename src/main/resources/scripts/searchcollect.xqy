xquery version "1.0-ml";

(: Copyright 2011-2012 MarkLogic Corporation.  All Rights Reserved. :)

module namespace searchcollect = "http://marklogic.com/rest-api/resource/searchcollect";

(: WARNING: 
    The search model library may change in a future release without notice.
    Do not call the functions of the search model library in your own code.
    :)
import module namespace searchmodq = "http://marklogic.com/rest-api/models/search-model-query"
    at "/MarkLogic/rest-api/models/search-model-query.xqy";

declare namespace search = "http://marklogic.com/appservices/search";

declare default function namespace "http://www.w3.org/2005/xpath-functions";
declare option xdmp:mapping "false";

declare function searchcollect:get(
    $context as map:map,
    $params  as map:map
) as document-node()*
{
    let $headers :=
        let $map := map:map()
        return (
            map:put($map, "accept", "application/xml"),
            $map
            )
    let $results := (
        searchcollect:prepare-params($params),

        if (exists(map:get($params,"q")))
        then searchmodq:search-get($headers, $params)
        else if (
            exists(map:get($params,"value")) and
            (exists(map:get($params,"key")) or exists(map:get($params,"element")))
            )
        then searchmodq:keyvalue-get($headers, $params)
        else error((),"REST-INVALIDPARAM",
            "no query parameters for collecting documents"
            )
        )
    return searchcollect:results($context,$results)
};

declare function searchcollect:post(
    $context as map:map,
    $params  as map:map,
    $input   as document-node()*
) as document-node()*
{
    if (empty($input))
    then error((),"RESTAPI-INVALIDCONTENT","empty input for collecting documents")
    else
        let $headers :=
            let $map := map:map()
            return (
                map:put($map, "accept", "application/xml"),
                $map
                )
        return (
            searchcollect:prepare-params($params),

            searchcollect:results(
                $context,
                searchmodq:search-post($headers,$params,$input)
                )
            )
};

declare private function searchcollect:prepare-params(
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

declare private function searchcollect:results(
    $context  as map:map,
    $response as element(search:response)?
) as document-node()*
{
    if (empty($response)) then ()
    else
        let $results := $response/search:result/@uri/string(.)
        return (
(: TODO: property or content :)
            map:put($context, "output-boundary",
                "document-collect-"||xdmp:random()),
            map:put($context, "output-types",
                ("application/xml", $results ! (
                    let $type := xdmp:uri-content-type(.)
                    return
                        if ($type eq "text/xml")
                        then "application/xml"
                        else $type))),
            document {$response},
            $results ! doc(.)
            )
};
