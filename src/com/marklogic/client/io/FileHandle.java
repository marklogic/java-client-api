package com.marklogic.client.io;

import java.io.File;

import com.marklogic.client.abstractio.BinaryReadWriteHandle;
import com.marklogic.client.abstractio.JSONReadWriteHandle;
import com.marklogic.client.abstractio.GenericReadWriteHandle;
import com.marklogic.client.abstractio.TextReadWriteHandle;
import com.marklogic.client.abstractio.XMLReadWriteHandle;

public interface FileHandle extends GenericReadWriteHandle<File>, BinaryReadWriteHandle<File>, JSONReadWriteHandle<File>, TextReadWriteHandle<File>, XMLReadWriteHandle<File> {
    public FileHandle on(File content);
}
