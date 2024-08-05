/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.io;

import java.io.*;

import com.marklogic.client.io.marker.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.client.MarkLogicIOException;
import com.marklogic.client.impl.JacksonBaseHandle;

/**
 * <p>An adapter for using the streaming capabilities of the Jackson Open Source library.
 * Enables low-level efficient reading and writing of JSON documents.</p>
 *
 * <p>Always call {@link #close} when finished with this handle to release the resources.</p>
 *
 * @see <a href="https://github.com/FasterXML/jackson">Jackson Streaming API</a>
 */
public class JacksonParserHandle
  extends JacksonBaseHandle<JsonParser>
  implements OutputStreamSender, StreamingContentHandle<JsonParser, InputStream>,
    JSONReadHandle, JSONWriteHandle,
    StructureReadHandle, StructureWriteHandle, CtsQueryWriteHandle,
    Closeable
{
  static final private Logger logger = LoggerFactory.getLogger(JacksonParserHandle.class);

  private JsonParser parser;
  private InputStream content;
  private boolean closed = false;

  final static private int BUFFER_SIZE = 8192;

  /**
   * Creates a factory to create a JacksonParserHandle instance for a JsonParser.
   * @return	the factory
   */
  static public ContentHandleFactory newFactory() {
    return new ContentHandleFactory() {
      @Override
      public Class<?>[] getHandledClasses() {
        return new Class<?>[]{ JsonParser.class };
      }
      @Override
      public boolean isHandled(Class<?> type) {
        return JsonParser.class.isAssignableFrom(type);
      }
      @Override
      public <C> ContentHandle<C> newHandle(Class<C> type) {
        @SuppressWarnings("unchecked")
        ContentHandle<C> handle = isHandled(type) ?
                                  (ContentHandle<C>) new JacksonParserHandle() : null;
        return handle;
      }
    };
  }

  public JacksonParserHandle() {
    super();
    setResendable(false);
  }

  /**
   * Specifies the format of the content and returns the handle
   * as a fluent convenience.
   * @param format	the format of the content
   * @return	this handle
   */
  public JacksonParserHandle withFormat(Format format) {
    setFormat(format);
    return this;
  }
  /**
   * Specifies the mime type of the content and returns the handle
   * as a fluent convenience.
   * @param mimetype	the mime type of the content
   * @return	this handle
   */
  public JacksonParserHandle withMimetype(String mimetype) {
    setMimetype(mimetype);
    return this;
  }

  /**
   * JsonParser allows streaming access to content as it arrives.
   * @return the JsonParser over the content (usually received from the server)
   */
  @Override
  public JsonParser get() {
    if ( parser == null ) {
      if ( content == null ) {
        throw new IllegalStateException("Handle is not yet populated with content");
      }
      parser = toContent(content);
    }
    return parser;
  }
  /**
   * Available for the edge case that content from a JsonParser must be written.
   * @param parser the JsonParser over the content to be written
   */
  @Override
  public void set(JsonParser parser) {
    this.parser = parser;
    if ( parser == null ) {
      content = null;
    } else if ( parser.getInputSource() instanceof InputStream ) {
      content = (InputStream) parser.getInputSource();
    }
  }
  /**
   * Assigns the JsonParser and returns the handle.
   * @param parser the JsonParser over the content to be written
   * @return this handle
   */
  public JacksonParserHandle with(JsonParser parser) {
    set(parser);
    return this;
  }

  @Override
  public Class<JsonParser> getContentClass() {
    return JsonParser.class;
  }
  @Override
  public JacksonParserHandle newHandle() {
    return new JacksonParserHandle().withMimetype(getMimetype());
  }
  @Override
  public JacksonParserHandle[] newHandleArray(int length) {
    if (length < 0) throw new IllegalArgumentException("array length less than zero: "+length);
    return new JacksonParserHandle[length];
  }
  @Override
  public JsonParser[] newArray(int length) {
    if (length < 0) throw new IllegalArgumentException("array length less than zero: "+length);
    return new JsonParser[length];
  }

  /**
   * Provides access to the ObjectMapper used internally so you can configure
   * it to fit your JSON.
   * @return the ObjectMapper instance
   */
  @Override
  public ObjectMapper getMapper() { return super.getMapper(); }
  /**
   * Enables clients to specify their own ObjectMapper instance, including databinding mappers
   * for formats other than JSON.
   * For <a href="https://github.com/FasterXML/jackson-dataformat-csv">example</a>:<pre>{@code
   *ObjectMapper mapper = new CsvMapper();
   *mapper.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
   *mapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, false);
   *handle.setMapper(mapper);
   * }</pre>
   *
   * Use at your own risk!  Note that you most likely want to set to false the two options we
   * demonstrate above (JsonGenerator.Feature.AUTO_CLOSE_TARGET and JsonParser.Feature.AUTO_CLOSE_SOURCE)
   * as we do so your mapper will not close streams which we may need to reuse if we have to
   * resend a network request.
   **/
  @Override
  public void setMapper(ObjectMapper mapper) { super.setMapper(mapper); }

  @Override
  protected OutputStreamSender sendContent() {
    return this;
  }
  @Override
  protected OutputStreamSender sendContent(JsonParser parser) {
    try {
      if (parser == null || parser.nextToken() == null) return null;
      return new OutputStreamSenderImpl(getMapper(), parser);
    } catch (IOException e) {
      throw new MarkLogicIOException("Failed to parse content", e);
    }
  }

  @Override
  protected void receiveContent(InputStream content) {
    this.content = content;
    if (content == null) parser = null;
  }
  @Override
  protected boolean hasContent() {
    return content != null || parser != null;
  }
  @Override
  public void write(OutputStream out) throws IOException {
    try {
      if (parser != null && parser.nextToken() != null) {
        JsonGenerator generator = getMapper().getFactory().createGenerator(out);
        generator.copyCurrentStructure(parser);
        generator.close();
      } else if (content != null) {
        byte[] b = new byte[BUFFER_SIZE];
        int len = 0;
        while ((len = content.read(b)) != -1) {
          out.write(b, 0, len);
        }
        content.close();
        closed = true;
      }
    } catch (IOException e) {
      throw new MarkLogicIOException(e);
    }
  }
  @Override
  public JsonParser toContent(InputStream serialization) {
    if (serialization == null) return null;
    try {
      return getMapper().getFactory().createParser(serialization);
    } catch (JsonParseException e) {
      throw new MarkLogicIOException(e);
    } catch (IOException e) {
      throw new MarkLogicIOException(e);
    }
  }

  /** Always call close() when finished with this handle -- it closes the underlying InputStream.
   */
  @Override
  public void close() {
    if (content != null && !closed) {
      try {
        content.close();
      } catch (IOException e) {
        logger.error("Failed to close underlying InputStream",e);
        throw new MarkLogicIOException(e);
      }
    }
  }

  private static class OutputStreamSenderImpl implements OutputStreamSender {
    private final ObjectMapper mapper;
    private final JsonParser parser;
    private OutputStreamSenderImpl(ObjectMapper mapper, JsonParser parser) {
      this.mapper = mapper;
      this.parser = parser;
    }
    @Override
    public void write(OutputStream out) throws IOException {
      JsonGenerator generator = mapper.getFactory().createGenerator(out);
      generator.copyCurrentStructure(parser);
      generator.close();
    }
  }
}
