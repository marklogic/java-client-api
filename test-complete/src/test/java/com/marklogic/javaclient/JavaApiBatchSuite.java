package com.marklogic.javaclient;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import com.marklogic.*;

public class JavaApiBatchSuite {

	/*
	 * Copyright 2003-2013 MarkLogic Corporation. All Rights Reserved.
	 */
	
	    private static final Class<?>[] testClasses = {  TestAggregates.class,
	    	 TestAppServicesAbsRangeConstraint.class,
	    	 TestAppServicesCollectionConstraint.class,
	    	 TestAppServicesConstraintCombination.class,
	    	 TestAppServicesFieldConstraint.class,
	    	 TestAppServicesGeoAttrPairConstraint.class,
	    	 TestAppServicesGeoElementChildConstraint.class,
	    	 TestAppServicesGeoElementConstraint.class,
	    	 TestAppServicesGeoElemPairConstraint.class,
	    	 TestAppServicesRangeConstraint.class,
	    	 TestAppServicesRangePathIndexConstraint.class,
	    	 TestAppServicesValueConstraint.class,
	    	 TestAppServicesWordConstraint.class,
	    	 TestBug18026.class,
	    	 TestBug18724.class,
	    	 TestBug18736.class,
	    	 TestBug18801.class,
	    	 TestBug18920.class,
	    	 TestBug18990.class,
	    	 TestBug18993.class,
	    	 TestBug19016.class,
	    	 TestBug19046.class,
	    	 TestBug19092.class,
	    	 TestBug19140.class,
	    	 TestBug19144.class,
	    	 TestBug19389.class,
	    	 TestBug19443.class,
	    	 TestBug20979.class,
	    	 TestBug21159.class,
	    	 TestBug21183.class,
	    	 TestBug22037.class,
	    	 TestBytesHandle.class,
	    	 TestBulkReadSample1.class,
	    	 TestBulkWriteMetadata1.class,
	    	 TestBulkWriteSample1.class,
	    	 TestConstraintCombination.class,
	    	 TestCRUDModulesDb.class,
	    	 TestDatabaseAuthentication.class,
	    	 TestDatabaseClientConnection.class,
	    	 TestDocumentEncoding.class,
	    	 TestDocumentFormat.class,
	    	 TestDocumentMimetype.class,
	    	 TestDOMHandle.class,
	    	 TestFieldConstraint.class,
	    	 TestFileHandle.class,
	    	 TestInputSourceHandle.class,
	    	 TestInputStreamHandle.class,
	    	 TestKeyValueSearch.class,
	    	 TestLinkResultDocuments.class,
	    	 TestMetadata.class,
	    	 TestMetadataXML.class,
	    	 TestMultithreading.class,
	    	 TestNamespaces.class,
	    	 TestOptimisticLocking.class,
	    	 TestOutputStreamHandle.class,
	    	 TestPartialUpdate.class,
	    	 TestPatchCardinality.class,
	    	 TestQueryByExample.class,
	    	 TestQueryOptionBuilder.class,
	    	 TestQueryOptionBuilderGrammar.class,
	    	 TestQueryOptionBuilderSearchableExpression.class,
	    	 TestQueryOptionBuilderSearchOptions.class,
	    	 TestQueryOptionBuilderSortOrder.class,
	    	 TestQueryOptionBuilderTransformResults.class,
	    	 TestQueryOptionsHandle.class,
	    	 TestQueryOptionsListHandle.class,
	    	 TestRangeConstraint.class,
	    	 TestRangeConstraintAbsoluteBucket.class,
	    	 TestRangeConstraintRelativeBucket.class,
	    	 TestRawAlert.class,
	    	 TestRawCombinedQuery.class,
	    	 TestRawCombinedQueryGeo.class,
	    	 TestRawStructuredQuery.class,
	    	 TestReaderHandle.class,
	    	 TestRequestLogger.class,
	    	 TestResponseTransform.class,
	    	 TestRollbackTransaction.class,
	    	 TestSearchMultibyte.class,
	    	 TestSearchMultipleForests.class,
	    	 TestSearchOnJSON.class,
	    	 TestSearchOnProperties.class,
	    	 TestSearchOptions.class,
	    	 TestSearchSuggestion.class,
	    	 TestServerAssignedDocumentURI.class,
	    	 TestSourceHandle.class,
	    	 TestSSLConnection.class,
	    	 TestStandaloneGeoQuery.class,
	    	 TestStandaloneQuery.class,
	    	 TestStringHandle.class,
	    	 TestStructuredQuery.class,
	    	 TestStructuredQueryMildNot.class,
	    	 TestStructuredSearchGeo.class,
	    	 TestTransformXMLWithXSLT.class,
	    	 TestValueConstraint.class,
	    	 TestWordConstraint.class,
	    	 TestWriteTextDoc.class,
	    	 TestXMLDocumentRepair.class,
	    	 TestXMLEventReaderHandle.class,
	    	 TestXMLMultiByte.class,
	    	 TestXMLStreamReaderHandle.class,
	    	 ThreadClass.class,
	    	 ThreadSearch.class,
	    	 ThreadWrite.class };

