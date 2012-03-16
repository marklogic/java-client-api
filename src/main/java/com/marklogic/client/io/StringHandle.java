package com.marklogic.client.io;

import com.marklogic.client.Format;
import com.marklogic.client.docio.JSONReadHandle;
import com.marklogic.client.docio.JSONWriteHandle;
import com.marklogic.client.docio.StructureReadHandle;
import com.marklogic.client.docio.StructureWriteHandle;
import com.marklogic.client.docio.TextReadHandle;
import com.marklogic.client.docio.TextWriteHandle;
import com.marklogic.client.docio.XMLReadHandle;
import com.marklogic.client.docio.XMLWriteHandle;

public class StringHandle
	implements
		JSONReadHandle<String>, JSONWriteHandle<String>, 
		TextReadHandle<String>, TextWriteHandle<String>,
		XMLReadHandle<String>, XMLWriteHandle<String>,
		StructureReadHandle<String>, StructureWriteHandle<String>
{
	public StringHandle() {
		super();
	}
	public StringHandle(String content) {
		this();
		set(content);
	}

	private String content;
	public String get() {
		return content;
	}
	public void set(String content) {
		this.content = content;
	}
	public StringHandle on(String content) {
		set(content);
		return this;
	}

	private Format format = Format.XML;
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
		return content;
	}
}
