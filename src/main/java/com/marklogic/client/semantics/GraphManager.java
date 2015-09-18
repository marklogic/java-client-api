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
package com.marklogic.client.semantics;

import java.util.Iterator;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.FailedRequestException;
import com.marklogic.client.ForbiddenUserException;
import com.marklogic.client.ResourceNotFoundException;
import com.marklogic.client.Transaction;
import com.marklogic.client.io.BaseHandle;
import com.marklogic.client.io.marker.JSONWriteHandle;
import com.marklogic.client.io.marker.QuadsWriteHandle;
import com.marklogic.client.io.marker.TriplesReadHandle;
import com.marklogic.client.io.marker.TriplesWriteHandle;
import com.marklogic.client.io.marker.XMLWriteHandle;
import com.marklogic.client.semantics.Capability;

/**
 * <p>A manager for CRUD operations on semantic graphs which reside in
 * MarkLogic Server.</p>
 *
 * <p>For example:</p>
 *
 * <p>If you have a file called "example.nt" containing one triple in turtle
 * mimetype:</p>
 *
 * <pre>    &lt;http://example.org/subject1&gt; &lt;http://example.org/predicate1&gt; &lt;http://example.org/object1&gt; .</pre>
 *
 * <p>You could write it to the database in a graph called "myExample/graphUri"
 * like so:</p>
 *
 * <pre>    GraphManager graphMgr = databaseClient.newGraphManager();
 *    FileHandle fileHandle = new FileHandle(new File("example.nt")).withMimetype(RDFMimeTypes.NTRIPLES);
 *    graphMgr.write("myExample/graphUri", fileHandle);
 * </pre>
 *
 * <p>Then you could add another triple to the graph like so:</p>
 *
 * <pre>    StringHandle stringHandle = new StringHandle()
 *        .with("&lt;http://example.org/subject2&gt; &lt;http://example.org/predicate2&gt; &lt;http://example.org/object2&gt; .")
 *        .withMimetype(RDFMimeTypes.NTRIPLES);
 *    graphMgr.merge("myExample/graphUri", stringHandle);
 * </pre>
 *
 * <p>Then you read the graph from the database into turtle syntax into a
 * variable "triples" like so:</p>
 *
 * <pre>    String triples = graphMgr.read("myExample/graphUri",
 *        new StringHandle().withMimetype(RDFMimeTypes.NTRIPLES)).get();
 * </pre>
 *
 * <p>You could simplify these examples if you first set the default mimetype
 * so you don't have to call setMimetype on each handle.  That also enables you
 * to use the *As convenience methods:</p>
 *
 * <pre>    graphMgr.setDefaultMimetype(RDFMimeTypes.NTRIPLES);
 *    String triples = graphMgr.readAs("myExample/graphUri", String.class);
 * </pre>
 *
 * <p>If you need to limit access to a graph you can set the permissions:</p>
 *
 * <pre>    graphMgr.writePermissions("myExample/graphUri",
 *        graphMgr.permission("example_manager", Capability.READ)
 *                .permission("example_manager", Capability.UPDATE));
 * </pre>
 *
 * <p>Permissions can also be added with {@link #mergePermissions
 * mergePermissions}, deleted with {@link #deletePermissions
 * deletePermissions}, or set with calls to {@link #write(String,
 * TriplesWriteHandle, GraphPermissions) write} or {@link #merge(String,
 * TriplesWriteHandle, GraphPermissions) merge} to a graph by providing the
 * permissions argument.</p>
 *
 * <p>Each new instance of GraphManager is created by {@link
 * DatabaseClient#newGraphManager}.  While these examples use FileHandle and
 * StringHandle, any TriplesWriteHandle may be used, including custom handles.
 * While {@link JSONWriteHandle}s will need to use {@link RDFMimeTypes#RDFJSON}
 * mimetype, and {@link XMLWriteHandle}s will need to use {@link
 * RDFMimeTypes#RDFXML} mimetype, most {@link TriplesWriteHandle}s can write
 * any text mimteypt and can therefore write triples using any of the
 * RDFMimeTypes.</p>
 *
 * <p>GraphManager is thread-safe other than {@link #setDefaultMimetype
 * setDefaultMimetype}. In other words the only state maintained by an instance
 * is the default mimetype.  Common usage is to call setDefaultMimetype only
 * once then use the instance across many threads.  If you intend to call
 * setDefaultMimetype from multiple threads, create a new GraphManager for each
 * thread.</p>
 *
 * <p>For details about RDF, SPARQL, and semantics in MarkLogic see
 * <a href="https://docs.marklogic.com/guide/semantics" target="_top">Semantics Developer's Guide</a>
 */
