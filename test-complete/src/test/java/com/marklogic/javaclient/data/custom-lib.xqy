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