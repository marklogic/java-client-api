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
package com.marklogic.client.extra.xom;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.ParsingException;
import nu.xom.Serializer;
import nu.xom.ValidityException;

import com.marklogic.client.MarkLogicIOException;
import com.marklogic.client.io.BaseHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.OutputStreamSender;
import com.marklogic.client.io.marker.BufferableHandle;
import com.marklogic.client.io.marker.ContentHandle;
import com.marklogic.client.io.marker.ContentHandleFactory;
import com.marklogic.client.io.marker.StructureReadHandle;
import com.marklogic.client.io.marker.StructureWriteHandle;
import com.marklogic.client.io.marker.XMLReadHandle;
import com.marklogic.client.io.marker.XMLWriteHandle;

/**
 * A XOM Handle represents XML content as a XOM document for reading or writing.
 * You must install the XOM library to use this class.
 * @deprecated
 * Changed to Gradle.
 */
@Deprecated
public class XOMHandle
  extends BaseHandle<InputStream, OutputStreamSender>
  implements OutputStreamSender, BufferableHandle, ContentHandle<Document>,
    XMLReadHandle, XMLWriteHandle,
    StructureReadHandle, StructureWriteHandle
{
  private Document content;
  private Builder  builder;

  /**
   * Creates a factory to create a XOMHandle instance for a XOM document.
   * @return	the factory
   */
  static public ContentHandleFactory newFactory() {
    return new ContentHandleFactory() {
      @Override
      public Class<?>[] getHandledClasses() {
        return new Class<?>[]{ Document.class };
      }
      @Override
      public boolean isHandled(Class<?> type) {
        return Document.class.isAssignableFrom(type);
      }
      @Override
      public <C> ContentHandle<C> newHandle(Class<C> type) {
        @SuppressWarnings("unchecked")
        ContentHandle<C> handle = isHandled(type) ?
          (ContentHandle<C>) new XOMHandle() : null;
        return handle;
      }
    };
  }

  /**
   * Zero-argument constructor.
   */
  public XOMHandle() {
    super();
    super.setFormat(Format.XML);
    setResendable(true);
  }
  /**
   * Provides a handle on XML content as a XOM document structure.
   * @param content	the XML document.
   */
  public XOMHandle(Document content) {
    this();
    set(content);
  }

  /**
   * Returns the XOM structure builder for XML content.
   * @return	the XOM builder.
   */
  public Builder getBuilder() {
    if (builder == null)
      builder = makeBuilder();
    return builder;
  }
  /**
   * Specifies a XOM structure builder for XML content.
   * @param builder	the XOM builder.
   */
  public void setBuilder(Builder builder) {
    this.builder = builder;
  }
  protected Builder makeBuilder() {
    return new Builder(false);
  }

  /**
   * Returns the XML document structure.
   * @return	the XML document.
   */
  @Override
  public Document get() {
    return content;
  }
  /**
   * Assigns an XML document structure as the content.
   * @param content	the XML document.
   */
  @Override
  public void set(Document content) {
    this.content = content;
  }
  /**
   * Assigns an XML document structure as the content and returns the handle.
   * @param content	the XML document.
   * @return	the handle on the XML document.
   */
  public XOMHandle with(Document content) {
    set(content);
    return this;
  }

  /**
   * Restricts the format to XML.
   */
  @Override
  public void setFormat(Format format) {
    if (format != Format.XML)
      throw new IllegalArgumentException("XOMHandle supports the XML format only");
  }

  @Override
  public void fromBuffer(byte[] buffer) {
    if (buffer == null || buffer.length == 0)
      content = null;
    else
      receiveContent(new ByteArrayInputStream(buffer));
  }
  @Override
  public byte[] toBuffer() {
    try {
      if (content == null)
        return null;

      ByteArrayOutputStream buffer = new ByteArrayOutputStream();
      write(buffer);

      return buffer.toByteArray();
    } catch (IOException e) {
      throw new MarkLogicIOException(e);
    }
  }

  /**
   * Returns the XML document as a string.
   */
  @Override
  public String toString() {
    try {
      return new String(toBuffer(),"UTF-8");
    } catch (UnsupportedEncodingException e) {
      throw new MarkLogicIOException(e);
    }
  }

  @Override
  protected Class<InputStream> receiveAs() {
    return InputStream.class;
  }
  @Override
  protected void receiveContent(InputStream content) {
    if (content == null)
      return;

    try {
      this.content = getBuilder().build(
        new InputStreamReader(content, "UTF-8")
      );
    } catch (ValidityException e) {
      throw new MarkLogicIOException(e);
    } catch (ParsingException e) {
      throw new MarkLogicIOException(e);
    } catch (IOException e) {
      throw new MarkLogicIOException(e);
    } finally {
      try {
        content.close();
      } catch (IOException e) {
        // ignore.
      }
    }
  }

  @Override
  protected OutputStreamSender sendContent() {
    if (content == null) {
      throw new IllegalStateException("No document to write");
    }

    return this;
  }
  @Override
  public void write(OutputStream out) throws IOException {
    makeSerializer(out).write(content);
  }
  protected Serializer makeSerializer(OutputStream out) throws UnsupportedEncodingException {
    return new Serializer(out, "UTF-8");
  }
}
