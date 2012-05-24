xquery version "1.0-ml";
module namespace testxqy = "http://marklogic.com/rest-api/transform/testxqy";

import module namespace plugin = "http://marklogic.com/extension/plugin"
    at "/MarkLogic/plugin/plugin.xqy";

declare default function namespace "http://www.w3.org/2005/xpath-functions";
declare option xdmp:mapping "false";

declare function testxqy:transform(
    $context as map:map,
    $params  as map:map,
    $content as document-node()  
) as document-node()
{
    if (empty($content/*)) then $content
    else
        let $value := (map:get($params,"value"),"UNDEFINED")[1]
        let $root  := $content/*
        return document {
            $root/preceding-sibling::node(),
            element {name($root)} {
                attribute {
                    QName(
                        "http://marklogic.com/rest-api/test/transform",
                        "transformed"
                        )
                    } {$value},
                $root/@*,
                $root/node()
                },
            $root/following-sibling::node()
            } 
};
