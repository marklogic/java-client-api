xquery version "1.0-ml";

module namespace ruleTransform = "http://marklogic.com/rest-api/transform/ruleTransform";

declare namespace rapi = "http://marklogic.com/rest-api";

declare function ruleTransform:transform(
              $context as map:map,
              $params as map:map,
              $content as document-node()
) as document-node() 
{
    let $rules := $content/node()/*
    return
    document {
        element { node-name($content/node()) } {
            for $rule-match in $rules
            return
                element { node-name($rule-match) } {
                    $rule-match/*,
                    element transformed-name { $rule-match/rapi:name/text() }
                }
        }
    }
};
