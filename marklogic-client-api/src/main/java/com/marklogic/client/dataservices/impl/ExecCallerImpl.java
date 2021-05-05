/*
 * Copyright (c) 2021 MarkLogic Corporation
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
package com.marklogic.client.dataservices.impl;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.io.marker.JSONWriteHandle;

final public class ExecCallerImpl<I,O> extends IOCallerImpl<I,O> {
    public ExecCallerImpl(JSONWriteHandle apiDeclaration) {
        super(apiDeclaration, false, null, null);

        if (getInputParamdef() != null) {
            throw new IllegalArgumentException("input parameter not supported in endpoint: "+ getEndpointPath());
        }

        ReturndefImpl returndef = getReturndef();
        if (returndef != null) {
            if (getEndpointStateParamdef() == null) {
                throw new IllegalArgumentException(
                        "cannot have return without endpointState parameter in endpoint: "+ getEndpointPath()
                );
            } else if (returndef.isMultiple()) {
                throw new IllegalArgumentException("return cannot be multiple in endpoint: "+ getEndpointPath());
            }
        }
    }

    public boolean call(DatabaseClient db,  CallContextImpl<I,O> callCtxt) {
        return responseWithState(makeRequest(db, callCtxt), callCtxt);
    }
}
