package com.marklogic.client.io;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;

import com.marklogic.client.docio.XMLReadHandle;

public class SourceHandle implements XMLReadHandle<Source> {
	public SourceHandle() {
	}

	private Source content;
	public Source get() {
		return content;
	}
	public void process(Transformer transformer, Result result) {
		try {
			transformer.transform(content, result);
		} catch (TransformerException e) {
			// TODO: log exception
			throw new RuntimeException(e);
		}
	}

	public Class<Source> receiveAs() {
		return Source.class;
	}
	public void receiveContent(Source content) {
		this.content = content;
	}
}
