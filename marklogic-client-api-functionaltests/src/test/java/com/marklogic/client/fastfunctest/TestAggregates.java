/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.fastfunctest;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.io.TuplesHandle;
import com.marklogic.client.io.ValuesHandle;
import com.marklogic.client.query.AggregateResult;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.ValuesDefinition;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;

public class TestAggregates extends AbstractFunctionalTest {

  @AfterEach
  public void tearDown() {
    deleteDocuments(client);
  }

  @Test
  public void testValuesAggregates() throws IOException, KeyManagementException, NoSuchAlgorithmException
  {
    System.out.println("Running testValuesAggregates");

    String[] filenames = { "aggr1.xml", "aggr2.xml", "aggr3.xml", "aggr4.xml", "aggr5.xml" };
    String queryOptionName = "aggregatesOpt.xml";

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

    // write docs
    for (String filename : filenames)
    {
      writeDocumentUsingInputStreamHandle(client, filename, "/values-aggr/", "XML");
    }

    setQueryOption(client, queryOptionName);

    QueryManager queryMgr = client.newQueryManager();

    // create query def
    ValuesDefinition queryDef = queryMgr.newValuesDefinition("popularity", "aggregatesOpt.xml");
    queryDef.setAggregate("sum", "avg", "max", "min");
    queryDef.setName("pop-aggr");

    // create handle
    ValuesHandle valuesHandle = new ValuesHandle();
    queryMgr.values(queryDef, valuesHandle);

    AggregateResult[] agg = valuesHandle.getAggregates();
    System.out.println(agg.length);
    assertEquals( 4, agg.length);
    int sum = agg[0].get("xs:int", Integer.class);
    double avg = agg[1].get("xs:double", Double.class);
    int max = agg[2].get("xs:int", Integer.class);
    int min = agg[3].get("xs:int", Integer.class);
    System.out.println(sum);
    assertEquals( 22, sum);
    System.out.println(avg);
    assertEquals( 4.4, avg, 0);
    System.out.println(max);
    assertEquals( 5, max);
    System.out.println(min);
    assertEquals( 3, min);
    System.out.println(agg[0].getValue());
    assertEquals( "22", agg[0].getValue());
    System.out.println(agg[1].getValue());
    assertEquals( "4.4", agg[1].getValue());
    System.out.println(agg[2].getValue());
    assertEquals( "5", agg[2].getValue());
    System.out.println(agg[3].getValue());
    assertEquals( "3", agg[3].getValue());

    QueryManager queryMgr1 = client.newQueryManager();
    // create query def
    ValuesDefinition queryDef1 = queryMgr1.newValuesDefinition("score", "aggregatesOpt.xml");
    queryDef1.setAggregate("sum", "avg", "max", "min");
    queryDef1.setName("score-aggr");

    // create handle
    ValuesHandle valuesHandle1 = new ValuesHandle();
    queryMgr.values(queryDef1, valuesHandle1);

    AggregateResult[] agg1 = valuesHandle1.getAggregates();

    System.out.println("Length :" + agg1.length + " Value :" + agg[1].getValue());
    double score_sum = agg1[0].get("xs:double", Double.class);
    double score_avg = agg1[1].get("xs:double", Double.class);
    double score_max = agg1[2].get("xs:double", Double.class);
    double score_min = agg1[3].get("xs:double", Double.class);
    System.out.println("Sum :" + score_sum + " Average :" + score_avg + " Max Score :" + score_max + " Min Score :" + score_min);

    // release client
    client.release();
  }

