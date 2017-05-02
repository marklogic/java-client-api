(:
  Copyright 2014-2017 MarkLogic Corporation
 
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

module namespace custom-lib =
  "http://marklogic.com/ext/patch/custom-lib";

declare function custom-lib:underwrite(
    $node    as node()?,
    $content as item()*
) as node()*
{
    let $element := $content[1]
    return
        if (empty($node))
        then $element
        else
            let $atts      := $element/@*
            let $att-names := $atts/node-name(.)
            return element {node-name($node)} {
                $node/@*[not(node-name(.) = $att-names)],
                $atts,
                $node/node(),
                $element/node()
                }
				
};