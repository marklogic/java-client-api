package com.marklogic.client.io;

import java.io.Reader;

import com.marklogic.client.abstractio.JSONReadWriteHandle;
import com.marklogic.client.abstractio.TextReadWriteHandle;
import com.marklogic.client.abstractio.XMLReadWriteHandle;

public interface ReaderHandle extends JSONReadWriteHandle, TextReadWriteHandle, XMLReadWriteHandle {
    public Reader get();
    public ReaderHandle on(Reader content);
}
