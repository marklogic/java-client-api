package com.marklogic.client.io;

import java.io.File;

import com.marklogic.client.abstractio.BinaryReadWriteHandle;
import com.marklogic.client.abstractio.JSONReadWriteHandle;
import com.marklogic.client.abstractio.GenericReadWriteHandle;
import com.marklogic.client.abstractio.TextReadWriteHandle;
import com.marklogic.client.abstractio.XMLReadWriteHandle;

public interface FileHandle extends GenericReadWriteHandle, BinaryReadWriteHandle, JSONReadWriteHandle, TextReadWriteHandle, XMLReadWriteHandle {
	public File get();
    public FileHandle on(File content);
}
