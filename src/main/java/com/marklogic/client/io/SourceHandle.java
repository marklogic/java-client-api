package com.marklogic.client.io;

import java.io.InputStream;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.marklogic.client.docio.XMLReadHandle;

public class SourceHandle implements XMLReadHandle<InputStream> {
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

	public Class<InputStream> receiveAs() {
		return InputStream.class;
	}
	public void receiveContent(InputStream content) {
		this.content = new StreamSource(content);
	}
}
