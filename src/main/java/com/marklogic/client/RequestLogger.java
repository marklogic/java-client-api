package com.marklogic.client;

public interface RequestLogger {
	// suspend or resume logging
    public boolean isEnabled();
    public void setEnabled(boolean enabled);
 
    // operations on the encapsulated Writer or OutputStream
    public void flush();
    public void close();
}