public interface GraphManager {
    /** <p>Use with any GraphManager method in place of the uri to work against
     * the default graph.  The string value is not important and for
     * java-client internal use only--it is never sent to the database.</p>
     *
     * <p>Example:</p>
     * <pre>    StringHandle stringHandle = new StringHandle()
     *        .with("&lt;http://example.org/subject2&gt; &lt;http://example.org/predicate2&gt; &lt;http://example.org/object2&gt; .")
     *        .withMimetype(RDFMimeTypes.NTRIPLES);
     *    graphMgr.merge(DEFAULT_GRAPH, stringHandle);</pre>
     */
    public static String DEFAULT_GRAPH = "com.marklogic.client.semantics.GraphManager.DEFAULT_GRAPH";

    /** <p>Get the uri for available graphs.</p>
     *
     * <p>Example:</p>
     * <pre>    Iterator<String> iter = graphMgr.listGraphUris();
     *    while ( iter.hasNext() ) {
     *        String uri = iter.next();
     *        ...
     *    }
     */
    public Iterator<String> listGraphUris();

    /** <p>Read triples from the server.  The server can serialize the triples
     * using any RDFMimeTypes except TRIG.  Specify the desired serialization
     * mimetype by calling {@link #setDefaultMimetype setDefaultMimetype} or
     * {@link BaseHandle#setMimetype handle.setMimetype} or withMimetype (if
     * available) on your handle.</p>
     *
     * @param uri the graph uri or {@link #DEFAULT_GRAPH} constant
     * @param handle the handle to populate and return, set with the desired
     *     mimetype from RDFMimeTypes
     *
     * @return the populated handle
     *
     * @see GraphManager example usage in class javadoc
     */
    public <T extends TriplesReadHandle> T read(String uri, T handle)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

    /** <p>Read triples from the server.  The server can serialize the triples
     * using any RDFMimeTypes except TRIG.  Specify the desired serialization
     * mimetype by calling {@link #setDefaultMimetype setDefaultMimetype} or
     * {@link BaseHandle#setMimetype handle.setMimetype} or withMimetype (if
     * available) on your handle.</p>
     *
     * @param uri the graph uri or {@link #DEFAULT_GRAPH} constant
     * @param handle the handle to populate and return with the desired
     *     mimetype from RDFMimeTypes
     *     mimetype from RDFMimeTypes
     * @param transaction the open transaction to read from
     *
     * @return the populated handle
     */
    public <T extends TriplesReadHandle> T read(String uri, T handle, Transaction transaction)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

    /** <p>Read triples from the server as the specified type.  The server can
     * serialize the triples using any RDFMimeTypes except TRIG.  Specify the
     * desired serialization using {@link #setDefaultMimetype setDefaultMimetype}.</p>
     *
     * @param uri the graph uri or {@link #DEFAULT_GRAPH} constant
     * @param as the type to populate and return. This type must be registered by an io handle.
     *
     * @return the retrieved triples as the specified type
     *
     * @see GraphManager example usage in class javadoc
     */
    public <T> T readAs(String uri, Class<T> as)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

    /** <p>Read triples from the server as the specified type.  The server can
     * serialize the triples using any RDFMimeTypes except TRIG.  Specify the
     * desired serialization using {@link #setDefaultMimetype setDefaultMimetype}.</p>
     *
     * @param uri the graph uri or {@link #DEFAULT_GRAPH} constant
     * @param as the type to populate and return. This type must be registered by an io handle.
     * @param transaction the open transaction to read from
     *
     * @return the retrieved triples as the specified type
     */
    public <T> T readAs(String uri, Class<T> as, Transaction transaction)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

