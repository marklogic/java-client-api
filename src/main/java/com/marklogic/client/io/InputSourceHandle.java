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
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.Schema;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ContentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

import com.marklogic.client.MarkLogicIOException;
import com.marklogic.client.MarkLogicInternalException;
import com.marklogic.client.io.marker.BufferableHandle;
import com.marklogic.client.io.marker.ContentHandle;
import com.marklogic.client.io.marker.ContentHandleFactory;
import com.marklogic.client.io.marker.StructureReadHandle;
import com.marklogic.client.io.marker.StructureWriteHandle;
import com.marklogic.client.io.marker.XMLReadHandle;
import com.marklogic.client.io.marker.XMLWriteHandle;

/**
 * An Input Source Handle represents XML content as an input source for reading or writing.
 * When reading, the XML may be processed by a SAX content handler.
 */
public class InputSourceHandle
	extends BaseHandle<InputStream, OutputStreamSender>
	implements OutputStreamSender, BufferableHandle, ContentHandle<InputSource>,
	    XMLReadHandle, XMLWriteHandle,
	    StructureReadHandle, StructureWriteHandle
{
	static final private Logger logger = LoggerFactory.getLogger(InputSourceHandle.class);

	private EntityResolver   resolver;
	private ErrorHandler     errorHandler;
	private Schema           defaultWriteSchema;
	private SAXParserFactory factory;
	private InputSource      content;

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
	/**
	 * Buffers the SAX input source and returns the buffer
	 * as an XML string.
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
	 * Returns the factory for parsing SAX events.
	 * @return	the SAX factory
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
		factory.setNamespaceAware(true);
		factory.setValidating(false);
		// TODO: XInclude

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
		try {
			if (content == null) {
				this.content = null;
				return;
			}

			this.content = new InputSource(new InputStreamReader(content, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			logger.error("Failed to read input stream as input source",e);
			throw new MarkLogicIOException(e);
		}
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
		try {
			TransformerFactory.newInstance().newTransformer().transform(
					new SAXSource(makeReader(true), content),
					new StreamResult(new OutputStreamWriter(out, "UTF-8"))
					);
		} catch (TransformerException e) {
			logger.error("Failed to transform input source into result",e);
			throw new MarkLogicIOException(e);
		}
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
}
