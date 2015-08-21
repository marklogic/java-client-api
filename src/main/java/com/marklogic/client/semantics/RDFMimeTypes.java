package com.marklogic.client.semantics;


/**
 * Some static constants to ease use of mime types appropriate for
 * handles used in GraphManager.{@link GraphManager#read GraphManager.read}/
 * {@link GraphManager#write GraphManager.write}/
 * {@link GraphManager#merge GraphManager.merge} methods.
 */
public final class RDFMimeTypes {

    public final static String NTRIPLES  = "application/n-triples";
    public final static String TURTLE    = "text/turtle";
    public final static String N3        = "text/n3";
    public final static String RDFXML    = "application/rdf+xml";
    public final static String RDFJSON   = "application/rdf+json";
    public final static String NQUADS    = "application/n-quads";
    public final static String TRIG      = "text/trig";
    public final static String TRIPLEXML = "application/vnd.marklogic.triples+xml";

}
