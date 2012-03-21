package com.marklogic.client.impl;

import java.util.HashMap;
import java.util.Map;

import com.marklogic.client.Format;
import com.marklogic.client.JSONDocumentManager;
import com.marklogic.client.io.marker.JSONReadHandle;
import com.marklogic.client.io.marker.JSONWriteHandle;

public class JSONDocumentImpl
    extends AbstractDocumentImpl<JSONReadHandle, JSONWriteHandle> 
    implements JSONDocumentManager
{
	JSONDocumentImpl(RESTServices services) {
		super(services,Format.JSON);
	}

	private String language;
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}

	protected Map<String,String> getWriteParams() {
		if (language == null)
			return null;

		HashMap<String,String> params = new HashMap<String,String>();
		params.put("lang", language);

		return params;
	}
}
