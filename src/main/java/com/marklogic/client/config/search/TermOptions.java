package com.marklogic.client.config.search;

import java.util.List;


public interface TermOptions {

	public List<String> getTermOptions();
	public void setTermOptions(List<String> termOptions);
	

	public Double getWeight();
	public void setWeight(Double weight);
	
}
