/*
 * Copyright 2012-2018 MarkLogic Corporation
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

import com.marklogic.client.MarkLogicIOException;
import com.marklogic.client.MarkLogicInternalException;
import com.marklogic.client.impl.XmlFactories;
import com.marklogic.client.io.marker.BufferableHandle;
import com.marklogic.client.io.marker.ContentHandle;
import com.marklogic.client.io.marker.ContentHandleFactory;
import com.marklogic.client.io.marker.CtsQueryWriteHandle;
import com.marklogic.client.io.marker.StructureReadHandle;
import com.marklogic.client.io.marker.StructureWriteHandle;
import com.marklogic.client.io.marker.XMLReadHandle;
import com.marklogic.client.io.marker.XMLWriteHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSException;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSParser;
import org.w3c.dom.ls.LSResourceResolver;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

/**
 * A DOM Handle represents XML content as a DOM document for reading or writing.
 */
public class DOMHandle
  extends BaseHandle<InputStream, OutputStreamSender>
  implements OutputStreamSender, BufferableHandle, ContentHandle<Document>,
    XMLReadHandle, XMLWriteHandle,
    StructureReadHandle, StructureWriteHandle, CtsQueryWriteHandle
{
  static final private Logger logger = LoggerFactory.getLogger(DOMHandle.class);

  private LSResourceResolver     resolver;
  private Document               content;
  private DocumentBuilder        documentBuilder = XmlFactories.getNsAwareNotValidatingDocBuilder();
  private LSParser               parser = XmlFactories.getSynchronousLSParser(documentBuilder);
  private XPath                  xpathProcessor;

  /**
   * Creates a factory to create a DOMHandle instance for a DOM document.
   * @return	the factory
   */
  static public ContentHandleFactory newFactory() {
    return new ContentHandleFactory() {
      @Override
      public Class<?>[] getHandledClasses() {
        return new Class<?>[]{ Document.class };
      }
      @Override
      public boolean isHandled(Class<?> type) {
        return Document.class.isAssignableFrom(type);
      }
      @Override
      public <C> ContentHandle<C> newHandle(Class<C> type) {
        @SuppressWarnings("unchecked")
        ContentHandle<C> handle = isHandled(type) ?
                                  (ContentHandle<C>) new DOMHandle() : null;
        return handle;
      }
    };
  }

  /**
   * Zero-argument constructor.
   */
  public DOMHandle() {
    super();
    super.setFormat(Format.XML);
    setResendable(true);
  }
  /**
   * Initializes the handle with a DOM document for the content.
   * @param content	a DOM document
   */
  public DOMHandle(Document content) {
    this();
    set(content);
  }

  /**
   * Returns the resolver for resolving references while parsing the document.
   * @return	the resolver
   */
  public LSResourceResolver getResolver() {
    return resolver;
  }
  /**
   * Specifies the resolver for resolving references while parsing the document.
   * @param resolver	the reference resolver
   */
  public void setResolver(LSResourceResolver resolver) {
    this.resolver = resolver;

    // remove possibly cached LSParser
    this.parser = null;
  }

  /**
   * Returns the DOM document for the content.
   * @return	the DOM document
   */
  @Override
  public Document get() {
    return content;
  }
  /**
   * Assigns a DOM document as the content.
   * @param content	a DOM document
   */
  @Override
  public void set(Document content) {
    this.content = content;
  }
  /**
   * Assigns a DOM document as the content and returns the handle
   * as a fluent convenience.
   * @param content	a DOM document
   * @return	this handle
   */
  public DOMHandle with(Document content) {
    set(content);
    return this;
  }

  /**
   * Restricts the format to XML.
   */
  @Override
  public void setFormat(Format format) {
    if (format != Format.XML)
      throw new IllegalArgumentException("DOMHandle supports the XML format only");
  }
  /**
   * Specifies the mime type of the content and returns the handle
   * as a fluent convenience.
   * @param mimetype	the mime type of the content
   * @return	this handle
   */
  public DOMHandle withMimetype(String mimetype) {
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

      return buffer.toByteArray();
    } catch (IOException e) {
      throw new MarkLogicIOException(e);
    }
  }
  /**
   * Returns the DOM document as an XML string.
   */
  @Override
  public String toString() {
    try {
      byte[] buffer = toBuffer();
      if ( buffer == null ) return null;
      else return new String(buffer,"UTF-8");
    } catch (UnsupportedEncodingException e) {
      throw new MarkLogicIOException(e);
    }
  }

  /**
   * Specifies the factory for building DOM documents.
   * @param factory	the document factory
   */
  public void setFactory(final DocumentBuilderFactory factory) {
    try {
      this.documentBuilder = factory.newDocumentBuilder();
      this.parser = null;
    } catch (final ParserConfigurationException e) {
      throw new MarkLogicInternalException("Failed to make document builder.", e);
    }
  }

  /**
   * Get the processor used to evaluate XPath expressions.
   * You might get the XPath processor to configure it. For instance,
   * you can configure the XPath processor to declare namespace
   * bindings or to set a function or variable resolver.
   * @see com.marklogic.client.util.EditableNamespaceContext
   * @return	the XPath expression processor
   */
  public XPath getXPathProcessor() {
    if (xpathProcessor == null)
      xpathProcessor = makeXPathProcessorFactory().newXPath();
    return xpathProcessor;
  }
  /**
   * Specifies the processor used to evaluate XPath expressions against
   * the document.
   * @param xpathProcessor	the XPath expression processor
   */
  public void setXPathProcessor(XPath xpathProcessor) {
    this.xpathProcessor = xpathProcessor;
  }
  protected XPathFactory makeXPathProcessorFactory() {
    return XPathFactory.newInstance();
  }

  /**
   * Evaluate a string XPath expression against the retrieved document.
   * An XPath expression can return a Node or subinterface such as
   * Element or Text, a NodeList, or a Boolean, Number, or String value.
   * @param xpathExpression	the XPath expression as a string
   * @param as	the type expected to be matched by the xpath
   * @param <T> the type to return
   * @return	the value produced by the XPath expression
   * @throws XPathExpressionException if xpathExpression cannot be evaluated
   */
  public <T> T evaluateXPath(String xpathExpression, Class<T> as)
    throws XPathExpressionException {
    return evaluateXPath(xpathExpression, get(), as);
  }
  /**
   * Evaluate a string XPath expression relative to a node such as a node
   * returned by a previous XPath expression.
   * An XPath expression can return a Node or subinterface such as
   * Element or Text, a NodeList, or a Boolean, Number, or String value.
   * @param xpathExpression	the XPath expression as a string
   * @param context	the node for evaluating the expression
   * @param as	the type expected to be matched by the xpath
   * @param <T> the type to return
   * @return	the value produced by the XPath expression
   * @throws XPathExpressionException if xpathExpression cannot be evaluated
   */
  public <T> T evaluateXPath(String xpathExpression, Node context, Class<T> as)
    throws XPathExpressionException {
    checkContext(context);
    return castAs(
      getXPathProcessor().evaluate(xpathExpression, context, returnXPathConstant(as)),
      as
    );
  }
  /**
   * Compile an XPath string expression for efficient evaluation later.
   * @param xpathExpression	the XPath expression as a string
   * @return	the compiled XPath expression
   * @throws XPathExpressionException if xpathExpression cannot be compiled
   */
  public XPathExpression compileXPath(String xpathExpression)
    throws XPathExpressionException {
    return getXPathProcessor().compile(xpathExpression);
  }
  /**
   * Evaluate a compiled XPath expression against the retrieved document.
   * An XPath expression can return a Node or subinterface such as
   * Element or Text, a NodeList, or a Boolean, Number, or String value.
   * @param xpathExpression	an XPath expression compiled previously
   * @param as	the type expected to be matched by the xpath
   * @param <T> the type to return
   * @return	the value produced by the XPath expression
   * @throws XPathExpressionException if xpathExpression cannot be evaluated
   */
  public <T> T evaluateXPath(XPathExpression xpathExpression, Class<T> as)
    throws XPathExpressionException {
    return evaluateXPath(xpathExpression, get(), as);
  }
  /**
   * Evaluate a compiled XPath expression relative to a node such as a node
   * returned by a previous XPath expression.
   * An XPath expression can return a Node or subinterface such as
   * Element or Text, a NodeList, or a Boolean, Number, or String value.
   * @param xpathExpression	an XPath expression compiled previously
   * @param context	the node for evaluating the expression
   * @param as	the type expected to be matched by the xpath
   * @param <T> the type to return
   * @return	the value produced by the XPath expression
   * @throws XPathExpressionException if xpathExpression cannot be evaluated
   */
  public <T> T evaluateXPath(XPathExpression xpathExpression, Node context, Class<T> as)
    throws XPathExpressionException {
    checkContext(context);
    return castAs(
      xpathExpression.evaluate(context, returnXPathConstant(as)),
      as
    );
  }
  protected void checkContext(Node context) {
    if (context == null) {
      throw new IllegalStateException("Cannot process empty context");
    }
  }
  protected QName returnXPathConstant(Class<?> as) {
    if (as == null) {
      throw new IllegalArgumentException("cannot execute XPath as null");
    } else if (Node.class.isAssignableFrom(as)) {
      return XPathConstants.NODE;
    } else if (NodeList.class.isAssignableFrom(as)) {
      return XPathConstants.NODESET;
    } else if (String.class.isAssignableFrom(as)) {
      return XPathConstants.STRING;
    } else if (Number.class.isAssignableFrom(as)) {
      return XPathConstants.NUMBER;
    } else if (Boolean.class.isAssignableFrom(as)) {
      return XPathConstants.BOOLEAN;
    }
    throw new IllegalArgumentException("cannot execute XPath as "+as.getName());
  }
  protected <T> T castAs(Object result, Class<?> as) {
    if (result == null) {
      return null;
    }
    if (!as.isAssignableFrom(result.getClass())) {
      throw new IllegalArgumentException("cannot cast "+result.getClass().getName()+" to "+as.getName());
    }
    @SuppressWarnings("unchecked")
    T typedResult = (T) result;
    return typedResult;
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

    try {
      if (logger.isDebugEnabled())
        logger.debug("Parsing DOM document from input stream");

      final DOMImplementationLS domImpl = (DOMImplementationLS) documentBuilder.getDOMImplementation();
      if (this.parser == null) {
        parser = domImpl.createLSParser(DOMImplementationLS.MODE_SYNCHRONOUS, null);
      }

      if (resolver != null) {
        parser.getDomConfig().setParameter("resource-resolver", resolver);
      }

      LSInput domInput = domImpl.createLSInput();
      domInput.setEncoding("UTF-8");
      domInput.setByteStream(content);

      this.content = parser.parse(domInput);
    } finally {
      try {
        content.close();
      } catch (IOException e) {
        //ignore
      }

    }
  }
  @Override
  protected OutputStreamSender sendContent() {
    if (content == null) {
      throw new IllegalStateException("No document to write");
    }

    return this;
  }
  @Override
  public void write(OutputStream out) throws IOException {
    try {
      if (logger.isDebugEnabled())
        logger.debug("Serializing DOM document to output stream");

      DOMImplementationLS domImpl = (DOMImplementationLS) documentBuilder.getDOMImplementation();
      LSOutput domOutput = domImpl.createLSOutput();
      domOutput.setEncoding("UTF-8");
      domOutput.setByteStream(out);
      domImpl.createLSSerializer().write(content, domOutput);
    } catch (final DOMException | LSException e) {
      logger.error("Failed to serialize DOM document to output stream",e);
      throw new MarkLogicInternalException(e);
    }
  }
}
