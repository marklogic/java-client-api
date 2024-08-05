/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLResolver;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import com.marklogic.client.io.marker.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.marklogic.client.MarkLogicIOException;
import com.marklogic.client.MarkLogicInternalException;

/**
 * <p>An XML Stream Reader Handle represents XML content as an XML stream reader
 * for reading as a StAX pull stream.</p>
 *
 * <p>Either call {@link #close} or {@link #get}.close() when finished with this handle
 * to release the resources.</p>
 */
public class XMLStreamReaderHandle
  extends BaseHandle<InputStream, OutputStreamSender>
  implements OutputStreamSender, StreamingContentHandle<XMLStreamReader, InputStream>,
    XMLReadHandle, XMLWriteHandle,
    StructureReadHandle, StructureWriteHandle, CtsQueryWriteHandle,
    Closeable
{
  static final private Logger logger = LoggerFactory.getLogger(XMLStreamReaderHandle.class);

  private XMLResolver     resolver;
  private XMLStreamReader content;
  private InputStream contentSource;
  private XMLInputFactory factory;

  /**
   * Creates a factory to create an XMLStreamReaderHandle instance for a StAX stream reader.
   * @return	the factory
   */
  static public ContentHandleFactory newFactory() {
    return new ContentHandleFactory() {
      @Override
      public Class<?>[] getHandledClasses() {
        return new Class<?>[]{ XMLStreamReader.class };
      }
      @Override
      public boolean isHandled(Class<?> type) {
        return XMLStreamReader.class.isAssignableFrom(type);
      }
      @Override
      public <C> ContentHandle<C> newHandle(Class<C> type) {
        @SuppressWarnings("unchecked")
        ContentHandle<C> handle = isHandled(type) ?
                                  (ContentHandle<C>) new XMLStreamReaderHandle() : null;
        return handle;
      }
    };
  }

  /**
   * Zero-argument constructor.
   */
  public XMLStreamReaderHandle() {
    super();
    super.setFormat(Format.XML);
    setResendable(false);
  }
  /**
   * Initializes the handle with a StAX stream reader for the content.
   * @param content	a StAX stream reader
   */
  public XMLStreamReaderHandle(XMLStreamReader content) {
    this();
    set(content);
  }

  /**
   * Returns the resolver for resolving references while parsing
   * the event reader source.
   * @return	the resolver
   */
  public XMLResolver getResolver() {
    return resolver;
  }
  /**
   * Specifies the resolver for resolving references while parsing
   * the event reader source.
   * @param resolver	the reference resolver
   */
  public void setResolver(XMLResolver resolver) {
    this.resolver = resolver;
  }

  /**
   * Returns an XML Stream Reader for reading a resource from the database
   * as a StAX pull stream.
   *
   * When finished with the stream reader, call {@link #close} to release
   * the response.
   *
   * @return	the XML stream reader
   */
  @Override
  public XMLStreamReader get() {
    return content;
  }
  /**
   * Assigns the stream reader for the content.
   * @param content	a StAX stream reader
   */
  @Override
  public void set(XMLStreamReader content) {
    this.content = content;
  }
  /**
   * Assigns an stream reader for the content and returns the handle
   * as a fluent convenience.
   * @param content	a StAX stream reader
   * @return	this handle
   */
  public XMLStreamReaderHandle with(XMLStreamReader content) {
    set(content);
    return this;
  }

  @Override
  public Class<XMLStreamReader> getContentClass() {
    return XMLStreamReader.class;
  }
  @Override
  public XMLStreamReaderHandle newHandle() {
    return new XMLStreamReaderHandle().withMimetype(getMimetype());
  }
  @Override
  public XMLStreamReaderHandle[] newHandleArray(int length) {
    if (length < 0) throw new IllegalArgumentException("array length less than zero: "+length);
    return new XMLStreamReaderHandle[length];
  }
  @Override
  public XMLStreamReader[] newArray(int length) {
    if (length < 0) throw new IllegalArgumentException("array length less than zero: "+length);
    return new XMLStreamReader[length];
  }

  /**
   * Restricts the format to XML.
   */
  @Override
  public void setFormat(Format format) {
    if (format != Format.XML)
      throw new IllegalArgumentException("XMLStreamReaderHandle supports the XML format only");
  }
  /**
   * Specifies the mime type of the content and returns the handle
   * as a fluent convenience.
   * @param mimetype	the mime type of the content
   * @return	this handle
   */
  public XMLStreamReaderHandle withMimetype(String mimetype) {
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
  public XMLStreamReader toContent(InputStream serialization) {
    if (serialization == null) return null;
    try {
      XMLInputFactory factory = getFactory();
      if (factory == null) {
        throw new MarkLogicInternalException("Failed to make StAX input factory");
      }

      if (resolver != null)
        factory.setXMLResolver(resolver);

      return factory.createXMLStreamReader(serialization, "UTF-8");
    } catch (XMLStreamException e) {
      logger.error("Failed to parse StAX stream from input stream",e);
      throw new MarkLogicInternalException(e);
    } catch (FactoryConfigurationError e) {
      logger.error("Failed to parse StAX stream from input stream",e);
      throw new MarkLogicInternalException(e);
    }
  }
  @Override
  public XMLStreamReader bytesToContent(byte[] buffer) {
    return (buffer == null || buffer.length == 0) ? null :
            toContent(new ByteArrayInputStream(buffer));
  }
  @Override
  public byte[] contentToBytes(XMLStreamReader content) {
    if (content == null) return null;
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    write(this.content, buffer);
    return buffer.toByteArray();
  }

  /**
   * Closes the XMLStreamReader and the InputStream, if any, used to populate
   * the XMLStreamReader.  This method or get().close() should always be called when finished
   * with the stream reader.
   */
  @Override
  public void close() {
    try {
      if ( content != null ) content.close();
    } catch (XMLStreamException e) {
      logger.error("Failed to close underlying XMLStreamReader",e);
      throw new MarkLogicIOException(e);
      // whether that failed or not, attempt to close underlying InputStream
    } finally {
      if ( contentSource != null ) {
        try {
          contentSource.close();
        } catch (IOException e) {
          logger.error("Failed to close underlying InputStream",e);
          throw new MarkLogicIOException(e);
        }
      }
    }
  }

  /**
   * Buffers the StAX stream and returns the buffer as an XML string.
   */
  @Override
  public String toString() {
    byte[] buffer = toBuffer();
    return (buffer == null) ? null : new String(buffer, StandardCharsets.UTF_8);
  }

  /**
   * Returns the factory for parsing StAX streams.
   * @return	the StAX factory
   */
  public XMLInputFactory getFactory() {
    if (factory == null)
      factory = makeXMLInputFactory();
    return factory;
  }
  /**
   * Specifies the factory for parsing StAX streams.
   * @param factory	the StAX factory
   */
  public void setFactory(XMLInputFactory factory) {
    this.factory = factory;
  }
  protected XMLInputFactory makeXMLInputFactory() {
    XMLInputFactory factory = XMLInputFactory.newFactory();
    // default to best practices for conservative security including recommendations per
    // https://github.com/OWASP/CheatSheetSeries/blob/master/cheatsheets/XML_External_Entity_Prevention_Cheat_Sheet.md
    try {
      factory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
    } catch (IllegalArgumentException e) {}
    try {
      factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
    } catch (IllegalArgumentException e) {}
    try {
      factory.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, false);
    } catch (IllegalArgumentException e) {}
    try {
      factory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, true);
    } catch (IllegalArgumentException e) {}
    try {
      factory.setProperty(XMLInputFactory.IS_VALIDATING, false);
    } catch (IllegalArgumentException e) {}

    return factory;
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

    if (logger.isInfoEnabled())
      logger.info("Parsing StAX stream from input stream");
    this.content = toContent(content);
    this.contentSource = content;
  }
  @Override
  protected OutputStreamSender sendContent() {
    if (content == null) {
      throw new IllegalStateException("No input source to write");
    }

    return this;
  }
  @Override
  public void write(OutputStream out) throws IOException {
    write(this.content, out);
  }
  private void write(XMLStreamReader content, OutputStream out) {
    try {
      XMLInputFactory inputFactory = getFactory();
      if (inputFactory == null) {
        throw new MarkLogicInternalException("Failed to make StAX input factory");
      }

      // TODO: rework to copy straight from stream reader to stream writer
      XMLEventReader reader = inputFactory.createXMLEventReader(content);

      XMLOutputFactory outputFactory = XMLOutputFactory.newFactory();
      XMLEventWriter   writer        =
              outputFactory.createXMLEventWriter(out, "UTF-8");

      writer.add(reader);
      writer.flush();
      writer.close();

      content.close();
    } catch (XMLStreamException e) {
      logger.error("Failed to parse StAX events from input stream",e);
      throw new MarkLogicInternalException(e);
    }
  }
}
