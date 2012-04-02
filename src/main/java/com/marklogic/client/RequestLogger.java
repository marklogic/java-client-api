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
package com.marklogic.client;

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
	public final static long NO_CONTENT  = 0;
	public final static long ALL_CONTENT = Long.MAX_VALUE;

	public long getContentMax();
	/**
	 * Controls how much content is copied to the log (defaulting to NO_CONTENT).
	 */
	public void setContentMax(long length);

	public boolean isEnabled();
	/**
	 * Suspend or resume logging.
	 */
    public void setEnabled(boolean enabled);

	public PrintStream getPrintStream();

	/**
	 * Copies the specified length of content to the log
	 * during request processing.
	 * @param content
	 * @return
	 */
    public <T> T copyContent(T content);

    public void flush();
	/**
	 * Close the log.
	 */
    public void close();
}
