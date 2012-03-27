package com.marklogic.client.io;

import java.io.IOException;
import java.io.OutputStream;

/**
 * An Output Stream Sender sends content to the database
 * by writing to the provided OutputStream.
 * 
 * When writing JSON, text, or XML content, you should use an OutputStream only
 * only to write bytes for characters encoded in UTF-8.  If the bytes provide
 * characters with a different encoding, convert the bytes using
 * the java.nio.charset.CharsetDecoder class.
 */
public interface OutputStreamSender {
	/**
	 * Implements a callback to write content to the provided database.
	 * @param out
	 * @throws IOException
	 */
	public void write(OutputStream out) throws IOException;
}
