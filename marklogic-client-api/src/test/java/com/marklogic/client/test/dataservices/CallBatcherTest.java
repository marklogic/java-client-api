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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.MarkLogicInternalException;
import com.marklogic.client.dataservices.CallBatcher;
import com.marklogic.client.dataservices.CallManager;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.query.DeleteQueryDefinition;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.test.Common;
import com.marklogic.client.test.dataservices.CallManagerTest.EndpointSetup;

public class CallBatcherTest {
	
	  private final static String ENDPOINT_DIRECTORY = "/javaApi/test/callManager/";
	  private static EndpointSetup endpointSetup = new EndpointSetup(ENDPOINT_DIRECTORY);
	  private static ObjectMapper objectMapper = new ObjectMapper();
	  
	  private DatabaseClient db      = Common.connect();
	  private CallManager    callMgr = CallManager.on(db);
	  private JacksonHandle serviceHandle;
	    {
	        ObjectNode servicedef = objectMapper.createObjectNode();
	        servicedef.put("endpointDirectory", ENDPOINT_DIRECTORY);
	        serviceHandle = new JacksonHandle(servicedef);
	    }

	  
	  private CallManager.CallableEndpoint installEndpoint(String functionName) {
		  if(functionName == null || functionName.length() == 0)
			  throw new MarkLogicInternalException("Invalid input was sent.");
		  JsonNode endpointdef = endpointSetup.endpointdefs.get(functionName);
	      return callMgr.endpoint(serviceHandle, new JacksonHandle(endpointdef), "sjs");
	  }
	  
	  @BeforeClass
	  public static void setup() {

		DatabaseClient adminClient = Common.newServerAdminClient("java-unittest-modules");
		JSONDocumentManager docMgr = adminClient.newJSONDocumentManager();

		DocumentMetadataHandle docMeta = new DocumentMetadataHandle();

		docMeta.getPermissions().add("rest-reader", DocumentMetadataHandle.Capability.EXECUTE);
		endpointSetup.setupParamNoReturnEndpoint(docMgr, docMeta, "paramNoReturn", "double");
		endpointSetup.setupEndpointSingleRequired(docMgr, docMeta, "oneParam", "double");
		endpointSetup.setupEndpointMultipleRequired(docMgr, docMeta, "float");
		
		adminClient.release();
	  }
	  
	  @Test
	  public void noneForArgsTest() {
		  CallManager.CallableEndpoint callableEndpoint = installEndpoint("paramNoReturn");
		  CallManager.NoneCaller noneCaller = callableEndpoint.returningNone();
		  
		  CallBatcher<CallManager.CallArgs,CallManager.CallEvent> batcher = noneCaller.batcher().forArgs();
		  assertNotNull(batcher);
		  String[] assignedParams = {""};
		  batcher.onCallSuccess(event-> {
				  assignedParams[0] = event.getArgs().getAssignedParamNames()[0];
		  });
		  
		  batcher.getDataMovementManager().startJob(batcher);
		  
	      batcher.add(noneCaller.args().param("param1", 1.2));

	      batcher.flushAndWait();
	      batcher.getDataMovementManager().stopJob(batcher);
	      
	      assertEquals("Invalid number of parameters", assignedParams.length, 1);
	      assertEquals("Param values are not equal",assignedParams[0],("param1"));
	  }
	  
	  @Test
	  public void oneForArgsTest() {
		  CallManager.CallableEndpoint callableEndpoint = installEndpoint("oneParam");
		  CallManager.OneCaller<Double> oneCaller = callableEndpoint.returningOne(Double.class);
		  
		  CallBatcher<CallManager.CallArgs,CallManager.OneCallEvent<Double>> batcher = oneCaller.batcher().forArgs();
		  assertNotNull(batcher);
		  String[] assignedParams = {""};
		  Double[] returnValue = {0.0};
		  batcher.onCallSuccess(event-> { 
			  assignedParams[0] = event.getArgs().getAssignedParamNames()[0];
			  returnValue[0] = event.getItem();
		  });
		  
		  batcher.getDataMovementManager().startJob(batcher);
		  
	      batcher.add(oneCaller.args().param("param1", 1.2));

	      batcher.flushAndWait();
	      batcher.getDataMovementManager().stopJob(batcher);
	      
	      assertEquals("Invalid number of parameters", assignedParams.length, 1);
	      assertEquals("Param values are not equal",assignedParams[0],("param1"));
	      assertEquals("Return value not as expected", returnValue[0], Double.valueOf(1.2));
	  }
	  
	  @Test
	  public void manyForArgsTest() {
		  CallManager.CallableEndpoint callableEndpoint = installEndpoint("float");
		  CallManager.ManyCaller<Float> manyCaller = callableEndpoint.returningMany(Float.class);
		  
		  CallBatcher<CallManager.CallArgs,CallManager.ManyCallEvent<Float>> batcher = manyCaller.batcher().forArgs();
		  assertNotNull(batcher);
		  Float[] values = new Float[]{1.8f, 2.4f};
		  Float[] returnValues =  {0f,0f};
		  String[] assignedParams = {""};
		  batcher.onCallSuccess(event-> { 
			  assignedParams[0] = event.getArgs().getAssignedParamNames()[0];
			  Float[] paramValues = event.getItems().toArray(Float[]::new);
			  returnValues[0] = paramValues[0];
			  returnValues[1] = paramValues[1];
			  
		  });
		  
		  batcher.getDataMovementManager().startJob(batcher);
		  
	      batcher.add(manyCaller.args().param("param1", values));

	      batcher.flushAndWait();
	      
	      batcher.getDataMovementManager().stopJob(batcher);
	     
	      assertEquals("Invalid number of parameters", assignedParams.length, 1);
	      assertEquals("Param values are not equal",assignedParams[0],("param1"));
	      assertEquals("Return value not as expected",returnValues[0], Float.valueOf(1.8f));
	      assertEquals("Return value not as expected",returnValues[1], Float.valueOf(2.4f));
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
}