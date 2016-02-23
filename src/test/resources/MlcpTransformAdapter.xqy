xquery version "1.0-ml";
module namespace testxqy = "http://marklogic.com/rest-api/transform/MlcpTransformAdapter";

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
  let $targetModule    := map:get($params,"ml.module")
  let $targetNamespace := map:get($params,"ml.namespace")
  let $targetFunction  := (map:get($params,"ml.function"), "transform")[1]
  let $_               := (
    map:delete($params, "ml.module"),
    map:delete($params, "ml.namespace"),
    map:delete($params, "ml.function")
  )
  let $function      := xdmp:function(QName($targetNamespace, $targetFunction), $targetModule)
  let $targetContent := map:entry("uri", map:get($context, "uri"))
  let $_             := map:put($targetContent, "value", $content)
let $_ := xdmp:log("DEBUG: [MlcpTransformAdapter] xdmp:to-json-string($params)=[" || xdmp:to-json-string($params) || "]")
  let $targetContext := if ( empty($params) or map:count($params) = 0 ) then ()
    else map:entry("transform_param", xdmp:to-json-string($params))
  let $returnMap     := xdmp:apply($function, $targetContent, $targetContext)[1]
  (: TODO: finish handling multiple documents--for now this only handles one :)
  let $firstValue    := map:get($returnMap, "value")
  return $firstValue
};

