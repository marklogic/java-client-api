/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.test;

import com.marklogic.client.FailedRequestException;
import com.marklogic.client.ForbiddenUserException;
import com.marklogic.client.ResourceNotFoundException;
import com.marklogic.client.ResourceNotResendableException;
import com.marklogic.client.admin.QueryOptionsManager;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.io.TuplesHandle;
import com.marklogic.client.query.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class TuplesHandleTest {

  private static final Logger logger = (Logger) LoggerFactory
    .getLogger(TuplesHandleTest.class);


  private static final String options =
    "<?xml version='1.0'?>" +
    "<options xmlns=\"http://marklogic.com/appservices/search\">" +
      "<values name=\"grandchild\">" +
        "<range type=\"xs:string\">" +
          "<element ns=\"\" name=\"grandchild\"/>" +
        "</range>" +
        "<values-option>limit=2</values-option>" +
      "</values>" +
      "<tuples name=\"co\">" +
        "<range type=\"xs:double\">" +
          "<element ns=\"\" name=\"double\"/>" +
        "</range>" +
        "<range type=\"xs:int\">" +
          "<element ns=\"\" name=\"int\"/>" +
        "</range>" +
      "</tuples>" +
      "<tuples name=\"n-way\">" +
        "<range type=\"xs:double\">" +
          "<element ns=\"\" name=\"double\"/>" +
        "</range>" +
        "<range type=\"xs:int\">" +
          "<element ns=\"\" name=\"int\"/>" +
        "</range>" +
        "<range type=\"xs:string\">" +
          "<element ns=\"\" name=\"string\"/>" +
        "</range>" +
        "<values-option>ascending</values-option>" +
      "</tuples>" +
      "<return-metrics>true</return-metrics>" +
      "<return-values>true</return-values>" +
    "</options>";

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
    throws IOException, ParserConfigurationException, SAXException, ResourceNotFoundException, ForbiddenUserException, FailedRequestException, ResourceNotResendableException
  {
    QueryOptionsManager optionsMgr = Common.restAdminClient.newServerConfigManager().newQueryOptionsManager();
    optionsMgr.writeOptions("valuesoptions2", new StringHandle(options));

    logger.debug(options.toString());

    QueryManager queryMgr = Common.client.newQueryManager();

    ValuesDefinition vdef = queryMgr.newValuesDefinition("co", "valuesoptions2");
    vdef.setAggregate("correlation", "covariance");

    TuplesHandle t = queryMgr.tuples(vdef, new TuplesHandle());

    AggregateResult[] agg = t.getAggregates();
    assertEquals( 2, agg.length);

    double cov = t.getAggregate("covariance").get("xs:double", Double.class);
    assertTrue(
      cov > 1.551 && cov < 1.552);

    Tuple[] tuples = t.getTuples();
    assertEquals( 12, tuples.length);
    assertEquals( "co", t.getName());

    ValuesMetrics metrics = t.getMetrics();
    assertTrue(metrics.getValuesResolutionTime() >= 0);
    assertTrue(metrics.getAggregateResolutionTime() >= 0);

    optionsMgr.deleteOptions("valuesoptions2");
  }

  @Test
  public void testCoVariances()
    throws IOException, ParserConfigurationException, SAXException, ResourceNotFoundException, ForbiddenUserException, FailedRequestException, ResourceNotResendableException
  {
    QueryOptionsManager optionsMgr = Common.restAdminClient.newServerConfigManager().newQueryOptionsManager();
    optionsMgr.writeOptions("valuesoptions3", new StringHandle(options));

    QueryManager queryMgr = Common.client.newQueryManager();

    ValuesDefinition vdef = queryMgr.newValuesDefinition("co", "valuesoptions3");

    TuplesHandle t = queryMgr.tuples(vdef, new TuplesHandle());

    Tuple[] tuples = t.getTuples();
    assertEquals( 12, tuples.length);
    assertEquals( "co", t.getName());

    ValuesMetrics metrics = t.getMetrics();
    assertTrue(metrics.getValuesResolutionTime() >= 0);
    // Restore after bug:18747 is fixed
    // assertEquals("The aggregate resolution time is -1 (absent)", metrics.getAggregateResolutionTime(), -1);

    optionsMgr.deleteOptions("valuesoptions3");
  }

  @Test
  public void testValuesHandle()
    throws IOException, ParserConfigurationException, SAXException, ResourceNotFoundException, ForbiddenUserException, FailedRequestException, ResourceNotResendableException
  {
    QueryOptionsManager optionsMgr = Common.restAdminClient.newServerConfigManager().newQueryOptionsManager();
    optionsMgr.writeOptions("valuesoptions", new StringHandle(options));

    QueryManager queryMgr = Common.client.newQueryManager();

    ValuesDefinition vdef = queryMgr.newValuesDefinition("co", "valuesoptions");

    TuplesHandle t = queryMgr.tuples(vdef, new TuplesHandle());

    Tuple[] tuples = t.getTuples();
    assertEquals( 12, tuples.length);
    assertEquals( "co", t.getName());

    TypedDistinctValue[] dv = tuples[0].getValues();

    assertEquals( 2, dv.length);
    assertEquals( "xs:double",  dv[0].getType());
    assertEquals( "xs:int", dv[1].getType());
    assertEquals( 1, tuples[0].getCount());
    assertEquals(  1.1, (double) dv[0].get(Double.class), 0.01);
    assertEquals( (int) 1, (int) dv[1].get(Integer.class));

    optionsMgr.deleteOptions("valuesoptions");
  }

  @Test
  public void testNWayTuples()
    throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException, ResourceNotResendableException
  {
    QueryOptionsManager optionsMgr = Common.restAdminClient.newServerConfigManager().newQueryOptionsManager();
    optionsMgr.writeOptions("valuesoptions", new StringHandle(options));

    QueryManager queryMgr = Common.client.newQueryManager();

    ValuesDefinition vdef = queryMgr.newValuesDefinition("n-way", "valuesoptions");

    TuplesHandle t = queryMgr.tuples(vdef, new TuplesHandle());

    Tuple[] tuples = t.getTuples();
    assertEquals( 4, tuples.length);
    assertEquals( "n-way", t.getName());

    TypedDistinctValue[] dv = tuples[0].getValues();

    assertEquals( 3, dv.length);
    assertEquals( "xs:double",  dv[0].getType());
    assertEquals( "xs:int", dv[1].getType());
    assertEquals( "xs:string", dv[2].getType());
    assertEquals( 1, tuples[0].getCount());
    assertEquals(  1.1, (double) dv[0].get(Double.class), 0.01);
    assertEquals( (int) 1, (int) dv[1].get(Integer.class));
    assertEquals( "Alaska", (String) dv[2].get(String.class));
    optionsMgr.deleteOptions("valuesoptions");
  }

  @Test
  public void testPagingTuples()
    throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException, ResourceNotResendableException
  {
    QueryOptionsManager optionsMgr = Common.restAdminClient.newServerConfigManager().newQueryOptionsManager();
    optionsMgr.writeOptions("valuesoptions", new StringHandle(options));

    QueryManager queryMgr = Common.client.newQueryManager();
    queryMgr.setPageLength(6);

    ValuesDefinition vdef = queryMgr.newValuesDefinition("co", "valuesoptions");

    TuplesHandle t = queryMgr.tuples(vdef, new TuplesHandle(), 3);

    Tuple[] tuples = t.getTuples();
    assertEquals( 6, tuples.length);

    TypedDistinctValue[] values = tuples[0].getValues();
    String value = values[0].get(Double.class)+" | "+values[1].get(Integer.class);
    assertEquals("1.2 | 3", value);

    values = tuples[5].getValues();
    value =
      values[0].get(Double.class).toString()
        + " | "
        + values[1].get(Integer.class).toString();
    assertEquals("2.2 | 4", value);

    optionsMgr.deleteOptions("valuesoptions");
  }
}
