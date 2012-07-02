/*
 * Copyright 2012 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.marklogic.client.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import com.marklogic.client.config.StructuredQueryBuilder;
import com.marklogic.client.config.StructuredQueryDefinition;

public class StructuredQueryBuilderTest {
    @Test
    public void testBuilder() throws IOException {
        StructuredQueryBuilder qb = new StructuredQueryBuilder(null);
        StructuredQueryDefinition t, u, v;

        // assertEquals("<query xmlns='http://marklogic.com/appservices/search'></query>", t.serialize());

        t = qb.and(qb.term("one"), qb.term("two"), qb.term("three"));
        assertEquals("<query xmlns='http://marklogic.com/appservices/search'><and-query><term-query><text>one</text></term-query><term-query><text>two</text></term-query><term-query><text>three</text></term-query></and-query></query>", t.serialize());

        u = qb.or(qb.term("one"), qb.term("two"), qb.term("three"));
        assertEquals("<query xmlns='http://marklogic.com/appservices/search'><or-query><term-query><text>one</text></term-query><term-query><text>two</text></term-query><term-query><text>three</text></term-query></or-query></query>", u.serialize());

        v = qb.or(t, u);
        assertEquals("<query xmlns='http://marklogic.com/appservices/search'>"
                + "<or-query>"
                + "<and-query><term-query><text>one</text></term-query><term-query><text>two</text></term-query><term-query><text>three</text></term-query></and-query>"
                + "<or-query><term-query><text>one</text></term-query><term-query><text>two</text></term-query><term-query><text>three</text></term-query></or-query>"
                + "</or-query></query>", v.serialize());

        t = qb.and(qb.term("one", "two", "three"));
        assertEquals("<query xmlns='http://marklogic.com/appservices/search'><and-query><term-query><text>one</text><text>two</text><text>three</text></term-query></and-query></query>", t.serialize());

        t = qb.and(qb.term(3.0, "one", "two", "three"));
        assertEquals("<query xmlns='http://marklogic.com/appservices/search'><and-query><term-query><text>one</text><text>two</text><text>three</text><weight>3.0</weight></term-query></and-query></query>", t.serialize());

        t = qb.not(qb.term("one"));
        assertEquals("<query xmlns='http://marklogic.com/appservices/search'><not-query><term-query><text>one</text></term-query></not-query></query>", t.serialize());

        t = qb.near(qb.term("one"));
        assertEquals("<query xmlns='http://marklogic.com/appservices/search'><near-query><term-query><text>one</text></term-query></near-query></query>", t.serialize());

        t = qb.near(4, 2.3, StructuredQueryBuilder.Ordering.UNORDERED, qb.term("two"));
        assertEquals("<query xmlns='http://marklogic.com/appservices/search'><near-query><term-query><text>two</text></term-query>"
                + "<ordered>false</ordered><distance>4</distance><distance-weight>2.3</distance-weight></near-query></query>", t.serialize());

        t = qb.term("leaf3");
        assertEquals("<query xmlns='http://marklogic.com/appservices/search'><term-query><text>leaf3</text></term-query></query>", t.serialize());

        t = qb.andNot(qb.term("one"), qb.term("two"));
        assertEquals("<query xmlns='http://marklogic.com/appservices/search'><and-not-query>"
                + "<term-query><text>one</text></term-query>"
                + "<term-query><text>two</text></term-query>"
                + "</and-not-query></query>", t.serialize());

        t = qb.documentFragment(qb.document("/some/uri.xml"));
        assertEquals("<query xmlns='http://marklogic.com/appservices/search'><document-fragment-query><document-query><uri>/some/uri.xml</uri></document-query></document-fragment-query></query>", t.serialize());

        t = qb.properties(qb.directory(false, "/dir1", "dir2"));
        assertEquals("<query xmlns='http://marklogic.com/appservices/search'><properties-query><directory-query><uri>/dir1</uri><uri>dir2</uri><infinite>false</infinite></directory-query></properties-query></query>", t.serialize());

        t = qb.locks(qb.term("one"));
        assertEquals("<query xmlns='http://marklogic.com/appservices/search'><locks-query><term-query><text>one</text></term-query></locks-query></query>", t.serialize());

        t = qb.elementConstraint("name", qb.term("one"));
        assertEquals("<query xmlns='http://marklogic.com/appservices/search'><element-constraint-query><constraint-name>name</constraint-name><term-query><text>one</text></term-query></element-constraint-query></query>", t.serialize());

        t = qb.propertiesConstraint("name", qb.term("one"));
        assertEquals("<query xmlns='http://marklogic.com/appservices/search'><properties-constraint-query><constraint-name>name</constraint-name><term-query><text>one</text></term-query></properties-constraint-query></query>", t.serialize());

        t = qb.collectionConstraint("name", "c1", "c2");
        assertEquals("<query xmlns='http://marklogic.com/appservices/search'><collection-constraint-query><constraint-name>name</constraint-name><uri>c1</uri><uri>c2</uri></collection-constraint-query></query>", t.serialize());

        t = qb.valueConstraint("name", "one");
        assertEquals("<query xmlns='http://marklogic.com/appservices/search'><value-constraint-query><constraint-name>name</constraint-name><text>one</text></value-constraint-query></query>", t.serialize());

        t = qb.valueConstraint("name", 2.0, "one");
        assertEquals("<query xmlns='http://marklogic.com/appservices/search'><value-constraint-query><constraint-name>name</constraint-name><text>one</text><weight>2.0</weight></value-constraint-query></query>", t.serialize());

        t = qb.wordConstraint("name", "one");
        assertEquals("<query xmlns='http://marklogic.com/appservices/search'><word-constraint-query><constraint-name>name</constraint-name><text>one</text></word-constraint-query></query>", t.serialize());

        t = qb.wordConstraint("name", 2.0, "one");
        assertEquals("<query xmlns='http://marklogic.com/appservices/search'><word-constraint-query><constraint-name>name</constraint-name><text>one</text><weight>2.0</weight></word-constraint-query></query>", t.serialize());

        t = qb.rangeConstraint("name", StructuredQueryBuilder.Operator.GE, "value1", "value2");
        assertEquals("<query xmlns='http://marklogic.com/appservices/search'><range-constraint-query><constraint-name>name</constraint-name><value>value1</value><value>value2</value><range-operator>GE</range-operator></range-constraint-query></query>", t.serialize());

        t = qb.geospatialConstraint("geo", qb.box(1, 2, 3, 4), qb.circle(0, 0, 100), qb.point(5, 6),
                qb.polygon(qb.point(1, 2), qb.point(2, 3), qb.point(3, 4), qb.point(4, 1)));

        assertEquals("<query xmlns='http://marklogic.com/appservices/search'>"
                + "<geospatial-constraint-query>"
                + "<constraint-name>geo</constraint-name>"
                + "<box><south>1.0</south><west>2.0</west><north>3.0</north><east>4.0</east></box>"
                + "<circle><radius>100.0</radius><point><latitude>0.0</latitude><longitude>0.0</longitude></point></circle>"
                + "<point><latitude>5.0</latitude><longitude>6.0</longitude></point>"
                + "<polygon><point><latitude>1.0</latitude><longitude>2.0</longitude></point>"
                + "<point><latitude>2.0</latitude><longitude>3.0</longitude></point>"
                + "<point><latitude>3.0</latitude><longitude>4.0</longitude></point>"
                + "<point><latitude>4.0</latitude><longitude>1.0</longitude></point>"
                + "</polygon></geospatial-constraint-query></query>",
                t.serialize());

        t = qb.customConstraint("name", "one", "two");
        assertEquals("<query xmlns='http://marklogic.com/appservices/search'><custom-constraint-query><constraint-name>name</constraint-name><text>one</text><text>two</text></custom-constraint-query></query>", t.serialize());
    }
}