    /** <p>Retrieve permissions for a graph.</p>
     *
     * @param uri the graph uri or {@link #DEFAULT_GRAPH} constant
     *
     * @return the retrieved GraphPermissions
     */
    public GraphPermissions getPermissions(String uri)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

    /** <p>Retrieve permissions for a graph.</p>
     *
     * @param uri the graph uri or {@link #DEFAULT_GRAPH} constant
     * @param transaction the open transaction to read from
     *
     * @return the retrieved GraphPermissions
     */
    public GraphPermissions getPermissions(String uri, Transaction transaction)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

    /** <p>Delete all permissions for the graph.</p>
     *
     * @param uri the graph uri or {@link #DEFAULT_GRAPH} constant
     */
    public void deletePermissions(String uri)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

    /** <p>Delete all permissions for the graph.</p>
     *
     * @param uri the graph uri or {@link #DEFAULT_GRAPH} constant
     * @param transaction the open transaction to delete in
     */
    public void deletePermissions(String uri, Transaction transaction)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

    /** <p>Overwrite all permissions for the graph.</p>
     *
     * @param uri the graph uri or {@link #DEFAULT_GRAPH} constant
     * @param permissions the permissions to set for this graph
     */
    public void writePermissions(String uri, GraphPermissions permissions)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

    /** <p>Overwrite all permissions for the graph.</p>
     *
     * @param uri the graph uri or {@link #DEFAULT_GRAPH} constant
     * @param permissions the permissions to set for this graph
     * @param transaction the open transaction to write in
     */
    public void writePermissions(String uri, GraphPermissions permissions, Transaction transaction)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

    /** <p>Add to permissions on the graph.</p>
     *
     * @param uri the graph uri or {@link #DEFAULT_GRAPH} constant
     * @param permissions the permissions to add to this graph
     */
    public void mergePermissions(String uri, GraphPermissions permissions)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

    /** <p>Add to permissions on the graph.</p>
     *
     * @param uri the graph uri or {@link #DEFAULT_GRAPH} constant
     * @param permissions the permissions to add to this graph
     * @param transaction the open transaction to write in
     */
    public void mergePermissions(String uri, GraphPermissions permissions, Transaction transaction)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

    /** Create a GraphPermissions builder object with the specified role and capabilities.
     *
     * @param role the name of the role receiving these capabilities
     * @param capabilities the capabilities (READ, UPDATE, or EXECUTE) granted to this role
     *
     * @return the new GraphPermissions object with these capabilities set
     */
    public GraphPermissions permission(String role, Capability... capabilities);

    /** <p>Add triples from the handle to the specified graph.  The server can
     * receive the triples as any of the {@link RDFMimeTypes}.  Specify the
     * mimetype appropriate for your content by calling {@link
     * #setDefaultMimetype setDefaultMimetype} or {@link BaseHandle#setMimetype
     * handle.setMimetype} or withMimetype (if available) on your handle.</p>
     *
     * @param uri the graph uri or {@link #DEFAULT_GRAPH} constant
     * @param handle the handle containing triples of appropriate RDFMimeTypes
     *
     * @see GraphManager example usage in class javadoc
     */
    public void merge(String uri, TriplesWriteHandle handle)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

    /** <p>Add triples from the handle to the specified graph.  The server can
     * receive the triples as any of the {@link RDFMimeTypes}.  Specify the
     * mimetype appropriate for your content by calling {@link
     * #setDefaultMimetype setDefaultMimetype} or {@link BaseHandle#setMimetype
     * handle.setMimetype} or withMimetype (if available) on your handle.</p>
     *
     * @param uri the graph uri or {@link #DEFAULT_GRAPH} constant
     * @param handle the handle containing triples of appropriate RDFMimeTypes
     * @param transaction the open transaction to write in
     */
    public void merge(String uri, TriplesWriteHandle handle, Transaction transaction)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

