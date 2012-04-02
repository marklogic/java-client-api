/*
 * Copyright 2012 MarkLogic Corporation
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
package com.marklogic.client.example.handle;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.marklogic.client.Format;
import com.marklogic.client.MarkLogicIOException;
import com.marklogic.client.io.marker.BinaryReadHandle;
import com.marklogic.client.io.marker.BinaryWriteHandle;
import com.marklogic.client.io.marker.GenericReadHandle;
import com.marklogic.client.io.marker.GenericWriteHandle;
import com.marklogic.client.io.marker.JSONReadHandle;
import com.marklogic.client.io.marker.JSONWriteHandle;
import com.marklogic.client.io.marker.StructureReadHandle;
import com.marklogic.client.io.marker.StructureWriteHandle;
import com.marklogic.client.io.marker.TextReadHandle;
import com.marklogic.client.io.marker.TextWriteHandle;
import com.marklogic.client.io.marker.XMLReadHandle;
import com.marklogic.client.io.marker.XMLWriteHandle;

/**
 * A URI Handle sends read document content to a URI or
 * receives written database content from a URI.
 */
public class URIHandle
implements BinaryReadHandle<InputStream>, BinaryWriteHandle<InputStream>,
    GenericReadHandle<InputStream>, GenericWriteHandle<InputStream>,
    JSONReadHandle<InputStream>, JSONWriteHandle<InputStream>, 
    TextReadHandle<InputStream>, TextWriteHandle<InputStream>,
    XMLReadHandle<InputStream>, XMLWriteHandle<InputStream>,
	StructureReadHandle<InputStream>, StructureWriteHandle<InputStream>
{
    /**
     * A ConnectionMaker creates and configures a connection (for instance,
     * providing authentication) for reading input or writing output.
     */
	public interface ConnectionMaker {
    	public URLConnection makeInputConnection(URI uri);
    	public URLConnection makeOutputConnection(URI uri);
    }

    static final private Logger logger = LoggerFactory.getLogger(URIHandle.class);

	static final private int BUFFER_SIZE = 1024;

	private URI             uri;
	private Format          format = Format.XML;
	private ConnectionMaker connectionMaker;

	public URIHandle() {
		super();
	}
	public URIHandle(URI uri) {
		this();
		set(uri);
	}

	public URI get() {
		return uri;
	}
	public void set(URI uri) {
		this.uri = uri;
	}
	public URIHandle with(URI uri) {
		set(uri);
		return this;
	}

	public Format getFormat() {
		return format;
	}
	public void setFormat(Format format) {
		this.format = format;
	}
	public URIHandle withFormat(Format format) {
		setFormat(format);
		return this;
	}

	public ConnectionMaker getConnectionMaker() {
		return connectionMaker;
	}
	public void setConnectionMaker(ConnectionMaker connectionMaker) {
		this.connectionMaker = connectionMaker;
	}

	public Class<InputStream> receiveAs() {
		return InputStream.class;
	}
	public void receiveContent(InputStream content) {
		if (content == null) {
			return;
		}

		try {
			logger.info("Updating URI with content read from database");

			URI uri = get();
			if (uri == null) {
				throw new IllegalStateException("No uri for output");
			}

			URLConnection connection = null;
			if (connectionMaker != null) {
				connection = connectionMaker.makeOutputConnection(uri);
			} else {
				URL url = uri.toURL();
				if (url == null) {
					throw new MarkLogicIOException("Could not create URL for output to "+uri.toString());
				}

				connection = url.openConnection();
			}
			if (connection == null) {
				throw new MarkLogicIOException("Could not open connection to write for "+uri.toString());
			}

			OutputStream out = new BufferedOutputStream(connection.getOutputStream());
			byte[] buf = new byte[BUFFER_SIZE];
			int len = 0;
			while ((len = content.read(buf, 0, BUFFER_SIZE)) != -1) {
				out.write(buf, 0, len);
			}
			content.close();
			out.close();
		} catch (MalformedURLException e) {
			logger.error("Failed to update URI with content read from database",e);
			throw new IllegalStateException(e);
		} catch (IOException e) {
			logger.error("Failed to update URI with content read from database",e);
			throw new MarkLogicIOException(e);
		}
	}
	public InputStream sendContent() {
		try {
			logger.info("Retrieving content from URI to write to database");

			URI uri = get();
			if (uri == null) {
				throw new IllegalStateException("No uri for input");
			}

			URLConnection connection = null;
			if (connectionMaker != null) {
				connection = connectionMaker.makeInputConnection(uri);
			} else {
				URL url = uri.toURL();
				if (url == null) {
					throw new MarkLogicIOException("Could not create URL for input from "+uri.toString());
				}

				connection = url.openConnection();
			}
			if (connection == null) {
				throw new MarkLogicIOException("Could not open connection to write for "+uri.toString());
			}

			InputStream stream = connection.getInputStream();
			if (stream == null) {
				throw new MarkLogicIOException("Could not get stream to write for "+uri.toString());
			}

			return stream;
		} catch (MalformedURLException e) {
			logger.error("Failed to retrieving content from URI to write to database",e);
			throw new IllegalStateException(e);
		} catch (IOException e) {
			logger.error("Failed to retrieving content from URI to write to database",e);
			throw new MarkLogicIOException(e);
		}
	}
}
