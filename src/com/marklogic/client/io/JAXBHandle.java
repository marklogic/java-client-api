package com.marklogic.client.io;

import javax.xml.bind.JAXBContext;

import com.marklogic.client.abstractio.XMLReadWriteHandle;

public interface JAXBHandle extends XMLReadWriteHandle<Object> {
	public Object get(JAXBContext context);
    public JAXBHandle on(JAXBContext context, Object content);
}
