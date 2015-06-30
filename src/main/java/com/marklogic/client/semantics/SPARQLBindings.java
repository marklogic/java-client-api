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
package com.marklogic.client.semantics;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Represents binding names and values to go with a SPARQL Query.
 * For details about semantics in MarkLogic see
 * {@link https://docs.marklogic.com/guide/semantics Semantics Developer's Guide}
 */
public interface SPARQLBindings extends Map<String, List<SPARQLBinding>> {
    public SPARQLBindings bind(String name, String value);
    public SPARQLBindings bind(String name, String value, String type);
    public SPARQLBindings bind(String name, String value, Locale languageTag);
};
