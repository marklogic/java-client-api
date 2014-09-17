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
import com.marklogic.client.io.marker.AbstractWriteHandle;

public interface EvalBuilder<T extends ServerEval> {
    public EvalBuilder addVariable(String name, String value);
    public EvalBuilder addVariable(String name, Number value);
    public EvalBuilder addVariable(String name, Boolean value);
    public EvalBuilder addVariable(String name, AbstractWriteHandle value);
    /** Like other *As convenience methods throughout the API, the Object value
     *  is managed by the Handle registered for that Class.  */
    public EvalBuilder addVariableAs(String name, Object value);
    public EvalBuilder database(String database);
    public EvalBuilder transaction(Transaction transaction);
    public T build();
}
