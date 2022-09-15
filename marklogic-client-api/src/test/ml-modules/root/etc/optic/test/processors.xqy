xquery version "1.0-ml";
module namespace optestproc="http://marklogic.com/optic/test/processors";
declare default function namespace "http://www.w3.org/2005/xpath-functions";
declare option xdmp:mapping "false";
declare function optestproc:seconds-mapper($row as map:map) {
    map:with($row, "seconds", floor(seconds-from-dateTime(current-dateTime())))
};
