package com.marklogic.client.io;

import javax.xml.stream.XMLEventReader;

import com.marklogic.client.abstractio.XMLReadHandle;

public interface XMLEventReaderHandle extends XMLReadHandle {
    public XMLEventReader get();
}