    /** <p>Add triples from the handle and add specified permissions to the
     * specified graph.  The server can receive the triples as any of the
     * {@link RDFMimeTypes}.  Specify the mimetype appropriate for your content
     * by calling {@link #setDefaultMimetype setDefaultMimetype} or {@link
     * BaseHandle#setMimetype handle.setMimetype} or withMimetype (if
     * available) on your handle.</p>
     *
     * @param uri the graph uri or {@link #DEFAULT_GRAPH} constant
     * @param handle the handle containing triples of appropriate RDFMimeTypes
     * @param permissions the permissions to add to this graph
     */
    public void merge(String uri, TriplesWriteHandle handle, GraphPermissions permissions)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

    /** <p>Add triples from the handle and add specified permissions to the
     * specified graph.  The server can receive the triples as any of the
     * {@link RDFMimeTypes}.  Specify the mimetype appropriate for your content
     * by calling {@link #setDefaultMimetype setDefaultMimetype} or {@link
     * BaseHandle#setMimetype handle.setMimetype} or withMimetype (if
     * available) on your handle.</p>
     *
     * @param uri the graph uri or {@link #DEFAULT_GRAPH} constant
     * @param handle the handle containing triples of appropriate RDFMimeTypes
     * @param permissions the permissions to add to this graph
     * @param transaction the open transaction to write in
     */
    public void merge(String uri, TriplesWriteHandle handle, GraphPermissions permissions, Transaction transaction)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

    /** <p>Add triples from the graphData object to the specified graph.  The
     * server can receive the triples as any of the {@link RDFMimeTypes}.
     * Specify the mimetype appropriate for your content by calling {@link
     * #setDefaultMimetype setDefaultMimetype}.</p>
     *
     * @param uri the graph uri or {@link #DEFAULT_GRAPH} constant
     * @param graphData the object containing triples of appropriate RDFMimeTypesMimetype}
     */
    public void mergeAs(String uri, Object graphData)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

    /** <p>Add triples from the graphData object to the specified graph.  The
     * server can receive the triples as any of the {@link RDFMimeTypes}.
     * Specify the mimetype appropriate for your content by calling {@link
     * #setDefaultMimetype setDefaultMimetype}.</p>
     *
     * @param uri the graph uri or {@link #DEFAULT_GRAPH} constant
     * @param graphData the object containing triples of RDFMimeTypes specified by
     *     {@link #setDefaultMimetype setDefaultMimetype}
     * @param transaction the open transaction to write in
     */
    public void mergeAs(String uri, Object graphData, Transaction transaction)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;


    /** <p>Add triples from the graphData object and add specified permissions
     * to the specified graph.  The server can receive the triples as any of the
     * {@link RDFMimeTypes}.  Specify the mimetype appropriate for your content
     * by calling {@link #setDefaultMimetype setDefaultMimetype}.</p>
     *
     * @param uri the graph uri or {@link #DEFAULT_GRAPH} constant
     * @param graphData the object containing triples of RDFMimeTypes specified by
     *     {@link #setDefaultMimetype setDefaultMimetype}
     * @param permissions the permissions to add to this graph
     */
    public void mergeAs(String uri, Object graphData, GraphPermissions permissions)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

    /** <p>Add triples from the graphData object and add specified permissions
     * to the specified graph.  The server can receive the triples as any of the
     * {@link RDFMimeTypes}.  Specify the mimetype appropriate for your content
     * by calling {@link #setDefaultMimetype setDefaultMimetype}.</p>
     *
     * @param uri the graph uri or {@link #DEFAULT_GRAPH} constant
     * @param graphData the object containing triples of RDFMimeTypes specified by
     *     {@link #setDefaultMimetype setDefaultMimetype}
     * @param permissions the permissions to add to this graph
     * @param transaction the open transaction to write in
     */
    public void mergeAs(String uri, Object graphData, GraphPermissions permissions, Transaction transaction)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

    /** <p>Overwrite triples from the handle as the
     * specified graph.  The server can receive the triples as any of the
     * {@link RDFMimeTypes}.  Specify the mimetype appropriate for your content
     * by calling {@link #setDefaultMimetype setDefaultMimetype} or {@link BaseHandle#setMimetype
     * handle.setMimetype} or withMimetype (if available) on your handle.</p>
     *
     * @param uri the graph uri or {@link #DEFAULT_GRAPH} constant
     * @param handle the handle containing triples of appropriate RDFMimeTypes
     *
     * @see GraphManager example usage in class javadoc
     */
    public void write(String uri, TriplesWriteHandle handle)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

