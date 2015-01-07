/**
 * The package provides classes and interfaces that implement MarkLogic Search API configuration using
 * Java Objects.
 * <p>
 * Ordinarily, objects in this package will be created in one of two ways: 
 * by retrieving them from a MarkLogic Server using a {@link com.marklogic.client.io.QueryOptionsHandle} 
 * or by building them with {@link com.marklogic.client.admin.config.QueryOptionsBuilder}.
 * <p>
 * A QueryOptions object contains getters and setters for each of the types of 
 * information that comprise a Search API configuration.  These options include
 * constraints, tuples configurations, operators, a grammar element, and options for 
 * configuring the result.
 * <p>
 * The classes of each kind of configuration object are enclosed within QueryOptions.  
 * For example, QueryOptions.QueryGrammar models the set of configuration elements that comprise
 * a grammar.  QueryOptions.QueryGeospatialElement models access to a
 * particular geospatial index configuration that uses an element to encode
 * coordinates.  QueryOptions.QueryTerm contains the behavior of bare search
 * terms in a Search string configuration.
 * <p>
 * While you can access and manipulate query options configurations in this way, 
 * it is simpler to build fresh ones with {@link com.marklogic.client.admin.config.QueryOptionsBuilder}.
 * <p>
 * QueryOptions, when stored on the server and referenced from a subsequent request, 
 * control the behavior and results of calls to 
 * {@link com.marklogic.client.query.QueryManager}.search(),
 * {@link com.marklogic.client.query.QueryManager}.values(), and
 * {@link com.marklogic.client.query.QueryManager}.tuples()
 * <p>
 * For comprehensive treatment of the Search API, the REST API and the Java API visit
 * http://docs.marklogic.com
 * <p>
 * <strong>Note:</strong> QueryOptions and QueryOptionsBuilder are deprecated.
 * Use a JSON or XML handle instead to write or read query options.
 */
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
@javax.xml.bind.annotation.XmlSchema (               
    xmlns = { 
            @javax.xml.bind.annotation.XmlNs(prefix = "search", 
                     namespaceURI="http://marklogic.com/appservices/search")
           },    
    elementFormDefault = javax.xml.bind.annotation.XmlNsForm.QUALIFIED        
)
package com.marklogic.client.admin.config;
