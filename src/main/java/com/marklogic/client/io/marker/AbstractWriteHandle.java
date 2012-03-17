package com.marklogic.client.io.marker;

import com.marklogic.client.Format;

/**
 * A Write Handle defines a representation for writing database content.
 *
 * @param <C> the type of content sent to the database when writing content; either Byte[], InputStream, File, Reader, or String
 */
public interface AbstractWriteHandle<C> {
	public Format getFormat();
	public void setFormat(Format format);

	public C sendContent();
}
