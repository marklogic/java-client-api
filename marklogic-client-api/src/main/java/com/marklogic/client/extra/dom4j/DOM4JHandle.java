/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.extra.dom4j;

import java.io.*;
import java.nio.charset.StandardCharsets;

import com.marklogic.client.io.BaseHandle;
import com.marklogic.client.io.marker.ResendableContentHandle;
import com.marklogic.client.io.marker.*;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.marklogic.client.MarkLogicIOException;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.OutputStreamSender;
import org.xml.sax.SAXException;

/**
 * A DOM4JHandle represents XML content as a dom4j document for reading or writing.
 * You must install the dom4j library to use this class.
 */
public class DOM4JHandle
  extends BaseHandle<InputStream, OutputStreamSender>
  implements ResendableContentHandle<Document, InputStream>, OutputStreamSender,
    XMLReadHandle, XMLWriteHandle,
    StructureReadHandle, StructureWriteHandle {
  private SAXReader reader;
  private OutputFormat outputFormat;
  private Document content;

  /**
   * Creates a factory to create a DOM4JHandle instance for a dom4j document.
   *
   * @return the factory
   */
  static public ContentHandleFactory newFactory() {
    return new ContentHandleFactory() {
      @Override
      public Class<?>[] getHandledClasses() {
        return new Class<?>[]{Document.class};
      }

      @Override
      public boolean isHandled(Class<?> type) {
        return Document.class.isAssignableFrom(type);
      }

      @Override
      public <C> ContentHandle<C> newHandle(Class<C> type) {
        @SuppressWarnings("unchecked")
        ContentHandle<C> handle = isHandled(type) ?
                (ContentHandle<C>) new DOM4JHandle() : null;
        return handle;
      }
    };
  }

  /**
   * Zero-argument constructor.
   */
  public DOM4JHandle() {
    super();
    setResendable(true);
    super.setFormat(Format.XML);
  }

  /**
   * Provides a handle on XML content as a dom4j document structure.
   *
   * @param content the XML document.
   */
  public DOM4JHandle(Document content) {
    this();
    set(content);
  }

  @Override
  public DOM4JHandle newHandle() {
    return new DOM4JHandle();
  }
  @Override
  public DOM4JHandle[] newHandleArray(int length) {
    if (length < 0) throw new IllegalArgumentException("array length less than zero: "+length);
    return new DOM4JHandle[length];
  }

  /**
   * Returns the dom4j reader for XML content.
   *
   * @return the dom4j reader.
   */
  public SAXReader getReader() {
    if (reader == null)
      reader = makeReader();

    return reader;
  }

  /**
   * Specifies a dom4j reader for XML content.
   *
   * @param reader the dom4j reader.
   */
  public void setReader(SAXReader reader) {
    this.reader = reader;
  }

  protected SAXReader makeReader() {
    SAXReader reader = new SAXReader();
    // default to best practices for conservative security including recommendations per
    // https://github.com/OWASP/CheatSheetSeries/blob/master/cheatsheets/XML_External_Entity_Prevention_Cheat_Sheet.md
    try {
      reader.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
    } catch (SAXException e) {
    }
    try {
      reader.setFeature("http://xml.org/sax/features/external-general-entities", false);
    } catch (SAXException e) {
    }
    try {
      reader.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
    } catch (SAXException e) {
    }

    reader.setValidation(false);
    return reader;
  }

  /**
   * Returns the dom4j output format for serializing XML content.
   *
   * @return the output format.
   */
  public OutputFormat getOutputFormat() {
    return outputFormat;
  }

  /**
   * Specifies the dom4j output format for serializing XML content.
   *
   * @param outputFormat the output format.
   */
  public void setOutputFormat(OutputFormat outputFormat) {
    this.outputFormat = outputFormat;
  }

  /**
   * Returns the XML document structure.
   *
   * @return the XML document.
   */
  @Override
  public Document get() {
    return content;
  }

  /**
   * Assigns an XML document structure as the content.
   *
   * @param content the XML document.
   */
  @Override
  public void set(Document content) {
    this.content = content;
  }

  /**
   * Assigns an XML document structure as the content and returns the handle.
   *
   * @param content the XML document.
   * @return the handle on the XML document.
   */
  public DOM4JHandle with(Document content) {
    set(content);
    return this;
  }

  @Override
  public Class<Document> getContentClass() {
    return Document.class;
  }

  /**
   * Restricts the format to XML.
   */
  @Override
  public void setFormat(Format format) {
    if (format != Format.XML)
      throw new IllegalArgumentException("JDOMHandle supports the XML format only");
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
  public Document bytesToContent(byte[] buffer) {
    return (buffer == null || buffer.length == 0) ? null :
            toContent(new ByteArrayInputStream(buffer));
  }
  @Override
  public byte[] contentToBytes(Document content) {
    try {
      if (content == null)
        return null;

      ByteArrayOutputStream buffer = new ByteArrayOutputStream();
      sendContent(content).write(buffer);
      return buffer.toByteArray();
    } catch (IOException e) {
      throw new MarkLogicIOException(e);
    }
  }

  /**
   * Returns the XML document as a string.
   */
  @Override
  public String toString() {
    byte[] buffer = toBuffer();
    return (buffer == null) ? null : new String(buffer, StandardCharsets.UTF_8);
  }

  @Override
  public Document toContent(InputStream serialization) {
    if (serialization == null) return null;

    try {
      return getReader().read(
              new InputStreamReader(serialization, StandardCharsets.UTF_8)
      );
    } catch (DocumentException e) {
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
  protected OutputStreamSender sendContent() {
    return sendContent(get());
  }
  private OutputStreamSender sendContent(Document content) {
    return new OutputStreamSenderImpl(getOutputFormat(), content);
  }

  @Override
  public void write(OutputStream out) throws IOException {
    sendContent().write(out);
  }

  static private class OutputStreamSenderImpl implements OutputStreamSender {
    private final OutputFormat outputFormat;
    private final Document content;

    private OutputStreamSenderImpl(OutputFormat outputFormat, Document content) {
      if (content == null) {
        throw new IllegalStateException("No document to write");
      }
      this.outputFormat = outputFormat;
      this.content = content;
    }
    @Override
    public void write(OutputStream out) throws IOException {
      Writer writer = new BufferedWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8));
      if (outputFormat != null) {
        new XMLWriter(writer, outputFormat).write(content);
      } else {
        new XMLWriter(writer).write(content);
      }
      writer.flush();
    }
  }
}
