/*
 * Copyright (c) 2019 MarkLogic Corporation
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

import com.marklogic.client.io.Format;
import com.marklogic.client.io.marker.TriplesReadHandle;
import com.marklogic.client.semantics.RDFMimeTypes;
import com.marklogic.client.semantics.SPARQLQueryDefinition;
import com.marklogic.client.impl.HandleImplementation;
import com.marklogic.client.impl.RESTServices;
import com.marklogic.client.impl.SPARQLQueryManagerImpl;
import com.marklogic.client.impl.SPARQLQueryDefinitionImpl;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import static org.mockito.Mockito.mock;

/**
 *
 */
public class SPARQLQueryManagerImplTest {

  public SPARQLQueryManagerImplTest() {
  }

  /**
   * Test of executeDescribe method, of class SPARQLQueryManagerImpl.
   */
  @Test
  public void testExecuteDescribeWithJSON() {
    HandleImplementation triplesHandle = executeSetRdfXmlOrJsonMimetype(Format.JSON, Format.JSON.getDefaultMimetype());
    assertTrue(triplesHandle.getMimetype().equals(RDFMimeTypes.RDFJSON));
  }

  @Test
  public void testExecuteDescribeWithJSONNotMime() {
    HandleImplementation triplesHandle = executeSetRdfXmlOrJsonMimetype(Format.JSON, Format.TEXT.getDefaultMimetype());
    assertFalse(triplesHandle.getMimetype().equals(RDFMimeTypes.RDFJSON));
    assertTrue(triplesHandle.getMimetype().equals(Format.TEXT.getDefaultMimetype()));
  }

  @Test
  public void testExecuteDescribeWithUNKNOWNFormat() {
    HandleImplementation triplesHandle = executeSetRdfXmlOrJsonMimetype(Format.UNKNOWN, Format.JSON.getDefaultMimetype());
    assertFalse(triplesHandle.getMimetype().equals(RDFMimeTypes.RDFJSON));
  }

  @Test
  public void testExecuteDescribeWithXML() {
    HandleImplementation triplesHandle = executeSetRdfXmlOrJsonMimetype(Format.XML, Format.XML.getDefaultMimetype());
    assertTrue(triplesHandle.getMimetype().equals(RDFMimeTypes.RDFXML));
  }

  @Test
  public void testExecuteDescribeWithXMLNotMime() {
    HandleImplementation triplesHandle = executeSetRdfXmlOrJsonMimetype(Format.XML, Format.TEXT.getDefaultMimetype());
    assertFalse(triplesHandle.getMimetype().equals(RDFMimeTypes.RDFXML));
    assertTrue(triplesHandle.getMimetype().equals(Format.TEXT.getDefaultMimetype()));
  }

  private HandleImplementation executeSetRdfXmlOrJsonMimetype(Format format, String mimeType) {
    HandleImplementation triplesHandle = new HandleImplementationTester();
    triplesHandle.setFormat(format);
    triplesHandle.setMimetype(mimeType);

    RESTServices services = mock(RESTServices.class);
    SPARQLQueryManagerImpl sparqlQueryManager = new SPARQLQueryManagerImpl(services);
    SPARQLQueryDefinition qdef = new SPARQLQueryDefinitionImpl();

    sparqlQueryManager.executeDescribe(qdef, (TriplesReadHandle) triplesHandle);
    return triplesHandle;
  }

  protected class HandleImplementationTester extends HandleImplementation implements TriplesReadHandle {

    private String mimeType;
    private Format format;

    @Override
    public Format getFormat() {
      return format;
    }

    @Override
    public void setFormat(Format format) {
      this.format = format;
    }

    @Override
    public String getMimetype() {
      return mimeType;
    }

    @Override
    public void setMimetype(String mimeType) {
      this.mimeType = mimeType;
    }

    @Override
    public long getByteLength() {
      throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setByteLength(long length) {
      throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

  }
}
