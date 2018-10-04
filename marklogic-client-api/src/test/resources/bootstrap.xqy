xquery version "1.0-ml";

module namespace bootstrap = "http://marklogic.com/rest-api/resource/bootstrap";
(: Copyright 2002-2018 Mark Logic Corporation.  All Rights Reserved. :)

import module namespace admin = "http://marklogic.com/xdmp/admin" at "/MarkLogic/admin.xqy";
import module namespace temporal = "http://marklogic.com/xdmp/temporal" at "/MarkLogic/temporal.xqy";

declare namespace dbx = "http://marklogic.com/xdmp/database";
declare namespace error = "http://marklogic.com/xdmp/error";

declare default function namespace "http://www.w3.org/2005/xpath-functions";
declare option xdmp:mapping "false";

declare function bootstrap:database-configure(
  $dbid as xs:unsignedLong
) as empty-sequence()
{
    let $c := admin:get-configuration()

    let $c := bootstrap:create-range-element-indexes($c, $dbid)
    let $c := bootstrap:create-element-attribute-range-indexes($c, $dbid)
    let $c := bootstrap:create-element-word-lexicons($c, $dbid)
    let $c := bootstrap:create-geospatial-element-indexes($c, $dbid)
    let $c := bootstrap:create-geospatial-element-child-indexes($c, $dbid)
    let $c := bootstrap:create-geospatial-element-pair-indexes($c, $dbid)
    let $c := bootstrap:create-path-namespaces($c, $dbid)
    let $c := bootstrap:create-geospatial-path-indexes($c, $dbid)
    let $c := bootstrap:create-geospatial-region-path-indexes($c, $dbid)
    let $c := bootstrap:create-path-range-indexes($c, $dbid)
    let $c := bootstrap:create-fields($c, $dbid)
    let $c := bootstrap:create-default-rulesets($c, $dbid)
    (: you can't create field and field range index in same transaction :)
    return admin:save-configuration-without-restart($c),

    let $c := admin:get-configuration()
    let $c := bootstrap:create-field-range-indexes($c, $dbid)
    return admin:save-configuration-without-restart($c)
};

declare function bootstrap:create-range-element-indexes(
    $c as element(configuration),
    $dbid as xs:unsignedLong
) as element(configuration)
{
    
    (: collection and word lexicons and triple index on :)
    let $c := admin:database-set-collection-lexicon($c, $dbid, true())
    let $c := admin:database-set-maintain-last-modified($c, $dbid, true())
    let $c := admin:database-set-triple-index($c, $dbid, true())

    let $index-specs :=
        let $curr-idx := admin:database-get-range-element-indexes($c, $dbid)
        let $new-idx  := (
            "dateTime", "",                                   "lastModified",
            "dateTime", "http://marklogic.com/xdmp/property", "last-modified",
            "string",   "http://nwalsh.com/ns/photolib",      "tag",
            "date",     "http://nwalsh.com/ns/photolib",      "date",
            "double",   "",                                   "double",
            "int",      "",                                   "int",
            "string",   "",                                   "grandchild",
            "string",   "",                                   "string",
            "dateTime", "",                                   "system-start",
            "dateTime", "",                                   "system-end",
            "dateTime", "",                                   "valid-start",
            "dateTime", "",                                   "valid-end"
            )
        for $i in 1 to (count($new-idx) idiv 3)
        let $offset    := ($i * 3) - 2
        let $datatype  := subsequence($new-idx, $offset, 1)
        let $collation :=
            if ($datatype eq "string")
            then "http://marklogic.com/collation/"
            else ""
        let $ns        := subsequence($new-idx, $offset + 1, 1)
        let $name      := subsequence($new-idx, $offset + 2, 1)
        let $curr      := $curr-idx[
            string(dbx:scalar-type) eq $datatype and
            string(dbx:namespace-uri) eq $ns and
            tokenize(string(dbx:localname), "\s+") = $name and
            string(dbx:collation) eq $collation
            ]
        return
            if (exists($curr)) then (
                xdmp:log(concat("Element range index already exists: ", $name))
            ) else (
                admin:database-range-element-index(
                    $datatype, $ns, $name, $collation, false()
                ),
                xdmp:log(concat("Creating element range index: ", $name))
            )

    return
        if (empty($index-specs)) then $c
        else (
            admin:database-add-range-element-index($c, $dbid, $index-specs),
            admin:save-configuration-without-restart($c),
            xdmp:commit()
        )
};

declare function bootstrap:create-element-attribute-range-indexes(
    $c as element(configuration),
    $dbid as xs:unsignedLong
) as element(configuration)
{
    let $index-specs :=
        let $curr-idx := admin:database-get-range-element-attribute-indexes($c, $dbid)
        let $new-idx  := (
            "date", "http://nwalsh.com/ns/photolib", "view", "", "date"
            )
        for $i in 1 to (count($new-idx) idiv 5)
        let $offset    := ($i * 5) - 4
        let $datatype  := subsequence($new-idx, $offset, 1)
        let $collation :=
            if ($datatype eq "string")
            then "http://marklogic.com/collation/"
            else ""
        let $e-ns      := subsequence($new-idx, $offset + 1, 1)
        let $e-name    := subsequence($new-idx, $offset + 2, 1)
        let $a-ns      := subsequence($new-idx, $offset + 3, 1)
        let $a-name    := subsequence($new-idx, $offset + 4, 1)
        let $curr      := $curr-idx[
            string(dbx:scalar-type) eq $datatype and
            string(dbx:parent-namespace-uri) eq $e-ns and
            tokenize(string(dbx:parent-localname), "\s+") = $e-name and
            string(dbx:namespace-uri) eq $a-ns and
            tokenize(string(dbx:localname), "\s+") = $a-name and
            string(dbx:collation) eq $collation
            ]
        return
            if (exists($curr)) then (
                xdmp:log(concat("element-attribute range index already exists:[", $e-name, ",", $a-name, "]"))
            ) else (
                admin:database-range-element-attribute-index(
                    $datatype, $e-ns, $e-name, $a-ns, $a-name, $collation, false()
                ),
                xdmp:log(concat("Creating element-attribute range index:[", $e-name, ",", $a-name, "]"))
            )
    return
        if (empty($index-specs)) then $c
        else admin:database-add-range-element-attribute-index($c, $dbid, $index-specs)
};

declare function bootstrap:create-element-word-lexicons(
    $c as element(configuration),
    $dbid as xs:unsignedLong
) as element(configuration)
{
    let $index-specs :=
        let $curr-idx := admin:database-get-element-word-lexicons($c, $dbid)
        let $new-idx  := (
            "", "suggest"
            )
        for $i in 1 to (count($new-idx) idiv 2)
        let $offset    := ($i * 2) - 1
        let $collation := "http://marklogic.com/collation/"
        let $ns        := subsequence($new-idx, $offset, 1)
        let $name      := subsequence($new-idx, $offset + 1, 1)
        let $curr      := $curr-idx[
            string(dbx:namespace-uri) eq $ns and
            tokenize(string(dbx:localname), "\s+") = $name and
            string(dbx:collation) eq $collation
            ]
        return
            if (exists($curr)) then (
                xdmp:log(concat("element-word index already exists: ", $name))
            ) else (
                admin:database-element-word-lexicon($ns, $name, $collation),
                xdmp:log(concat("Creating element-word index: ", $name))
            )
    return
        if (empty($index-specs)) then $c
        else admin:database-add-element-word-lexicon($c, $dbid, $index-specs)
};

declare function bootstrap:create-geospatial-element-indexes(
    $c as element(configuration),
    $dbid as xs:unsignedLong
) as element(configuration)
{
    (: create geospatial indexes for geo unit test :)
    let $index-specs :=
        let $curr-idx := admin:database-get-geospatial-element-indexes($c, $dbid)
        let $new-idx  := (
            "", "latLong", "wgs84", "point"
            )
        for $i in 1 to (count($new-idx) idiv 4)
        let $offset    := ($i * 4) - 3
        let $element-ns    := subsequence($new-idx, $offset + 0, 1)
        let $element-name  := subsequence($new-idx, $offset + 1, 1)
        let $coord-sys := subsequence($new-idx, $offset + 2, 1)
        let $point-format := subsequence($new-idx, $offset + 3, 1)
        let $curr      := $curr-idx[
            string(dbx:namespace-uri) eq $element-ns and
            tokenize(string(dbx:localname), "\s+") = $element-name and
            string(dbx:coordinate-system) eq $coord-sys and
            string(dbx:point-format) eq $point-format
            ]
        return
            if (exists($curr)) then (
                xdmp:log(concat("Geo-elem-index already exists:[", $element-name, "]"))
            ) else (
                admin:database-geospatial-element-index(
                    $element-ns, $element-name, $coord-sys, false(), $point-format, "reject"
                ),
                xdmp:log(concat("Creating geo-elem-index:[", $element-name, "]"))
            )
    return
        if (empty($index-specs)) then $c
        else admin:database-add-geospatial-element-index($c, $dbid, $index-specs)
};

declare function bootstrap:create-geospatial-element-child-indexes(
    $c as element(configuration),
    $dbid as xs:unsignedLong
) as element(configuration)
{
    (: create geospatial indexes for geo unit test :)
    let $index-specs :=
        let $curr-idx := admin:database-get-geospatial-element-child-indexes($c, $dbid)
        let $p := "http://marklogic.com/ns/test/places"
        let $new-idx  := (
            "", "com.marklogic.client.test.City", "", "latLong", "wgs84", "point"
            )
        for $i in 1 to (count($new-idx) idiv 6)
        let $offset := ($i * 6) - 5
        let $p-ns         := subsequence($new-idx, $offset, 1)
        let $p-name       := subsequence($new-idx, $offset + 1, 1)
        let $element-ns   := subsequence($new-idx, $offset + 2, 1)
        let $element-name := subsequence($new-idx, $offset + 3, 1)
        let $coord-sys    := subsequence($new-idx, $offset + 4, 1)
        let $point-format := subsequence($new-idx, $offset + 5, 1)
        let $curr      := $curr-idx[
            string(dbx:parent-namespace-uri) eq $p-ns and
            tokenize(string(dbx:parent-localname), "\s+") = $p-name and
            string(dbx:namespace-uri) eq $element-ns and
            tokenize(string(dbx:localname), "\s+") = $element-name and
            string(dbx:coordinate-system) eq $coord-sys and
            string(dbx:point-format) eq $point-format
            ]
        return
            if (exists($curr)) then (
                xdmp:log(concat("Geo-elem-child-index already exists:[", $p-name, ",", $element-name, "]"))
            ) else (
                admin:database-geospatial-element-child-index(
                    $p-ns, $p-name, $element-ns, $element-name, $coord-sys, false(), $point-format, "reject"
                ),
                xdmp:log(concat("Creating geo-elem-child-index:[", $p-name, ",", $element-name, "]"))
            )
    return
        if (empty($index-specs)) then $c
        else admin:database-add-geospatial-element-child-index($c, $dbid, $index-specs)
};

declare function bootstrap:create-geospatial-element-pair-indexes(
    $c as element(configuration),
    $dbid as xs:unsignedLong
) as element(configuration)
{
    (: create geospatial indexes for geo unit test :)
    let $index-specs :=
        let $curr-idx := admin:database-get-geospatial-element-pair-indexes($c, $dbid)
        let $p := "http://marklogic.com/ns/test/places"
        let $new-idx  := (
            $p, "place", $p, "lat", $p, "long", "wgs84",
            "", "com.marklogic.client.test.City", "", "latitude", "", "longitude", "wgs84",
            "", "point", "", "lat", "", "lon", "wgs84/double"
            )
        for $i in 1 to (count($new-idx) idiv 7)
        let $offset    := ($i * 7) - 6
        let $p-ns      := subsequence($new-idx, $offset, 1)
        let $p-name    := subsequence($new-idx, $offset + 1, 1)
        let $lat-ns    := subsequence($new-idx, $offset + 2, 1)
        let $lat-name  := subsequence($new-idx, $offset + 3, 1)
        let $lon-ns    := subsequence($new-idx, $offset + 4, 1)
        let $lon-name  := subsequence($new-idx, $offset + 5, 1)
        let $coord-sys := subsequence($new-idx, $offset + 6, 1)
        let $curr      := $curr-idx[
            string(dbx:parent-namespace-uri) eq $p-ns and
            tokenize(string(dbx:parent-localname), "\s+") = $p-name and
            string(dbx:latitude-namespace-uri) eq $lat-ns and
            tokenize(string(dbx:latitude-localname), "\s+") = $lat-name and
            string(dbx:longitude-namespace-uri) eq $lon-ns and
            tokenize(string(dbx:longitude-localname), "\s+") = $lon-name and
            string(dbx:coordinate-system) eq $coord-sys
            ]
        return
            if (exists($curr)) then (
                xdmp:log(concat("Geo-elem-pair-index already exists:[", $p-name, ",", $lat-name, ",", $lon-name, "]"))
            ) else (
                admin:database-geospatial-element-pair-index(
                    $p-ns, $p-name, $lat-ns, $lat-name, $lon-ns, $lon-name, $coord-sys, false(), "reject"
                ),
                xdmp:log(concat("Creating geo-elem-pair-index:[", $p-name, ",", $lat-name, ",", $lon-name, "]"))
            )
    return
        if (empty($index-specs)) then $c
        else admin:database-add-geospatial-element-pair-index($c, $dbid, $index-specs)
};

declare function bootstrap:create-path-namespaces(
    $c as element(configuration),
    $dbid as xs:unsignedLong
) as element(configuration)
{
    let $index-specs :=
        let $curr-idx := admin:database-get-path-namespaces($c, $dbid)
        let $new-idx  := (
            "rootOrg", "root.org",
            "targetOrg", "target.org"
            )
        let $n := 2
        for $i in 1 to (count($new-idx) idiv $n)
        let $offset    := ($i * $n) - ($n - 1)
        let $prefix    := subsequence($new-idx, $offset, 1)
        let $namespace := subsequence($new-idx, $offset + 1, 1)
        let $curr      := $curr-idx[
            string(dbx:prefix) eq $prefix and
            string(dbx:namespace-uri) eq $namespace
            ]
        return
            if (exists($curr)) then (
                xdmp:log(concat("Namespace already exists:", $prefix))
            ) else (
                admin:database-path-namespace( $prefix, $namespace ),
                xdmp:log(concat("Creating path-namespace:", $prefix))
            )
    return
        if (empty($index-specs)) then $c
        else admin:database-add-path-namespace($c, $dbid, $index-specs)
};

declare function bootstrap:create-geospatial-path-indexes(
    $c as element(configuration),
    $dbid as xs:unsignedLong
) as element(configuration)
{
    let $index-specs := 
        let $curr-idx := admin:database-get-geospatial-path-indexes($c, $dbid)
        let $new-idx  := (
            "com.marklogic.client.test.City/latLong",
            "/rootOrg:geo/targetOrg:path"
            )
        let $n := 1
        for $i in 1 to (count($new-idx) idiv $n)
        let $offset    := ($i * $n) - ($n - 1) 
        let $path      := subsequence($new-idx, $offset, 1)
        let $curr      := $curr-idx[
            string(dbx:path-expression) eq $path and
            string(dbx:coordinate-system) eq "wgs84" and
            string(dbx:range-value-positions) eq "false" and
            string(dbx:point-format) eq "point"
            ]
        return
            if (exists($curr)) then (
                xdmp:log(concat("Geospatial-path-index already exists:", $path))
            ) else (
                admin:database-geospatial-path-index(
                    $path, "wgs84", false(), "point", "reject"
                ),
                xdmp:log(concat("Creating geospatial-path-index:", $path))
            )
    return
        if (empty($index-specs)) then $c
        else admin:database-add-geospatial-path-index($c, $dbid, $index-specs)
};

declare function bootstrap:create-geospatial-region-path-indexes(
    $c as element(configuration),
    $dbid as xs:unsignedLong
) as element(configuration)
{
    let $index-specs :=
        let $curr-idx := admin:database-get-geospatial-region-path-indexes($c, $dbid)
        let $new-idx  := (
            "/country/region", "wgs84", 3, "ignore",
            "/country/region", "wgs84/double", 3, "ignore"
            )
        let $n := 4
        for $i in 1 to (count($new-idx) idiv $n)
        let $offset     := ($i * $n) - ($n - 1)
        let $path       := subsequence($new-idx, $offset, 1)
        let $coordsys   := subsequence($new-idx, $offset + 1, 1)
        let $precision  := subsequence($new-idx, $offset + 2, 1)
        let $invalidval := subsequence($new-idx, $offset + 3, 1)
        let $curr       := $curr-idx[
            string(dbx:path-expression) eq $path and
            string(dbx:coordinate-system) eq $coordsys and
            string(dbx:geohash-precision) eq $precision and
            string(dbx:invalid-values) eq $invalidval
            ]
        return
            if (exists($curr)) then (
                xdmp:log(concat("Geospatial-region-path-index already exists:", $path))
            ) else (
                admin:database-geospatial-region-path-index(
                    $path, $coordsys, $precision, $invalidval
                ),
                xdmp:log(concat("Creating geospatial-region-path-index:", $path))
            )
    return
        if (empty($index-specs)) then $c
        else admin:database-add-geospatial-region-path-index($c, $dbid, $index-specs)
};

declare function bootstrap:create-fields(
    $c as element(configuration),
    $dbid as xs:unsignedLong
) as element(configuration)
{

    let $def-specs :=
        let $curr-def := admin:database-get-fields($c, $dbid)
        for $new-def in ("int1", "int2")
        let $curr := $curr-def[
            string(dbx:field-name) eq $new-def
            ]
        return
            if (exists($curr)) then (
                xdmp:log(concat("Field index already exists: ", $new-def))
            ) else (
                admin:database-field($new-def, false()),
                xdmp:log(concat("Creating field: ", $new-def))
            )
    return
        if (empty($def-specs)) then $c
        else admin:database-add-field($c, $dbid, $def-specs)
};

declare function bootstrap:create-field-range-indexes(
    $c as element(configuration),
    $dbid as xs:unsignedLong
) as element(configuration)
{
    let $index-specs :=
        let $curr-idx := admin:database-get-range-field-indexes($c, $dbid)
        let $new-idx  := (
            "int", "int1",
            "int", "int2"
            )
        for $i in 1 to (count($new-idx) idiv 2)
        let $offset    := ($i * 2) - 1
        let $datatype  := subsequence($new-idx, $offset, 1)
        let $collation :=
            if ($datatype eq "string")
            then "http://marklogic.com/collation/"
            else ""
        let $name      := subsequence($new-idx, $offset + 1, 1)
        let $curr      := $curr-idx[
            string(dbx:scalar-type) eq $datatype and
            tokenize(string(dbx:field-name), "\s+") = $name and
            string(dbx:collation) eq $collation
            ]
        return
            if (exists($curr)) then (
                xdmp:log(concat("Field range index already exists: ", $name))
            ) else (
                admin:database-range-field-index(
                    $datatype, $name, $collation, false()
                ),
                xdmp:log(concat("Creating field range index: ", $name))
            )
    return
        if (empty($index-specs)) then $c
        else admin:database-add-range-field-index($c, $dbid, $index-specs)
};

declare function bootstrap:create-path-range-indexes(
    $c as element(configuration),
    $dbid as xs:unsignedLong
) as element(configuration)
{
    let $index-specs := 
        let $curr-idx := admin:database-get-range-path-indexes($c, $dbid)
        let $new-idx  := (
            "long", "com.marklogic.client.test.City/population",
            "string", "com.marklogic.client.test.City/alternateNames",
            "string", "com.marklogic.client.test.Country/continent",
            "dateTime", "com.marklogic.client.test.TimeTest/calendarTest",
            "dateTime", "com.marklogic.client.test.TimeTest/calendarTestCet",
            "dateTime", "com.marklogic.client.test.TimeTest/dateTest"
            )
        for $i in 1 to (count($new-idx) idiv 2)
        let $offset    := ($i * 2) - 1
        let $datatype  := subsequence($new-idx, $offset, 1)
        let $collation :=
            if ($datatype eq "string")
            then "http://marklogic.com/collation/"
            else ""
        let $path      := subsequence($new-idx, $offset + 1, 1)
        let $curr      := $curr-idx[
            string(dbx:scalar-type) eq $datatype and
            string(dbx:path-expression) eq $path and
            string(dbx:collation) eq $collation
            ]
        return
            if (exists($curr)) then (
                xdmp:log(concat("Path range index already exists: ", $path))
            ) else (
                admin:database-range-path-index(
                    $dbid, $datatype, $path, $collation, false(), "ignore"
                ),
                xdmp:log(concat("Creating path range index: ", $path))
            )
    return
        if (empty($index-specs)) then $c
        else admin:database-add-range-path-index($c, $dbid, $index-specs)
};

declare function bootstrap:create-default-rulesets(
    $c as element(configuration),
    $dbid as xs:unsignedLong
) as element(configuration)
{
    let $ruleset := admin:database-ruleset("rdfs.rules")
    return admin:database-add-default-ruleset($c, $dbid, $ruleset)
};

declare function bootstrap:security-config() { 
    try {
        bootstrap:security-eval(
            'sec:create-role("rest-evaluator", "rest-evaluator", ("rest-evaluator", "rest-writer"), (), ())')
    } catch($e) {
        if ( "SEC-ROLEEXISTS" = $e/error:code ) then xdmp:log("rest-evaluator role exists")
        else xdmp:log($e)
    },
    try {
        (: no rest-reader role, just the permission :)
        bootstrap:security-eval(
            'sec:create-role("read-privileged", "read-privileged", (), (), ())')
    } catch($e) {
        if ( "SEC-ROLEEXISTS" = $e/error:code ) then xdmp:log("read-privileged role exists")
        else xdmp:log($e)
    },
    try {
        (: no rest-reader role, just the permission :)
        bootstrap:security-eval(
            'sec:create-role("write-privileged", "write-privileged", (), (), ())')
    } catch($e) {
        if ( "SEC-ROLEEXISTS" = $e/error:code ) then xdmp:log("write-privileged role exists")
        else xdmp:log($e)
    },
    bootstrap:security-eval(
        'sec:privilege-add-roles("http://marklogic.com/xdmp/privileges/xdbc-eval", "execute", "rest-evaluator")'),
    bootstrap:security-eval(
        'sec:privilege-add-roles("http://marklogic.com/xdmp/privileges/xdbc-eval-in", "execute", "rest-evaluator")'),
    bootstrap:security-eval(
        'sec:privilege-add-roles("http://marklogic.com/xdmp/privileges/xdmp-eval", "execute", "rest-evaluator")'),
    bootstrap:security-eval(
        'sec:privilege-add-roles("http://marklogic.com/xdmp/privileges/xdmp-eval-in", "execute", "rest-evaluator")'),
    bootstrap:security-eval(
        'sec:privilege-add-roles("http://marklogic.com/xdmp/privileges/xdbc-invoke", "execute", "rest-evaluator")'),
    bootstrap:security-eval(
        'sec:privilege-add-roles("http://marklogic.com/xdmp/privileges/xdbc-invoke-in", "execute", "rest-evaluator")'),
    bootstrap:security-eval(
        'sec:privilege-add-roles("http://marklogic.com/xdmp/privileges/rest-reader", "execute", "read-privileged")'),
    bootstrap:security-eval(
        'sec:privilege-add-roles("http://marklogic.com/xdmp/privileges/rest-reader", "execute", "write-privileged")'),
    bootstrap:security-eval(
        'sec:privilege-add-roles("http://marklogic.com/xdmp/privileges/rest-writer", "execute", "write-privileged")'),
    bootstrap:security-eval(
        'sec:privilege-add-roles("http://marklogic.com/xdmp/privileges/temporal-document-protect", "execute", "rest-admin")'),
    bootstrap:security-eval(
        'sec:privilege-add-roles("http://marklogic.com/xdmp/privileges/temporal-document-wipe", "execute", "rest-admin")'),

    for $user in ("rest-admin", "rest-reader", "rest-writer", "rest-evaluator", "valid",
        "read-privileged", "write-privileged")
        let $user-id := 
            try {
                xdmp:user($user)
            } catch($e) {
                xdmp:log("User "||$user||" not found.")
            }
    return (
        if (exists($user-id)) then (
            xdmp:log("User "|| $user || ", id "||$user-id|| "already exists")
        ) else (
            if ($user eq "valid") then (
                bootstrap:security-eval('sec:create-user("valid", "valid unprivileged user", "x", (), (), (), ())')
            ) else (
                bootstrap:security-eval('sec:create-user("'||$user||'", "'||$user||' user", "x", ("'||$user||'"), (), (), () )')
            )
        )
    )
};

declare function bootstrap:security-eval(
$command as xs:string
) 
{ 
    xdmp:eval(concat('xquery version "1.0-ml"; ',
                'import module namespace sec="http://marklogic.com/xdmp/security" at  ',
                '    "/MarkLogic/security.xqy"; ',
                $command),
    (),
    <options xmlns="xdmp:eval">
        <database>{ xdmp:database("Security") }</database>
    </options>)
};

declare function bootstrap:load-modules-db()
{
    try {
    xdmp:eval('xquery version "1.0-ml";
declare default function namespace "http://www.w3.org/2005/xpath-functions";
declare option xdmp:mapping "false";
declare variable $sjssrc as xs:string external;
declare variable $xqysrc as xs:string external;
xdmp:document-insert("/etc/optic/test/processors.sjs", text{$sjssrc}, (
    xdmp:permission("rest-reader", "execute"),
    xdmp:permission("rest-admin",  "update")
    )),
xdmp:document-insert("/etc/optic/test/processors.xqy", text{$xqysrc}, (
    xdmp:permission("rest-reader", "execute"),
    xdmp:permission("rest-admin",  "update")
    ))
    ',
    map:map()
        => map:with("sjssrc", "'use strict';
function secondsMapper(result) {
  result.seconds = new Date().getSeconds();
  return result;
}
module.exports = {
    secondsMapper: secondsMapper
};
")
        => map:with("xqysrc", 'xquery version "1.0-ml";
module namespace optestproc="http://marklogic.com/optic/test/processors";
declare default function namespace "http://www.w3.org/2005/xpath-functions";
declare option xdmp:mapping "false";
declare function optestproc:seconds-mapper($row as map:map) {
    map:with($row, "seconds", floor(seconds-from-dateTime(current-dateTime())))
};
'),
    <options xmlns="xdmp:eval">
        <database>{xdmp:database("java-unittest-modules")}</database>
    </options>)
    } catch($e) {
        xdmp:log("Failed to populate modules database"),
        xdmp:log($e)
    }
};

declare function bootstrap:schema-config() { 
    try {
    xdmp:eval('xquery version "1.0-ml";
declare default function namespace "http://www.w3.org/2005/xpath-functions";
declare option xdmp:mapping "false";

xdmp:document-insert("/optic/test/musician.tdex",
    <template xmlns="http://marklogic.com/xdmp/tde">
    <context>/musician</context>
    <rows>
    <row>
      <schema-name>opticUnitTest</schema-name>
      <view-name>musician</view-name>
      <columns>
        <column><name>lastName</name><scalar-type>string</scalar-type><val>lastName</val></column>
        <column><name>firstName</name><scalar-type>string</scalar-type><val>firstName</val></column>
        <column><name>dob</name><scalar-type>date</scalar-type><val>dob</val></column>
      </columns>
    </row>
    </rows>
    </template>,
    (xdmp:permission("rest-reader", "read"), xdmp:permission("rest-writer", "update")),
    "http://marklogic.com/xdmp/tde"
    )',
    (),
    <options xmlns="xdmp:eval">
        <database>{xdmp:database("Schemas")}</database>
    </options>)
    } catch($e) {
        xdmp:log("Failed to create TDE template for testing"),
        xdmp:log($e)
    }
};

declare function bootstrap:temporal-setup() as xs:string*
{
    try {
        let $id := bootstrap:temporal-eval('temporal:axis-create(
            "system-axis",
            cts:element-reference(xs:QName("system-start"), "type=dateTime"),
            cts:element-reference(xs:QName("system-end"), "type=dateTime")
        )')
        return if ( $id ) then (
            xdmp:log("Created system-axis")
        ) else ()
    } catch($e) {
        if ( "XDMP-ELEMRIDXNOTFOUND" = $e/error:code ) then
            xdmp:log("Couldn't create system-axis.  Waiting for creation of system-start and system-end " ||
                "element range indexes...try again")
        else if ( "TEMPORAL-DUPAXIS" = $e/error:code ) then xdmp:log("system-axis already exists")
        else xdmp:log($e)
    },
    try {
        let $id := bootstrap:temporal-eval('temporal:axis-create(
            "valid-axis",
            cts:element-reference(xs:QName("valid-start"), "type=dateTime"),
            cts:element-reference(xs:QName("valid-end"), "type=dateTime")
        )')
        return if ( $id ) then (
            xdmp:log("Created valid-axis")
        ) else ()
    } catch($e) {
        if ( "XDMP-ELEMRIDXNOTFOUND" = $e/error:code ) then
            xdmp:log("Couldn't create valid-axis.  Waiting for creation of valid-start and valid-end " ||
                "element range indexes...try again")
        else if ( "TEMPORAL-DUPAXIS" = $e/error:code ) then xdmp:log("valid-axis already exists")
        else xdmp:log($e)
    },
    try {
        let $id := bootstrap:temporal-eval('temporal:collection-create(
			"temporal-collection", "system-axis", "valid-axis", "updates-admin-override")')
        return if ( $id ) then (
            xdmp:log("Created temporal-collection") 
        ) else ()
    } catch($e) {
        if ( "TEMPORAL-AXISNOTFOUND" = $e/error:code ) then 
            xdmp:log("Couldn't create temporal-collection.  " ||
                "Waiting for creation of system-axis and valid-axis...try again")
        else if ( "TEMPORAL-DUPCOLLECTION" = $e/error:code ) then xdmp:log("temporal-collection already exists")
        else xdmp:log($e)
    },
    try {
        bootstrap:temporal-eval('temporal:set-use-lsqt("temporal-collection", true())'),
        xdmp:log("set-use-lsqt to true()"),
        bootstrap:temporal-eval('temporal:set-lsqt-automation("temporal-collection", true())'),
        xdmp:log("set-lsqt-automation to true()")
    } catch($e) {
        if ( "TEMPORAL-COLLECTIONNOTFOUND" = $e/error:code ) then 
            xdmp:log("Couldn't set use-lsqt to true() and lsqt-automation to true().  " ||
                "Waiting for creation of temporal-colleciton...try again")
        else xdmp:log($e)
    }
};


declare function bootstrap:temporal-eval(
$command as xs:string
) 
{ 
    xdmp:eval(concat('xquery version "1.0-ml"; ',
				'import module namespace temporal = "http://marklogic.com/xdmp/temporal" ',
				'	at "/MarkLogic/temporal.xqy";',
                $command))
};

declare function bootstrap:load-data()
{
    xdmp:eval('xquery version "1.0-ml";
    xdmp:document-insert(
        "/sample/first.xml",
        <root name="first">
            <child id="1">
                <leaf>leaf1</leaf>
            </child>
            <child id="2">
                <leaf>leaf2</leaf>
            </child>
            <child id="3">
                <leaf>leaf3</leaf>
            </child>
        </root>
        ),
    xdmp:document-add-collections("/sample/first.xml",("http://some.org/collection1","http://some.org/collection2")),
    xdmp:document-set-permissions("/sample/first.xml",
        (xdmp:permission("rest-reader","read"),
        xdmp:permission("rest-writer","update"),
        xdmp:permission("app-user","read"),
        xdmp:permission("app-user","update"))
        ),
    xdmp:document-set-properties(
        "/sample/first.xml",
        (
            <sample-property>first</sample-property>,
            <container-property><name>alpha</name><number>1</number></container-property>
            )
        ),
    xdmp:document-insert(
        "/sample/second.txt",
        <text><![CDATA[First line
    Second line
    Third line]]></text>/text()
        ),
    xdmp:document-set-permissions("/sample/second.txt",
        (xdmp:permission("rest-reader","read"),
        xdmp:permission("rest-writer","update"),
        xdmp:permission("app-user","read"),
        xdmp:permission("app-user","update"))
        ),
    xdmp:document-set-properties(
        "/sample/second.txt",
        (
            <sample-property>second</sample-property>,
            <container-property><name>beta</name><number>2</number></container-property>
            )
        ),
    xdmp:document-insert(
        "/sample/third.json",
        <text><![CDATA[{
        "firstKey":"first value",
        "secondKey":["first item","second item","third item"],
        "thirdKey":3,
        "fourthKey":{"subKey":"sub value"}
    }]]></text>/text()
        ),
    xdmp:document-set-permissions("/sample/third.json",
        (xdmp:permission("rest-reader","read"),
        xdmp:permission("rest-writer","update"),
        xdmp:permission("app-user","read"),
        xdmp:permission("app-user","update"))
        ),
    xdmp:document-set-properties(
        "/sample/third.json",
        (
            <json:sample-property type="string">third</json:sample-property>,
            <json:container-property type="object">
                <json:name type="string">gamma</json:name>
                <json:number type="number">3</json:number>
            </json:container-property>
            )
        ),
    xdmp:document-insert(
        "/sample/fourth.xml",
    <json:json version="1.0" type="object">
        <json:firstKey type="string">first value</json:firstKey>
        <json:fourthKey type="object">
            <json:subKey type="string">sub value</json:subKey>
        </json:fourthKey>
        <json:secondKey type="array">
            <json:item type="string">first item</json:item>
            <json:item type="string">second item</json:item>
            <json:item type="string">third item</json:item>
        </json:secondKey>
        <json:thirdKey type="number">3</json:thirdKey>
    </json:json>
        ),
    xdmp:document-set-permissions("/sample/fourth.xml",
        (xdmp:permission("rest-reader","read"),
        xdmp:permission("rest-writer","update"),
        xdmp:permission("app-user","read"),
        xdmp:permission("app-user","update"))
        ),
    xdmp:document-set-properties(
        "/sample/fourth.xml",
        (
            <sample-property>fourth</sample-property>,
            <container-property><name>delta</name><number>4</number></container-property>
            )
        ),
    xdmp:document-insert(
        "/sample/fifth.sh",
            <text><![CDATA[#!/bin/sh
    echo "hello, world"
    ]]></text>/text()
        ),
    xdmp:document-set-permissions("/sample/fifth.sh",
        (xdmp:permission("rest-reader","read"),
        xdmp:permission("rest-writer","update"),
        xdmp:permission("app-user","read"),
        xdmp:permission("app-user","update"))
        ),
    xdmp:document-set-properties(
        "/sample/fifth.sh",
        (
            <sample-property>fifth</sample-property>,
            <container-property><name>epsilon</name><number>5</number></container-property>
            )
        ),
    xdmp:document-insert(
        "/sample/databases-icon-mini.png",
    binary{xs:hexBinary(xs:base64Binary(
    "iVBORw0KGgoAAAANSUhEUgAAAA0AAAATCAYAAABLN4eXAAAAAXNSR0IArs4c6QAAAAZiS0dEAP8A/wD/oL2nkwAAAAlwSFlzAAALEwAACxMBAJqcGAAAAAd0SU1FB9oIEQEjMtAYogQAAAKvSURBVCjPlZLLbhxFAEVPVVdXVz/G8zCOn0CsKGyQkSIIKzas8xfsWbLkp/gJhCKheIlAJDaj2MYez6u7p7vrxQKUPVc6+yOdK77/4cfXQohJqlOVZdmBSpKY6jQKBM45oVMlgHvrvMuNWRljvlNKq69G2YyqLDg4mLE/2yPNYFRWlFXF/nTC2clRWbc7Fss1IcZzqTA8eWY5eu7p1Hv+WvyBVjnGZOQmI9UKISUqSXDO0bS7Tko0xfGSp18kjM7v+P3+NUMr8T5grWMYLCEErHM474khoCw1t78eU/8mEOpjXpxekJUORIZSCbkxSCnRWpPnBikTqbx31E1DjJHpeIzRhnW9xceI857H5Yr1Zku765jf3DIMtlUAIQRCiFhnabsOH1IEAmstAGWRY11ApykmM0oplTKZjNGZREpJoUueHI0ZFRV7exX7+1Nm0yn9YLm5u2fX96lUseLwxQ0vX8H04i2/XP9Et5H44OkHS920hBDo+56u77GDjcrHjvV1ya3TDO2M01mOUAEAhED+R5IkpKmCiFCOjoc/p+xuLbPpCc+P95HaEqIBIhHoB8t2W/PwsKBudl5FH7GxwUYYouJh5ci7nLbtWW02LBaPvLuef1AdrItKKolJpkivwGrG5QxTCsq8pCxLqqrk7PiIwTmW6y0xRCVTSg4vFnz+raM4+5ur1RtSUZHnOUWeMx5VVFWJTlOstfTWRuk96NIyOUgRRc188RZvgRg/3OffjoFESohxUMvmjqufP+X+MqDTU77+5EvMKKBUQpZpijxHSkluDHvjMW8uL79Rnz07bwSyzDLFqCzwDNw/PNI0O9bbhvVmQ7vb0bQdi+Wq327rl+rko8krodKnCHnofJju+r5oupBstg1KJT7Vuruev185O9zVm/WVUmouYoz83/0DxhRmafe2kasAAAAASUVORK5CYII="
    ))}
        ),
    xdmp:document-insert(
        "/sample/databases-icon-mini.xhtml",
    <xhtml:html xmlns:xhtml="http://www.w3.org/1999/xhtml">
      <xhtml:head>
        <xhtml:meta name="content-type" content="image/png"/>
        <xhtml:meta name="filter-capabilities" content="none"/>
        <xhtml:meta name="size" content="815"/>
        <xhtml:meta name="MarkLogic_Binary_Source" content="/sample/databases-icon-mini.png"/>
      </xhtml:head>
    </xhtml:html>
    ),
    xdmp:document-set-permissions("/sample/databases-icon-mini.png",
        (xdmp:permission("rest-reader","read"),
        xdmp:permission("rest-writer","update"),
        xdmp:permission("app-user","read"),
        xdmp:permission("app-user","update"))
        ),
    xdmp:document-set-permissions("/sample/databases-icon-mini.xhtml",
        (xdmp:permission("rest-reader","read"),
        xdmp:permission("rest-writer","update"),
        xdmp:permission("app-user","read"),
        xdmp:permission("app-user","update"))
        ),
    xdmp:document-insert(
        "/sample/metadata-separate.xml",
        <metadata-target/>
        ),
    xdmp:document-set-permissions("/sample/metadata-separate.xml",
        (xdmp:permission("rest-reader","read"),
        xdmp:permission("rest-writer","update"),
        xdmp:permission("app-user","read"),
        xdmp:permission("app-user","update"))
        ),
    xdmp:document-insert(
        "/sample/metadata-all.xml",
        <metadata-target/>
        ),
    xdmp:document-set-permissions("/sample/metadata-all.xml",
        (xdmp:permission("rest-reader","read"),
        xdmp:permission("rest-writer","update"),
        xdmp:permission("app-user","read"),
        xdmp:permission("app-user","update"))
        ),
    xdmp:document-insert(
        "/sample/metadata-separate2.xml",
        <metadata-target/>
        ),
    xdmp:document-set-permissions("/sample/metadata-separate2.xml",
        (xdmp:permission("rest-reader","read"),
        xdmp:permission("rest-writer","update"),
        xdmp:permission("app-user","read"),
        xdmp:permission("app-user","update"))
        ),
    xdmp:document-insert(
        "/sample/metadata-all2.xml",
        <metadata-target/>
        ),
    xdmp:document-set-permissions("/sample/metadata-all2.xml",
        (xdmp:permission("rest-reader","read"),
        xdmp:permission("rest-writer","update"),
        xdmp:permission("app-user","read"),
        xdmp:permission("app-user","update"))
        ),
    xdmp:document-insert(
        "/sample/metadata-all3.xml",
        <metadata-target/>
        ),
    xdmp:document-set-permissions("/sample/metadata-all3.xml",
        (xdmp:permission("rest-reader","read"),
        xdmp:permission("rest-writer","update"),
        xdmp:permission("app-user","read"),
        xdmp:permission("app-user","update"))
        ),
    xdmp:document-insert(
        "/sample/metadata-all4.xml",
        <metadata-target/>
        ),
    xdmp:document-set-permissions("/sample/metadata-all4.xml",
        (xdmp:permission("rest-reader","read"),
        xdmp:permission("rest-writer","update"),
        xdmp:permission("app-user","read"),
        xdmp:permission("app-user","update"))
        ),
    xdmp:document-insert(
        "/sample/metadata-separate-json.xml",
        <json:json type="object"/>
        ),
    xdmp:document-set-permissions("/sample/metadata-separate-json.xml",
        (xdmp:permission("rest-reader","read"),
        xdmp:permission("rest-writer","update"),
        xdmp:permission("app-user","read"),
        xdmp:permission("app-user","update"))
        ),
    xdmp:document-insert(
        "/sample/metadata-all-json.xml",
        <json:json type="object"/>
        ),
    xdmp:document-set-permissions("/sample/metadata-all-json.xml",
        (xdmp:permission("rest-reader","read"),
        xdmp:permission("rest-writer","update"),
        xdmp:permission("app-user","read"),
        xdmp:permission("app-user","update"))
        ),
    xdmp:document-insert(
        "/sample/metadata-separate-json2.xml",
        <json:json type="object"/>
        ),
    xdmp:document-set-permissions("/sample/metadata-separate-json2.xml",
        (xdmp:permission("rest-reader","read"),
        xdmp:permission("rest-writer","update"),
        xdmp:permission("app-user","read"),
        xdmp:permission("app-user","update"))
        ),
    xdmp:document-insert(
        "/sample/metadata-all-json2.xml",
        <json:json type="object"/>
        ),
    xdmp:document-set-permissions("/sample/metadata-all-json2.xml",
        (xdmp:permission("rest-reader","read"),
        xdmp:permission("rest-writer","update"),
        xdmp:permission("app-user","read"),
        xdmp:permission("app-user","update"))
        ),
    xdmp:document-insert(
        "/sample/metadata-all-json3.xml",
        <json:json type="object"/>
        ),
    xdmp:document-set-permissions("/sample/metadata-all-json3.xml",
        (xdmp:permission("rest-reader","read"),
        xdmp:permission("rest-writer","update"),
        xdmp:permission("app-user","read"),
        xdmp:permission("app-user","update"))
        ),
    xdmp:document-set-properties(
        "/sample/prop-only.xml",
        (
            <sample-property>first</sample-property>,
            <container-property><name>alpha</name><number>1</number></container-property>
            )
        ),
    xdmp:document-set-properties(
        "/sample/prop-only.json",
        (
            <json:sample-property type="string">third</json:sample-property>,
            <json:container-property type="object">
                <json:name type="string">gamma</json:name>
                <json:number type="number">3</json:number>
            </json:container-property>
            )
        ),
    xdmp:document-insert(
        "/sample/invalid-separate.xml",
        <metadata-target/>
        ),
    xdmp:document-set-permissions("/sample/invalid-separate.xml",
        (xdmp:permission("rest-reader","read"),
        xdmp:permission("rest-writer","update"),
        xdmp:permission("app-user","read"),
        xdmp:permission("app-user","update"))
        ),
    xdmp:document-insert(
        "/sample/invalid-all.xml",
        <metadata-target/>
        ),
    xdmp:document-set-permissions("/sample/invalid-all.xml",
        (xdmp:permission("rest-reader","read"),
        xdmp:permission("rest-writer","update"),
        xdmp:permission("app-user","read"),
        xdmp:permission("app-user","update"))
        ),
    xdmp:document-insert(
        "/sample/invalid-separate-json.xml",
        <json:json type="object"/>
        ),
    xdmp:document-set-permissions("/sample/invalid-separate-json.xml",
        (xdmp:permission("rest-reader","read"),
        xdmp:permission("rest-writer","update"),
        xdmp:permission("app-user","read"),
        xdmp:permission("app-user","update"))
        ),
    xdmp:document-insert(
        "/sample/invalid-all-json.xml",
        <json:json type="object"/>
        ),
    xdmp:document-set-permissions("/sample/invalid-all-json.xml",
        (xdmp:permission("rest-reader","read"),
        xdmp:permission("rest-writer","update"),
        xdmp:permission("app-user","read"),
        xdmp:permission("app-user","update"))
        ),
    xdmp:document-insert(
        "/sample/lexicon-test1.xml",
        <root name="first">
            <child id="1">
                <grandchild>grandchild1</grandchild>
            </child>
            <child id="2">
                <grandchild>grandchild2</grandchild>
            </child>
            <child id="3">
                <grandchild>grandchild3</grandchild>
            </child>
        </root>
        ),
    xdmp:document-add-collections("/sample/lexicon-test1.xml",("http://some.org/collection1","http://some.org/collection2")),
    xdmp:document-set-permissions("/sample/lexicon-test1.xml",
        (xdmp:permission("rest-reader","read"),
        xdmp:permission("rest-writer","update"),
        xdmp:permission("app-user","read"),
        xdmp:permission("app-user","update"))
        ),
    xdmp:document-set-properties(
        "/sample/lexicon-test1.xml",
        (
            <sample-property>lexicon-test</sample-property>,
            <container-property><name>alpha</name><number>1</number></container-property>
            )
        ),
    xdmp:document-insert(
        "/sample/lexicon-test2.xml",
        <root name="first">
            <child id="1">
                <grandchild>grandchild4</grandchild>
            </child>
            <child id="2">
                <grandchild>grandchild5</grandchild>
            </child>
            <child id="3">
                <grandchild>grandchild6</grandchild>
            </child>
        </root>
        ),
    xdmp:document-add-collections("/sample/lexicon-test2.xml",("http://some.org/collection1","http://some.org/collection2")),
    xdmp:document-set-permissions("/sample/lexicon-test2.xml",
        (xdmp:permission("rest-reader","read"),
        xdmp:permission("rest-writer","update"),
        xdmp:permission("app-user","read"),
        xdmp:permission("app-user","update"))
        ),
    xdmp:document-set-properties(
        "/sample/lexicon-test2.xml",
        (
            <sample-property>lexicon-test</sample-property>,
            <container-property><name>alpha</name><number>1</number></container-property>
            )
        ),
    xdmp:document-insert(
        "/sample/lexicon-test3.xml",
        <root name="first">
            <child id="1">
                <double>1.2</double>
                <int>10</int>
            </child>
            <child id="2">
                <double>2.3</double>
                <int>20</int>
            </child>
        </root>
        ),
    xdmp:document-set-permissions("/sample/lexicon-test3.xml",
        (xdmp:permission("rest-reader","read"),
        xdmp:permission("rest-writer","update"),
        xdmp:permission("app-user","read"),
        xdmp:permission("app-user","update"))
        ),
    xdmp:document-insert(
        "/sample/lexicon-test4.xml",
        <root name="second">
            <child id="3">
                <double>2.2</double>
                <int>30</int>
            </child>
            <child id="5">
                <double>1.2</double>
                <int>10</int>
            </child>
             <child id="6">
                <double>1.2</double>
                <int>4</int>
            </child>
        </root>
        ),
    xdmp:document-set-permissions("/sample/lexicon-test4.xml",
        (xdmp:permission("rest-reader","read"),
        xdmp:permission("rest-writer","update"),
        xdmp:permission("app-user","read"),
        xdmp:permission("app-user","update"))
        ),
    xdmp:document-insert(
        "/sample/udf-test.xml",
        <root name="first">
            <element-test attribute-test="1"/>
            <element-test attribute-test="5"/>
            <element-test attribute-test="9"/>
        </root>),
xdmp:document-insert(
    "/sample/tuples-test1.xml",
    <root name="first">
        <child id="1">
            <double>1.1</double>
            <int>1</int>
            <string>Alaska</string>
        </child>
    </root>
    ),
xdmp:document-set-permissions("/sample/tuples-test1.xml",
    (xdmp:permission("rest-reader","read"),
    xdmp:permission("rest-writer","update"),
    xdmp:permission("app-user","read"),
    xdmp:permission("app-user","update"))
    ),
xdmp:document-insert(
    "/sample/tuples-test2.xml",
    <root name="second">
        <child id="3">
            <double>1.1</double>
            <int>3</int>
            <string>Birmingham</string>
        </child>
    </root>
    ),
xdmp:document-set-permissions("/sample/tuples-test2.xml",
    (xdmp:permission("rest-reader","read"),
    xdmp:permission("rest-writer","update"),
    xdmp:permission("app-user","read"),
    xdmp:permission("app-user","update"))
    ),
xdmp:document-insert(
    "/sample/tuples-test3.xml",
    <root name="second">
        <child id="3">
            <double>1.2</double>
            <int>3</int>
            <string>Birmingham</string>
        </child>
    </root>
    ),
xdmp:document-set-permissions("/sample/tuples-test3.xml",
    (xdmp:permission("rest-reader","read"),
    xdmp:permission("rest-writer","update"),
    xdmp:permission("app-user","read"),
    xdmp:permission("app-user","update"))
    ),
xdmp:document-insert(
    "/sample/tuples-test4.xml",
    <root name="second">
        <child id="3">
            <double>1.2</double>
            <int>3</int>
            <string>Alaska</string>
        </child>
    </root>
    ),
xdmp:document-set-permissions("/sample/tuples-test4.xml",
    (xdmp:permission("rest-reader","read"),
    xdmp:permission("rest-writer","update"),
    xdmp:permission("app-user","read"),
    xdmp:permission("app-user","update"))
    ),
    xdmp:document-set-permissions("/sample/udf-test.xml",
        (xdmp:permission("rest-reader","read"),
        xdmp:permission("rest-writer","update"),
        xdmp:permission("app-user","read"),
        xdmp:permission("app-user","update"))
        ),
    xdmp:document-insert(
        "/sample/legacy space file.xml",
        <samplelegacy>document</samplelegacy>
        ),
    xdmp:document-set-permissions("/sample/legacy space file.xml",
        (xdmp:permission("rest-reader","read"),
        xdmp:permission("rest-writer","update"))
        ),

    xdmp:document-insert("/sample/suggestion.xml",
        <suggest><string>FINDME</string>Something I love to suggest is sugar with savory succulent limes.</suggest>),
    xdmp:document-set-permissions("/sample/suggestion.xml",
        (xdmp:permission("rest-reader","read"),
        xdmp:permission("rest-writer","update"))
        ),
    xdmp:document-add-collections("/sample/suggestion.xml",("http://some.org/complexsuggestions")),
    xdmp:document-insert("/sample2/suggestion.xml",
        <suggest>Something I hate to suggest is liver with lard.</suggest>),
    xdmp:document-set-permissions("/sample2/suggestion.xml",
        (xdmp:permission("rest-reader","read"),
        xdmp:permission("rest-writer","update"))
            ),
    xdmp:document-add-collections("/sample2/suggestion.xml",("http://some.org/suggestions")),

    xdmp:document-insert(
    "/optic/test/musician1.json",
    xdmp:unquote(string(<t>{{"musician":{{
        "lastName":"Armstrong", "firstName":"Louis", "dob":"1901-08-04", "instrument":["trumpet", "vocal"]
        }}}}</t>)),
    (xdmp:permission("rest-reader","read"), xdmp:permission("rest-writer","update")),
    ("/optic/test", "/optic/music")
    ),
    xdmp:document-insert(
    "/optic/test/musician2.json",
    xdmp:unquote(string(<t>{{"musician":{{
        "lastName":"Byron", "firstName":"Don", "dob":"1958-11-08", "instrument":["clarinet", "saxophone"]
        }}}}</t>)),
    (xdmp:permission("rest-reader","read"), xdmp:permission("rest-writer","update")),
    ("/optic/test", "/optic/music")
    ),
    xdmp:document-insert(
    "/optic/test/musician3.json",
    xdmp:unquote(string(<t>{{"musician":{{
        "lastName":"Coltrane", "firstName":"John", "dob":"1926-09-23", "instrument":["saxophone"]
        }}}}</t>)),
    (xdmp:permission("rest-reader","read"), xdmp:permission("rest-writer","update")),
    ("/optic/test", "/optic/music")
    ),
    xdmp:document-insert(
    "/optic/test/musician4.json",
    xdmp:unquote(string(<t>{{"musician":{{
        "lastName":"Davis", "firstName":"Miles", "dob":"1926-05-26", "instrument":["trumpet"]
        }}}}</t>)),
    (xdmp:permission("rest-reader","read"), xdmp:permission("rest-writer","update")),
    ("/optic/test", "/optic/music")
    )
'
    )
};

declare function bootstrap:appserver-config()
{
    let $config   := admin:get-configuration()
    let $groupid  := admin:group-get-id($config, "Default")
    let $serverid := admin:appserver-get-id($config, $groupid, "java-unittest")
    return $config
        => admin:appserver-set-distribute-timestamps($serverid,"cluster")
        => admin:save-configuration-without-restart()
};

declare function bootstrap:post(
    $context as map:map,
    $params  as map:map,
    $input as document-node()*
) as document-node()*
{
    let $responses := (
        let $dbid := xdmp:database("java-unittest")
        return (
            bootstrap:security-config(),
            bootstrap:schema-config(),
            bootstrap:database-configure($dbid),
            xdmp:log(concat("Configured Java test database:", xdmp:database-name($dbid)))
            ),

        bootstrap:load-modules-db(),
        bootstrap:temporal-setup(),
        bootstrap:load-data(),

        let $is-balanced := (map:get($params,"balanced") eq "true")
        return
            if (not($is-balanced)) then ()
            else bootstrap:appserver-config()
        )
    return
        if (empty($responses)) then  ()
        else (
            document{<responses>{
                for $response in $responses
                return <response>{$response}</response>
            }</responses>},
            admin:save-configuration(admin:get-configuration())
            )
};
