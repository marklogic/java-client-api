xquery version "1.0-ml";

declare default function namespace "http://www.w3.org/2005/xpath-functions";
declare option xdmp:mapping "false";

declare variable $value as xs:string? external := ();

xdmp:to-json(
    if (empty($value))
    then map:entry("type","null")
    else map:entry("value",$value)=>map:with("type",xdmp:type($value))
    )
