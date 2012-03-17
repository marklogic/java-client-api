package com.marklogic.client.io;

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
 * A Bytes Handle represents document content as a byte array for reading or writing.
 */
public class BytesHandle
	implements
		BinaryReadHandle<byte[]>, BinaryWriteHandle<byte[]>,
		GenericReadHandle<byte[]>, GenericWriteHandle<byte[]>,
		JSONReadHandle<byte[]>, JSONWriteHandle<byte[]>, 
		TextReadHandle<byte[]>, TextWriteHandle<byte[]>,
		XMLReadHandle<byte[]>, XMLWriteHandle<byte[]>,
		StructureReadHandle<byte[]>, StructureWriteHandle<byte[]>
{
	public BytesHandle() {
		super();
	}
	public BytesHandle(byte[] content) {
		this();
		set(content);
	}

	private byte[] content;
	public byte[] get() {
		return content;
	}
	public void set(byte[] content) {
		this.content = content;
	}
	public BytesHandle on(byte[] content) {
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
