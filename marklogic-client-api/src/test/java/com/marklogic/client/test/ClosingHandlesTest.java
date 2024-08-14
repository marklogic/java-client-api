/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.test;

import com.marklogic.client.extra.dom4j.DOM4JHandle;
import com.marklogic.client.extra.gson.GSONHandle;
import com.marklogic.client.extra.jdom.JDOMHandle;
import com.marklogic.client.impl.HandleAccessor;
import com.marklogic.client.io.*;
import com.marklogic.client.io.marker.AbstractReadHandle;
import org.junit.jupiter.api.Test;

import jakarta.xml.bind.JAXBContext;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;

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
  }

  private void validateClosingHandleClosesUnderlyingStream(AbstractReadHandle handle, String content)
    throws Exception
  {
    MockInputStream stream = new MockInputStream(content.getBytes());
    HandleAccessor.receiveContent(handle, stream);
    if ( handle instanceof Closeable ) {
      ((Closeable) handle).close();
    }
    assertTrue(stream.isClosed(), "Handle " + handle.getClass().getName() + " did not close underlying InputStream");

  }

}
