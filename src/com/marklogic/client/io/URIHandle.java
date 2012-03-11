package com.marklogic.client.io;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

import com.marklogic.client.docio.BinaryReadHandle;
import com.marklogic.client.docio.BinaryWriteHandle;
import com.marklogic.client.docio.GenericReadHandle;
import com.marklogic.client.docio.GenericWriteHandle;
import com.marklogic.client.docio.JSONReadHandle;
import com.marklogic.client.docio.JSONWriteHandle;
import com.marklogic.client.docio.TextReadHandle;
import com.marklogic.client.docio.TextWriteHandle;
import com.marklogic.client.docio.XMLReadHandle;
import com.marklogic.client.docio.XMLWriteHandle;

public class URIHandle
implements BinaryReadHandle<InputStream>, BinaryWriteHandle<InputStream>,
    GenericReadHandle<InputStream>, GenericWriteHandle<InputStream>, JSONReadHandle<InputStream>, JSONWriteHandle<InputStream>, 
    TextReadHandle<InputStream>, TextWriteHandle<InputStream>, XMLReadHandle<InputStream>, XMLWriteHandle<InputStream>
{
	private static final int BUFFER_SIZE = 1024;

	public URIHandle(String uri) {
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

	public Class<InputStream> receiveAs() {
		return InputStream.class;
	}
	public void receiveContent(InputStream content) {
		try {
			OutputStream out = new BufferedOutputStream(new URI(uri).toURL().openConnection().getOutputStream());
			byte[] buf = new byte[BUFFER_SIZE];
			int len = 0;
			while ((len = content.read(buf, 0, BUFFER_SIZE)) != -1) {
				out.write(buf, 0, len);
			}
			content.close();
			out.close();
		} catch (MalformedURLException e) {
			// TODO: log exception
			throw new RuntimeException(e);
		} catch (IOException e) {
			// TODO: log exception
			throw new RuntimeException(e);
		} catch (URISyntaxException e) {
			// TODO: log exception
			throw new RuntimeException(e);
		}
	}
	public InputStream sendContent() {
		try {
			return new URI(uri).toURL().openStream();
		} catch (MalformedURLException e) {
			// TODO: log exception
			throw new RuntimeException(e);
		} catch (IOException e) {
			// TODO: log exception
			throw new RuntimeException(e);
		} catch (URISyntaxException e) {
			// TODO: log exception
			throw new RuntimeException(e);
		}
	}
}
