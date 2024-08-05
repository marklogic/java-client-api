/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.test;

import com.marklogic.client.FailedRequestException;
import com.marklogic.client.ForbiddenUserException;
import com.marklogic.client.ResourceNotFoundException;
import com.marklogic.client.ResourceNotResendableException;
import com.marklogic.client.admin.QueryOptionsManager;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.io.ValuesHandle;
import com.marklogic.client.io.ValuesListHandle;
import com.marklogic.client.query.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ValuesHandleTest {


  private static final Logger logger = (Logger) LoggerFactory
    .getLogger(ValuesHandleTest.class);

  @BeforeAll
  public static void beforeClass() {
    Common.connect();
    Common.connectRestAdmin();
  }

  @AfterAll
  public static void afterClass() {
  }

  @Test
  public void testAggregates()
    throws IOException, ParserConfigurationException, SAXException, ResourceNotFoundException, ForbiddenUserException,
           FailedRequestException, ResourceNotResendableException
  {
    String optionsName = makeValuesOptions();

    QueryManager queryMgr = Common.client.newQueryManager();

    ValuesDefinition vdef = queryMgr.newValuesDefinition("double", optionsName);
    vdef.setAggregate("sum", "avg");
    vdef.setName("double");

    //ValuesListDefinition vldef = queryMgr.newValuesListDefinition("valuesoptions");
    //ValuesListHandle vlh = queryMgr.valuesList(vldef, new ValuesListHandle());

    ValuesHandle v = queryMgr.values(vdef, new ValuesHandle());

    AggregateResult[] agg = v.getAggregates();
    assertEquals( 2, agg.length);
    double first  = agg[0].get("xs:double", Double.class);
    assertTrue(
      11.4 < first && first < 12.0);

    double second = agg[1].get("xs:double", Double.class);

    logger.debug("" + second);
    assertTrue(
      1.43 < second && second < 1.44);

    Common.restAdminClient.newServerConfigManager().newQueryOptionsManager().deleteOptions(optionsName);
  }

  @Test
  public void testCriteria()
    throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException, ResourceNotResendableException
  {
    String optionsName = makeValuesOptions();

    QueryManager queryMgr = Common.client.newQueryManager();

    ValuesDefinition vdef = queryMgr.newValuesDefinition("double", optionsName);

    for (int i=0; i < 4; i++) {
      ValueQueryDefinition vQuery = null;
      switch (i) {
        case 0:
          StringQueryDefinition stringQuery = queryMgr.newStringDefinition();
          stringQuery.setCriteria("10");
          vQuery = stringQuery;
          break;
        case 1:
          StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder(null);
          StructuredQueryDefinition t = qb.term("10");
          vQuery = t;
          break;
        case 2:
          RawCtsQueryDefinition query = queryMgr.newRawCtsQueryDefinition(null);
          query.setCriteria("10");
          vQuery = query;
          break;
        case 3:
          String ctsQuery = "<word-query xmlns=\"http://marklogic.com/cts\"><text>10</text></word-query>";
          StringHandle handle = new StringHandle(ctsQuery).withFormat(Format.XML);
          RawCtsQueryDefinition rawCtsQuery = queryMgr.newRawCtsQueryDefinition(handle);
          vQuery = rawCtsQuery;
          break;
        default:
          assertTrue( false);
      }
      vdef.setQueryDefinition(vQuery);

      ValuesHandle v = queryMgr.values(vdef, new ValuesHandle());
      CountedDistinctValue dv[] = v.getValues();
      assertNotNull( dv);
      assertEquals( 3, dv.length);
    }

    Common.restAdminClient.newServerConfigManager().newQueryOptionsManager().deleteOptions(optionsName);
  }

  @Test
  public void testValuesHandle() throws IOException, ParserConfigurationException, SAXException {
    File f = new File("src/test/resources/values.xml");
    MyValuesHandle v;
    try (FileInputStream is = new FileInputStream(f)) {
      v = new MyValuesHandle();
      v.parseTestData(is);
    }
    assertTrue("size".equals(v.getName()));
    assertEquals( "xs:unsignedLong", v.getType());

    CountedDistinctValue dv[] = v.getValues();

    assertEquals( 8, dv.length);
    assertEquals( 1, dv[0].getCount());
    assertEquals( (long) 815, (long) dv[0].get(v.getType(), Long.class));
  }

  @Test
  public void testValuesListHandle() throws IOException, ParserConfigurationException, SAXException {
    File f = new File("src/test/resources/valueslist.xml");
    FileInputStream is = new FileInputStream(f);

    MyValuesListHandle v = new MyValuesListHandle();

    v.parseTestData(is);
    Map<String,String> map = v.getValuesMap();
    assertEquals( map.size(), 2);
    assertEquals( map.get("size"), "/v1/values/size?options=photos");
  }


  @Test
  public void testValuesPaging() throws IOException, ParserConfigurationException, SAXException {
    String optionsName = makeValuesOptions();

    QueryManager queryMgr = Common.client.newQueryManager();
    queryMgr.setPageLength(2);

    ValuesDefinition vdef = queryMgr.newValuesDefinition("double", optionsName);

    ValuesHandle v = queryMgr.values(vdef, new ValuesHandle(), 2);
    CountedDistinctValue dv[] = v.getValues();
    assertNotNull( dv);
    assertEquals( 2, dv.length);

    assertEquals(
      dv[0].get("xs:double", Double.class).toString(), "1.2");
    assertEquals(
      dv[1].get("xs:double", Double.class).toString(), "2.2");

    Common.restAdminClient.newServerConfigManager().newQueryOptionsManager().deleteOptions(optionsName);
  }

  // this test only works if you've loaded the 5min guide @Test
  public void serverValuesList() throws IOException, ParserConfigurationException, SAXException {
    String optionsName = "photos";

    QueryManager queryMgr = Common.client.newQueryManager();
    ValuesListDefinition vdef = queryMgr.newValuesListDefinition(optionsName);

    ValuesListHandle results = queryMgr.valuesList(vdef, new ValuesListHandle());
    assertNotNull(results);
    Map<String,String> map = results.getValuesMap();
    assertEquals( map.size(), 2);
    assertEquals( map.get("size"), "/v1/values/size?options=photos");

    // test pagelength
    queryMgr.setPageLength(1L);
    results = queryMgr.valuesList(vdef, new ValuesListHandle());
    assertNotNull(results);
    map = results.getValuesMap();
    assertEquals( map.size(), 1);

  }

  static public String makeValuesOptions()
    throws FailedRequestException, ForbiddenUserException, ResourceNotFoundException, ResourceNotResendableException
  {
    String options =
      "<?xml version='1.0'?>"+
      "<options xmlns=\"http://marklogic.com/appservices/search\">"+
        "<values name=\"grandchild\">"+
           "<range type=\"xs:string\">"+
              "<element ns=\"\" name=\"grandchild\"/>"+
           "</range>"+
        "</values>"+
        "<values name=\"double\">"+
           "<range type=\"xs:double\">"+
              "<element ns=\"\" name=\"double\"/>"+
           "</range>"+
        "</values>"+
        "<return-metrics>false</return-metrics>"+
      "</options>";

    QueryOptionsManager optionsMgr = Common.restAdminClient.newServerConfigManager().newQueryOptionsManager();
    optionsMgr.writeOptions("valuesoptions", new StringHandle(options));

    return "valuesoptions";
  }

  static public class MyValuesHandle extends ValuesHandle {
    public void parseTestData(InputStream stream) {
      receiveContent(stream);
    }
  }

  static public class MyValuesListHandle extends ValuesListHandle {
    public void parseTestData(InputStream stream) {
      receiveContent(stream);
    }
  }
}
