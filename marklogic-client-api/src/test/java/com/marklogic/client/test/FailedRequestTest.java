/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.test;

import com.marklogic.client.*;
import com.marklogic.client.admin.QueryOptionsManager;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.StringHandle;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;

public class FailedRequestTest {
  private static final Logger logger = LoggerFactory.getLogger(FailedRequestTest.class);

  @Test
  public void testFailedRequest()
  throws FailedRequestException, ForbiddenUserException, ResourceNotFoundException, ResourceNotResendableException, XMLStreamException
  {
    Common.connectRestAdmin();
    QueryOptionsManager mgr = Common.restAdminClient.newServerConfigManager()
      .newQueryOptionsManager();

    try {
      mgr.writeOptions("testempty", new StringHandle("<options xmlns=\"http://marklogic.com/appservices/search\"/>"));
//      assertFalse( true);
    } catch (ForbiddenUserException e) {
      assertEquals(
          "Local message: User is not allowed to write /config/query. Server Message: You do not have permission to this method and URL.",
          e.getMessage());
      assertEquals(403, e.getServerStatusCode());
      assertEquals("Forbidden", e.getServerStatus());
    }
    mgr = Common.restAdminClient.newServerConfigManager().newQueryOptionsManager();

    Common.restAdminClient.newServerConfigManager().setQueryOptionValidation(true);

    Common.propertyWait();

    StringWriter xml = new StringWriter();
    XMLStreamWriter xsw = XMLOutputFactory.newInstance().createXMLStreamWriter(xml);
    xsw.writeStartDocument();
    xsw.writeStartElement("options");
      xsw.writeDefaultNamespace("http://marklogic.com/appservices/search");
      xsw.writeStartElement("constraint");
        xsw.writeAttribute("name", "blah");
        xsw.writeStartElement("collection");
          xsw.writeAttribute("prefix", "S");
          xsw.writeAttribute("facet", "false");
        xsw.writeEndElement();//"collection"
      xsw.writeEndElement();//"constraint"
      xsw.writeStartElement("constraint");
        xsw.writeAttribute("name", "blah");
        xsw.writeStartElement("collection");
          xsw.writeAttribute("prefix", "D");
          xsw.writeAttribute("facet", "false");
        xsw.writeEndElement();//"collection"
      xsw.writeEndElement();//"constraint"
    xsw.writeEndElement(); //"http://marklogic.com/appservices/search", "options"
    xsw.writeEndDocument();

    logger.debug(xml.toString());
    try {
      mgr.writeOptions("testempty", new StringHandle(xml.toString()));
//      assertFalse( true);
    } catch (FailedRequestException e) {
      assertEquals(
        "Local message: /config/query write failed: Bad Request. Server Message: RESTAPI-INVALIDCONTENT: (err:FOER0000) Invalid content: Operation results in invalid Options: Operator or constraint name \"blah\" is used more than once (must be unique).",
        e.getMessage());
      assertEquals(400, e.getServerStatusCode());
      assertEquals("Bad Request", e.getServerStatus());
      assertEquals("RESTAPI-INVALIDCONTENT", e.getServerMessageCode());
    }

  }

  @Test
  public void testFailedRequestParsing() {
    DatabaseClient client = Common.connect();
    XMLDocumentManager docMgr = client.newXMLDocumentManager();
    try {
      docMgr.write("/failed.xml", new StringHandle("{\"json\":\"object\"}").withFormat(Format.XML));
      fail("Invalid call succeeded");
    } catch (MarkLogicServerException e) {
      assertEquals(400, e.getServerStatusCode());
      assertEquals("Bad Request", e.getServerStatus());
      assertEquals("XDMP-DOCROOTTEXT", e.getServerMessageCode());
      assertEquals("XDMP-DOCROOTTEXT: Invalid root text \"{&quot;json&quot;:&quot;object&quot;}\" at  line 1", e.getServerMessage());
      assertNull(e.getServerStackTrace());
    } catch (Exception e) {
      fail("Call failed with unexpected exception: "+e.getMessage());
    }
  }
}
