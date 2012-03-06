package com.marklogic.client.io;

import java.io.Reader;

import com.marklogic.client.docio.JSONReadHandle;
import com.marklogic.client.docio.JSONWriteHandle;
import com.marklogic.client.docio.TextReadHandle;
import com.marklogic.client.docio.TextWriteHandle;
import com.marklogic.client.docio.XMLReadHandle;
import com.marklogic.client.docio.XMLWriteHandle;

public class ReaderHandle
	implements
		JSONReadHandle<Reader>, JSONWriteHandle<Reader>, 
		TextReadHandle<Reader>, TextWriteHandle<Reader>,
		XMLReadHandle<Reader>, XMLWriteHandle<Reader>
{
    public ReaderHandle() {
    }
	
    private Reader content;
    public Reader get() {
    	return content;
    }
	public void set(Reader content) {
		this.content = content;
	}
	public ReaderHandle on(Reader content) {
		set(content);
		return this;
	}

	public Class<Reader> receiveAs() {
		return Reader.class;
	}
	public void receiveContent(Reader content) {
		this.content = content;
	}
	public Reader sendContent() {
		return content;
	}
}
