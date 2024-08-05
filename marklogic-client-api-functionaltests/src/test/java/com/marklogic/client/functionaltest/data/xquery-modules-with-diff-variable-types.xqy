(:
  Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
:)

declare namespace test='http://marklogic.com/test';
declare variable $test:myString as xs:string external;
declare variable $myXmlNode external;
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
    document{ $myXmlNode },
    $myXmlNode//comment(),
    $myXmlNode//text(),
    $myXmlNode//*,
    $myXmlNode/@attr,
    $myXmlNode//processing-instruction(),
    $myInteger,
    $myDecimal,
    $myDouble,
    $myFloat
    )
