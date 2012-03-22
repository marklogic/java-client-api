package com.marklogic.client.config.search;

public interface SortOrder extends SearchOption, Annotate, Indexable {

	
	public String getType();
	public void setType(String type);
	public String getDirection();
	public void setDirection(String direction);
	public void setCollation(String collation);
	public String getCollation();
	public void setScore();
	public boolean getScore();
	
	
	
}