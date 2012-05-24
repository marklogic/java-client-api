package com.marklogic.client;

import java.util.HashMap;
import java.util.Map;

public class ExtensionMetadata {
	private String title;
	private String description;
	private String provider;
	private String version;

	public ExtensionMetadata() {
		super();
	}

	public String getTitle() {
    	return title;
    }
    public void  setTitle(String title) {
    	this.title = title;
    }

    public String getDescription() {
    	return description;
    }
    public void  setDescription(String description) {
    	this.description = description;
    }

    public String getProvider() {
    	return provider;
    }
    public void  setProvider(String provider) {
    	this.provider = provider;
    }

    public String getVersion() {
    	return version;
    }
    public void  setVersion(String version) {
    	this.version = version;
    }

    public RequestParameters asParameters() {
    	RequestParameters params = new RequestParameters();
    	if (title != null)
    		params.put("title",       title);
    	if (description != null)
    		params.put("description", description);
    	if (provider != null)
    		params.put("provider",    provider);
    	if (version != null)
    		params.put("version",     version);
    	return params;
	}
}