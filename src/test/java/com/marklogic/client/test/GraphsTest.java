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
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.InputStream;
import java.util.Iterator;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.ForbiddenUserException;
import com.marklogic.client.ResourceNotFoundException;
import com.marklogic.client.Transaction;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.io.marker.ContentHandle;
import com.marklogic.client.io.marker.ContentHandleFactory;
import com.marklogic.client.semantics.GraphManager;
import com.marklogic.client.semantics.RDFMimeTypes;

public class GraphsTest {
    private static GraphManager gmgr;
    @BeforeClass
    public static void beforeClass() {
        // implementing a custom handle is the only way currently to set specific
        // mime types on the writeAs, mergeAs, and readAs methods
        DatabaseClientFactory.getHandleRegistry().register(NTriplesHandleForTesting.newFactory());
        Common.connect();
        gmgr = Common.client.newGraphManager();
        //System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.wire", "debug");

    }

    @AfterClass
    public static void afterClass() {
        Common.release();
        DatabaseClientFactory.getHandleRegistry().register(StringHandle.newFactory());
    }

    @Test
    public void testListUris() throws Exception {
        Iterator<String> iter = gmgr.listGraphUris();
        while ( iter.hasNext() ) {
            String uri = iter.next();
            assertNotNull(uri);
            assertNotEquals("", uri);
            System.out.println("DEBUG: [GraphsTest] uri =[" + uri  + "]");
        }
    }

    @Test
    public void testQuads() throws Exception {
        String quadGraphUri = "http://example.org/g1";
        String quad1 = "<http://example.org/s1> <http://example.com/p1> <http://example.org/o1> <http://example.org/g1>.";
        String quad2 = "<http://example.org/s2> <http://example.com/p2> <http://example.org/o2> <http://example.org/g1>.";
        String quad3 = "<http://example.org/s3> <http://example.com/p2> <http://example.org/o2> <http://example.org/g1>.";
        String quad4 = "<http://example.org/s4> <http://example.com/p2> <http://example.org/o2> <http://example.org/g1>.";
        GraphManager gmgr = Common.client.newGraphManager();
        gmgr.replaceGraphs(new StringHandle(quad1).withMimetype("application/n-quads"));
        StringHandle quadsHandle = gmgr.read(quadGraphUri,
            new StringHandle().withMimetype(RDFMimeTypes.NQUADS));
        assertEquals(quad1, quadsHandle.get());

        gmgr.mergeGraphs(new StringHandle(quad2).withMimetype("application/n-quads"));
        StringHandle quads1and2 = gmgr.read(quadGraphUri,
            new StringHandle().withMimetype(RDFMimeTypes.NQUADS));
        assertEquals(quad1 + "\n" + quad2, quads1and2.get());

        gmgr.replaceGraphsAs(quad3);
        quadsHandle = gmgr.read(quadGraphUri,
            new StringHandle().withMimetype(RDFMimeTypes.NQUADS));
        assertEquals(quad3, quadsHandle.get());

        gmgr.mergeGraphsAs(quad4);
        StringHandle quads3and4 = gmgr.read(quadGraphUri,
                new StringHandle().withMimetype(RDFMimeTypes.NQUADS));
        assertEquals(quad3 + "\n" + quad4, quads3and4.get());

        gmgr.delete(quadGraphUri);
    }

    @Test
    public void testTriples() throws Exception {
        GraphManager gmgr = Common.client.newGraphManager();
        String tripleGraphUri = "http://example.org/g2";
        String ntriple5 = "<http://example.org/s5> <http://example.com/p2> <http://example.org/o2> .";
        String ntriple6 = "<http://example.org/s6> <http://example.com/p2> <http://example.org/o2> .";
        String ntriple7 = "<http://example.org/s7> <http://example.com/p2> <http://example.org/o2> .";
        String ntriple8 = "<http://example.org/s8> <http://example.com/p2> <http://example.org/o2> .";
        gmgr.write(tripleGraphUri, new StringHandle(ntriple5).withMimetype(RDFMimeTypes.NTRIPLES));
        StringHandle triplesHandle = gmgr.read(tripleGraphUri,
            new StringHandle().withMimetype(RDFMimeTypes.NTRIPLES));
        assertEquals(ntriple5, triplesHandle.get());

        gmgr.write(tripleGraphUri, new StringHandle(ntriple5 + "\n" + ntriple6)
            .withMimetype(RDFMimeTypes.NTRIPLES));
        String triples5and6 = gmgr.readAs(tripleGraphUri, String.class);
        // I expect n-triples format String.class is currently handled by my custom NTriplesHandleForTesting
        String expected = new String(ntriple5 + "\n" + ntriple6);
        assertEquals(expected, triples5and6);

        gmgr.merge(tripleGraphUri, new StringHandle(ntriple7).withMimetype(RDFMimeTypes.NTRIPLES));
        StringHandle triples5and6and7 = gmgr.read(tripleGraphUri,
            new StringHandle().withMimetype(RDFMimeTypes.NTRIPLES));
        assertEquals(ntriple5 + "\n" + ntriple6 + "\n" + ntriple7, triples5and6and7.get());

        gmgr.writeAs(tripleGraphUri, ntriple7);
        String triples7 = gmgr.readAs(tripleGraphUri, String.class);
        assertEquals(ntriple7, triples7);

        gmgr.mergeAs(tripleGraphUri, ntriple8);
        String triples7and8 = gmgr.readAs(tripleGraphUri, String.class);
        assertEquals(ntriple7 + "\n" + ntriple8, triples7and8);

        gmgr.delete(tripleGraphUri);
        // ensure it's gone
        try {
            gmgr.read(tripleGraphUri, new StringHandle());
            fail("read non-existent graph should throw ResourceNotFoundException");
        } catch (ResourceNotFoundException e) {
            // pass
        }
    }
    public static class NTriplesHandleForTesting extends StringHandle {
        public String getMimetype() { return RDFMimeTypes.NTRIPLES; }

