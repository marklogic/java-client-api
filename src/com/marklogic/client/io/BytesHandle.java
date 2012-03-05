package com.marklogic.client.io;

import com.marklogic.client.abstractio.BinaryReadWriteHandle;
import com.marklogic.client.abstractio.JSONReadWriteHandle;
import com.marklogic.client.abstractio.GenericReadWriteHandle;
import com.marklogic.client.abstractio.TextReadWriteHandle;
import com.marklogic.client.abstractio.XMLReadWriteHandle;

public interface BytesHandle extends GenericReadWriteHandle<byte[]>, BinaryReadWriteHandle<byte[]>, JSONReadWriteHandle<byte[]>, TextReadWriteHandle<byte[]>, XMLReadWriteHandle<byte[]> {
    public BytesHandle on(byte[] content);
}
