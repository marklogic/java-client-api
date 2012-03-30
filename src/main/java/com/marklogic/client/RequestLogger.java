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
