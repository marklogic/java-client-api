/*
 * Copyright 2012 MarkLogic Corporation
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

import com.marklogic.client.Format;

/**
 * A Write Handle defines a representation for writing database content.
 *
 * @param <C> the type of content sent to the database when writing content; either Byte[], InputStream, File, Reader, or String
 */
public interface AbstractWriteHandle<C> {
	public Format getFormat();
	public void setFormat(Format format);

	/**
	 * As part of the contract between a write handle and the API, 
	 * sends content to the database.  You should rarely
	 * if ever need to call this method directly when using the handle.
	 * @return
	 */
	public C sendContent();
}
