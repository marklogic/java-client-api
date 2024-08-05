(:
  Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
:)

xquery version "1.0-ml";
module namespace example =
  "http://marklogic.com/rest-api/transform/add-attr-xquery-transform";

declare function example:transform(
  $context as map:map,
  $params as map:map,
  $content as document-node()
) as document-node()
{
   if (fn:empty($content/*)) then $content
  else (
    let $value := (map:get($params,"value"),"UNDEFINED")[1]
    let $name := (map:get($params, "name"), "transformed")[1]
    let $root  := $content/*
    return
    (
    document {
      $root/preceding-sibling::node(),
      element {fn:node-name($root)} {
        attribute { fn:QName("", $name) } {$value},
        $root/@*,
        $root/node()
      },
      $root/following-sibling::node()
    },
    xdmp:log(fn:concat($name,"-",$value))
    )
    )
};
