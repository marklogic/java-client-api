/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.dataservices.impl;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.io.marker.BufferableContentHandle;
import com.marklogic.client.io.marker.JSONWriteHandle;

final public class InputCallerImpl<I,O> extends IOCallerImpl<I,O> {
    public InputCallerImpl(JSONWriteHandle apiDeclaration, HandleProvider<I,O> handleProvider) {
        super(apiDeclaration, handleProvider);

        if (getInputParamdef() == null) {
            throw new IllegalArgumentException("input parameter missing in endpoint: "+ getEndpointPath());
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

    public void arrayCall(DatabaseClient db, CallContextImpl<I,O> callCtxt, BufferableContentHandle<?,?>[] input) {
        responseWithState(makeRequest(db, callCtxt, input), callCtxt);
    }
}
