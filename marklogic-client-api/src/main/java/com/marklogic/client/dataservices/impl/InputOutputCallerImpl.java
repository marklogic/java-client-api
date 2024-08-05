/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.dataservices.impl;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.io.marker.BufferableContentHandle;
import com.marklogic.client.io.marker.JSONWriteHandle;

final public class InputOutputCallerImpl<I,O> extends IOCallerImpl<I,O> {
    public InputOutputCallerImpl(JSONWriteHandle apiDeclaration, HandleProvider<I,O> handleProvider) {
        super(apiDeclaration, handleProvider);

        if (getInputParamdef() == null) {
            throw new IllegalArgumentException("input parameter missing in endpoint: "+ getEndpointPath());
        }

        ReturndefImpl returndef = getReturndef();
        if (returndef == null) {
            throw new IllegalArgumentException("return required in endpoint: "+ getEndpointPath());
        } else if (!returndef.isMultiple()) {
            throw new IllegalArgumentException("return must be multiple in endpoint: "+ getEndpointPath());
        }
    }

    public O[] arrayCall(DatabaseClient db, CallContextImpl<I,O> callCtxt, BufferableContentHandle<?,?>[] input) {
        return responseMultipleAsArray(makeRequest(db, callCtxt, input), callCtxt);
    }
}
