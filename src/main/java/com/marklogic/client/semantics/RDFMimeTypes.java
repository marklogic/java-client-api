package com.marklogic.client.semantics;

import com.marklogic.client.io.marker.TriplesReadHandle;

/**
 * Some static constants to ease use of mime types appropriate for
 * handles of type {@link TriplesReadHandle}
 * (used in GraphManager.{@link GraphManager#read GraphManager.read}/
 * {@link GraphManager#write GraphManager.write}/
 * {@link GraphManager#merge GraphManager.merge} methods).  Using
 * SPARQLQueryManager and GraphManager all mimetypes can be written to or read
 * from the server except TRIG which can only be written to the server.
 */
public final class RDFMimeTypes {

    public final static String NTRIPLES  = "application/n-triples";
    public final static String TURTLE    = "text/turtle";
    public final static String N3        = "text/n3";
    public final static String RDFXML    = "application/rdf+xml";
    public final static String RDFJSON   = "application/rdf+json";
    public final static String NQUADS    = "application/n-quads";
    /* TRIG is only for writing to the server.  The server does not
     * support reading TRIG format.
     */
    public final static String TRIG      = "text/trig";
    public final static String TRIPLEXML = "application/vnd.marklogic.triples+xml";

}
