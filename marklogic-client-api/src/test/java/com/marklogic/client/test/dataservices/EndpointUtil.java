package com.marklogic.client.test.dataservices;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.dataservices.CallManager;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.StringHandle;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNotNull;

class EndpointUtil {
    private final static Map<String, Format> NODE_FORMATS = new HashMap<>();
    {
        NODE_FORMATS.put("array",          Format.JSON);
        NODE_FORMATS.put("binaryDocument", Format.BINARY);
        NODE_FORMATS.put("jsonDocument",   Format.JSON);
        NODE_FORMATS.put("object",         Format.JSON);
        NODE_FORMATS.put("textDocument",   Format.TEXT);
        NODE_FORMATS.put("xmlDocument",    Format.XML);
    }

    private CallManager callMgr;
    private String endpointDirectory;
    private Map<String, JsonNode> endpointdefs = new HashMap<>();
    private ObjectMapper objectMapper = new ObjectMapper();
    private JacksonHandle serviceHandle;

    EndpointUtil(CallManager callMgr, String endpointDirectory) {
        if (callMgr == null)
            throw new IllegalArgumentException("CallManager cannot be null");
        this.callMgr = callMgr;
        if (endpointDirectory == null || endpointDirectory.length()==0)
            throw new IllegalArgumentException("Endpoint Directory cannot be null or empty");
        this.callMgr = callMgr;
        this.endpointDirectory = endpointDirectory;

        ObjectNode servicedef = objectMapper.createObjectNode();
        servicedef.put("endpointDirectory", endpointDirectory);
        this.serviceHandle = new JacksonHandle(servicedef);
    }

    CallManager.CallableEndpoint makeCallableEndpoint(String functionName) {
        JsonNode endpointdef = endpointdefs.get(functionName);
        assertNotNull("no endpoint definition found for "+functionName, endpointdef);
        return callMgr.endpoint(serviceHandle, new JacksonHandle(endpointdef), "sjs");
    }

    CallManager.CallableEndpoint installEndpoint(String functionName) {
        if (functionName == null || functionName.length() == 0)
            throw new IllegalArgumentException("Null or empty function name");
        JsonNode endpointdef = endpointdefs.get(functionName);
        if (endpointdef == null)
            throw new IllegalArgumentException("No endpoint definition of name: "+functionName);
        return callMgr.endpoint(serviceHandle, new JacksonHandle(endpointdef), "sjs");
    }

