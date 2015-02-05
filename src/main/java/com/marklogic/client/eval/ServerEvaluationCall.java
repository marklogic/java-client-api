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
package com.marklogic.client.eval;

import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.FailedRequestException;
import com.marklogic.client.ForbiddenUserException;
import com.marklogic.client.Transaction;
import com.marklogic.client.io.marker.AbstractReadHandle;
import com.marklogic.client.io.marker.AbstractWriteHandle;
import com.marklogic.client.io.marker.TextWriteHandle;
import com.marklogic.client.util.EditableNamespaceContext;

/**
 * ServerEvaluationCall uses a fluent builder-style API to collect the parameters
 * for a server-side {@link #xquery xquery} or {@link #javascript javascript} eval or
 * invoke ({@link #modulePath modulePath}) call. ServerEvaluationCall also
 * conveniently has the eval* methods which execute those calls and return the
 * results.  You must call one and only one of the following methods: xquery, 
 * javascript, or modulePath.  The xquery
 * and javascript methods initialize this call for server-side eval and accept
 * source code as a String or a TextWriteHandle (in case you are streaming the
 * source code from the file system, a URL, or other source that is most easily
 * accessed via io handles). The modulePath method initializes this call for server-
 * side invoke given the path to a module previously installed on the server.
 *
 * Here is a simple "hello world" junit example: <pre>{@code
 *String javascript = "'hello world'";
 *String response = client.newServerEval()
 *    .javascript(javascript)
 *    .evalAs(String.class);
 *assertEquals("hello world", response);
 *}</pre>
 * or in xquery: <pre>{@code
 *String xquery = "'hello world'";
 *String response = client.newServerEval()
 *    .xquery(xquery)
 *    .evalAs(String.class);
 *assertEquals("hello world", response);
 *}</pre>
 *
 * Variables can be added with the addVariable methods.
 * {@link #addVariable(String, AbstractWriteHandle) addVariable(String, AbstractWriteHandle)}
 * allows you to pass complex JSON or XML values directly from io handles.
 * {@link #addVariableAs(String, Object) addVariableAs(String, Object)}
 * follows the <a href="http://www.marklogic.com/blog/io-shortcut-marklogic-java-client-api/">
 * shortcut pattern</a> which maps objects by type to the appropriate handle.
 * For simpler atomic values, convenience addVariable methods are provided for
 * String, Number, and Boolean types.
 *
 * Here is a simple "hello solar system" example with a variable: <pre>{@code
 *String javascript = "var planet;'hello solar system from ' + planet";
 *String response = client.newServerEval()
 *    .javascript(javascript)
 *    .addVariable("planet", "Mars)
 *    .evalAs(String.class);
 *assertEquals( "hello solar system from Mars", response);
 *}</pre>
 * or in xquery: <pre>{@code
 *String xquery = "declare variable $planet external;'hello solar system from ' || $planet";
 *String response = client.newServerEval()
 *    .xquery(xquery)
 *    .addVariable("planet", "Mars)
 *    .evalAs(String.class);
 *assertEquals( "hello solar system from Mars", response);
 * }</pre>
 *
 * Each call can be executed within a {@link #transaction transaction}, within a
 * {@link DatabaseClientFactory#newClient(String, int, String) particular database},
 * and with particular {@link #namespaceContext namespaces} available for expansion
 * of prefixed variable names.
 *
 * Each call can be executed with only one expected response of a particular
 * {@link #evalAs type} or {@link #eval(AbstractReadHandle) handle type}.  Or calls can be executed
 * with {@link #eval() multiple responses expected}.
 */
public interface ServerEvaluationCall {
    /** Initialize this server-side eval with xquery-syntax source code.
     * @param xquery the xquery-syntax source code to eval on the server
     * @return a reference to this ServerEvaluationCall instance for use as a fluent-style builder
     */
    public ServerEvaluationCall xquery(String xquery);

    /** Initialize this server-side eval with xquery-syntax source code.
     * @param xquery a handle containing the xquery-syntax source code to eval on the server
     * @return a reference to this ServerEvaluationCall instance for use as a fluent-style builder
     */
    public ServerEvaluationCall xquery(TextWriteHandle xquery);

    /** Initialize this server-side eval with javascript-syntax source code.
     * @param javascript the javascript-syntax source code to eval on the server
     * @return a reference to this ServerEvaluationCall instance for use as a fluent-style builder
     */
    public ServerEvaluationCall javascript(String javascript);

    /** Initialize this server-side eval call with javascript-syntax source code.
     * @param javascript a handle containing the javascript-syntax source code to eval on the server
     * @return a reference to this ServerEvaluationCall instance for use as a fluent-style builder
     */
    public ServerEvaluationCall javascript(TextWriteHandle javascript);

