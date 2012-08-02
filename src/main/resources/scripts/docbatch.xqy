xquery version "1.0-ml";

(: Copyright 2011-2012 MarkLogic Corporation.  All Rights Reserved. :)

module namespace docbatch = "http://marklogic.com/rest-api/resource/docbatch";

(: WARNING: 
    The document model libraries may change in a future release without notice.
    Do not call the functions of the document-model library in your own code.
    :)
import module namespace docmodqry = "http://marklogic.com/rest-api/models/document-model-query"
    at "/MarkLogic/rest-api/models/document-model-query.xqy";

import module namespace docmodupd = "http://marklogic.com/rest-api/models/document-model-update"
    at "/MarkLogic/rest-api/models/document-model-update.xqy";

declare namespace rapi = "http://marklogic.com/rest-api";

declare default function namespace "http://www.w3.org/2005/xpath-functions";
declare option xdmp:mapping "false";

declare function docbatch:post(
    $context as map:map,
    $params  as map:map,
    $input   as document-node()*
) as document-node()*
{
    (: TODO: support JSON manifest including JSON metadata :)
    let $batch-requests :=
        if (exists($input))
        then subsequence($input,1,1)/rapi:batch-requests/*
        else error((),"RESTAPI-INVALIDCONTENT","empty input for batch request")
    let $request-results :=
        if (empty($batch-requests))
        then error((),"RESTAPI-INVALIDCONTENT","no manifest for batch requests")
        else
            let $raw-uris      := $batch-requests/rapi:uri/string(.)
            let $distinct-uris := distinct-values($raw-uris)
            return
                if (not(count($batch-requests) = count($distinct-uris)))
                then error((),"RESTAPI-INVALIDCONTENT","multiple requests for "||
                    string-join(
                        for $uri in $distinct-uris
                        where count($raw-uris[. eq $uri]) gt 1
                        return $uri,
                        ", "
                        )
                    )
                else map:map()
    let $output :=
        let $content-inputs    := subsequence($input,2)
        let $input-count       := count($content-inputs)
        let $content-puts      := $batch-requests /
            self::rapi:put-request[exists(rapi:content-mimetype)]
        let $put-request-count := count($content-puts)
        return (
            if ($put-request-count = $input-count) then ()
            else error((),"RESTAPI-INVALIDCONTENT",
                "mismatch between "||$put-request-count||" PUT requests and "||
                $input-count||" PUT content"
                ),

            for $batch-request in $batch-requests
            let $uri              := $batch-request/rapi:uri/string(.)
            let $content-mimetype := $batch-request/rapi:content-mimetype/string(.)
            let $metadata         := $batch-request/rapi:metadata
            return
                if ($batch-request instance of element(rapi:put-request))
                then docbatch:apply-put(
                    $uri,
                    $metadata,
                    $content-mimetype,
                    if (empty($content-mimetype)) then ()
                    else subsequence(
                            $content-inputs,
                            count($content-puts[. << $batch-request]) + 1,
                            1
                            ),
                    $request-results
                    )
                else if ($batch-request instance of element(rapi:get-request))
                then docbatch:apply-get(
                    $uri,$metadata,$content-mimetype,$request-results
                    )
                else if ($batch-request instance of element(rapi:delete-request))
                then docbatch:apply-delete(
                    $uri,$request-results
                    )
                else error((),"REST-UNSUPPORTEDMETHOD",$batch-request/local-name(.))
            )
    let $batch-response :=
        <rapi:batch-responses>{
            for $batch-request in $batch-requests
            let $uri-elem  := $batch-request/rapi:uri
            let $succeeded := map:get($request-results,$uri-elem/string(.))
            return
                if ($batch-request instance of element(rapi:put-request)) then
                    <rapi:put-response>{
                        $uri-elem,
                        <rapi:request-succeeded>{$succeeded}</rapi:request-succeeded>,
                        if ($succeeded) then ()
                        else <rapi:error-mimetype>application/xml</rapi:error-mimetype>
                    }</rapi:put-response>
                else if ($batch-request instance of element(rapi:get-request)) then
                    <rapi:get-response>{
                        $uri-elem,
                        <rapi:request-succeeded>{$succeeded}</rapi:request-succeeded>,
                        if ($succeeded) then (
                            if (empty($batch-request/rapi:metadata)) then ()
                            else <rapi:metadata-mimetype>application/xml</rapi:metadata-mimetype>,
                            (: TODO: need format :)
                            $batch-request/rapi:content-mimetype
                            )
                        else (
                            <rapi:error-mimetype>application/xml</rapi:error-mimetype>
                            )
                    }</rapi:get-response>
                else if ($batch-request instance of element(rapi:delete-request)) then
                    <rapi:delete-response>{
                        $uri-elem,
                        <rapi:request-succeeded>{$succeeded}</rapi:request-succeeded>,
                        if ($succeeded) then ()
                        else <rapi:error-mimetype>application/xml</rapi:error-mimetype>
                    }</rapi:delete-response>
                else ()  
        }</rapi:batch-responses>
    return (
        map:put($context, "output-boundary", "document-batch-"||xdmp:random()),
        map:put($context, "output-types", (
            "application/xml",
            $batch-response /
                * /
                (rapi:exception-mimetype|rapi:metadata-mimetype|rapi:content-mimetype) /
                string(.)
            )),

        document {$batch-response},
        $output
        )
};

declare private function docbatch:apply-put(
    $uri              as xs:string,
    $metadata         as element(rapi:metadata)?,
    $content-mimetype as xs:string?,
    $content          as document-node()?,
    $request-results  as map:map
) as document-node()?
{
    let $result :=
        for $i in 1 to 2
        return
            if      ($i eq 1 and empty($content-mimetype)) then ()
            else if ($i eq 2 and empty($metadata))         then ()
            else
                let $headers :=
                    let $map := map:map()
                    return (
                        map:put($map, "content-type",
                            if ($i eq 1)
                            then $content-mimetype
                            else "application/xml"
                            ),
                        $map
                        )
                let $params :=
                    let $map := map:map()
                    return (
                        map:put($map, "uri",        $uri),
                        map:put($map, "categories",
                            if ($i eq 1)
                            then "content"
                            else $metadata/*/local-name(.)
                            ),
                        $map
                        )
                let $env := 
                    let $map := map:map()
                    return (
                        map:put($map, "buffer", "true"),
                        map:put($map, "body-getter", function($format as xs:string?) {
                            if ($i eq 1)
                            then $content
                            else document {$metadata}
                            }),
                        $map
                        )
                return
                    try {
                        docmodupd:put($headers,$params,$env)
                    } catch($e) {
                        <rapi:error>{$e}</rapi:error>
                    }
    let $errors := $result/rapi:error
    return
        if (exists($errors)) then (
            map:put($request-results,$uri,false()),
            document {
                <rapi:request-failure>
                    <rapi:uri>{$uri}</rapi:uri>
                    {$errors}
                </rapi:request-failure>
                }
            )
        else map:put($request-results,$uri,true())
};

