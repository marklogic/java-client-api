package com.marklogic.client.config.search;

import javax.xml.namespace.QName;

import com.marklogic.client.config.search.Joiner.Tokenize;

public interface Starter extends FunctionRef {


	public int getStrength();
	
	public QName getElement();
	
	public String getOptions();
	
	public String getDelimiter();
	
	public Tokenize getTokenize();

	public void setStrength(int strength);
	
	public void setElement(QName element);
	
	public void setOptions(String options);
	
	public void setDelimiter(String delimiter);
	
	public void setTokenize(Tokenize tokenize);
	
}

