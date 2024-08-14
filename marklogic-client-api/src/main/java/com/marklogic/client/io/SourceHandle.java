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
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import javax.xml.XMLConstants;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import com.marklogic.client.io.marker.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.marklogic.client.MarkLogicIOException;

/**
 * <p>A Source Handle represents XML content as a transform source for reading
 * or transforms a source into a result for writing.</p>
 *
 * <p>Always call {@link #close} when finished with this handle to release the resources.</p>
 */
public class SourceHandle
  extends BaseHandle<InputStream, OutputStreamSender>
  implements OutputStreamSender, StreamingContentHandle<Source, InputStream>,
    XMLReadHandle, XMLWriteHandle,
    StructureReadHandle, StructureWriteHandle, CtsQueryWriteHandle,
    Closeable
{
  static final private Logger logger = LoggerFactory.getLogger(SourceHandle.class);

  private Transformer transformer;
  private Source      content;
  private InputStream underlyingStream;

  /**
   * Creates a factory to create a SourceHandle instance for a Transformer Source.
   * @return	the factory
   */
  static public ContentHandleFactory newFactory() {
    return new ContentHandleFactory() {
      @Override
      public Class<?>[] getHandledClasses() {
        return new Class<?>[]{ Source.class };
      }
      @Override
      public boolean isHandled(Class<?> type) {
        return Source.class.isAssignableFrom(type);
      }
      @Override
      public <C> ContentHandle<C> newHandle(Class<C> type) {
        @SuppressWarnings("unchecked")
        ContentHandle<C> handle = isHandled(type) ?
                                  (ContentHandle<C>) new SourceHandle() : null;
        return handle;
      }
    };
  }

  /**
   * Zero-argument constructor.
   */
  public SourceHandle() {
    super();
    super.setFormat(Format.XML);
    setResendable(false);
  }
  /**
   * Initializes the handle with a transform source for the content.
   * @param content	a transform source
   */
  public SourceHandle(Source content) {
    this();
    set(content);
  }

  /**
   * Returns a transformer for modifying the content.
   * @return	the transformer
   */
  public Transformer getTransformer() {
    return transformer;
  }
  /**
   * Specifies a transformer for modifying the content.
   * @param transformer	the transformer
   */
  public void setTransformer(Transformer transformer) {
    this.transformer = transformer;
  }
  /**
   * Specifies a transformer for modifying the content and returns the handle
   * as a fluent convenience.
   * @param transformer	the transformer
   * @return	this handle
   */
  public SourceHandle withTransformer(Transformer transformer) {
    setTransformer(transformer);
    return this;
  }

  /**
   * Returns the transform source that produces the content.
   * @return	the transform source
   */
  @Override
  public Source get() {
    return content;
  }
  /**
   * Assigns a transform source that produces the content.
   * @param content	the transform source
   */
  @Override
  public void set(Source content) {
    this.content = content;
  }
  /**
   * Assigns a transform source that produces the content and returns
   * the handle as a fluent convenience.
   * @param content	the transform source
   * @return	this handle
   */
  public SourceHandle with(Source content) {
    set(content);
    return this;
  }

  @Override
  public Class<Source> getContentClass() {
    return Source.class;
  }
  @Override
  public SourceHandle newHandle() {
    return new SourceHandle().withMimetype(getMimetype());
  }
  @Override
  public SourceHandle[] newHandleArray(int length) {
    if (length < 0) throw new IllegalArgumentException("array length less than zero: "+length);
    return new SourceHandle[length];
  }
  @Override
  public Source[] newArray(int length) {
    if (length < 0) throw new IllegalArgumentException("array length less than zero: "+length);
    return new Source[length];
  }

  /**
   * Transforms the source for the content output to the result.  If
   * the transformer is not specified, an identity transform sends
   * the source to the result.  When writing, the result is stored
   * in the database
   * @param result	the receiver of the transform output
   */
  public void transform(Result result) {
    if (logger.isInfoEnabled())
      logger.info("Transforming source into result");
    try {
      if (content == null) {
        throw new IllegalStateException("No source to transform");
      }

      Transformer transformer = null;
      if (this.transformer != null) {
        transformer = getTransformer();
      } else {
        if (logger.isWarnEnabled())
          logger.warn("No transformer, so using identity transform");
        transformer = makeTransformer().newTransformer();
      }

      transformer.transform(content, result);
    } catch (TransformerException e) {
      logger.error("Failed to transform source into result",e);
      throw new MarkLogicIOException(e);
    }
  }
  private TransformerFactory makeTransformer() {
    TransformerFactory factory = TransformerFactory.newInstance();
    // default to best practices for conservative security including recommendations per
    // https://github.com/OWASP/CheatSheetSeries/blob/master/cheatsheets/XML_External_Entity_Prevention_Cheat_Sheet.md
    try {
      factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
    } catch (TransformerConfigurationException e) {}
    try {
      factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
    } catch (IllegalArgumentException e) {}
    try {
      factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
    } catch (IllegalArgumentException e) {}
    return factory;
  }

  /**
   * Restricts the format to XML.
   */
  @Override
  public void setFormat(Format format) {
    if (format != Format.XML)
      throw new IllegalArgumentException("SourceHandle supports the XML format only");
  }
  /**
   * Specifies the mime type of the content and returns the handle
   * as a fluent convenience.
   * @param mimetype	the mime type of the content
   * @return	this handle
   */
  public SourceHandle withMimetype(String mimetype) {
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
  public Source toContent(InputStream serialization) {
    return (serialization == null) ? null :
            new StreamSource(new InputStreamReader(serialization, StandardCharsets.UTF_8));
  }
  @Override
  public Source bytesToContent(byte[] buffer) {
    return (buffer == null || buffer.length == 0) ? null :
            toContent(new ByteArrayInputStream(buffer));
  }
  @Override
  public byte[] contentToBytes(Source content) {
    if (content == null) return null;
    try {
      ByteArrayOutputStream buffer = new ByteArrayOutputStream();
      makeTransformer().newTransformer().transform(content,
              new StreamResult(new OutputStreamWriter(buffer, StandardCharsets.UTF_8)));
      return buffer.toByteArray();
    } catch (TransformerException e) {
      throw new MarkLogicIOException("Could not convert Source to byte[] array", e);
    }
  }

  /**
   * Buffers the transform source and returns the buffer as a string.
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
    if (content == null) {
      this.content = null;
      return;
    }

    this.underlyingStream = content;
    this.content = new StreamSource(new InputStreamReader(content, StandardCharsets.UTF_8));
  }
  @Override
  protected OutputStreamSender sendContent() {
    if (content == null) {
      throw new IllegalStateException("No source to transform to result for writing");
    }

    return this;
  }
  @Override
  public void write(OutputStream out) throws IOException {
    transform(new StreamResult(new OutputStreamWriter(out, StandardCharsets.UTF_8)));
  }

  /** Always call close() when finished with this handle -- it closes the underlying InputStream.
   */
  @Override
  public void close() {
    if ( underlyingStream != null ) {
      try {
        underlyingStream.close();
      } catch (IOException e) {
        logger.error("Failed to close underlying InputStream",e);
        throw new MarkLogicIOException(e);
      }
    }
  }
}
