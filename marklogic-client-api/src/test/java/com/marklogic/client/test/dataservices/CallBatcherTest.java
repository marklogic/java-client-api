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
package com.marklogic.client.test.dataservices;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.MarkLogicInternalException;
import com.marklogic.client.dataservices.CallBatcher;
import com.marklogic.client.dataservices.CallManager;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.query.DeleteQueryDefinition;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.test.Common;

public class CallBatcherTest {
	  private final static String ENDPOINT_DIRECTORY = "/javaApi/test/callManager/";
	  private static ObjectMapper objectMapper = new ObjectMapper();
	  private DatabaseClient db      = Common.connect();
	  private CallManager    callMgr = CallManager.on(db);
	  private JacksonHandle serviceHandle;
	  private final static Map<String, Format> NODE_FORMATS = new HashMap<>();
	  private static Map<String, JsonNode> endpointdefs = new HashMap<>();
	    {
	        ObjectNode servicedef = objectMapper.createObjectNode();
	        servicedef.put("endpointDirectory", ENDPOINT_DIRECTORY);
	        serviceHandle = new JacksonHandle(servicedef);
	    }

	  
	  private CallManager.CallableEndpoint installEndpoint(String functionName) {
		  if(functionName == null || functionName.length() == 0)
			  throw new MarkLogicInternalException("Invalid input was sent.");
		  JsonNode endpointdef = endpointdefs.get(functionName);
	      return callMgr.endpoint(serviceHandle, new JacksonHandle(endpointdef), "sjs");
	  }
	  
	  @BeforeClass
	  public static void setup() {
		NODE_FORMATS.put("array", Format.JSON);
		NODE_FORMATS.put("binaryDocument", Format.BINARY);
		NODE_FORMATS.put("jsonDocument", Format.JSON);
		NODE_FORMATS.put("object", Format.JSON);
		NODE_FORMATS.put("textDocument", Format.TEXT);
		NODE_FORMATS.put("xmlDocument", Format.XML);

		DatabaseClient adminClient = Common.newServerAdminClient("java-unittest-modules");
		JSONDocumentManager docMgr = adminClient.newJSONDocumentManager();

		DocumentMetadataHandle docMeta = new DocumentMetadataHandle();

		docMeta.getPermissions().add("rest-reader", DocumentMetadataHandle.Capability.EXECUTE);
		setupNoParamReturnEndpoint(docMgr, docMeta, "noParamReturn", "double", "1.2");
		setupEndpointSingleRequired(docMgr, docMeta, "oneParam", "double");
		setupEndpointMultipleRequired(docMgr, docMeta, "float", "manyParam");
		
		adminClient.release();
	  }
	  
	  @Test
	  public void noneForArgsTest() {
		  CallManager.CallableEndpoint callableEndpoint = installEndpoint("noParamReturn");
		  CallManager.NoneCaller noneCaller = callableEndpoint.returningNone();
		  
		  CallBatcher<CallManager.CallArgs,CallManager.CallEvent> batcher = noneCaller.batcher().forArgs();
		  assertNotNull(batcher);
		  ArrayList<String> assignedParams = new ArrayList<String>();
		  batcher.onCallSuccess(event-> { 
			  assignedParams.add(event.getArgs().getAssignedParamNames()[0]);
		  });
		  
		  batcher.getDataMovementManager().startJob(batcher);
		  
	      batcher.add(noneCaller.args().param("param1", 1.2));

	      batcher.flushAndWait();
	      assertTrue(assignedParams.size()!=0);
	      assertTrue(assignedParams.get(0).contains("param1"));
	  }
	  