  @Test
  public void testValuesAggregatesWithNS() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException,
      TransformerException
  {
    System.out.println("Running testValuesAggregatesWithNS");

    String[] filenames = { "aggr1.xml", "aggr2.xml", "aggr3.xml", "aggr4.xml", "aggr5.xml" };
    String queryOptionName = "aggregatesOpt.xml";

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

    // write docs
    for (String filename : filenames)
    {
      writeDocumentUsingInputStreamHandle(client, filename, "/values-aggr-ns/", "XML");
    }

    setQueryOption(client, queryOptionName);

    QueryManager queryMgr = client.newQueryManager();

    // create query def
    ValuesDefinition queryDef = queryMgr.newValuesDefinition("score", "aggregatesOpt.xml");
    queryDef.setAggregate("sum", "avg", "max", "min");
    queryDef.setName("score-aggr");

    // create handle
    ValuesHandle valuesHandle = new ValuesHandle();
    queryMgr.values(queryDef, valuesHandle);

    AggregateResult[] agg = valuesHandle.getAggregates();
    System.out.println(agg.length);
    assertEquals( 4, agg.length);
    double sum = agg[0].get("xs:double", Double.class);
    double avg = agg[1].get("xs:double", Double.class);
    double max = agg[2].get("xs:double", Double.class);
    double min = agg[3].get("xs:double", Double.class);

    DecimalFormat df = new DecimalFormat("###.##");
    String roundedSum = df.format(sum);
    String roundedAvg = df.format(avg);

    System.out.println("roundedSum :" + roundedSum);
    assertEquals( "272.73", roundedSum);
    System.out.println("roundedAvg :" + roundedAvg);
    assertEquals( "54.55", roundedAvg);
    System.out.println("Max :" + max);
    assertEquals( 92.45, max, 0);
    System.out.println("Min :" + min);
    assertEquals( 12.34, min, 0);
    System.out.println("agg[0] :" + agg[0].getValue() + " After Formatting :" + df.format(agg[0].get("xs:double", Double.class)));
    assertEquals( "272.73", df.format(agg[0].get("xs:double", Double.class)));
    System.out.println("agg[1] :" + agg[1].getValue() + " After Formatting :" + df.format(agg[1].get("xs:double", Double.class)));
    assertEquals( "54.55", df.format(agg[1].get("xs:double", Double.class)));
    System.out.println("agg[2] :" + agg[2].getValue());
    assertEquals( "92.45", df.format(agg[2].get("xs:double", Double.class)));
    System.out.println("agg[3] :" + agg[3].getValue());
    assertEquals( "12.34", df.format(agg[3].get("xs:double", Double.class)));

    // release client
    client.release();
  }

  @Test
  public void testTuplesAggregates() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException,
      TransformerException
  {
    System.out.println("Running testTuplesAggregates");

    String[] filenames = { "aggr1.xml", "aggr2.xml", "aggr3.xml", "aggr4.xml", "aggr5.xml" };
    String queryOptionName = "aggregatesOpt.xml";

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

    // write docs
    for (String filename : filenames)
    {
      writeDocumentUsingInputStreamHandle(client, filename, "/tuples-aggr/", "XML");
    }

    setQueryOption(client, queryOptionName);

    QueryManager queryMgr = client.newQueryManager();

    // create query def
    ValuesDefinition queryDef = queryMgr.newValuesDefinition("popularity", "aggregatesOpt.xml");
    queryDef.setAggregate("correlation", "covariance");
    queryDef.setName("pop-rate-tups");

    // create handle
    TuplesHandle tuplesHandle = new TuplesHandle();
    queryMgr.tuples(queryDef, tuplesHandle);

    AggregateResult[] agg = tuplesHandle.getAggregates();
    System.out.println(agg.length);
    assertEquals( 2, agg.length);
    double correlation = agg[0].get("xs:double", Double.class);
    double covariance = agg[1].get("xs:double", Double.class);

    DecimalFormat df = new DecimalFormat("###.##");
    String roundedCorrelation = df.format(correlation);
    String roundedCovariance = df.format(covariance);

    System.out.println(roundedCorrelation);
    System.out.println(roundedCovariance);

    assertEquals( "0.26", roundedCorrelation);
    assertEquals( "0.35", roundedCovariance);

    // release client
    client.release();
  }

