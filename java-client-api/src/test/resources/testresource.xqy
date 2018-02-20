xquery version "1.0-ml";
module namespace testresource = "http://marklogic.com/rest-api/resource/testresource";

import module namespace plugin = "http://marklogic.com/extension/plugin"
    at "/MarkLogic/plugin/plugin.xqy";

declare default function namespace "http://www.w3.org/2005/xpath-functions";
declare option xdmp:mapping "false";

declare function testresource:get(
    $context as map:map,
    $params  as map:map
) as document-node()*
{
    document {
        <read-doc>
            <param>{(map:get($params,"value"),"UNDEFINED")[1]}</param>
        </read-doc>
        },
    if (empty(map:get($context,"output-boundary")))
    then map:put($context,"output-types","application/xml")
    else (
        document {
            <read-multi-doc>
                <multi-param>{(map:get($params,"value"),"UNDEFINED")[1]}</multi-param>
            </read-multi-doc>
            },
        map:put($context, "output-types", ("application/xml","application/xml"))
        )
};

declare function testresource:put(
    $context as map:map,
    $params  as map:map,
    $input   as document-node()*
) as document-node()?
{
    document {
        <wrote-doc>
            <param>{(map:get($params,"value"),"UNDEFINED")[1]}</param>
            <input-doc>{(subsequence($input,1,1)/string(*),"UNDEFINED")[1]}</input-doc>
            {
                if (count($input) lt 2) then ()
                else <multi-input-doc>{(subsequence($input,2,1)/string(*),"UNDEFINED")[1]}</multi-input-doc>
            }
        </wrote-doc>
        }
};

declare function testresource:post(
    $context as map:map,
    $params  as map:map,
    $input   as document-node()*
) as document-node()*
{
    document {
        <applied-doc>
            <param>{(map:get($params,"value"),"UNDEFINED")[1]}</param>
            <input-doc>{($input/string(*),"UNDEFINED")[1]}</input-doc>
            {
                if (count($input) lt 2) then ()
                else <multi-input-doc>{(subsequence($input,2,1)/string(*),"UNDEFINED")[1]}</multi-input-doc>
            }
        </applied-doc>
        },
    if (empty(map:get($context,"output-boundary")))
    then map:put($context,"output-types","application/xml")
    else (
        document {
            <applied-multi-doc>
                <multi-param>{(map:get($params,"value"),"UNDEFINED")[1]}</multi-param>
            </applied-multi-doc>
            },
        map:put($context, "output-types", ("application/xml","application/xml"))
        )
};

declare function testresource:delete(
    $context as map:map,
    $params  as map:map
) as document-node()?
{
    document {
        <deleted-doc>
            <param>{(map:get($params,"value"),"UNDEFINED")[1]}</param>
        </deleted-doc>
        }
};
