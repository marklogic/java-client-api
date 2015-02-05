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
	 * Implements a callback to write content to the provided output stream
	 * for sending to the database server.
	 * @param out	the output stream receiving the content
	 */
	public void write(OutputStream out) throws IOException;
}
