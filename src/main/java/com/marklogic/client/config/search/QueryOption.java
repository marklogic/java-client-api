package com.marklogic.client.config.search;

import java.util.List;




public interface QueryOption {

	public Object asJaxbObject();

	public List<Object> getJAXBChildren();
	
}
