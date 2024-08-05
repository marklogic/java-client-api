/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.fastfunctest;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.admin.ExtensionMetadata;
import com.marklogic.client.admin.ExtensionMetadata.ScriptLanguage;
import com.marklogic.client.admin.MethodType;
import com.marklogic.client.admin.ResourceExtensionsManager;
import com.marklogic.client.admin.ResourceExtensionsManager.MethodParameters;
import com.marklogic.client.extensions.ResourceManager;
import com.marklogic.client.extensions.ResourceServices.ServiceResult;
import com.marklogic.client.extensions.ResourceServices.ServiceResultIterator;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.util.RequestParameters;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;
import org.skyscreamer.jsonassert.JSONAssert;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;



public class TestJSResourceExtensions extends AbstractFunctionalTest {
  ResourceExtensionsManager resourceMgr;

  static public class TestJSExtension extends ResourceManager {
    static final public String NAME = "simpleJSResourceModule";

    public TestJSExtension(DatabaseClient client) {
      super();
      // a Resource Manager must be initialized by a Database Client
      client.init(NAME, this);
    }

    public String getJSON(String docUri) {
      RequestParameters params = new RequestParameters();
      params.add("arg1", docUri);
      params.add("arg2", "Earth");

      // call the service
      ServiceResultIterator resultItr = getServices().get(params);

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

      String input = "{\"array\" : [1,2,3]}";
      // call the service
      ServiceResultIterator resultItr = getServices().post(params, new StringHandle(input).withFormat(Format.JSON));
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
      StringHandle output = new StringHandle();
      // call the service
      getServices().delete(params, output);
      // iterate over the results
      return output.get();
    }
  }

  @BeforeAll
  public static void setUpBeforeClass() throws Exception {
    createUserRolesWithPrevilages("test-eval", "xdbc:eval", "xdbc:eval-in", "xdmp:value", "xdmp:eval", "xdmp:eval-in", "any-uri", "xdbc:invoke");
    createRESTUser("eval-user", "x", "test-eval", "rest-admin", "rest-writer", "rest-reader", "rest-extension-user");
  }

  @AfterAll
  public static void tearDownAfterClass() throws Exception {
    deleteRESTUser("eval-user");
    deleteUserRole("test-eval");
  }

  @BeforeEach
  public void setUp() throws Exception {
    client = newClientAsUser("eval-user", "x");
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
    handle.set(myStream);
    resourceMgr.writeServices("simpleJSResourceModule", handle, resextMetadata, getParams);
  }

  @AfterEach
  public void tearDown() throws Exception {
    resourceMgr.deleteServices("simpleJSResourceModule");
    client.release();
  }

  @Test
  public void test1GetAllResourceServices() throws Exception {

    JacksonHandle jh = new JacksonHandle();
    resourceMgr.listServices(jh);

    assertEquals( "JSON", jh.getFormat().name());
    assertEquals( "application/json", jh.getMimetype());

    String expectedList = "{\"resources\":{\"resource\":[{\"name\":\"simpleJSResourceModule\", \"source-format\":\"javascript\", \"description\":\"Testing resource extension for java script\", \"version\":\"1.0\", \"title\":\"BasicJSTest\", \"methods\":{\"method\":[{\"method-name\":\"get\", \"parameter\":[{\"parameter-name\":\"my-uri\", \"parameter-type\":\"xs:string?\"}]}, {\"method-name\":\"post\"}, {\"method-name\":\"put\"}, {\"method-name\":\"delete\"}]}, \"resource-source\":\"/v1/resources/simpleJSResourceModule\"}]}}";
    JSONAssert.assertEquals(expectedList, jh.get().toString(), false);
    TestJSExtension tjs = new TestJSExtension(client);
    String expectedResponse = "{\"response\":[200, \"OK\"]}";
    JSONAssert.assertEquals(expectedResponse, tjs.putJSON("helloJS.json"), false);
    String expAftrPut = "{\"argument1\":\"helloJS.json\", \"argument2\":\"Earth\",\"database-name\":\"java-functest\", \"document-count\":1, \"content\":\"This is a JSON document\", \"document-content\":{\"argument1\":\"hello\", \"argument2\":\"Earth\", \"content\":\"This is a JSON document\", \"response\":[200, \"OK\"], \"outputTypes\":\"application/json\"}, \"response\":[200, \"OK\"], \"outputTypes\":[\"application/json\"]}";
    JSONAssert.assertEquals(expAftrPut, tjs.getJSON("helloJS.json"), false);
    JSONAssert.assertEquals(expectedResponse, tjs.postJSON("helloJS.json"), false);
    String expAftrPost = "{\"argument1\":\"helloJS.json\", \"argument2\":\"Earth\", \"document-count\":1, \"content\":\"This is a JSON document\", \"document-content\":{\"argument1\":\"hello\", \"argument2\":\"Earth\", \"content\":\"This is a JSON document\", \"array\":[1, 2, 3], \"response\":[200, \"OK\"], \"outputTypes\":\"application/json\"}, \"response\":[200, \"OK\"], \"outputTypes\":[\"application/json\"]}";
    JSONAssert.assertEquals(expAftrPost, tjs.getJSON("helloJS.json"), false);
    String expected = "{\"argument1\":\"helloJS.json\", \"argument2\":\"Earth\", \"document-count\":0, \"content\":\"This is a JSON document\", \"document-content\":null, \"response\":[200, \"OK\"], \"outputTypes\":[\"application/json\"]}";

    JSONAssert.assertEquals(expectedResponse, tjs.deleteJSON("helloJS.json"), false);
    JSONAssert.assertEquals(expected, tjs.getJSON("helloJS.json"), false);
  }

  @Test
  public void test2GetAllResourceServicesMultipleTimes() throws Exception {

    JacksonHandle jh = new JacksonHandle();

    TestJSExtension tjs = new TestJSExtension(client);
    String expectedResponse = "{\"response\":[200, \"OK\"]}";
    // load multiple documents using extension
    for (int i = 0; i < 150; i++) {
      JSONAssert.assertEquals(expectedResponse, tjs.putJSON("helloJS" + i + ".json"), false);
      JSONAssert.assertEquals(expectedResponse, tjs.postJSON("helloJS" + i + ".json"), false);
    }

    JacksonHandle jh2 = new JacksonHandle();
    jh.set(jh2.getMapper().readTree(tjs.getJSON("helloJS0.json")));

    assertEquals( 150, jh.get().get("document-count").intValue());

    String expAftrPut = "{\"argument1\":\"hello\", \"argument2\":\"Earth\", \"content\":\"This is a JSON document\", \"array\":[1, 2, 3], \"response\":[200, \"OK\"], \"outputTypes\":\"application/json\"}";
    String expected = "{\"argument1\":\"helloJS.json\", \"argument2\":\"Earth\", \"database-name\":\"java-functest\", \"document-count\":0, \"content\":\"This is a JSON document\", \"document-content\":null, \"response\":[200, \"OK\"], \"outputTypes\":[\"application/json\"]}";
    // verify by reading all the documents to see put and post services
    // correctly inserted documents and delete them
    for (int j = 0; j < 150; j++) {
      jh.set(jh2.getMapper().readTree(tjs.getJSON("helloJS" + j + ".json")));
      JSONAssert.assertEquals(expAftrPut, jh.get().get("document-content").findParent("array").toString(), false);
      JSONAssert.assertEquals(expectedResponse, tjs.deleteJSON("helloJS" + j + ".json"), false);
    }
    System.out.println(tjs.getJSON("helloJS.json"));
    JSONAssert.assertEquals(expected, tjs.getJSON("helloJS.json"), false);
  }
}
