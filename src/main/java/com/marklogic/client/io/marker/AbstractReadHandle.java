package com.marklogic.client.io.marker;

import com.marklogic.client.Format;

/**
 * A Read Handle defines a representation for reading database content.
 *
 * @param <C> the type of content received from the database when reading content; either Byte[], InputStream, File, Reader, or String
 */
public interface AbstractReadHandle<C> {
	public Format getFormat();
	public void setFormat(Format format);

	public Class<C> receiveAs();
	public void receiveContent(C content);
}
