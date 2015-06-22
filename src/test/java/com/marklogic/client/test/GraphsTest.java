/*
 * Copyright 2012-2015 MarkLogic Corporation
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

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.marklogic.client.io.StringHandle;
import com.marklogic.client.io.marker.TriplesReadHandle;
import com.marklogic.client.io.marker.TriplesWriteHandle;
import com.marklogic.client.semantics.GraphManager;

public class GraphsTest {
    @BeforeClass
    public static void beforeClass() {
        Common.connect();
    }

    @AfterClass
    public static void afterClass() {
        Common.release();
    }

    @Test
    public void testQuads() throws Exception {
        String quadGraphUri = "http://example.org/g1";
        String quad1 = "<http://example.org/s1> <http://example.com/p1> <http://example.org/o1> <http://example.org/g1> .";
        String quad2 = "<http://example.org/s2> <http://example.com/p2> <http://example.org/o2> <http://example.org/g2> .";
        String quad3 = "<http://example.org/s3> <http://example.com/p2> <http://example.org/o2> <http://example.org/g2> .";
        String quad4 = "<http://example.org/s4> <http://example.com/p2> <http://example.org/o2> <http://example.org/g2> .";
        GraphManager gmgr = Common.client.newGraphManager();
        gmgr.replaceGraphs(new StringHandle(quad1));
        StringHandle triplesHandle = gmgr.read(quadGraphUri, new StringHandle());
        gmgr.mergeGraphs(new StringHandle(quad2));
        triplesHandle = gmgr.read(quadGraphUri, new StringHandle());
        gmgr.replaceGraphsAs(quad3);
        String triples = gmgr.readAs(quadGraphUri, String.class);
        gmgr.mergeGraphsAs(quad4);
        triples = gmgr.readAs(quadGraphUri, String.class);
        gmgr.delete(quadGraphUri);
        triples = gmgr.readAs(quadGraphUri, String.class);
        assertEquals(null, triples);
    }

    public void testTriples() throws Exception {
        GraphManager gmgr = Common.client.newGraphManager();
        String tripleGraphUri = "http://example.org/g2";
        String ntriple5 = "<http://example.org/s5> <http://example.com/p2> <http://example.org/o2> .";
        String ntriple6 = "<http://example.org/s6> <http://example.com/p2> <http://example.org/o2> .";
        String ntriple7 = "<http://example.org/s7> <http://example.com/p2> <http://example.org/o2> .";
        String ntriple8 = "<http://example.org/s8> <http://example.com/p2> <http://example.org/o2> .";
        gmgr.write(tripleGraphUri, new StringHandle(ntriple5));
        StringHandle triplesHandle = gmgr.read(tripleGraphUri, new StringHandle());
        gmgr.merge(tripleGraphUri, new StringHandle(ntriple6));
        triplesHandle = gmgr.read(tripleGraphUri, new StringHandle());
        gmgr.writeAs(tripleGraphUri, ntriple7);
        String triples = gmgr.readAs(tripleGraphUri, String.class);
        gmgr.mergeAs(tripleGraphUri, ntriple8);
        triples = gmgr.readAs(tripleGraphUri, String.class);
        gmgr.delete(tripleGraphUri);
        triples = gmgr.readAs(tripleGraphUri, String.class);
        assertEquals(null, triples);
    }
}
