package com.marklogic.client.io;

import javax.xml.transform.Source;

import com.marklogic.client.abstractio.XMLReadHandle;

public interface SourceHandle extends XMLReadHandle {
    public Source get();
}
