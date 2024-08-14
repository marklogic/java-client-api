/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.io;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import com.marklogic.client.impl.NodeConverter;
import com.marklogic.client.io.marker.*;

/**
 * A File Handle represents document content as a file for reading or writing.
 *
 * When you read a database document into a file handle, the API creates a temporary
 * file.  You can then open the file or move it with the File.renameTo() method.
 *
 * When writing JSON, text, or XML content, you should use a File only
 * if the file is encoded in UTF-8.  If the characters have a different encoding, use
 * a ReaderHandle and specify the correct character encoding for the file when
 * creating the Reader.
 */
public class FileHandle
  extends BaseHandle<File, File>
  implements ResendableContentHandle<File, File>,
    BinaryReadHandle, BinaryWriteHandle,
    GenericReadHandle, GenericWriteHandle,
    JSONReadHandle, JSONWriteHandle,
    TextReadHandle, TextWriteHandle,
    XMLReadHandle, XMLWriteHandle,
    StructureReadHandle, StructureWriteHandle, CtsQueryWriteHandle,
    QuadsWriteHandle,
    TriplesReadHandle, TriplesWriteHandle
{
  private File content;

  /**
   * Creates a factory to create a FileHandle for a file.
   * @return	the factory
   */
  static public ContentHandleFactory newFactory() {
    return new ContentHandleFactory() {
      @Override
      public Class<?>[] getHandledClasses() {
        return new Class<?>[]{ File.class };
      }
      @Override
      public boolean isHandled(Class<?> type) {
        return File.class.isAssignableFrom(type);
      }
      @Override
      public <C> ContentHandle<C> newHandle(Class<C> type) {
        @SuppressWarnings("unchecked")
        ContentHandle<C> handle = isHandled(type) ?
                                  (ContentHandle<C>) new FileHandle() : null;
        return handle;
      }
    };
  }

  /**
   * Zero-argument constructor.
   */
  public FileHandle() {
    super();
    setResendable(true);
  }
  /**
   * Initializes the handle with a file containing the content.
   * @param content	the file
   */
  public FileHandle(File content) {
    this();
    set(content);
  }

  /**
   * Returns the file for the handle content.
   * @return	the file
   */
  @Override
  public File get() {
    return content;
  }
  /**
   * Assigns a file as the content.
   * @param content	the file
   */
  @Override
  public void set(File content) {
    this.content = content;
  }
  /**
   * Assigns a file as the content and returns the handle
   * as a fluent convenience.
   * @param content	the file
   * @return	this handle
   */
  public FileHandle with(File content) {
    set(content);
    return this;
  }

  @Override
  public Class<File> getContentClass() {
    return File.class;
  }
  @Override
  public FileHandle newHandle() {
    return new FileHandle().withFormat(getFormat()).withMimetype(getMimetype());
  }
  @Override
  public FileHandle[] newHandleArray(int length) {
    if (length < 0) throw new IllegalArgumentException("array length less than zero: "+length);
    return new FileHandle[length];
  }
  @Override
  public File[] newArray(int length) {
    if (length < 0) throw new IllegalArgumentException("array length less than zero: "+length);
    return new File[length];
  }

  /**
   * Specifies the format of the content and returns the handle
   * as a fluent convenience.
   * @param format	the format of the content
   * @return	this handle
   */
  public FileHandle withFormat(Format format) {
    setFormat(format);
    return this;
  }
  /**
   * Specifies the mime type of the content and returns the handle
   * as a fluent convenience.
   * @param mimetype	the mime type of the content
   * @return	this handle
   */
  public FileHandle withMimetype(String mimetype) {
    setMimetype(mimetype);
    return this;
  }

  @Override
  public File toContent(File serialization) {
    return serialization;
  }

  @Override
  protected Class<File> receiveAs() {
    return File.class;
  }
  @Override
  protected void receiveContent(File content) {
    set(toContent(content));
  }
  @Override
  protected File sendContent() {
    if (get() == null) {
      throw new IllegalStateException("No file to write");
    }

    return get();
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
  public File bytesToContent(byte[] buffer) {
    if (buffer == null || buffer.length == 0) return null;
    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(buffer);
    return NodeConverter.InputStreamToFile(byteArrayInputStream);
  }
  @Override
  public byte[] contentToBytes(File content) {
    if (content == null) return null;
    try(InputStreamHandle inputStreamHandle = new InputStreamHandle(new FileInputStream(content))) {
      return inputStreamHandle.toBuffer();

    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    return null;
  }
}
