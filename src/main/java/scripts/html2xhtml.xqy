xquery version "1.0-ml";

(: Copyright 2011-2012 MarkLogic Corporation.  All Rights Reserved. :)

(: An XQuery transform must follow some conventions rigorously:
   *  The transform must be an XQuery library module
   *  The namespace must be http://marklogic.com/rest-api/transform/TRANSFORM_NAME
   *  The namespace prefix must be TRANSFORM_NAME
   *  The library module must have a function with the local name "transform"
      that takes two maps and a document as input and returns a document as output
      The $context map provides environment information including the input type
          and the output type (which may be modified)
      The $params map provides any parameters for the transform
 :)
module namespace html2xhtml = "http://marklogic.com/rest-api/transform/html2xhtml";

import module namespace plugin = "http://marklogic.com/extension/plugin"
    at "/MarkLogic/plugin/plugin.xqy";

declare namespace tidy = "xdmp:tidy";

declare default function namespace "http://www.w3.org/2005/xpath-functions";
declare option xdmp:mapping "false";

(: transform HTML documents into modifiable and indexable XHTML documents :)
declare function html2xhtml:transform(
    $context as map:map,
    $params  as map:map,
    $content as document-node()  
) as document-node()
{
    if (empty($content) or exists($content/(element()|binary())))
    then $content
    else
        let $input-type := map:get($context,"input-type")
        return
            if (not($input-type = "text/html"))
            then $content
            else
                let $param-names := map:keys($params)
                let $options     :=
                    if (empty($param-names)) then ()
                    else
                        <options xmlns="xdmp:tidy">{
                            for $param-name in $param-names
                            return element {QName("xdmp:tidy",$param-name)} {
                                text {map:get($params,$param-name)}
                                }
                        }</options>
                let $result      :=
                    if (empty($options))
                    then xdmp:tidy($content)
                    else xdmp:tidy($content, $options)
                let $status      := subsequence($result,1,1)
                return (
                    if (empty($status/tidy:error)) then ()
                    else xdmp:log(
                        "XHTML conversion error for " ||
                        map:get($context,"uri") || ": " ||
                        xdmp:quote($status)
                        ),

                    if (count($result) lt 2) then ()
                    else (
                        map:put($context,"output-type","application/xhtml+xml"),
                        subsequence($result,2,1)
                        )
                    )
};
