package com.marklogic.client.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.marklogic.client.Format;
import com.marklogic.client.io.marker.BinaryWriteHandle;
import com.marklogic.client.io.marker.GenericWriteHandle;
import com.marklogic.client.io.marker.JSONWriteHandle;
import com.marklogic.client.io.marker.OutputStreamSender;
import com.marklogic.client.io.marker.StructureWriteHandle;
import com.marklogic.client.io.marker.TextWriteHandle;
import com.marklogic.client.io.marker.XMLWriteHandle;

/**
 * An OutputStreamHandle generates output during writing.
 * 
 * You define a class that implements the OutputStreamSender interface.
 * When the client is ready to write to the server, it calls the write()
 * method of the class with an OutputStream.  That is, the write() method
 * generates the output to be written to the server.  The OutputStreamSender
 * implementer is typically an anonymous class.
 * 
 * Initialize the OutputStreamHandle with the OutputStreamSender implementer
 * before passing the OutputStreamHandle to the write() method of a document
 * manager.
 *
 */
public class OutputStreamHandle
    implements
	    BinaryWriteHandle<OutputStreamSender>,
        GenericWriteHandle<OutputStreamSender>,
        JSONWriteHandle<OutputStreamSender>, 
        TextWriteHandle<OutputStreamSender>,
        XMLWriteHandle<OutputStreamSender>,
        StructureWriteHandle<OutputStreamSender>
{
	static final private Logger logger = LoggerFactory.getLogger(OutputStreamHandle.class);

	private Format             format = Format.XML;
	private OutputStreamSender sender;

	public OutputStreamHandle() {
		super();
	}
	public OutputStreamHandle(OutputStreamSender sender) {
		this();
		set(sender);
	}

	public Format getFormat() {
		return format;
	}
	public void setFormat(Format format) {
		this.format = format;
	}
	public OutputStreamHandle withFormat(Format format) {
		setFormat(format);
		return this;
	}

	public OutputStreamSender get() {
		return sender;
	}
	public void set(OutputStreamSender sender) {
		this.sender = sender;
	}
	public OutputStreamHandle with(OutputStreamSender sender) {
		set(sender);
		return this;
	}

	@Override
	public OutputStreamSender sendContent() {
		if (sender == null) {
			throw new IllegalStateException("No sender for writing to output stream");
		}

		return sender;
	}
}
