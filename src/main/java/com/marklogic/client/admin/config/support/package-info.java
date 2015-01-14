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
/**
 * Interfaces and classes used by {@link com.marklogic.client.admin.config.QueryOptionsBuilder},
 * {@link com.marklogic.client.admin.config.QueryOptions}, 
 * and {@link com.marklogic.client.io.QueryOptionsHandle}
 * to comprise builder expressions and mark function arguments.
 * 
 * <p>
 * It shouldn't be necessary to use any of the classes in this package
 * in client applications, except to support builder expressions.
 * <p>
 * <strong>Note:</strong> the QueryOptions and QueryOptionsBuilder support
 * component classes are deprecated.
 * Use a JSON or XML handle instead to write or read query options.
 */
package com.marklogic.client.admin.config.support;
