/*
 * Copyright (c) 2022 MarkLogic Corporation
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
package com.marklogic.client.expression;

import java.util.Map;

/**
 * Defines a transform for using with the {@code transformDoc} operator; the
 * assumption is that a factory method will be used to instantiate this which
 * requires the path of the transform module.
 */
public interface TransformDef {
    /**
     * Define the kind of transform; either "mjs" (the default) or "xslt".
     * 
     * @param kind
     * @return
     */
    TransformDef withKind(String kind);

    /**
     * Define a set of parameters to pass to the transform.
     * 
     * @param params
     * @return
     */
    TransformDef withParams(Map<String, Object> params);

    /**
     * Convenience method for adding a single parameter to the transform definition. If a map of params has already been
     * set via {@code withParams}, then this will add to that map.
     * 
     * @param name
     * @param value
     * @return
     */
    TransformDef withParam(String name, Object value);
}
