package com.marklogic.client.io;

import com.marklogic.client.abstractio.JSONReadWriteHandle;
import com.marklogic.client.abstractio.TextReadWriteHandle;
import com.marklogic.client.abstractio.XMLReadWriteHandle;

public interface StringHandle extends JSONReadWriteHandle<String>, TextReadWriteHandle<String>, XMLReadWriteHandle<String> {
    public StringHandle on(String content);
}
