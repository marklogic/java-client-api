package com.marklogic.client.io;

import com.marklogic.client.Format;

public abstract class BaseHandle<R,W> {
	private Format format = Format.XML;

	public BaseHandle() {
		super();
	}

	public Format getFormat() {
		return format;
	}
	public void setFormat(Format format) {
		this.format = format;
	}

	/**
	 * As part of the contract between a read handle and the API, 
	 * declares the class of the content received from the database.
	 * You should rarely if ever need to call this method directly when using the handle.
	 * @return
	 */
	protected Class<R> receiveAs() {
		throw new UnsupportedOperationException(this.getClass().getName()+" cannot receive content");
	}
	/**
	 * As part of the contract between a read handle and the API, 
	 * receives content from the database.  You should rarely
	 * if ever need to call this method directly when using the handle.
	 * @return
	 */
	protected void receiveContent(R content) {
		throw new UnsupportedOperationException(this.getClass().getName()+" cannot receive content");
	}

	/**
	 * As part of the contract between a write handle and the API, 
	 * sends content to the database.  You should rarely
	 * if ever need to call this method directly when using the handle.
	 * @return
	 */
	protected W sendContent() {
		throw new UnsupportedOperationException(this.getClass().getName()+" cannot send content");
	}

}
