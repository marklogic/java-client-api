/*
 * Copyright 2012-2014 MarkLogic Corporation
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

import com.marklogic.client.Transaction;
import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.client.io.marker.AbstractReadHandle;
import com.marklogic.client.io.marker.AbstractWriteHandle;
import com.marklogic.client.io.marker.TextWriteHandle;
import com.marklogic.client.util.EditableNamespaceContext;

public interface ServerEvaluationCall {
    public ServerEvaluationCall xquery(String xquery);
    public ServerEvaluationCall xquery(AbstractWriteHandle xquery);
    public ServerEvaluationCall javascript(String javascript);
    public ServerEvaluationCall javascript(AbstractWriteHandle xquery);
    public ServerEvaluationCall modulePath(String modulePath);
    public ServerEvaluationCall addVariable(String name, String value);
    public ServerEvaluationCall addVariable(String name, Number value);
    public ServerEvaluationCall addVariable(String name, Boolean value);
    public ServerEvaluationCall addVariable(String name, AbstractWriteHandle value);
    /** Like other *As convenience methods throughout the API, the Object value
     *  is managed by the Handle registered for that Class.  */
    public ServerEvaluationCall addVariableAs(String name, Object value);
    public ServerEvaluationCall database(String database);
    public ServerEvaluationCall transaction(Transaction transaction);
    public ServerEvaluationCall addNamespace(String prefix, String namespaceURI);
    public ServerEvaluationCall namespaceContext(EditableNamespaceContext namespaces);
    public <T> T evalAs(Class<T> responseType);
    public <H extends AbstractReadHandle> H eval(H responseHandle);
    public EvalResultIterator eval();
}
