(:
  Copyright (c) 2018 MarkLogic Corporation
 
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at
 
     http://www.apache.org/licenses/LICENSE-2.0
 
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
:)

xquery version "1.0-ml";
module namespace example =
  "http://marklogic.com/rest-api/transform/add-element-xquery-invalid-bitemp-transform";

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
    let $element-name := "javaValidStartERI"
    let $element-value := "2007-12-31T11:59:59"
    return 
    (
    document {
      $root/preceding-sibling::node(),
      element {fn:name($root)} {
        attribute { fn:QName("", $name) } {$value},
	element {$element-name} {$element-value},
        $root/@*,
        $root/node()
      },
      $root/following-sibling::node()
    },
    xdmp:log(fn:concat($name,"-",$value))
    )
    )
};
