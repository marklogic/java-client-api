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

import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import static org.custommonkey.xmlunit.XMLAssert.assertXpathEvaluatesTo;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import com.marklogic.client.bitemporal.TemporalDocumentManager.ProtectionLevel;
import com.marklogic.client.document.DocumentPatchBuilder;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.document.DocumentMetadataPatchBuilder;
import com.marklogic.client.document.DocumentMetadataPatchBuilder.Cardinality;
import com.marklogic.client.document.DocumentPatchBuilder.Position;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.io.marker.DocumentPatchHandle;
import com.marklogic.client.query.MatchDocumentSummary;
import com.marklogic.client.query.QueryDefinition;
import com.marklogic.client.query.QueryManager;

public class BitemporalFeaturesTest {

  static String temporalCollection = "temporal-collection";
  static String temporalDocument1 = "temporal-document1";
  static String temporalDocument2 = "temporal-document2";
  static String temporalDocument3 = "temporal-document3";
  static String temporalDocument4 = "temporal-document4";
  static String temporalDocument5 = "temporal-document5";

  static XMLDocumentManager docMgr;
  static QueryManager queryMgr;
  static String uniqueBulkTerm = "temporalBulkDocTerm";
  static String uniqueTerm1 = "temporalDocTerm1";
  static String uniqueTerm2 = "temporalDocTerm2";
  static String uniqueTerm3 = "temporalDocTerm3";
  static String docId1 = "test-" + uniqueTerm1 + ".xml";
  static String docId2 = "test-" + uniqueTerm2 + ".xml";
  static String docId3 = "test-" + uniqueTerm3 + ".xml";

  @BeforeClass
  public static void beforeClass() {
    Common.connectAdmin();
    docMgr = Common.adminClient.newXMLDocumentManager();
    queryMgr = Common.adminClient.newQueryManager();
  }

  @AfterClass
  public static void afterClass() throws DatatypeConfigurationException {
    cleanUp();
  }

  @Test
  public void testBitemporalDocument() {
    String doc1 = "<test>" +
      uniqueTerm1 + " doc1" +
      "<system-start></system-start>" +
      "<system-end></system-end>" +
      "<valid-start>2014-08-19T00:00:00Z</valid-start>" +
      "<valid-end>2014-08-19T00:00:01Z</valid-end>" +
      "</test>";
    String doc2 = "<test>" +
      uniqueTerm2 + " doc1" +
      "<system-start></system-start>" +
      "<system-end></system-end>" +
      "<valid-start>2014-08-19T00:00:00Z</valid-start>" +
      "<valid-end>2014-08-19T00:00:01Z</valid-end>" +
      "</test>";
    String doc3 = "<test>" +
      uniqueTerm3 + " doc1" +
      "<system-start></system-start>" +
      "<system-end></system-end>" +
      "<valid-start>2014-08-19T00:00:00Z</valid-start>" +
      "<valid-end>2014-08-19T00:00:01Z</valid-end>" +
      "</test>";
    StringHandle handle1 = new StringHandle(doc1).withFormat(Format.XML);
    docMgr.write(docId1, temporalDocument1, null, handle1, null, null, temporalCollection);
    StringHandle handle2 = new StringHandle(doc2).withFormat(Format.XML);
    docMgr.write(docId2, temporalDocument2, null, handle2, null, null, temporalCollection);
    StringHandle handle3 = new StringHandle(doc3).withFormat(Format.XML);
    docMgr.write(docId3, temporalDocument1, null, handle3, null, null, temporalCollection);
    QueryManager queryMgr = Common.adminClient.newQueryManager();
    queryMgr.setPageLength(1000);
    QueryDefinition query = queryMgr.newStringDefinition();
    query.setCollections(temporalDocument1);
    SearchHandle handle = queryMgr.search(query, new SearchHandle());
    MatchDocumentSummary[] docs = handle.getMatchResults();
    assertEquals("Incorrect number of docs",2, docs.length);
  }