    /** <p>Overwrite triples from the handle as the
     * specified graph.  The server can receive the triples as any of the
     * {@link RDFMimeTypes}.  Specify the mimetype appropriate for your content
     * by calling {@link #setDefaultMimetype setDefaultMimetype} or {@link BaseHandle#setMimetype
     * handle.setMimetype} or withMimetype (if available) on your handle.</p>
     *
     * @param uri the graph uri or {@link #DEFAULT_GRAPH} constant
     * @param handle the handle containing triples of appropriate RDFMimeTypes
     * @param transaction the open transaction to write in
     */
    public void write(String uri, TriplesWriteHandle handle, Transaction transaction)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

    /** <p>Overwrite triples from the handle and specified permissions as the
     * specified graph.  The server can receive the triples as any of the
     * {@link RDFMimeTypes}.  Specify the mimetype appropriate for your content
     * by calling {@link #setDefaultMimetype setDefaultMimetype} or {@link BaseHandle#setMimetype
     * handle.setMimetype} or withMimetype (if available) on your handle.</p>
     *
     * @param uri the graph uri or {@link #DEFAULT_GRAPH} constant
     * @param handle the handle containing triples of appropriate RDFMimeTypes
     * @param permissions the permissions to ovewrite for this graph
     *
     * @see GraphManager example usage in class javadoc
     */
    public void write(String uri, TriplesWriteHandle handle, GraphPermissions permissions)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

    /** <p>Overwrite triples from the handle and specified permissions as the
     * specified graph.  The server can receive the triples as any of the
     * {@link RDFMimeTypes}.  Specify the mimetype appropriate for your content
     * by calling {@link #setDefaultMimetype setDefaultMimetype} or {@link BaseHandle#setMimetype
     * handle.setMimetype} or withMimetype (if available) on your handle.</p>
     *
     * @param uri the graph uri or {@link #DEFAULT_GRAPH} constant
     * @param handle the handle containing triples of appropriate RDFMimeTypes
     * @param permissions the permissions to ovewrite for this graph
     * @param transaction the open transaction to write in
     */
    public void write(String uri, TriplesWriteHandle handle, GraphPermissions permissions, Transaction transaction)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

    /** <p>Overwrite triples from the graphData object as the specified graph.
     * The server can receive the triples as any of the {@link RDFMimeTypes}.
     * Specify the mimetype appropriate for your content by calling {@link
     * #setDefaultMimetype setDefaultMimetype}.</p>
     *
     * @param uri the graph uri or {@link #DEFAULT_GRAPH} constant
     * @param graphData the object containing triples of RDFMimeTypes specified by
     *     {@link #setDefaultMimetype setDefaultMimetype}
     */    public void writeAs(String uri, Object graphData)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

    /** <p>Overwrite triples from the graphData object as the specified graph.
     * The server can receive the triples as any of the {@link RDFMimeTypes}.
     * Specify the mimetype appropriate for your content by calling {@link
     * #setDefaultMimetype setDefaultMimetype}.</p>
     *
     * @param uri the graph uri or {@link #DEFAULT_GRAPH} constant
     * @param graphData the object containing triples of RDFMimeTypes specified by
     *     {@link #setDefaultMimetype setDefaultMimetype}
     * @param transaction the open transaction to write in
     */    public void writeAs(String uri, Object graphData, Transaction transaction)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

    /** <p>Overwrite triples from the graphData object and specified
     * permissions as the specified graph.  The server can receive the triples
     * as any of the {@link RDFMimeTypes}.  Specify the mimetype appropriate
     * for your content by calling {@link #setDefaultMimetype setDefaultMimetype}.</p>
     *
     * @param uri the graph uri or {@link #DEFAULT_GRAPH} constant
     * @param graphData the object containing triples of RDFMimeTypes specified by
     *     {@link #setDefaultMimetype setDefaultMimetype}
     * @param permissions the permissions to ovewrite for this graph
     */    public void writeAs(String uri, Object graphData, GraphPermissions permissions)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

