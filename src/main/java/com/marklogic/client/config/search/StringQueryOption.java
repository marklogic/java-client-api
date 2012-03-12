package com.marklogic.client.config.search;

public abstract class StringQueryOption implements SearchOption {

	public abstract String getValue();
	
	public abstract void setValue(String value);
}
