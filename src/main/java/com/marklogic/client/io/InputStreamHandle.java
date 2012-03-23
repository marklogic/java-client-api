package com.marklogic.client.io;

import java.io.InputStream;

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
 * An Input Stream Handle represents document content as an input stream for reading or writing.
 */
public class InputStreamHandle
	implements
		BinaryReadHandle<InputStream>, BinaryWriteHandle<InputStream>,
		GenericReadHandle<InputStream>, GenericWriteHandle<InputStream>,
		JSONReadHandle<InputStream>, JSONWriteHandle<InputStream>, 
		TextReadHandle<InputStream>, TextWriteHandle<InputStream>,
		XMLReadHandle<InputStream>, XMLWriteHandle<InputStream>,
		StructureReadHandle<InputStream>, StructureWriteHandle<InputStream>
{
	private InputStream content;
	private Format      format = Format.XML;

	public InputStreamHandle() {
		super();
	}
	public InputStreamHandle(InputStream content) {
		this();
		set(content);
	}

	public InputStream get() {
		return content;
	}
	public void set(InputStream content) {
		this.content = content;
	}
	public InputStreamHandle with(InputStream content) {
		set(content);
		return this;
	}

	public Format getFormat() {
		return format;
	}
	public void setFormat(Format format) {
		this.format = format;
	}
	public InputStreamHandle withFormat(Format format) {
		setFormat(format);
		return this;
	}

	public Class<InputStream> receiveAs() {
		return InputStream.class;
	}
	public void receiveContent(InputStream content) {
		this.content = content;
	}
	public InputStream sendContent() {
		if (content == null) {
			throw new IllegalStateException("No stream to write");
		}

		return content;
	}
}
