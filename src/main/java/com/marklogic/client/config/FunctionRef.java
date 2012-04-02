package com.marklogic.client.config;


public interface FunctionRef {

	public void setApply(String apply);
	public void setNs(String namespace);
	public void setAt(String at);
	
	public String getApply();
	public String getNs();
	public String getAt();
	
	
}
