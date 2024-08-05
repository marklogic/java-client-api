/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.io;

import java.io.*;
import java.nio.charset.StandardCharsets;

import com.marklogic.client.impl.NodeConverter;
import com.marklogic.client.io.marker.*;

/**
 * A String Handle represents document content as a string for reading or writing.
 */
public class StringHandle
  extends BaseHandle<byte[], OutputStreamSender>
  implements ResendableContentHandle<String, byte[]>, OutputStreamSender,
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
  @Override
  public StringHandle newHandle() {
    return new StringHandle().withFormat(getFormat()).withMimetype(getMimetype());
  }
  @Override
  public StringHandle[] newHandleArray(int length) {
    if (length < 0) throw new IllegalArgumentException("array length less than zero: "+length);
    return new StringHandle[length];
  }
  @Override
  public String[] newArray(int length) {
    if (length < 0) throw new IllegalArgumentException("array length less than zero: "+length);
    return new String[length];
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
    set(bytesToContent(buffer));
  }
  @Override
  public byte[] toBuffer() {
    return contentToBytes(get());
  }
  @Override
  public String bytesToContent(byte[] buffer) {
    return (buffer == null || buffer.length == 0) ?
            null : new String(buffer, StandardCharsets.UTF_8);
  }
  @Override
  public byte[] contentToBytes(String content) {
    if (content == null) return null;
    return content.getBytes(StandardCharsets.UTF_8);
  }

  /**
   * Returns the content.
   */
  @Override
  public String toString() {
    return content;
  }

  @Override
  public String toContent(byte[] serialization) {
    if (serialization == null) return null;

    return new String(serialization, StandardCharsets.UTF_8);
  }

  @Override
  protected Class<byte[]> receiveAs() {
    return byte[].class;
  }
  @Override
  protected void receiveContent(byte[] content) {
    set(toContent(content));
  }
  @Override
  protected OutputStreamSender sendContent() {
    return new OutputStreamSenderImpl(get());
  }

  @Override
  public void write(OutputStream out) throws IOException {
    sendContent().write(out);
  }

  static private class OutputStreamSenderImpl implements OutputStreamSender {
    private final String content;
    private OutputStreamSenderImpl(String content) {
      if (content == null) {
        throw new IllegalStateException("No string to send");
      }

      this.content = content;
    }
    @Override
    public void write(OutputStream out) throws IOException {
      BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8));
      writer.write(this.content);
      writer.flush();
    }
  }
}