	  @Test
	  public void oneForArgsTest() {
		  CallManager.CallableEndpoint callableEndpoint = installEndpoint("oneParam");
		  CallManager.OneCaller<Double> oneCaller = callableEndpoint.returningOne(Double.class);
		  
		  CallBatcher<CallManager.CallArgs,CallManager.OneCallEvent<Double>> batcher = oneCaller.batcher().forArgs();
		  assertNotNull(batcher);
		  ArrayList<String> assignedParams = new ArrayList<String>();
		  batcher.onCallSuccess(event-> { 
			  assignedParams.add(event.getArgs().getAssignedParamNames()[0]);
		  });
		  
		  batcher.getDataMovementManager().startJob(batcher);
		  
	      batcher.add(oneCaller.args().param("param1", 1.2));

	      batcher.flushAndWait();
	      assertTrue(assignedParams.size()!=0);
	      assertTrue(assignedParams.get(0).contains("param1"));
	  }
	  
	  @Test
	  public void manyForArgsTest() {
		  CallManager.CallableEndpoint callableEndpoint = installEndpoint("manyParam");
		  CallManager.ManyCaller<Float> manyCaller = callableEndpoint.returningMany(Float.class);
		  
		  CallBatcher<CallManager.CallArgs,CallManager.ManyCallEvent<Float>> batcher = manyCaller.batcher().forArgs();
		  assertNotNull(batcher);
		  Float[] values = new Float[]{1.8f, 2.4f};
		  ArrayList<String> assignedParams = new ArrayList<String>();
		  batcher.onCallSuccess(event-> { 
			  assignedParams.add(event.getArgs().getAssignedParamNames()[0]);
		  });
		  
		  batcher.getDataMovementManager().startJob(batcher);
		  
	      batcher.add(manyCaller.args().param("param1", values));

	      batcher.flushAndWait();
	      
	      assertTrue(assignedParams.size()!=0);
	      assertTrue(assignedParams.get(0).contains("param1"));
	  }
	  
	    @AfterClass
	    public static void release() {
	        DatabaseClient adminClient = Common.newServerAdminClient("java-unittest-modules");
	
	        QueryManager queryMgr = adminClient.newQueryManager();
	        DeleteQueryDefinition deletedef = queryMgr.newDeleteDefinition();
	        deletedef.setDirectory(ENDPOINT_DIRECTORY);
	        queryMgr.delete(deletedef);
	
	        adminClient.release();
	       
	    }
	    
	    private static void setupNoParamReturnEndpoint(
	            JSONDocumentManager docMgr, DocumentMetadataHandle docMeta, String functionName, String datatype, String returnVal
	    ) {
	        JsonNode endpointdef = getEndpointdef(functionName, datatype, null, null, false, false);
	        String script = getScript(datatype, null, null, false, false);
	        setupEndpoint(docMgr, docMeta, endpointdef, script);
	    }
	    
	    private static void setupEndpointMultipleRequired(JSONDocumentManager docMgr, DocumentMetadataHandle docMeta, String datatype, String functionName) {
	        JsonNode endpointdef = getEndpointdef(functionName, datatype, true, false);
	        String script = getScript(datatype, null, null, true, false);
	        setupEndpoint(docMgr, docMeta, endpointdef, script);
	    }
	    
	    private static void setupEndpointSingleRequired(
	            JSONDocumentManager docMgr, DocumentMetadataHandle docMeta, String functionName, String datatype
	    ) {
	        JsonNode endpointdef = getEndpointdef(functionName, datatype, false, false);
	        String script = getScript(datatype,null,null, false, false);
	        setupEndpoint(docMgr, docMeta, endpointdef, script);
	    }
	    
	    private static void setupEndpoint(JSONDocumentManager docMgr, DocumentMetadataHandle docMeta, JsonNode endpointdef, String script) {
	        String functionName = endpointdef.get("functionName").asText();
	        String baseUri      = ENDPOINT_DIRECTORY + functionName;
	        docMgr.write(baseUri+".api", docMeta, new JacksonHandle(endpointdef));
	        docMgr.write(baseUri+".sjs", docMeta, new StringHandle(script));

	        endpointdefs.put(functionName, endpointdef);
	    }
	    
	    private static JsonNode getEndpointdef(String functionName, String datatype, boolean isMultiple, boolean isNullable) {
	        return getEndpointdef(functionName, datatype, null, datatype, isMultiple, isNullable);
	    }
	    private static JsonNode getEndpointdef(
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
	    
	    private static String getScript(
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
	  
}