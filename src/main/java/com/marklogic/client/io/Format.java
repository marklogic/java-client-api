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

import com.marklogic.client.MarkLogicInternalException;

/**
 * The Format enumerates different kinds of document content.
 */
public enum Format {
    /**
     * Identifies the format of binary documents such as images.
     */
	BINARY,
	/**
	 * Identifies the format of JSON documents.
	 */
	JSON,
	/**
	 * Identifies the format of text documents such as plain text and HTML.
	 */
	TEXT,
	/**
	 * Identifies the format of XML documents such as XHTML.
	 */
	XML,
	/**
	 * Used for documents with unknown or multiple formats.
	 */
	UNKNOWN;
    /**
     * Returns the default mime type for the format.
     * @return	the default mime type
     */
	public String getDefaultMimetype() {
    	switch(this) {
    	case UNKNOWN:
    		return null;
    	case BINARY:
    		// TODO: or possibly "application/x-unknown-content-type"
    		return "application/octet-stream";
    	case JSON:
    		return "application/json";
    	case TEXT:
    		return "text/plain";
    	case XML:
    		return "application/xml";
    	default:
        	throw new MarkLogicInternalException("Unknown format "+this.toString());
    	}
    }
	
	public static Format getFromMimetype(String mimeType) {
		if      ( mimeType == null ) return UNKNOWN;
		else if ( "application/xml".equals(mimeType) ) return XML;
		else if ( "text/xml".equals(mimeType) ) return XML;
		else if ( "application/json".equals(mimeType) ) return JSON;
		else if ( "text/xml".equals(mimeType) ) return JSON;
		else if ( "application/octet-stream".equals(mimeType) ) return BINARY;
		else if ( mimeType.startsWith("text/") ) return TEXT;
		else return UNKNOWN;
	}
}
