package com.marklogic.client.io;

import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;

import com.marklogic.client.abstractio.XMLReadHandle;

public interface InputSourceHandle extends XMLReadHandle<InputSource> {
    public void process(ContentHandler handler);
}
