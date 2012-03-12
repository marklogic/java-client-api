package com.marklogic.client.io;

import java.io.File;

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

public class FileHandle
	implements
		BinaryReadHandle<File>, BinaryWriteHandle<File>,
		GenericReadHandle<File>, GenericWriteHandle<File>,
		JSONReadHandle<File>, JSONWriteHandle<File>, 
		TextReadHandle<File>, TextWriteHandle<File>,
		XMLReadHandle<File>, XMLWriteHandle<File>
{
	public FileHandle() {
	}

	private File content;
	public File get() {
		return content;
	}
	public void set(File content) {
		this.content = content;
	}
	public FileHandle on(File content) {
		set(content);
    	return this;
	}

	public Class<File> receiveAs() {
		return File.class;
	}
	public void receiveContent(File content) {
		this.content = content;
	}
	public File sendContent() {
		return content;
	}
}
