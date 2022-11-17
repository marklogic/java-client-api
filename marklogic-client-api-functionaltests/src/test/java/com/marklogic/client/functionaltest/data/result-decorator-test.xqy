(:
  Copyright (c) 2022 MarkLogic Corporation
 
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
module namespace rd = "http://my.extension/namespace";

declare namespace search =  "http://marklogic.com/appservices/search";

declare function rd:decorate(
  $uri as xs:anyURI
) as node()*
{
    let $format   := xdmp:uri-format($uri)
    let $mimetype := xdmp:uri-content-type($uri)
    return (
        attribute result-decorator { "true" }

        )
};

declare function rd:decorator(
 $uri as xs:anyURI
 ) as node()*
{
  let $format   := xdmp:uri-format($uri)
  let $mimetype := xdmp:uri-content-type($uri)
  return (
    attribute href { concat("/documents/are/here?uri=", $uri) },
 
    if (empty($mimetype)) then ()
    else attribute mimetype {$mimetype},
 
    if (empty($format)) then ()
    else attribute format { $format },
	element my-elem { "Result Decorated" }
  )
};

declare function rd:decoratorWithoutMimeType(
 $uri as xs:anyURI
 ) as node()*
{
  let $format   := xdmp:uri-format($uri)
  let $mimetype := xdmp:uri-content-type($uri)
  return (
    attribute href { concat("/documents/are/here?uri=", $uri) },
 
   if (empty($mimetype)) then ()
    else attribute mimetype {"Null"},
 
    if (empty($format)) then ()
    else attribute format { $format },
	element my-elem { "Result Decorated" }
  )
};

declare function rd:decorate-element(
  $uri as xs:anyURI
) as node()*
{
    let $format   := xdmp:uri-format($uri)
    let $mimetype := xdmp:uri-content-type($uri)
    return (
        element search:href { concat("/documents/are/here?uri=", $uri) },
 
        if (empty($mimetype)) then ()
        else element search:mimetype {$mimetype},
 
        if (empty($format)) then ()
        else element search:format { $format }
        )
};

declare function rd:decorate-with-attribute(
  $uri as xs:anyURI
) as node()*
{
    let $format   := xdmp:uri-format($uri)
    let $mimetype := xdmp:uri-content-type($uri)
    return (
        attribute href { concat("/documents/are/here?uri=", $uri) },
 
        if (empty($mimetype)) then ()
        else attribute mimetype {$mimetype},
 
        if (empty($format)) then ()
        else attribute format { $format }
        )
};