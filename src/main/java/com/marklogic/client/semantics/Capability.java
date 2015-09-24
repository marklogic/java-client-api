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

/**
 * <p>The permission capabilities available for operations on graphs.  Used in
 * {@link GraphManager#permission}, {@link SPARQLQueryManager#permission}, and
 * {@link GraphPermissions#permission}.  See usage examples in javadocs for
 * {@link GraphManager} and {@link SPARQLQueryManager}.</p>
 *
 * <p>For details about RDF, SPARQL, and semantics in MarkLogic see
 * <a href="https://docs.marklogic.com/guide/semantics" target="_top">Semantics Developer's Guide</a>
 */
public enum Capability { READ, UPDATE, EXECUTE; }
