/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.dataservices.impl;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.io.marker.JSONWriteHandle;

final public class ExecCallerImpl<I,O> extends IOCallerImpl<I,O> {
    public ExecCallerImpl(JSONWriteHandle apiDeclaration) {
        super(apiDeclaration, new HandleProvider.ContentHandleProvider<>(null, null));

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
