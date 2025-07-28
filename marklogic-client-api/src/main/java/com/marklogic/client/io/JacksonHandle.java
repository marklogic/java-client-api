/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.io;

import java.io.*;
import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.client.MarkLogicIOException;
import com.marklogic.client.io.marker.*;
import com.marklogic.client.impl.JacksonBaseHandle;

/**
 * An adapter for using the Jackson Open Source library for JSON; represents
 * JSON content as a Jackson JsonNode for reading or writing.  Enables reading and
 * writing JSON documents, JSON structured search, and other JSON input and output.
 */
public class JacksonHandle
  extends JacksonBaseHandle<JsonNode>
  implements ResendableContentHandle<JsonNode, InputStream>, OutputStreamSender,
    JSONReadHandle, JSONWriteHandle,
    StructureReadHandle, StructureWriteHandle, CtsQueryWriteHandle,
    SPARQLResultsReadHandle
{
  private JsonNode content;

  /**
   * Creates a factory to create a JacksonHandle instance for a JSON node.
   * @return	the factory
   */
  static public ContentHandleFactory newFactory() {
    return new ContentHandleFactory() {
      @Override
      public Class<?>[] getHandledClasses() {
        return new Class<?>[]{ JsonNode.class };
      }
      @Override
      public boolean isHandled(Class<?> type) {
        return JsonNode.class.isAssignableFrom(type);
      }
      @Override
      public <C> ContentHandle<C> newHandle(Class<C> type) {
        @SuppressWarnings("unchecked")
        ContentHandle<C> handle = isHandled(type) ?
                                  (ContentHandle<C>) new JacksonHandle() : null;
        return handle;
      }
    };
  }

  /**
   * Zero-argument constructor.
   */
  public JacksonHandle() {
    super();
    setResendable(true);
  }
  /**
   * Provides a handle on JSON content as a Jackson tree.
   * @param content	the JSON root node of the tree.
   */
  public JacksonHandle(JsonNode content) {
    this();
    set(content);
  }

  /**
   * Specifies the format of the content and returns the handle
   * as a fluent convenience.
   * @param format	the format of the content
   * @return	this handle
   */
  public JacksonHandle withFormat(Format format) {
    setFormat(format);
    return this;
  }
  /**
   * Specifies the mime type of the content and returns the handle
   * as a fluent convenience.
   * @param mimetype	the mime type of the content
   * @return	this handle
   */
  public JacksonHandle withMimetype(String mimetype) {
    setMimetype(mimetype);
    return this;
  }

  /**
   * Returns the root node of the JSON tree.
   * @return	the JSON root node.
   */
  @Override
  public JsonNode get() {
    return content;
  }
  /**
   * Assigns a JSON tree as the content.
   * @param content	the JSON root node.
   */
  @Override
  public void set(JsonNode content) {
    this.content = content;
  }
  /**
   * Assigns a JSON tree as the content and returns the handle.
   * @param content	the JSON root node.
   * @return	the handle on the JSON tree.
   */
  public JacksonHandle with(JsonNode content) {
    set(content);
    return this;
  }

  @Override
  public Class<JsonNode> getContentClass() {
    return JsonNode.class;
  }
  @Override
  public JacksonHandle newHandle() {
    return new JacksonHandle().withMimetype(getMimetype());
  }
  @Override
  public JacksonHandle[] newHandleArray(int length) {
    if (length < 0) throw new IllegalArgumentException("array length less than zero: "+length);
    return new JacksonHandle[length];
  }
  @Override
  public JsonNode[] newArray(int length) {
    if (length < 0) throw new IllegalArgumentException("array length less than zero: "+length);
    return new JsonNode[length];
  }

  @Override
  public JsonNode toContent(InputStream serialization) {
    if (serialization == null) return null;

    try {
      return getMapper().readValue(
        new InputStreamReader(serialization, StandardCharsets.UTF_8), JsonNode.class
      );
    } catch (JsonParseException e) {
      throw new MarkLogicIOException(e);
    } catch (JsonMappingException e) {
      throw new MarkLogicIOException(e);
    } catch (IOException e) {
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
  protected OutputStreamSender sendContent(JsonNode content) {
    if (content == null) {
      throw new IllegalStateException("No document to write");
    }

    return new OutputStreamSenderImpl(getMapper(), content);
  }
  @Override
  protected OutputStreamSender sendContent() {
    return sendContent(get());
  }
  @Override
  protected void receiveContent(InputStream content) {
    set(toContent(content));
  }
  @Override
  protected boolean hasContent() {
    return content != null;
  }
  @Override
  public void write(OutputStream out) throws IOException {
    sendContent().write(out);
  }

  static private class OutputStreamSenderImpl implements OutputStreamSender {
    private final ObjectMapper mapper;
    private final JsonNode content;
    private OutputStreamSenderImpl(ObjectMapper mapper, JsonNode content) {
      this.mapper = mapper;
      this.content = content;
    }
    @Override
    public void write(OutputStream out) throws IOException {
      mapper.writeValue(new OutputStreamWriter(out, StandardCharsets.UTF_8), content);
    }
  }
}