    void setupParamNoReturnEndpoint(
            JSONDocumentManager docMgr, DocumentMetadataHandle docMeta, String functionName, String datatype
    ) {
        JsonNode endpointdef = getEndpointdef(functionName, datatype, null, null, false, false);
        String script = getScript(datatype, null, null, false, false);
        setupEndpoint(docMgr, docMeta, endpointdef, script);
    }
    void setupNoParamReturnEndpoint(
            JSONDocumentManager docMgr, DocumentMetadataHandle docMeta, String functionName, String datatype, String returnVal
    ) {
        JsonNode endpointdef = getEndpointdef(functionName, null, null, datatype, false, false);
        String script = getScript(null, null, returnVal, false, false);
        setupEndpoint(docMgr, docMeta, endpointdef, script);
    }
    void setupNoParamNoReturnEndpoint(
            JSONDocumentManager docMgr, DocumentMetadataHandle docMeta, String functionName
    ) {
        JsonNode endpointdef = getEndpointdef(functionName, null, null, null, false, false);
        String script = getScript(null, null, null, false, false);
        setupEndpoint(docMgr, docMeta, endpointdef, script);
    }
    void setupTwoParamEndpoint(
            JSONDocumentManager docMgr, DocumentMetadataHandle docMeta, String functionName, String datatype, String paramType2
    ) {
        setupTwoParamEndpoint(docMgr, docMeta, functionName, datatype, paramType2, false);
    }
    void setupTwoParamEndpoint(
            JSONDocumentManager docMgr, DocumentMetadataHandle docMeta, String functionName, String datatype,
            String paramType2, boolean isMultiple
    ) {
        JsonNode endpointdef = getEndpointdef(functionName, datatype, paramType2, datatype, isMultiple, false);
        String script = getScript(datatype, paramType2, null, isMultiple, false);
        setupEndpoint(docMgr, docMeta, endpointdef, script);
    }
    void setupEndpointSingleNulled(
            JSONDocumentManager docMgr, DocumentMetadataHandle docMeta, String functionName, String datatype
    ) {
        JsonNode endpointdef = getEndpointdef(functionName, datatype, false, true);
        String script = getScript(datatype, false, true);
        setupEndpoint(docMgr, docMeta, endpointdef, script);
    }
    void setupEndpointSingleRequired(
            JSONDocumentManager docMgr, DocumentMetadataHandle docMeta, String functionName, String datatype
    ) {
        JsonNode endpointdef = getEndpointdef(functionName, datatype, false, false);
        String script = getScript(datatype, false, false);
        setupEndpoint(docMgr, docMeta, endpointdef, script);
    }
    void setupEndpointMultipleNulled(
            JSONDocumentManager docMgr, DocumentMetadataHandle docMeta, String functionName, String datatype
    ) {
        JsonNode endpointdef = getEndpointdef(functionName, datatype, true, true);
        String script = getScript(datatype, true, true);
        setupEndpoint(docMgr, docMeta, endpointdef, script);
    }
    void setupEndpointMultipleRequired(JSONDocumentManager docMgr, DocumentMetadataHandle docMeta, String datatype) {
        JsonNode endpointdef = getEndpointdef(datatype, datatype, true, false);
        String script = getScript(datatype, true, false);
        setupEndpoint(docMgr, docMeta, endpointdef, script);
    }
    void setupEndpoint(JSONDocumentManager docMgr, DocumentMetadataHandle docMeta, JsonNode endpointdef, String script) {
        String functionName = endpointdef.get("functionName").asText();
        String baseUri      = endpointDirectory + functionName;
        docMgr.write(baseUri+".api", docMeta, new JacksonHandle(endpointdef));
        docMgr.write(baseUri+".sjs", docMeta, new StringHandle(script));

        endpointdefs.put(functionName, endpointdef);
    }

    JsonNode getEndpointdef(String functionName, String datatype, boolean isMultiple, boolean isNullable) {
        return getEndpointdef(functionName, datatype, null, datatype, isMultiple, isNullable);
    }
    JsonNode getEndpointdef(
            String functionName, String paramType1, String paramType2, String returnType, boolean isMultiple, boolean isNullable
    ) {
        ObjectNode endpointdef = objectMapper.createObjectNode();
        endpointdef.put("functionName", functionName);
        if (paramType1 != null) {
            ArrayNode paramdefs  = objectMapper.createArrayNode();
            ObjectNode paramdef = objectMapper.createObjectNode();
            paramdef.put("name", "param1");
            paramdef.put("datatype", paramType1);
            paramdef.put("multiple", isMultiple);
            paramdef.put("nullable", isNullable);
            paramdefs.add(paramdef);
            if (paramType2 != null) {
                paramdef = objectMapper.createObjectNode();
                paramdef.put("name", "param2");
                paramdef.put("datatype", paramType2);
                paramdef.put("multiple", !isMultiple);
                paramdef.put("nullable", isNullable);
                paramdefs.add(paramdef);
            }
            endpointdef.set("params", paramdefs);
        }
        if (returnType != null) {
            ObjectNode returndef = objectMapper.createObjectNode();
            returndef.put("datatype", returnType);
            returndef.put("multiple", isMultiple);
            returndef.put("nullable", isNullable);
            endpointdef.set("return", returndef);
        }
        return endpointdef;
    }

    String getScript(String datatype, boolean isMultiple, boolean isNullable) {
        return getScript(datatype, null, null, isMultiple, isNullable);
    }

