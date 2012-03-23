package com.marklogic.client.io;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.marklogic.client.Format;
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
	static final private Logger logger = LoggerFactory.getLogger(URIHandle.class);

	static final private int BUFFER_SIZE = 1024;

	private URI    uri;
	private Format format = Format.XML;

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
	public URIHandle on(URI uri) {
		set(uri);
		return this;
	}

	public Format getFormat() {
		return format;
	}
	public void setFormat(Format format) {
		this.format = format;
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

			OutputStream out = new BufferedOutputStream(get().toURL().openConnection().getOutputStream());
			byte[] buf = new byte[BUFFER_SIZE];
			int len = 0;
			while ((len = content.read(buf, 0, BUFFER_SIZE)) != -1) {
				out.write(buf, 0, len);
			}
			content.close();
			out.close();
		} catch (MalformedURLException e) {
			logger.error("Failed to update URI with content read from database",e);
			throw new RuntimeException(e);
		} catch (IOException e) {
			logger.error("Failed to update URI with content read from database",e);
			throw new RuntimeException(e);
		}
	}
	public InputStream sendContent() {
		try {
			logger.info("Retrieving content from URI to write to database");

			if (uri == null) {
				throw new RuntimeException("No uri to write");
			}

			InputStream stream = get().toURL().openStream();
			if (stream == null) {
				throw new RuntimeException("No stream to write");
			}

			return stream;
		} catch (MalformedURLException e) {
			logger.error("Failed to retrieving content from URI to write to database",e);
			throw new RuntimeException(e);
		} catch (IOException e) {
			logger.error("Failed to retrieving content from URI to write to database",e);
			throw new RuntimeException(e);
		}
	}
}
