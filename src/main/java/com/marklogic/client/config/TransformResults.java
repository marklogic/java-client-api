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

import java.util.List;

import com.marklogic.client.ElementLocator;

public interface TransformResults extends BoundQueryOption {

	public enum Apply { SNIPPET, RAW };
	
	public String getApply();
	public void setApply(String apply);
	public FunctionRef getTransformFunction();
	public void setTransformFunction(FunctionRef function);
	
	public void setPerMatchTokens(long perMatchTokens);
	public long getPerMatchTokens();

	public void setMaxMatches(long maxMatches);
	public long getMaxMatches();
	
	public void setMaxSnippetChars(long maxSnippetChars);
	public long getMaxSnippetChars();
	
	public List<ElementLocator> getPreferredElements();
	public void setPreferredElements(List<ElementLocator> elements);
	
}