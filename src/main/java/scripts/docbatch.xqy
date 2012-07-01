xquery version "1.0-ml";

(: Copyright 2011-2012 MarkLogic Corporation.  All Rights Reserved. :)

module namespace docbatch = "http://marklogic.com/rest-api/resource/docbatch";

(: WARNING: 
    The document-model library may change in a future release without notice.
    Do not call the functions of the document-model library in your own code.
    :)
import module namespace docmod = "http://marklogic.com/rest-api/models/document-model"
    at "/MarkLogic/rest-api/models/document-model.xqy";

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
            let $result    := map:get($request-results,$uri-elem/string(.))
            let $succeeded := subsequence($result,1,1)
            return
                if ($batch-request instance of element(rapi:put-request)) then
                    <rapi:put-response>{
                        $uri-elem,
                        <rapi:request-succeeded>{$succeeded}</rapi:request-succeeded>,
                        if ($succeeded) then ()
                        else subsequence($result,2,1)
                    }</rapi:put-response>
                else if ($batch-request instance of element(rapi:get-request)) then
                    <rapi:get-response>{
                        $uri-elem,
                        <rapi:request-succeeded>{$succeeded}</rapi:request-succeeded>,
                        if ($succeeded) then (
                            subsequence($result,2,1),
                            (: TODO: need format :)
                            $batch-request/rapi:content-mimetype
                            )
                        else subsequence($result,2,1)
                    }</rapi:get-response>
                else if ($batch-request instance of element(rapi:delete-request)) then
                    <rapi:delete-response>{
                        $uri-elem,
                        <rapi:request-succeeded>{$succeeded}</rapi:request-succeeded>,
                        if ($succeeded) then ()
                        else subsequence($result,2,1)
                    }</rapi:delete-response>
                else ()  
        }</rapi:batch-responses>
    return (
        map:put($context, "output-boundary", "document-batch-"||xdmp:random()),
        map:put($context, "output-types", (
            "application/xml",
            $batch-response/rapi:get-response/rapi:content-mimetype/string(.)
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
                        docmod:put($headers,$params,$env)
                    } catch($e) {
                        <rapi:error>{$e}</rapi:error>
                    }
    let $errors := $result/rapi:error
    return
        if (exists($errors))
        then map:put($request-results,$uri,(false(),
                <rapi:request-failure>
                    <rapi:uri>{$uri}</rapi:uri>
                    {$errors}
                </rapi:request-failure>
                ))
        else map:put($request-results,$uri,true())
};

declare private function docbatch:apply-get(
    $uri              as xs:string,
    $metadata         as element(rapi:metadata)?,
    $content-mimetype as xs:string?,
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
                        map:put($map, "accept",
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
                        map:put($map, "category",
                            if ($i eq 1)
                            then "content"
                            else $metadata/*/local-name(.)
                            ),
                        $map
                        )
                return
                    try {
                        docmod:get($headers,$params,())
                    } catch($e) {
                        <rapi:error>{$e}</rapi:error>
                    }
    let $errors := $result/rapi:error
    return
        if (exists($errors))
        then map:put($request-results,$uri,(false(),
                <rapi:request-failure>
                    <rapi:uri>{$uri}</rapi:uri>
                    {$errors}
                </rapi:request-failure>
                ))
        else
            let $content-result  :=
                if (empty($content-mimetype)) then ()
                else $result[1]
            let $metadata-result :=
                if (empty($metadata)) then ()
                else if (count($result) eq 1)
                then $result[1]
                else $result[2]
            return (
                map:put($request-results,$uri,(true(),$metadata-result)),
                $content-result
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
                docmod:delete($headers,$params,())
            } catch($e) {
                <rapi:error>{$e}</rapi:error>
            }
    let $errors := $result/rapi:error
    return
        if (exists($errors))
        then map:put($request-results,$uri,(false(),
                <rapi:request-failure>
                    <rapi:uri>{$uri}</rapi:uri>
                    {$errors}
                </rapi:request-failure>
                ))
        else map:put($request-results,$uri,true())
};
