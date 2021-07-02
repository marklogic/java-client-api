/*
 * Copyright (c) 2019 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.Schema;

import com.marklogic.client.io.marker.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.*;

import com.marklogic.client.MarkLogicIOException;
import com.marklogic.client.MarkLogicInternalException;

/**
 * <p>An Input Source Handle represents XML content as an input source for reading or writing.
 * When reading, the XML may be processed by a SAX content handler.</p>
 *
 * <p>Always call {@link #close} when finished with this handle to release the resources.</p>
 */
public class InputSourceHandle
  extends BaseHandle<InputStream, OutputStreamSender>
  implements OutputStreamSender, StreamingContentHandle<InputSource, InputStream>,
    XMLReadHandle, XMLWriteHandle,
    StructureReadHandle, StructureWriteHandle, CtsQueryWriteHandle,
    Closeable
{
  static final private Logger logger = LoggerFactory.getLogger(InputSourceHandle.class);

  private EntityResolver   resolver;
  private ErrorHandler     errorHandler;
  private Schema           defaultWriteSchema;
  private SAXParserFactory factory;
  private InputSource      content;
  private InputStream      underlyingStream;

  /**
   * Creates a factory to create a InputSourceHandle instance for a SAX InputSource.
   * @return	the factory
   */
  static public ContentHandleFactory newFactory() {
    return new ContentHandleFactory() {
      @Override
      public Class<?>[] getHandledClasses() {
        return new Class<?>[]{ InputSource.class };
      }
      @Override
      public boolean isHandled(Class<?> type) {
        return InputSource.class.isAssignableFrom(type);
      }
      @Override
      public <C> ContentHandle<C> newHandle(Class<C> type) {
        @SuppressWarnings("unchecked")
        ContentHandle<C> handle = isHandled(type) ?
                                  (ContentHandle<C>) new InputSourceHandle() : null;
        return handle;
      }
    };
  }

  /**
   * Zero-argument constructor.
   */
  public InputSourceHandle() {
    super();
    super.setFormat(Format.XML);
    setResendable(false);
  }
  /**
   * Initializes the handle with a SAX input source for the content.
   * @param content	a SAX input source
   */
  public InputSourceHandle(InputSource content) {
    this();
    set(content);
  }

  /**
   * Returns the resolver for resolving references while parsing
   * the input source.
   * @return	the resolver
   */
  public EntityResolver getResolver() {
    return resolver;
  }
  /**
   * Specifies the resolver for resolving references while parsing
   * the input source.
   * @param resolver	the reference resolver
   */
  public void setResolver(EntityResolver resolver) {
    this.resolver = resolver;
  }

  /**
   * Returns the error handler for errors discovered while parsing
   * the input source.
   * @return	the error handler
   */
  public ErrorHandler getErrorHandler() {
    return errorHandler;
  }
  /**
   * Specifies the error handler for errors discovered while parsing
   * the input source.
   * @param errorHandler	the error handler
   */
  public void setErrorHandler(ErrorHandler errorHandler) {
    this.errorHandler = errorHandler;
  }

  /**
   * Returns the input source for the content.
   * @return	the input source
   */
  @Override
  public InputSource get() {
    return content;
  }
  /**
   * Assigns an input source as the content.
   * @param content	an input source
   */
  @Override
  public void set(InputSource content) {
    this.content = content;
  }
  /**
   * Assigns an input source as the content and returns the handle
   * as a fluent convenience.
   * @param content	an input source
   * @return	this handle
   */
  public InputSourceHandle with(InputSource content) {
    set(content);
    return this;
  }

  @Override
  public Class<InputSource> getContentClass() {
    return InputSource.class;
  }
  @Override
  public InputSourceHandle newHandle() {
    return new InputSourceHandle().withMimetype(getMimetype());
  }
  @Override
  public InputSourceHandle[] newHandleArray(int length) {
    if (length < 0) throw new IllegalArgumentException("array length less than zero: "+length);
    return new InputSourceHandle[length];
  }
  @Override
  public InputSource[] newArray(int length) {
    if (length < 0) throw new IllegalArgumentException("array length less than zero: "+length);
    return new InputSource[length];
  }

  /**
   * Reads the input source, sending SAX events to the supplied content handler.
   * @param handler	the SAX content handler
   */
  public void process(ContentHandler handler) {
    try {
      if (logger.isInfoEnabled())
        logger.info("Processing input source with SAX content handler");

      XMLReader reader = makeReader(false);

      reader.setContentHandler(handler);

      reader.parse(content);
    } catch (SAXException e) {
      logger.error("Failed to process input source with SAX content handler",e);
      throw new MarkLogicInternalException(e);
    } catch (IOException e) {
      logger.error("Failed to process input source with SAX content handler",e);
      throw new MarkLogicInternalException(e);
    }
  }

  /**
   * Restricts the format to XML.
   */
  @Override
  public void setFormat(Format format) {
    if (format != Format.XML)
      throw new IllegalArgumentException("InputSourceHandle supports the XML format only");
  }
  /**
   * Specifies the mime type of the content and returns the handle
   * as a fluent convenience.
   * @param mimetype	the mime type of the content
   * @return	this handle
   */
  public InputSourceHandle withMimetype(String mimetype) {
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
  public InputSource toContent(InputStream serialization) {
    return (serialization == null) ? null :
            new InputSource(new InputStreamReader(serialization, StandardCharsets.UTF_8));
  }
  @Override
  public InputSource bytesToContent(byte[] buffer) {
    return (buffer == null || buffer.length == 0) ? null :
            toContent(new ByteArrayInputStream(buffer));
  }
  @Override
  public byte[] contentToBytes(InputSource content) {
    try {
      ByteArrayOutputStream buffer = new ByteArrayOutputStream();
      sendContent(content).write(buffer);
      return buffer.toByteArray();
    } catch (IOException e) {
      throw new MarkLogicIOException("Could not convert InputSource to byte[] array", e);
    }
  }

  /**
   * Buffers the SAX input source and returns the buffer
   * as an XML string.
   */
  @Override
  public String toString() {
    byte[] buffer = toBuffer();
    return (buffer == null) ? null : new String(buffer, StandardCharsets.UTF_8);
  }

  /**
   * Returns the factory for parsing SAX events.
   * @return	the SAX factory
   * @throws SAXException if such an error occurs while initializing the
   *         new factory
   * @throws ParserConfigurationException if such an error occurs while
   *         initializing the new factory
   */
  public SAXParserFactory getFactory() throws SAXException, ParserConfigurationException {
    if (factory == null)
      factory = makeSAXParserFactory();
    return factory;
  }
  /**
   * Specifies the factory for parsing SAX events.
   * @param factory	the SAX factory
   */
  public void setFactory(SAXParserFactory factory) {
    this.factory = factory;
  }
  protected SAXParserFactory makeSAXParserFactory() throws SAXException, ParserConfigurationException {
    SAXParserFactory factory = SAXParserFactory.newInstance();
    // default to best practices for conservative security including recommendations per
    // https://github.com/OWASP/CheatSheetSeries/blob/master/cheatsheets/XML_External_Entity_Prevention_Cheat_Sheet.md
    try {
      factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
    } catch (ParserConfigurationException | SAXNotRecognizedException | SAXNotSupportedException e) {}
    try {
      factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
    } catch (ParserConfigurationException | SAXNotRecognizedException | SAXNotSupportedException e) {}
    try {
      factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtds", false);
    } catch (ParserConfigurationException | SAXNotRecognizedException | SAXNotSupportedException e) {}
    try {
      factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
    } catch (ParserConfigurationException | SAXNotRecognizedException | SAXNotSupportedException e) {}
    try {
      factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
    } catch (ParserConfigurationException | SAXNotRecognizedException | SAXNotSupportedException e) {}
    factory.setXIncludeAware(false);
    factory.setNamespaceAware(true);
    factory.setValidating(false);

    return factory;
  }

  /**
   * Returns the default schema for validating the input source
   * while writing to the database.
   * @return	the default schema for writing documents
   */
  public Schema getDefaultWriteSchema() {
    return defaultWriteSchema;
  }
  /**
   * Specifies the default schema for validating the input source. The
   * default schema is used only while writing to the database and only
   * when no schema has been set directly on the factory. To minimize
   * creation of partial documents while writing an input source to the database,
   * set the error handler on the InputSourceHandle to {@link DraconianErrorHandler}
   * and set the repair policy to NONE on the XMLDocumentManager.  An error on the
   * root element can still result in an empty document.
   * @param schema	the default schema for writing documents
   */
  public void setDefaultWriteSchema(Schema schema) {
    this.defaultWriteSchema = schema;
  }

  protected XMLReader makeReader(boolean isForWrite) {
    try {
      SAXParserFactory factory = getFactory();
      if (factory == null) {
        throw new MarkLogicInternalException("Failed to make SAX parser factory");
      }

      boolean useDefaultSchema =
        (isForWrite && defaultWriteSchema != null && factory.getSchema() == null);
      if (useDefaultSchema) {
        factory.setSchema(defaultWriteSchema);
      }

      XMLReader reader = factory.newSAXParser().getXMLReader();
      // default to best practices for conservative security including recommendations per
      // https://github.com/OWASP/CheatSheetSeries/blob/master/cheatsheets/XML_External_Entity_Prevention_Cheat_Sheet.md
      try {
        reader.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
      } catch (SAXNotRecognizedException | SAXNotSupportedException e) {}
      try {
        reader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtds", false);
      } catch (SAXNotRecognizedException | SAXNotSupportedException e) {}
      try {
        reader.setFeature("http://xml.org/sax/features/external-general-entities", false);
      } catch (SAXNotRecognizedException | SAXNotSupportedException e) {}
      try {
        reader.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
      } catch (SAXNotRecognizedException | SAXNotSupportedException e) {}

      if (useDefaultSchema) {
        factory.setSchema(null);
      }

      if (resolver != null)
        reader.setEntityResolver(resolver);

      if (errorHandler != null)
        reader.setErrorHandler(errorHandler);

      return reader;
    } catch (SAXException e) {
      logger.error("Failed to process input source with SAX content handler",e);
      throw new MarkLogicInternalException(e);
    } catch (ParserConfigurationException e) {
      logger.error("Failed to process input source with SAX content handler",e);
      throw new MarkLogicInternalException(e);
    }
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
    this.content = new InputSource(new InputStreamReader(content, StandardCharsets.UTF_8));
  }
  @Override
  protected OutputStreamSender sendContent() {
    if (content == null) {
      throw new IllegalStateException("No input source to write");
    }

    return this;
  }
  protected OutputStreamSender sendContent(InputSource content) {
    return (content == null) ? null :
            new OutputStreamSenderImpl(makeTransformer(), makeReader(true), content);
  }
  @Override
  public void write(OutputStream out) throws IOException {
    try {
      makeTransformer().newTransformer().transform(
        new SAXSource(makeReader(true), content),
        new StreamResult(new OutputStreamWriter(out, StandardCharsets.UTF_8))
      );
    } catch (TransformerException e) {
      logger.error("Failed to transform input source into result",e);
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
   * DraconianErrorHandler treats SAX parse errors as exceptions
   * but ignores warnings (based on the JavaDoc for the
   * javax.xml.validation package). To minimize creation of partial
   * documents while writing an input source to the database,
   * set the error handler on the InputSourceHandle to DraconianErrorHandler
   * and set the repair policy to NONE on the XMLDocumentManager.  An error
   * on the root element can still result in an empty document.
   */
  static public class DraconianErrorHandler implements ErrorHandler {
    /**
     * Throws the fatal error as a parse exception.
     */
    @Override
    public void fatalError(SAXParseException e) throws SAXException {
      throw e;
    }
    /**
     * Throws the error as a parse exception.
     */
    @Override
    public void error(SAXParseException e) throws SAXException {
      throw e;
    }
    /**
     * Ignores the warning.
     */
    @Override
    public void warning(SAXParseException e) throws SAXException {
      // noop
    }
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

  private static class OutputStreamSenderImpl implements OutputStreamSender {
    private final TransformerFactory transformerFactory;
    private final XMLReader xmlReader;
    private final InputSource content;
    private OutputStreamSenderImpl(TransformerFactory transformerFactory, XMLReader xmlReader, InputSource content) {
      this.transformerFactory = transformerFactory;
      this.xmlReader = xmlReader;
      this.content  = content;
    }

    @Override
    public void write(OutputStream out) throws IOException {
      try {
        this.transformerFactory.newTransformer().transform(
                new SAXSource(this.xmlReader, this.content),
                new StreamResult(new OutputStreamWriter(out, StandardCharsets.UTF_8))
        );
      } catch (TransformerException e) {
        logger.error("Failed to transform input source into result",e);
        throw new MarkLogicIOException(e);
      }
    }
  }
}