  @Test
  public void testBitemporalDocumentBulk() {
    String prefix = "test_" + uniqueBulkTerm;
    String doc1 = "<test>" +
      uniqueBulkTerm + " doc1" +
      "<system-start></system-start>" +
      "<system-end></system-end>" +
      "<valid-start>2014-08-19T00:00:00Z</valid-start>" +
      "<valid-end>2014-08-19T00:00:01Z</valid-end>" +
      "</test>";
    String doc2 = "<test>" +
      uniqueBulkTerm + " doc2" +
      "<system-start></system-start>" +
      "<system-end></system-end>" +
      "<valid-start>2014-08-19T00:00:02Z</valid-start>" +
      "<valid-end>2014-08-19T00:00:03Z</valid-end>" +
      "</test>";
    String doc3 = "<test>" +
      uniqueBulkTerm + " doc3" +
      "<system-start></system-start>" +
      "<system-end></system-end>" +
      "<valid-start>2014-08-19T00:00:03Z</valid-start>" +
      "<valid-end>2014-08-19T00:00:04Z</valid-end>" +
      "</test>";
    String doc4 = "<test>" +
      uniqueBulkTerm + " doc4" +
      "<system-start></system-start>" +
      "<system-end></system-end>" +
      "<valid-start>2014-08-19T00:00:05Z</valid-start>" +
      "<valid-end>2014-08-19T00:00:06Z</valid-end>" +
      "</test>";
    DocumentWriteSet writeSet = docMgr.newWriteSet();
    writeSet.add(prefix + "_A.xml", new StringHandle(doc1).withFormat(Format.XML), temporalDocument1);
    writeSet.add(prefix + "_B.xml", new StringHandle(doc2).withFormat(Format.XML), temporalDocument2);
    writeSet.add(prefix + "_C.xml", new StringHandle(doc3).withFormat(Format.XML), temporalDocument3);
    writeSet.add(prefix + "_D.xml", new StringHandle(doc4).withFormat(Format.XML), temporalDocument4);
    docMgr.write(writeSet, null, null, temporalCollection);
    writeSet = docMgr.newWriteSet();
    QueryManager queryMgr = Common.adminClient.newQueryManager();
    queryMgr.setPageLength(1000);
    QueryDefinition query = queryMgr.newStringDefinition();
    query.setCollections(temporalDocument1);
    SearchHandle handle = queryMgr.search(query, new SearchHandle());
    MatchDocumentSummary[] docs = handle.getMatchResults();
    assertEquals("Incorrect number of docs",3, docs.length);
    query = queryMgr.newStringDefinition();
    query.setCollections(temporalDocument2);
    handle = queryMgr.search(query, new SearchHandle());
    docs = handle.getMatchResults();
    assertEquals("Incorrect number of docs",2, docs.length);
  }

