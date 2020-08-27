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
package com.marklogic.client.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import com.marklogic.client.MarkLogicIOException;
import com.marklogic.client.impl.NodeConverter;
import com.marklogic.client.io.marker.*;

/**
 * A String Handle represents document content as a string for reading or writing.
 */
public class StringHandle
  extends BaseHandle<byte[], OutputStreamSender>
  implements OutputStreamSender, BufferableHandle, ResendableHandle<String>,
    JSONReadHandle, JSONWriteHandle,
    TextReadHandle, TextWriteHandle,
    XMLReadHandle, XMLWriteHandle,
    StructureReadHandle, StructureWriteHandle, CtsQueryWriteHandle,
    QuadsWriteHandle,
    TriplesReadHandle, TriplesWriteHandle,
    SPARQLResultsReadHandle
{
  private String content;

  /**
   * Creates a factory to create a StringHandle instance for a string.
   * @return	the factory
   */
  static public ContentHandleFactory newFactory() {
    return new ContentHandleFactory() {
      @Override
      public Class<?>[] getHandledClasses() {
        return new Class<?>[]{ String.class };
      }
      @Override
      public boolean isHandled(Class<?> type) {
        return String.class.isAssignableFrom(type);
      }
      @Override
      public <C> ContentHandle<C> newHandle(Class<C> type) {
        @SuppressWarnings("unchecked")
        ContentHandle<C> handle = isHandled(type) ?
                                  (ContentHandle<C>) new StringHandle() : null;
        return handle;
      }
    };
  }

  /**
   * Zero-argument constructor.
   */
  public StringHandle() {
    super();
    setResendable(true);
  }
  /**
   * Initializes the handle with a string for the content.
   * @param content	a content string
   */
  public StringHandle(String content) {
    this();
    set(content);
  }
  /**
   * Initializes the handle by constructing a string from
   * the content of a reader.
   * @param content	the reader with the content
   */
  public StringHandle(Reader content) {
    this();
    from(content);
  }

  /**
   * Returns the string for the content.
   * @return	the string
   */
  @Override
  public String get() {
    return content;
  }
  /**
   * Assigns an string as the content.
   * @param content	a string
   */
  @Override
  public void set(String content) {
    this.content = content;
  }
  /**
   * Assigns a string as the content and returns the handle
   * as a fluent convenience.
   * @param content	a string
   * @return	this handle
   */
  public StringHandle with(String content) {
    set(content);
    return this;
  }
  /**
   * Assigns a string constructed from the content of a reader
   * and returns the handle as a fluent convenience.
   * @param content	the reader with the content
   * @return	this handle
   */
  public StringHandle from(Reader content) {
    set(NodeConverter.ReaderToString(content));
    return this;
  }
  @Override
  public Class<String> getContentClass() {
    return String.class;
  }

  /**
   * Specifies the format of the content and returns the handle
   * as a fluent convenience.
   * @param format	the format of the content
   * @return	this handle
   */
  public StringHandle withFormat(Format format) {
    setFormat(format);
    return this;
  }
  /**
   * Specifies the mime type of the content and returns the handle
   * as a fluent convenience.
   * @param mimetype	the mime type of the content
   * @return	this handle
   */
  public StringHandle withMimetype(String mimetype) {
    setMimetype(mimetype);
    return this;
  }

  @Override
  public void fromBuffer(byte[] buffer) {
    if (buffer == null || buffer.length == 0)
      content = null;
    else
      content = new String(buffer, Charset.forName("UTF-8"));
  }
  @Override
  public byte[] toBuffer() {
    if (content == null)
      return null;

    return content.getBytes(Charset.forName("UTF-8"));
  }
  /**
   * Returns the content.
   */
  @Override
  public String toString() {
    return content;
  }

  @Override
  protected Class<byte[]> receiveAs() {
    return byte[].class;
  }
  @Override
  protected void receiveContent(byte[] content) {
    try {
      if (content == null) {
        this.content = null;
        return;
      }

      this.content = new String(content, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      throw new MarkLogicIOException(e);
    }
  }
  @Override
  protected OutputStreamSender sendContent() {
    if (content == null) {
      throw new IllegalStateException("No string to write");
    }

    return this;
  }
  @Override
  public void write(OutputStream out) throws IOException {
    out.write(toBuffer());
  }
}