declare private function docbatch:apply-get(
    $uri              as xs:string,
    $metadata         as element(rapi:metadata)?,
    $content-mimetype as xs:string?,
    $request-results  as map:map
) as document-node()*
{
    let $result :=
        for $i in 1 to 2
        return
            if      ($i eq 1 and empty($metadata))         then ()
            else if ($i eq 2 and empty($content-mimetype)) then ()
            else
                let $headers :=
                    let $map := map:map()
                    return (
                        map:put($map, "accept",
                            if ($i eq 1)
                            then "application/xml"
                            else $content-mimetype
                            ),
                        $map
                        )
                let $params :=
                    let $map := map:map()
                    return (
                        map:put($map, "uri",        $uri),
                        map:put($map, "category",
                            if ($i eq 1)
                            then $metadata/*/local-name(.)
                            else "content"
                            ),
                        $map
                        )
                return
                    try {
                        if ($i eq 1)
(: TODO: document {} should be unneeded :)
                        then document {docmodqry:get($headers,$params,())}
                        else docmodqry:get($headers,$params,())
                    } catch($e) {
                        <rapi:error>{$e}</rapi:error>
                    }
    let $errors := $result/rapi:error
    return
        if (exists($errors)) then (
            map:put($request-results,$uri,false()),
            <rapi:request-failure>
                <rapi:uri>{$uri}</rapi:uri>
                {$errors}
            </rapi:request-failure>
            )
        else (
            map:put($request-results,$uri,true()),
            $result
            )
};

declare private function docbatch:apply-delete(
    $uri             as xs:string,
    $request-results as map:map
) as document-node()?
{
    let $result :=
        let $headers := map:map()
        let $params :=
            let $map := map:map()
            return (
                map:put($map, "uri", $uri),
                $map
                )
        return
            try {
                docmodupd:delete($headers,$params,())
            } catch($e) {
                <rapi:error>{$e}</rapi:error>
            }
    let $errors := $result/rapi:error
    return
        if (exists($errors)) then (
            map:put($request-results,$uri,false()),
            <rapi:request-failure>
                <rapi:uri>{$uri}</rapi:uri>
                {$errors}
            </rapi:request-failure>
            )
        else map:put($request-results,$uri,true())
};