  @Test
  public void testTemporalDocumentPatch() throws XpathException, SAXException, IOException {
    String doc1 = "<test>" +
      uniqueTerm1 + " doc1" +
      "<system-start></system-start>" +
      "<system-end></system-end>" +
      "<valid-start>2014-08-19T00:00:00Z</valid-start>" +
      "<valid-end>2014-08-19T00:00:01Z</valid-end>" +
      "<song>Here without you</song>" +
      "</test>";

    StringHandle handle1 = new StringHandle(doc1).withFormat(Format.XML);
    docMgr.write(temporalDocument5, null, handle1, null, null, temporalCollection);

    DocumentPatchBuilder patchBldr = docMgr.newPatchBuilder();
    patchBldr.insertFragment("/test/song", Position.AFTER, "<song>Kryptonite</song>");
    DocumentPatchHandle patchHandle = patchBldr.build();
    docMgr.patch(temporalDocument5, temporalCollection, patchHandle);
    String content = docMgr.read(temporalDocument5, new StringHandle().withFormat(Format.XML)).get();
    assertXpathEvaluatesTo("2","count(/*[local-name()='test']/*[local-name()='song'])",content);

    DocumentMetadataPatchBuilder metadatapatchBldr = docMgr.newPatchBuilder(Format.XML);
    DocumentPatchHandle metadatapatchHandle = metadatapatchBldr
      .addMetadataValue("key1", "value1").build();
    docMgr.patch(temporalDocument5, temporalCollection, metadatapatchHandle);
    String metadata = docMgr.readMetadata(temporalDocument5, new StringHandle().withFormat(Format.XML)).get();
    assertXpathEvaluatesTo("2","count(/*[local-name()='metadata']/*[local-name()='metadata-values']/*[local-name()='metadata-value'])",metadata);

    patchBldr = docMgr.newPatchBuilder();
    patchBldr.insertFragment("/test", Position.LAST_CHILD, "<song>Here I am</song>");
    patchHandle = patchBldr.build();
    docMgr.patch("temporal-document5v1", temporalDocument5, temporalCollection, temporalDocument5, patchHandle);
    content = docMgr.read("temporal-document5v1", new StringHandle().withFormat(Format.XML)).get();
    assertXpathEvaluatesTo("3","count(/*[local-name()='test']/*[local-name()='song'])",content);

    patchBldr = docMgr.newPatchBuilder();
    patchBldr.insertFragment("/test", Position.LAST_CHILD, "<song>Please forgive me</song>");
    patchHandle = patchBldr.build();
    docMgr.patch("temporal-document5v2", temporalDocument5, temporalCollection, "temporal-document5v1", patchHandle);
    content = docMgr.read("temporal-document5v2", new StringHandle().withFormat(Format.XML)).get();
    assertXpathEvaluatesTo("4","count(/*[local-name()='test']/*[local-name()='song'])",content);
  }

  @Test
  public void testProtectWipe() throws DatatypeConfigurationException {
    String protectDocID = "protectedDocument.xml";
    String protectDocIDv2 = "protectedDocumentv2.xml";
    String logicalID = "protectDocument.xml";
    String doc1 = "<test>" +
      "protect doc1" +
      "<system-start></system-start>" +
      "<system-end></system-end>" +
      "<valid-start>2014-08-19T00:00:00Z</valid-start>" +
      "<valid-end>2014-08-19T00:00:01Z</valid-end>" +
      "</test>";
    String doc2 = "<test>" +
      "protect doc2" +
      "<system-start></system-start>" +
      "<system-end></system-end>" +
      "<valid-start>2014-08-19T00:00:01Z</valid-start>" +
      "<valid-end>2014-08-19T00:00:02Z</valid-end>" +
      "</test>";
    StringHandle handle1 = new StringHandle(doc1).withFormat(Format.XML);
    docMgr.write(protectDocID, logicalID, null, handle1, null, null, temporalCollection);
    StringHandle handle2 = new StringHandle(doc2).withFormat(Format.XML);
    docMgr.write(protectDocIDv2, logicalID, null, handle2, null, null, temporalCollection);
    docMgr.protect(logicalID, temporalCollection, ProtectionLevel.NOWIPE, DatatypeFactory.newInstance().newDuration("PT1S"));

    Common.waitFor(1500);

    docMgr.wipe(logicalID, temporalCollection);
    QueryManager queryMgr = Common.adminClient.newQueryManager();
    queryMgr.setPageLength(1000);
    QueryDefinition query = queryMgr.newStringDefinition();
    query.setCollections(logicalID);
    SearchHandle handle = queryMgr.search(query, new SearchHandle());
    MatchDocumentSummary[] docs = handle.getMatchResults();
    assertEquals("Incorrect number of docs",0, docs.length);
  }

  static public void cleanUp() throws DatatypeConfigurationException {
    String temporalDoc = "temporal-document";
    for (int i = 1; i < 6; i++) {
      docMgr.protect(temporalDoc + i, temporalCollection, ProtectionLevel.NOWIPE,
        DatatypeFactory.newInstance().newDuration("PT1S"));
    }

    Common.waitFor(1500);

    for (int i = 1; i < 6; i++) {
      docMgr.wipe(temporalDoc + i, temporalCollection);
    }
  }
}
