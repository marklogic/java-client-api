/*
 * Copyright 2014-2018 MarkLogic Corporation
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
