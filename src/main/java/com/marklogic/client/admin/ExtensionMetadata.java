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
package com.marklogic.client.admin;

import com.marklogic.client.util.RequestParameters;

/**
 * Extension Metadata provides detail about a transform
 * or resource services extension.  All of the detail
 * is optional but recommended.
 */
public class ExtensionMetadata {
    /**
     * The ScriptLanguage enumeration specifies the categories of metadata read from or written to the database.
     */
	public enum ScriptLanguage {
		/** For resource extensions written in xquery. */
		XQUERY,
		/** For resource extensions written in javascript. */
		JAVASCRIPT;
	}
	/** Convenience constant to provide something shorter than ExtensionMetadata.ScriptLanguage.XQUERY */
	public static final ScriptLanguage XQUERY = ScriptLanguage.XQUERY;
	/** Convenience constant to provide something shorter than ExtensionMetadata.ScriptLanguage.JAVASCRIPT */
	public static final ScriptLanguage JAVASCRIPT = ScriptLanguage.JAVASCRIPT;

	private String title;
	private String description;
	private String provider;
	private String version;
	private ScriptLanguage scriptLanguage = XQUERY;

	/**
	 * Zero-argument constructor.
	 */
	public ExtensionMetadata() {
		super();
	}

	/**
	 * Returns the title of the extension.
	 * @return	the extension title
	 */
	public String getTitle() {
    	return title;
    }
	/**
	 * Specifies the title of the extension.
	 * @param title	the extension title
	 */
    public void setTitle(String title) {
    	this.title = title;
    }

    /**
     * Returns the description of the extension.
     * @return	the extension description
     */
    public String getDescription() {
    	return description;
    }
    /**
     * Specifies the description of the extension.
     * @param description	the extension description
     */
    public void setDescription(String description) {
    	this.description = description;
    }

    /**
     * Returns the name of the organization providing the extension.
     * @return	the provider name
     */
    public String getProvider() {
    	return provider;
    }
    /**
     * Specifies the name of the organization providing the extension.
     * @param provider	the provider name
     */
    public void setProvider(String provider) {
    	this.provider = provider;
    }

    /**
     * Returns the version identifier for the extension implementation.
     * @return	the version number or code
     */
    public String getVersion() {
    	return version;
    }
    /**
     * Specifies the version identifier for the extension implementation.
     * @param version	the version number or code
     */
    public void setVersion(String version) {
    	this.version = version;
    }

	/**
	 * Returns the script language for this resource extension, either XQUERY (default)
	 * or JAVASCRIPT.
	 */
    public ScriptLanguage getScriptLanguage() {
        return scriptLanguage;
    }

	/**
	 * Specifies the script language for this resource extension, either XQUERY (default)
	 * or JAVASCRIPT.
	 */
    public void setScriptLanguage(ScriptLanguage scriptLanguage) {
        this.scriptLanguage = scriptLanguage;
    }

    /**
     * Constructs request parameters expressing the extension metadata.
     * 
     * Ordinarily, this method is called internally during extension
     * processing, but you can call this method directly if convenient.
     * @return	the metadata as parameters
     */
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
