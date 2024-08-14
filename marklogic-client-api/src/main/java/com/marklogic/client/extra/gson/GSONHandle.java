/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.extra.gson;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.marklogic.client.MarkLogicIOException;
import com.marklogic.client.io.BaseHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.marker.ResendableContentHandle;
import com.marklogic.client.io.marker.*;

/**
 * A GSONHandle represents JSON content as a GSON JsonElement for reading or
 * writing.  You must install the GSON library to use this class.
 */
public class GSONHandle
  extends BaseHandle<InputStream, String>
  implements ResendableContentHandle<JsonElement, InputStream>,
    JSONReadHandle, JSONWriteHandle,
    StructureReadHandle, StructureWriteHandle
{
  private JsonElement content;
  private JsonParser  parser;

  /**
   * Creates a factory to create a GSONHandle instance for a JsonElement node.
   * @return	the factory
   */
  static public ContentHandleFactory newFactory() {
    return new ContentHandleFactory() {
      @Override
      public Class<?>[] getHandledClasses() {
        return new Class<?>[]{ JsonElement.class };
      }
      @Override
      public boolean isHandled(Class<?> type) {
        return JsonElement.class.isAssignableFrom(type);
      }
      @Override
      public <C> ContentHandle<C> newHandle(Class<C> type) {
        @SuppressWarnings("unchecked")
        ContentHandle<C> handle = isHandled(type) ?
          (ContentHandle<C>) new GSONHandle() : null;
        return handle;
      }
    };
  }

  /**
   * Zero-argument constructor.
   */
  public GSONHandle() {
    super();
    setResendable(true);
    super.setFormat(Format.JSON);
  }
  /**
   * Provides a handle on JSON content as a tree.
   * @param content	the JSON root element of the tree.
   */
  public GSONHandle(JsonElement content) {
    this();
    set(content);
  }

  @Override
  public GSONHandle newHandle() {
    return new GSONHandle();
  }
  @Override
  public GSONHandle[] newHandleArray(int length) {
    if (length < 0) throw new IllegalArgumentException("array length less than zero: "+length);
    return new GSONHandle[length];
  }

  /**
   * Returns the parser used to construct element objects from JSON.
   * @return	the JSON parser.
   */
  public JsonParser getParser() {
    if (parser == null)
      parser = new JsonParser();
    return parser;
  }

  /**
   * Returns the root node of the JSON tree.
   * @return	the JSON root element.
   */
  @Override
  public JsonElement get() {
    return content;
  }
  /**
   * Assigns a JSON tree as the content.
   * @param content	the JSON root element.
   */
  @Override
  public void set(JsonElement content) {
    this.content = content;
  }
  /**
   * Assigns a JSON tree as the content and returns the handle.
   * @param content	the JSON root element.
   * @return	the handle on the JSON tree.
   */
  public GSONHandle with(JsonElement content) {
    set(content);
    return this;
  }

  @Override
  public Class<JsonElement> getContentClass() {
    return JsonElement.class;
  }

  /**
   * Restricts the format to JSON.
   */
  @Override
  public void setFormat(Format format) {
    if (format != Format.JSON)
      throw new IllegalArgumentException(
        "GSONHandle supports the JSON format only");
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
  public JsonElement bytesToContent(byte[] buffer) {
    return (buffer == null || buffer.length == 0) ?
            null : toContent(new ByteArrayInputStream(buffer));
  }
  @Override
  public byte[] contentToBytes(JsonElement content) {
    return (content == null) ?
            null : content.toString().getBytes(StandardCharsets.UTF_8);
  }

  /**
   * Returns the JSON tree as a string.
   */
  @Override
  public String toString() {
    if (content == null) {
      return "";
    }
    return content.toString();
  }

  @Override
  public JsonElement toContent(InputStream serialization) {
    if (serialization == null) return null;

    try {
      return JsonParser.parseReader(
              new InputStreamReader(serialization, StandardCharsets.UTF_8)
      );
    } catch (JsonIOException e) {
      throw new MarkLogicIOException(e);
    } catch (JsonSyntaxException e) {
      throw new MarkLogicIOException(e);
    } finally {
      try {
        serialization.close();
      } catch (IOException e) {
        // ignore.
      }
    }
  }

  @Override
  protected Class<InputStream> receiveAs() {
    return InputStream.class;
  }
  @Override
  protected void receiveContent(InputStream serialization) {
    set(toContent(serialization));
  }
  @Override
  protected String sendContent() {
    return sendContent(get());
  }
  private String sendContent(JsonElement content) {
    if (content == null) {
      throw new IllegalStateException("No document to write");
    }
    return content.toString();
  }
}
