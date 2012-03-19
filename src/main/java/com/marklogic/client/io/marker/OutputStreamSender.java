package com.marklogic.client.io.marker;

import java.io.IOException;
import java.io.OutputStream;

/**
 * An Output Stream Sender sends content to the database
 * by writing to the provided OutputStream.
 * 
 * @param <C>
 */
public interface OutputStreamSender {
	/**
	 * Implements a callback to write content to the provided database.
	 * @param out
	 * @throws IOException
	 */
	public void write(OutputStream out) throws IOException;
}
