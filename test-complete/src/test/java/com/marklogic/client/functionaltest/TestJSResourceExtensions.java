/*
 * Copyright 2014-2016 MarkLogic Corporation
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

package com.marklogic.client.functionaltest;

import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.admin.ExtensionMetadata;
import com.marklogic.client.admin.ExtensionMetadata.ScriptLanguage;
import com.marklogic.client.admin.MethodType;
import com.marklogic.client.admin.ResourceExtensionsManager;
import com.marklogic.client.admin.ResourceExtensionsManager.MethodParameters;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.extensions.ResourceManager;
import com.marklogic.client.extensions.ResourceServices.ServiceResult;
import com.marklogic.client.extensions.ResourceServices.ServiceResultIterator;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.util.RequestParameters;

public class TestJSResourceExtensions extends BasicJavaClientREST {
	private static final int BATCH_SIZE=100;
	private static final String DIRECTORY ="/bulkTransform/";
	private static String dbName = "TestJSResourceExtensionDB";
	private static String [] fNames = {"TestResourceExtensionDB-1"};
	
	//
	private  DatabaseClient client ;
	ResourceExtensionsManager resourceMgr;

	static public class TestJSExtension extends ResourceManager {
		static final public String NAME = "simpleJSResourceModule";
		static final public ExtensionMetadata.ScriptLanguage scriptLanguage = ExtensionMetadata.JAVASCRIPT;
		private JSONDocumentManager docMgr;


		public TestJSExtension(DatabaseClient client) {
			super();
			// a Resource Manager must be initialized by a Database Client
			client.init(NAME, this);

			//  delegates some services to a document manager
			docMgr = client.newJSONDocumentManager();
		}

		public String getJSON(String docUri) {
			RequestParameters params = new RequestParameters();
			params.add("arg1", docUri);
			params.add("arg2", "Earth");

			// specify the mime type for each expected document returned
			String[] mimetypes = new String[] {"text/plain"};

			// call the service
			ServiceResultIterator resultItr = getServices().get(params, mimetypes);

			// iterate over the results
			List<String> responses = new ArrayList<>();
			StringHandle readHandle = new StringHandle();
			while (resultItr.hasNext()) {
				ServiceResult result = resultItr.next();

				// get the result content
				result.getContent(readHandle);
				responses.add(readHandle.get());
			}

			// release the iterator resources
			resultItr.close();

			return responses.get(0);
		}
		public String postJSON(String docUri) {
			RequestParameters params = new RequestParameters();
			params.add("uri", docUri);
			// specify the mime type for each expected document returned
			String[] mimetypes = new String[] {"text/plain"};
			StringHandle output = new StringHandle();
			String input = "{\"array\" : [1,2,3]}";
			// call the service
			ServiceResultIterator resultItr= getServices().post(params, new StringHandle(input).withFormat(Format.JSON), mimetypes);
			// iterate over the results
			List<String> responses = new ArrayList<>();
			StringHandle readHandle = new StringHandle();
			while (resultItr.hasNext()) {
				ServiceResult result = resultItr.next();
				// get the result content
				result.getContent(readHandle);
				responses.add(readHandle.get());
			}

			// release the iterator resources
			resultItr.close();
			return responses.get(0);
		}
		public String putJSON(String docUri) {
			RequestParameters params = new RequestParameters();
			params.add("uri", docUri);
			// specify the mime type for each expected document returned
			String[] mimetypes = new String[] {"text/plain"};
			StringHandle output = new StringHandle();
			String input = "{\"argument1\":\"hello\", \"argument2\":\"Earth\", \"content\":\"This is a JSON document\", \"response\":[200, \"OK\"], \"outputTypes\":\"application/json\"}";
			StringHandle readHandle = new StringHandle();
			// call the service
			getServices().put(params, new StringHandle(input).withFormat(Format.JSON), readHandle);
			// iterate over the results

			return readHandle.get();
		}
		public String deleteJSON(String docUri) {
			RequestParameters params = new RequestParameters();
			params.add("uri", docUri);
			// specify the mime type for each expected document returned
			String[] mimetypes = new String[] {"text/plain"};
			StringHandle output = new StringHandle();
			// call the service
			getServices().delete(params, output);
			// iterate over the results

			return output.get();
		}
	}
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.out.println("In setup");
		configureRESTServer(dbName, fNames, false);
		createUserRolesWithPrevilages("test-eval","xdbc:eval", "xdbc:eval-in","xdmp:eval-in","any-uri","xdbc:invoke");
	    createRESTUser("eval-user", "x", "test-eval","rest-admin","rest-writer","rest-reader");
		//		System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.wire", "debug");

	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		System.out.println("In tear down" );
		cleanupRESTServer(dbName, fNames);
		deleteRESTUser("eval-user");
		deleteUserRole("test-eval");
	}

	@Before
	public void setUp() throws KeyManagementException, NoSuchAlgorithmException, Exception {
		
		int restPort = getRestServerPort();
		client = getDatabaseClientOnDatabase("localhost", restPort, dbName, "eval-user", "x", Authentication.DIGEST);
		resourceMgr = client.newServerConfigManager().newResourceExtensionsManager();
		ExtensionMetadata resextMetadata = new ExtensionMetadata();
		resextMetadata.setTitle("BasicJSTest");
		resextMetadata.setDescription("Testing resource extension for java script");
		System.out.println(resextMetadata.getScriptLanguage());
		resextMetadata.setScriptLanguage(ScriptLanguage.JAVASCRIPT);
		System.out.println(resextMetadata.getScriptLanguage());
		resextMetadata.setVersion("1.0");
		MethodParameters getParams = new MethodParameters(MethodType.GET);
		getParams.add("my-uri", "xs:string?");
		FileInputStream myStream = new FileInputStream("src/test/java/com/marklogic/client/functionaltest/data/JSResource.js");
		InputStreamHandle handle = new InputStreamHandle(myStream);
		handle.set (myStream);
		resourceMgr.writeServices("simpleJSResourceModule", handle, resextMetadata,getParams);


	}

	@After
	public void tearDown() throws Exception {
		resourceMgr.deleteServices("simpleJSResourceModule");
		client.release();

	}

	@Test
	public void test1GetAllResourceServices() throws KeyManagementException, NoSuchAlgorithmException, Exception {
		
		JacksonHandle jh = new JacksonHandle();
		resourceMgr.listServices(jh);
		
		assertEquals("Format on Handle", "JSON", jh.getFormat().name());
		assertEquals("Mime Type on Handle", "application/json", jh.getMimetype());
		
		String expectedList ="{\"resources\":{\"resource\":[{\"name\":\"simpleJSResourceModule\", \"source-format\":\"javascript\", \"description\":\"Testing resource extension for java script\", \"version\":\"1.0\", \"title\":\"BasicJSTest\", \"methods\":{\"method\":[{\"method-name\":\"get\", \"parameter\":[{\"parameter-name\":\"my-uri\", \"parameter-type\":\"xs:string?\"}]}, {\"method-name\":\"post\"}, {\"method-name\":\"put\"}, {\"method-name\":\"delete\"}]}, \"resource-source\":\"/v1/resources/simpleJSResourceModule\"}]}}";
		JSONAssert.assertEquals(expectedList,jh.get().toString(),false);
		TestJSExtension tjs= new TestJSExtension(client);
		String expectedResponse="{\"response\":[200, \"OK\"]}";
		JSONAssert.assertEquals(expectedResponse, tjs.putJSON("helloJS.json"), false);
		String expAftrPut ="{\"argument1\":\"helloJS.json\", \"argument2\":\"Earth\",\"database-name\":\"TestJSResourceExtensionDB\", \"document-count\":1, \"content\":\"This is a JSON document\", \"document-content\":{\"argument1\":\"hello\", \"argument2\":\"Earth\", \"content\":\"This is a JSON document\", \"response\":[200, \"OK\"], \"outputTypes\":\"application/json\"}, \"response\":[200, \"OK\"], \"outputTypes\":[\"application/json\"]}";
		JSONAssert.assertEquals(expAftrPut, tjs.getJSON("helloJS.json"), false);
		JSONAssert.assertEquals(expectedResponse, tjs.postJSON("helloJS.json"), false);
		String expAftrPost ="{\"argument1\":\"helloJS.json\", \"argument2\":\"Earth\", \"document-count\":1, \"content\":\"This is a JSON document\", \"document-content\":{\"argument1\":\"hello\", \"argument2\":\"Earth\", \"content\":\"This is a JSON document\", \"array\":[1, 2, 3], \"response\":[200, \"OK\"], \"outputTypes\":\"application/json\"}, \"response\":[200, \"OK\"], \"outputTypes\":[\"application/json\"]}";
		JSONAssert.assertEquals(expAftrPost, tjs.getJSON("helloJS.json"), false);
		String expected ="{\"argument1\":\"helloJS.json\", \"argument2\":\"Earth\", \"document-count\":0, \"content\":\"This is a JSON document\", \"document-content\":null, \"response\":[200, \"OK\"], \"outputTypes\":[\"application/json\"]}";
	
		JSONAssert.assertEquals(expectedResponse, tjs.deleteJSON("helloJS.json"), false);
		JSONAssert.assertEquals(expected, tjs.getJSON("helloJS.json"), false);
	}
	@Test
	public void test2GetAllResourceServicesMultipleTimes() throws KeyManagementException, NoSuchAlgorithmException, Exception {
		
		JacksonHandle jh = new JacksonHandle();
		
		TestJSExtension tjs= new TestJSExtension(client);
		String expectedResponse="{\"response\":[200, \"OK\"]}";
		//load multiple documents using extension
		for(int i=0;i<150;i++){
		JSONAssert.assertEquals(expectedResponse, tjs.putJSON("helloJS"+i+".json"), false);
		JSONAssert.assertEquals(expectedResponse, tjs.postJSON("helloJS"+i+".json"), false);
		}
		
		JacksonHandle jh2 = new JacksonHandle();
		jh.set(jh2.getMapper().readTree(tjs.getJSON("helloJS0.json")));
//		System.out.println(jh.get().toString());
		assertEquals("Total documents loaded are",150,jh.get().get("document-count").intValue());

		String expAftrPut ="{\"argument1\":\"hello\", \"argument2\":\"Earth\", \"content\":\"This is a JSON document\", \"array\":[1, 2, 3], \"response\":[200, \"OK\"], \"outputTypes\":\"application/json\"}";
		String expected ="{\"argument1\":\"helloJS.json\", \"argument2\":\"Earth\", \"database-name\":\"TestJSResourceExtensionDB\", \"document-count\":0, \"content\":\"This is a JSON document\", \"document-content\":null, \"response\":[200, \"OK\"], \"outputTypes\":[\"application/json\"]}";
//		verify by reading all the documents to see put and post services correctly inserted documents and delete them
	    for(int j=0;j<150;j++){
	    jh.set(jh2.getMapper().readTree(tjs.getJSON("helloJS"+j+".json")));	
	    JSONAssert.assertEquals(expAftrPut,jh.get().get("document-content").findParent("array").toString(), false);
	    JSONAssert.assertEquals(expectedResponse, tjs.deleteJSON("helloJS"+j+".json"), false);
	    }
		System.out.println(tjs.getJSON("helloJS.json"));
		JSONAssert.assertEquals(expected, tjs.getJSON("helloJS.json"), false);

	}

}
