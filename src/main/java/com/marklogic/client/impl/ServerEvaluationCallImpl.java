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
package com.marklogic.client.impl;

import java.util.HashMap;

import com.marklogic.client.DatabaseClientFactory.HandleFactoryRegistry;
import com.marklogic.client.FailedRequestException;
import com.marklogic.client.ForbiddenUserException;
import com.marklogic.client.Transaction;
import com.marklogic.client.eval.EvalResultIterator;
import com.marklogic.client.eval.ServerEvaluationCall;
import com.marklogic.client.io.marker.AbstractReadHandle;
import com.marklogic.client.io.marker.AbstractWriteHandle;
import com.marklogic.client.io.marker.ContentHandle;
import com.marklogic.client.io.marker.TextWriteHandle;
import com.marklogic.client.util.EditableNamespaceContext;

public class ServerEvaluationCallImpl 
    extends AbstractLoggingManager
    implements ServerEvaluationCall
{
    public enum Context { ADHOC_XQUERY, ADHOC_JAVASCRIPT, INVOKE };
     
    private RESTServices             services;
    private HandleFactoryRegistry    handleRegistry;
    private String                   code;
    private String                   modulePath;
    private Context                  evalContext;
    private String                   transactionId;
    private HashMap<String, Object>  vars = new HashMap<String, Object>();
    private EditableNamespaceContext namespaceContext;

    public ServerEvaluationCallImpl(RESTServices services, HandleFactoryRegistry handleRegistry) {
        this.services = services;
        this.handleRegistry = handleRegistry;
    }

    @Override
    public ServerEvaluationCall xquery(String xquery) {
        setContext(Context.ADHOC_XQUERY);
        code = xquery;
        return this;
    }

    @Override
    public ServerEvaluationCall xquery(TextWriteHandle xquery) {
        setContext(Context.ADHOC_XQUERY);
        code = HandleAccessor.contentAsString(xquery);
        return this;
    }

    @Override
    public ServerEvaluationCall javascript(String javascript) {
        setContext(Context.ADHOC_JAVASCRIPT);
        code = javascript;
        return this;
    }

    @Override
    public ServerEvaluationCall javascript(TextWriteHandle javascript) {
        setContext(Context.ADHOC_JAVASCRIPT);
        code = HandleAccessor.contentAsString(javascript);
        return this;
    }

    @Override
    public ServerEvaluationCall modulePath(String modulePath) {
        setContext(Context.INVOKE);
        this.modulePath = modulePath;
        return this;
    }

    @Override
    public ServerEvaluationCall addVariable(String name, String value) {
        vars.put(name, value);
        return this;
    }

    @Override
    public ServerEvaluationCall addVariable(String name, Number value) {
        vars.put(name, value);
        return this;
    }

    @Override
    public ServerEvaluationCall addVariable(String name, Boolean value) {
        vars.put(name, value);
        return this;
    }

    @Override
    public ServerEvaluationCall addVariable(String name, AbstractWriteHandle value) {
        vars.put(name, value);
        return this;
    }

    /** Like other *As convenience methods throughout the API, the Object value
     *  is managed by the Handle registered for that Class.  */
    @Override
    public ServerEvaluationCall addVariableAs(String name, Object value) {
        if (value == null) return this;

        Class<?> as = value.getClass();
        AbstractWriteHandle writeHandle = null;
        if (AbstractWriteHandle.class.isAssignableFrom(as)) {
            writeHandle = (AbstractWriteHandle) value;
        } else {
            ContentHandle<?> contentHandle = handleRegistry.makeHandle(as);
            Utilities.setHandleContent(contentHandle, value);
            writeHandle = contentHandle;
        }
        return addVariable(name, writeHandle);
    }

    @Override
    public ServerEvaluationCall transaction(Transaction transaction) {
        if ( transaction != null ) this.transactionId = transaction.getTransactionId();
        return this;
    }

    @Override
    public <T> T evalAs(Class<T> responseType) 
        throws ForbiddenUserException, FailedRequestException
    {
        if (responseType == null) throw new IllegalArgumentException("responseType cannot be null");

        ContentHandle<T> readHandle = handleRegistry.makeHandle(responseType);
        if ( readHandle == null ) return null;
        readHandle = eval(readHandle); 
        if ( readHandle == null ) return null;
        return readHandle.get();
    }

    @Override
    public <H extends AbstractReadHandle> H eval(H responseHandle) 
        throws ForbiddenUserException, FailedRequestException
    {
        EvalResultIterator iterator = eval();
        try {
            if ( iterator == null || iterator.hasNext() == false ) return null;
            return iterator.next().get(responseHandle);
        } finally { iterator.close(); }
    }

    @Override
    public EvalResultIterator eval() 
        throws ForbiddenUserException, FailedRequestException
    {
        return services.postEvalInvoke(requestLogger, code, modulePath, evalContext,
            vars, namespaceContext, transactionId);
    }

    @Override
    public ServerEvaluationCall addNamespace(String prefix, String namespaceURI) {
        if ( namespaceContext == null ) namespaceContext = new EditableNamespaceContext();
        namespaceContext.put(prefix, namespaceURI);
        return this;
    }

    @Override
    public ServerEvaluationCall namespaceContext(EditableNamespaceContext namespaces) {
        this.namespaceContext = namespaces;
        return this;
    }

    private void setContext(Context context) {
        if ( evalContext == null ) {
            evalContext = context;
        } else {
            throw new IllegalStateException("You can only initialize the code to evaluate one time. " +
                    "That means only one call to the xquery, javascript, xqueryModule, or " +
                    "javascriptModule methods per ServerEvaluationCall.");
        }
    }

}
