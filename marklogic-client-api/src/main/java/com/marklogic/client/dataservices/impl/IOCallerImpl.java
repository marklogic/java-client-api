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

abstract class IOCallerImpl extends BaseCallerImpl {
    private JsonNode      apiDeclaration;
    private String        endpointPath;
    private ParamdefImpl  endpointStateParamdef;
    private ParamdefImpl  sessionParamdef;
    private ParamdefImpl  workUnitParamdef;
    private ParamdefImpl  inputParamdef;
    private ReturndefImpl returndef;

    private BaseProxy.DBFunctionRequest requester;

    IOCallerImpl(JSONWriteHandle apiDeclaration) {
        super();

        this.apiDeclaration = NodeConverter.handleToJsonNode(apiDeclaration);
        if (!this.apiDeclaration.isObject()) {
            throw new IllegalArgumentException(
                    "endpoint declaration must be object: " + this.apiDeclaration.toString()
            );
        }

        this.endpointPath = getText(this.apiDeclaration.get("endpoint"));
        if (this.endpointPath == null || this.endpointPath.length() == 0) {
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
                        "endpointState parameter requires return in endpoint: "+getEndpointPath()
                );
            } else if (this.endpointStateParamdef.getFormat() != this.returndef.getFormat()) {
                throw new IllegalArgumentException(
                        "endpointState format must match return format in endpoint: "+getEndpointPath()
                );
            }
        }

        this.requester = getBaseProxy().moduleRequest(
                getEndpointPath(), BaseProxy.ParameterValuesKind.forNodeCount(nodeArgCount)
        );
    }

    BaseProxy.DBFunctionRequest makeRequest(
            DatabaseClient db, InputStream endpointState, SessionState session, InputStream workUnit
    ) {
        return makeRequest(db, endpointState, session, workUnit, (RESTServices.CallField) null);
    }
    BaseProxy.DBFunctionRequest makeRequest(
            DatabaseClient db, InputStream endpointState, SessionState session, InputStream workUnit, Stream<InputStream> input
    ) {
        RESTServices.CallField inputField = null;

        ParamdefImpl paramdef = getInputParamdef();
        if (paramdef != null) {
            inputField = BaseProxy.documentParam(
                    "input",
                    paramdef.isNullable(),
                    NodeConverter.streamWithFormat(NodeConverter.InputStreamToHandle(input), paramdef.getFormat())
                    );
        } else if (input != null) {
            throw new IllegalArgumentException("input parameter not supported by endpoint: "+getEndpointPath());
        }

        return makeRequest(db, endpointState, session, workUnit, inputField);
    }
    BaseProxy.DBFunctionRequest makeRequest(
            DatabaseClient db, InputStream endpointState, SessionState session, InputStream workUnit, InputStream[] input
    ) {
        RESTServices.CallField inputField = null;

        ParamdefImpl paramdef = getInputParamdef();
        if (paramdef != null) {
            inputField = BaseProxy.documentParam(
                    "input",
                    paramdef.isNullable(),
                    NodeConverter.arrayWithFormat(NodeConverter.InputStreamToHandle(input), paramdef.getFormat())
            );
        } else if (input != null && input.length > 0) {
            throw new IllegalArgumentException("input parameter not supported by endpoint: "+getEndpointPath());
        }

        return makeRequest(db, endpointState, session, workUnit, inputField);
    }
    BaseProxy.DBFunctionRequest makeRequest(
            DatabaseClient db, InputStream endpointState, SessionState session, InputStream workUnit, RESTServices.CallField inputField
    ) {
        BaseProxy.DBFunctionRequest request = getRequester().on(db);

        if (getSessionParamdef() != null) {
            request = request.withSession(
                    getSessionParamdef().getParamName(), session, getSessionParamdef().isNullable()
            );
        } else if (session != null) {
            throw new IllegalArgumentException("session not supported by endpoint: "+getEndpointPath());
        }

        int fieldNum = 0;

        RESTServices.CallField endpointStateField = null;
        if (getEndpointStateParamdef() != null) {
            endpointStateField = BaseProxy.documentParam(
                    "endpointState",
                    getEndpointStateParamdef().isNullable(),
                    NodeConverter.withFormat(
                            NodeConverter.InputStreamToHandle(endpointState), getEndpointStateParamdef().getFormat()
                    ));
            if (endpointState != null)
                fieldNum++;
        } else if (endpointState != null) {
            throw new IllegalArgumentException("endpointState parameter not supported by endpoint: "+getEndpointPath());
        }

        RESTServices.CallField workUnitField = null;
        if (getWorkUnitParamdef() != null) {
            workUnitField = BaseProxy.documentParam(
                    "workUnit",
                    getWorkUnitParamdef().isNullable(),
                    NodeConverter.withFormat(
                            NodeConverter.InputStreamToHandle(workUnit), getWorkUnitParamdef().getFormat()
                    ));
            if (workUnit != null)
                fieldNum++;
        } else if (endpointState != null) {
            throw new IllegalArgumentException("workUnit parameter not supported by endpoint: "+getEndpointPath());
        }

        if (inputField != null)
            fieldNum++;

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
        if (getReturndef() == null) {
            request.responseNone();
            return null;
        } else if (getReturndef().isMultiple()) {
            throw new UnsupportedOperationException("multiple return from endpoint: "+getEndpointPath());
        }

        return request.responseSingle(getReturndef().isNullable(), getReturndef().getFormat())
                      .asInputStream();
    }
    InputStream responseSingle(BaseProxy.DBFunctionRequest request) {
        if (getReturndef() == null) {
            throw new UnsupportedOperationException("no return from endpoint: "+getEndpointPath());
        } else if (getReturndef().isMultiple()) {
            throw new UnsupportedOperationException("multiple return from endpoint: "+getEndpointPath());
        }

        return request.responseSingle(getReturndef().isNullable(), getReturndef().getFormat())
                      .asInputStream();
    }
    Stream<InputStream> responseMultipleAsStream(BaseProxy.DBFunctionRequest request) {
        return responseMultiple(request).asStreamOfInputStream();
    }
    InputStream[] responseMultipleAsArray(BaseProxy.DBFunctionRequest request) {
        return responseMultiple(request).asArrayOfInputStream();
    }
    private RESTServices.MultipleCallResponse responseMultiple(BaseProxy.DBFunctionRequest request) {
        if (getReturndef() == null) {
            throw new UnsupportedOperationException("no return from endpoint: "+getEndpointPath());
        } else if (!getReturndef().isMultiple()) {
            throw new UnsupportedOperationException("single return from endpoint: "+getEndpointPath());
        }

        return request.responseMultiple(getReturndef().isNullable(), getReturndef().getFormat());
    }

    JsonNode getApiDeclaration() {
        return this.apiDeclaration;
    }

    String getEndpointPath() {
        return this.endpointPath;
    }

    ParamdefImpl getEndpointStateParamdef() {
        return this.endpointStateParamdef;
    }
    ParamdefImpl getSessionParamdef() {
        return this.sessionParamdef;
    }
    ParamdefImpl getWorkUnitParamdef() {
        return this.workUnitParamdef;
    }
    ParamdefImpl getInputParamdef() {
        return this.inputParamdef;
    }
    ReturndefImpl getReturndef() {
        return this.returndef;
    }
    BaseProxy.DBFunctionRequest getRequester() {
        return this.requester;
    }
}
