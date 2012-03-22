package com.marklogic.client.config.search;

import org.w3c.dom.Node;

public interface FunctionRef {

	public void setApply(String apply);
	public void setNs(String namespace);
	public void setAt(String at);
	
	public String getApply();
	public String getNs();
	public String getAt();
	
	public void fillFrom(Node c);
	
}
