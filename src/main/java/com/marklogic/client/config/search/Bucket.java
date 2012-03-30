package com.marklogic.client.config.search;

public interface Bucket extends JAXBBackedQueryOption {

	public String getContent();

	public void setContent(String value);

	public String getName();

	public void setName(String value);

	public String getGe();

	public void setGe(String value);

	public String getLt();

	public void setLt(String value);
}
