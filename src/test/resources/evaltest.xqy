declare namespace test='http://marklogic.com/test';
declare variable $test:myString as xs:string external;
declare variable $myArray as json:array external;
declare variable $myObject as json:object external;
declare variable $myAnyUri as xs:anyURI external;
declare variable $myBinary as xs:hexBinary external;
declare variable $myBase64Binary as xs:base64Binary external;
declare variable $myHexBinary as xs:hexBinary external;
declare variable $myDuration as xs:duration external;
declare variable $myQName as xs:QName external;
declare variable $myDocument as xs:string external;
declare variable $myAttribute as xs:string external;
declare variable $myComment as xs:string external;
declare variable $myElement as xs:string external;
declare variable $myProcessingInstruction as xs:string external;
declare variable $myText as xs:string external;
declare variable $myBool as xs:boolean external;
declare variable $myInteger as xs:integer external;
declare variable $myBigInteger as xs:string external;
declare variable $myDecimal as xs:decimal external;
declare variable $myDouble as xs:double external;
declare variable $myFloat as xs:float external;
declare variable $myGDay as xs:gDay external;
declare variable $myGMonth as xs:gMonth external;
declare variable $myGMonthDay as xs:gMonthDay external;
declare variable $myGYear as xs:gYear external;
declare variable $myGYearMonth as xs:gYearMonth external;
declare variable $myDate as xs:date external;
declare variable $myDateTime as xs:dateTime external;
declare variable $myTime as xs:time external;
declare variable $myNull external;
let $myBinary := binary{$myBinary}
let $myDocument := 
    xdmp:unquote($myDocument) 
let $myAttribute := 
    xdmp:unquote($myAttribute)/*/@* 
let $myComment := 
    xdmp:unquote($myComment)/comment() 
let $myElement := 
    xdmp:unquote($myElement)/element() 
let $myProcessingInstruction := 
    xdmp:unquote($myProcessingInstruction)/processing-instruction() 
let $myText := text {$myText} 
let $myCtsQuery := cts:word-query('a') 
return (
    $test:myString, $myArray, $myObject, $myAnyUri, 
    $myBinary, $myBase64Binary, $myHexBinary, $myDuration, $myQName,
    $myDocument, $myAttribute, $myComment, $myElement, $myProcessingInstruction, $myText, 
    $myBool, $myInteger, $myBigInteger, $myDecimal, $myDouble, $myFloat,
    $myGDay, $myGMonth, $myGMonthDay, $myGYear, $myGYearMonth, $myDate, $myDateTime, $myTime,
    $myNull, $myCtsQuery
)
