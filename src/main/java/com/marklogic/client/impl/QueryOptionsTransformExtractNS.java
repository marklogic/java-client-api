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

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;

import java.util.HashMap;
import java.util.Vector;

public class QueryOptionsTransformExtractNS extends XMLFilterImpl {
    private static final String SEARCH_NS = "http://marklogic.com/appservices/search";
    private HashMap<String, String> nsmap = new HashMap<String, String> ();

    public QueryOptionsTransformExtractNS() {
    }

    @Override
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        nsmap.put(prefix, uri);
        super.startPrefixMapping(prefix, uri);
    }

    @Override
    public void endPrefixMapping(String prefix) throws SAXException {
        super.endPrefixMapping(prefix);
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        String ename = "namespace-bindings";

        if (SEARCH_NS.equals(uri) && ("path-index".equals(localName) || "searchable-expression".equals(localName))) {
            if ("searchable-expression".equals(localName)) {
                //ename = "searchable-expression-bindings";
            }

            super.startElement(SEARCH_NS, ename, ename, null);
            for (String pfx : nsmap.keySet()) {
                BindingAttributes attrs = new BindingAttributes();
                attrs.addAttribute("prefix", pfx);
                attrs.addAttribute("namespace-uri", nsmap.get(pfx));
                super.startElement(SEARCH_NS, "binding", "binding", attrs);
                super.endElement(SEARCH_NS, "binding", "binding");
            }
            super.endElement(SEARCH_NS, ename, ename);
        }

        super.startElement(uri, localName, qName, attributes);
        nsmap.clear();
    }

    private static class BindingAttributes implements Attributes {
        private Vector<String> names = new Vector<String> ();
        private Vector<String> values = new Vector<String> ();

        public BindingAttributes() {
        }

        public void addAttribute(String prefix, String uri) {
            names.add(prefix);
            values.add(uri);
        }

        @Override
        public int getLength() {
            return names.size();
        }

        @Override
        public String getURI(int i) {
            return values.get(i);
        }

        @Override
        public String getLocalName(int i) {
            return names.get(i);
        }

        @Override
        public String getQName(int i) {
            return names.get(i);
        }

        @Override
        public String getType(int i) {
            return "CDATA";
        }

        @Override
        public String getValue(int i) {
            return values.get(i);
        }

        @Override
        public int getIndex(String uri, String localName) {
            if (uri != null && !"".equals(uri)) {
                return -1;
            }

            for (int i = 0; i < names.size(); i++) {
                if (localName.equals(names.get(i))) {
                    return i;
                }
            }

            return -1;
        }

        @Override
        public int getIndex(String qName) {
            for (int i = 0; i < names.size(); i++) {
                if (qName.equals(names.get(i))) {
                    return i;
                }
            }
            return -1;
        }

        @Override
        public String getType(String uri, String localName) {
            if (getIndex(uri, localName) >= 0) {
                return "CDATA";
            } else {
                return null;
            }
        }

        @Override
        public String getType(String qName) {
            if (getIndex(qName) >= 0) {
                return "CDATA";
            } else {
                return null;
            }
        }

        @Override
        public String getValue(String uri, String localName) {
            int pos = getIndex(uri, localName);
            if (pos >= 0) {
                return getValue(pos);
            } else {
                return null;
            }
        }

        @Override
        public String getValue(String qName) {
            int pos = getIndex(qName);
            if (pos >= 0) {
                return getValue(pos);
            } else {
                return null;
            }
        }
    }
}