    /** <p>Overwrite triples from the graphData object and specified
     * permissions as the specified graph.  The server can receive the triples
     * as any of the {@link RDFMimeTypes}.  Specify the mimetype appropriate
     * for your content by calling {@link #setDefaultMimetype setDefaultMimetype}.</p>
     *
     * @param uri the graph uri or {@link #DEFAULT_GRAPH} constant
     * @param graphData the object containing triples of RDFMimeTypes specified by
     *     {@link #setDefaultMimetype setDefaultMimetype}
     * @param permissions the permissions to ovewrite for this graph
     * @param transaction the open transaction to write in
     */
    public void writeAs(String uri, Object graphData, GraphPermissions permissions, Transaction transaction)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

    /** <p>Permanently delete the specified graph from the server.</p>
     *
     * @param uri the graph uri or {@link #DEFAULT_GRAPH} constant
     */
    public void delete(String uri)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

    /** <p>Permanently delete the specified graph from the server.</p>
     *
     * @param uri the graph uri or {@link #DEFAULT_GRAPH} constant
     * @param transaction the open transaction to delete in
     */
    public void delete(String uri, Transaction transaction)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

    /** <p>Retrieves all triples related to specified subject or object iris.
     * The server can serialize the triples using any RDFMimeTypes except TRIG.
     * Specify the desired serialization mimetype by calling {@link
     * #setDefaultMimetype setDefaultMimetype} or {@link BaseHandle#setMimetype
     * handle.setMimetype} or withMimetype (if available) on your handle.</p>
     *
     * @param handle the handle to populate and return with the desired
     *     mimetype from RDFMimeTypes
     * @param iris the subject or object iris to retrieve
     *
     * @return the populated handle
     */
    public <T extends TriplesReadHandle> T things(T handle, String... iris)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

    /** <p>Retrieves all triples related to specified subject or object iris.  The server can
     * serialize the triples using any RDFMimeTypes except TRIG.  Specify the
     * desired serialization using {@link #setDefaultMimetype setDefaultMimetype}.</p>
     *
     * @param as the type to populate and return. This type must be registered by an io handle.
     * @param iris the subject or object iris to retrieve
     *
     * @return the retrieved triples as the specified type
     */
    public <T> T thingsAs(Class<T> as, String... iris)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

    /** <p>Add quads from the handle to the graphs specified in the quads data.
     * The server can receive the quads as {@link RDFMimeTypes#NQUADS} or
     * {@link RDFMimeTypes#TRIG}.  Specify the mimetype appropriate for your
     * content by calling {@link #setDefaultMimetype setDefaultMimetype} or
     * {@link BaseHandle#setMimetype handle.setMimetype} or withMimetype (if
     * available) on your handle.</p>
     *
     * @param handle the handle containing quads of appropriate RDFMimeTypes
     */
    public void mergeGraphs(QuadsWriteHandle handle)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

    /** <p>Add quads from the handle to the graphs specified in the quads data.
     * The server can receive the quads as {@link RDFMimeTypes#NQUADS} or
     * {@link RDFMimeTypes#TRIG}.  Specify the mimetype appropriate for your
     * content by calling {@link #setDefaultMimetype setDefaultMimetype} or
     * {@link BaseHandle#setMimetype handle.setMimetype} or withMimetype (if
     * available) on your handle.</p>
     *
     * @param handle the handle containing quads of appropriate RDFMimeTypes
     * @param transaction the open transaction to write in
     */
    public void mergeGraphs(QuadsWriteHandle handle, Transaction transaction)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

    /** <p>Add quads from the object to the graphs specified in the quads data.
     * The server can receive the quads as {@link RDFMimeTypes#NQUADS} or
     * {@link RDFMimeTypes#TRIG}.  Specify the mimetype appropriate for your
     * content by calling {@link #setDefaultMimetype setDefaultMimetype}.</p>
     *
     * @param quadsData the object containing quads of RDFMimeTypes specified by
     *     {@link #setDefaultMimetype setDefaultMimetype}
     */
    public void mergeGraphsAs(Object quadsData)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

