package com.marklogic.client.io;

import java.io.File;

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
 * A File Handle represents document content as a file for reading or writing.
 */
public class FileHandle
	implements
		BinaryReadHandle<File>, BinaryWriteHandle<File>,
		GenericReadHandle<File>, GenericWriteHandle<File>,
		JSONReadHandle<File>, JSONWriteHandle<File>, 
		TextReadHandle<File>, TextWriteHandle<File>,
		XMLReadHandle<File>, XMLWriteHandle<File>,
		StructureReadHandle<File>, StructureWriteHandle<File>
{
	private File   content;
	private Format format = Format.XML;

	public FileHandle() {
		super();
	}
	public FileHandle(File content) {
		this();
		set(content);
	}

	public File get() {
		return content;
	}
	public void set(File content) {
		this.content = content;
	}
	public FileHandle with(File content) {
		set(content);
    	return this;
	}

	public Format getFormat() {
		return format;
	}
	public void setFormat(Format format) {
		this.format = format;
	}
	public FileHandle withFormat(Format format) {
		setFormat(format);
		return this;
	}

	public Class<File> receiveAs() {
		return File.class;
	}
	public void receiveContent(File content) {
		this.content = content;
	}
	public File sendContent() {
		if (content == null) {
			throw new RuntimeException("No file to write");
		}

		return content;
	}
}
