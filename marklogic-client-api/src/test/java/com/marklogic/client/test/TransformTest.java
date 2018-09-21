/*
 * Copyright 2012-2018 MarkLogic Corporation
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
package com.marklogic.client.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import javax.xml.namespace.QName;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;

import com.marklogic.client.FailedRequestException;
import com.marklogic.client.ForbiddenUserException;
import com.marklogic.client.ResourceNotFoundException;
import com.marklogic.client.ResourceNotResendableException;
import com.marklogic.client.admin.ExtensionLibrariesManager;
import com.marklogic.client.admin.ServerConfigurationManager;
import com.marklogic.client.admin.TransformExtensionsManager;
import com.marklogic.client.document.ServerTransform;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StringQueryDefinition;
import com.marklogic.client.query.ValuesDefinition;

public class TransformTest {
  final static public String JS_NAME = "testsjs";
  final static public String JS_FILE = "testsjs.sjs";
  final static public String MLCP_TRANSFORM_ADAPTER = "MlcpTransformAdapter";
  final static public String TEST_NS =
    "http://marklogic.com/rest-api/test/transform";

  static private String optionsName;
  static private ServerConfigurationManager confMgr;
  static private TransformExtensionsManager extensionMgr;
  static private ExtensionLibrariesManager libMgr;


  @BeforeClass
  public static void beforeClass()
    throws IOException, FailedRequestException, ForbiddenUserException, ResourceNotFoundException, ResourceNotResendableException {
    Common.connect();
    Common.connectAdmin();
    confMgr = Common.adminClient.newServerConfigManager();

    extensionMgr = confMgr.newTransformExtensionsManager();
    optionsName = ValuesHandleTest.makeValuesOptions();
    libMgr = confMgr.newExtensionLibrariesManager();

    libMgr.write("/ext/RestTransformAdapter.xqy",
      new StringHandle(Common.testFileToString("RestTransformAdapter.xqy")).withFormat(Format.TEXT));

    libMgr.write("/ext/memory-operations.xqy",
      new StringHandle(Common.testFileToString("memory-operations.xqy")).withFormat(Format.TEXT));

    libMgr.write("/ext/node-operations.xqy",
      new StringHandle(Common.testFileToString("node-operations.xqy")).withFormat(Format.TEXT));

    extensionMgr.writeXQueryTransformAs(
      TransformExtensionsTest.XQUERY_NAME,
      TransformExtensionsTest.makeXQueryMetadata(),
      Common.testFileToString(TransformExtensionsTest.XQUERY_FILE)
    );

    extensionMgr.writeJavascriptTransformAs(
      JS_NAME,
      TransformExtensionsTest.makeXQueryMetadata(),
      Common.testFileToString(JS_FILE)
    );

    extensionMgr.writeXQueryTransformAs(
      MLCP_TRANSFORM_ADAPTER,
      TransformExtensionsTest.makeXQueryMetadata(),
      Common.testFileToString(MLCP_TRANSFORM_ADAPTER + ".xqy")
    );

    extensionMgr.writeXSLTransform(
      TransformExtensionsTest.XSLT_NAME,
      new StringHandle(Common.testFileToString(TransformExtensionsTest.XSLT_FILE)),
      TransformExtensionsTest.makeXSLTMetadata()
    );
  }

  @AfterClass
  public static void afterClass()
    throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
    confMgr.newQueryOptionsManager().deleteOptions(optionsName);

    extensionMgr.deleteTransform(MLCP_TRANSFORM_ADAPTER);
    extensionMgr.deleteTransform(TransformExtensionsTest.XQUERY_NAME);
    extensionMgr.deleteTransform(TransformExtensionsTest.XSLT_NAME);

    libMgr.delete("/ext/RestTransformAdapter.xqy");
    libMgr.delete("/ext/memory-operations.xqy");
    libMgr.delete("/ext/node-operations.xqy");

  }

  @Test
  public void testXQueryTransform()
    throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException, ResourceNotResendableException {
    runTransform(new ServerTransform(TransformExtensionsTest.XQUERY_NAME));
  }

  @Test
  public void testJavascriptTransform() throws Exception{
    runTransform(new ServerTransform(JS_NAME));
  }

  @Test
  public void testXSLTransform()
    throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException, ResourceNotResendableException {
    runTransform(new ServerTransform(TransformExtensionsTest.XSLT_NAME));
  }

  @Test
  public void testXQueryMlcpTransformAdapter() throws Exception{
    String transformContents = Common.testFileToString("SampleMlcpTransform.xqy");
    libMgr.write("/ext/SampleMlcpTransform.xqy",
      new StringHandle(transformContents).withFormat(Format.TEXT));

    ServerTransform transform = new ServerTransform(MLCP_TRANSFORM_ADAPTER)
      .addParameter("ml.module", "/ext/SampleMlcpTransform.xqy")
      .addParameter("ml.namespace", "http://marklogic.com/example")
      .addParameter("attr-value", "true");
    runTransform(transform);

    libMgr.delete("/ext/SampleMlcpTransform.xqy");
  }

  @Test
  public void testXQueryRestTransformAdapter() throws Exception{
    ServerTransform transform = new ServerTransform(MLCP_TRANSFORM_ADAPTER)
      .addParameter("ml.module", "/ext/RestTransformAdapter.xqy")
      .addParameter("ml.namespace", "http://marklogic.com/mlcp/transform/RestTransformAdapter.xqy")
      .addParameter("ml.transform", TransformExtensionsTest.XQUERY_NAME)
      .addParameter("value", "true");
    runTransform(transform);
  }

  @Test
  public void testJavascriptMlcpTransformAdapter() throws Exception{
    String transformContents = Common.testFileToString("SampleMlcpTransform.sjs");
    libMgr.write("/ext/SampleMlcpTransform.sjs",
      new StringHandle(transformContents).withFormat(Format.TEXT));

    ServerTransform transform = new ServerTransform(MLCP_TRANSFORM_ADAPTER)
      .addParameter("ml.module", "/ext/SampleMlcpTransform.sjs")
      .addParameter("attr-value", "true");
    runTransform(transform);

    libMgr.delete("/ext/SampleMlcpTransform.sjs");
  }

  @Test
  public void testJavascriptRestTransformAdapter() throws Exception{
    ServerTransform transform = new ServerTransform(MLCP_TRANSFORM_ADAPTER)
      .addParameter("ml.module", "/ext/RestTransformAdapter.xqy")
      .addParameter("ml.namespace", "http://marklogic.com/mlcp/transform/RestTransformAdapter.xqy")
      .addParameter("ml.transform", JS_NAME)
      .addParameter("value", "true");
    runTransform(transform);
  }

  private void runTransform(ServerTransform transform)
    throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
    transform.put("value", "true");

    String docId = "/test/testTransformable1.xml";

    XMLDocumentManager docMgr = Common.client.newXMLDocumentManager();
    docMgr.write(docId, new StringHandle().with("<document/>"));
    Document result = docMgr.read(docId, new DOMHandle(), transform).get();
    String value = result.getDocumentElement().getAttributeNS(TEST_NS, "transformed");
    assertEquals("Document read transform failed","true",value);

    //docMgr.delete(docId);

    docId = "/test/testTransformable2.xml";
    docMgr.write(docId, new StringHandle().with("<document/>"), transform);
    result = docMgr.read(docId, new DOMHandle()).get();
    value = result.getDocumentElement().getAttributeNS(TEST_NS, "transformed");
    assertEquals("Document write transform failed",value,"true");

    //docMgr.delete(docId);

    QueryManager queryMgr = Common.client.newQueryManager();

    StringQueryDefinition stringQuery = queryMgr.newStringDefinition();
    stringQuery.setCriteria("grandchild1 OR grandchild4");
    stringQuery.setResponseTransform(transform);

    result = queryMgr.search(stringQuery, new DOMHandle()).get();
    value = result.getDocumentElement().getAttributeNS(TEST_NS, "transformed");
    assertEquals("String query read transform failed","true",value);

    ValuesDefinition vdef =
      queryMgr.newValuesDefinition("double", optionsName);
    stringQuery = queryMgr.newStringDefinition();
    stringQuery.setCriteria("10");
    stringQuery.setResponseTransform(transform);

    vdef.setQueryDefinition(stringQuery);
    result = queryMgr.values(vdef, new DOMHandle()).get();
    value = result.getDocumentElement().getAttributeNS(TEST_NS, "transformed");
    assertEquals("Values query read transform failed",value,"true");

// TODO: QBE tests with XQuery and XSLT
  }

  @Test
  public void test118() {
    String naiveTransform = "xquery version \"1.0-ml\";\n" +

      "module namespace ex = \"http://marklogic.com/rest-api/transform/test118\";\n" +

      "declare function ex:transform(\n" +
      "  $context as map:map,\n" +
      "  $params as map:map,\n" +
      "  $content as document-node())\n" +
      "as document-node() {\n" +
      " document{\n" +

      "<search:response snippet-format=\"highlight\" total=\"1\" start=\"1\" page-length=\"1\" " +
      "  xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns=\"\" " +
      "  xmlns:search=\"http://marklogic.com/appservices/search\">\n" +
      "  <search:result index=\"1\" uri=\"/doc/2.xml\" path=\"fn:doc('/doc/2.xml')\" score=\"92160\" " +
      "    confidence=\"0.674626\" fitness=\"0.674626\">\n" +
      "    <search:snippet>\n" +
      "      <headline>Q1 <match>outlook</match></headline>\n" +
      "    </search:snippet>\n" +
      "    <search:metadata>\n" +
      "       <id>a</id>\n" +
      "    </search:metadata>\n" +
      "  </search:result>\n" +
      "  <search:qtext>outlook snippet:highlight</search:qtext>\n" +
      "  <search:metrics>\n" +
      "    <search:query-resolution-time>PT0.008042S</search:query-resolution-time>\n" +
      "    <search:facet-resolution-time>PT0.000323S</search:facet-resolution-time>\n" +
      "    <search:snippet-resolution-time>PT0.018339S</search:snippet-resolution-time>\n" +
      "    <search:total-time>PT0.027161S</search:total-time>\n" +
      "  </search:metrics>\n" +
      "</search:response>}\n" +
      "};";
    extensionMgr.writeXQueryTransform( "test118", new StringHandle().with(naiveTransform));

    QueryManager q = Common.client.newQueryManager();
    StringQueryDefinition s = q.newStringDefinition("");
    s.setCriteria("a");
    s.setResponseTransform(new ServerTransform("test118"));
    q.search(s, new SearchHandle());
    // if the previous line throws no exception, then 118 is resolved
  }

/*
  @Test
  public void testIssue471() {
    String transform = "xquery version '1.0-ml';" +
      "module namespace transform = 'http://marklogic.com/rest-api/transform/testIssue471';" +

      "import module namespace json = 'http://marklogic.com/xdmp/json' at '/MarkLogic/json/json.xqy';" +

      "declare namespace search = 'http://marklogic.com/appservices/search';" +

      "declare function transform(" +
      "        $context as map:map," +
      "        $params as map:map," +
      "        $content as document-node()" +
      ") as document-node()" +
      "{" +
      "    let $c := json:config('custom') ," +
      "        $cx := map:put( $c, 'whitespace', 'ignore' )," +
      "        $cx := map:put( $c, 'text-value', 'label' )," +
      "        $cx := map:put( $c, 'camel-case', fn:true() )," +
      "        $cx := map:put( $c, 'json-attributes', ('snippet-format', 'total', 'start', 'page-length'))," +
      "        $cx := map:put( $c, 'array-element-names', (xs:QName('search:result')))" +
      "    let $_ := map:put($context, 'output-type', 'text/json')" +
      "    let $json := json:transform-to-json(  $content ,$c )" +
      "    return $json" +
      "};";
    extensionMgr.writeXQueryTransform( "testIssue471", new StringHandle().with(transform));

    QueryManager q = Common.client.newQueryManager();
    StringQueryDefinition s = q.newStringDefinition();
    s.setCriteria("a");
    s.setResponseTransform(new ServerTransform("testIssue471"));
    JacksonHandle response = q.search(s, new JacksonHandle());
    System.out.println(response.toString());
  }
*/
}