  @Test
  public void testValuesAggregatesWithJson() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException,
      TransformerException
  {
    System.out.println("Running testValuesAggregatesWithJson");

    String[] filenames = { "aggr1.xml", "aggr2.xml", "aggr3.xml", "aggr4.xml", "aggr5.xml" };
    String queryOptionName = "aggregatesOpt.xml";

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

    // write docs
    for (String filename : filenames)
    {
      writeDocumentUsingInputStreamHandle(client, filename, "/values-aggr/", "XML");
    }

    setQueryOption(client, queryOptionName);

    QueryManager queryMgr = client.newQueryManager();

    // create query def
    ValuesDefinition queryDef = queryMgr.newValuesDefinition("popularity", "aggregatesOpt.xml");
    queryDef.setAggregate("sum", "avg", "max", "min");
    queryDef.setName("pop-aggr");

    // create handle
    StringHandle resultHandle = new StringHandle().withFormat(Format.JSON);
    queryMgr.values(queryDef, resultHandle);

    String result = resultHandle.get();

    System.out.println(result);

    assertEquals("{", result.substring(0, 1));
    // release client
    client.release();
  }

  @Test
  public void testValuesAggregatesThreeOccurences() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException,
      XpathException, TransformerException
  {
    System.out.println("Running testValuesAggregatesThreeOccurences");

    String[] filenames = { "aggr1.xml", "aggr2.xml", "aggr3.xml", "aggr4.xml" };
    String queryOptionName = "aggregatesOpt3Occ.xml";

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

    // write docs
    for (String filename : filenames)
    {
      writeDocumentUsingInputStreamHandle(client, filename, "/values-aggr/", "XML");
    }

    setQueryOption(client, queryOptionName);

    QueryManager queryMgr = client.newQueryManager();

    // create query def
    ValuesDefinition queryDef = queryMgr.newValuesDefinition("date", "aggregatesOpt3Occ.xml");
    queryDef.setAggregate("count");
    queryDef.setName("date-val");

    // create handle
    ValuesHandle valuesHandle = new ValuesHandle();
    queryMgr.values(queryDef, valuesHandle);

    AggregateResult[] agg = valuesHandle.getAggregates();
    assertEquals( "4", agg[0].getValue());

    ValuesDefinition queryDef1 = queryMgr.newValuesDefinition("popularity", "aggregatesOpt3Occ.xml");
    queryDef1.setAggregate("correlation", "covariance");
    queryDef1.setName("pop-rate-tups");

    // create handle
    TuplesHandle tuplesHandle = new TuplesHandle();
    queryMgr.tuples(queryDef1, tuplesHandle);

    AggregateResult[] aggn = tuplesHandle.getAggregates();
    System.out.println(aggn.length);
    assertEquals( 2, aggn.length);
    double correlation = aggn[0].get("xs:double", Double.class);
    double covariance = aggn[1].get("xs:double", Double.class);

    DecimalFormat df = new DecimalFormat("###.##");
    String roundedCorrelation = df.format(correlation);
    String roundedCovariance = df.format(covariance);

    System.out.println(roundedCorrelation);
    System.out.println(roundedCovariance);
    assertEquals( "0.43", roundedCorrelation);
    assertEquals( "0.67", roundedCovariance);
    // release client
    client.release();
  }

  @Test
  public void testValuesAggregatesFiveOccurences() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException,
      XpathException, TransformerException
  {
    System.out.println("Running testValuesAggregatesThreeOccurences");

    String[] filenames = { "aggr1.xml", "aggr2.xml", "aggr3.xml", "aggr4.xml" };
    String queryOptionName = "aggregatesOpt5Occ.xml";

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

    // write docs
    for (String filename : filenames)
    {
      writeDocumentUsingInputStreamHandle(client, filename, "/values-aggr/", "XML");
    }

    setQueryOption(client, queryOptionName);

    QueryManager queryMgr = client.newQueryManager();

    // create query def
    ValuesDefinition queryDef = queryMgr.newValuesDefinition("title", "aggregatesOpt5Occ.xml");
    queryDef.setAggregate("count");
    queryDef.setName("title-val");

    // create handle
    ValuesHandle valuesHandle = new ValuesHandle();
    queryMgr.values(queryDef, valuesHandle);

    AggregateResult[] agg = valuesHandle.getAggregates();
    System.out.println(agg.length);
    System.out.println(agg[0].getValue());

    // release client
    client.release();
  }
}
