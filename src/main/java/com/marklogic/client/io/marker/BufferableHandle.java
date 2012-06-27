package com.marklogic.client.io.marker;

/**
 * A Bufferable Handle can read content from a byte[] buffer or write content
 * as a byte[] buffer without changing the state of an external resource.
 * Handles that modify an external resource, such as FileHandle, cannot
 * implement BufferableHandle.
 * 
 * For JSON, text, or XML content, the byte array must be encoded in UTF-8. 
 * Where possible, the handle uses the byte array directly as a backing store.
 * Otherwise, the handle makes a copy into its own store from the byte array.
 */
public interface BufferableHandle {
	public void fromBuffer(byte[] buffer);
	public byte[] toBuffer();
}
