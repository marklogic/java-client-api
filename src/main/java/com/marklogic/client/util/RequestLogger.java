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
package com.marklogic.client.util;

import java.io.PrintStream;

/**
 * Request Logger records the requests sent to the server.  After creating
 * a document or query manager, you can set a logger on the manager.  You
 * can choose to log content sent to the server as well as requests.
 * 
 * FileHandle constitutes an exception to the ability to log content.  Only
 * the name of the file is logged.
 */
public interface RequestLogger {
	/**
	 * Indicates that no content is copied to the log (the default).
	 */
	public final static long NO_CONTENT  = 0;
	/**
	 * Indicates that all content is copied to the log.
	 */
	public final static long ALL_CONTENT = Long.MAX_VALUE;

	/**
	 * Returns how much content is copied to the log.
	 * @return	the limit on copying content
	 */
	public long getContentMax();
	/**
	 * Controls how much content is copied to the log (defaulting to NO_CONTENT).
	 * @param length	the limit on copying content
	 */
	public void setContentMax(long length);

	/**
	 * Returns whether logging is active or suspended.
	 * @return	the enablement of logging
	 */
	public boolean isEnabled();
	/**
	 * Suspend or resume logging.
	 * @param enabled	the enablement of logging
	 */
    public void setEnabled(boolean enabled);

    /**
     * Returns the underlying PrintStream used for logging.
     * @return	the PrintStream for logging
     */
	public PrintStream getPrintStream();

	/**
	 * Copies content to the log during request processing 
	 * up to the length limit specified for the logger.
	 * 
	 * Ordinarily, this method is called internally
	 * during reading content from the database or writing
	 * content to the database.  You may, however, use
	 * this method directly if convenient.
	 * 
	 * @param content	the copied content
	 * @return	the copied content
	 */
    public <T> T copyContent(T content);

    /**
     * Send buffered output to the log destination.
     */
    public void flush();
	/**
	 * Close the log.
	 */
    public void close();
}
