declare namespace test='http://marklogic.com/test';
declare variable $test:myString as xs:string external;
declare variable $myXmlNode as xs:string external;
(:declare variable $myJsonNode as xs:string external;:)
declare variable $myBool as xs:boolean external;
declare variable $myInteger as xs:integer external;
declare variable $myDecimal as xs:decimal external;
declare variable $myDouble as xs:double external;
declare variable $myFloat as xs:float external;
declare variable $myNull  external;

(
    $test:myString,
      $myBool,
     document{ xdmp:unquote($myXmlNode) },
     xdmp:unquote($myXmlNode)//comment(),
     xdmp:unquote($myXmlNode)//text(),
     xdmp:unquote($myXmlNode)//*,
     xdmp:unquote($myXmlNode)/@attr,    
    xdmp:unquote($myXmlNode)//processing-instruction(),
     (:$myNull,
     xdmp:unquote($myJsonNode)/a,
     xdmp:unquote($myJsonNode)/b,
     xdmp:unquote($myJsonNode)/c1,
     xdmp:unquote($myJsonNode)/d,
     xdmp:unquote($myJsonNode)/f,
     xdmp:unquote($myJsonNode)/g, :)
     $myInteger,  
     $myDecimal, 
     $myDouble, 
     $myFloat
    )