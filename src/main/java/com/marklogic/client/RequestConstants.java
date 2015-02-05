/*
 * Copyright 2013-2015 MarkLogic Corporation
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

/**
 * The RequestConstants class provides constants for the common namespace
 * prefixes and URIs used in the Java API (similar to the XML constants
 * provided by the javax.xml.XMLConstants class).
 */
public class RequestConstants {
	/**
	 * The namespace URI for vocabularies implemented by the REST API.
	 */
	final static public String RESTAPI_NS = "http://marklogic.com/rest-api";
	/**
	 * The conventional namespace prefix for vocabularies implemented by the REST API.
	 */
	final static public String RESTAPI_PREFIX = "rapi";
	/**
	 * The namespace URI for vocabularies implemented by the Search API.
	 */
	final static public String SEARCH_NS = "http://marklogic.com/appservices/search";
	/**
	 * The conventional namespace prefix for vocabularies implemented by the Search API.
	 */
	final static public String SEARCH_PREFIX = "search";
}
