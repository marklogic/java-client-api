/*
 * Copyright 2012-2015 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.marklogic.client.io;

import com.marklogic.client.io.marker.BinaryWriteHandle;
import com.marklogic.client.io.marker.GenericWriteHandle;
import com.marklogic.client.io.marker.JSONWriteHandle;
import com.marklogic.client.io.marker.OperationNotSupported;
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
	extends BaseHandle<OperationNotSupported, OutputStreamSender>
    implements
	    BinaryWriteHandle,
        GenericWriteHandle,
        JSONWriteHandle, 
        TextWriteHandle,
        XMLWriteHandle,
        StructureWriteHandle
{

	private OutputStreamSender sender;

	/**
	 * Zero-argument constructor.
	 */
	public OutputStreamHandle() {
		super();
		super.setResendable(false);
	}
	/**
	 * Initializes the handle with an outputstream sender providing
	 * the callback that writes content to the database via an output stream.
	 * @param sender	the processor that sends content to the database
	 */
	public OutputStreamHandle(OutputStreamSender sender) {
		this();
		set(sender);
	}

	/**
	 * Specifies the format of the content and returns the handle
	 * as a fluent convenience.
	 * @param format	the format of the content
	 * @return	this handle
	 */
	public OutputStreamHandle withFormat(Format format) {
		setFormat(format);
		return this;
	}
	/**
	 * Specifies the mime type of the content and returns the handle
	 * as a fluent convenience.
	 * @param mimetype	the mime type of the content
	 * @return	this handle
	 */
	public OutputStreamHandle withMimetype(String mimetype) {
		setMimetype(mimetype);
		return this;
	}

	/**
	 * Returns whether the content can be resent to the output stream
	 * if the request must be retried.  The default is false.
	 */
	public boolean isResendable() {
		return super.isResendable();
	}
	/**
	 * Specifies whether the content can be resent to the output stream
	 * if the request must be retried.
	 */
	public void setResendable(boolean resendable) {
		super.setResendable(resendable);
	}
	/**
	 * Specifies whether the content can be resent to the output stream and
	 * returns the handle as a fluent convenience.
	 * @param resendable	true if the content can be sent again
	 * @return	this handle
	 */
	public OutputStreamHandle withResendable(boolean resendable) {
		setResendable(resendable);
		return this;
	}

	/**
	 * Returns the output stream sender that writes the content.
	 * @return	the output stream sender
	 */
	public OutputStreamSender get() {
		return sender;
	}
	/**
	 * Assigns an output stream sender providing the callback that writes
	 * content to the database via an output stream.
	 * @param sender	the output stream sender
	 */
	public void set(OutputStreamSender sender) {
		this.sender = sender;
	}
    /**
	 * Assigns an output stream sender providing the callback that writes
	 * content to the database and returns the handle as a fluent convenience.
	 * @param sender	the output stream sender
	 * @return	this handle
     */
	public OutputStreamHandle with(OutputStreamSender sender) {
		set(sender);
		return this;
	}

	@Override
	protected OutputStreamSender sendContent() {
		if (sender == null) {
			throw new IllegalStateException("No sender for writing to output stream");
		}

		return sender;
	}
}
