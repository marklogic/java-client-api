package com.marklogic.client.config.search;

import java.util.List;




public interface SearchOption {

	public Object asJaxbObject();

	public List<Object> getJAXBChildren();
	
}