	    private JavaApiBatchSuite() {
	        // cannot instantiate
	    }

	    public static void runTestCase(Class testCase)
	    {
	        Result result = JUnitCore.runClasses(testCase);
	        for (Failure failure : result.getFailures())
	        {
	            System.out.println("##################################\n Test Failed "+testCase+"with reason \n "+failure.toString()+"\n############################\n");
	        }
	    }
	    public static void createRESTAppServer(String restServerName ,int restPort){
	    	try{
	    		DefaultHttpClient client = new DefaultHttpClient();

				client.getCredentialsProvider().setCredentials(
						new AuthScope("localhost", 8002),
						new UsernamePasswordCredentials("admin", "admin"));
				HttpPost post = new HttpPost("http://localhost:8002"+ "/v1/rest-apis?format=json");	
				String JSONString = 
						"{ \"rest-api\": {\"name\":\""+
						restServerName +
						"\",\"port\":\""+
						restPort+
						"\"}}";
				//System.out.println(JSONString);		
				post.addHeader("Content-type", "application/json");
				post.setEntity(new StringEntity(JSONString));
			
					HttpResponse response = client.execute(post);
					HttpEntity respEntity = response.getEntity();

				    if (respEntity != null) {
				        // EntityUtils to get the response content
				        String content =  EntityUtils.toString(respEntity);
				        System.out.println(content);
				    }
				}catch (Exception e) {
				    // writing error to Log
				    e.printStackTrace();
				}
	    }
	    public static void createRESTUser(String usrName, String pass, String roleName ){
	    	try{
				DefaultHttpClient client = new DefaultHttpClient();
				client.getCredentialsProvider().setCredentials(
						new AuthScope("localhost", 8002),
						new UsernamePasswordCredentials("admin", "admin"));
				HttpPost post = new HttpPost("http://localhost:8002"+ "/manage/v2/users?format=json");
		 	ObjectMapper mapper = new ObjectMapper();
			ObjectNode mainNode = mapper.createObjectNode();
//			ObjectNode childNode = mapper.createObjectNode();
			ArrayNode childArray = mapper.createArrayNode();
			mainNode.put("name",usrName);
			mainNode.put("description", "user discription");
			mainNode.put("password", pass);
			childArray.add(roleName);
			mainNode.put("role", childArray);
			//System.out.println(type + mainNode.path("range-element-indexes").path("range-element-index").toString());
//				System.out.println(mainNode.toString());
				post.addHeader("Content-type", "application/json");
				post.setEntity(new StringEntity(mainNode.toString()));
			
				HttpResponse response = client.execute(post);
				HttpEntity respEntity = response.getEntity();
                  if( response.getStatusLine().getStatusCode() == 400)
                  {
                	  System.out.println("User already exist or a bad create request");
                  }
                  else if (respEntity != null) {
				// EntityUtils to get the response content
				String content =  EntityUtils.toString(respEntity);
				System.out.println(content);
				}
                  else {System.out.print("No Proper Response");}
				}catch (Exception e) {
				    // writing error to Log
				    e.printStackTrace();
				}
	    }

	    // ----------------------------------------------------------
	    public static void createRESTSSLAppServer(String SSLServerName ,int restPort ){
	    	
	    }
	    public static void deleteRESTAppServerWithDB(String restServerName)	{
			try{
				DefaultHttpClient client = new DefaultHttpClient();

				client.getCredentialsProvider().setCredentials(
						new AuthScope("localhost", 8002),
						new UsernamePasswordCredentials("admin", "admin"));

				HttpDelete delete = new HttpDelete("http://localhost:8002/v1/rest-apis/"+restServerName+"?include=content&include=modules");
				
					HttpResponse response = client.execute(delete);
				if(response.getStatusLine().getStatusCode()== 202){
								Thread.sleep(3500);
				}
				}catch (Exception e) {
				    // writing error to Log
				    e.printStackTrace();
				}
			}	    
	    /**
	     * This class is for the .Net IKVM port of the junit tests.
	     * 
	     * @param args
	     *            The args
	     */
	    public static void main(String[] args) {
	        System.setProperty("REST.host", "localhost");
	        System.setProperty("REST.port", "8013");
	        System.setProperty("REST.user", "rest-admin");
	        System.setProperty("xcc.pass", "x");
	        createRESTUser("rest-admin","x","rest-admin");
	        createRESTUser("rest-writer","x","rest-writer");
	        createRESTUser("rest-reader","x","rest-reader");
	        createRESTAppServer("JavaClientApiDefault",8013);
	        createRESTSSLAppServer("JavaClientApiDefaultSSL",8014);
	        for(Class testclass: testClasses ) {
	          System.out.println("Starting the Test: "+testclass);
	        	runTestCase(testclass);
	        }
	        deleteRESTAppServerWithDB("JavaClientApiDefault");
	        deleteRESTAppServerWithDB("JavaClientApiDefaultSSL");
	        
	    }


}
