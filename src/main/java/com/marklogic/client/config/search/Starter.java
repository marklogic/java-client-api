package com.marklogic.client.config.search;

import javax.xml.namespace.QName;

import com.marklogic.client.config.search.Joiner.Tokenize;

public interface Starter extends FunctionRef {

	public String getDelimiter();
	
	public QName getElement();
	
	public String getOptions();
	
	public int getStrength();
	
	public Tokenize getTokenize();

	public void setDelimiter(String delimiter);
	
	public void setElement(QName element);
	
	public void setOptions(String options);
	
	public void setStrength(int strength);
	
	public void setTokenize(Tokenize tokenize);
	
}

