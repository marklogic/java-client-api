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
package com.marklogic.client.admin;

/**
 * The Method Type enumerates a kind of operation.
 */
public enum MethodType {
    /**
     * A read operation.
     */
	GET,
    /**
     * A write operation.
     */
	PUT,
    /**
     * An apply operation; a catch-all for operations that
     * don't fit another method
     */
	POST,
    /**
     * A remove operation.
     */
	DELETE;
}
