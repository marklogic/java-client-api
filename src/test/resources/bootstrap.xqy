xquery version "1.0-ml";

module namespace bootstrap = "http://marklogic.com/rest-api/resource/bootstrap";
(: Copyright 2002-2010 Mark Logic Corporation.  All Rights Reserved. :)

import module namespace admin = "http://marklogic.com/xdmp/admin" at "/MarkLogic/admin.xqy";

declare namespace dbx = "http://marklogic.com/xdmp/database";

declare default function namespace "http://www.w3.org/2005/xpath-functions";
declare option xdmp:mapping "false";

declare function bootstrap:database-configure(
  $dbid as xs:unsignedLong)
as empty-sequence()
{
    let $c := admin:get-configuration()
    
    (: collection and word lexicons and triple index on :)
    let $c := admin:database-set-collection-lexicon($c, $dbid, true())
    let $c := admin:database-set-maintain-last-modified($c, $dbid, true())
    let $c := admin:database-set-triple-index($c, $dbid, true())

    let $index-specs :=
        let $curr-idx := admin:database-get-range-element-indexes($c, $dbid)
        let $new-idx  := (
            "dateTime", "http://marklogic.com/xdmp/property", "last-modified",
            "string",   "http://nwalsh.com/ns/photolib",      "tag",
            "date",     "http://nwalsh.com/ns/photolib",      "date",
            "double",   "",                                   "double",
            "int",      "",                                   "int",
            "string",   "",                                   "grandchild",
            "string",   "",                                   "string"
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
            if (exists($curr)) then ()
            else admin:database-range-element-index(
                $datatype, $ns, $name, $collation, false()
                )

    let $c :=
        if (empty($index-specs)) then $c
        else admin:database-add-range-element-index($c, $dbid, $index-specs)

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
            if (exists($curr)) then ()
            else admin:database-range-element-attribute-index(
                $datatype, $e-ns, $e-name, $a-ns, $a-name, $collation, false()
                )
    let $c :=
        if (empty($index-specs)) then $c
        else admin:database-add-range-element-attribute-index($c, $dbid, $index-specs)

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
            if (exists($curr)) then ()
            else admin:database-element-word-lexicon($ns, $name, $collation)
    let $c :=
        if (empty($index-specs)) then $c
        else admin:database-add-element-word-lexicon($c, $dbid, $index-specs)

    (: create geospatial indexes for geo unit test :)
    let $index-specs :=
        let $curr-idx := admin:database-get-geospatial-element-pair-indexes($c, $dbid)
        let $p := "http://marklogic.com/ns/test/places"
        let $new-idx  := (
            $p, "place", $p, "lat", $p, "long", "wgs84"
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
            if (exists($curr)) then ()
            else admin:database-geospatial-element-pair-index(
                $p-ns, $p-name, $lat-ns, $lat-name, $lon-ns, $lon-name, $coord-sys, false()
                )
    let $c :=
        if (empty($index-specs)) then $c
        else admin:database-add-geospatial-element-pair-index($c, $dbid, $index-specs)

    let $def-specs :=
        let $curr-def := admin:database-get-fields($c, $dbid)
        for $new-def in ("int1", "int2")
        let $curr := $curr-def[
            string(dbx:field-name) eq $new-def
            ]
        return
            if (exists($curr)) then ()
            else admin:database-field($new-def, false())
    let $c :=
        if (empty($def-specs)) then $c
        else (
            (: you can't create field and field range index in same transaction :)
            admin:save-configuration-without-restart(
                admin:database-add-field($c, $dbid, $def-specs)
                ),

            admin:get-configuration()
            )

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
            if (exists($curr)) then ()
            else admin:database-range-field-index(
                $datatype, $name, $collation, false()
                )
    let $c :=
        if (empty($index-specs)) then $c
        else admin:database-add-range-field-index($c, $dbid, $index-specs)

    let $index-specs := admin:database-range-path-index(
      $dbid, 
      "string",
      "com.marklogic.client.test.Country/continent",
      "http://marklogic.com/collation/",
      fn:false(),
      "ignore")
    let $c :=
        if (empty($index-specs)) then $c
        else admin:database-add-range-path-index($c, $dbid, $index-specs)

    let $index-specs := admin:database-range-path-index(
      $dbid, 
      "unsignedLong",
      "com.marklogic.client.test.City/population",
      (),
      fn:false(),
      "ignore")
    let $c :=
        if (empty($index-specs)) then $c
        else admin:database-add-range-path-index($c, $dbid, $index-specs)

    return admin:save-configuration-without-restart($c)
};

declare function bootstrap:security-config(
$command as xs:string
) 
{ 
    try {
        xdmp:eval(concat('xquery version "1.0-ml"; ',
                    'import module namespace sec="http://marklogic.com/xdmp/security" at  ',
                    '    "/MarkLogic/security.xqy"; ',
                    $command),
        (),
        <options xmlns="xdmp:eval">
            <database>{ xdmp:database("Security") }</database>
        </options>)
    } catch($e) {
        xdmp:log($e)
    }
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
    xdmp:document-add-collections("/sample2/suggestion.xml",("http://some.org/suggestions"))
'
    )
};

declare function bootstrap:load-search-data()
{
    ()
};

declare function bootstrap:post(
    $context as map:map,
    $params  as map:map,
    $input as document-node()*
) as document-node()*
{
    for $user in ("rest-admin", "rest-reader", "rest-writer", "valid") 
    let $user-id := 
        try {
            xdmp:user($user)
        } catch($e) {
            xdmp:log("User "||$user||" not found.")
        }
    return
        if (exists($user-id)) then xdmp:log("User "|| $user || ", id "||$user-id|| "already exists")
        else if ($user eq "valid")
        then bootstrap:security-config('sec:create-user("valid", "valid unprivileged user", "x", (), (), (), ())')
        else bootstrap:security-config('sec:create-user("'||$user||'", "'||$user||' user", "x", ("'||$user||'"), (), (), () )'),

    let $dbid := xdmp:database("java-unittest")
    return (
        bootstrap:database-configure($dbid),
        xdmp:log(concat("Configured Java test database:", xdmp:database-name($dbid)))
        ),

    bootstrap:load-data()
};
