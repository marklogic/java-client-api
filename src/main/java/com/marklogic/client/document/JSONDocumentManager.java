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
package com.marklogic.client.document;

import com.marklogic.client.bitemporal.TemporalDocumentManager;
import com.marklogic.client.io.marker.JSONReadHandle;
import com.marklogic.client.io.marker.JSONWriteHandle;

/**
 * A JSON Document Manager supports database operations on JSON documents.
 */
public interface JSONDocumentManager 
	extends DocumentManager<JSONReadHandle, JSONWriteHandle>, TemporalDocumentManager<JSONReadHandle, JSONWriteHandle>  
{
    /**
     * Creates a builder for specifying changes to the content and metadata
     * of a JSON document.
     * @return	the patch builder
     */
    public DocumentPatchBuilder newPatchBuilder();

    /**
	 * Returns the language code for JSON documents written by the manager.
	 * @return	the language code assigned to documents
	 * @deprecated as a result of adding native json support, the lang param in the REST API is deprecated
	 */
	@Deprecated
	public String getLanguage();
	/**
	 * Specifies the language code for JSON documents written by the manager.  The IANA codes
	 * for specifying a language for JSON documents are the same as those used with
	 * the <a href="http://www.w3.org/TR/REC-xml/#sec-lang-tag">xml:lang</a> attribute.
	 * 
	 * @param language	the language code assigned to documents
	 * @deprecated as a result of adding native json support, the lang param in the REST API is deprecated
	 */
	@Deprecated
    public void setLanguage(String language);
}
