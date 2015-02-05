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
package com.marklogic.client.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.marklogic.client.impl.RequestParametersImplementation;

/**
 * RequestParameters supports a map with a string as the key and
 * a list of strings as the value, which can represent parameters
 * of an operation including parameters transported over HTTP.
 */
public class RequestParameters
    extends RequestParametersImplementation
    implements Map<String,List<String>>
{
	/**
	 * Zero-argument constructor.
	 */
	public RequestParameters() {
		super();
	}

	/**
	 * Set a parameter to a single value.
	 * @param name	the parameter name
	 * @param value	the value of the parameter
	 */
	public void put(String name, String value) {
		List<String> list = new ArrayList<String>();
		list.add(value);
		getMap().put(name, list);
	}
	/**
	 * Sets a parameter to a list of values.
	 * @param name	the parameter
	 * @param values	the list of values
	 */
	public void put(String name, String... values) {
		getMap().put(name, Arrays.asList(values));
	}
	/**
	 * Appends a value to the list for a parameter.
	 * @param name	the parameter
	 * @param value	the value to add to the list
	 */
	public void add(String name, String value) {
		if (containsKey(name)) {
			get(name).add(value);
		} else {
			put(name, value);
		}
	}
	/**
	 * Appends a list of values to the list for a parameter.
	 * @param name	the parameter
	 * @param values	the values to add to the list
	 */
	public void add(String name, String... values) {
		if (containsKey(name)) {
			List<String> list = get(name);
			for (String value: values) {
				list.add(value);
			}
		} else {
			put(name, values);
		}
	}

	/**
	 * Returns the number of request parameters.
	 */
	@Override
	public int size() {
		return getMap().size();
	}

	/**
	 * Returns whether or not any request parameters have been specified.
	 */
	@Override
	public boolean isEmpty() {
		return getMap().isEmpty();
	}

	/**
	 * Checks whether the parameter name has been specified.
	 */
	@Override
	public boolean containsKey(Object key) {
		return getMap().containsKey(key);
	}

	/**
	 * Checks whether any parameters have the value.
	 */
	@Override
	public boolean containsValue(Object value) {
		return getMap().containsValue(value);
	}

	/**
	 * Gets the values for a parameter name.
	 */
	@Override
	public List<String> get(Object key) {
		return getMap().get(key);
	}

	/**
	 * Sets the values of a parameter name, returning the previous values if any.
	 */
	@Override
	public List<String> put(String key, List<String> value) {
		return getMap().put(key, value);
	}

	/**
	 * Removes a parameter name, returning its values if any.
	 */
	@Override
	public List<String> remove(Object key) {
		return getMap().remove(key);
	}

	/**
	 * Adds existing parameter names and values.
	 */
	@Override
	public void putAll(Map<? extends String, ? extends List<String>> m) {
		getMap().putAll(m);
	}

	/**
	 * Removes all parameters.
	 */
	@Override
	public void clear() {
		getMap().clear();
	}

	/**
	 * Returns the set of specified parameter names.
	 */
	@Override
	public Set<String> keySet() {
		return getMap().keySet();
	}

	/**
	 * Returns a list of value lists.
	 */
	@Override
	public Collection<List<String>> values() {
		return getMap().values();
	}

	/**
	 * Returns a set of parameter-list entries.
	 */
	@Override
	public Set<Map.Entry<String, List<String>>> entrySet() {
		return getMap().entrySet();
	}

	/**
	 * Creates a copy of the parameters, prepending a namespace prefix
	 * to each parameter name.
	 * @param prefix	the prefix to prepend
	 * @return	the copy of the parameters
	 */
	public RequestParameters copy(String prefix) {
		String keyPrefix = prefix+":";

		RequestParameters copy = new RequestParameters();
		for (Map.Entry<String, List<String>> entry: entrySet()) {
			copy.put(keyPrefix+entry.getKey(), entry.getValue());
		}

		return copy;
	}
}
