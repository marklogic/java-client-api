package com.marklogic.client;

import com.marklogic.client.abstractio.TextContentHandle;
import com.marklogic.client.abstractio.TextReadHandle;
import com.marklogic.client.abstractio.TextWriteHandle;

public interface TextDocument extends AbstractDocument<TextContentHandle, TextReadHandle, TextWriteHandle> {

}
