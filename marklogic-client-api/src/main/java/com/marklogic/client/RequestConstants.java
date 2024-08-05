/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
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