    /** Initialize this server-side invoke call with a path to the module to invoke.
     * @param modulePath a path to a module previously installed in the server
     * @return a reference to this ServerEvaluationCall instance for use as a fluent-style builder
     */
    public ServerEvaluationCall modulePath(String modulePath);

    /** Set a variable name-value pair to pass to the code executing server-side.
     * @param name the variable name, including a namespace prefix if the prefix is
     *      mapped to a uri in the {@link #namespaceContext namespace context}
     * @param value the atomic variable value
     * @return a reference to this ServerEvaluationCall instance for use as a fluent-style builder
     */
    public ServerEvaluationCall addVariable(String name, String value);

    /** Set a variable name-value pair to pass to the code executing server-side.
     * @param name the variable name, including a namespace prefix if the prefix is
     *      mapped to a uri in the {@link #namespaceContext namespace context}
     * @param value the atomic variable value
     * @return a reference to this ServerEvaluationCall instance for use as a fluent-style builder
     */
    public ServerEvaluationCall addVariable(String name, Number value);

    /** Set a variable name-value pair to pass to the code executing server-side.
     * @param name the variable name, including a namespace prefix if the prefix is
     *      mapped to a uri in the {@link #namespaceContext namespace context}
     * @param value the atomic variable value
     * @return a reference to this ServerEvaluationCall instance for use as a fluent-style builder
     */
    public ServerEvaluationCall addVariable(String name, Boolean value);

    /** Set a variable name-value pair to pass to the code executing server-side.
     * @param name the variable name, including a namespace prefix if the prefix is
     *      mapped to a uri in the {@link #namespaceContext namespace context}
     * @param value the handle containing the variable value, most likely XML or JSON
     * @return a reference to this ServerEvaluationCall instance for use as a fluent-style builder
     */
    public ServerEvaluationCall addVariable(String name, AbstractWriteHandle value);

    /** Convenience method to set a variable of a type mapped to an io handle.  Like other
     * <a href="http://www.marklogic.com/blog/io-shortcut-marklogic-java-client-api/">
     * *As convenience methods</a> throughout the API, the Object value
     *  is provided by the handle registered for that Class.
     *
     * @param name the variable name, including a namespace prefix if the prefix is
     *      mapped to a uri in the {@link #namespaceContext namespace context}
     * @param value the handle containing the variable value
     * @return a reference to this ServerEvaluationCall instance for use as a fluent-style builder
     */
    public ServerEvaluationCall addVariableAs(String name, Object value);

    /** Initialize this call with a transaction under which server-side execution should occur.
     * @param transaction the open transaction under which to run this call in the server
     * @return a reference to this ServerEvaluationCall instance for use as a fluent-style builder
     */
    public ServerEvaluationCall transaction(Transaction transaction);

    /** Add a single namespace prefix-to-uri mapping to the namespace context.
     * @param prefix the prefix for this mapping
     * @param namespaceURI the uri for this mapping
     * @return a reference to this ServerEvaluationCall instance for use as a fluent-style builder
     */
    public ServerEvaluationCall addNamespace(String prefix, String namespaceURI);

    /** Initialize this call with namespaces so variables with prefixes can be sent with
     * their prefixes translated to uris that will match the uris in the code to be
     * executed on the server.
     * @param namespaces a namespace context specifying the mapping from prefixes to namespaces
     * @return a reference to this ServerEvaluationCall instance for use as a fluent-style builder
     */
    public ServerEvaluationCall namespaceContext(EditableNamespaceContext namespaces);

    /** Conveneince method to get the response serialized to a particular type by an io handle.
     * Like other <a href="http://www.marklogic.com/blog/io-shortcut-marklogic-java-client-api/">
     * *As convenience methods</a> throughout the API, the return value
     *  is provided by the handle registered for the provided responseType.
     *
     * @param responseType the type desired for the response.  Must be a Class regiestered
     *      to a handle.
     * @return the result deserialized by the implicit handle mapped to this responseType
     */
    public <T> T evalAs(Class<T> responseType)
        throws ForbiddenUserException, FailedRequestException;

    /** Provides the single result of the server-side eval or invoke call, wrapped in an io
     * handle.
     * @param responseHandle the type of handle appropriate for the expected single result
     * @return the handle which wraps the response
     */
    public <H extends AbstractReadHandle> H eval(H responseHandle)
        throws ForbiddenUserException, FailedRequestException;

    /** Provides all results returned by the server-side eval or invoke call.
     * @return an EvalResultIterator which provides access to all the results returned by 
     *      the server-side eval or invoke call.
     */
    public EvalResultIterator eval()
        throws ForbiddenUserException, FailedRequestException;
}
