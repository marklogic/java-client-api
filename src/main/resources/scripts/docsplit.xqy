xquery version "1.0-ml";

(: Copyright 2011-2012 MarkLogic Corporation.  All Rights Reserved. :)

module namespace docsplit = "http://marklogic.com/rest-api/resource/docsplit";

(: WARNING: 
    The document model library may change in a future release without notice.
    Do not call the functions of the document-model library in your own code.
    :)
import module namespace docmodupd = "http://marklogic.com/rest-api/models/document-model-update"
    at "/MarkLogic/rest-api/models/document-model-update.xqy";

declare namespace rapi = "http://marklogic.com/rest-api";

declare default function namespace "http://www.w3.org/2005/xpath-functions";
declare option xdmp:mapping "false";

declare function docsplit:post(
    $context as map:map,
    $params  as map:map,
    $input   as document-node()*
) as document-node()*
{
    if (not(count($input) eq 1))
    then error((),"RESTAPI-INVALIDCONTENT","can split exactly one document")
    else if (empty($input/element()))
    then error((),"RESTAPI-INVALIDCONTENT","can split an XML document")
    else
        let $roots := $input//*[exists(@rapi:uri) and not(string(@rapi:uri) eq "")]
        return
            if (empty($roots))
            then error((),"RESTAPI-INVALIDCONTENT","no rapi:uri attributes to split")
            else
                let $content-mimetype := map:get($context, "input-types")
                let $errors := $roots/docsplit:apply-put(.,$content-mimetype)
                return (
                    map:put($context,"output-types","application/xml"),

                    document {
                        if (empty($errors))
                        then <rapi:split-docs>{count($roots)}</rapi:split-docs>
                        else <rapi:split-errors>{$errors}</rapi:split-errors>
                    })
};

declare private function docsplit:apply-put(
    $root             as element(),
    $content-mimetype as xs:string
) as element(rapi:request-failure)?
{
    let $uri-att := $root/@rapi:uri
    let $uri     := $uri-att/string(.)
    let $headers :=
        let $map := map:map()
        return (
            map:put($map, "content-type",$content-mimetype),
            $map
            )
    let $params :=
        let $map := map:map()
        return (
            map:put($map, "uri",        $uri),
            map:put($map, "categories", "content"),
            $map
            )
    let $env := 
        let $map := map:map()
        return (
            map:put($map, "buffer", "true"),
            map:put($map, "body-getter", function($format as xs:string?) {
                document {
                    element {node-name($root)} {
                        $root/(@* except $uri-att),
                        $root/node()
                        }
                    }
                }),
            $map
            )
    return
        try {
            docmodupd:put($headers,$params,$env)
        } catch($e) {
            <rapi:request-failure>
                <rapi:uri>{$uri}</rapi:uri>
                <rapi:error>{$e}</rapi:error>
            </rapi:request-failure>
        }
};
