package com.marklogic.client.io;

import com.marklogic.client.Format;
import com.marklogic.client.io.marker.JSONReadHandle;
import com.marklogic.client.io.marker.JSONWriteHandle;
import com.marklogic.client.io.marker.StructureReadHandle;
import com.marklogic.client.io.marker.StructureWriteHandle;
import com.marklogic.client.io.marker.TextReadHandle;
import com.marklogic.client.io.marker.TextWriteHandle;
import com.marklogic.client.io.marker.XMLReadHandle;
import com.marklogic.client.io.marker.XMLWriteHandle;

/**
 * A String Handle represents document content as a string for reading or writing.
 */
public class StringHandle
	implements
		JSONReadHandle<String>, JSONWriteHandle<String>, 
		TextReadHandle<String>, TextWriteHandle<String>,
		XMLReadHandle<String>, XMLWriteHandle<String>,
		StructureReadHandle<String>, StructureWriteHandle<String>
{
	private String content;
	private Format format = Format.XML;

	public StringHandle() {
		super();
	}
	public StringHandle(String content) {
		this();
		set(content);
	}

	public String get() {
		return content;
	}
	public void set(String content) {
		this.content = content;
	}
	public StringHandle with(String content) {
		set(content);
		return this;
	}

	public Format getFormat() {
		return format;
	}
	public void setFormat(Format format) {
		this.format = format;
	}

	public Class<String> receiveAs() {
		return String.class;
	}
	public void receiveContent(String content) {
		this.content = content;
	}
	public String sendContent() {
		if (content == null) {
			throw new RuntimeException("No string to write");
		}

		return content;
	}
}
