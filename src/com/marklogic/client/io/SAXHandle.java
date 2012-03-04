package com.marklogic.client.io;

import org.xml.sax.ContentHandler;

import com.marklogic.client.abstractio.XMLReadHandle;

public interface SAXHandle extends XMLReadHandle {
    public void get(ContentHandler handler);
}
