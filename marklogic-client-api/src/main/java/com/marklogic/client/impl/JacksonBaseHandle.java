/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.client.MarkLogicIOException;
import com.marklogic.client.io.BaseHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.OutputStreamSender;

public abstract class JacksonBaseHandle<T>
  extends BaseHandle<InputStream, OutputStreamSender>
  implements OutputStreamSender
{
  private ObjectMapper mapper;

  protected JacksonBaseHandle() {
    super();
    super.setFormat(Format.JSON);
  }

  public ObjectMapper getMapper() {
    if (mapper == null) {
      mapper = new ObjectMapper();
      // if we don't do the next two lines Jackson will automatically close our streams which is undesirable
      mapper.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
      mapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, false);
    }
    return mapper;
  }

  public void setMapper(ObjectMapper mapper) {
    this.mapper = mapper;
  }

  public abstract T get();
  public abstract void set(T content);
  public abstract T toContent(InputStream serialization);

  public void fromBuffer(byte[] buffer) {
    if (buffer == null || buffer.length == 0)
      set(null);
    else
      receiveContent(new ByteArrayInputStream(buffer));
  }
  public byte[] toBuffer() {
    byte[] b = contentToBytes(get());
    if (!isResendable())
      fromBuffer(b);
    return b;
  }
  public T bytesToContent(byte[] buffer) {
    return toContent(new ByteArrayInputStream(buffer));
  }

  public byte[] contentToBytes(T content) {
    try {
      if (!hasContent())
        return null;

      ByteArrayOutputStream buffer = new ByteArrayOutputStream();
      sendContent(content).write(buffer);

      return buffer.toByteArray();
    } catch (IOException e) {
      throw new MarkLogicIOException(e);
    }
  }

  /**
   * Returns the JSON as a string.
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

  protected abstract OutputStreamSender sendContent(T content);

  protected abstract boolean hasContent();
}

