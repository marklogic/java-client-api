package com.marklogic.client.config;

import java.util.List;

public interface Term extends JAXBBackedQueryOption, TermOptions {

	public enum EmptyApply { ALL_RESULTS };
	
	public List<String> getTermOptions();
	public void setTermOptions(List<String> termOptions);

	public FunctionRef getTermFunction();
	public void setTermFunction(FunctionRef function);
	
	
	public FunctionRef getEmpty();
	public void setEmpty(FunctionRef function);
	public void setEmpty(EmptyApply apply);
	

}