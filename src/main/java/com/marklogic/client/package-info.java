/**
 * The package provides the core classes, interfaces and exceptions for working with the database.
 * Use {@link com.marklogic.client.DatabaseClientFactory}.newClient() to create
 * a {@link com.marklogic.client.DatabaseClient} object.  Use the
 * Client object to create document managers for reading, writing, and deleting documents,
 * {@link com.marklogic.client.admin.QueryOptionsManager} to write configuration options 
 * for queries, and {@link com.marklogic.client.query.QueryManager} to execute queries.  
 * <p>The list of 
 * exceptions in this package enumerate the ways in which a REST server request can go wrong.
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
package com.marklogic.client;
