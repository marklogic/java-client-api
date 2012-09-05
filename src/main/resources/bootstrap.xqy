xquery version "1.0-ml";

module namespace bootstrap = "http://marklogic.com/rest-api/resource/bootstrap";
(: Copyright 2002-2010 Mark Logic Corporation.  All Rights Reserved. :)

import module namespace admin = "http://marklogic.com/xdmp/admin" at "/MarkLogic/admin.xqy";

declare default function namespace "http://www.w3.org/2005/xpath-functions";

declare variable $p := "http://marklogic.com/ns/test/places";

declare function bootstrap:database-configure(
  $dbid as xs:unsignedLong)
as empty-sequence()
{

	
    let $c := admin:get-configuration()

    (: collection and word lexicons on :)
    let $c := admin:database-set-collection-lexicon($c, $dbid, true())
    let $c := admin:database-set-maintain-last-modified($c, $dbid, fn:true())
    
    let $rangespec := admin:database-range-element-index("dateTime",
                                "http://marklogic.com/xdmp/property","last-modified","",false() )
    let $c := admin:database-add-range-element-index($c, $dbid, $rangespec)

    let $rangespec := admin:database-range-element-index("double",
        "","double","",false() )
    let $c := admin:database-add-range-element-index($c, $dbid, $rangespec)
    let $rangespec := admin:database-range-element-index("int",
        "","int","",false() )
    let $c := admin:database-add-range-element-index($c, $dbid, $rangespec)
    let $rangespec := admin:database-range-element-index("string",
        "","grandchild","http://marklogic.com/collation/",false() )
    let $c := admin:database-add-range-element-index($c, $dbid, $rangespec)

    (: create geospatial indexes for geo unit test :)
    let $geospec := admin:database-geospatial-element-pair-index(
                       $p, "place", $p, "lat", $p, "long", "wgs84", false())
    let $c := admin:database-add-geospatial-element-pair-index($c, $dbid, $geospec)

    let $f1 := admin:database-field("int1", false())
    let $f2 := admin:database-field("int2", false())
    let $c := admin:database-add-field($c, $dbid, $f1)
    let $c := admin:database-add-field($c, $dbid, $f2)

 
    return admin:save-configuration-without-restart($c),
    (: you can't create field and field range index in same transaction :)
    let $c := admin:get-configuration()
    let $fi1 := admin:database-range-field-index("int", "int1", "", fn:false() )
    let $fi2 := admin:database-range-field-index("int", "int2", "", fn:false() )
    let $c := admin:database-add-range-field-index($c, $dbid, ($fi1, $fi2))
    return admin:save-configuration-without-restart($c)
};

declare function bootstrap:security-config(
$command as xs:string
) 
{ 
    try {
        xdmp:eval(fn:concat('xquery version "1.0-ml"; ',
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
    let $ncre := bootstrap:security-config('sec:create-user("rest-admin","rest-admin user", "x",("rest-admin"),(),())')
    let $ncre := bootstrap:security-config('sec:create-user("rest-reader","rest-reader user", "x",("rest-reader"),(),())')
    let $ncre := bootstrap:security-config('sec:create-user("rest-writer","rest-writer user", "x",("rest-writer"),(),())')
    let $ncre := bootstrap:security-config('sec:create-user("valid","valid unprivileged user", "x",(),(),())')
    let $dbid := xdmp:database("java-unittest")
    let $config := bootstrap:database-configure($dbid)
    let $d1 := bootstrap:load-data()
    return ()
};

