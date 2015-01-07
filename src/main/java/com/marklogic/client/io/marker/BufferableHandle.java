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
	/**
	 * Sets the content of the handle by copying from a byte array buffer
	 * encoded in UTF-8.
	 * @param buffer	the byte array
	 */
	public void fromBuffer(byte[] buffer);
	/**
	 * Copies the content of the handle to a byte array buffer
	 * encoded in UTF-8.
	 * @return	the byte array
	 */
	public byte[] toBuffer();
}
