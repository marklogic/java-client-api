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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;

import com.marklogic.client.semantics.RDFTypes;
import com.marklogic.client.semantics.SPARQLBinding;
import com.marklogic.client.semantics.SPARQLBindings;

/**
 * Represents binding names and values to go with a SPARQL Query.
 * For details about semantics in MarkLogic see
 * {@link https://docs.marklogic.com/guide/semantics Semantics Developer's Guide}
 */
public class SPARQLBindingImpl implements SPARQLBinding {
    private String name;
    private String value;
    private RDFTypes datatype;
    private Locale languageTag;

    public SPARQLBindingImpl(String name, String value, RDFTypes type) {
        this.name = name;
        this.value = value;
        this.datatype = type;
    }
    public SPARQLBindingImpl(String name, String value, Locale languageTag) {
        this.name = name;
        this.value = value;
        this.languageTag = languageTag;
    }
    public String getName()        { return name;        }
    public String getValue()       { return value;       }
    public RDFTypes getDatatype()  { return datatype;    }
    public Locale getLanguageTag() { return languageTag; }
}
