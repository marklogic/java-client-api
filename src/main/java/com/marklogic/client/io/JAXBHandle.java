package com.marklogic.client.io;

import javax.xml.bind.JAXBContext;

import com.marklogic.client.Format;
import com.marklogic.client.io.marker.XMLReadHandle;
import com.marklogic.client.io.marker.XMLWriteHandle;

// TODO: sender / receiver

public class JAXBHandle implements XMLReadHandle<Object>, XMLWriteHandle<Object> {
	private JAXBContext context;
	private Object      content;

	public JAXBHandle(JAXBContext context) {
		this.context = context;
	}

	public Object get() {
		return content;
	}
    public void set(Object content) {
    	this.content = content;
    }
    public JAXBHandle with(Object content) {
    	set(content);
    	return this;
    }

	public Format getFormat() {
		return Format.XML;
	}
	public void setFormat(Format format) {
		if (format != Format.XML)
			new RuntimeException("JAXBHandle supports the XML format only");
	}
	public JAXBHandle withFormat(Format format) {
		setFormat(format);
		return this;
	}

    public Class<Object> receiveAs() {
		return Object.class;
	}
	public void receiveContent(Object content) {
		this.content = content;
	}
	public Object sendContent() {
		if (content == null) {
			throw new RuntimeException("No object to write");
		}

		return content;
	}
}
