package com.marklogic.client.io;

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

public class BytesHandle
	implements
		BinaryReadHandle<byte[]>, BinaryWriteHandle<byte[]>,
		GenericReadHandle<byte[]>, GenericWriteHandle<byte[]>,
		JSONReadHandle<byte[]>, JSONWriteHandle<byte[]>, 
		TextReadHandle<byte[]>, TextWriteHandle<byte[]>,
		XMLReadHandle<byte[]>, XMLWriteHandle<byte[]>
{
	public BytesHandle() {
	}

	private byte[] content;
	public byte[] get() {
		return content;
	}
	public void set(byte[] content) {
		this.content = content;
	}

	public Class<byte[]> receiveAs() {
		return byte[].class;
	}
	public void receiveContent(byte[] content) {
		this.content = content;
	}
	public byte[] sendContent() {
		return content;
	}
}
