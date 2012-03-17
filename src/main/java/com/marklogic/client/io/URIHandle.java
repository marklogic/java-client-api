package com.marklogic.client.io;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.marklogic.client.Format;
import com.marklogic.client.docio.BinaryReadHandle;
import com.marklogic.client.docio.BinaryWriteHandle;
import com.marklogic.client.docio.GenericReadHandle;
import com.marklogic.client.docio.GenericWriteHandle;
import com.marklogic.client.docio.JSONReadHandle;
import com.marklogic.client.docio.JSONWriteHandle;
import com.marklogic.client.docio.StructureReadHandle;
import com.marklogic.client.docio.StructureWriteHandle;
import com.marklogic.client.docio.TextReadHandle;
import com.marklogic.client.docio.TextWriteHandle;
import com.marklogic.client.docio.XMLReadHandle;
import com.marklogic.client.docio.XMLWriteHandle;

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

	public URIHandle() {
		super();
	}
	public URIHandle(String uri) {
		this();
		set(uri);
	}

	private String uri;
	public String get() {
		return uri;
	}
	public void set(String uri) {
		this.uri = uri;
	}
	public URIHandle on(String uri) {
		set(uri);
		return this;
	}

	private Format format = Format.XML;
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
		try {
			logger.info("Updating URI with content read from database");

			OutputStream out = new BufferedOutputStream(new URI(uri).toURL().openConnection().getOutputStream());
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
		} catch (URISyntaxException e) {
			logger.error("Failed to update URI with content read from database",e);
			throw new RuntimeException(e);
		}
	}
	public InputStream sendContent() {
		try {
			logger.info("Retrieving content from URI to write to database");

			return new URI(uri).toURL().openStream();
		} catch (MalformedURLException e) {
			logger.error("Failed to retrieving content from URI to write to database",e);
			throw new RuntimeException(e);
		} catch (IOException e) {
			logger.error("Failed to retrieving content from URI to write to database",e);
			throw new RuntimeException(e);
		} catch (URISyntaxException e) {
			logger.error("Failed to retrieving content from URI to write to database",e);
			throw new RuntimeException(e);
		}
	}
}
