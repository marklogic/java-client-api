/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.extra.jdom;

import java.io.*;
import java.nio.charset.StandardCharsets;

import com.marklogic.client.io.BaseHandle;
import com.marklogic.client.io.marker.ResendableContentHandle;
import com.marklogic.client.io.marker.*;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.input.sax.XMLReaders;
import org.jdom2.output.XMLOutputter;

import com.marklogic.client.MarkLogicIOException;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.OutputStreamSender;

/**
 * A JDOM Handle represents XML content as a JDOM document for reading or writing.
 * You must install the JDOM library to use this class.
 */
public class JDOMHandle
  extends BaseHandle<InputStream, OutputStreamSender>
  implements ResendableContentHandle<Document, InputStream>, OutputStreamSender,
    XMLReadHandle, XMLWriteHandle,
    StructureReadHandle, StructureWriteHandle {
  private Document content;
  private SAXBuilder builder;
  private XMLOutputter outputter;

  /**
   * Creates a factory to create a JDOMHandle instance for a JDOM document.
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
                (ContentHandle<C>) new JDOMHandle() : null;
        return handle;
      }
    };
  }

  /**
   * Zero-argument constructor.
   */
  public JDOMHandle() {
    super();
    setResendable(true);
    super.setFormat(Format.XML);
  }

  /**
   * Provides a handle on XML content as a JDOM document structure.
   *
   * @param content the XML document.
   */
  public JDOMHandle(Document content) {
    this();
    set(content);
  }

  @Override
  public JDOMHandle newHandle() {
    return new JDOMHandle();
  }

  @Override
  public JDOMHandle[] newHandleArray(int length) {
    if (length < 0) throw new IllegalArgumentException("array length less than zero: "+length);
    return new JDOMHandle[length];
  }

  /**
   * Returns the JDOM structure builder for XML content.
   *
   * @return the JDOM builder.
   */
  public SAXBuilder getBuilder() {
    if (builder == null)
      builder = makeBuilder();
    return builder;
  }

  /**
   * Specifies a JDOM structure builder for XML content.
   *
   * @param builder the JDOM builder.
   */
  public void setBuilder(SAXBuilder builder) {
    this.builder = builder;
  }

  protected SAXBuilder makeBuilder() {
    SAXBuilder builder = new SAXBuilder(XMLReaders.NONVALIDATING);
    // default to best practices for conservative security including recommendations per
    // https://github.com/OWASP/CheatSheetSeries/blob/master/cheatsheets/XML_External_Entity_Prevention_Cheat_Sheet.md
    builder.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
    builder.setFeature("http://xml.org/sax/features/external-general-entities", false);
    builder.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
    return builder;
  }

  /**
   * Returns the JDOM serializer for XML content.
   *
   * @return the JDOM serializer.
   */
  public XMLOutputter getOutputter() {
    if (outputter == null)
      outputter = makeOutputter();
    return outputter;
  }

  /**
   * Specifies a JDOM serializer for XML content.
   *
   * @param outputter the JDOM serializer.
   */
  public void setOutputter(XMLOutputter outputter) {
    this.outputter = outputter;
  }

  protected XMLOutputter makeOutputter() {
    return new XMLOutputter();
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
  public JDOMHandle with(Document content) {
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
    return (buffer == null || buffer.length == 0) ?
            null : toContent(new ByteArrayInputStream(buffer));
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
      return getBuilder().build(
              new InputStreamReader(serialization, StandardCharsets.UTF_8)
      );
    } catch (JDOMException e) {
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
  protected Class<InputStream> receiveAs() {
    return InputStream.class;
  }
  @Override
  protected void receiveContent(InputStream content) {
    set(toContent(content));
  }
  @Override
  protected OutputStreamSender sendContent() {
    return sendContent(get());
  }
  private OutputStreamSender sendContent(Document content) {
    return new OutputStreamSenderImpl(getOutputter(), content);
  }

  @Override
  public void write(OutputStream out) throws IOException {
    sendContent().write(out);
  }

  static private class OutputStreamSenderImpl implements OutputStreamSender {
    private final XMLOutputter outputter;
    private final Document content;
    private OutputStreamSenderImpl(XMLOutputter outputter, Document content) {
      if (content == null) {
        throw new IllegalStateException("No document to write");
      }
      this.outputter = outputter;
      this.content = content;
    }
    @Override
    public void write(OutputStream out) throws IOException {
      outputter.output(
              content,
              new BufferedWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8))
      );
    }
  }
}
