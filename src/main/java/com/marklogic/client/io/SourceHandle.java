package com.marklogic.client.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.marklogic.client.Format;
import com.marklogic.client.MarkLogicIOException;
import com.marklogic.client.io.marker.OutputStreamSender;
import com.marklogic.client.io.marker.StructureReadHandle;
import com.marklogic.client.io.marker.StructureWriteHandle;
import com.marklogic.client.io.marker.XMLReadHandle;
import com.marklogic.client.io.marker.XMLWriteHandle;

/**
 * A Source Handle represents XML content as a transform source for reading
 * or transforms a source into a result for writing.
 */
public class SourceHandle
	implements OutputStreamSender,
	    XMLReadHandle<InputStream>, XMLWriteHandle<OutputStreamSender>, 
	    StructureReadHandle<InputStream>, StructureWriteHandle<OutputStreamSender>
{
	static final private Logger logger = LoggerFactory.getLogger(SourceHandle.class);

	private Transformer transformer;
	private Source      content;

	public SourceHandle() {
	}

	public Transformer getTransformer() {
		return transformer;
	}
	public void setTransformer(Transformer transformer) {
		this.transformer = transformer;
	}

	public Source get() {
		return content;
	}
	public void set(Source content) {
		this.content = content;
	}
	public SourceHandle with(Source content) {
		set(content);
		return this;
	}
	public void transform(Result result) {
		logger.info("Transforming source into result");
		try {
			if (content == null) {
				throw new IllegalStateException("No source to transform");
			}

			Transformer transformer = null;
			if (this.transformer != null) {
				transformer = getTransformer();
			} else {
				logger.warn("No transformer, so using identity transform");
				transformer = TransformerFactory.newInstance().newTransformer();
			}

			transformer.transform(content, result);
		} catch (TransformerException e) {
			logger.error("Failed to transform source into result",e);
			throw new MarkLogicIOException(e);
		}
	}

	public Format getFormat() {
		return Format.XML;
	}
	public void setFormat(Format format) {
		if (format != Format.XML)
			new IllegalArgumentException("SourceHandle supports the XML format only");
	}
	public SourceHandle withFormat(Format format) {
		setFormat(format);
		return this;
	}

	public Class<InputStream> receiveAs() {
		return InputStream.class;
	}
	public void receiveContent(InputStream content) {
		if (content == null) {
			this.content = null;
			return;
		}

		this.content = new StreamSource(content);
	}
	public OutputStreamSender sendContent() {
		return this;
	}
	public void write(OutputStream out) throws IOException {
		transform(new StreamResult(out));
	}
}
