/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.dataservices.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.SessionState;
import com.marklogic.client.impl.BaseProxy;
import com.marklogic.client.impl.NodeConverter;
import com.marklogic.client.impl.RESTServices;
import com.marklogic.client.io.BaseHandle;
import com.marklogic.client.io.BytesHandle;
import com.marklogic.client.io.marker.BufferableContentHandle;
import com.marklogic.client.io.marker.JSONWriteHandle;

abstract class IOCallerImpl<I,O> extends BaseCallerImpl {
    private final JsonNode                    apiDeclaration;
    private final String                      endpointPath;
    private final BaseProxy.DBFunctionRequest requester;

    private ParamdefImpl  endpointStateParamdef;
    private ParamdefImpl  sessionParamdef;
    private ParamdefImpl  endpointConstantsParamdef;
    private ParamdefImpl  inputParamdef;
    private ReturndefImpl returndef;

    private final HandleProvider<I,O> handleProvider;

    IOCallerImpl(JSONWriteHandle apiDeclaration, HandleProvider<I,O> handleProvider) {
        super();
        if (apiDeclaration == null) {
            throw new IllegalArgumentException("null endpoint declaration");
        } else if (handleProvider == null) {
            throw new IllegalArgumentException("null handle provider");
        }

        this.apiDeclaration = NodeConverter.handleToJsonNode(apiDeclaration);
        if (!this.apiDeclaration.isObject()) {
            throw new IllegalArgumentException(
                    "endpoint declaration must be object: " + this.apiDeclaration
            );
        }

        this.handleProvider = handleProvider;

        this.endpointPath = getText(this.apiDeclaration.get("endpoint"));
        if (this.endpointPath == null || this.endpointPath.length() == 0) {
            throw new IllegalArgumentException(
                    "no endpoint in endpoint declaration: " + this.apiDeclaration
            );
        }

        int nodeArgCount = 0;

        BaseHandle<?,?> inputHandleBase  = (BaseHandle<?,?>) handleProvider.getInputHandle();
        BaseHandle<?,?> outputHandleBase = (BaseHandle<?,?>) handleProvider.getOutputHandle();

        JsonNode functionParams = this.apiDeclaration.get("params");
        if (functionParams != null) {
            if (!functionParams.isArray()) {
                throw new IllegalArgumentException(
                        "params must be array in endpoint declaration: " + this.apiDeclaration
                );
            }

            int paramCount = functionParams.size();
            if (paramCount > 0) {
                for (JsonNode functionParam : functionParams) {
                    if (!functionParam.isObject()) {
                        throw new IllegalArgumentException(
                                "parameter must be object in endpoint declaration: " + functionParam
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
                            if (inputHandleBase == null) {
                                throw new IllegalArgumentException("no input handle provided for input parameter");
                            }
                            inputHandleBase.setFormat(paramdef.getFormat());
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
                        case "endpointConstants":
                        case "workUnit":
                            if (this.endpointConstantsParamdef != null) {
                                throw new IllegalArgumentException("can only declare one of "+paramName+" and "+
                                        this.endpointConstantsParamdef.getParamName());
                            } else if (paramdef.isMultiple()) {
                                throw new IllegalArgumentException(paramName+" parameter cannot be multiple");
                            }
                            this.endpointConstantsParamdef  = paramdef;
                            nodeArgCount++;
                            break;
                        default:
                            throw new IllegalArgumentException("unknown parameter name: "+paramName);
                    }
                }
            }
        }
        if (this.inputParamdef == null && inputHandleBase != null) {
            throw new IllegalArgumentException("no input parameter declared but input handle provided");
        }

        JsonNode functionReturn = this.apiDeclaration.get("return");
        if (functionReturn != null) {
            if (!functionReturn.isObject()) {
                throw new IllegalArgumentException(
                        "return must be object in endpoint declaration: "+ functionReturn
                );
            }
            this.returndef = new ReturndefImpl(functionReturn);
            if (!this.returndef.isNullable()) {
                throw new IllegalArgumentException("return must be nullable");
            }
            if (outputHandleBase != null) {
                outputHandleBase.setFormat(this.returndef.getFormat());
            } else if (this.endpointStateParamdef == null) {
                throw new IllegalArgumentException("no output handle provided for return values");
            }
        } else if (outputHandleBase != null) {
            throw new IllegalArgumentException("no return values declared but output handle provided");
        }

        if (this.endpointStateParamdef != null) {
            if (this.returndef == null) {
                throw new IllegalArgumentException(
                        "endpointState parameter requires return in endpoint: "+getEndpointPath()
                );
            } else if (this.endpointStateParamdef.getFormat() != this.returndef.getFormat() && !"anyDocument".equals(this.returndef.getDataType())) {
                throw new IllegalArgumentException(
                        "endpointState format must match return format in endpoint: "+getEndpointPath()
                );
            }
        }

        this.requester = BaseProxy.moduleRequest(
                getEndpointPath(), BaseProxy.ParameterValuesKind.forNodeCount(nodeArgCount)
        );
    }

    BufferableContentHandle<?,?>[] bufferableInputHandleOn(I[] input) {
        return handleProvider.bufferableInputHandleOn(input);
    }
    I[] newContentInputArray(int length) {
        return handleProvider.newInputArray(length);
    }
    O[] newContentOutputArray(int length) {
        return handleProvider.newOutputArray(length);
    }

    BaseProxy.DBFunctionRequest makeRequest(DatabaseClient db, CallContextImpl<I,O> callCtxt) {
        return makeRequest(db, callCtxt, (RESTServices.CallField) null);
    }
    BaseProxy.DBFunctionRequest makeRequest(
            DatabaseClient db, CallContextImpl<I,O> callCtxt, BufferableContentHandle<?,?>[] input
    ) {
        RESTServices.CallField inputField = null;

        ParamdefImpl paramdef = getInputParamdef();
        if (paramdef != null) {
            inputField = BaseProxy.documentParam("input", paramdef.isNullable(), input);
        } else if (input != null && input.length > 0) {
            throw new IllegalArgumentException("input parameter not supported by endpoint: "+getEndpointPath());
        }

        return makeRequest(db, callCtxt, inputField);
    }
    private BaseProxy.DBFunctionRequest makeRequest(
            DatabaseClient db, CallContextImpl<I,O> callCtxt, RESTServices.CallField inputField
    ) {
        BaseProxy.DBFunctionRequest request = getRequester().on(db);

        SessionState session = callCtxt.getSessionState();
        if (getSessionParamdef() != null) {
            request = request.withSession(
                    getSessionParamdef().getParamName(), session, getSessionParamdef().isNullable()
            );
        } else if (session != null) {
            throw new IllegalArgumentException("session not supported by endpoint: "+getEndpointPath());
        }

        int fieldNum = 0;

        RESTServices.CallField endpointStateField = null;
        BytesHandle endpointState = callCtxt.getEndpointState();
        if (getEndpointStateParamdef() != null) {
            endpointStateField = BaseProxy.documentParam(
                    "endpointState",
                    getEndpointStateParamdef().isNullable(),
                    NodeConverter.withFormat(endpointState, getEndpointStateParamdef().getFormat())
                    );
            if (endpointState != null)
                fieldNum++;
        } else if (endpointState != null) {
            throw new IllegalArgumentException("endpointState parameter not supported by endpoint: "+getEndpointPath());
        }

        RESTServices.CallField endpointConstantsField = null;
        BytesHandle endpointConstants = callCtxt.getEndpointConstants();
        if (getEndpointConstantsParamdef() != null) {
            endpointConstantsField = BaseProxy.documentParam(
                    getEndpointConstantsParamdef().getParamName(),
                    getEndpointConstantsParamdef().isNullable(),
                    NodeConverter.withFormat(endpointConstants, getEndpointConstantsParamdef().getFormat())
                    );
            if (endpointConstants != null)
                fieldNum++;
        } else if (endpointConstants != null) {
            throw new IllegalArgumentException(callCtxt.getEndpointConstantsParamName()+
                    " parameter not supported by endpoint: "+getEndpointPath());
        }

        if (inputField != null)
            fieldNum++;

        if (fieldNum > 0) {
            RESTServices.CallField[] fields = new RESTServices.CallField[fieldNum];
            fieldNum = 0;
            if (endpointStateField != null) {
                fields[fieldNum++] = endpointStateField;
            }
            if (endpointConstantsField != null) {
                fields[fieldNum++] = endpointConstantsField;
            }
            if (inputField != null) {
                fields[fieldNum++] = inputField;
            }

            request = request.withParams(fields);
        }

        return request;
    }
    boolean responseWithState(BaseProxy.DBFunctionRequest request, CallContextImpl<I,O> callCtxt) {
        if (getReturndef() == null) {
            request.responseNone();
            return false;
        } else if (getReturndef().isMultiple()) {
            throw new UnsupportedOperationException("multiple return from endpoint: "+getEndpointPath());
        }

        return request.responseSingle(getReturndef().isNullable(), getReturndef().getFormat())
               .asEndpointState(callCtxt.getEndpointState());
    }
    O[] responseMultipleAsArray(BaseProxy.DBFunctionRequest request, CallContextImpl<I,O> callCtxt) {
        return handleProvider.outputAsArray(callCtxt, responseMultiple(request));
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
    ParamdefImpl getEndpointConstantsParamdef() {
        return this.endpointConstantsParamdef;
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
