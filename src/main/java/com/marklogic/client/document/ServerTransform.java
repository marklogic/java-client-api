/*
 * Copyright 2012-2015 MarkLogic Corporation
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
package com.marklogic.client.document;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.marklogic.client.util.RequestParameters;

/**
 * ServerTransform specifies the invocation of a transform on the server
 * including both the name of the transform and the parameters passed
 * to the transform.
 */
public class ServerTransform extends RequestParameters {
	private String name;

	/**
	 * Specifies invocation of the named transform on the server.
	 * @param name	the transform installed on the server
	 */
	public ServerTransform(String name) {
		super();
    	this.name = name;
	}

	/**
	 * Gets the name of the invoked transform.
	 * @return	the name of the transform installed on the server
	 */
	public String getName() {
		return name;
	}

	/**
	 * Merges the transform and its parameters with other parameters
	 * of the request.
	 * 
	 * Ordinarily, and application does not need to call this method.
	 * @param currentParams	the other parameters
	 * @return	the union of the other parameters and the transform parameters
	 */
	public Map<String,List<String>> merge(Map<String,List<String>> currentParams) {
		Map<String,List<String>> params = (currentParams != null) ?
				currentParams : new RequestParameters();

		params.put("transform", Arrays.asList(getName()));

		for (Map.Entry<String, List<String>> entry: entrySet()) {
			params.put("trans:"+entry.getKey(), entry.getValue());
		}

		return params;
	}
}
