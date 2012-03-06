package com.marklogic.client.io;

import java.io.InputStream;

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

public class InputStreamHandle
	implements
		BinaryReadHandle<InputStream>, BinaryWriteHandle<InputStream>,
		GenericReadHandle<InputStream>, GenericWriteHandle<InputStream>,
		JSONReadHandle<InputStream>, JSONWriteHandle<InputStream>, 
		TextReadHandle<InputStream>, TextWriteHandle<InputStream>,
		XMLReadHandle<InputStream>, XMLWriteHandle<InputStream>
{
	public InputStreamHandle() {
	}

	private InputStream content;
	public InputStream get() {
		return content;
	}
	public void set(InputStream content) {
		this.content = content;
	}

	public Class<InputStream> receiveAs() {
		return InputStream.class;
	}
	public void receiveContent(InputStream content) {
		this.content = content;
	}
	public InputStream sendContent() {
		return content;
	}
}
