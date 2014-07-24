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