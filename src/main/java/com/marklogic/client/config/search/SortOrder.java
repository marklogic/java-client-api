package com.marklogic.client.config.search;

public interface SortOrder extends JAXBBackedQueryOption, Annotatable, Indexable {

	public enum Direction { ASCENDING, DESCENDING };
	
	public String getType();
	public void setType(String type);
	public Direction getDirection();
	public void setDirection(Direction direction);
	public void setCollation(String collation);
	public String getCollation();
	public void setScore();
	public boolean getScore();
	
	
	
}