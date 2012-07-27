xquery version "1.0-ml";

(: Copyright 2011-2012 MarkLogic Corporation.  All Rights Reserved. :)

module namespace dictionary = "http://marklogic.com/rest-api/resource/dictionary";

import module namespace spell = "http://marklogic.com/xdmp/spell" 
          at "/MarkLogic/spell.xqy";

declare default function namespace "http://www.w3.org/2005/xpath-functions";
declare option xdmp:mapping "false";

declare function dictionary:get(
    $context as map:map,
    $params  as map:map
) as document-node()*
{
    let $service := map:get($params,"service")
    let $uris    := map:get($params,"uris")
    let $word    := map:get($params,"word")
    return
        if (empty($service))
        then error((),"REST-REQUIREDPARAM",
            "the service parameter is required for spelling services"
            )
        else if (empty($uris))
        then error((),"REST-REQUIREDPARAM",
            "the uris parameter is required for spelling services"
            )
        else
            switch ($service)
            case "check-dictionary" return (
                map:put($context, "output-types", $uris ! "application/xml"),
                $uris ! doc(.)/document {
                    try {
                            validate as spell:dictionary {*}
                        } catch ($e) {
                            <invalid>{$e}</invalid>
                        }
                    }
                )
            case "is-correct" return
                let $word := map:get($params,"word")
                return
                    if (empty($word))
                    then error((),"REST-REQUIREDPARAM",
                        "the word parameter is required to check spelling correctness"
                        )
                    else (
                        map:put($context, "output-types", "application/xml"),
                        document {
                            <spell-check>
                                <dictionaries>{string-join($uris,", ")}</dictionaries>
                                <word>{$word}</word>
                                <correct>{spell:is-correct($uris, $word)}</correct>
                            </spell-check>
                            }
                        )
            case "suggest-detailed" return
                let $word               := map:get($params,"word")
                let $maximum            := map:get($params,"maximum")
                let $distance-threshold := map:get($params,"distance-threshold")
                let $options            :=
                    if (empty($maximum) and empty($distance-threshold)) then ()
                    else
                        <spell:options>{
                            if (empty($maximum)) then ()
                            else
                                <spell:maximum>{
                                    $maximum
                                }</spell:maximum>,

                            if (empty($distance-threshold)) then ()
                            else
                                <spell:distance-threshold>{
                                    $distance-threshold
                                }</spell:distance-threshold>
                        }</spell:options>
                return
                    if (empty($word))
                    then error((),"REST-REQUIREDPARAM",
                        "the word parameter is required for spelling suggestions"
                        )
                    else (
                        map:put($context, "output-types", "application/xml"),
                        document {
                            <spell:suggestions>{
                                spell:suggest-detailed($uris, $word, $options)
                            }</spell:suggestions>
                            }
                        )
            default return
                error((),"REST-INVALIDPARAM",
                    "unsupported spelling service: "||$service
                    )
};
