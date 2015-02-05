/*
 * Copyright 2012-2015 MarkLogic Corporation
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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSException;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSParser;

import com.marklogic.client.MarkLogicIOException;
import com.marklogic.client.MarkLogicInternalException;
import com.marklogic.client.io.marker.BufferableHandle;
import com.marklogic.client.io.marker.ContentHandle;
import com.marklogic.client.io.marker.ContentHandleFactory;
import com.marklogic.client.io.marker.XMLReadHandle;
import com.marklogic.client.io.marker.XMLWriteHandle;

import javax.xml.datatype.Duration;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Supports get/set of any type handled by javax.xml.bind.DatatypeConverter or for time-
 * related types, javax.xml.datatype.DatatypeFactory.
 */
public class JAXBDatatypeHandle<T>
    extends BaseHandle<InputStream, OutputStreamSender>
    implements OutputStreamSender, BufferableHandle, ContentHandle<T>,
        XMLReadHandle, XMLWriteHandle
{
    public static final DataType<Calendar> XS_ANYURI = new DataType<Calendar>(Calendar.class);
    public static final DataType<Calendar> XS_BASE64BINARY = new DataType<Calendar>(Calendar.class);
    public static final DataType<Calendar> XS_BOOLEAN = new DataType<Calendar>(Calendar.class);
    public static final DataType<Calendar> XS_DATE = new DataType<Calendar>(Calendar.class);
    public static final DataType<Calendar> XS_DATETIME = new DataType<Calendar>(Calendar.class);
    public static final DataType<Calendar> XS_DAYTIMEDURATION = new DataType<Calendar>(Calendar.class);
    public static final DataType<Calendar> XS_DECIMAL = new DataType<Calendar>(Calendar.class);
    public static final DataType<Calendar> XS_DOUBLE = new DataType<Calendar>(Calendar.class);
    public static final DataType<Duration> XS_DURATION = new DataType<Duration>(Duration.class);
    public static final DataType<Calendar> XS_FLOAT = new DataType<Calendar>(Calendar.class);
    public static final DataType<Calendar> XS_GDAY = new DataType<Calendar>(Calendar.class);
    public static final DataType<Calendar> XS_GMONTH = new DataType<Calendar>(Calendar.class);
    public static final DataType<Calendar> XS_GMONTHDAY = new DataType<Calendar>(Calendar.class);
    public static final DataType<Calendar> XS_GYEAR = new DataType<Calendar>(Calendar.class);
    public static final DataType<Calendar> XS_GYEARMONTH = new DataType<Calendar>(Calendar.class);
    public static final DataType<Calendar> XS_HEXBINARY = new DataType<Calendar>(Calendar.class);
    public static final DataType<Calendar> XS_INTEGER = new DataType<Calendar>(Calendar.class);
    public static final DataType<Calendar> XS_QNAME = new DataType<Calendar>(Calendar.class);
    public static final DataType<Calendar> XS_STRING = new DataType<Calendar>(Calendar.class);
    public static final DataType<Calendar> XS_TIME = new DataType<Calendar>(Calendar.class);

    public static class DataType<D> {
         private Class<D> clazz;
         private DataType(Class<D> clazz) {
             this.clazz = clazz;
         }

         public Class<D> getMappedClass() {
             return clazz;
         }
     };

	static final private Logger logger = LoggerFactory.getLogger(JAXBHandle.class);

    private T                      content;
    private DocumentBuilderFactory factory;

    /**
     * Creates a factory to create a DOMHandle instance for a DOM document.
     * @return    the factory
     */
    static public ContentHandleFactory newFactory() {
        return new ContentHandleFactory() {
            @Override
            public Class<?>[] getHandledClasses() {
                return null;
            }
            @Override
            public boolean isHandled(Class<?> type) {
                return (Boolean) null;
            }
            @Override
            public <C> ContentHandle<C> newHandle(Class<C> type) {
                return null;
            }
        };
    }

    public JAXBDatatypeHandle(Class<T> clazz) {
        super();
        setResendable(true);
    }


    /**
     * Initializes the handle with the content.
     * @param xsType the localname part of the XML Schema type.  The type must be one of:
     *   <pre>
     *   anySimpleType
     *   base64Binary
     *   boolean
     *   byte
     *   date
     *   dateTime
     *   dayTimeDuration
     *   decimal
     *   double
     *   duration
     *   float
     *   hexBinary
     *   int
     *   integer
     *   long
     *   QName
     *   short
     *   string
     *   time
     *   unsignedInt
     *   unsignedLong
     *   unsignedShort
     *   yearMonthDuration
     *   <pre>
     * @param content the java object.  The type must be one of:
     *   <pre>
     *   String
     *   byte[]
     *   boolean
     *   byte
     *   double
     *   float
     *   int
     *   BigInteger
     *   long
     *   javax.xml.namespace.QName
     *   short
     *   Calendar
     *   javax.xml.datatype.Duration
     *   javax.xml.datatype.XMLGregorianCalendar
     *   </pre>
     */
    public JAXBDatatypeHandle(DataType<T> type) {
        super();
    }

    public JAXBDatatypeHandle(DataType<T> type, String content) {
        super();
        set(convert(content));
    }

    public JAXBDatatypeHandle(T content) {
        super();
        set(content);
    }

    public T convert(String content) {
        // use DatatypeConverter or DatatypeFactory to convert 
        return null;
    }

    /**
     * Returns the DOM Node for the content.
     * @return    the DOM Node
     */
    @Override
    public T get() {
        return content;
    }
    /**
     * Assigns a DOM Node document as the content.
     * @param content    a DOM Node
     */
    @Override
    public void set(T content) {
        this.content = content;
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
     * Returns the DOM Node as a string.
     */
    @Override
    public String toString() {
        try {
            return new String(toBuffer(),"UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new MarkLogicIOException(e);
        }
    }
    /**
     * Returns the factory for building DOM documents.
     * @return    the document factory
     */
    public DocumentBuilderFactory getFactory() throws ParserConfigurationException {
        if (factory == null)
            factory = makeDocumentBuilderFactory();
        return factory;
    }
    /**
     * Specifies the factory for building DOM documents.
     * @param factory    the document factory
     */
    public void setFactory(DocumentBuilderFactory factory) {
        this.factory = factory;
    }
    protected DocumentBuilderFactory makeDocumentBuilderFactory() throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setValidating(false);
        // TODO: XInclude

        return factory;
    }
    @Override
    protected void receiveContent(InputStream content) {
        if (content == null) {
            this.content = null;
            return;
        }

        try {
            if (logger.isInfoEnabled())
                logger.info("Parsing DOM document from input stream");

            DocumentBuilderFactory factory = getFactory();
            if (factory == null) {
                throw new MarkLogicInternalException("Failed to make DOM document builder factory");
            }

            DOMImplementationLS domImpl = (DOMImplementationLS) factory.newDocumentBuilder().getDOMImplementation();

            LSParser parser = domImpl.createLSParser(DOMImplementationLS.MODE_SYNCHRONOUS, null);
//            if (resolver != null) {
//                parser.getDomConfig().setParameter("resource-resolver", resolver);
//            }

            LSInput domInput = domImpl.createLSInput();
            domInput.setEncoding("UTF-8");
            domInput.setByteStream(content);

            this.content = (T) parser.parse(domInput);
            content.close();
        } catch (IOException e) {
            logger.error("Failed to parse DOM document from input stream",e);
            throw new MarkLogicInternalException(e);
        } catch (ParserConfigurationException e) {
            logger.error("Failed to parse DOM document from input stream",e);
            throw new MarkLogicInternalException(e);
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
            if (logger.isInfoEnabled())
                logger.info("Serializing DOM document to output stream");

            DocumentBuilderFactory factory = getFactory();
            if (factory == null) {
                throw new MarkLogicInternalException("Failed to make DOM document builder factory");
            }

            DOMImplementationLS domImpl = (DOMImplementationLS) factory.newDocumentBuilder().getDOMImplementation();
            LSOutput domOutput = domImpl.createLSOutput();
            domOutput.setEncoding("UTF-8");
            domOutput.setByteStream(out);
            domImpl.createLSSerializer().write((Node) content, domOutput);
        } catch (DOMException e) {
            logger.error("Failed to serialize DOM document to output stream",e);
            throw new MarkLogicInternalException(e);
        } catch (LSException e) {
            logger.error("Failed to serialize DOM document to output stream",e);
            throw new MarkLogicInternalException(e);
        } catch (ParserConfigurationException e) {
            logger.error("Failed to serialize DOM document to output stream",e);
            throw new MarkLogicInternalException(e);
        }
    }

}


