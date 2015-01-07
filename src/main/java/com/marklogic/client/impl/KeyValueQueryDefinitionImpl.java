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
package com.marklogic.client.impl;

import com.marklogic.client.query.ValueLocator;
import com.marklogic.client.query.KeyValueQueryDefinition;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

public class KeyValueQueryDefinitionImpl extends AbstractQueryDefinition implements KeyValueQueryDefinition {
    Map<ValueLocator, String> defs = new Hashtable<ValueLocator, String> ();
    
    protected KeyValueQueryDefinitionImpl(String uri) {
        optionsUri = uri;
    }
    
    @Override
    public int size() {
        return defs.size();
    }

    @Override
    public boolean isEmpty() {
        return defs.isEmpty();
    }

    @Override
    public boolean containsKey(Object o) {
        return defs.containsKey(o);
    }

    @Override
    public boolean containsValue(Object o) {
        return defs.containsValue(o);
    }

    @Override
    public String get(Object o) {
        return defs.get(o);
    }

    @Override
    public String put(ValueLocator valueLocator, String value) {
        return defs.put(valueLocator, value);
    }

    @Override
    public String remove(Object o) {
        return defs.remove(o);
    }

    @Override
    public void putAll(Map<? extends ValueLocator, ? extends String> map) {
        defs.putAll(map);
    }

    @Override
    public void clear() {
        defs.clear();
    }

    @Override
    public Set<ValueLocator> keySet() {
        return defs.keySet();
    }

    @Override
    public Collection<String> values() {
        return defs.values();
    }

    @Override
    public Set<Entry<ValueLocator, String>> entrySet() {
        return defs.entrySet();
    }
}
