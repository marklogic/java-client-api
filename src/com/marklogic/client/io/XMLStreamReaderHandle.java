package com.marklogic.client.io;

import javax.xml.stream.XMLStreamReader;
import com.marklogic.client.abstractio.XMLReadHandle;

public interface XMLStreamReaderHandle extends XMLReadHandle {
    public XMLStreamReader get();
}