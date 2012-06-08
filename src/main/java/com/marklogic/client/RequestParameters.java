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

import javax.ws.rs.core.MultivaluedMap;

import com.sun.jersey.core.util.MultivaluedMapImpl;

public class RequestParameters implements Map<String,List<String>> {
	private MultivaluedMap<String, String> map = new MultivaluedMapImpl();

	public RequestParameters() {
		super();
	}

	public void put(String name, String value) {
		List<String> list = new ArrayList<String>();
		list.add(value);
		map.put(name, list);
	}
	public void put(String name, String... values) {
		map.put(name, Arrays.asList(values));
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
		return map.size();
	}

	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return map.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return map.containsValue(value);
	}

	@Override
	public List<String> get(Object key) {
		return map.get(key);
	}

	@Override
	public List<String> put(String key, List<String> value) {
		return map.put(key, value);
	}

	@Override
	public List<String> remove(Object key) {
		return map.remove(key);
	}

	@Override
	public void putAll(Map<? extends String, ? extends List<String>> m) {
		map.putAll(m);
	}

	@Override
	public void clear() {
		map.clear();
	}

	@Override
	public Set<String> keySet() {
		return map.keySet();
	}

	@Override
	public Collection<List<String>> values() {
		return map.values();
	}

	@Override
	public Set<Map.Entry<String, List<String>>> entrySet() {
		return map.entrySet();
	}

	public RequestParameters copy(String prefix) {
		String keyPrefix = prefix+":";

		RequestParameters copy = new RequestParameters();
		for (Map.Entry<String, List<String>> entry: entrySet()) {
			copy.put(keyPrefix+entry.getKey(), entry.getValue());
		}

		return copy;
	}

	MultivaluedMap<String, String> getMap() {
		return map;
	}
}
