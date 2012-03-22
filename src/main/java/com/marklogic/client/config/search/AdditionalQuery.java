package com.marklogic.client.config.search;

import org.w3c.dom.Element;

public interface AdditionalQuery {

	public void setQuery(Element ctsQuery);
	public Element getQuery();
	
}
