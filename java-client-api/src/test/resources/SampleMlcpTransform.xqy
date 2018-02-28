xquery version "1.0-ml";
module namespace example = "http://marklogic.com/example";

(: If the input document is XML, insert @NEWATTR, with the value
 : specified in the input parameter. If the input document is not
 : XML, leave it as-is.
 :)
declare function example:transform(
  $content as map:map,
  $context as map:map
) as map:map*
{
  let $params := xdmp:from-json-string(
    (map:get($context, "transform_param"), '{"attr-value":"UNDEFINED"}')[1]
  )
  let $attr-value := map:get($params, "attr-value")
  let $the-doc := map:get($content, "value")
  return
    if (fn:empty($the-doc/element()))
    then $content
    else
      let $root := $the-doc/*
      return (
        map:put($content, "value",
          document {
            $root/preceding-sibling::node(),
            element {node-name($root)} {
              attribute {
                  QName(
                      "http://marklogic.com/rest-api/test/transform",
                      "transformed"
                      )
                  } {$attr-value},
              $root/@*,
              $root/node()
            },
            $root/following-sibling::node()
          }
        ), $content
      )
};
