package com.marklogic.client.impl;

import com.marklogic.client.ValueLocator;
import com.marklogic.client.config.search.KeyValueQueryDefinition;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: ndw
 * Date: 3/19/12
 * Time: 10:51 AM
 * To change this template use File | Settings | File Templates.
 */
public class KeyValueQueryDefinitionImpl implements KeyValueQueryDefinition {
    String optionsUri = null;
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

    @Override
    public String getOptionsUri() {
        return optionsUri;
    }

    @Override
    public void setOptionsUri(String uri) {
        optionsUri = uri;
    }
}
