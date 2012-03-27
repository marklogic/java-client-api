package com.marklogic.client.example.handle;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

import com.marklogic.client.Format;
import com.marklogic.client.io.OutputStreamSender;
import com.marklogic.client.io.marker.StructureReadHandle;
import com.marklogic.client.io.marker.StructureWriteHandle;
import com.marklogic.client.io.marker.XMLReadHandle;
import com.marklogic.client.io.marker.XMLWriteHandle;

public class JDOMHandle
    implements OutputStreamSender,
    	XMLReadHandle<InputStream>, XMLWriteHandle<OutputStreamSender>,
    	StructureReadHandle<InputStream>, StructureWriteHandle<OutputStreamSender>
{
	private Document     content;
	private SAXBuilder   builder;
	private XMLOutputter outputter;

	public JDOMHandle() {
		super();
	}
	public JDOMHandle(Document content) {
		this();
    	set(content);
	}

	public SAXBuilder getBuilder() {
		if (builder == null)
			builder = makeBuilder();
		return builder;
	}
	public void setBuilder(SAXBuilder builder) {
		this.builder = builder;
	}
	protected SAXBuilder makeBuilder() {
		return new SAXBuilder(false);
	}

	public XMLOutputter getOutputter() {
		if (outputter == null)
			outputter = makeOutputter();
		return outputter;
	}
	public void setOutputter(XMLOutputter outputter) {
		this.outputter = outputter;
	}
	protected XMLOutputter makeOutputter() {
		return new XMLOutputter();
	}

	public Document get() {
		return content;
	}
    public void set(Document content) {
    	this.content = content;
    }
    public JDOMHandle with(Document content) {
    	set(content);
    	return this;
    }

    public Format getFormat() {
		return Format.XML;
	}
	public void setFormat(Format format) {
		if (format != Format.XML)
			new IllegalArgumentException("JDOMHandle supports the XML format only");
	}
	public JDOMHandle withFormat(Format format) {
		setFormat(format);
		return this;
	}

	@Override
	public Class<InputStream> receiveAs() {
		return InputStream.class;
	}
	@Override
	public void receiveContent(InputStream content) {
		if (content == null)
			return;

		try {
			this.content = getBuilder().build(content);
		} catch (JDOMException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public OutputStreamSender sendContent() {
		if (content == null) {
			throw new IllegalStateException("No document to write");
		}

		return this;
	}
	@Override
	public void write(OutputStream out) throws IOException {
		getOutputter().output(content, out);
	}

}