    String getScript(
            String paramType1, String paramType2, String returnVal, boolean isMultiple, boolean isNullable
    ) {
        StringBuilder scriptBldr = new StringBuilder()
                .append("'use strict';\n");
        if (paramType1 != null) {
            scriptBldr = scriptBldr
                    .append("var param1;\n");
            if (paramType2 != null) {
                scriptBldr = scriptBldr
                        .append("var param2;\n");
            }
        }

        if (paramType1 != null) {
            if (isNullable) {
                scriptBldr = scriptBldr
                        .append("if (fn.count(param1) != 0)\n")
                        .append("  fn.error(null, 'TEST_ERROR',\n")
                        .append("    'received ' + fn.count(param1) + ' instead of no values');\n");
            } else if (isMultiple) {
                scriptBldr = scriptBldr
                        .append("if (fn.count(param1) < 2)\n")
                        .append("  fn.error(null, 'TEST_ERROR',\n")
                        .append("    'received ' + fn.count(param1) + ' instead of multiple values');\n")
                        .append("const value1 = fn.head(param1);\n");
            } else {
                scriptBldr = scriptBldr
                        .append("const value1 = param1;\n");
            }
            if (paramType2 != null) {
                if (!isMultiple) {
                    scriptBldr = scriptBldr
                            .append("if (fn.count(param2) < 2)\n")
                            .append("  fn.error(null, 'TEST_ERROR',\n")
                            .append("    'received ' + fn.count(param2) + ' instead of multiple values');\n")
                            .append("const value2 = fn.head(param2);\n");
                } else {
                    scriptBldr = scriptBldr
                            .append("const value2 = param2;\n");
                }
            }

            Format documentFormat = isNullable ? null : NODE_FORMATS.get(paramType1);
            if (isNullable) {
                scriptBldr = scriptBldr
                        .append("const isValid = true;\n");
            } else if (documentFormat != null) {
                scriptBldr = scriptBldr
                        .append("const isValid = ((value1 instanceof Document) ?\n")
                        .append("    value1.documentFormat == '").append(documentFormat.name()).append("' :\n")
                        .append("    xdmp.nodeKind(value1) == '").append(paramType1).append("'\n")
                        .append("    );\n");
            } else {
                scriptBldr = scriptBldr
                        .append("const isValid = (\n")
                        .append("    fn.localNameFromQName(xdmp.type(value1)) == '").append(paramType1).append("' ||\n")
                        .append("    xdmp.castableAs('http://www.w3.org/2001/XMLSchema', '").append(paramType1).append("', value1)\n")
                        .append("    );\n");
            }
            if (paramType2 != null) {
                Format documentFormat2 = isNullable ? null : NODE_FORMATS.get(paramType2);
                if (documentFormat2 != null) {
                    scriptBldr = scriptBldr
                            .append("const isValid2 = ((value2 instanceof Document) ?\n")
                            .append("    value2.documentFormat == '").append(documentFormat2.name()).append("' :\n")
                            .append("    xdmp.nodeKind(value2) == '").append(paramType2).append("'\n")
                            .append("    );\n");
                } else {
                    scriptBldr = scriptBldr
                            .append("const isValid2 = (\n")
                            .append("    fn.localNameFromQName(xdmp.type(value2)) == '").append(paramType2).append("' ||\n")
                            .append("    xdmp.castableAs('http://www.w3.org/2001/XMLSchema', '").append(paramType2).append("', value2)\n")
                            .append("    );\n");
                }
            }

            scriptBldr = scriptBldr
                    .append("if (!isValid)\n")
                    .append("  fn.error(null, 'TEST_ERROR',\n")
                    .append("    'param1 set to ' + Object.prototype.toString.call(value1) +")
                    .append("    ' instead of ").append(paramType1).append(" value');\n");
            if (paramType2 != null) {
                scriptBldr = scriptBldr
                        .append("if (!isValid2)\n")
                        .append("  fn.error(null, 'TEST_ERROR',\n")
                        .append("    'param2 set to ' + Object.prototype.toString.call(value2) +")
                        .append("    ' instead of ").append(paramType2).append(" value');\n");
            }

            scriptBldr = scriptBldr
                    .append("param1;");
        } else if (returnVal != null) {
            scriptBldr = scriptBldr
                    .append(returnVal)
                    .append(";");
        }

        return scriptBldr.toString();
    }

    <T> CallManager.ManyCaller<T> makeManyCaller(CallManager.CallableEndpoint callableEndpoint, Class<T> as) {
        return callableEndpoint.returningMany(as);
    }

    <T> CallManager.OneCaller<T> makeOneCaller(CallManager.CallableEndpoint callableEndpoint, Class<T> as) {
        return callableEndpoint.returningOne(as);
    }
}
