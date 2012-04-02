package com.marklogic.client.config;



public interface ComputedBucket extends JAXBBackedQueryOption {

	public String getName();
	public void setName(String name);
	
	public String getGe();
	public void setGe(String ge);
	
	public String getLt();
	public void setLt(String lt);
	
	public AnchorValue getAnchor();
	public void setAnchor(AnchorValue name);
	
	public AnchorValue getLtAnchor();
	public void setLtAnchor(AnchorValue name);
	
	public AnchorValue getGeAnchor();
	public void setGeAnchor(AnchorValue name);
	
	public String getContent();
	public void setContent(String content);
	
}