    /** <p>Add quads from the object to the graphs specified in the quads data.
     * The server can receive the quads as {@link RDFMimeTypes#NQUADS} or
     * {@link RDFMimeTypes#TRIG}.  Specify the mimetype appropriate for your
     * content by calling {@link #setDefaultMimetype setDefaultMimetype}.</p>
     *
     * @param quadsData the object containing quads of RDFMimeTypes specified by
     *     {@link #setDefaultMimetype setDefaultMimetype}
     * @param transaction the open transaction to write in
     */
    public void mergeGraphsAs(Object quadsData, Transaction transaction)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

    /** <p>Remove all quads from all graphs then insert quads from the handle
     * to the graphs specified in the quads data.  The server can receive the
     * quads as {@link RDFMimeTypes#NQUADS} or {@link RDFMimeTypes#TRIG}.
     * Specify the mimetype appropriate for your content by calling {@link
     * #setDefaultMimetype setDefaultMimetype} or {@link BaseHandle#setMimetype
     * handle.setMimetype} or withMimetype (if available) on your handle.</p>
     *
     * @param handle the handle containing quads of appropriate RDFMimeTypes
     */
    public void replaceGraphs(QuadsWriteHandle handle)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

    /** <p>Remove all quads from all graphs then insert quads from the handle
     * to the graphs specified in the quads data.  The server can receive the
     * quads as {@link RDFMimeTypes#NQUADS} or {@link RDFMimeTypes#TRIG}.
     * Specify the mimetype appropriate for your content by calling {@link
     * #setDefaultMimetype setDefaultMimetype} or {@link BaseHandle#setMimetype
     * handle.setMimetype} or withMimetype (if available) on your handle.</p>
     *
     * @param handle the handle containing quads of appropriate RDFMimeTypes
     * @param transaction the open transaction to write in
     */
    public void replaceGraphs(QuadsWriteHandle handle, Transaction transaction)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

    /** <p>Remove all quads from all graphs then insert quads from the quadsData
     * to the graphs specified in the quads data.  The server can receive the
     * quads as {@link RDFMimeTypes#NQUADS} or {@link RDFMimeTypes#TRIG}.
     * Specify the mimetype appropriate for your content by calling {@link
     * #setDefaultMimetype setDefaultMimetype}.</p>
     *
     * @param quadsData the object containing quads of RDFMimeTypes specified by
     *     {@link #setDefaultMimetype setDefaultMimetype}
     */
    public void replaceGraphsAs(Object quadsData)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

    /** <p>Remove all quads from all graphs then insert quads from the quadsData
     * to the graphs specified in the quads data.  The server can receive the
     * quads as {@link RDFMimeTypes#NQUADS} or {@link RDFMimeTypes#TRIG}.
     * Specify the mimetype appropriate for your content by calling {@link
     * #setDefaultMimetype setDefaultMimetype}.</p>
     *
     * @param quadsData the object containing quads of RDFMimeTypes specified by
     *     {@link #setDefaultMimetype setDefaultMimetype}
     * @param transaction the open transaction to write in
     */
    public void replaceGraphsAs(Object quadsData, Transaction transaction)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

    /** <p>Permanently delete all quads from all graphs.</p>
     */
    public void deleteGraphs()
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

    /** <p>Permanently delete all quads from all graphs.</p>
     *
     * @param transaction the open transaction to delete in
     */
    public void deleteGraphs(Transaction transaction)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

    /** Get the mimetype set by calling setDefaultMimetype. */
    public String getDefaultMimetype();

    /** Set the default mimetype for data sent by write* or merge* methods
     * and data serialized by the server in response to calls to read* or
     * things* methods.  A mimetype explicitly set on the handle will be used
     * instead of the default.  This default mimetype must be set to use the
     * *As methods since there is no handle on which to explicity set a
     * mimetype.
     */
    public void setDefaultMimetype(String mimetype);

    /** Get an empty GraphPermissions instance. */
    public GraphPermissions newGraphPermissions();
}
