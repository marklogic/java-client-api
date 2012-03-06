package com.marklogic.client.io;

import javax.xml.bind.JAXBContext;

import com.marklogic.client.docio.XMLReadHandle;
import com.marklogic.client.docio.XMLWriteHandle;

// TODO: sender / receiver

public class JAXBHandle implements XMLReadHandle<Object>, XMLWriteHandle<Object> {
	private JAXBContext context;
	public JAXBHandle(JAXBContext context) {
		this.context = context;
	}

	private Object content;
	public Object get() {
		return content;
	}
    public void set(Object content) {
    	this.content = content;
    }

    public Class<Object> receiveAs() {
		return Object.class;
	}
	public void receiveContent(Object content) {
		this.content = content;
	}
	public Object sendContent() {
		return content;
	}
}
