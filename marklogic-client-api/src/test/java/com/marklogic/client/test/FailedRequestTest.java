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
import static org.junit.Assert.assertFalse;

import org.junit.Test;
import org.junit.Ignore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.FailedRequestException;
import com.marklogic.client.ForbiddenUserException;
import com.marklogic.client.DatabaseClientFactory.DigestAuthContext;
import com.marklogic.client.ResourceNotFoundException;
import com.marklogic.client.ResourceNotResendableException;
import com.marklogic.client.admin.QueryOptionsManager;
import com.marklogic.client.admin.ServerConfigurationManager;
import com.marklogic.client.io.StringHandle;

import java.io.StringWriter;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class FailedRequestTest {
  private static final Logger logger = LoggerFactory.getLogger(FailedRequestTest.class);

  @Test
  public void testFailedRequest()
  throws FailedRequestException, ForbiddenUserException, ResourceNotFoundException, ResourceNotResendableException, XMLStreamException
  {
    Common.connectAdmin();
    QueryOptionsManager mgr = Common.adminClient.newServerConfigManager()
      .newQueryOptionsManager();

    try {
      mgr.writeOptions("testempty", new StringHandle("<options xmlns=\"http://marklogic.com/appservices/search\"/>"));
//      assertFalse("expected options write to fail because forbidden", true);
    } catch (ForbiddenUserException e) {
      assertEquals(
          "Local message: User is not allowed to write /config/query. Server Message: You do not have permission to this method and URL.",
          e.getMessage());
      assertEquals(403, e.getFailedRequest().getStatusCode());
      assertEquals("Forbidden", e.getFailedRequest().getStatus());
    }
    mgr = Common.adminClient.newServerConfigManager().newQueryOptionsManager();

    Common.adminClient.newServerConfigManager().setQueryOptionValidation(true);

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
//      assertFalse("expected options write to fail because empty", true);
    } catch (FailedRequestException e) {
      assertEquals(
        "Local message: /config/query write failed: Bad Request. Server Message: RESTAPI-INVALIDCONTENT: (err:FOER0000) Invalid content: Operation results in invalid Options: Operator or constraint name \"blah\" is used more than once (must be unique).",
        e.getMessage());
      assertEquals(400, e.getFailedRequest().getStatusCode());
      assertEquals("Bad Request", e.getFailedRequest().getStatus());
      assertEquals("RESTAPI-INVALIDCONTENT", e.getFailedRequest()
        .getMessageCode());
    }

  }

  // Test testErrorOnNonREST commented out because of Git issue #865
  @Ignore
  public void testErrorOnNonREST() throws ForbiddenUserException {
    DatabaseClient badClient = DatabaseClientFactory.newClient(Common.HOST,
      8001, new DigestAuthContext(Common.USER, Common.PASS));
    ServerConfigurationManager serverConfig = badClient
      .newServerConfigManager();

    try {
      serverConfig.readConfiguration();
    } catch (FailedRequestException e) {


      assertEquals(
        "Local message: config/properties read failed: Not Found. Server Message: Server (not a REST instance?) did not respond with an expected REST Error message.",
        e.getMessage());
      assertEquals(404, e.getFailedRequest().getStatusCode());
      assertEquals("UNKNOWN", e.getFailedRequest().getStatus());
    } finally {
      badClient.release();
    }

  }

}
