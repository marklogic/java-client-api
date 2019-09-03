/*
 * Copyright 2019 MarkLogic Corporation
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

import com.fasterxml.jackson.databind.JsonNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.SessionState;
import com.marklogic.client.impl.BaseProxy;
import com.marklogic.client.impl.NodeConverter;
import com.marklogic.client.impl.RESTServices;
import com.marklogic.client.io.marker.JSONWriteHandle;

import java.io.InputStream;
import java.util.stream.Stream;

class IOCallerImpl extends BaseCallerImpl {
    private JsonNode      apiDeclaration;
    private String        endpoint;
    private ParamdefImpl  endpointStateParamdef;
    private ParamdefImpl  sessionParamdef;
    private ParamdefImpl  workUnitParamdef;
    private ParamdefImpl  inputParamdef;
    private ReturndefImpl returndef;

    private BaseProxy.DBFunctionRequest requester;

/*
TODO:
     unit tests
     cache db, session, and callerImpl in the bulk caller instance
 */

    IOCallerImpl(JSONWriteHandle apiDeclaration) {
        super();

        this.apiDeclaration = NodeConverter.handleToJsonNode(apiDeclaration);
        if (!this.apiDeclaration.isObject()) {
            throw new IllegalArgumentException(
                    "endpoint declaration must be object: " + this.apiDeclaration.toString()
            );
        }

        this.endpoint = getText(this.apiDeclaration.get("endpoint"));
        if (this.endpoint == null || this.endpoint.length() == 0) {
            throw new IllegalArgumentException(
                    "no endpoint in endpoint declaration: " + this.apiDeclaration.toString()
            );
        }

        int nodeArgCount = 0;

        JsonNode functionParams = this.apiDeclaration.get("params");
        if (functionParams != null) {
            if (!functionParams.isArray()) {
                throw new IllegalArgumentException(
                        "params must be array in endpoint declaration: " + this.apiDeclaration.toString()
                );
            }

            int paramCount = functionParams.size();
            if (paramCount > 0) {
                for (JsonNode functionParam : functionParams) {
                    if (!functionParam.isObject()) {
                        throw new IllegalArgumentException(
                                "parameter must be object in endpoint declaration: " + functionParam.toString()
                        );
                    }
                    ParamdefImpl paramdef = new ParamdefImpl(functionParam);

                    String paramName = paramdef.getParamName();
                    switch(paramName) {
                        case "endpointState":
                            if (paramdef.isMultiple()) {
                                throw new IllegalArgumentException("endpointState parameter cannot be multiple");
                            } else if (!paramdef.isNullable()) {
                                throw new IllegalArgumentException("endpointState parameter must be nullable");
                            }
                            this.endpointStateParamdef = paramdef;
                            nodeArgCount++;
                            break;
                        case "input":
                            if (!paramdef.isMultiple()) {
                                throw new IllegalArgumentException("input parameter must be multiple");
                            } else if (!paramdef.isNullable()) {
                                throw new IllegalArgumentException("input parameter must be nullable");
                            }
                            this.inputParamdef = paramdef;
                            nodeArgCount += 2;
                            break;
                        case "session":
                            if (!"session".equalsIgnoreCase(paramdef.getDataType())) {
                                throw new IllegalArgumentException("session parameter must have session data type");
                            } else if (paramdef.isMultiple()) {
                                throw new IllegalArgumentException("session parameter cannot be multiple");
                            }
                            this.sessionParamdef = paramdef;
                            break;
                        case "workUnit":
                            if (paramdef.isMultiple()) {
                                throw new IllegalArgumentException("workUnit parameter cannot be multiple");
                            }
                            this.workUnitParamdef = paramdef;
                            nodeArgCount++;
                            break;
                        default:
                            throw new IllegalArgumentException("unknown parameter name: "+paramName);
                    }
                }
            }
        }

        JsonNode functionReturn = this.apiDeclaration.get("return");
        if (functionReturn != null) {
            if (!functionReturn.isObject()) {
                throw new IllegalArgumentException(
                        "return must be object in endpoint declaration: "+functionReturn.toString()
                );
            }
            this.returndef = new ReturndefImpl(functionReturn);
            if (!this.returndef.isNullable()) {
                throw new IllegalArgumentException("return must be nullable");
            }
        }

        if (this.endpointStateParamdef != null) {
            if (this.returndef == null) {
                throw new IllegalArgumentException(
                        "endpointState parameter requires return in endpoint: "+this.endpoint
                );
            } else if (this.endpointStateParamdef.getFormat() != this.returndef.getFormat()) {
                throw new IllegalArgumentException(
                        "endpointState format must match return format in endpoint: "+this.endpoint
                );
            }
        }

        this.requester = getBaseProxy().moduleRequest(
                this.endpoint, BaseProxy.ParameterValuesKind.forNodeCount(nodeArgCount)
        );
    }

    BaseProxy.DBFunctionRequest makeRequest(
            DatabaseClient db, InputStream endpointState, SessionState session, InputStream workUnit
    ) {
        return makeRequest(db, endpointState, session, workUnit, null);
    }
    BaseProxy.DBFunctionRequest makeRequest(
            DatabaseClient db, InputStream endpointState, SessionState session, InputStream workUnit, Stream<InputStream> input
    ) {
        BaseProxy.DBFunctionRequest request = this.requester.on(db);

        if (this.sessionParamdef != null) {
            request = request.withSession(
                    this.sessionParamdef.getParamName(), session, this.sessionParamdef.isNullable()
            );
        } else if (session != null) {
            throw new IllegalArgumentException("session not supported by endpoint: "+this.endpoint);
        }

        int fieldNum = 0;

        RESTServices.CallField endpointStateField = null;
        if (this.endpointStateParamdef != null) {
            endpointStateField = BaseProxy.documentParam(
                    "endpointState",
                    this.endpointStateParamdef.isNullable(),
                    NodeConverter.withFormat(
                            NodeConverter.InputStreamToHandle(endpointState), this.endpointStateParamdef.getFormat()
                    ));
            if (endpointState != null)
                fieldNum++;
        } else if (endpointState != null) {
            throw new IllegalArgumentException("endpointState parameter not supported by endpoint: "+this.endpoint);
        }

        RESTServices.CallField workUnitField = null;
        if (this.workUnitParamdef != null) {
            workUnitField = BaseProxy.documentParam(
                    "workUnit",
                    this.workUnitParamdef.isNullable(),
                    NodeConverter.withFormat(
                            NodeConverter.InputStreamToHandle(workUnit), this.workUnitParamdef.getFormat()
                    ));
            if (workUnit != null)
                fieldNum++;
        } else if (endpointState != null) {
            throw new IllegalArgumentException("workUnit parameter not supported by endpoint: "+this.endpoint);
        }

        RESTServices.CallField inputField = null;
        if (this.inputParamdef != null) {
            inputField = BaseProxy.documentParam(
                    "input",
                    this.inputParamdef.isNullable(),
                    NodeConverter.streamWithFormat(
                            NodeConverter.InputStreamToHandle(input), this.inputParamdef.getFormat()
                    ));
            if (input != null)
                fieldNum++;
        } else if (input != null) {
            throw new IllegalArgumentException("input parameter not supported by endpoint: "+this.endpoint);
        }

        if (fieldNum > 0) {
            RESTServices.CallField[] fields = new RESTServices.CallField[fieldNum];
            fieldNum = 0;
            if (endpointStateField != null) {
                fields[fieldNum++] = endpointStateField;
            }
            if (workUnitField != null) {
                fields[fieldNum++] = workUnitField;
            }
            if (inputField != null) {
                fields[fieldNum++] = inputField;
            }

            request = request.withParams(fields);
        }

        return request;
    }
    InputStream responseMaybe(BaseProxy.DBFunctionRequest request) {
        if (this.returndef == null) {
            request.responseNone();
            return null;
        } else if (this.returndef.isMultiple()) {
            throw new UnsupportedOperationException("multiple return from endpoint: "+this.endpoint);
        }

        return request.responseSingle(this.returndef.isNullable(), this.returndef.getFormat())
                      .asInputStream();
    }
    InputStream responseSingle(BaseProxy.DBFunctionRequest request) {
        if (this.returndef == null) {
            throw new UnsupportedOperationException("no return from endpoint: "+this.endpoint);
        } else if (this.returndef.isMultiple()) {
            throw new UnsupportedOperationException("multiple return from endpoint: "+this.endpoint);
        }

        return request.responseSingle(this.returndef.isNullable(), this.returndef.getFormat())
                      .asInputStream();
    }
    Stream<InputStream> responseMultiple(BaseProxy.DBFunctionRequest request) {
        if (this.returndef == null) {
            throw new UnsupportedOperationException("no return from endpoint: "+this.endpoint);
        } else if (!this.returndef.isMultiple()) {
            throw new UnsupportedOperationException("single return from endpoint: "+this.endpoint);
        }

        return request.responseMultiple(this.returndef.isNullable(), this.returndef.getFormat())
                      .asStreamOfInputStream();
    }

    JsonNode getApiDeclaration() {
        return apiDeclaration;
    }
    String getEndpoint() {
        return endpoint;
    }
    ParamdefImpl getEndpointStateParamdef() {
        return endpointStateParamdef;
    }
    ParamdefImpl getSessionParamdef() {
        return sessionParamdef;
    }
    ParamdefImpl getWorkUnitParamdef() {
        return workUnitParamdef;
    }
    ParamdefImpl getInputParamdef() {
        return inputParamdef;
    }
    ReturndefImpl getReturndef() {
        return returndef;
    }
    BaseProxy.DBFunctionRequest getRequester() {
        return requester;
    }
}
