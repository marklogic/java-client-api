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

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;

import javax.xml.bind.JAXBContext;

import org.junit.Test;

import com.marklogic.client.extra.dom4j.DOM4JHandle;
import com.marklogic.client.extra.gson.GSONHandle;
import com.marklogic.client.extra.jdom.JDOMHandle;
// NOTE: uncomment to test XOMHandle
// import com.marklogic.client.extra.xom.XOMHandle;
import com.marklogic.client.impl.HandleAccessor;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.InputSourceHandle;
import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.client.io.JAXBHandle;
import com.marklogic.client.io.JacksonDatabindHandle;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.JacksonParserHandle;
import com.marklogic.client.io.QueryOptionsListHandle;
import com.marklogic.client.io.ReaderHandle;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.io.SourceHandle;
import com.marklogic.client.io.TuplesHandle;
import com.marklogic.client.io.ValuesHandle;
import com.marklogic.client.io.ValuesListHandle;
import com.marklogic.client.io.XMLEventReaderHandle;
import com.marklogic.client.io.XMLStreamReaderHandle;
import com.marklogic.client.io.marker.AbstractReadHandle;

public class ClosingHandlesTest {
  private static class MockInputStream extends ByteArrayInputStream {
    public MockInputStream(byte[] buf) {
      super(buf);
    }

    private boolean isClosed = false;
    @Override
    public void close() throws IOException {
      super.close();
      isClosed = true;
    }

    public boolean isClosed() {
      return isClosed;
    }
  }

  @Test
  public void closeHandles() throws Exception {
    JAXBContext context = JAXBContext.newInstance(City.class);
    validateClosingHandleClosesUnderlyingStream(
      new DocumentMetadataHandle(), "<metadata xmlns='http://marklogic.com/rest-api'/>");
    validateClosingHandleClosesUnderlyingStream(new DOMHandle(), "<xml/>");
    validateClosingHandleClosesUnderlyingStream(new DOM4JHandle(), "<xml/>");
    validateClosingHandleClosesUnderlyingStream(new GSONHandle(), "null");
    validateClosingHandleClosesUnderlyingStream(new InputSourceHandle(), "testing");
    validateClosingHandleClosesUnderlyingStream(new InputStreamHandle(), "testing");
    validateClosingHandleClosesUnderlyingStream(new JacksonHandle(), "null");
    validateClosingHandleClosesUnderlyingStream(
      new JacksonDatabindHandle<>(Object.class), "null");
    validateClosingHandleClosesUnderlyingStream(new JacksonParserHandle(), "null");
    validateClosingHandleClosesUnderlyingStream(
      new JAXBHandle<>(context), "<city><population>0</population></city>");
    validateClosingHandleClosesUnderlyingStream(new JDOMHandle(), "<xml/>");
    validateClosingHandleClosesUnderlyingStream(
      new QueryOptionsListHandle(), "<query-options xmlns='http://marklogic.com/rest-api'/>");
    validateClosingHandleClosesUnderlyingStream(new ReaderHandle(), "testing");
    validateClosingHandleClosesUnderlyingStream(
      new SearchHandle(), "<response page-length='0' start='0' xmlns='http://marklogic.com/appservices/search'/>");
    validateClosingHandleClosesUnderlyingStream(new SourceHandle(), "testing");
    validateClosingHandleClosesUnderlyingStream(
      new TuplesHandle(), "<values-response xmlns='http://marklogic.com/appservices/search'/>");
    validateClosingHandleClosesUnderlyingStream(
      new ValuesHandle(), "<values-response xmlns='http://marklogic.com/appservices/search'/>");
    validateClosingHandleClosesUnderlyingStream(
      new ValuesListHandle(), "<values-list xmlns='http://marklogic.com/rest-api'/>");
    validateClosingHandleClosesUnderlyingStream(new XMLEventReaderHandle(), "<xml/>");
    validateClosingHandleClosesUnderlyingStream(new XMLStreamReaderHandle(), "<xml/>");
// NOTE: uncomment to test XOMHandle
//  validateClosingHandleClosesUnderlyingStream(new XOMHandle(), "<xml/>");
  }

  private void validateClosingHandleClosesUnderlyingStream(AbstractReadHandle handle, String content)
    throws Exception
  {
    MockInputStream stream = new MockInputStream(content.getBytes());
    HandleAccessor.receiveContent(handle, stream);
    if ( handle instanceof Closeable ) {
      ((Closeable) handle).close();
    }
    assertTrue("Handle " + handle.getClass().getName() + " did not close underlying InputStream", stream.isClosed());

  }

}
