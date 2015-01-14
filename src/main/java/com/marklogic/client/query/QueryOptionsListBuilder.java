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
package com.marklogic.client.query;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This is an implementation class used to read the list of named query options from
 * the server. It may be moved into the .impl package in a future release.
 */
public final class QueryOptionsListBuilder {
    /**
     * This is an implementation class that lists the named query options from
     * the server. It may be moved into the .impl package in a future release.
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlRootElement(namespace = OptionsList.OPTIONS_LIST_NS, name = "query-options")
    public static final class OptionsList {
        public static final String OPTIONS_LIST_NS = "http://marklogic.com/rest-api";

        @XmlElement(namespace = OptionsList.OPTIONS_LIST_NS, name = "options")
        private ArrayList<Options> options;

        public OptionsList() {
            options = new ArrayList<Options>();
        }

        public HashMap<String, String> getOptionsMap() {
            HashMap<String,String> map = new HashMap<String, String>();
            for (Options opt : options) {
                map.put(opt.getName(), opt.getUri());
            }
            return map;
        }
    }

    private static final class Options {
        @XmlElement(namespace = OptionsList.OPTIONS_LIST_NS, name = "name")
        String name;

        @XmlElement(namespace = OptionsList.OPTIONS_LIST_NS, name = "uri")
        String uri;

        public String getName() {
            return name;
        }

        public String getUri() {
            return uri;
        }
    }
}
