package com.marklogic.client.io;

import java.io.InputStream;

import com.marklogic.client.abstractio.BinaryReadWriteHandle;
import com.marklogic.client.abstractio.JSONReadWriteHandle;
import com.marklogic.client.abstractio.GenericReadWriteHandle;
import com.marklogic.client.abstractio.TextReadWriteHandle;
import com.marklogic.client.abstractio.XMLReadWriteHandle;

public interface InputStreamHandle extends GenericReadWriteHandle<InputStream>, BinaryReadWriteHandle<InputStream>, JSONReadWriteHandle<InputStream>, TextReadWriteHandle<InputStream>, XMLReadWriteHandle<InputStream> {
    public InputStreamHandle on(InputStream content);
}
