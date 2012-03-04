package com.marklogic.client.io;

import com.marklogic.client.abstractio.BinaryReadWriteHandle;
import com.marklogic.client.abstractio.JSONReadWriteHandle;
import com.marklogic.client.abstractio.GenericReadWriteHandle;
import com.marklogic.client.abstractio.TextReadWriteHandle;
import com.marklogic.client.abstractio.XMLReadWriteHandle;

public interface BytesHandle extends GenericReadWriteHandle, BinaryReadWriteHandle, JSONReadWriteHandle, TextReadWriteHandle, XMLReadWriteHandle {
    public byte[] get();
    public BytesHandle on(byte[] content);
}
