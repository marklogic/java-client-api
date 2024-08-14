/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

import com.marklogic.client.io.marker.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.marklogic.client.MarkLogicIOException;
import com.marklogic.client.impl.Utilities;

/**
 * <p>A Reader Handle represents a character content as a reader
 * for reading to or writing from the database.</p>
 *
 * <p>Either call {@link #close} or {@link #get}.close() when finished with this handle
 * to release the resources.</p>
 */
public class ReaderHandle
  extends BaseHandle<InputStream, OutputStreamSender>
  implements OutputStreamSender, StreamingContentHandle<Reader, InputStream>,
    JSONReadHandle, JSONWriteHandle,
    TextReadHandle, TextWriteHandle,
    XMLReadHandle, XMLWriteHandle,
    StructureReadHandle, StructureWriteHandle, CtsQueryWriteHandle,
    QuadsWriteHandle,
    TriplesReadHandle, TriplesWriteHandle,
    Closeable
{
  static final private Logger logger = LoggerFactory.getLogger(InputStreamHandle.class);

  private Reader content;

  /**
   * Creates a factory to create a ReaderHandle instance for a Reader.
   * @return	the factory
   */
  static public ContentHandleFactory newFactory() {
    return new ContentHandleFactory() {
      @Override
      public Class<?>[] getHandledClasses() {
        return new Class<?>[]{ Reader.class };
      }
      @Override
      public boolean isHandled(Class<?> type) {
        return Reader.class.isAssignableFrom(type);
      }
      @Override
      public <C> ContentHandle<C> newHandle(Class<C> type) {
        @SuppressWarnings("unchecked")
        ContentHandle<C> handle = isHandled(type) ?
                                  (ContentHandle<C>) new ReaderHandle() : null;
        return handle;
      }
    };
  }

  /**
   * Zero-argument constructor.
   */
  public ReaderHandle() {
    super();
    setResendable(false);
  }
  /**
   * Initializes the handle with a character reader for the content.
   * @param content	a character reader
   */
  public ReaderHandle(Reader content) {
    this();
    set(content);
  }

  /**
   * Returns a character reader for reading content.
   *
   * When finished with the reader, close the reader to release
   * the resource.
   *
   * @return	the character reader
   */
  @Override
  public Reader get() {
    return content;
  }
  /**
   * Assigns an character reader as the content.
   * @param content	a reader
   */
  @Override
  public void set(Reader content) {
    this.content = content;
  }
  /**
   * Assigns a character reader as the content and returns the handle
   * as a fluent convenience.
   * @param content	a reader
   * @return	this handle
   */
  public ReaderHandle with(Reader content) {
    set(content);
    return this;
  }

  @Override
  public Class<Reader> getContentClass() {
    return Reader.class;
  }
  @Override
  public ReaderHandle newHandle() {
    return new ReaderHandle().withFormat(getFormat()).withMimetype(getMimetype());
  }
  @Override
  public ReaderHandle[] newHandleArray(int length) {
    if (length < 0) throw new IllegalArgumentException("array length less than zero: "+length);
    return new ReaderHandle[length];
  }
  @Override
  public Reader[] newArray(int length) {
    if (length < 0) throw new IllegalArgumentException("array length less than zero: "+length);
    return new Reader[length];
  }

  /**
   * Specifies the format of the content and returns the handle
   * as a fluent convenience.
   * @param format	the format of the content
   * @return	this handle
   */
  public ReaderHandle withFormat(Format format) {
    setFormat(format);
    return this;
  }
  /**
   * Specifies the mime type of the content and returns the handle
   * as a fluent convenience.
   * @param mimetype	the mime type of the content
   * @return	this handle
   */
  public ReaderHandle withMimetype(String mimetype) {
    setMimetype(mimetype);
    return this;
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

      byte[] b = buffer.toByteArray();
      fromBuffer(b);

      return b;
    } catch (IOException e) {
      throw new MarkLogicIOException(e);
    }
  }
  @Override
  public Reader toContent(InputStream serialization) {
    return (serialization == null) ? null :
            new InputStreamReader(serialization, StandardCharsets.UTF_8);
  }
  @Override
  public Reader bytesToContent(byte[] buffer) {
    return (buffer == null || buffer.length == 0) ? null :
            toContent(new ByteArrayInputStream(buffer));
  }
  @Override
  public byte[] contentToBytes(Reader content) {
    if (content == null) return null;
    try {
      ByteArrayOutputStream buffer = new ByteArrayOutputStream();
      Utilities.write(content, buffer);
      return buffer.toByteArray();
    } catch (IOException e) {
      throw new MarkLogicIOException("Could not convert Reader to byte[] array", e);
    }
  }

  /**
   * Buffers the character stream and returns the buffer as a string.
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
    // avoid NullPointerException by using an empty InputStream
    if ( content == null ) content = new ByteArrayInputStream(new byte[0]);
    this.content = new InputStreamReader(content, StandardCharsets.UTF_8);
  }
  @Override
  protected ReaderHandle sendContent() {
    if (content == null) {
      throw new IllegalStateException("No character stream to write");
    }

    return this;
  }

  @Override
  public void write(OutputStream out) throws IOException {
    if (content == null) {
      throw new IllegalStateException("No character stream to send as output");
    }

    Utilities.write(content, out);
  }

  /** Either call close() or get().close() when finished with this handle to close the underlying Reader.
   */
  @Override
  public void close() {
    if ( content != null ) {
      try {
        content.close();
      } catch (IOException e) {
        logger.error("Failed to close underlying InputStream",e);
        throw new MarkLogicIOException(e);
      }
    }
  }
}
