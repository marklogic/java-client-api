/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.io;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import com.marklogic.client.impl.NodeConverter;
import com.marklogic.client.io.marker.*;

/**
 * A Bytes Handle represents document content as a byte array for reading or writing.
 *
 * When writing JSON, text, or XML content, you should use a byte[] array only
 * if the bytes are encoded in UTF-8.  If the characters have a different encoding, use
 * a StringHandle and specify the correct character encoding for the bytes when
 * creating the String.
 */
public class BytesHandle
  extends BaseHandle<byte[], byte[]>
  implements ResendableContentHandle<byte[], byte[]>,
    BinaryReadHandle, BinaryWriteHandle,
    GenericReadHandle, GenericWriteHandle,
    JSONReadHandle, JSONWriteHandle,
    TextReadHandle, TextWriteHandle,
    XMLReadHandle, XMLWriteHandle,
    StructureReadHandle, StructureWriteHandle, CtsQueryWriteHandle,
    QuadsWriteHandle,
    TriplesReadHandle, TriplesWriteHandle
{
  private byte[] content;

  /**
   * Creates a factory to create a BytesHandle instance for a byte[] array.
   * @return	the factory
   */
  static public ContentHandleFactory newFactory() {
    return new ContentHandleFactory() {
      @Override
      public Class<?>[] getHandledClasses() {
        return new Class<?>[]{ byte[].class };
      }
      @Override
      public boolean isHandled(Class<?> type) {
        return byte[].class.isAssignableFrom(type);
      }
      @Override
      public <C> ContentHandle<C> newHandle(Class<C> type) {
        @SuppressWarnings("unchecked")
        ContentHandle<C> handle = isHandled(type) ?
                                  (ContentHandle<C>) new BytesHandle() : null;
        return handle;
      }
    };
  }

  /**
   * Zero-argument constructor.
   */
  public BytesHandle() {
    super();
    setResendable(true);
  }
  /**
   * Initializes the handle with a byte array for the content.
   * @param content	the byte array
   */
  public BytesHandle(byte[] content) {
    this();
    set(content);
  }
  /**
   * Initializes the handle by reading a byte array from an input stream.
   * @param content	the input stream with the content
   */
  public BytesHandle(InputStream content) {
    this();
    from(content);
  }
  /**
   * Initializes the handle from the byte content of another handle
   * @param content	the other handle
   */
  public BytesHandle(BufferableHandle content) {
    this((content == null) ? null : content.toBuffer());
    if(content != null) {
			if (!(content instanceof BaseHandle<?, ?>))
				throw new IllegalArgumentException("Bufferable Handle not instance of BaseHandle.");
			BaseHandle<?, ?> baseHandle = (BaseHandle<?, ?>) content;
			setFormat(baseHandle.getFormat());
			setMimetype(baseHandle.getMimetype());
    }
  }

  /**
   * Returns the byte array for the handle content.
   * @return	the byte array
   */
  @Override
  public byte[] get() {
    return content;
  }
  /**
   * Assigns a byte array as the content.
   * @param content	the byte array
   */
  @Override
  public void set(byte[] content) {
    this.content = content;
  }
  /**
   * Assigns a byte array by reading all bytes from an input stream
   * and returns the handle as a fluent convenience.
   * @param content	the input stream with the content
   * @return	this handle
   */
  public BytesHandle from(InputStream content) {
    set(NodeConverter.InputStreamToBytes(content));
    return this;
  }
  /**
   * Assigns a byte array as the content and returns the handle
   * as a fluent convenience.
   * @param content	the byte array
   * @return	this handle
   */
  public BytesHandle with(byte[] content) {
    set(content);
    return this;
  }

  @Override
  public Class<byte[]> getContentClass() {
    return byte[].class;
  }
  @Override
  public BytesHandle newHandle() {
    return new BytesHandle().withFormat(getFormat()).withMimetype(getMimetype());
  }
  @Override
  public BytesHandle[] newHandleArray(int length) {
    if (length < 0) throw new IllegalArgumentException("array length less than zero: "+length);
    return new BytesHandle[length];
  }
  @Override
  public byte[][] newArray(int length) {
    if (length < 0) throw new IllegalArgumentException("array length less than zero: "+length);
    return new byte[length][];
  }

  /**
   * Specifies the format of the content and returns the handle
   * as a fluent convenience.
   * @param format	the format of the content
   * @return	this handle
   */
  public BytesHandle withFormat(Format format) {
    setFormat(format);
    return this;
  }
  /**
   * Specifies the mime type of the content and returns the handle
   * as a fluent convenience.
   * @param mimetype	the mime type of the content
   * @return	this handle
   */
  public BytesHandle withMimetype(String mimetype) {
    setMimetype(mimetype);
    return this;
  }

  @Override
  public void fromBuffer(byte[] buffer) {
    content = buffer;
  }
  @Override
  public byte[] toBuffer() {
    return content;
  }
  @Override
  public byte[] toContent(byte[] serialization) {
    return serialization;
  }
  @Override
  public byte[] bytesToContent(byte[] buffer) {
    return buffer;
  }
  @Override
  public byte[] contentToBytes(byte[] content) {
    return content;
  }

  /**
   * Returns a byte array as a string with the assumption
   * that the bytes are encoded in UTF-8. If the bytes
   * have a different encoding, instantiate a String
   * directly instead of calling this method.
   */
  @Override
  public String toString() {
    return (content == null) ? null : new String(content, StandardCharsets.UTF_8);
  }

  @Override
  protected Class<byte[]> receiveAs() {
    return byte[].class;
  }
  @Override
  protected void receiveContent(byte[] content) {
    if (content == null || content.length == 0) {
      throw new IllegalStateException("No bytes to write");
    }

    set(content);
  }

  @Override
  protected byte[] sendContent() {
    return get();
  }
}
