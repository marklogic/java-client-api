package com.marklogic.client;

public enum Format {
    BINARY, JSON, TEXT, XML, UNKNOWN;
    public String getDefaultMimetype() {
    	if (this == BINARY)
    		// TODO: or possibly "application/x-unknown-content-type" or null
    		return "application/octet-stream";
    	else if (this == JSON)
    		return "application/json";
    	else if (this == TEXT)
    		return "text/plain";
    	else if (this == XML)
    		return "application/xml";
    	else
    		return null;
    }
}
