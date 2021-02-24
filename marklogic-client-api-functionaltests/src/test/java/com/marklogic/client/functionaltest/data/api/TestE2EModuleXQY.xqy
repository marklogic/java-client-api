xquery version "1.0-ml";

declare variable $items as xs:string? external;
(: Do not initialize the external variable:)
declare function local:xqyfunction($inputs as xs:string) as xs:string
{
 fn:concat("QA Module Returns ", $inputs)
};

if (fn:exists($items))
then
  local:xqyfunction($items)
else
  local:xqyfunction( "Passed in null parameter.")