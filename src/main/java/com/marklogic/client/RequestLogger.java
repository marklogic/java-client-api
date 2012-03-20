package com.marklogic.client;

import java.io.PrintStream;

public interface RequestLogger {
	// suspend or resume logging
    public boolean isEnabled();
    public void setEnabled(boolean enabled);
 
    public PrintStream getPrintStream();
    public void close();
}
