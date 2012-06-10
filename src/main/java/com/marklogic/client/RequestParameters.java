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
package com.marklogic.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.marklogic.client.impl.RequestParametersImplementation;

public class RequestParameters
    extends RequestParametersImplementation
    implements Map<String,List<String>>
{
	public RequestParameters() {
		super();
	}

	public void put(String name, String value) {
		List<String> list = new ArrayList<String>();
		list.add(value);
		getMap().put(name, list);
	}
	public void put(String name, String... values) {
		getMap().put(name, Arrays.asList(values));
	}
	public void add(String name, String value) {
		if (containsKey(name)) {
			get(name).add(value);
		} else {
			put(name, value);
		}
	}
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

	@Override
	public int size() {
		return getMap().size();
	}

	@Override
	public boolean isEmpty() {
		return getMap().isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return getMap().containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return getMap().containsValue(value);
	}

	@Override
	public List<String> get(Object key) {
		return getMap().get(key);
	}

	@Override
	public List<String> put(String key, List<String> value) {
		return getMap().put(key, value);
	}

	@Override
	public List<String> remove(Object key) {
		return getMap().remove(key);
	}

	@Override
	public void putAll(Map<? extends String, ? extends List<String>> m) {
		getMap().putAll(m);
	}

	@Override
	public void clear() {
		getMap().clear();
	}

	@Override
	public Set<String> keySet() {
		return getMap().keySet();
	}

	@Override
	public Collection<List<String>> values() {
		return getMap().values();
	}

	@Override
	public Set<Map.Entry<String, List<String>>> entrySet() {
		return getMap().entrySet();
	}

	public RequestParameters copy(String prefix) {
		String keyPrefix = prefix+":";

		RequestParameters copy = new RequestParameters();
		for (Map.Entry<String, List<String>> entry: entrySet()) {
			copy.put(keyPrefix+entry.getKey(), entry.getValue());
		}

		return copy;
	}
}
