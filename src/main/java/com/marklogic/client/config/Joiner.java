/*
 * Copyright 2012 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.marklogic.client.config;

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
