/*
 * Copyright 2013-2015 MarkLogic Corporation
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
package com.marklogic.client.impl;

import com.marklogic.client.query.SuggestDefinition;

public class SuggestDefinitionImpl implements SuggestDefinition {

	private String options;
	private String stringCriteria;
	private String[] queries;
    private Integer limit;
    private Integer cursorPosition;
    
	
	@Override
	public String getOptionsName() {
		return options;
	}

	@Override
	public void setOptionsName(String optionsName) {
		this.options = optionsName;
	}

	@Override
	public void setStringCriteria(String qtext) {
		this.stringCriteria = qtext;
	}

	@Override
	public String getStringCriteria() {
		return stringCriteria;
	}

	@Override
	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	@Override
	public Integer getLimit() {
		return limit;
	}

	@Override
	public void setCursorPosition(Integer cursorPosition) {
		this.cursorPosition = cursorPosition;
	}

	@Override
	public Integer getCursorPosition() {
		return cursorPosition;
	}

	@Override
	public void setQueryStrings(String... qtext) {
		this.queries = qtext;
	}

	@Override
	public String[] getQueryStrings() {
		return queries;
	}

}
