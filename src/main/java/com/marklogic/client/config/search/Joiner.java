package com.marklogic.client.config.search;

import javax.xml.namespace.QName;

public interface Joiner  extends FunctionRef {

	public enum Tokenize { WORD, DEFAULT };
	

	public int getStrength();
	
	public QName getElement();
	
	public String getOptions();
	
	public String getCompare();
	
	public int getConsume();
	
	public String getText();
	
	public Tokenize getTokenize();

	public void setStrength(int strength);
	
	public void setElement(QName element);
	
	public void setOptions(String options);
	
	public void setCompare(String compare);
	
	public void setTokenize(Tokenize tokenize);
	
	public void setText(String text);
	
}
