xquery version "1.0-ml";
module namespace adapter = "http://marklogic.com/mlcp/transform/RestTransformAdapter.xqy";

import module namespace tformod = "http://marklogic.com/rest-api/models/transform-model"
    at "/MarkLogic/rest-api/models/transform-model.xqy";

declare default function namespace "http://www.w3.org/2005/xpath-functions";
declare option xdmp:mapping "false";

declare function adapter:transform(
  $content as map:map,
  $context as map:map
) as map:map*
{
  let $uri           := map:get($content, "uri")
  let $docIn         := map:get($content, "value")
  let $params        := xdmp:from-json-string(
    (map:get($context, "transform_param"), '{"ml.transform":"transform"}')[1]
  )
  let $transform     := map:get($params, "ml.transform")
  let $targetContext := map:entry("uri", $uri)
  let $_             := map:delete($params, "ml.transform")
  let $docOut        := tformod:apply-transform(
    $transform, $targetContext, $params, $docIn
  )
  return (
    map:put($content, "value", $docOut),
    $content
  )
};

