/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import com.marklogic.client.io.marker.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.marklogic.client.MarkLogicIOException;

/**
 * <p>An InputStreamHandle represents a resource as an InputStream for reading or writing.</p>
 *
 * <p>When writing JSON, text, or XML content, you should use an InputStream only
 * if the stream is encoded in UTF-8.  If the characters have a different encoding, use
 * a {@link ReaderHandle} and specify the correct character encoding for the stream when
 * creating the Reader.</p>
 *
 * <p>Either call {@link #close} or {@link #get}.close() when finished with this handle
 * to release the resources.</p>
 */
public class InputStreamHandle
  extends BaseHandle<InputStream, InputStream>
  implements StreamingContentHandle<InputStream, InputStream>,
    BinaryReadHandle, BinaryWriteHandle,
    GenericReadHandle, GenericWriteHandle,
    JSONReadHandle, JSONWriteHandle,
    TextReadHandle, TextWriteHandle,
    XMLReadHandle, XMLWriteHandle,
    StructureReadHandle, StructureWriteHandle, CtsQueryWriteHandle,
    TriplesReadHandle, TriplesWriteHandle,
    QuadsWriteHandle, SPARQLResultsReadHandle,
    Closeable
{
  static final private Logger logger = LoggerFactory.getLogger(InputStreamHandle.class);

  private byte[] contentBytes;
  private InputStream content;

  final static private int BUFFER_SIZE = 8192;

  /**
   * Creates a factory to create an InputStreamHandle instance for an input stream.
   * @return  the factory
   */
  static public ContentHandleFactory newFactory() {
    return new ContentHandleFactory() {
      @Override
      public Class<?>[] getHandledClasses() {
        return new Class<?>[]{ InputStream.class };
      }
      @Override
      public boolean isHandled(Class<?> type) {
        return InputStream.class.isAssignableFrom(type);
      }
      @Override
      public <C> ContentHandle<C> newHandle(Class<C> type) {
        @SuppressWarnings("unchecked")
        ContentHandle<C> handle = isHandled(type) ?
                                  (ContentHandle<C>) new InputStreamHandle() : null;
        return handle;
      }
    };
  }

  /**
   * Zero-argument constructor.
   */
  public InputStreamHandle() {
    super();
    setResendable(false);
  }
  /**
   * Initializes the handle with an input stream for the content.
   * @param content  an input stream
   */
  public InputStreamHandle(InputStream content) {
    this();
    set(content);
  }

  /**
   * Returns an input stream for a resource read from the database.
   *
   * When finished with the input stream, close the input stream to release
   * the response.
   *
   * @return  the input stream
   */
  @Override
  public InputStream get() {
    if (contentBytes != null) {
      return new ByteArrayInputStream(contentBytes);
    }
    return content;
  }
  /**
   * Assigns an input stream as the content.
   * @param content  an input stream
   */
  @Override
  public void set(InputStream content) {
    set(content, null);
  }
  private void set(InputStream content, byte[] contentBytes) {
    this.content      = content;
    this.contentBytes = contentBytes;
  }
  /**
   * Assigns an input stream as the content and returns the handle
   * as a fluent convenience.
   * @param content  an input stream
   * @return  this handle
   */
  public InputStreamHandle with(InputStream content) {
    set(content);
    return this;
  }

  @Override
  public Class<InputStream> getContentClass() {
    return InputStream.class;
  }
  @Override
  public InputStreamHandle newHandle() {
    return new InputStreamHandle().withFormat(getFormat()).withMimetype(getMimetype());
  }
  @Override
  public InputStreamHandle[] newHandleArray(int length) {
    if (length < 0) throw new IllegalArgumentException("array length less than zero: "+length);
    return new InputStreamHandle[length];
  }
  @Override
  public InputStream[] newArray(int length) {
    if (length < 0) throw new IllegalArgumentException("array length less than zero: "+length);
    return new InputStream[length];
  }

  /**
   * Specifies the format of the content and returns the handle
   * as a fluent convenience.
   * @param format  the format of the content
   * @return  this handle
   */
  public InputStreamHandle withFormat(Format format) {
    setFormat(format);
    return this;
  }
  /**
   * Specifies the mime type of the content and returns the handle
   * as a fluent convenience.
   * @param mimetype  the mime type of the content
   * @return  this handle
   */
  public InputStreamHandle withMimetype(String mimetype) {
    setMimetype(mimetype);
    return this;
  }

  @Override
  public void fromBuffer(byte[] buffer) {
    set(bytesToContent(buffer), buffer);
  }
  @Override
  public byte[] toBuffer() {
    if (contentBytes == null && content != null) {
      contentBytes = contentToBytes(get());
      content = null;
    }
    return contentBytes;
  }
  @Override
  public InputStream bytesToContent(byte[] buffer) {
    return (buffer == null || buffer.length == 0) ?
            null : new ByteArrayInputStream(buffer);
  }
  @Override
  public byte[] contentToBytes(InputStream content) {
    try {
      if (content == null) return null;

      ByteArrayOutputStream buffer = new ByteArrayOutputStream();

      byte[] b = new byte[BUFFER_SIZE];
      int len = 0;
      while ((len = content.read(b)) != -1) {
        buffer.write(b, 0, len);
      }
      content.close();

      return buffer.toByteArray();
    } catch (IOException e) {
      throw new MarkLogicIOException(e);
    }
  }
  @Override
  public InputStream toContent(InputStream serialization) {
    return serialization;
  }

  /**
   * Buffers the input stream and returns the buffer as a string
   * with the assumption that the stream is encoded in UTF-8. If
   * the stream has a different encoding, use InputStreamReader
   * instead of calling this method.
   */
  @Override
  public String toString() {
    byte[] buffer = toBuffer();
    return (buffer == null) ? null : new String(buffer, StandardCharsets.UTF_8);
  }

  @Override
  protected Class<InputStream> receiveAs() {
    return InputStream.class;
  }
  @Override
  protected void receiveContent(InputStream content) {
    set(content, null);
  }
  @Override
  protected InputStream sendContent() {
    InputStream sendableContent = get();
    if (sendableContent == null) {
      throw new IllegalStateException("No stream to write");
    }

    return sendableContent;
  }

  /** Either call close() or get().close() when finished with this handle to close the underlying InputStream.
   */
  @Override
  public void close() {
    if (contentBytes != null) {
      contentBytes = null;
    }
    if (content != null) {
      try {
        content.close();
      } catch (IOException e) {
        logger.error("Failed to close underlying InputStream", e);
        throw new MarkLogicIOException(e);
      } finally {
        content = null;
      }
    }
  }
}
