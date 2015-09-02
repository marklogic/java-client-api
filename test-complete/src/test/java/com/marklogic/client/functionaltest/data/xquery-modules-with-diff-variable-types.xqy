(:
  Copyright 2014-2015 MarkLogic Corporation
 
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