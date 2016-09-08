/*
 * Copyright 2013-2016 MarkLogic Corporation
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
package com.marklogic.client.util;

import java.util.Collection;

import javax.xml.namespace.NamespaceContext;

/**
 * InterableNamespaceContext extends NamespaceContext to support
 * introspection of unknown namespace bindings.
 */
public interface IterableNamespaceContext extends NamespaceContext {
    /**
     * Returns all bound prefixes.
     * @return	the set of prefixes
     */
	Collection<String> getAllPrefixes();
}
