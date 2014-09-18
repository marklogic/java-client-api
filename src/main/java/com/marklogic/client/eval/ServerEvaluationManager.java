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
import com.marklogic.client.io.marker.AbstractReadHandle;
import com.marklogic.client.io.marker.AbstractWriteHandle;
import com.marklogic.client.io.marker.TextWriteHandle;

public interface ServerEvaluationManager {
    public ServerEvaluationManager xquery(String xquery);
    public ServerEvaluationManager xquery(TextWriteHandle xquery);
    public ServerEvaluationManager javascript(String javascript);
    public ServerEvaluationManager javascript(TextWriteHandle javascript);
    public ServerEvaluationManager xqueryModule(String modulePath);
    public ServerEvaluationManager javascriptModule(String modulePath);
    public ServerEvaluationManager addVariable(String name, String value);
    public ServerEvaluationManager addVariable(String name, Number value);
    public ServerEvaluationManager addVariable(String name, Boolean value);
    public ServerEvaluationManager addVariable(String name, AbstractWriteHandle value);
    /** Like other *As convenience methods throughout the API, the Object value
     *  is managed by the Handle registered for that Class.  */
    public ServerEvaluationManager addVariableAs(String name, Object value);
    public ServerEvaluationManager database(String database);
    public ServerEvaluationManager transaction(Transaction transaction);
    public <T> T evalAs(Class<T> responseType);
    public <H extends AbstractReadHandle> H eval(H responseHandle);
    public EvalResults eval();
}
