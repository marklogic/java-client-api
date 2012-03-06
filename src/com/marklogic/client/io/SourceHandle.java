package com.marklogic.client.io;

import javax.xml.transform.Source;

import com.marklogic.client.docio.XMLReadHandle;

public class SourceHandle implements XMLReadHandle<Source> {
	public SourceHandle() {
	}

	private Source content;
	public Source get() {
		return content;
	}

	public Class<Source> receiveAs() {
		return Source.class;
	}
	public void receiveContent(Source content) {
		this.content = content;
	}
}