        public static ContentHandleFactory newFactory() {
            return new ContentHandleFactory() {
                @SuppressWarnings("unchecked")
                public <C> ContentHandle<C> newHandle(Class<C> type) {
                    return (ContentHandle<C>) new NTriplesHandleForTesting();
                }

                public Class<?>[] getHandledClasses() {
                    return new Class<?>[]{ String.class };
                }

                public boolean isHandled(Class<?> type) {
                    return String.class.isAssignableFrom(type);
                }
            };
        }
    }

    @Test
    public void testTransactions() {
        GraphManager graphManagerWriter = Common.client.newGraphManager();
        DatabaseClient readOnlyClient = DatabaseClientFactory.newClient(
                Common.HOST, Common.PORT, "rest-reader", "x",
                Authentication.DIGEST);
        GraphManager graphManagerReader = readOnlyClient.newGraphManager();
        String t1 = "<s1> <p1> <o1> .";
        String t2 = "<s2> <p2> <o2> .";
        try {
            graphManagerReader.write("thisFails", new StringHandle().with(t1)
                    .withMimetype(RDFMimeTypes.NTRIPLES.toString()));
            fail("reader could write a graph.");
        } catch (ForbiddenUserException e) {
            // pass
        }
        
        // write in a transaction
        Transaction tx = null;
        try {
            tx = Common.client.openTransaction();
            graphManagerWriter.write("newGraph", new StringHandle().with(t1)
                    .withMimetype(RDFMimeTypes.NTRIPLES.toString()), tx);
            // reader can't see it
            try {
                graphManagerReader.read(
                        "newGraph",
                        new StringHandle());
                fail("reader could read graph outside transaction");
            } catch (ResourceNotFoundException e) {
                // pass
            }

            tx.rollback();
            tx = null;

            // doesn't exist 
            try {
                graphManagerWriter.read(
                        "newGraph",
                        new StringHandle());
                fail("graph was written despite rollback");
            } catch (ResourceNotFoundException e) {
                // pass
            }
            
            // new tx
            tx = Common.client.openTransaction();
            // write a graph in transaction
            graphManagerWriter.write("newGraph", new StringHandle().with(t1)
                    .withMimetype(RDFMimeTypes.NTRIPLES), tx);

//            graphManagerWriter.merge("newGraph",  new StringHandle().with(t2)
//                    .withMimetype(RDFMimeTypes.NTRIPLES), tx);

            tx.commit();
            tx = null;
            // graph is now there.  No failure.
            String mergedGraph = graphManagerWriter.read(
                    "newGraph",
                    new StringHandle().withMimetype(RDFMimeTypes.NTRIPLES)).get();
            // TODO, merge is not implemented yet.
            // assertEquals(t1 + t2, mergedGraph);
            // reader cannot delete
            try {
                graphManagerReader.delete("newGraph");
                fail("Reader could delete graph");
            } catch (ForbiddenUserException e) {
                //pass
            }

            // new transaction
            tx = Common.client.openTransaction();
            // write a graph in transaction
            graphManagerWriter.delete("newGraph", tx);
            tx.commit();
            tx = null;

            // must be gone.
            try {
                graphManagerWriter.read(
                        "newGraph",
                        new StringHandle());
                fail("graph was written despite rollback");
            } catch (ResourceNotFoundException e) {
                // pass
            }
        } finally {
            if (tx != null) {
                tx.rollback();
                tx = null;
            }
            // always try to delete graph
            try {
                graphManagerWriter.delete(
                        "newGraph");
            } catch (Exception e) {
                // nop
            }

        }
    }
}
