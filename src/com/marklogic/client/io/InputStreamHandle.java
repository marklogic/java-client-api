package com.marklogic.client.io;

import java.io.InputStream;

import com.marklogic.client.abstractio.BinaryReadWriteHandle;
import com.marklogic.client.abstractio.JSONReadWriteHandle;
import com.marklogic.client.abstractio.GenericReadWriteHandle;
import com.marklogic.client.abstractio.TextReadWriteHandle;
import com.marklogic.client.abstractio.XMLReadWriteHandle;

public interface InputStreamHandle extends GenericReadWriteHandle, BinaryReadWriteHandle, JSONReadWriteHandle, TextReadWriteHandle, XMLReadWriteHandle {
	public InputStream get();
    public InputStreamHandle on(InputStream content);
}
