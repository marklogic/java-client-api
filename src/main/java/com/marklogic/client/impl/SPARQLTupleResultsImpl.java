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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.SortedMap;
import java.util.Set;
import java.util.TreeMap;

import com.fasterxml.jackson.databind.JsonNode;
import com.marklogic.client.query.Tuple;
import com.marklogic.client.semantics.SPARQLTuple;
import com.marklogic.client.semantics.SPARQLTupleResults;
import com.marklogic.client.semantics.SPARQLBinding;
import com.marklogic.client.impl.TuplesBuilder;
import com.marklogic.client.io.BaseHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.OutputStreamSender;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.io.marker.ContentHandle;
import com.marklogic.client.io.marker.OperationNotSupported;
import com.marklogic.client.io.marker.SPARQLReadHandle;

public class SPARQLTupleResultsImpl 
    extends BaseHandle<InputStream, OperationNotSupported>
    implements SPARQLTupleResults, SPARQLReadHandle
{
    private List<SPARQLTuple> bindings;
    private String[] bindingNames;
    private MyJacksonHandle jacksonHandle;


    @Override
    public String[] getBindingNames() {
        if ( bindingNames == null ) throw new IllegalStateException("this has not been populated yet");
        return bindingNames;
    }

    public SPARQLTupleResults withFormat(Format format) {
        setFormat(format);
        return this;
    }

    private class MyJacksonHandle extends JacksonHandle {
        void receiveContent2(InputStream content) {receiveContent(content);}
    };

    private void init() {
        if ( jacksonHandle == null ) {
            jacksonHandle = new MyJacksonHandle();
        }
    }

    @Override
    protected Class<InputStream> receiveAs() {
        return InputStream.class;
    }

    private class SPARQLTupleImpl extends TreeMap<String, SPARQLBinding> implements SPARQLTuple {}

    @Override
    protected void receiveContent(InputStream content) {
        init();
        // TODO: finish
        ArrayList tmpTuple = new ArrayList<SPARQLTuple>();
        jacksonHandle.receiveContent2(content);
        JsonNode response = jacksonHandle.get();
        if ( ! response.has("head") ) throw new IllegalStateException("malformed response: no head field");
        JsonNode head = response.get("head");
        if ( ! head.has("vars") ) throw new IllegalStateException("malformed response: no vars field");
        JsonNode vars = head.get("vars");
        String[] tmpBindingNames = new String[vars.size()];
        tmpBindingNames = new String[vars.size()];
        for ( int i=0; i < vars.size(); i++ ) {
            tmpBindingNames[i] = vars.get(i).textValue();
        }
        if ( ! response.has("results") ) throw new IllegalStateException("malformed response: no results field");
        JsonNode results = response.get("results");
        if ( ! results.has("bindings") ) throw new IllegalStateException("malformed results: no bindings field");
        JsonNode bindingsNode = results.get("bindings");
        int i=0;
        for ( JsonNode tuple : bindingsNode ) {
            i++;
            SPARQLTuple bindings = new SPARQLTupleImpl();
            Iterator<String> keys = tuple.fieldNames();
            while ( keys.hasNext() ) {
                String key = keys.next();
                JsonNode valueNode = tuple.get(key);
                if ( ! valueNode.has("value") ) {
                    throw new IllegalStateException("malformed results: no value for tuple " + i +
                      " binding '" + key + "'");
                }
                String value = valueNode.get("value").textValue();
                if ( valueNode.has("xml:lang") ) {
                    String languageTag = valueNode.get("xml:lang").textValue();
                    Locale locale = Locale.forLanguageTag(languageTag);
                    bindings.put(key, new SPARQLBindingImpl(key, value, locale));
                } else if ( valueNode.has("type") ) {
                    String type = valueNode.get("type").textValue();
                    bindings.put(key, new SPARQLBindingImpl(key, value, type));
                }
            }
            tmpTuple.add(bindings);
        }
        this.bindingNames = tmpBindingNames;
        this.bindings = new ArrayList<SPARQLTuple>(tmpTuple);
    }

    @Override
    public Iterator<SPARQLTuple> iterator() {
        if ( bindings == null ) throw new IllegalStateException("this has not been populated yet");
        return bindings.iterator();
    }

    @Override
    public long size() {
        return bindings.size();
    }
}
