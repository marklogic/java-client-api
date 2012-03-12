package com.marklogic.client.io;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.marklogic.client.docio.XMLReadHandle;

public class SourceHandle implements XMLReadHandle<Source> {
	static final private Logger logger = LoggerFactory.getLogger(SourceHandle.class);

	public SourceHandle() {
	}

	private Source content;
	public Source get() {
		return content;
	}
	public void process(Transformer transformer, Result result) {
		try {
			logger.info("Transforming source into result");

			transformer.transform(content, result);
		} catch (TransformerException e) {
			logger.error("Failed to transform source into result",e);
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
