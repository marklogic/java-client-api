xquery version "1.0-ml";

module namespace my-lib = "http://marklogic.com/java-unit-test/my-lib";

(: Find the minimum value in a sequence of value composed of :)
(: the current node and a set of input values. :)
declare function my-lib:getMin(
        $current as node()?,
        $args as item()*
) as node()*
{
    if ($current/data() castable as xs:decimal)
    then
        let $new-value := fn:min(($current, $args))
        return
            typeswitch($current)
                case element()           (: XML :)
                    return element {fn:node-name($current)} {$new-value}
                case number-node()       (: JSON :)
                    return number-node {$new-value}
                default return fn:error((), "RESTAPI-SRVEXERR",
                        ("400", "Bad Request",
                        fn:concat("Not an element or number node: ",
                                xdmp:path($current))))
    else fn:error((), "RESTAPI-SRVEXERR", ("400", "Bad Request",
    fn:concat("Non-decimal data: ", $current)))
};